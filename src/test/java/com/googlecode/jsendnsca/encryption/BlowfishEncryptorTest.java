package com.googlecode.jsendnsca.encryption;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class BlowfishEncryptorTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void constructor_should_throw_on_keyBytesLength_less_than_1() throws Exception {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("keyBytesLength must be greater than zero");

        new BlowfishEncryptor(0);
    }

    @Test
    public void encrypt_should_throw_on_password_too_long() throws Exception {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Key size 21 is longer than the specified key size 16");

        final BlowfishEncryptor encryptor = new BlowfishEncryptor(16);

        encryptor.encrypt("Passive check".getBytes(), "12345678".getBytes(), "thisPasswordIsTooLong");
    }

}