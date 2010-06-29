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
package com.googlecode.jsendnsca.quickstart;

import java.io.IOException;

import com.googlecode.jsendnsca.Level;
import com.googlecode.jsendnsca.MessagePayload;
import com.googlecode.jsendnsca.NagiosException;
import com.googlecode.jsendnsca.NagiosPassiveCheckSender;
import com.googlecode.jsendnsca.NagiosSettings;
import com.googlecode.jsendnsca.builders.MessagePayloadBuilder;
import com.googlecode.jsendnsca.builders.NagiosSettingsBuilder;
import com.googlecode.jsendnsca.encryption.Encryption;

public class QuickStart {

    public static void main(String[] args) {
        NagiosSettings settings = new NagiosSettingsBuilder()
            .withNagiosHost("nagiosHostNameOrIPAddress")
            .withPort(5667) // you don't really need to set this as 5667 is default
            .withEncryption(Encryption.XOR)
            .create();
        
        MessagePayload payload = new MessagePayloadBuilder()
            // you can use .withLocalHostname() or withCanonicalHostname to determine
            // your short and fully qualified domain name respectively for you instead of .withHostname
            .withHostname("hostname of machine sending check")
            .withLevel(Level.OK)
            .withServiceName("Service Name")
            .withMessage("should work if everything set up OK")
            .create();
        
        NagiosPassiveCheckSender sender = new NagiosPassiveCheckSender(settings);
        
        try {
            sender.send(payload);
        } catch (NagiosException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
