/*
 * Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.apache.tamaya.consul;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.UUID;

import org.apache.tamaya.mutableconfig.ConfigChangeRequest;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for the consul backend integration for writing to the consul backend.
 */
public class ConsulWriteTest {

    /**
     * Needs to be enabled manually in case you want to do integration tests.
     */
    static boolean execute = false;
    private static ConsulPropertySource propertySource;

    @BeforeClass
    public static void setup() throws MalformedURLException, URISyntaxException {
        System.setProperty("consul.urls", "http://127.0.0.1:8300");
        propertySource = new ConsulPropertySource();

        System.out.println("At the moment no write-tests can be executed to verify the Consul integration. You can manually edit this test class.");
    }

    @Test
    public void testSetNormal() throws Exception {
        if (!execute) {
            return;
        }
        String taID = UUID.randomUUID().toString();
        ConfigChangeRequest request = new ConfigChangeRequest("testSetNormal");
        request.put(taID, "testSetNormal");
        propertySource.applyChange(request);
    }


    @Test
    public void testDelete() throws Exception {
        if (!execute) {
            return;
        }
        String taID = UUID.randomUUID().toString();
        ConfigChangeRequest request = new ConfigChangeRequest("testDelete");
        request.put(taID, "testDelete");
        propertySource.applyChange(request);
        assertThat(taID).isEqualTo(propertySource.get("testDelete").getValue());
        assertThat(propertySource.get("_testDelete.createdIndex")).isNotNull();
        request = new ConfigChangeRequest("testDelete2");
        request.remove("testDelete");
        propertySource.applyChange(request);
        assertThat(propertySource.get("testDelete")).isNull();
    }

    @Test
    public void testGetProperties() throws Exception {
        if (!execute) {
            return;
        }
        assertThat(propertySource.getProperties()).isEmpty();
    }
}
