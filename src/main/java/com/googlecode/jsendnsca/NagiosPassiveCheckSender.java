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

import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import org.apache.commons.lang.Validate;

import com.googlecode.jsendnsca.utils.IOUtils;

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

        final Socket socket = new Socket();
        final InetSocketAddress nagiosEndpoint = new InetSocketAddress(nagiosSettings.getNagiosHost(), nagiosSettings.getPort());

        OutputStream outputStream = null;
        DataInputStream inputStream = null;
        try {
            connectWithTimeout(socket, nagiosEndpoint);
            outputStream = socket.getOutputStream();
            inputStream = new DataInputStream(socket.getInputStream());
        } catch (IOException ioe) {
            throw ioe;
        }

        final byte[] initVector = readInitializationVector(inputStream);
        int timeStamp = inputStream.readInt();

        try {

            final byte[] passiveCheckBytes = new PassiveCheckBytesBuilder().withTimeStamp(timeStamp).withLevel(payload.getLevel()).withHostname(
                    payload.getHostname()).withServiceName(payload.getServiceName()).withMessage(payload.getMessage()).writeCRC().encrypt(initVector,
                    nagiosSettings).toByteArray();

            outputStream.write(passiveCheckBytes, 0, passiveCheckBytes.length);
            outputStream.flush();
        } catch (SocketTimeoutException ste) {
            throw ste;
        } catch (Exception e) {
            throw new NagiosException("Error occurred while sending passive alert", e);
        } finally {
            IOUtils.closeQuietly(outputStream);
            IOUtils.closeQuietly(inputStream);
            socket.close();
        }
    }

    private void connectWithTimeout(final Socket socket, final InetSocketAddress nagiosEndpoint) throws IOException, SocketException {
        socket.connect(nagiosEndpoint, nagiosSettings.getConnectTimeout());
        socket.setSoTimeout(nagiosSettings.getTimeout());
    }

    private byte[] readInitializationVector(DataInputStream inputStream) throws NagiosException, SocketTimeoutException {
        final byte[] initVector = new byte[INITIALISATION_VECTOR_SIZE];
        try {
            inputStream.readFully(initVector, 0, INITIALISATION_VECTOR_SIZE);
            return initVector;
        } catch (SocketTimeoutException ste) {
            throw ste;
        } catch (Exception e) {
            throw new NagiosException("Can't read initialisation vector", e);
        }
    }
}