//package edu.clemson.bigdata.tls.handlers;
//
//import java.io.IOException;
//import java.net.InetSocketAddress;
//
//import org.apache.hadoop.conf.Configuration;
//import org.apache.hadoop.fs.CommonConfigurationKeys;
//import org.apache.hadoop.hdfs.DFSConfigKeys;
//import org.apache.hadoop.hdfs.DFSUtilClient;
//import org.apache.hadoop.hdfs.protocol.ClientDatanodeProtocol;
//import org.apache.hadoop.net.NetUtils;
//import org.apache.hadoop.security.UserGroupInformation;
//
///**
// * Created by pxuan on 1/31/16.
// */
//public class HdfsEvictor implements Evictor {
//  @Override
//  public void releaseSpace(String hostname, long freeBytes) throws IOException {
//    ClientDatanodeProtocol dnProxy = getDataNodeProxy(hostname);
//    dnProxy.startMemEviction(freeBytes);
//    System.out.println("In-memory eviction has been issued on DataNode" + hostname + " (total reserved size: " + freeBytes + " bytes)");
//  }
//
//  private ClientDatanodeProtocol getDataNodeProxy(String datanode)
//      throws IOException {
//    InetSocketAddress datanodeAddr = NetUtils.createSocketAddr(datanode);
//
//    // Get the current configuration
//    Configuration conf = new Configuration();
//
//    // For datanode proxy the server principal should be DN's one.
//    conf.set(CommonConfigurationKeys.HADOOP_SECURITY_SERVICE_USER_NAME_KEY,
//        conf.get(DFSConfigKeys.DFS_DATANODE_KERBEROS_PRINCIPAL_KEY, ""));
//
//    // Create the client
//    ClientDatanodeProtocol dnProtocol =
//        DFSUtilClient.createClientDatanodeProtocolProxy(datanodeAddr, getUGI(), conf,
//            NetUtils.getSocketFactory(conf, ClientDatanodeProtocol.class));
//    return dnProtocol;
//  }
//
//  private static UserGroupInformation getUGI()
//      throws IOException {
//    return UserGroupInformation.getCurrentUser();
//  }
//}
