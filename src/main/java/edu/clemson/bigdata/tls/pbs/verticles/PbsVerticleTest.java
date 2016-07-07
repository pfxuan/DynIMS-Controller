package edu.clemson.bigdata.tls.pbs.verticles;

import edu.clemson.bigdata.tls.pbs.PBS;
import io.vertx.core.AbstractVerticle;

/**
 * Created by pxuan on 12/2/15.
 */
public class PbsVerticleTest extends AbstractVerticle {

  @Override
  public void start() throws Exception {
    PBS.getNodesSnapshot();
  }
}
