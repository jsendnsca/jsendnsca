![Maven Build](https://github.com/jsendnsca/jsendnsca/actions/workflows/maven.yml/badge.svg)
[![Coverage](.github/badges/jacoco.svg)](https://github.com/jsendnsca/jsendnsca/actions/workflows/maven.yml)
[![Maven Central](https://img.shields.io/maven-central/v/com.github.jsendnsca/jsendnsca.svg?style=flat-square)](https://maven-badges.herokuapp.com/maven-central/com.github.jsendnsca/jsendnsca/)

*This project has been migrated from the [google code project](https://code.google.com/p/jsendnsca)*

# Overview

JSend NSCA is Java API for sending passive checks to the [Nagios NSCA add-on](https://exchange.nagios.org/directory/Addons/Passive-Checks/NSCA--2D-Nagios-Service-Check-Acceptor/details).

By using JSend NSCA, you can easily integrate your Java applications into a Nagios monitored environment thereby notifying [Nagios](https://www.nagios.org)/[Icinga](https://www.icinga.org/)/[Opsview](https://www.opsview.com/) of problems and issues during the running of your application.

# Versions

For Java 11+, use [2.2.0+](https://search.maven.org/artifact/com.github.jsendnsca/jsendnsca/2.2.0/jar)

For Java 6+, use [2.1.1](https://search.maven.org/artifact/com.github.jsendnsca/jsendnsca/2.1.1/jar)

## Maven

```xml
<dependency>
  <groupId>com.github.jsendnsca</groupId>
  <artifactId>jsendnsca</artifactId>
  <version>2.2.0</version>
</dependency>
```

# Quickstart

```java
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
                .withPort(5667)
                .withEncryption(Encryption.XOR)
                .create();

        MessagePayload payload = new MessagePayloadBuilder()
                .withHostname("hostname of machine sending check")
                .withLevel(Level.OK)
                .withServiceName("Service Name")
                .withMessage("should work if everything set up OK")
                .create();

        NagiosPassiveCheckSender sender = new NagiosPassiveCheckSender(settings);

        try {
            sender.send(payload);
        } catch (NagiosException | IOException e) {
            e.printStackTrace();
        }
    }
}
```

# Background

JSend NSCA was developed as a company I worked for at the time used Nagios to monitor applications and servers. For existing applications written in Perl and c, there are options available to send passive checks but for Java applications, the option available was to shell out and execute the send_nsca command line tool.

Although send_nsca worked in this manner, it was ugly and we preferred having the code within our applications for better performance, testability, cleanliness...

A search on the internet revealed a few options such as the [NagiosAppender](https://sourceforge.net/projects/nagiosappender/) for log4j but in the end we settled on writing our own client.

On the back of this, I decided to write JSend NSCA from the ground up as an exercise in TDD and thought I would make it available as an open source project so other developers can benefit from the functionality.

# Acknowledgements

Thanks goes to the [NagiosAppender](https://sourceforge.net/projects/nagiosappender/) project for details of the NSCA protocol and inspiration for this project.

# Used By

Amongst others:

* [Apache Camel Nagios Component](https://camel.apache.org/components/3.14.x/nagios-component.htmll)
* [Groundwork Open Source JDMA](https://kb.groundworkopensource.com/display/DOC71/Nagios)
* [jAlarms](http://jalarms.sourceforge.net/)
* [bischeck](http://gforge.ingby.com/gf/project/bischeck/)
* [Cisco Prime Network](http://www.cisco.com/c/dam/en/us/td/docs/net_mgmt/prime/network/4-2/open_source/CiscoPrimeNetwork-4-2-OpenSource.pdf)
