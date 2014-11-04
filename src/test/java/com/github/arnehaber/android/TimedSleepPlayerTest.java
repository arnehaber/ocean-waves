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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;

import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Handler;

import com.github.arnehaber.android.helper.TimeConstants;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

@RunWith(RobolectricTestRunner.class)
public class TimedSleepPlayerTest {

	private IOceanWavesGui mockedGui = Mockito.mock(IOceanWavesGui.class);
	private MediaPlayer mockedPlayer = Mockito.mock(MediaPlayer.class);
	private Handler mockedHandler = Mockito.mock(Handler.class);

	private ITimedSleepPlayer testee;

	@Before
	public void setUp() {
		final AbstractModule testModule = new AbstractModule() {
			@Override
			protected void configure() {
				bind(IOceanWavesGui.class).toInstance(mockedGui);
				bind(MediaPlayer.class).toInstance(mockedPlayer);
				bind(Handler.class).toInstance(mockedHandler);
				bind(ITimedSleepPlayer.class).to(TimedSleepPlayer.class);
			}
		};

		Injector i = Guice.createInjector(testModule);
		testee = i.getInstance(TimedSleepPlayer.class);
	}

	@Test
	public void testGetSleepTime() {
		int expected = TimeConstants.DEFAULT_TIME;
		int actual = testee.getSleepTime();
		assertEquals(expected, actual);
	}

	@Test
	public void setSleepTimeUninitialized() {
		assertFalse(isInitialized());
		int pos = 123;
		int expected = 2003;
		testee.setSleepTime(expected);
		verify(mockedGui).updateTime("0:02");
		when(mockedPlayer.getCurrentPosition()).thenReturn(pos);
		verify(mockedPlayer, never()).isPlaying();
		verify(mockedPlayer, never()).getCurrentPosition();
		verify(mockedGui, never()).updateProgress(pos);

		assertEquals(expected, testee.getSleepTime());

	}

	@Test
	public void setSleepTimeInitialized() {
		int pos = 123;
		int expected = 2003;
		when(mockedPlayer.isPlaying()).thenReturn(true);
		when(mockedPlayer.getCurrentPosition()).thenReturn(pos);
		testInitializeTestee();
		testee.setSleepTime(expected);

		verify(mockedGui).updateTime(TimeConstants.timeToString(expected));

		verify(mockedGui, times(1)).updateProgress(pos);

		assertEquals(expected, testee.getSleepTime());
	}

