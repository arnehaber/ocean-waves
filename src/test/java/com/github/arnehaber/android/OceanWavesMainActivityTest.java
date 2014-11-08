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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.lang.reflect.Field;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import android.content.res.AssetFileDescriptor;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.github.arnehaber.android.helper.OceanWavesModule;
import com.github.arnehaber.android.helper.TimeConstants;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

/**
 * Tests for {@link OceanWavesMainActivity}.
 * 
 * @author Arne Haber
 * 
 */
@RunWith(RobolectricTestRunner.class)
public class OceanWavesMainActivityTest {

    private OceanWavesMainActivity testee;

    private ITimedSleepPlayer mockedPlayer;

    private ITimedSleepPlayer player;

    @Before
    public void setUp() {
        testee = Robolectric.buildActivity(OceanWavesMainActivity.class).create().get();
        // setup guice
        Module mod = new OceanWavesModule(testee);
        Injector injector = Guice.createInjector(mod);

        // inject player
        player = injector.getInstance(ITimedSleepPlayer.class);
    }

    @Test
    public void testOnCreate() {

        ProgressBar progress = (ProgressBar) testee.findViewById(R.id.progressBar);
        SeekBar sleepTimeSetter = (SeekBar) testee.findViewById(R.id.sleepTimeSetter);

        assertEquals(player.getDuration(), progress.getMax());
        assertEquals(TimeConstants.MAX_SLEEP_TIME, sleepTimeSetter.getMax());
        assertEquals(player.getSleepTime(), sleepTimeSetter.getProgress());
    }

    @Test
    public void testPressPlayButton() {
        replacePlayerWithMock();
        final Button playButton = (Button) testee.findViewById(R.id.buttonPlay);
        playButton.performClick();
        verify(mockedPlayer, times(1)).startPlayer();
    }

    @Test
    public void testPressStopButton() {
        replacePlayerWithMock();
        final Button stopButton = (Button) testee.findViewById(R.id.buttonStop);
        stopButton.performClick();
        verify(mockedPlayer, times(1)).pausePlayer();
    }

    @Test
    public void testAdjustSleepTime() {
        replacePlayerWithMock();
        SeekBar sleepTimeSetter = (SeekBar) testee.findViewById(R.id.sleepTimeSetter);
        int progress = 25;
        sleepTimeSetter.setProgress(progress);
        verify(mockedPlayer, times(1)).setSleepTime(progress);
    }

    @Test
    public void testUpdateTime() {
        TextView timeTextView = (TextView) testee.findViewById(R.id.textTime);

        String expected = "47:00";
        testee.updateTime(expected);

        assertEquals(expected, timeTextView.getText());
    }

    @Test
    public void testUpdateProgress() {
        int[] testValues = { 0, 1, 2, 100 };
        ProgressBar progress = (ProgressBar) testee.findViewById(R.id.progressBar);
        progress.setMax(1000);
        for (int expected : testValues) {
            testee.updateProgress(expected);
            int actual = progress.getProgress();
            assertEquals(expected, actual);
        }
    }

    @Test
    public void testUpdateProgressRespectMax() {

        ProgressBar progress = (ProgressBar) testee.findViewById(R.id.progressBar);
        int max = 1000;

        int expected = max;

        progress.setMax(max);
        testee.updateProgress(1001);
        int actual = progress.getProgress();
        assertEquals(expected, actual);

    }

    @Test
    public void testGetSelectedAudioFile() {
        AssetFileDescriptor expected = testee.getResources().openRawResourceFd(R.raw.ocean_mp3);
        AssetFileDescriptor actual = testee.getSelectedAudioFile();
        assertEquals(expected, actual);
    }

    @Test
    public void testProvider() {
        IOceanWavesGui expected = testee;
        IOceanWavesGui actual = testee.get();
        assertEquals(expected, actual);
    }

    private void replacePlayerWithMock() {
        try {
            Field player = testee.getClass().getDeclaredField("player");
            player.setAccessible(true);
            mockedPlayer = mock(ITimedSleepPlayer.class);
            player.set(testee, mockedPlayer);
        }
        catch (Exception e) {
            fail(e.getMessage());
        }
    }
}
