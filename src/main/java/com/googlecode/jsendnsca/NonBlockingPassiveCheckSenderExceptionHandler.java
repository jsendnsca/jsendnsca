package com.googlecode.jsendnsca;

/**
 * Exception handler to handle exceptions while sending passive checks with the {@link NonBlockingNagiosPassiveCheckSender}.
 *
 * @author max.schwaab@gmail.com
 */
public interface NonBlockingPassiveCheckSenderExceptionHandler {

    /**
     * Handles an exception thrown while sending a passive check.
     *
     * @param exception The exception to handle.
     */
    void handleException(Exception exception);

}
