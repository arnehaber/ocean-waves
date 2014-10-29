package com.github.arnehaber.android;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Handler;

import com.github.arnehaber.android.helper.TimeConstants;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

public class TimedSleepPlayerTest {

	
	private OceanWavesGui mockedGui = Mockito.mock(OceanWavesGui.class);
	private MediaPlayer mockedPlayer = Mockito.mock(MediaPlayer.class);
	private Handler mockedHandler = Mockito.mock(Handler.class);
	
	private ITimedSleepPlayer testee;
	
	@Before
	public void setUp() {
		final AbstractModule testModule  = new AbstractModule() {
			@Override
			protected void configure() {
				bind(OceanWavesGui.class).toInstance(mockedGui);
				bind(MediaPlayer.class).toInstance(mockedPlayer);
				bind(Handler.class).toInstance(mockedHandler);
				bind(ITimedSleepPlayer.class).to(TimedSleepPlayer.class);
			}
		};
		
		Injector i = Guice.createInjector(testModule);
		testee = i.getInstance(TimedSleepPlayer.class);
	}
	
	
	
	@Test
	public void getSleepTime() {
		int expected = TimeConstants.DEFAULT_TIME;
		int actual  = testee.getSleepTime();
		assertEquals(expected, actual);
	}
	
	@Test
	public void setSleepTimeUninitialized() {
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
		
		verify(mockedGui).updateTime("0:02");
		
		verify(mockedGui, times(1)).updateProgress(pos);
		
		assertEquals(expected, testee.getSleepTime());
	}

	@Test
	public void testInitializeTestee() {
		try {
			when(mockedGui.getSelectedAudioFile()).thenReturn(mock(AssetFileDescriptor.class));
			Method initializePlayer = testee.getClass().getDeclaredMethod("initializePlayer");
			initializePlayer.setAccessible(true);
			initializePlayer.invoke(testee);
			verify(mockedGui, times(1)).getSelectedAudioFile();
			verify(mockedPlayer, times(1)).setDataSource(null, 0, 0);
			verify(mockedPlayer, times(1)).prepare();
			verify(mockedPlayer, times(1)).setLooping(true);
			
		} 
		catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}



	public void testPausePlayer() {
		
	}

	public void testStartPlayer() {
		
	}

	public void testStopPlayer() {
		
	}

	public void testGetDuration() {
	}

}