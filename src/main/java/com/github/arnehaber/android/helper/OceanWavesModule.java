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


import com.github.arnehaber.android.ITimedSleepPlayer;
import com.github.arnehaber.android.IOceanWavesGui;
import com.github.arnehaber.android.TimedSleepPlayer;
import com.google.inject.AbstractModule;
import com.google.inject.Provider;

/**
 * Default guice module.
 * 
 * @author Arne Haber
 *
 */
public class OceanWavesModule extends AbstractModule {

	/**
	 * The used {@link IOceanWavesGui} provider.
	 */
	private final Provider<IOceanWavesGui> provider;
	
	/**
	 * 
	 * @param provider the {@link IOceanWavesGui} provider to use.
	 */
	public OceanWavesModule(Provider<IOceanWavesGui> provider) {
		this.provider = provider;
	}
	
	@Override
	protected void configure() {
		bind(ITimedSleepPlayer.class).to(TimedSleepPlayer.class);
		
		bind(IOceanWavesGui.class).toProvider(provider);
	}

}
