/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.googlecode.jsendnsca.builders;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import com.googlecode.jsendnsca.Level;
import com.googlecode.jsendnsca.MessagePayload;

import java.net.UnknownHostException;

import static org.junit.Assert.assertEquals;

/**
 * @version $Revision$
 */
public class MessagePayloadBuilderTest {

    @Test
    public void shouldConstructNewMessagePayload() {
        final MessagePayload messagePayload = new MessagePayloadBuilder()
            .withHostname("localhost")
            .withLevel(Level.CRITICAL)
            .withServiceName("test service")
            .withMessage("test message")
            .create();

        assertEquals("localhost", messagePayload.getHostname());
        assertEquals(Level.CRITICAL, messagePayload.getLevel());
        assertEquals("test service", messagePayload.getServiceName());
        assertEquals("test message", messagePayload.getMessage());
    }

    @Test
    public void shouldConstructTwoNewMessagePayload() {
        final MessagePayload messagePayload = new MessagePayloadBuilder()
            .withHostname("localhost")
            .withLevel(Level.OK)
            .withServiceName("test service")
            .withMessage("test message")
            .create();

        final MessagePayload messagePayload2 = new MessagePayloadBuilder()
            .withHostname("somehost")
            .withLevel(Level.WARNING)
            .withServiceName("foo service")
            .withMessage("foo message")
            .create();

        assertEquals("localhost", messagePayload.getHostname());
        assertEquals(Level.OK, messagePayload.getLevel());
        assertEquals("test service", messagePayload.getServiceName());
        assertEquals("test message", messagePayload.getMessage());

        assertEquals("somehost", messagePayload2.getHostname());
        assertEquals(Level.WARNING, messagePayload2.getLevel());
        assertEquals("foo service", messagePayload2.getServiceName());
        assertEquals("foo message", messagePayload2.getMessage());
    }

    @Test
    public void shouldConstructPayloadWithoutUsingLocalHostname() {
        MessagePayload messagePayload = new MessagePayloadBuilder() {
            @Override
            MessagePayload createMessagePayload() {
                return new MessagePayload(false) {
                    @Override
                    public void useLocalHostname() {
                        throw new UnknownHostRuntimeException(new UnknownHostException());
                    }
                };
            }
        }.create();

        assertEquals("UNKNOWN", messagePayload.getHostname());
        assertEquals(Level.UNKNOWN, messagePayload.getLevel());
        assertEquals("UNDEFINED", messagePayload.getServiceName());
        assertEquals(StringUtils.EMPTY, messagePayload.getMessage());
    }

}
