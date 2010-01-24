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

import static org.junit.Assert.*;

import org.apache.commons.lang.StringUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.googlecode.jsendnsca.NagiosSettings;
import com.googlecode.jsendnsca.encryption.Encryption;
import com.googlecode.jsendnsca.encryption.TripleDESEncryptor;

public class NagiosSettingsTest {
    
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void shouldThrowIllegalArgumentExceptionWhenSettingHostnameToEmptyString() throws Exception {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("nagiosHost cannot be null or empty");
        
        final NagiosSettings nagiosSettings = new NagiosSettings();

        nagiosSettings.setNagiosHost(StringUtils.EMPTY);
    }

    @Test
    public void shouldThrowIllegalArgumentExceptionWhenSettingPasswordToEmptyString() throws Exception {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("password cannot be null or empty");
        
        final NagiosSettings nagiosSettings = new NagiosSettings();

        nagiosSettings.setPassword(StringUtils.EMPTY);
    }
    
    @Test
    public void shouldThrowIllegalArgumentExceptionForInvalidPort() throws Exception {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("port must be between 1 and 65535 inclusive");
        
        NagiosSettings nagiosSettings = new NagiosSettings();
        int maxValidPort = 65535;
        nagiosSettings.setPort(maxValidPort + 1);
    }

    @Test
    public void shouldSetEncryptionUsingEnum() throws Exception {
        NagiosSettings settings = new NagiosSettings();

        settings.setEncryption(Encryption.TRIPLE_DES_ENCRYPTION);

        assertEquals(Encryption.TRIPLE_DES_ENCRYPTION.getEncryptor(), settings.getEncryptor());
    }
    
    @Test
    public void shouldReturnStringOfNagiosSettings() throws Exception {
        String settings = new NagiosSettings().toString();
        assertEquals("NagiosSettings[nagiosHost=localhost,port=5667,password=password,timeout=10000,connectTimeout=5000,encryptor=none]", settings);
    }

    @Test
    public void shouldSetEncryptionUsingEncryptor() throws Exception {
        TripleDESEncryptor expectedEncryptor = new TripleDESEncryptor();
        NagiosSettings settings = new NagiosSettings();

        settings.setEncryptor(expectedEncryptor);

        assertEquals(expectedEncryptor, settings.getEncryptor());
    }
}
