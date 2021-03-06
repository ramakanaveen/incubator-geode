/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.gemstone.gemfire.cache.lucene.internal;

import static com.gemstone.gemfire.distributed.internal.DistributionStats.getStatTime;

import com.gemstone.gemfire.StatisticDescriptor;
import com.gemstone.gemfire.Statistics;
import com.gemstone.gemfire.StatisticsFactory;
import com.gemstone.gemfire.StatisticsType;
import com.gemstone.gemfire.StatisticsTypeFactory;
import com.gemstone.gemfire.internal.StatisticsTypeFactoryImpl;

public class LuceneIndexStats {
  // statistics type
  private static final StatisticsType statsType;
  private static final String statsTypeName = "LuceneIndexStats";
  private static final String statsTypeDescription = "Statistics about lucene indexes";

  private static final int queryExecutionsId;
  private static final int queryExecutionTimeId;
  private static final int queryExecutionsInProgressId;
  private static final int queryExecutionTotalHits;
  private static final int updatesId;
  private static final int updateTimeId;
  private static final int updatesInProgressId;
  private static final int commitsId;
  private static final int commitTimeId;
  private static final int commitsInProgressId;
  private static final int documentsId;

  private final Statistics stats;

  static {
    final StatisticsTypeFactory f = StatisticsTypeFactoryImpl.singleton();
    statsType = f.createType(
      statsTypeName,
      statsTypeDescription,
      new StatisticDescriptor[] {
        f.createIntCounter("queryExecutions", "Number of lucene queries executed on this member", "operations"),
        f.createLongCounter("queryExecutionTime", "Amount of time spent executing lucene queries", "nanoseconds"),
        f.createIntGauge("queryExecutionsInProgress", "Number of query executions currently in progress", "operations"),
        f.createLongCounter("queryExecutionTotalHits", "Total number of documents returned by query executions", "entries"),
        f.createIntCounter("updates", "Number of lucene index documents added/removed on this member", "operations"),
        f.createLongCounter("updateTime", "Amount of time spent adding or removing documents from the index", "nanoseconds"),
        f.createIntGauge("updatesInProgress", "Number of index updates in progress", "operations"),
        f.createIntCounter("commits", "Number of lucene index commits on this member", "operations"),
        f.createLongCounter("commitTime", "Amount of time spent in lucene index commits", "nanoseconds"),
        f.createIntGauge("commitsInProgress", "Number of lucene index commits in progress", "operations"),
        f.createIntGauge("documents", "Number of documents in the index", "documents"),
      }
    );

    queryExecutionsId = statsType.nameToId("queryExecutions");
    queryExecutionTimeId = statsType.nameToId("queryExecutionTime");
    queryExecutionsInProgressId = statsType.nameToId("queryExecutionsInProgress");
    queryExecutionTotalHits = statsType.nameToId("queryExecutionTotalHits");
    updatesId = statsType.nameToId("updates");
    updateTimeId = statsType.nameToId("updateTime");
    updatesInProgressId = statsType.nameToId("updatesInProgress");
    commitsId = statsType.nameToId("commits");
    commitTimeId = statsType.nameToId("commitTime");
    commitsInProgressId = statsType.nameToId("commitsInProgress");
    documentsId = statsType.nameToId("documents");
  }

  public LuceneIndexStats(StatisticsFactory f, String name) {
    this.stats = f.createAtomicStatistics(statsType, name);
  }

  /**
   * @return the timestamp that marks the start of the operation
   */
  public long startQuery() {
    stats.incInt(queryExecutionsInProgressId, 1);
    return getStatTime();
  }
  /**
   * @param start the timestamp taken when the operation started
   */
  public void endQuery(long start, final int totalHits) {
    stats.incLong(queryExecutionTimeId, getStatTime()-start);
    stats.incInt(queryExecutionsInProgressId, -1);
    stats.incInt(queryExecutionsId, 1);
    stats.incLong(queryExecutionTotalHits, totalHits);
  }

  /**
   * @return the timestamp that marks the start of the operation
   */
  public long startUpdate() {
    stats.incInt(updatesInProgressId, 1);
    return getStatTime();
  }
  /**
   * @param start the timestamp taken when the operation started
   */
  public void endUpdate(long start) {
    stats.incLong(updateTimeId, getStatTime()-start);
    stats.incInt(updatesInProgressId, -1);
    stats.incInt(updatesId, 1);
  }

  /**
   * @return the timestamp that marks the start of the operation
   */
  public long startCommit() {
    stats.incInt(commitsInProgressId, 1);
    return getStatTime();
  }
  /**
   * @param start the timestamp taken when the operation started
   */
  public void endCommit(long start) {
    stats.incLong(commitTimeId, getStatTime()-start);
    stats.incInt(commitsInProgressId, -1);
    stats.incInt(commitsId, 1);
  }

  public void incDocuments(int delta) {
    stats.incInt(documentsId, delta);
  }
}
