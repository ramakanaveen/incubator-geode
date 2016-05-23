/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.gemstone.gemfire.cache.client.internal;

import org.junit.experimental.categories.Category;
import org.junit.Test;

import static org.junit.Assert.*;

import com.gemstone.gemfire.test.dunit.cache.internal.JUnit4CacheTestCase;
import com.gemstone.gemfire.test.dunit.internal.JUnit4DistributedTestCase;
import com.gemstone.gemfire.test.junit.categories.DistributedTest;

import com.gemstone.gemfire.cache.*;
import com.gemstone.gemfire.cache.client.Pool;
import com.gemstone.gemfire.cache.client.PoolManager;
import com.gemstone.gemfire.cache.server.CacheServer;
import com.gemstone.gemfire.cache.server.ServerLoadProbe;
import com.gemstone.gemfire.distributed.DistributedSystem;
import com.gemstone.gemfire.distributed.Locator;
import com.gemstone.gemfire.distributed.internal.DistributionConfig;
import com.gemstone.gemfire.internal.cache.PoolFactoryImpl;
import com.gemstone.gemfire.test.dunit.*;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.*;

/**
 *
 */
public abstract class LocatorTestBase extends JUnit4DistributedTestCase {
  protected static final String CACHE_KEY = "CACHE";
  protected static final String LOCATOR_KEY = "LOCATOR";
  protected static final String REGION_NAME = "A_REGION";
  protected static final String POOL_NAME = "daPool";
  protected static final Object CALLBACK_KEY = "callback";
  /**
   * A map for storing temporary objects in a remote VM so that they can be used
   * between calls. Cleared after each test.
   */
  protected static final HashMap remoteObjects = new HashMap();

  public LocatorTestBase() {
    super();
  }

  @Override
  public final void preTearDown() throws Exception {

    SerializableRunnable tearDown = new SerializableRunnable("tearDown") {
      public void run() {
        Locator locator = (Locator) remoteObjects.get(LOCATOR_KEY);
        if (locator != null) {
          try {
            locator.stop();
          } catch (Exception e) {
            //do nothing
          }
        }

        Cache cache = (Cache) remoteObjects.get(CACHE_KEY);
        if (cache != null) {
          try {
            cache.close();
          } catch (Exception e) {
            //do nothing
          }
        }
        remoteObjects.clear();

      }
    };
    //We seem to like leaving the DS open if we can for
    //speed, but lets at least destroy our cache and locator.
    Invoke.invokeInEveryVM(tearDown);
    tearDown.run();

    postTearDownLocatorTestBase();
  }

  protected void postTearDownLocatorTestBase() throws Exception {
  }

  protected void startLocator(final String hostName, final int locatorPort, final String otherLocators) {
    final String testName = getUniqueName();
    disconnectFromDS();
    Properties props = new Properties();
    props.setProperty(DistributionConfig.MCAST_PORT_NAME, String.valueOf(0));
    props.setProperty(DistributionConfig.LOCATORS_NAME, otherLocators);
    props.setProperty(DistributionConfig.LOG_LEVEL_NAME, LogWriterUtils.getDUnitLogLevel());
    props.setProperty(DistributionConfig.ENABLE_CLUSTER_CONFIGURATION_NAME, "false");
    try {
      File logFile = new File(testName + "-locator" + locatorPort + ".log");
      InetAddress bindAddr = null;
      try {
        bindAddr = InetAddress.getByName(hostName);
      } catch (UnknownHostException uhe) {
        Assert.fail("While resolving bind address ", uhe);
      }
      Locator locator = Locator.startLocatorAndDS(locatorPort, logFile, bindAddr, props);
      remoteObjects.put(LOCATOR_KEY, locator);
    } catch (IOException ex) {
      Assert.fail("While starting locator on port " + locatorPort, ex);
    }
  }

  protected void startLocatorInVM(final VM vm, final int locatorPort, final String otherLocators) {
    vm.invoke(new SerializableRunnable("Create Locator") {
      public void run() {
        String hostName = NetworkUtils.getServerHostName(vm.getHost());
        startLocator(hostName, locatorPort, otherLocators);
      }
    });
  }

  protected void stopLocatorInVM(VM vm) {
    vm.invoke(new SerializableRunnable("Stop Locator") {
      public void run() {
        stopLocator();
      }
    });
  }

  protected void stopLocator() {
    Locator locator = (Locator) remoteObjects.remove(LOCATOR_KEY);
    locator.stop();
  }

  protected int startBridgeServerInVM(VM vm, String[] groups, String locators) {
    return startBridgeServerInVM(vm, groups, locators, new String[] { REGION_NAME });
  }

