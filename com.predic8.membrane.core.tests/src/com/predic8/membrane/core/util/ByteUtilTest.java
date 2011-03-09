/* Copyright 2009 predic8 GmbH, www.predic8.com

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License. */


package com.predic8.membrane.core.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import junit.framework.TestCase;

import com.predic8.membrane.core.Constants;
import com.predic8.membrane.core.util.ByteUtil;

public class ByteUtilTest extends TestCase {

	private static final String message1 = "This is a test message";
	
	private static final String message2 = "This is a test message with carriage return and linefeed " + Constants.CRLF;
		
	private InputStream in1, in2;
	
	@Override
	protected void setUp() throws Exception {
		in1 = new ByteArrayInputStream(message1.getBytes());
		in2 = new ByteArrayInputStream(message2.getBytes());
	}
	
	@Override
	protected void tearDown() throws Exception {
		in1.close();
		in2.close();
	}
	
	public void testReadByteArray1() throws IOException {
		byte[] readBytes = ByteUtil.readByteArray(in1, message1.length());
		assertTrue(Arrays.equals(readBytes, message1.getBytes()));
	}
	
	public void testReadByteArray2() throws IOException {
		byte[] readBytes = ByteUtil.readByteArray(in2, message2.length());
		assertTrue(Arrays.equals(readBytes, message2.getBytes()));
	}

}
