package edu.clemson.bigdata.tls.pbs;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static java.util.stream.Collectors.toList;

/**
 * Created by pfxuan on 12/3/15.
 */
@RunWith(VertxUnitRunner.class)
public class PbsNodesTest {
  private Vertx vertx;
  private URI jsonFile;
  private String jsonString;

  @Before
  public void setUp(TestContext context) throws Exception {
    vertx = Vertx.vertx();
    jsonFile = this.getClass().getResource("/pbsnodes.json").toURI();
    jsonString = new String(Files.readAllBytes(Paths.get(jsonFile)));
  }

  @After
  public void tearDown(TestContext context) {
    vertx.close();
  }

  @Test
  public void pbsnodesJSONTest() throws IOException {
    JsonObject object = new JsonObject(jsonString);
//    String mem = object.getJsonObject("nodes").getJsonObject("node1442")
//        .getJsonObject("resources_available").getString("mem");
    List<String> mems = object.getJsonObject("nodes").stream()
        .map(node -> ((JsonObject) node.getValue()).getJsonObject("resources_available").getString("host"))
        .collect(toList());
    System.out.println(mems);
  }

}
