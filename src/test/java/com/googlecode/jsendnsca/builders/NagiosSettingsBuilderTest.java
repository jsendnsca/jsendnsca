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
package com.googlecode.jsendnsca.builders;

import com.googlecode.jsendnsca.NagiosSettings;
import org.junit.Test;

import static com.googlecode.jsendnsca.encryption.Encryption.XOR;
import static org.apache.commons.lang.StringUtils.EMPTY;
import static org.junit.Assert.assertEquals;

public class NagiosSettingsBuilderTest {

    @Test
    public void shouldCreateDefault() throws Exception {
        NagiosSettings defaultNagiosSettings = new NagiosSettings();

        NagiosSettings nagiosSettings = new NagiosSettingsBuilder().createDefault();
        assertEquals(defaultNagiosSettings, nagiosSettings);
    }

    @Test
    public void shouldCreateWithEverythingOverriden() throws Exception {
        String host = "nagioshost";
        int port = 9999;
        String password = "s3cr3t";
        int connectionTimeout = 1;
        int responseTimeout = 1;

        NagiosSettings nagiosSettings = new NagiosSettingsBuilder()
            .withLargeMessageSupportEnabled()
            .withNagiosHost(host)
            .withPort(port)
            .withPassword(password)
            .withConnectionTimeout(connectionTimeout)
            .withResponseTimeout(responseTimeout)
            .withEncryption(XOR)
            .withEncryptor(XOR.getEncryptor())
            .create();

        assertEquals(host, nagiosSettings.getNagiosHost());
        assertEquals((long) port, (long) nagiosSettings.getPort());
        assertEquals(password, nagiosSettings.getPassword());
        assertEquals((long) connectionTimeout, (long) nagiosSettings.getConnectTimeout());
        assertEquals((long) responseTimeout, (long) nagiosSettings.getTimeout());
        assertEquals(XOR.getEncryptor(), nagiosSettings.getEncryptor());
        assertEquals(4096L, (long) nagiosSettings.getMaxMessageSizeInChars());
    }

    @Test
    public void shouldCreateSettingsWithNoPassword() throws Exception {
        NagiosSettings settings = new NagiosSettingsBuilder()
            .withPassword("set to something as default is empty string")
            .withNoPassword()
            .create();

        assertEquals(EMPTY, settings.getPassword());
    }
}