  protected int addCacheServer(final String[] groups) throws IOException {
    Cache cache = (Cache) remoteObjects.get(CACHE_KEY);
    CacheServer server = cache.addCacheServer();
    server.setPort(0);
    server.setGroups(groups);
    server.start();
    return new Integer(server.getPort());
  }

  protected int addCacheServerInVM(VM vm, final String[] groups) {
    SerializableCallable connect =
        new SerializableCallable("Add Bridge server") {
          public Object call() throws Exception {
            return addCacheServer(groups);
          }
        };
    Integer port = (Integer) vm.invoke(connect);
    return port.intValue();
  }

  protected int startBridgeServerInVM(VM vm, final String[] groups, final String locators, final String[] regions) {
    return startBridgeServerInVM(vm, groups, locators, regions, CacheServer.DEFAULT_LOAD_PROBE);
  }

  protected int startBridgeServerInVM(VM vm, final String[] groups, final String locators, final String[] regions, final ServerLoadProbe probe) {
    SerializableCallable connect =
        new SerializableCallable("Start bridge server") {
          public Object call() throws IOException {
            return startBridgeServer(groups, locators, regions, probe);
          }
        };
    Integer port = (Integer) vm.invoke(connect);
    return port.intValue();
  }

  protected int startBridgeServer(String[] groups, String locators) throws IOException {
    return startBridgeServer(groups, locators, new String[] { REGION_NAME });
  }

  protected int startBridgeServer(final String[] groups, final String locators, final String[] regions) throws IOException {
    return startBridgeServer(groups, locators, regions, CacheServer.DEFAULT_LOAD_PROBE);
  }

  protected int startBridgeServer(final String[] groups, final String locators, final String[] regions, final ServerLoadProbe probe)
      throws IOException {
    Properties props = new Properties();
    props.setProperty(DistributionConfig.MCAST_PORT_NAME, "0");
    props.setProperty(DistributionConfig.LOCATORS_NAME, locators);
    DistributedSystem ds = getSystem(props);
    Cache cache = CacheFactory.create(ds);
    AttributesFactory factory = new AttributesFactory();
    factory.setScope(Scope.DISTRIBUTED_ACK);
    factory.setEnableBridgeConflation(true);
    factory.setDataPolicy(DataPolicy.REPLICATE);
    RegionAttributes attrs = factory.create();
    for (int i = 0; i < regions.length; i++) {
      cache.createRegion(regions[i], attrs);
    }
    CacheServer server = cache.addCacheServer();
    server.setPort(0);
    server.setGroups(groups);
    server.setLoadProbe(probe);
    server.start();

    remoteObjects.put(CACHE_KEY, cache);

    return new Integer(server.getPort());
  }

  protected int startBridgeServerWithEmbeddedLocator(final String[] groups, final String locators, final String[] regions, final ServerLoadProbe probe)
      throws IOException {
    Properties props = new Properties();
    props.setProperty(DistributionConfig.MCAST_PORT_NAME, String.valueOf(0));
    props.setProperty(DistributionConfig.START_LOCATOR_NAME, locators);
    props.setProperty(DistributionConfig.LOCATORS_NAME, locators);
    DistributedSystem ds = getSystem(props);
    Cache cache = CacheFactory.create(ds);
    AttributesFactory factory = new AttributesFactory();
    factory.setScope(Scope.DISTRIBUTED_ACK);
    factory.setEnableBridgeConflation(true);
    factory.setDataPolicy(DataPolicy.REPLICATE);
    RegionAttributes attrs = factory.create();
    for (int i = 0; i < regions.length; i++) {
      cache.createRegion(regions[i], attrs);
    }
    CacheServer server = cache.addCacheServer();
    server.setGroups(groups);
    server.setLoadProbe(probe);
    server.setPort(0);
    server.start();

    remoteObjects.put(CACHE_KEY, cache);

    return new Integer(server.getPort());
  }

  protected int startBridgeServerWithEmbeddedLocatorInVM(VM vm, final String[] groups, final String locators, final String[] regions,
      final ServerLoadProbe probe) {
    SerializableCallable connect =
        new SerializableCallable("Start bridge server") {
          public Object call() throws IOException {
            return startBridgeServerWithEmbeddedLocator(groups, locators, regions, probe);
          }
        };
    Integer port = (Integer) vm.invoke(connect);
    return port.intValue();
  }

  protected void startBridgeClientInVM(VM vm, final String group, final String host, final int port) throws Exception {
    startBridgeClientInVM(vm, group, host, port, new String[] { REGION_NAME });
  }

