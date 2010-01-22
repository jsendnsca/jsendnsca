package com.googlecode.jsendnsca.core;

import static org.junit.Assert.*;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;

import com.googlecode.jsendnsca.core.encryption.Encryption;
import com.googlecode.jsendnsca.core.encryption.TripleDESEncryptor;

public class NagiosSettingsTest {

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentExceptionWhenSettingHostnameToEmptyString() throws Exception {
        final NagiosSettings nagiosSettings = new NagiosSettings();

        nagiosSettings.setNagiosHost(StringUtils.EMPTY);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentExceptionWhenSettingPasswordToEmptyString() throws Exception {
        final NagiosSettings nagiosSettings = new NagiosSettings();

        nagiosSettings.setPassword(StringUtils.EMPTY);
    }

    @Test
    public void shouldSetEncryptionUsingEnum() throws Exception {
        NagiosSettings settings = new NagiosSettings();

        settings.setEncryption(Encryption.TRIPLE_DES_ENCRYPTION);

        assertEquals(Encryption.TRIPLE_DES_ENCRYPTION.getEncryptor(), settings.getEncryptor());
    }

    @Test
    public void shouldSetEncryptionUsingEncryptor() throws Exception {
        TripleDESEncryptor expectedEncryptor = new TripleDESEncryptor();
        NagiosSettings settings = new NagiosSettings();

        settings.setEncryptor(expectedEncryptor);

        assertEquals(expectedEncryptor, settings.getEncryptor());
    }
}
