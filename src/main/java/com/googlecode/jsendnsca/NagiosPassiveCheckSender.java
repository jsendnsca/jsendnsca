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

import org.apache.commons.lang3.Validate;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * This class is used to send a Passive Check to the Nagios NSCA add-on
 *
 * @author Raj.Patel
 * @version 1.0
 */
public class NagiosPassiveCheckSender implements PassiveCheckSender {

    private static final int INITIALISATION_VECTOR_SIZE = 128;

    private final NagiosSettings nagiosSettings;

    /**
     * Construct a new {@link NagiosPassiveCheckSender} with the provided
     * {@link NagiosSettings}
     *
     * @param nagiosSettings the {@link NagiosSettings} to use to send the Passive Check
     */
    public NagiosPassiveCheckSender(NagiosSettings nagiosSettings) {
        Validate.notNull(nagiosSettings, "nagiosSettings cannot be null");
        this.nagiosSettings = nagiosSettings;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.googlecode.jsendnsca.sender.INagiosPassiveCheckSender#send(com.googlecode
     * .jsendnsca.sender.MessagePayload)
     */
    public void send(MessagePayload payload) {
        Validate.notNull(payload, "payload cannot be null");

        try (Socket socket = connectedToNagios()) {
            try (OutputStream outputStream = socket.getOutputStream()) {
                try (InputStream inputStream = socket.getInputStream()) {
                    outputStream.write(passiveCheck(payload, new DataInputStream(inputStream)));
                    outputStream.flush();
                }
            }
        } catch (IOException e) {
            throw new NagiosException("Error occurred while sending passive alert", e);
        }
    }

    private Socket connectedToNagios() {
        try {
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress(nagiosSettings.getNagiosHost(), nagiosSettings.getPort()), nagiosSettings.getConnectTimeout());
            socket.setSoTimeout(nagiosSettings.getTimeout());
            return socket;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private byte[] passiveCheck(MessagePayload payload, DataInputStream inputStream) {
        final byte[] initVector = readFrom(inputStream);
        try {
            int receivedTimeStamp = inputStream.readInt();
            return new PassiveCheckBytesBuilder(nagiosSettings)
                    .withTimeStamp(receivedTimeStamp)
                    .withLevel(payload.getLevel())
                    .withHostname(payload.getHostname())
                    .withServiceName(payload.getServiceName())
                    .withMessage(payload.getMessage())
                    .writeCRC()
                    .encrypt(initVector)
                    .toByteArray();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static byte[] readFrom(DataInputStream inputStream) {
        try {
            final byte[] initVector = new byte[INITIALISATION_VECTOR_SIZE];
            inputStream.readFully(initVector, 0, INITIALISATION_VECTOR_SIZE);
            return initVector;
        } catch (IOException e) {
            throw new NagiosException("Can't read initialisation vector", e);
        }
    }
}