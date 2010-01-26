/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.googlecode.jsendnsca;

import java.util.Properties;

import com.googlecode.jsendnsca.encryption.Encryption;

public class NagiosSettingsFactory {
    
    public enum PropertyKeyNames {
        HOST("nagios.nsca.host"),
        PORT("nagios.nsca.port"),
        PASSWORD("nagios.nsca.password"),
        TIMEOUT("nagios.nsca.timeout"),
        CONNECT_TIMEOUT("nagios.nsca.connect.timeout"),
        ENCRYPTION("nagios.nsca.encryption");
        
        private final String key;

        private PropertyKeyNames(String key) {
            this.key = key;
        }
        
        private boolean providedIn(Properties properties) {
            if(properties.containsKey(key)) {
                return true;
            }
            return false;
        }
    }

    public static NagiosSettings createSettings(Properties properties) {
        NagiosSettings settings = new NagiosSettings();
        overrideUsing(properties, settings);
        return settings;
    }

    private static void overrideUsing(Properties properties, NagiosSettings settings) {
        for (PropertyKeyNames keyName : PropertyKeyNames.values()) {
            if(keyName.providedIn(properties)) {
                String value = properties.getProperty(keyName.key);
                switch (keyName) {
                case HOST:
                    settings.setNagiosHost(value);
                    break;
                case PORT:
                    settings.setPort(toInteger(value));
                    break;
                case PASSWORD:
                    settings.setPassword(value);
                    break;
                case TIMEOUT:
                    settings.setTimeout(toInteger(value));
                    break;
                case CONNECT_TIMEOUT:
                    settings.setConnectTimeout(toInteger(value));
                    break;
                case ENCRYPTION:
                    settings.setEncryption(getEncryption(value));
                    break;
                }
            }
            
        }
    }

    private static Encryption getEncryption(String value) {
        return Encryption.valueOf(Encryption.class, value.toUpperCase());
    }

    private static int toInteger(String value) {
        return Integer.parseInt(value);
    }

}
