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

import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.commons.lang.StringUtils;

import com.googlecode.jsendnsca.core.builders.MessagePayloadBuilder;

/**
 * The Passive Check Message Payload
 *
 * @author Raj.Patel
 * @version 1.0
 * @see MessagePayloadBuilder
 */
public class MessagePayload implements Serializable {

	private static final long serialVersionUID = 6014395299584333124L;

	private static final String DEFAULT_HOSTNAME = "localhost";
	private static final String DEFAULT_SERVICENAME = "UNDEFINED";

	private String hostname = DEFAULT_HOSTNAME;
	private Level level = Level.UNKNOWN;
	private String serviceName = DEFAULT_SERVICENAME;
	private String message = StringUtils.EMPTY;

	/**
	 * Construct a new {@link MessagePayload}
	 */
	public MessagePayload() {

	}

	/**
	 * Construct a new {@link MessagePayload}
	 *
	 * @param hostname
	 *            the hostname to be sent in this passive check
	 * @param level
	 *            the level
	 * @param serviceName
	 *            the service name
	 * @param message
	 *            the message
	 */
	public MessagePayload(String hostname, Level level, String serviceName,
			String message) {
		if (StringUtils.isBlank(hostname) || StringUtils.isBlank(serviceName)) {
			throw new IllegalArgumentException(
					"hostname or serviceName cannot be null or an empty String");
		}

		this.hostname = hostname;
		this.level = level;
		this.serviceName = serviceName;
		this.message = message;
	}

	/**
	 * The hostname to be sent in this passive check
	 *
	 * @return the hostname, defaults to "localhost"
	 */
	public String getHostname() {
		return hostname;
	}

	/**
	 * Use the short hostname of this machine in the passive check
	 *
	 * @throws UnknownHostException
	 *             thrown if unable to determine the machines hostname
	 */
	public void useLocalHostname() throws UnknownHostException {
		setHostname(false);
	}

	/**
	 * Set the hostname in the passive check
	 *
	 * @param useCanonical
	 *            true to use this machines fully qualified domain name, false
	 *            to use the short hostname
	 * @throws UnknownHostException
	 *             thrown if unable to determine the machines hostname
	 */
	public void setHostname(boolean useCanonical) throws UnknownHostException {
		InetAddress ipAddress = InetAddress.getLocalHost();
		if (useCanonical) {
			this.hostname = ipAddress.getCanonicalHostName();
		} else {
			this.hostname = ipAddress.getHostName();
		}
	}

	/**
	 * Set the hostname in the passive check
	 *
	 * @param hostname
	 *            the hostname to use
	 */
	public void setHostname(String hostname) {
		if (StringUtils.isBlank(hostname)) {
			throw new IllegalArgumentException(
					"hostname cannot be null or an empty String");
		}
		this.hostname = hostname;
	}

	/**
	 * Get the level of the Passive check
	 *
	 * @return the level
	 */
	public Level getLevel() {
		return level;
	}

	/**
	 * Set the level of the Passive check using a {@link String} The case of the
	 * {@link String} is ignored
	 *
	 * @param level
	 *            either "ok", "warning", "critical" or "unknown"
	 */
	public void setLevel(String level) {
	    this.level = Level.tolevel(level);
	}

	/**
	 * Set the level of the Passive check
	 *
	 * @param level the level
	 */
	public void setLevel(Level level) {
		this.level = level;

	}

	/**
	 * The service name of this passive check
	 *
	 * @return the service name, default is "UNDEFINED"
	 */
	public String getServiceName() {
		return serviceName;
	}

	/**
	 * Set the service name of this passive check
	 *
	 * @param serviceName
	 *            the service name
	 */
	public void setServiceName(String serviceName) {
		if (StringUtils.isBlank(serviceName)) {
			throw new IllegalArgumentException(
					"serviceName cannot be null or an empty String");
		}
		this.serviceName = serviceName;
	}

	/**
	 * The message to send in this passive check
	 *
	 * @return the message, default is an empty string
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * Set the message to send in this passive check
	 *
	 * @param message
	 *            the message
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((hostname == null) ? 0 : hostname.hashCode());
		result = prime * result + level.ordinal();
		result = prime * result + ((message == null) ? 0 : message.hashCode());
		result = prime * result
				+ ((serviceName == null) ? 0 : serviceName.hashCode());
		return result;
	}

	/* (non-Javadoc)
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
		MessagePayload other = (MessagePayload) obj;
		if (hostname == null) {
			if (other.hostname != null)
				return false;
		} else if (!hostname.equals(other.hostname))
			return false;
		if (level != other.level)
			return false;
		if (message == null) {
			if (other.message != null)
				return false;
		} else if (!message.equals(other.message))
			return false;
		if (serviceName == null) {
			if (other.serviceName != null)
				return false;
		} else if (!serviceName.equals(other.serviceName))
			return false;
		return true;
	}

    @Override
    public String toString() {
        return "MessagePayload[level=" + level + ", hostname=" + hostname + ", serviceName=" + serviceName
                + ", message=" + message + "]";
    }
}
