package com.github.arnehaber.android;

import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Handler;
import android.util.Log;

import com.github.arnehaber.android.helper.TimeConstants;
import com.google.inject.Inject;
import com.google.inject.Injector;

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
 * Responsible for playing media files in a loop. Stops play back after a
 * controllable interval.
 * 
 * @author Arne Haber
 * 
 */
public class TimedSleepPlayer implements ITimedSleepPlayer {

	private int currentTime = TimeConstants.DEFAULT_TIME;

	private final OceanWavesGui gui;
	
	private final Injector injector;

	private MediaPlayer player;

	private boolean playerIsInitialized = false;

	private int sleepTime = TimeConstants.DEFAULT_TIME;

	private Handler timerHandler;

	private Runnable timerRunnable;

	@Inject
	public TimedSleepPlayer(final Injector injector) {
		this.gui = injector.getInstance(OceanWavesGui.class);
		this.injector = injector;

		this.timerHandler = injector.getInstance(Handler.class);
		this.timerRunnable = createTimerRunnable(timerHandler);
	}

	protected Runnable createTimerRunnable(final Handler timerHandler) {
		Runnable r = new Runnable() {

			public void run() {
				timerHandler.removeCallbacks(this);
				currentTime -= TimeConstants.SECOND;
				updateTime();
				if (currentTime <= 0) {
					timerRunnable = this;
					stopPlayer();
				} else {
					timerHandler.postDelayed(this, TimeConstants.SECOND);
				}
			}
		};
		return r;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.github.arnehaber.android.ITimedSleepPlayer#getSleepTime()
	 */
	public int getSleepTime() {
		return sleepTime;
	}

	/**
	 * Initializes the used {@link MediaPlayer} with the audio file served from
	 * the {@link OceanWavesGui}.
	 */
	private void initializePlayer() {
		AssetFileDescriptor audioFile = gui.getSelectedAudioFile();
		player = injector.getInstance(MediaPlayer.class);

		try {

			player.setDataSource(audioFile.getFileDescriptor(),
					audioFile.getStartOffset(), audioFile.getLength());
			audioFile.close();
			player.prepare();
			player.setLooping(true);
			playerIsInitialized = true;
		} 
		catch (Exception e) {
			Log.e(getClass().getName(), e.getMessage(), e);
		}
	}

	private boolean isPlayerInitialized() {
		return playerIsInitialized;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.github.arnehaber.android.ITimedSleepPlayer#pausePlayer()
	 */
	public void pausePlayer() {
		if (playerIsInitialized) {
			currentTime = sleepTime;
			updateTime();
			if (player.isPlaying()) {
				timerHandler.removeCallbacks(timerRunnable);
			}
			player.pause();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.github.arnehaber.android.ITimedSleepPlayer#startPlayer()
	 */
	public void startPlayer() {
		if (!playerIsInitialized) {
			initializePlayer();
		}
		if (!player.isPlaying()) {
			Thread t = new Thread(new Runnable() {
				public void run() {
					player.start();
					timerHandler.postDelayed(timerRunnable,
							TimeConstants.SECOND);
				}
			});
			t.start();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.github.arnehaber.android.ITimedSleepPlayer#stopPlayer()
	 */
	public void stopPlayer() {
		pausePlayer();
		gui.updateProgress(0);
		playerIsInitialized = false;
		player.stop();
		player.reset();
		player.release();
	}

	private void updateTime() {
		int minutes = (currentTime / TimeConstants.SECOND);
		StringBuilder sb = new StringBuilder();
		sb.append((minutes / 60));
		sb.append(":");
		int mod = minutes % 60;
		if (mod < 10) {
			sb.append("0");
		}
		sb.append(mod);

		gui.updateTime(sb.toString());
		if (playerIsInitialized && player.isPlaying()) {
			int pos = player.getCurrentPosition();
			gui.updateProgress(pos);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.github.arnehaber.android.ITimedSleepPlayer#getDuration()
	 */
	public int getDuration() {
		if (isPlayerInitialized()) {
			return player.getDuration();
		} else {
			return 0;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.github.arnehaber.android.ITimedSleepPlayer#updateSleepTime(int)
	 */
	public void setSleepTime(int progress) {
		currentTime = progress;
		sleepTime = progress;
		updateTime();
	}

}
