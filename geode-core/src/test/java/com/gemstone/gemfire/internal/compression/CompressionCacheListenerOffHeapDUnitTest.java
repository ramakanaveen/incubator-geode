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
package com.gemstone.gemfire.internal.compression;

import org.junit.experimental.categories.Category;
import org.junit.Test;

import static org.junit.Assert.*;

import com.gemstone.gemfire.test.dunit.cache.internal.JUnit4CacheTestCase;
import com.gemstone.gemfire.test.dunit.internal.JUnit4DistributedTestCase;
import com.gemstone.gemfire.test.junit.categories.DistributedTest;

import java.util.Properties;

import com.gemstone.gemfire.compression.SnappyCompressor;
import com.gemstone.gemfire.distributed.internal.DistributionConfig;
import com.gemstone.gemfire.internal.cache.OffHeapTestUtil;
import com.gemstone.gemfire.test.dunit.Invoke;
import com.gemstone.gemfire.test.dunit.SerializableRunnable;

@SuppressWarnings("serial")
@Category(DistributedTest.class)
public class CompressionCacheListenerOffHeapDUnitTest extends
    CompressionCacheListenerDUnitTest {

  public CompressionCacheListenerOffHeapDUnitTest() {
    super();
  }
  
  public static void caseSetUp() {
    System.setProperty("gemfire.trackOffHeapRefCounts", "true");
  }
  public static void caseTearDown() {
    System.clearProperty("gemfire.trackOffHeapRefCounts");
  }

  @Override
  public final void preTearDownAssertions() throws Exception {
    SerializableRunnable checkOrphans = new SerializableRunnable() {

      @Override
      public void run() {
        if(hasCache()) {
          OffHeapTestUtil.checkOrphans();
        }
      }
    };
    Invoke.invokeInEveryVM(checkOrphans);
    checkOrphans.run();
  }

  @Override
  public Properties getDistributedSystemProperties() {
    Properties props = super.getDistributedSystemProperties();
    props.setProperty(DistributionConfig.OFF_HEAP_MEMORY_SIZE_NAME, "1m");
    return props;
  }

  @Override
  protected void createRegion() {
    try {
      SnappyCompressor.getDefaultInstance();
    } catch (Throwable t) {
      // Not a supported OS
      return;
    }
    createCompressedRegionOnVm(getVM(TEST_VM), REGION_NAME, SnappyCompressor.getDefaultInstance(), true);
  }
  

}
