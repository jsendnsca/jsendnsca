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
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class NagiosSettingsTest {

    private NagiosSettings nagiosSettings;

    @BeforeEach
    public void setUp() {
        nagiosSettings = new NagiosSettings();
    }

    @Test
    public void shouldThrowIllegalArgumentExceptionWhenSettingHostnameToEmptyString() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> nagiosSettings.setNagiosHost(EMPTY));
        assertThat(ex.getMessage(), is("nagiosHost cannot be null or empty"));
    }

    @Test
    public void shouldThrowIllegalArgumentExceptionForInvalidPort() {
        int maxValidPort = 65535;
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                nagiosSettings.setPort(maxValidPort + 1)
        );
        assertThat(ex.getMessage(), is("port must be between 1 and 65535 inclusive"));
    }

    @Test
    public void shouldSetEncryptionUsingEnum() {
        nagiosSettings.setEncryption(Encryption.TRIPLE_DES);

        assertEquals(Encryption.TRIPLE_DES.getEncryptor(), nagiosSettings.getEncryptor());
    }

    @Test
    public void shouldReturn512MaxCharsInMessageByDefault() {
        assertEquals(512L, nagiosSettings.getMaxMessageSizeInChars());
    }
    
    @Test
    public void shouldReturn4096MaxCharsInMessageWhenEnabled() {
        nagiosSettings.enableLargeMessageSupport();

        assertEquals(4096L, nagiosSettings.getMaxMessageSizeInChars());
    }
    
    @Test
    public void shouldThrowNPEForNullEncryptor() {
        NullPointerException thrown = assertThrows(NullPointerException.class, () ->
                nagiosSettings.setEncryptor(null)
        );
        assertThat(thrown.getMessage(), is("encryptor cannot be null"));
    }

    @Test
    public void shouldReturnStringOfNagiosSettings() {
        String settings = new NagiosSettings().toString();

        assertThat(settings, Matchers.startsWith("NagiosSettings[nagiosHost=localhost,port=5667,password=,timeout=10000,connectTimeout=5000"));
    }

    @Test
    public void shouldSetEncryptionUsingEncryptor() {
        Encryptor expectedEncryptor = new TripleDESEncryptor();
        nagiosSettings.setEncryptor(expectedEncryptor);

        assertEquals(expectedEncryptor, nagiosSettings.getEncryptor());
    }
}
