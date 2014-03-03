/*******************************************************************************
 * Copyright (c) 2009-2014 Black Rook Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 *  
 * Contributors:
 *     Matt Tropiano - initial API and implementation
 ******************************************************************************/
package com.blackrook.oal.util.stage;

import java.io.IOException;

import javax.sound.sampled.UnsupportedAudioFileException;

import com.blackrook.oal.OALBuffer;
import com.blackrook.oal.OALSource;
import com.blackrook.oal.util.OALSoundResource;

/**
 * Sound environment adapter.
 * Implements sound environment listener methods that do NOTHING.
 * @author Matthew Tropiano
 */
public class OALSoundStageAdapter implements OALSoundStageListener
{
	@Override
	public void soundCached(OALSoundResource data, OALBuffer buffer)
	{
	}

	@Override
	public void soundPlayed(OALSoundResource data)
	{
	}

	@Override
	public void soundStopped(OALSoundResource data)
	{
	}

	@Override
	public void soundReleased(OALSoundResource data, OALBuffer buffer)
	{
	}

	@Override
	public void soundStreamStarted(OALSoundResource data)
	{
	}
	
	@Override
	public void soundStreamStopped(OALSoundResource data)
	{
	}

	@Override
	public void sourcePaused(OALSource source)
	{
	}

	@Override
	public void sourcePlayed(OALSource source)
	{
	}

	@Override
	public void sourceRewound(OALSource source)
	{
	}

	@Override
	public void sourceStopped(OALSource source)
	{
	}

	@Override
	public void errorIO(OALSoundResource data, IOException exception)
	{
	}

	@Override
	public void errorUnsupportedResource(OALSoundResource data, UnsupportedAudioFileException exception)
	{
		
	}

	@Override
	public void sourceBufferEnqueued(OALSource source, OALBuffer buffer)
	{
	}

	@Override
	public void sourceBufferDequeued(OALSource source, OALBuffer buffer)
	{
	}
	
}
