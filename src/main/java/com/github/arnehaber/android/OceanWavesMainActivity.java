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


import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

/**
 * Main {@link Activity} of the ocean waves app.
 * 
 * @author Arne Haber
 */
public class OceanWavesMainActivity extends Activity {

	private static final int SECOND = 1000;

	private static final int DEFAULT_TIME = 45 * 60 * SECOND;
	
	private static final int MAX_SLEEP_TIME = 60 * 60 * SECOND;

	private int currentTime = DEFAULT_TIME;

	private int sleepTime = DEFAULT_TIME;

	private MediaPlayer player;

	private TextView timeTextView;

	private ProgressBar progress;

	private Handler timerHandler;
	
	private boolean playerIsInitialized = false;

	/**
	 * Called when the activity is first created.
	 * 
	 * @param savedInstanceState
	 *            If the activity is being re-initialized after previously being
	 *            shut down then this Bundle contains the data it most recently
	 *            supplied in onSaveInstanceState(Bundle). <b>Note: Otherwise it
	 *            is null.</b>
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initializePlayer();

		progress = (ProgressBar) findViewById(R.id.progressBar);
		progress.setMax(player.getDuration());

		// setup sleep time setter
		SeekBar sleepTimeSetter = (SeekBar) findViewById(R.id.sleepTimeSetter);
		// set max to 4 hours
		sleepTimeSetter.setMax(MAX_SLEEP_TIME);
		sleepTimeSetter.setProgress(sleepTime);

		sleepTimeSetter
				.setOnSeekBarChangeListener(createSleepTimeSetterListener());

		timeTextView = (TextView) findViewById(R.id.textTime);
		updateTime();

		timerHandler = new Handler();
		final Runnable timerRunnable = createTimerRunnable(timerHandler);

		final Button buttonPlay = (Button) findViewById(R.id.buttonPlay);

		buttonPlay.setOnClickListener(createPlayButtonListener(timerRunnable));

		final Button button = (Button) findViewById(R.id.buttonStop);
		button.setOnClickListener(createStopButtonListener(timerRunnable));

	}

	/**
	 * 
	 * @return a change listener that sets currentTime and sleepTime when
	 *         progress is detected.
	 */
	private OnSeekBarChangeListener createSleepTimeSetterListener() {
		OnSeekBarChangeListener result = new OnSeekBarChangeListener() {
			public void onStopTrackingTouch(SeekBar seekBar) {
			}

			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				if (fromUser) {
					currentTime = progress;
					sleepTime = progress;
					updateTime();
				}
			}
		};
		return result;
	}

	/**
	 * Provides stop button functionality.
	 * 
	 * @param timerRunnable
	 *            timer runnable
	 * @return
	 */
	private OnClickListener createStopButtonListener(
			final Runnable timerRunnable) {
		OnClickListener result = new OnClickListener() {
			public void onClick(View v) {
				pausePlayer(timerRunnable);
			}
		};
		return result;
	}

	private OnClickListener createPlayButtonListener(
			final Runnable timerRunnable) {
		OnClickListener result = new OnClickListener() {
			public void onClick(View v) {
				if (!playerIsInitialized) {
					initializePlayer();
				}
				if (!player.isPlaying()) {
					timerHandler.postDelayed(timerRunnable, SECOND);
					Thread t = new Thread(new Runnable() {
						public void run() {
							player.start();
							timerHandler.postDelayed(timerRunnable, SECOND);
						}
					});
					t.start();
				}
			}
		};
		return result;
	}

	private Runnable createTimerRunnable(final Handler timerHandler) {
		Runnable r = new Runnable() {

			public void run() {
				timerHandler.removeCallbacks(this);
				currentTime -= SECOND;
				updateTime();
				if (currentTime <= 0) {
					stopPlayer(this);
				} 
				else {
					timerHandler.postDelayed(this, SECOND);
				}
			}
		};
		return r;
	}
	private void initializePlayer() {
		AssetFileDescriptor afd = getResources().openRawResourceFd(R.raw.ocean_mp3);

        player = new MediaPlayer();
        
        try {
			player.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
			afd.close();
			player.prepare();
			player.setLooping(true);
			playerIsInitialized = true;
		} 
        catch (Exception e) {
			// todo log
		} 
	}
	
	private void stopPlayer(Runnable timerRunnable) {
		pausePlayer(timerRunnable);
		playerIsInitialized = false;
		player.stop();
		player.reset();
		player.release();
	}
	
	private void pausePlayer(Runnable timerRunnable) {
		if (playerIsInitialized) {
			currentTime = sleepTime;
			updateTime();
			if (player.isPlaying()) {
				timerHandler.removeCallbacks(timerRunnable);
			}
			player.pause();			
		}
	}

	private void updateTime() {
		int minutes = (currentTime / SECOND);
		StringBuilder sb = new StringBuilder();
		sb.append((minutes / 60));
		sb.append(":");
		int mod = minutes % 60;
		if (mod < 10) {
			sb.append("0");
		}
		sb.append(mod);

		timeTextView.setText(sb.toString());
		if (player.isPlaying()) {
			progress.setProgress(player.getCurrentPosition());			
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(com.github.arnehaber.android.R.menu.main,
				menu);
		return true;
	}
}
