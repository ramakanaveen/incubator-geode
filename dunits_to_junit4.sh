#!/bin/bash

# Licensed to the Apache Software Foundation (ASF) under one or more
# contributor license agreements.  See the NOTICE file distributed with
# this work for additional information regarding copyright ownership.
# The ASF licenses this file to You under the Apache License, Version 2.0
# (the "License"); you may not use this file except in compliance with
# the License.  You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
find . -name *DUnitTest.java -exec ./convert_one_dunit_to_junit4.pl {} \;

./convert_one_dunit_to_junit4.pl ./geode-core/src/test/java/com/gemstone/gemfire/management/ManagementTestBase.java
./convert_one_dunit_to_junit4.pl ./geode-core/src/test/java/com/gemstone/gemfire/internal/cache/execute/PRClientServerTestBase.java
./convert_one_dunit_to_junit4.pl ./geode-core/src/test/java/com/gemstone/gemfire/cache30/MultiVMRegionTestCase.java
./convert_one_dunit_to_junit4.pl ./geode-core/src/test/java/com/gemstone/gemfire/cache30/RegionTestCase.java
./convert_one_dunit_to_junit4.pl ./geode-core/src/test/java/com/gemstone/gemfire/cache/query/dunit/NonDistinctOrderByDUnitImpl.java
./convert_one_dunit_to_junit4.pl ./geode-core/src/test/java/com/gemstone/gemfire/internal/cache/EvictionTestBase.java
./convert_one_dunit_to_junit4.pl ./geode-core/src/test/java/com/gemstone/gemfire/cache30/DiskDistributedNoAckRegionTestCase.java
./convert_one_dunit_to_junit4.pl ./geode-core/src/test/java/com/gemstone/gemfire/internal/cache/PartitionedRegionDUnitTestCase.java
./convert_one_dunit_to_junit4.pl ./geode-core/src/test/java/com/gemstone/gemfire/cache30/RegionAttributesTestCase.java
./convert_one_dunit_to_junit4.pl ./geode-core/src/test/java/com/gemstone/gemfire/cache30/ClientServerTestCase.java
./convert_one_dunit_to_junit4.pl ./geode-core/src/test/java/com/gemstone/gemfire/cache30/ReliabilityTestCase.java
./convert_one_dunit_to_junit4.pl ./geode-core/src/test/java/com/gemstone/gemfire/internal/cache/wan/AsyncEventQueueTestBase.java
./convert_one_dunit_to_junit4.pl ./geode-core/src/test/java/com/gemstone/gemfire/internal/cache/DistributedCacheTestCase.java
./convert_one_dunit_to_junit4.pl ./geode-cq/src/test/java/com/gemstone/gemfire/internal/cache/tier/sockets/DurableClientTestCase.java
./convert_one_dunit_to_junit4.pl ./geode-core/src/test/java/com/gemstone/gemfire/internal/cache/partitioned/fixed/FixedPartitioningTestBase.java
./convert_one_dunit_to_junit4.pl ./geode-core/src/test/java/com/gemstone/gemfire/internal/cache/tier/sockets/HAInterestTestCase.java
./convert_one_dunit_to_junit4.pl ./geode-core/src/test/java/com/gemstone/gemfire/cache/client/internal/LocatorTestBase.java
./convert_one_dunit_to_junit4.pl ./geode-core/src/test/java/com/gemstone/gemfire/internal/cache/tier/sockets/RedundancyLevelTestBase.java
./convert_one_dunit_to_junit4.pl ./geode-assembly/src/test/java/com/gemstone/gemfire/rest/internal/web/controllers/RestAPITestBase.java
./convert_one_dunit_to_junit4.pl ./geode-wan/src/test/java/com/gemstone/gemfire/internal/cache/wan/WANTestBase.java
./convert_one_dunit_to_junit4.pl ./geode-core/src/test/java/com/gemstone/gemfire/cache30/CacheWriterTestCase.java
./convert_one_dunit_to_junit4.pl ./geode-core/src/test/java/com/gemstone/gemfire/cache30/CacheListenerTestCase.java
./convert_one_dunit_to_junit4.pl ./geode-core/src/test/java/com/gemstone/gemfire/cache30/CacheLoaderTestCase.java
./convert_one_dunit_to_junit4.pl ./geode-core/src/test/java/com/gemstone/gemfire/cache30/RegionReliabilityTestCase.java

./reset_existing_junit4_dunits.sh
