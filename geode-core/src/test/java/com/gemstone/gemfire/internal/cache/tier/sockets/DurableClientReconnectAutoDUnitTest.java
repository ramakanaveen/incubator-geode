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
package com.gemstone.gemfire.internal.cache.tier.sockets;

import org.junit.experimental.categories.Category;
import org.junit.Test;

import static org.junit.Assert.*;

import com.gemstone.gemfire.test.dunit.cache.internal.JUnit4CacheTestCase;
import com.gemstone.gemfire.test.dunit.internal.JUnit4DistributedTestCase;
import com.gemstone.gemfire.test.junit.categories.DistributedTest;

import com.gemstone.gemfire.cache.client.PoolFactory;
import com.gemstone.gemfire.cache.client.PoolManager;
import com.gemstone.gemfire.test.dunit.DistributedTestCase;
import com.gemstone.gemfire.test.dunit.DistributedTestUtils;
import com.gemstone.gemfire.test.dunit.Host;
import com.gemstone.gemfire.test.dunit.NetworkUtils;

/**
 * @since 5.7
 *
 * Test reconnecting a durable client that is using
 * the locator to discover its servers
 */
@Category(DistributedTest.class)
public class DurableClientReconnectAutoDUnitTest extends
    DurableClientReconnectDUnitTest {

  public static void caseSetUp() throws Exception {
    DistributedTestCase.disconnectAllFromDS();
  }
 
  public DurableClientReconnectAutoDUnitTest() {
    super();
  }
  
  @Test
  public void testDurableReconnectSingleServerWithZeroConnPerServer() {
    //do nothing, this test doesn't make sense with the locator
  }

  @Test
  public void testDurableReconnectSingleServer() throws Exception {
    //do nothing, this test doesn't make sense with the locator
  }
  
  protected PoolFactory getPoolFactory() {
    Host host = Host.getHost(0);
    PoolFactory factory = PoolManager.createFactory()
    .addLocator(NetworkUtils.getServerHostName(host), DistributedTestUtils.getDUnitLocatorPort());
    return factory;
  }

}
