/**
 * 
 */
package com.googlecode.jsendnsca.core;

import com.googlecode.jsendnsca.core.utils.EncryptionUtils;
import com.googlecode.jsendnsca.core.utils.StringUtils;


class XorEncryptor implements Encryptor {

    public void encrypt(byte[] passiveCheckBytes, byte[] initVector, String password) {
        for (int y = 0, x = 0; y < passiveCheckBytes.length; y++, x++) {
            if (x >= EncryptionUtils.INITIALISATION_VECTOR_SIZE) {
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