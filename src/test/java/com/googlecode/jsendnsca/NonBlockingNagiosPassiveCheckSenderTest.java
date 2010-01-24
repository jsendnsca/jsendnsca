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

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

import java.io.IOException;
import java.util.Date;

import org.junit.After;
import org.junit.Test;

import com.googlecode.jsendnsca.MessagePayload;
import com.googlecode.jsendnsca.NagiosException;
import com.googlecode.jsendnsca.NonBlockingNagiosPassiveCheckSender;
import com.googlecode.jsendnsca.PassiveCheckSender;


public class NonBlockingNagiosPassiveCheckSenderTest {
    
	private NonBlockingNagiosPassiveCheckSender sender;
	
	@After
	public void shutdownSender() {
	    sender.shutdown();
	}

    @Test
	public void shouldReturnImmediatelyWhenSendingPassiveCheck() throws Exception {
		sender = new NonBlockingNagiosPassiveCheckSender(new SlowNagiosPassiveCheckSender());
		
		long start = new Date().getTime();
		sender.send(new MessagePayload());
		long duration = new Date().getTime() - start;
		assertThat(duration, lessThan(100L));
	}
	
	private static class SlowNagiosPassiveCheckSender implements PassiveCheckSender {

		public void send(MessagePayload payload) throws NagiosException, IOException {
			try {
				Thread.sleep(100L);
			} catch (InterruptedException ignore) {
			}
		}
	}
}
