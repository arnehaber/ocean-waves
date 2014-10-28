package com.github.arnehaber.android;

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

/**
 * A media player that plays audio files in a loop and stops after a sleep timer expired.
 * 
 * @author Arne Haber
 *
 */
public interface ITimedSleepPlayer {

	/**
	 * 
	 * @return the current sleep time.
	 */
	int getSleepTime();

	/**
	 * Pauses the play back. Keeps the current play back position and resets the
	 * sleep timer.
	 */
	void pausePlayer();
	
	/**
	 * Starts play back at the current position.
	 */
	void startPlayer();

	/**
	 * Stops the player and releases internal resources.
	 */
	void stopPlayer();

	/**
	 * 
	 * @return the duration of the currently played track.
	 */
	int getDuration();

	/**
	 * Sets the sleep timer with the given <b>progress</b>.
	 * 
	 * @param progress new sleep time in ms.
	 */
	void setSleepTime(int progress);

}