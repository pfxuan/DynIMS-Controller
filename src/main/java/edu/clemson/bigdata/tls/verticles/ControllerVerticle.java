package edu.clemson.bigdata.tls.verticles;

import com.cyngn.kafka.consume.KafkaEvent;
import com.cyngn.kafka.consume.SimpleConsumer;

import edu.clemson.bigdata.tls.handlers.Evictor;
import edu.clemson.bigdata.tls.handlers.AlluxioEvictor;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.json.JsonObject;

import java.io.IOException;

/**
 * This verticle is used to run TLS-Controller.
 */
public class ControllerVerticle extends AbstractVerticle {
  //private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(MyFirstVerticle.class);

  /**
   * This method is called when the verticle is deployed. It creates a HTTP server and registers a simple request
   * handler.
   * <p/>
   * Notice the `listen` method. It passes a lambda checking the port binding result. When the HTTP server has been
   * bound on the port, it call the `complete` method to inform that the starting has completed. Else it reports the
   * error.
   *
   * @param future the future
   */
  @Override
  public void start(Future<Void> future) {
    // deploy the consumer verticle
    vertx.deployVerticle(SimpleConsumer.class.getName(), new DeploymentOptions().setConfig(config()),
      deploy -> {
        if(deploy.failed()) {
          System.err.println(String.format("Failed to start kafka consumer verticle, ex: %s", deploy.cause()));
          vertx.close();
          return;
        }
        System.out.println("kafka consumer verticle started");
        future.complete();
      }
    );

    vertx.eventBus().consumer(config().getString("eventbus.address"),
      message -> {
        // System.out.println(String.format("got message: %s", message.body()));
        // message handling code
        KafkaEvent event = new KafkaEvent((JsonObject) message.body());
        String evcitionMsg[] = event.value.split("\\t");
        String hostname = evcitionMsg[0];
        long inMemSize = Long.parseLong(evcitionMsg[1]);
        Evictor evictor = new AlluxioEvictor();

        try {
          evictor.releaseSpace(hostname, inMemSize);
        } catch (IOException e) {
          e.printStackTrace();
        }

        System.out.println(hostname + " ===> " + inMemSize);
      });
  }

}