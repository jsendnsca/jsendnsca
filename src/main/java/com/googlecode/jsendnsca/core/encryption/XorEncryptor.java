/**
 *
 */
package com.googlecode.jsendnsca.core.encryption;

import org.apache.commons.lang.StringUtils;


class XorEncryptor implements Encryptor {

    private static final int INITIALISATION_VECTOR_SIZE = 128;

    public void encrypt(byte[] passiveCheckBytes, byte[] initVector, String password) {
        for (int y = 0, x = 0; y < passiveCheckBytes.length; y++, x++) {
            if (x >= INITIALISATION_VECTOR_SIZE) {
                x = 0;
            }
            passiveCheckBytes[y] ^= initVector[x];
        }

        if (StringUtils.isNotBlank(password)) {
            final byte[] passwordBytes = password.getBytes();

            for (int y = 0, x = 0; y < passiveCheckBytes.length; y++, x++) {
                if (x >= passwordBytes.length) {
                    x = 0;
                }
                passiveCheckBytes[y] ^= passwordBytes[x];
            }
        }
    }
}