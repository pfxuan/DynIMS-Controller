package edu.clemson.bigdata.tls.handlers;

import java.io.IOException;

/**
 * Created by pxuan on 1/31/16.
 */
public interface Evictor {

  /**
   * Release in-memory space on the given size
   * @param hostname
   * @param freeBytes
   */
  void releaseSpace(String hostname, long freeBytes) throws IOException;
}
