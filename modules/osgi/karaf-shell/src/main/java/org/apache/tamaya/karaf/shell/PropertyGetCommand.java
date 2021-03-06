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
package org.apache.tamaya.karaf.shell;

import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Argument;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.Option;
import org.apache.karaf.shell.api.action.lifecycle.Service;
import org.apache.tamaya.osgi.commands.ConfigCommands;

import java.io.IOException;

/**
 * A Karaf shell command.
 */
@Command(scope = "tamaya", name = "tm_property", description="Get a Tamaya property.")
@Service
public class PropertyGetCommand implements Action{

    @Argument(index = 0, name = "key", description = "The target property source id.",
            required = false, multiValued = false)
    String key = null;

    @Option(name="extended", aliases = "e", description = "Also print extended property createValue attributes.")
    boolean extended;

    @Option(name = "propertysource", aliases = "ps", description = "The target property source id.",
            required = false, multiValued = false)
    String propertysource = null;

    public Object execute() throws IOException {
        return(ConfigCommands.getProperty(propertysource, key, extended));
    }

}
