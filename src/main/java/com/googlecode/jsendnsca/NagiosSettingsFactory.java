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

import org.apache.commons.lang.StringUtils;

import com.googlecode.jsendnsca.encryption.Encryption;

public class NagiosSettingsFactory {

    public enum PropertyKey {
        HOST("nagios.nsca.host"),
        PORT("nagios.nsca.port"),
        PASSWORD("nagios.nsca.password"),
        TIMEOUT("nagios.nsca.timeout"),
        CONNECT_TIMEOUT("nagios.nsca.connect.timeout"),
        ENCRYPTION("nagios.nsca.encryption");

        private final String name;

        private PropertyKey(String name) {
            this.name = name;
        }

        private boolean providedIn(Properties properties) {
            if (properties.containsKey(name)) {
                return true;
            }
            return false;
        }
    }

    public static NagiosSettings createSettings(Properties properties) throws NagiosConfigurationException {
        NagiosSettings settings = new NagiosSettings();
        overrideUsing(properties, settings);
        return settings;
    }

    private static void overrideUsing(Properties properties, NagiosSettings settings) throws NagiosConfigurationException {
        for (PropertyKey key : PropertyKey.values()) {
            if (key.providedIn(properties)) {
                String name = key.name;
                String value = getValue(properties, name);
                switch (key) {
                case HOST:
                    settings.setNagiosHost(value);
                    break;
                case PORT:
                    toPort(settings, name, value);
                    break;
                case PASSWORD:
                    settings.setPassword(value);
                    break;
                case TIMEOUT:
                    settings.setTimeout(toInteger(name, value));
                    break;
                case CONNECT_TIMEOUT:
                    settings.setConnectTimeout(toInteger(name, value));
                    break;
                case ENCRYPTION:
                    settings.setEncryption(toEncryption(value));
                    break;
                }
            }
        }
    }

    private static String getValue(Properties properties, String name) throws NagiosConfigurationException {
        String value = properties.getProperty(name);
        if (StringUtils.isBlank(value)) {
            throw new NagiosConfigurationException("Key [%s] value cannot be empty or purely whitespace", name);
        }
        return value;
    }

    private static void toPort(NagiosSettings settings, String name, String value) throws NagiosConfigurationException {
        try {
            settings.setPort(toInteger(name, value));
        } catch (IllegalArgumentException e) {
            throw new NagiosConfigurationException("Key [%s] %s, was [%s]", name, e.getMessage(), value);
        }
    }

    private static Encryption toEncryption(String value) throws NagiosConfigurationException {
        try {
            return Encryption.valueOf(Encryption.class, value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new NagiosConfigurationException("Key [%s] must be one of [%s], was [foobar]", PropertyKey.ENCRYPTION.name, Encryption
                    .supportedList().toLowerCase(), value);
        }
    }

    private static int toInteger(String name, String value) throws NagiosConfigurationException {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new NagiosConfigurationException("Key [%s] must be an integer, was [%s]", name, value);
        }
    }

}
