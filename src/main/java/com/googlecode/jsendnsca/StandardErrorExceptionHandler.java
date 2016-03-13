package com.googlecode.jsendnsca;

/**
 * {@link NonBlockingPassiveCheckSenderExceptionHandler} to print the handled exception to standard error.
 *
 * @author max.schwaab@gmail.com
 */
public class StandardErrorExceptionHandler implements NonBlockingPassiveCheckSenderExceptionHandler {

    @Override
    public void handleException(final Exception e) {
        e.printStackTrace();
    }

}
