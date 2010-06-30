/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.googlecode.jsendnsca.utils;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Utility IO methods
 *
 * @author Raj.Patel
 * @since 1.1.1
 */
public class IOUtils {

	private IOUtils() {
		// not to be constructed
	}

	/**
	 * Unconditionally close an <code>InputStream</code>.
	 * <p>
	 * Equivalent to {@link InputStream#close()}, except any exceptions will be
	 * ignored. This is typically used in finally blocks.
	 *
	 * @param input
	 *            the InputStream to close, may be null or already closed
	 */
	public static void closeQuietly(InputStream input) {
		close(input);
	}

	/**
	 * Unconditionally close an <code>OutputStream</code>.
	 * <p>
	 * Equivalent to {@link OutputStream#close()}, except any exceptions will be
	 * ignored. This is typically used in finally blocks.
	 *
	 * @param output
	 *            the OutputStream to close, may be null or already closed
	 */
	public static void closeQuietly(OutputStream output) {
		close(output);
	}

	/**
	 * Unconditionally close a <code>Socket</code>.
	 * <p>
	 * Equivalent to {@link Socket#close()}, except any exceptions will be
	 * ignored. This is typically used in finally blocks.
	 *
	 * @param socket
	 *            the socket to close, may be null or already closed
	 */
	public static void closeQuietly(Socket socket) {
		try {
			if (socket != null) {
				socket.close();
			}
		} catch (IOException ignore) {
		}
	}

	public static void closeQuietly(ServerSocket serverSocket) {
		try {
			if (serverSocket != null) {
				serverSocket.close();
			}
		} catch (IOException ignore) {
		}
	}
	
	private static void close(Closeable closeable) {
	    try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (IOException ignore) {
        }
	}
}
