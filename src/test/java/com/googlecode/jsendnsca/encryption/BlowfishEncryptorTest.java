package com.googlecode.jsendnsca.encryption;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class BlowfishEncryptorTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void encrypt_should_throw_on_password_too_long() throws Exception {
        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage("Key size 57 bytes is longer than the specified key size 56 bytes");

        final BlowfishEncryptor encryptor = new BlowfishEncryptor();

        encryptor.encrypt("Passive check".getBytes(), "12345678".getBytes(), "thisPasswordIsTooooooooooooooooooooooooooooooooooooooLong");
    }

}