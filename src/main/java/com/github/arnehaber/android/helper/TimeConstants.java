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


public class TimeConstants {
	
	/**
	 * A second in ms.
	 */
	public static final int SECOND = 1000;
	
	/**
	 * Default sleep time.
	 */
	public static final int DEFAULT_TIME = 45 * 60 * SECOND;
	
	/**
	 * Max sleep time.
	 */
	public static final int MAX_SLEEP_TIME = 60 * 60 * SECOND;


	/**
	 * Private constructor to prevent utility class instantiation.
	 */
	private TimeConstants() {
		
	}
	
	/**
	 * Prints the given time in format mm:ss.
	 * 
	 * @param ms time to print in ms
	 * @return the given time as a readable string.
	 */
	public static String timeToString(int ms) {
		int minutes = (ms / TimeConstants.SECOND);
		StringBuilder sb = new StringBuilder();
		sb.append((minutes / 60));
		sb.append(":");
		int mod = minutes % 60;
		if (mod < 10) {
			sb.append("0");
		}
		sb.append(mod);
		return sb.toString();
	}

}
