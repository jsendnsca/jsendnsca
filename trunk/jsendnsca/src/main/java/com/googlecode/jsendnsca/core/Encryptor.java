/**
 * 
 */
package com.googlecode.jsendnsca.core;

interface Encryptor {
    void encrypt(byte[] passiveCheckBytes, byte[] initVector, String password);
}