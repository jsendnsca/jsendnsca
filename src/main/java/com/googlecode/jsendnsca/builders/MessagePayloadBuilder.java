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
package com.googlecode.jsendnsca.builders;

import com.googlecode.jsendnsca.Level;
import com.googlecode.jsendnsca.MessagePayload;

import java.net.UnknownHostException;

/**
 * Used to construct a {@link MessagePayload} using a builder pattern e.g.
 * 
 * <pre>
 * MessagePayload payload = new MessagePayloadBuilder()
 *      .withHostname(&quot;localhost&quot;)
 *      .withLevel(Level.CRITICAL)
 *      .withServiceName(&quot;Test Service Name&quot;)
 *      .withMessage(&quot;Test Message&quot;)
 *      .create();
 * </pre>
 * 
 * @author Raj.Patel
 * @since 1.2
 */
public class MessagePayloadBuilder {

    private final MessagePayload payload = new MessagePayload();

    /**
     * Return the built {@link MessagePayload}
     * 
     * @return the built {@link MessagePayload}
     */
    public MessagePayload create() {
        return payload;
    }

    /**
     * Use the short hostname of the local machine in the passive check
     * 
     * @return the {@link MessagePayloadBuilder}
     * @throws UnknownHostException
     *             error while determining local machine name
     */
    public MessagePayloadBuilder withLocalHostname() throws UnknownHostException {
        payload.useLocalHostname();
        return this;
    }

    /**
     * Use the fully qualified domain name of the local machine in the passive
     * check
     * 
     * @return the {@link MessagePayloadBuilder}
     * @throws UnknownHostException
     *             error while determining local machine name
     */
    public MessagePayloadBuilder withCanonicalHostname() throws UnknownHostException {
        payload.setHostname(true);
        return this;
    }

    /**
     * Use the supplied hostname in the passive check
     * 
     * @param hostname
     *            the hostname
     * @return the {@link MessagePayloadBuilder}
     */
    public MessagePayloadBuilder withHostname(String hostname) {
        payload.setHostname(hostname);
        return this;
    }

    /**
     * Set the level of the passive check
     * 
     * @param level
     *            the level value
     * @return the {@link MessagePayloadBuilder}
     */
    public MessagePayloadBuilder withLevel(int level) {
        payload.setLevel(Level.toLevel(level));
        return this;
    }

    /**
     * Set the level of the passive check
     * 
     * @param level
     *            the {@link Level}
     * @return the {@link MessagePayloadBuilder}
     */
    public MessagePayloadBuilder withLevel(Level level) {
        payload.setLevel(level);
        return this;
    }

    /**
     * Set the service name of the passive check
     * 
     * @param serviceName
     *            the service name
     * @return the {@link MessagePayloadBuilder}
     */
    public MessagePayloadBuilder withServiceName(String serviceName) {
        payload.setServiceName(serviceName);
        return this;
    }

    /**
     * Set the message of the passive check which will be truncated to 512
     * chars unless withSupportForLargeMessages is invoked when the message
     * will be truncated to 4096 chars
     * 
     * @param message
     *            the message
     * @return the {@link MessagePayloadBuilder}
     */
    public MessagePayloadBuilder withMessage(String message) {
        payload.setMessage(message);
        return this;
    }

    /**
     * Set support for larger message of 4096 chars which is supported from NSCA 2.9.1
     *
     * Any message larger than 4096 will be truncated to this
     *
     * @return the {@link MessagePayloadBuilder}
     */
    public MessagePayloadBuilder withSupportForLargeMessages() {
        payload.setSupportForLargeMessage();
        return this;
    }
}
