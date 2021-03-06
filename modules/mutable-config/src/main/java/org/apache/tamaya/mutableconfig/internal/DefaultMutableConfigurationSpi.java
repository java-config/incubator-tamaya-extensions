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
package org.apache.tamaya.mutableconfig.internal;

import org.apache.tamaya.Configuration;
import org.apache.tamaya.mutableconfig.ChangePropagationPolicy;
import org.apache.tamaya.mutableconfig.MutableConfiguration;
import org.apache.tamaya.mutableconfig.spi.MutableConfigurationProviderSpi;
import org.osgi.service.component.annotations.Component;


/**
 * SPI implementation that creates instances of {@link DefaultMutableConfiguration}, hereby for
 * each instance of {@link Configuration} a new instance has to be returned.
 */
@Component
public class DefaultMutableConfigurationSpi implements MutableConfigurationProviderSpi {

    @Override
    public MutableConfiguration createMutableConfiguration(Configuration configuration,
                                                    ChangePropagationPolicy propagationPolicy){
        return new DefaultMutableConfiguration(configuration, propagationPolicy);
    }
}
