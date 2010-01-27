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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;

import com.googlecode.jsendnsca.encryption.Encryption;
import com.googlecode.jsendnsca.utils.IOUtils;

/**
 * <p>
 * Factory for creating {@link NagiosSettings} from a {@link Properties} object
 * via file, stream or an instance. The names of the keys are documented in the
 * {@link PropertyKey} enum. An example is shown below. You only need to provide
 * keys for settings which you wish to override the default value of. See
 * {@link NagiosSettings} for default values.
 * </p>
 * 
 * <pre>
 * {@code
 * nagios.nsca.host=foobar
 * nagios.nsca.port=7665
 * nagios.nsca.password=password
 * nagios.nsca.timeout=20000
 * nagios.nsca.connect.timeout=10000
 * nagios.nsca.encryption=xor
 * }
 * </pre>
 * 
 * @author Raj Patel
 * @since 2.0.0
 */
public class NagiosSettingsFactory {

    /**
     * Property Key constants to be used for nagios settings as keys in a
     * {@link Properties} instance
     * 
     * @author Raj Patel
     */
    public enum PropertyKey {
        /**
         * key nagios.nsca.host
         */
        HOST("nagios.nsca.host"),
        /**
         * key nagios.nsca.port, must be integer value in range 1 to 65535
         */
        PORT("nagios.nsca.port"),
        /**
         * key nagios.nsca.password
         */
        PASSWORD("nagios.nsca.password"),
        /**
         * key nagios.nsca.timeout, must be integer value
         */
        TIMEOUT("nagios.nsca.timeout"),
        /**
         * key nagios.nsca.connect.timeout, must be integer value
         */
        CONNECT_TIMEOUT("nagios.nsca.connect.timeout"),
        /**
         * key nagios.nsca.encryption, must be either none, xor or triple_des
         */
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

    /**
     * Create {@link NagiosSettings} from a properties file
     * 
     * @param file
     *            containing properties
     * @return the {@link NagiosSettings}
     * @throws IOException
     *             thrown on IO issue accessing file
     * @throws NagiosConfigurationException
     *             thrown on invalid configuration values
     */
    public static NagiosSettings createSettings(File file) throws IOException, NagiosConfigurationException {
        return createSettings(new FileInputStream(file));
    }

    /**
     * Create {@link NagiosSettings} from a stream containing properties
     * 
     * @param inputStream
     *            containing properties
     * @return the {@link NagiosSettings}
     * @throws IOException
     *             thrown on IO issue accessing stream
     * @throws NagiosConfigurationException
     *             thrown on invalid configuration values
     */
    public static NagiosSettings createSettings(InputStream inputStream) throws IOException, NagiosConfigurationException {
        try {
            Properties properties = new Properties();
            properties.load(inputStream);
            return createSettings(properties);
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
    }

    /**
     * Create {@link NagiosSettings} from a properties object
     * 
     * @param properties
     *            the properties object
     * @return the {@link NagiosSettings}
     * @throws IOException
     *             thrown on IO issue accessing stream
     * @throws NagiosConfigurationException
     *             thrown on invalid configuration values
     */
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
