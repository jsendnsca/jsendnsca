[![Build Status](https://travis-ci.org/jsendnsca/jsendnsca.svg?branch=master)](https://travis-ci.org/jsendnsca/jsendnsca)
[![Coverage Status](https://coveralls.io/repos/github/jsendnsca/jsendnsca/badge.svg?branch=master)](https://coveralls.io/github/jsendnsca/jsendnsca?branch=master)
[![Dependency Status](https://www.versioneye.com/user/projects/56f0777535630e0029daff2f/badge.svg?style=flat)](https://www.versioneye.com/user/projects/56f0777535630e0029daff2f)
[![Maven Central](https://img.shields.io/maven-central/v/com.github.jsendnsca/jsendnsca.svg?style=flat-square)](https://maven-badges.herokuapp.com/maven-central/com.github.jsendnsca/jsendnsca/)

*This project has been migrated from the [google code project](https://code.google.com/p/jsendnsca)*

# Overview

JSend NSCA is Java API for sending passive checks to the [Nagios NSCA add-on](https://exchange.nagios.org/directory/Addons/Passive-Checks/NSCA--2D-Nagios-Service-Check-Acceptor/details).

By using JSend NSCA, you can easily integrate your Java applications into a Nagios monitored environment thereby notifying [Nagios](https://www.nagios.org)/[Icinga](https://www.icinga.org/)/[Opsview](https://www.opsview.com/) of problems and issues during the running of your application.

# Latest Release

[2.1.1](https://github.com/jsendnsca/jsendnsca/releases/tag/v2.1.1)

[Javadocs](http://jsendnsca.github.io/jsendnsca/)

## Maven

```xml
<dependency>
  <groupId>com.github.jsendnsca</groupId>
  <artifactId>jsendnsca</artifactId>
  <version>2.1.1</version>
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
      } catch (NagiosException e) {
          e.printStackTrace();
      } catch (IOException e) {
          e.printStackTrace();
      }
  }
}
```

# Background

JSend NSCA was developed as a company I worked for used Nagios to monitor applications and servers. For existing applications written in Perl and c, there are options available to send passive checks but for Java applications, the option available was to shell out and execute the send_nsca command line tool.

Although send_nsca worked in this manner, it’s ugly and we preferred having the code within our applications for better performance, testability, cleanliness...

A search on the internet revealed a few options such as the [NagiosAppender](https://sourceforge.net/projects/nagiosappender/) for log4j but in the end we settled on writing our own client. This client is currently in use thus proving the feasibility of the approach.

On the back of this, I decided to write JSend NSCA from the ground up as an exercise in TDD and thought I would make it available as an open source project so other developers can benefit from the functionality.

# Acknowledgements

Thanks goes to the [NagiosAppender](https://sourceforge.net/projects/nagiosappender/) project for details of the NSCA protocol and inspiration for this project.

# Used By

Amongst others:

* [Apache Camel Nagios Component](http://camel.apache.org/nagios.html)
* [Groundwork Open Source JDMA](https://kb.groundworkopensource.com/display/SUPPORT/Technical+Product+Description+for+JDMA)
* [jAlarms](http://jalarms.sourceforge.net/)
* [bischeck](http://gforge.ingby.com/gf/project/bischeck/)
* [Cisco Prime Network](http://www.cisco.com/c/dam/en/us/td/docs/net_mgmt/prime/network/4-2/open_source/CiscoPrimeNetwork-4-2-OpenSource.pdf)

