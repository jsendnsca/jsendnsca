package com.googlecode.jsendnsca.core.builders;

import java.net.UnknownHostException;

import com.googlecode.jsendnsca.core.Level;
import com.googlecode.jsendnsca.core.MessagePayload;

/**
 * Used to construct a {@link MessagePayload} using a builder pattern e.g.
 *
 * <pre>
 * MessagePayload payload = new MessagePayloadBuilder()
 *			.withHostname("localhost")
 *			.withLevel(Level.CRITICAL)
 *			.withServiceName("Test Service Name")
 *			.withMessage("Test Message")
 *			.create();
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
	public MessagePayloadBuilder withLocalHostname()
			throws UnknownHostException {
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
	public MessagePayloadBuilder withCanonicalHostname()
			throws UnknownHostException {
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
	 * @param level the level value
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
	 * Set the message of the passive check
	 *
	 * @param message
	 *            the message
	 * @return the {@link MessagePayloadBuilder}
	 */
	public MessagePayloadBuilder withMessage(String message) {
		payload.setMessage(message);
		return this;
	}
}
