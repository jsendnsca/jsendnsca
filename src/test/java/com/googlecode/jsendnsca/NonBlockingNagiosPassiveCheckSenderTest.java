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

import com.googlecode.jsendnsca.NonBlockingNagiosPassiveCheckSender.ExceptionHandler;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class NonBlockingNagiosPassiveCheckSenderTest {

    private NonBlockingNagiosPassiveCheckSender sender;

    @BeforeEach
    public void setUp() {
        sender = new NonBlockingNagiosPassiveCheckSender(new SlowNagiosPassiveCheckSender(), new TestExceptionHandler());
    }

    @AfterEach
    public void shutdownSender() {
        sender.shutdown();
    }

    @Test
    public void shouldReturnImmediatelyWhenSendingPassiveCheck() throws Exception {
        sender = new NonBlockingNagiosPassiveCheckSender(new SlowNagiosPassiveCheckSender(), new TestExceptionHandler());

        long start = new Date().getTime();
        sender.send(new MessagePayload());
        long duration = new Date().getTime() - start;

        assertThat(duration, lessThan(100L));
    }

    @Test
    public void shouldBeAbleToUseOwnExecutor() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        sender.setExecutor(new CurrentThreadExecutorService(latch));

        sender.send(new MessagePayload());

        assertTrue(latch.await(10, TimeUnit.SECONDS), "timed out waiting for message to be sent");
    }

    private static class SlowNagiosPassiveCheckSender implements PassiveCheckSender {

        public void send(MessagePayload payload) throws NagiosException {
            try {
                Thread.sleep(100L);
            } catch (InterruptedException ignore) {
            }
        }
    }

    private static class TestExceptionHandler implements ExceptionHandler {

        @Override
        public void handleException(final Exception exception) {
            exception.printStackTrace();
        }

    }

    private static class CurrentThreadExecutorService extends AbstractExecutorService {
        private final CountDownLatch countDownLatch;

        public CurrentThreadExecutorService(CountDownLatch countDownLatch) {
            this.countDownLatch = countDownLatch;
        }

        @Override
        public void shutdown() {

        }

        @Override
        public List<Runnable> shutdownNow() {
            return Collections.emptyList();
        }

        @Override
        public boolean isShutdown() {
            return false;
        }

        @Override
        public boolean isTerminated() {
            return false;
        }

        @Override
        public boolean awaitTermination(long timeout, TimeUnit unit) {
            return false;
        }

        @Override
        public void execute(Runnable command) {
            countDownLatch.countDown();
        }
    }
}
