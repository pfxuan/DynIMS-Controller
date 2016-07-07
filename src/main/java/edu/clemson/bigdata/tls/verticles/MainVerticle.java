package edu.clemson.bigdata.tls.verticles;

import com.cyngn.kafka.consume.SimpleConsumer;
import java.util.ArrayList;
import java.util.List;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Context;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

import edu.clemson.bigdata.tls.utils.MultipleFutures;

/**
 * Main verticle, orchestrate the instanciation of other verticles
 */
public class MainVerticle extends AbstractVerticle {

	private List<String> deploymentIds;

	@Override
	public void init(Vertx vertx, Context context) {
		super.init(vertx, context);
		deploymentIds = new ArrayList<String>(3);
	}

	@Override
	public void start(Future<Void> future) {
		deployKafkaConsumer(future);
	}

//	private void deployEmbeddedDbs(Future<Void> future, Handler<Future<Void>> whatsNext) {
//		MultipleFutures dbDeployments = new MultipleFutures();
//		dbDeployments.add(this::deployEmbeddedRedis);
//		dbDeployments.setHandler(result -> {
//			if (result.failed()) {
//				future.fail(result.cause());
//			} else {
//				whatsNext.handle(future);
//			}
//		});
//		dbDeployments.start();
//	}

	private void deployKafkaConsumer(Future<Void> future) {
		DeploymentOptions consumerOptions = new DeploymentOptions().setConfig(config());
		vertx.deployVerticle(SimpleConsumer.class.getName(), consumerOptions, consumerResult -> {
			if (consumerResult.failed()) {
				System.err.println(String.format("Failed to start kafka consumer verticle, ex: %s", consumerResult.cause()));
				future.fail(consumerResult.cause());
			} else {
				deploymentIds.add(consumerResult.result());
				System.out.println("kafka consumer verticle started");
				future.complete();
			}
		});
	}

	@Override
	public void stop(Future<Void> future) {
		MultipleFutures futures = new MultipleFutures(future);
		deploymentIds.forEach(deploymentId -> {
			futures.add(fut -> {
				undeploy(deploymentId, fut);
			});
		});
		futures.start();
	}

	private void undeploy(String deploymentId, Future<Void> future) {
		vertx.undeploy(deploymentId, res -> {
			if (res.succeeded()) {
				future.complete();
			} else {
				future.fail(res.cause());
			}
		});
	}
}
