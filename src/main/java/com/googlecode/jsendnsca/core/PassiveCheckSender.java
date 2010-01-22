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
package com.googlecode.jsendnsca.core;

import java.io.IOException;

/**
 * Interface to be implemented by {@link PassiveCheckSender}'s
 *
 * @author Raj.Patel
 * @version 1.0
 */
public interface PassiveCheckSender
{
	/**
	 * Send Passive Check
	 *
	 * @param payload the Passive Check message payload
	 * @throws NagiosException thrown if an error occurs while sending the passive check
	 * @throws IOException thrown if I/O error occurs while trying to establish connection with nagios host
	 */
	void send(MessagePayload payload) throws NagiosException, IOException;
}