	@Test
	public void testInitializeTestee() {
		try {
			when(mockedGui.getSelectedAudioFile()).thenReturn(
					mock(AssetFileDescriptor.class));
			Method initializePlayer = testee.getClass().getDeclaredMethod(
					"initializePlayer");
			initializePlayer.setAccessible(true);
			initializePlayer.invoke(testee);
			verify(mockedGui, times(1)).getSelectedAudioFile();
			verify(mockedPlayer, times(1)).setDataSource(null, 0, 0);
			verify(mockedPlayer, times(1)).prepare();
			verify(mockedPlayer, times(1)).setLooping(true);
			assertTrue(isInitialized());
		} 
		catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testPausePlayerUninitialized() {
		testee.pausePlayer();
		verify(mockedGui, never()).updateTime(null);
		verify(mockedPlayer, never()).isPlaying();
		verify(mockedPlayer, never()).pause();
	}

	@Test
	public void testPausePlayerInitializedPlaying() {
		int pos = 123;
		when(mockedPlayer.getCurrentPosition()).thenReturn(pos);
		when(mockedPlayer.isPlaying()).thenReturn(true);
		ArgumentCaptor<Runnable> argument = ArgumentCaptor
				.forClass(Runnable.class);
		testInitializeTestee();

		testee.pausePlayer();

		verify(mockedGui, times(1)).updateTime(
				TimeConstants.timeToString(TimeConstants.DEFAULT_TIME));
		verify(mockedPlayer, times(2)).isPlaying();
		verify(mockedGui, times(1)).updateProgress(pos);
		verify(mockedHandler, times(1)).removeCallbacks(argument.capture());
		verify(mockedPlayer, times(1)).pause();
	}
	
	@Test
	public void testPausePlayerInitializedNotPlaying() {
		int pos = 123;
		when(mockedPlayer.getCurrentPosition()).thenReturn(pos);
		when(mockedPlayer.isPlaying()).thenReturn(false);
		ArgumentCaptor<Runnable> argument = ArgumentCaptor
				.forClass(Runnable.class);
		testInitializeTestee();

		testee.pausePlayer();

		verify(mockedGui, times(1)).updateTime(
				TimeConstants.timeToString(TimeConstants.DEFAULT_TIME));
		verify(mockedPlayer, times(2)).isPlaying();
		verify(mockedGui, never()).updateProgress(pos);
		verify(mockedHandler, never()).removeCallbacks(argument.capture());
		verify(mockedPlayer, times(1)).pause();
	}

	@Test
	public void testStartPlayerUninitialized() {
		assertFalse(isInitialized());
		when(mockedPlayer.isPlaying()).thenReturn(false);
		when(mockedGui.getSelectedAudioFile()).thenReturn(
				mock(AssetFileDescriptor.class));
		ArgumentCaptor<Runnable> runnable = ArgumentCaptor
				.forClass(Runnable.class);
		ArgumentCaptor<Integer> delay = ArgumentCaptor.forClass(Integer.class);
		testee.startPlayer();
		// we have to wait a bit to let the started Thread finish
		try {
			Thread.sleep(100);
		} 
		catch (InterruptedException e) {
			e.printStackTrace();
		}

		// check, if the player is initialized
		assertTrue(isInitialized());
		verify(mockedPlayer, times(1)).setLooping(true);

		verify(mockedPlayer, times(1)).isPlaying();
		verify(mockedPlayer, times(1)).start();
		verify(mockedHandler, times(1)).postDelayed(runnable.capture(),
				delay.capture());
	}

	@Test
	public void testStartPlayerInitializedNotPlaying() {
		testInitializeTestee();
		assertTrue(isInitialized());
		// reset player mock to remove initialized calls from
		// testInitializeTestee.
		Mockito.reset(mockedPlayer);
		when(mockedPlayer.isPlaying()).thenReturn(false);

		ArgumentCaptor<Runnable> runnable = ArgumentCaptor
				.forClass(Runnable.class);
		ArgumentCaptor<Integer> delay = ArgumentCaptor.forClass(Integer.class);
		testee.startPlayer();
		// we have to wait a bit to let the started Thread finish
		try {
			Thread.sleep(100);
		} 
		catch (InterruptedException e) {
			e.printStackTrace();
		}
		// check, that init has not been called
		verify(mockedPlayer, never()).setLooping(true);

		verify(mockedPlayer, times(1)).isPlaying();
		verify(mockedPlayer, times(1)).start();
		verify(mockedHandler, times(1)).postDelayed(runnable.capture(),
				delay.capture());
	}
	
	@Test
	public void testStartPlayerInitializedIsPlaying() {
		testInitializeTestee();
		assertTrue(isInitialized());
		// reset player mock to remove initialized calls from
		// testInitializeTestee.
		Mockito.reset(mockedPlayer);
		when(mockedPlayer.isPlaying()).thenReturn(true);

		testee.startPlayer();
		// we have to wait a bit to let the started Thread finish
		try {
			Thread.sleep(100);
		} 
		catch (InterruptedException e) {
			e.printStackTrace();
		}
		// check, that init has not been called
		verify(mockedPlayer, never()).setLooping(true);
		
		verify(mockedPlayer, never()).start();
	}

	@Test
	public void testStopPlayerUninitialized() {
		assertFalse(isInitialized());
		testee.stopPlayer();
		
		verify(mockedGui, never()).updateProgress(0);
		verify(mockedPlayer, never()).stop();
		verify(mockedPlayer, never()).reset();
		verify(mockedPlayer, never()).release();
	}
	
	@Test
	public void testStopPlayerInitialized() {
		testInitializeTestee();
		testee.stopPlayer();
		
		verify(mockedGui, times(1)).updateProgress(0);
		verify(mockedPlayer, times(1)).stop();
		verify(mockedPlayer, times(1)).reset();
		verify(mockedPlayer, times(1)).release();
	}

	@Test
	public void testGetDurationUninitialized() {
		int expected = 0;
		int actual = testee.getDuration();
		assertEquals(expected, actual);
	}
	
	@Test
	public void testGetDurationInitialized() {
		testInitializeTestee();
		
		int expected = 200;
		when(mockedPlayer.getDuration()).thenReturn(expected);
		int actual = testee.getDuration();
		assertEquals(expected, actual);
	}

	private boolean isInitialized() {
		try {
			Field playerIsInitialized = testee.getClass().getDeclaredField(
					"playerIsInitialized");
			playerIsInitialized.setAccessible(true);
			boolean result = playerIsInitialized.getBoolean(testee);
			return result;
		} 
		catch (Exception e) {
			fail(e.getMessage());
		}
		return false;
	}

}
