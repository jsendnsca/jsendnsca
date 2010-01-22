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

import com.googlecode.jsendnsca.core.builders.NagiosSettingsBuilder;
import com.googlecode.jsendnsca.core.utils.EncryptionUtils;
import com.googlecode.jsendnsca.core.utils.StringUtils;

/**
 * The settings to use for sending the Passive Check
 * 
 * @author Raj.Patel
 * @version 1.0
 * @see NagiosSettingsBuilder
 */
public class NagiosSettings {

	/**
	 * No encryption
	 */
	public static final int NO_ENCRYPTION = 0;

	/**
	 * XOR encryption
	 */
	public static final int XOR_ENCRYPTION = 1;

	/**
	 * Triple-DES encryption
	 */
	public static final int TRIPLE_DES_ENCRYPTION = 3;

	private String nagiosHost = "localhost";
	private int port = 5667;
	private String password = "password";
	private int timeout = 10000;
	private int connectTimeout = 5000;
	private int encryptionMethod = XOR_ENCRYPTION;

	/**
	 * The host or IP of the Nagios host running the NSCA add-on
	 * 
	 * @return the host or IP, defaults to localhost
	 */
	public String getNagiosHost() {
		return nagiosHost;
	}

	/**
	 * The host or IP of the Nagios host running the NSCA add-on
	 * 
	 * @param nagiosHost
	 *            the host or IP, defaults to localhost
	 */
	public void setNagiosHost(String nagiosHost) {
		if (StringUtils.isBlank(nagiosHost)) {
			throw new IllegalArgumentException(
					"nagiosHost cannot be null or empty");
		}
		this.nagiosHost = nagiosHost;
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
	 * The port on which NSCA is listening
	 * 
	 * @param port
	 *            the port, defaults to 5667
	 */
	public void setPort(int port) {
		this.port = port;
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
	 * The password configured in the ncsa.cfg file used by NSCA
	 * 
	 * @param password
	 *            the password, defaults to "password"
	 */
	public void setPassword(String password) {
		if (StringUtils.isBlank(password)) {
			throw new IllegalArgumentException(
					"password cannot be null or empty");
		}
		this.password = password;
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
	 * The socket timeout to use when sending the passive check
	 * 
	 * @param timeout
	 *            the timeout in ms, defaults to 10000 ms
	 */
	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	/**
	 * The encryption method used by the NSCA Daemon
	 * 
	 * @return the method
	 */
	public int getEncryptionMethod() {
		return encryptionMethod;
	}

	/**
	 * The encryption method used by the NSCA Daemon, currently only
	 * NO_ENCRYPTION, XOR_ENCRYPTION or TRIPLE_DES_ENCRYPTION
	 * 
	 * @param encryptionMethod
	 *            the method
	 */
	public void setEncryptionMethod(int encryptionMethod) {
		if (!EncryptionUtils.isEncryptionMethodSupported(encryptionMethod)) {
			throw new UnsupportedOperationException(
					"Currently only NO_ENCRYPTION, XOR_ENCRYPTION or TRIPLE_DES_ENCRYPTION Supported");
		}
		this.encryptionMethod = encryptionMethod;
	}
	
    /**
     * The encryption method used by the NSCA Daemon, currently only
     * NO_ENCRYPTION, XOR_ENCRYPTION or TRIPLE_DES_ENCRYPTION
     * 
     * @param encryption
     *            the {@link Encryption} method
     */
    public void setEncryptionMethod(Encryption encryption) {
        this.encryptionMethod = encryption.getCode();
    }

	/**
	 * The connection timeout
	 * 
	 * @return timeout in ms
	 */
	public int getConnectTimeout() {
		return connectTimeout;
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + connectTimeout;
		result = prime * result + encryptionMethod;
		result = prime * result
				+ ((nagiosHost == null) ? 0 : nagiosHost.hashCode());
		result = prime * result
				+ ((password == null) ? 0 : password.hashCode());
		result = prime * result + port;
		result = prime * result + timeout;
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		NagiosSettings other = (NagiosSettings) obj;
		if (connectTimeout != other.connectTimeout)
			return false;
		if (encryptionMethod != other.encryptionMethod)
			return false;
		if (nagiosHost == null) {
			if (other.nagiosHost != null)
				return false;
		} else if (!nagiosHost.equals(other.nagiosHost))
			return false;
		if (password == null) {
			if (other.password != null)
				return false;
		} else if (!password.equals(other.password))
			return false;
		if (port != other.port)
			return false;
		if (timeout != other.timeout)
			return false;
		return true;
	}
}
