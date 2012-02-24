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

import org.apache.commons.lang.Validate;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;

import static com.googlecode.jsendnsca.utils.IOUtils.closeQuietly;

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
     * @param nagiosSettings
     *            the {@link NagiosSettings} to use to send the Passive Check
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
    public void send(MessagePayload payload) throws NagiosException, IOException {
        Validate.notNull(payload, "payload cannot be null");

        Socket socket = connectedToNagios();
        OutputStream outputStream = socket.getOutputStream();
        InputStream inputStream = socket.getInputStream();

        try {
            outputStream.write(passiveCheck(payload, new DataInputStream(inputStream)));
            outputStream.flush();
        } catch (SocketTimeoutException ste) {
            throw ste;
        } catch (IOException e) {
            throw new NagiosException("Error occurred while sending passive alert", e);
        } finally {
            close(socket, outputStream, inputStream);
        }
    }

    private Socket connectedToNagios() throws IOException {
        Socket socket = new Socket();
        socket.connect(new InetSocketAddress(nagiosSettings.getNagiosHost(), nagiosSettings.getPort()), nagiosSettings.getConnectTimeout());
        socket.setSoTimeout(nagiosSettings.getTimeout());
        return socket;
    }

    private byte[] passiveCheck(MessagePayload payload, DataInputStream inputStream) throws IOException, NagiosException {
        final byte[] initVector = readFrom(inputStream);
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
    }

    private static void close(final Socket socket, OutputStream outputStream, InputStream inputStream) {
        closeQuietly(inputStream);
        closeQuietly(outputStream);
        closeQuietly(socket);
    }

    private static byte[] readFrom(DataInputStream inputStream) throws NagiosException, SocketTimeoutException {
        try {
            final byte[] initVector = new byte[INITIALISATION_VECTOR_SIZE];
            inputStream.readFully(initVector, 0, INITIALISATION_VECTOR_SIZE);
            return initVector;
        } catch (SocketTimeoutException ste) {
            throw ste;
        } catch (IOException e) {
            throw new NagiosException("Can't read initialisation vector", e);
        }
    }
}