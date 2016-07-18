package edu.clemson.bigdata.tls.handlers;

import java.io.IOException;

import alluxio.client.block.AlluxioBlockStore;

/**
 * Created by pxuan on 1/31/16.
 */
public class AlluxioEvictor implements Evictor {
  @Override
  public void releaseSpace(String hostname, long freeBytes) throws IOException {
    AlluxioBlockStore.get().evictMem(hostname, freeBytes);
    System.out.println("In-memory eviction has been issued on DataNode" + hostname + " (size: "
        + freeBytes + " bytes)");
  }
}
