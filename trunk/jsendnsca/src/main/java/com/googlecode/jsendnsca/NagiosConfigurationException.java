package com.googlecode.jsendnsca;

public class NagiosConfigurationException extends Exception {
    private static final long serialVersionUID = 7490550467630607617L;

    public NagiosConfigurationException(String messageFormat, Object... args) {
        super(String.format(messageFormat, args));
    }
}