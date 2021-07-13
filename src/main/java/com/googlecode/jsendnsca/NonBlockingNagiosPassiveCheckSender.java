/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.googlecode.jsendnsca;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * This sender does not block unlike the {@link NagiosPassiveCheckSender}.
 * Instead it internally queues the passive check in an unbounded queue and has
 * a single worker thread sending from the queue.
 * <p>
 *
 * Any exceptions resulting from sending the passive check are output to
 * standard error with a stack trace.
 * <p>
 *
 * This sender is useful where you don't want to wait for the passive check to
 * be sent and don't care if the sending fails
 * <p>
 *
 * @author Raj Patel
 * @since 1.2
 */
public class NonBlockingNagiosPassiveCheckSender implements PassiveCheckSender {

    private final PassiveCheckSender sender;
    private final ExceptionHandler handler;

    private ExecutorService executor;

    /**
     * Construct a new {@link NonBlockingNagiosPassiveCheckSender} with the
     * provided {@link NagiosSettings}
     *
     * @param settings
     *            the {@link NagiosSettings} to use to send the Passive Check
     */
    public NonBlockingNagiosPassiveCheckSender(NagiosSettings settings) {
        this(new NagiosPassiveCheckSender(settings), new StandardErrorExceptionHandler());
    }

    /**
     * Construct a new {@link NonBlockingNagiosPassiveCheckSender} with the
     * provided {@link NagiosSettings} and {@link ExceptionHandler}
     *
     * @param settings the {@link NagiosSettings} to use to send the Passive Check
     * @param handler the {@link ExceptionHandler} to use while sending the Passive Check
     */
    public NonBlockingNagiosPassiveCheckSender(NagiosSettings settings, ExceptionHandler handler) {
        this(new NagiosPassiveCheckSender(settings), handler);
    }

    NonBlockingNagiosPassiveCheckSender(PassiveCheckSender sender, ExceptionHandler handler) {
        this.sender = sender;
        this.handler = handler;
        this.executor = Executors.newSingleThreadExecutor();
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.googlecode.jsendnsca.sender.INagiosPassiveCheckSender#send(com.googlecode
     * .jsendnsca.sender.MessagePayload)
     */
    @Override
    public void send(MessagePayload payload) throws NagiosException {
        executor.execute(new NonBlockingSender(payload));
    }

    /**
     * Sets the backing executor to use if you do not want to use the default
     * executor which is a single thread executor.
     * <p>
     * You may want to use a custom executor in environments where you want to
     * be in control of the used thread pools.
     *
     * @param executor
     *            the custom executor to use
     */
    public void setExecutor(ExecutorService executor) {
        this.executor = executor;
    }

    /**
     * Shutdown the backing executor.
     * <p>
     * To be used when your application has been shutdown and you want to
     * cleanup all resources such as if you run in a hot deployment environment.
     */
    public void shutdown() {
        executor.shutdown();
    }

    /**
     * Exception handler to handle exceptions while sending passive checks with the {@link NonBlockingNagiosPassiveCheckSender}.
     *
     * @author max.schwaab@gmail.com
     */
    public interface ExceptionHandler {

        /**
         * Handles an exception thrown while sending a passive check.
         *
         * @param exception The exception to handle.
         */
        void handleException(Exception exception);

    }

    private class NonBlockingSender implements Runnable {

        private final MessagePayload payload;

        public NonBlockingSender(MessagePayload payload) {
            this.payload = payload;
        }

        @Override
        public void run() {
            try {
                sender.send(payload);
            } catch (Exception e) {
                handler.handleException(e);
            }
        }
    }

    private static class StandardErrorExceptionHandler implements ExceptionHandler {

        @Override
        public void handleException(final Exception e) {
            e.printStackTrace();
        }

    }

}
