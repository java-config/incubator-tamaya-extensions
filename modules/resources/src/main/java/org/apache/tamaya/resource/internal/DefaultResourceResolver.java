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
package org.apache.tamaya.resource.internal;

import org.apache.tamaya.resource.ResourceLocator;
import org.apache.tamaya.resource.ResourceResolver;
import org.apache.tamaya.spi.ClassloaderAware;
import org.apache.tamaya.spi.ServiceContextManager;
import org.osgi.service.component.annotations.Component;

import javax.annotation.Priority;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

/**
 * Simple default implementation of the resource loader, which does only support direct references to files.
 */
@Priority(0)
@Component
public class DefaultResourceResolver implements ResourceResolver, ClassloaderAware {

    private static final Logger LOG = Logger.getLogger(DefaultResourceResolver.class.getName());
    private ClassLoader classLoader = ServiceContextManager.getDefaultClassLoader();

    @Override
    public List<URL> getResources(Collection<String> expressions) {
        List<URL> resources = new ArrayList<>();
        for (String expression : expressions) {
            for(ResourceLocator locator: getResourceLocators()){
                Collection<URL> found = locator.lookup(classLoader, expression);
                if(!found.isEmpty()) {
                    resources.addAll(found);
                    break;
                }
            }
        }
        return resources;
    }

    @Override
    public Collection<ResourceLocator> getResourceLocators() {
        return ServiceContextManager.getServiceContext(classLoader).getServices(ResourceLocator.class);
    }

    @Override
    public void init(ClassLoader classLoader) {
        this.classLoader = Objects.requireNonNull(classLoader);
    }

    @Override
    public ClassLoader getClassLoader() {
        return classLoader;
    }
}
