package com.googlecode.jsendnsca.core;


public enum Encryption {

    NO_ENCRYPTION(), 
    XOR_ENCRYPTION(1 ,new XorEncryptor()), 
    TRIPLE_DES_ENCRYPTION(3, new TripleDESEncryptor());

    private final int code;
    private final Encryptor encryptor;

    private Encryption() {
        this.code = 0;
        this.encryptor = doNothingEncryptor();
    }

    private Encryptor doNothingEncryptor() {
        return new Encryptor() {
            public void encrypt(byte[] passiveCheckBytes, byte[] initVector, String password) {
            }
        };
    }
    
    private Encryption(int code, Encryptor encryptor) {
        this.code = code;
        this.encryptor = encryptor;
    }

    int getCode() {
        return code;
    }
    
    Encryptor getEncryptor() {
        return encryptor;
    }
    
    static Encryptor getEncryptor(int code) {
        for (Encryption encryption : Encryption.values()) {
            if (encryption.getCode() == code) {
                return encryption.getEncryptor();
            }
        }
        throw new IllegalArgumentException(String.format("Code [%d] is not a supported encryption method code", code));
    }
}
