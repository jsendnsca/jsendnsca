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

import static com.googlecode.jsendnsca.encryption.Encryption.*;

import static org.junit.Assert.*;

import java.util.Properties;

import org.junit.Test;


public class NagiosSettingsFactoryTest {

    @Test
    public void shouldCreateDefaultNagiosSettingForEmptyProperties() throws Exception {
        Properties emptyProperties = new Properties();
        
        NagiosSettings settings = NagiosSettingsFactory.createSettings(emptyProperties);
        
        NagiosSettings defaultSettings = new NagiosSettings();
        assertEquals(defaultSettings, settings);
    }
    
    @Test
    public void shouldOverideDefaultSettings() throws Exception {
        Properties overrideAllSettings = new Properties();
        overrideAllSettings.setProperty("nagios.nsca.host", "foobar");
        overrideAllSettings.setProperty("nagios.nsca.port", "7665");
        overrideAllSettings.setProperty("nagios.nsca.password", "secret");
        overrideAllSettings.setProperty("nagios.nsca.timeout", "20000");
        overrideAllSettings.setProperty("nagios.nsca.connect.timeout", "10000");
        overrideAllSettings.setProperty("nagios.nsca.encryption", "xor");
        
        NagiosSettings settings = NagiosSettingsFactory.createSettings(overrideAllSettings);
        
        NagiosSettings expectedSettings = new NagiosSettings();
        expectedSettings.setNagiosHost("foobar");
        expectedSettings.setPort(7665);
        expectedSettings.setPassword("secret");
        expectedSettings.setTimeout(20000);
        expectedSettings.setConnectTimeout(10000);
        expectedSettings.setEncryption(XOR);
        
        assertEquals(expectedSettings, settings);
    }
}
