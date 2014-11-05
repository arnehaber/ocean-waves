package com.github.arnehaber.android.helper;

/*
 * #%L
 * ocean-waves
 * %%
 * Copyright (C) 2014 Arne Haber
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */


import static com.github.arnehaber.android.helper.TimeConstants.*;
import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Tests for the {@link TimeConstants} helper class.
 * 
 * @author Arne Haber
 *
 */
public class TimeConstantsTest {

	@Test
	public void testTimeToStringLowerBound() {
		String expected = "45:59";
		int input = (45 * 60 * SECOND) + (59 * SECOND);
		
		String current = timeToString(input);
		assertEquals(current, expected);
	}
	
	@Test
	public void testTimeToStringExact() {
		String expected = "46:00";
		int input = (45 * 60 * SECOND) + (60 * SECOND);
		
		String current = timeToString(input);
		assertEquals(current, expected);
	}
	
	@Test
	public void testTimeToStringUpperBound() {
		String expected = "46:01";
		int input = (45 * 60 * SECOND) + (61 * SECOND);
		
		String current = timeToString(input);
		assertEquals(current, expected);
	}
	
	@Test
	public void testTimeToStringZeroSeconds() {
		String expected = "45:00";
		int input = (45 * 60 * SECOND) + (0 * SECOND);
		
		String current = timeToString(input);
		assertEquals(current, expected);
	}
	
	@Test
	public void testTimeToStringNineToZeroSeconds() {
		for (int i = 0; i < 9; i++) {
			String expected = "45:0" + i;
			int input = (45 * 60 * SECOND) + (i * SECOND);
			
			String current = timeToString(input);
			assertEquals(current, expected);			
		}
	}
	
	@Test
	public void testTimeToStringNineToZeroMinutes() {
		for (int i = 0; i < 9; i++) {
			String expected = "" + i + ":01";
			int input = (i * 60 * SECOND) + (1 * SECOND);
			
			String current = timeToString(input);
			assertEquals(current, expected);			
		}
	}
	
	@Test
	public void testTimeToStringZeroMinutes() {
		for (int i = 10; i < 59; i++) {
			String expected = "0:" + i;
			int input = (i * SECOND);
			
			String current = timeToString(input);
			assertEquals(current, expected);			
		}
	}
}
