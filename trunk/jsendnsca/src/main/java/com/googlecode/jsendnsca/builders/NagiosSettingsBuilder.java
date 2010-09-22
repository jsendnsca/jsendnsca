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

import static org.apache.commons.lang.StringUtils.*;

import com.googlecode.jsendnsca.NagiosSettings;
import com.googlecode.jsendnsca.encryption.Encryption;
import com.googlecode.jsendnsca.encryption.Encryptor;

/**
 * Used to construct a {@link NagiosSettings} instance using a builder pattern
 * e.g.
 *
 * <pre>
 *
 * NagiosSettings nagiosSettings = new NagiosSettingsBuilder()
 *      .withNagiosHost(HOSTNAME)
 *      .withPassword(PASSWORD)
 *      .create();
 * </pre>
 *
 * @author Raj.Patel
 * @since 1.2
 */
public class NagiosSettingsBuilder {

    private final NagiosSettings nagiosSettings = new NagiosSettings();

    /**
     * Return an instance of {@link NagiosSettings} with default values
     *
     * @return default instance
     */
    public NagiosSettings createDefault() {
        return new NagiosSettings();
    }

    /**
     * Return the built instance of {@link NagiosSettings}
     *
     * @return the built instance
     */
    public NagiosSettings create() {
        return nagiosSettings;
    }

    /**
     * The next {@link NagiosSettings} created will use the supplied nagiosHost
     *
     * @param nagiosHost
     *            the NSCA hostname or IP address
     * @return the {@link NagiosSettingsBuilder} instance
     */
    public NagiosSettingsBuilder withNagiosHost(String nagiosHost) {
        nagiosSettings.setNagiosHost(nagiosHost);
        return this;
    }

    /**
     * The next {@link NagiosSettings} created will use the supplied port
     *
     * @param port
     *            the port NSCA is listening on
     * @return the {@link NagiosSettingsBuilder} instance
     */
    public NagiosSettingsBuilder withPort(int port) {
        nagiosSettings.setPort(port);
        return this;
    }

    /**
     * The next {@link NagiosSettings} created will have an empty string set as
     * the password
     *
     * @return the {@link NagiosSettingsBuilder} instance
     */
    public NagiosSettingsBuilder withNoPassword() {
        nagiosSettings.setPassword(EMPTY);
        return this;
    }

    /**
     * The next {@link NagiosSettings} created will use the supplied password
     *
     * @param password
     *            the NSCA password
     * @return the {@link NagiosSettingsBuilder} instance
     */
    public NagiosSettingsBuilder withPassword(String password) {
        nagiosSettings.setPassword(password);
        return this;
    }

    /**
     * The next {@link NagiosSettings} created will use the supplied connection
     * timeout
     *
     * @param connectionTimeout
     *            the connection timeout
     * @return the {@link NagiosSettingsBuilder} instance
     */
    public NagiosSettingsBuilder withConnectionTimeout(int connectionTimeout) {
        nagiosSettings.setConnectTimeout(connectionTimeout);
        return this;
    }

    /**
     * The next {@link NagiosSettings} created will use the supplied response
     * timeout
     *
     * @param responseTimeout
     *            the NSCA response timeout
     * @return the {@link NagiosSettingsBuilder} instance
     */
    public NagiosSettingsBuilder withResponseTimeout(int responseTimeout) {
        nagiosSettings.setTimeout(responseTimeout);
        return this;
    }

    /**
     * The next {@link NagiosSettings} created will use the specified
     * {@link Encryptor}
     *
     * @param encryptor
     *            the encryptor to use
     * @return the {@link NagiosSettingsBuilder} instance
     */
    public NagiosSettingsBuilder withEncryptor(Encryptor encryptor) {
        nagiosSettings.setEncryptor(encryptor);
        return this;
    }

    /**
     * The next {@link NagiosSettings} created will use the specified
     * {@link Encryption} constant
     *
     * @param encryption
     *            the {@link Encryption} to use
     * @return the {@link NagiosSettingsBuilder} instance
     */
    public NagiosSettingsBuilder withEncryption(Encryption encryption) {
        nagiosSettings.setEncryption(encryption);
        return this;
    }

}
