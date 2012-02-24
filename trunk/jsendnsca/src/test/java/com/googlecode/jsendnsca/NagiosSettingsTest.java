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

import com.googlecode.jsendnsca.encryption.Encryption;
import com.googlecode.jsendnsca.encryption.Encryptor;
import com.googlecode.jsendnsca.encryption.TripleDESEncryptor;
import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertEquals;

public class NagiosSettingsTest {

    private NagiosSettings nagiosSettings;

    @SuppressWarnings({"PublicField"})
    @Rule
    public final ExpectedException expectedException = ExpectedException.none();
    
    @Before
    public void setUp() {
        nagiosSettings = new NagiosSettings();
    }

    @Test
    public void shouldThrowIllegalArgumentExceptionWhenSettingHostnameToEmptyString() throws Exception {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("nagiosHost cannot be null or empty");

        nagiosSettings.setNagiosHost(StringUtils.EMPTY);
    }

    @Test
    public void shouldThrowIllegalArgumentExceptionForInvalidPort() throws Exception {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("port must be between 1 and 65535 inclusive");

        int maxValidPort = 65535;
        nagiosSettings.setPort(maxValidPort + 1);
    }

    @Test
    public void shouldSetEncryptionUsingEnum() throws Exception {
        nagiosSettings.setEncryption(Encryption.TRIPLE_DES);

        assertEquals(Encryption.TRIPLE_DES.getEncryptor(), nagiosSettings.getEncryptor());
    }

    @Test
    public void shouldReturn512MaxCharsInMessageByDefault() throws Exception {
        assertEquals(512L, (long) nagiosSettings.getMaxMessageSizeInChars());
    }
    
    @Test
    public void shouldReturn4096MaxCharsInMessageWhenEnabled() throws Exception {
        nagiosSettings.enableLargeMessageSupport();

        assertEquals(4096L, (long) nagiosSettings.getMaxMessageSizeInChars());
    }
    
    @Test
    public void shouldThrowIllegalArgumentExceptionForNullEncryptor() throws Exception {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("encryptor cannot be null");
        
        nagiosSettings.setEncryptor(null);
    }

    @Test
    public void shouldReturnStringOfNagiosSettings() throws Exception {
        String settings = new NagiosSettings().toString();
        assertEquals("NagiosSettings[nagiosHost=localhost,port=5667,password=,timeout=10000,connectTimeout=5000,encryptor=none]", settings);
    }

    @Test
    public void shouldSetEncryptionUsingEncryptor() throws Exception {
        Encryptor expectedEncryptor = new TripleDESEncryptor();
        nagiosSettings.setEncryptor(expectedEncryptor);

        assertEquals(expectedEncryptor, nagiosSettings.getEncryptor());
    }
}