  protected void startBridgeClientInVM(VM vm, final String group, final String host, final int port, final String[] regions) throws Exception {
    PoolFactoryImpl pf = new PoolFactoryImpl(null);
    pf.addLocator(host, port)
        .setServerGroup(group)
        .setPingInterval(200)
        .setSubscriptionEnabled(true)
        .setSubscriptionRedundancy(-1);
    startBridgeClientInVM(vm, pf.getPoolAttributes(), regions);
  }

  protected void startBridgeClientInVM(VM vm, final Pool pool, final String[] regions) throws Exception {
    SerializableRunnable connect = new SerializableRunnable("Start bridge client") {
      public void run() throws Exception {
        startBridgeClient(pool, regions);
      }
    };
    if (vm == null) {
      connect.run();
    } else {
      vm.invoke(connect);
    }
  }

  protected void startBridgeClient(final String group, final String host, final int port) throws Exception {
    startBridgeClient(group, host, port, new String[] { REGION_NAME });
  }

  protected void startBridgeClient(final String group, final String host, final int port, final String[] regions) throws Exception {
    PoolFactoryImpl pf = new PoolFactoryImpl(null);
    pf.addLocator(host, port)
        .setServerGroup(group)
        .setPingInterval(200)
        .setSubscriptionEnabled(true)
        .setSubscriptionRedundancy(-1);
    startBridgeClient(pf.getPoolAttributes(), regions);
  }

  protected void startBridgeClient(final Pool pool, final String[] regions) throws Exception {
    Properties props = new Properties();
    props.setProperty(DistributionConfig.MCAST_PORT_NAME, String.valueOf(0));
    props.setProperty(DistributionConfig.LOCATORS_NAME, "");
    DistributedSystem ds = getSystem(props);
    Cache cache = CacheFactory.create(ds);
    AttributesFactory factory = new AttributesFactory();
    factory.setScope(Scope.LOCAL);
    factory.setPoolName(POOL_NAME);
    PoolFactoryImpl pf = (PoolFactoryImpl) PoolManager.createFactory();
    pf.init(pool);
    LocatorDiscoveryCallback locatorCallback = new MyLocatorCallback();
    remoteObjects.put(CALLBACK_KEY, locatorCallback);
    pf.setLocatorDiscoveryCallback(locatorCallback);
    pf.create(POOL_NAME);

    RegionAttributes attrs = factory.create();
    for (int i = 0; i < regions.length; i++) {
      cache.createRegion(regions[i], attrs);
    }

    remoteObjects.put(CACHE_KEY, cache);
  }

  protected void stopBridgeMemberVM(VM vm) {
    vm.invoke(new SerializableRunnable("Stop bridge member") {
      public void run() {
        Cache cache = (Cache) remoteObjects.remove(CACHE_KEY);
        cache.close();
        disconnectFromDS();
      }
    });
  }

  public String getLocatorString(Host host, int locatorPort) {
    return getLocatorString(host, new int[] { locatorPort });
  }

  public String getLocatorString(Host host, int[] locatorPorts) {
    StringBuffer str = new StringBuffer();
    for (int i = 0; i < locatorPorts.length; i++) {
      str.append(NetworkUtils.getServerHostName(host))
          .append("[")
          .append(locatorPorts[i])
          .append("]");
      if (i < locatorPorts.length - 1) {
        str.append(",");
      }
    }

    return str.toString();
  }

  protected static class MyLocatorCallback extends LocatorDiscoveryCallbackAdapter {

    private final Set discoveredLocators = new HashSet();
    private final Set removedLocators = new HashSet();

    public synchronized void locatorsDiscovered(List locators) {
      discoveredLocators.addAll(locators);
      notifyAll();
    }

    public synchronized void locatorsRemoved(List locators) {
      removedLocators.addAll(locators);
      notifyAll();
    }

    public boolean waitForDiscovery(InetSocketAddress locator, long time) throws InterruptedException {
      return waitFor(discoveredLocators, locator, time);
    }

    public boolean waitForRemove(InetSocketAddress locator, long time) throws InterruptedException {
      return waitFor(removedLocators, locator, time);
    }

    private synchronized boolean waitFor(Set set, InetSocketAddress locator, long time) throws InterruptedException {
      long remaining = time;
      long endTime = System.currentTimeMillis() + time;
      while (!set.contains(locator) && remaining >= 0) {
        wait(remaining);
        remaining = endTime - System.currentTimeMillis();
      }
      return set.contains(locator);
    }

    public synchronized Set getDiscovered() {
      return new HashSet(discoveredLocators);
    }

  }

}
