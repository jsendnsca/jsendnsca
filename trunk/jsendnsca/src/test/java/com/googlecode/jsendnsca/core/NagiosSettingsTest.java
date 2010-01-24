package com.googlecode.jsendnsca.core;

import static org.hamcrest.Matchers.*;

import static org.junit.Assert.*;

import org.apache.commons.lang.StringUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.googlecode.jsendnsca.core.encryption.Encryption;
import com.googlecode.jsendnsca.core.encryption.TripleDESEncryptor;

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
