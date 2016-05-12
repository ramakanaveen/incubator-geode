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
package com.gemstone.gemfire.management.internal.cli;

import static org.assertj.core.api.Assertions.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.gemstone.gemfire.management.internal.cli.parser.Argument;
import com.gemstone.gemfire.management.internal.cli.parser.Option;
import com.gemstone.gemfire.management.internal.cli.parser.OptionSet;
import com.gemstone.gemfire.management.internal.cli.parser.jopt.JoptOptionParser;
import com.gemstone.gemfire.test.junit.categories.UnitTest;

@Category(UnitTest.class)
public class JoptOptionParserTest {

  private JoptOptionParser defaultJoptOptionParser;
  private OptionSet defaultOptionSet;

  @Before
  public void setUp() throws Exception {
    this.defaultJoptOptionParser = new JoptOptionParser();
    this.defaultOptionSet = new OptionSet();
  }

  @Test
  public void getArgumentsIsEmptyByDefault() throws Exception {
    assertThat(this.defaultJoptOptionParser.getArguments()).isEmpty();
  }

  @Test
  public void getOptionsIsNullByDefault() throws Exception {
    assertThat(this.defaultJoptOptionParser.getOptions()).isNull();
  }

  @Test
  public void parseNullReturnsDefaultOptionSet() throws Exception {
    OptionSet optionSet = this.defaultJoptOptionParser.parse(null);
    assertThat(optionSet.areArgumentsPresent()).isEqualTo(defaultOptionSet.areArgumentsPresent());
    assertThat(optionSet.areOptionsPresent()).isEqualTo(defaultOptionSet.areOptionsPresent());
    assertThat(optionSet.getNoOfSpacesRemoved()).isEqualTo(defaultOptionSet.getNoOfSpacesRemoved());
    assertThat(optionSet.getSplit()).isEqualTo(defaultOptionSet.getSplit());
    assertThat(optionSet.getNoOfSpacesRemoved()).isEqualTo(defaultOptionSet.getNoOfSpacesRemoved());
    assertThat(optionSet.getUserInput()).isEqualTo(""); //defaultOptionSet.getUserInput());
    assertThat(optionSet.getValue((Argument)null)).isEqualTo(defaultOptionSet.getValue((Argument)null));
    assertThat(optionSet.getValue((Option)null)).isEqualTo(defaultOptionSet.getValue((Option)null));
  }

  @Test
  public void parseEmptyThrowsNullPointerException() throws Exception {
    assertThatThrownBy(() -> this.defaultJoptOptionParser.parse("")).isInstanceOf(NullPointerException.class);
  }

  @Test
  public void foo() throws Exception {
    //assertThatThrownBy(() -> this.defaultJoptOptionParser.parse(null)
}


}
