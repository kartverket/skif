/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package no.statkart.skif.config;

import java.util.Iterator;

/**
 * A configuration based on the system configuration.
 *
 * @author Emmanuel Bourg
 * @version $Revision: 727834 $, $Date: 2008-12-18 23:16:32 +0100 (Do, 18 Dez 2008) $
 * @since 1.1
 */
public class SystemConfiguration extends MapConfiguration
{
    /**
     * Create a Configuration based on the system configuration.
     *
     * @see System#getProperties
     */
    public SystemConfiguration()
    {
        super(System.getProperties());
    }

    /**
     * The method allows system configuration to be set from a property file.
     * @param fileName The name of the property file.
     * @throws Exception if an error occurs.
     */
    public static void setSystemProperties(String fileName) throws Exception
    {
        PropertiesConfiguration config =new PropertiesConfiguration(fileName);
        setSystemProperties(config);
    }

    /**
     * Set System configuration from a configuration file.
     * @param systemConfig The configuration containing the configuration to be set.
     */
    public static void setSystemProperties(PropertiesConfiguration systemConfig)
    {
        Iterator iter = systemConfig.getKeys();
        while (iter.hasNext())
        {
            String key = (String) iter.next();
            String value = (String) systemConfig.getProperty(key);
            System.setProperty(key, value);
        }
    }
}
