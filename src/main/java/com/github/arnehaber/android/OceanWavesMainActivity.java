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
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.github.arnehaber.android.helper.OceanWavesModule;
import com.github.arnehaber.android.helper.TimeConstants;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Provider;

/**
 * Main {@link Activity} of the ocean waves app.
 * 
 * @author Arne Haber
 */
public class OceanWavesMainActivity extends Activity implements IOceanWavesGui, Provider<IOceanWavesGui> {

    /**
     * Displays the current sleep time.
     */
    private TextView timeTextView;

    /**
     * Displays the play back progress.
     */
    private ProgressBar progress;

    /**
     * Controlled sound player.
     */
    private ITimedSleepPlayer player;

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

        // setup guice
        Module mod = new OceanWavesModule(this);
        Injector injector = Guice.createInjector(mod);

        // inject player
        player = injector.getInstance(ITimedSleepPlayer.class);

        // setup sleep time progress bar
        progress = (ProgressBar) findViewById(R.id.progressBar);
        progress.setMax(player.getDuration());

        // setup sleep time setter
        SeekBar sleepTimeSetter = (SeekBar) findViewById(R.id.sleepTimeSetter);
        sleepTimeSetter.setMax(TimeConstants.MAX_SLEEP_TIME);
        sleepTimeSetter.setProgress(player.getSleepTime());
        sleepTimeSetter.setOnSeekBarChangeListener(createSleepTimeSetterListener());

        // setup sleep time display
        timeTextView = (TextView) findViewById(R.id.textTime);

        // setup buttons
        final Button playButton = (Button) findViewById(R.id.buttonPlay);
        playButton.setOnClickListener(createPlayButtonListener());
        final Button stopButton = (Button) findViewById(R.id.buttonStop);
        stopButton.setOnClickListener(createStopButtonListener());
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

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                player.setSleepTime(progress);
            }
        };
        return result;
    }

    /**
     * 
     * @return stop button listener
     */
    private OnClickListener createStopButtonListener() {
        OnClickListener result = new OnClickListener() {
            public void onClick(View v) {
                player.pausePlayer();
            }
        };
        return result;
    }

    /**
     * 
     * @return start button listener
     */
    private OnClickListener createPlayButtonListener() {
        OnClickListener result = new OnClickListener() {
            public void onClick(View v) {
                player.startPlayer();
            }
        };
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.github.arnehaber.android.IOceanWavesGui#updateTime()
     */
    public void updateTime(String time) {
        timeTextView.setText(time);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.github.arnehaber.android.IOceanWavesGui#updateProgress()
     */
    public void updateProgress(int progress) {
        this.progress.setProgress(progress);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.github.arnehaber.android.IOceanWavesGui#getSelectedAudioFile()
     */
    public AssetFileDescriptor getSelectedAudioFile() {
        return getResources().openRawResourceFd(R.raw.ocean_mp3);
    }

    /**
     * @return the provided {@link IOceanWavesGui}.
     */
    public IOceanWavesGui get() {
        return this;
    }
}
