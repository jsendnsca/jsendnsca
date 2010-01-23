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

import static com.googlecode.jsendnsca.core.encryption.Encryption.*;

import org.apache.commons.lang.Validate;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.googlecode.jsendnsca.core.encryption.Encryption;
import com.googlecode.jsendnsca.core.encryption.Encryptor;

/**
 * The settings to use for sending the Passive Check
 * 
 * @author Raj.Patel
 * @version 1.0
 * @see NagiosSettingsBuilder
 */
public class NagiosSettings {

    private static final int MIN_PORT = 1;
    private static final int MAX_PORT = 65535;
    
    private String nagiosHost = "localhost";
    private int port = 5667;
    private String password = "password";
    private int timeout = 10000;
    private int connectTimeout = 5000;
    private Encryptor encryptor = NO_ENCRYPTION.getEncryptor();

    /**
     * The connection timeout
     * 
     * @return timeout in ms
     */
    public int getConnectTimeout() {
        return connectTimeout;
    }

    /**
     * The {@link Encryptor} used to encrypt the passive check
     * 
     * @return the {@link Encryptor}
     */
    public Encryptor getEncryptor() {
        return encryptor;
    }

    /**
     * The host or IP of the Nagios host running the NSCA add-on
     * 
     * @return the host or IP, defaults to localhost
     */
    public String getNagiosHost() {
        return nagiosHost;
    }

    /**
     * The password configured in the ncsa.cfg file used by NSCA
     * 
     * @return the password, defaults to "password"
     */
    public String getPassword() {
        return password;
    }

    /**
     * The port on which NSCA is listening
     * 
     * @return the port, defaults to 5667
     */
    public int getPort() {
        return port;
    }

    /**
     * The socket timeout to use when sending the passive check
     * 
     * @return the timeout in ms, defaults to 10000 ms
     */
    public int getTimeout() {
        return timeout;
    }

    /**
     * Set the connection timeout, default is 5000 ms
     * 
     * @param connectTimeout
     *            timeout in ms
     */
    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    /**
     * The {@link Encryption} to use to encrypt the passive check
     * 
     * @param encryption
     */
    public void setEncryption(Encryption encryption) {
        this.encryptor = encryption.getEncryptor();
    }

    /**
     * The {@link Encryptor} to use to encrypt the passive check
     * 
     * @param encryptor
     */
    public void setEncryptor(Encryptor encryptor) {
        this.encryptor = encryptor;
    }

    /**
     * The host or IP of the Nagios host running the NSCA add-on
     * 
     * @param nagiosHost
     *            the host or IP, defaults to localhost
     */
    public void setNagiosHost(String nagiosHost) {
        Validate.notEmpty(nagiosHost, "nagiosHost cannot be null or empty");
        this.nagiosHost = nagiosHost;
    }

    /**
     * The password configured in the ncsa.cfg file used by NSCA
     * 
     * @param password
     *            the password, defaults to "password"
     */
    public void setPassword(String password) {
        Validate.notEmpty(password, "password cannot be null or empty");
        this.password = password;
    }

    /**
     * The port on which NSCA is listening
     * 
     * @param port
     *            the port, defaults to 5667
     */
    public void setPort(int port) {
        Validate.isTrue(validPortRange(port), "port must be between 1 and 65535 inclusive");
        this.port = port;
    }

    /**
     * The socket timeout to use when sending the passive check
     * 
     * @param timeout
     *            the timeout in ms, defaults to 10000 ms
     */
    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(19, 55)
            .append(nagiosHost)
            .append(port)
            .append(password)
            .append(timeout)
            .append(connectTimeout)
            .append(encryptor)
            .toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof NagiosSettings == false) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        NagiosSettings rhs = (NagiosSettings) obj;
        
        return new EqualsBuilder()
            .append(nagiosHost, rhs.nagiosHost)
            .append(port, rhs.port)
            .append(password,rhs.password)
            .append(timeout, rhs.timeout)
            .append(connectTimeout, rhs.connectTimeout)
            .append(encryptor, rhs.encryptor)
            .isEquals();
    }

    private boolean validPortRange(int port) {
        return port >= MIN_PORT && port <= MAX_PORT;
    }

}