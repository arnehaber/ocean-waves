package com.github.arnehaber.android.helper;

import static com.github.arnehaber.android.helper.TimeConstants.*;
import static org.junit.Assert.*;

import org.junit.Test;
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
