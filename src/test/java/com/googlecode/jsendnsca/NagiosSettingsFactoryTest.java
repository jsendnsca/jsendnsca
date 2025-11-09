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

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Properties;

import static com.googlecode.jsendnsca.NagiosSettingsFactory.createSettings;
import static com.googlecode.jsendnsca.encryption.Encryption.XOR;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class NagiosSettingsFactoryTest {

    @Test
    public void shouldCreateDefaultNagiosSettingForEmptyProperties() throws Exception {
        Properties emptyProperties = new Properties();

        NagiosSettings settings = createSettings(emptyProperties);

        NagiosSettings defaultSettings = new NagiosSettings();
        assertEquals(defaultSettings, settings);
    }

    @Test
    public void shouldOverideDefaultSettingsWithValidProperties() throws Exception {
        Properties overrideAllSettings = new Properties();
        overrideAllSettings.setProperty("nagios.nsca.host", "foobar");
        overrideAllSettings.setProperty("nagios.nsca.port", "7665");
        overrideAllSettings.setProperty("nagios.nsca.password", "secret");
        overrideAllSettings.setProperty("nagios.nsca.timeout", "20000");
        overrideAllSettings.setProperty("nagios.nsca.connect.timeout", "10000");
        overrideAllSettings.setProperty("nagios.nsca.encryption", "xor");

        NagiosSettings settings = createSettings(overrideAllSettings);

        NagiosSettings expectedSettings = new NagiosSettings();
        expectedSettings.setNagiosHost("foobar");
        expectedSettings.setPort(7665);
        expectedSettings.setPassword("secret");
        expectedSettings.setTimeout(20000);
        expectedSettings.setConnectTimeout(10000);
        expectedSettings.setEncryption(XOR);

        assertEquals(expectedSettings, settings);
    }

    @Test
    public void shouldOverideDefaultSettingsWithValidPropertiesFile() throws Exception {
        NagiosSettings settings = createSettings(new File("src/test/resources/nsca.properties"));

        NagiosSettings expectedSettings = new NagiosSettings();
        expectedSettings.setNagiosHost("foobar");
        expectedSettings.setPort(7665);
        expectedSettings.setPassword("password");
        expectedSettings.setTimeout(20000);
        expectedSettings.setConnectTimeout(10000);
        expectedSettings.setEncryption(XOR);

        assertEquals(expectedSettings, settings);
    }

    @Test
    public void shouldOverideHostOnly() throws Exception {
        Properties overrideHostNameOnly = new Properties();
        overrideHostNameOnly.setProperty("nagios.nsca.host", "foobar");

        NagiosSettings settings = createSettings(overrideHostNameOnly);

        NagiosSettings expectedSettings = new NagiosSettings();
        expectedSettings.setNagiosHost("foobar");

        assertEquals(expectedSettings, settings);
    }

    @Test
    public void shouldThrowNagiosConfigurationExceptionForEmptyPropertyValue() {
        Properties emptyPropertyValue = new Properties();
        emptyPropertyValue.setProperty("nagios.nsca.host", StringUtils.EMPTY);

        NagiosConfigurationException ex = assertThrows(
                NagiosConfigurationException.class,
                () -> NagiosSettingsFactory.createSettings(emptyPropertyValue));
        assertThat(ex.getMessage(),
                is("Key [nagios.nsca.host] value cannot be empty or purely whitespace"));
    }

    @Test
    public void shouldThrowNagiosConfigurationExceptionForNonIntegerValueProvidedForIntegerValueKey() {
        Properties nonIntegerTimeout = new Properties();
        nonIntegerTimeout.setProperty("nagios.nsca.timeout", "notANumber");

        NagiosConfigurationException ex = assertThrows(NagiosConfigurationException.class,
                () -> createSettings(nonIntegerTimeout));
        assertThat(ex.getMessage(),
                is("Key [nagios.nsca.timeout] must be an integer, was [notANumber]"));
    }

    @Test
    public void shouldThrowNagiosConfigurationExceptionForOutOfRangePort() {
        Properties outOfRangePort = new Properties();
        outOfRangePort.setProperty("nagios.nsca.port", "65536");


        NagiosConfigurationException ex = assertThrows(
                NagiosConfigurationException.class,
                () -> createSettings(outOfRangePort));
        assertThat(ex.getMessage(),
                is("Key [nagios.nsca.port] port must be between 1 and 65535 inclusive, was [65536]"));
    }

    @Test
    public void shouldThrowNagiosConfigurationExceptionForUnknownEncryption() {
        Properties unknownEncryption = new Properties();
        unknownEncryption.setProperty("nagios.nsca.encryption", "foobar");

        NagiosConfigurationException ex = assertThrows(
                NagiosConfigurationException.class,
                () -> createSettings(unknownEncryption));
        assertThat(ex.getMessage(),
                is("Key [nagios.nsca.encryption] must be one of [none,triple_des,xor,rijndael128,rijndael192,rijndael256,blowfish], was [foobar]"));

    }

}
