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
 * Sound environment listener.
 * @author Matthew Tropiano
 */
public interface OALSoundStageListener
{
	/**
	 * Called when a sound is cached. 
	 * @param data		the sound data that was the source of the data.
	 * @param buffer	the buffer that was cached.
	 */
	public void soundCached(OALSoundResource data, OALBuffer buffer);
	
	/**
	 * Called when the buffer was released from the cache.
	 * @param data		the sound data that was the source of the data.
	 * @param buffer	The buffer that was released from the cache.
	 */
	public void soundReleased(OALSoundResource data, OALBuffer buffer);
	
	/**
	 * Called when a sound resource is played. 
	 */
	public void soundPlayed(OALSoundResource data);

	/**
	 * Called when a sound resource is stopped. 
	 */
	public void soundStopped(OALSoundResource data);

	/**
	 * Called when a streaming thread is started.
	 */
	public void soundStreamStarted(OALSoundResource data);
	
	/**
	 * Called when a streaming thread dies.
	 */
	public void soundStreamStopped(OALSoundResource data);
	
	/**
	 * Called when a Source is played.
	 * @param source 	the source that this occurred on. 
	 */
	public void sourcePlayed(OALSource source);
	
	/**
	 * Called when a Source is paused.
	 * @param source 	the source that this occurred on. 
	 */
	public void sourcePaused(OALSource source);
	
	/**
	 * Called when a Source is rewound.
	 * @param source 	the source that this occurred on. 
	 */
	public void sourceRewound(OALSource source);
	
	/**
	 * Called when a Source is stopped.
	 * @param source 	the source that this occurred on. 
	 */
	public void sourceStopped(OALSource source);

	/**
	 * Called when a Source gets a buffer enqueued on it.
	 * @param source 	the source that this occurred on. 
	 */
	public void sourceBufferEnqueued(OALSource source, OALBuffer buffer);
	
	/**
	 * Called when a Source gets a buffer dequeued from it.
	 * @param source 	the source that this occurred on. 
	 */
	public void sourceBufferDequeued(OALSource source, OALBuffer buffer);

	/**
	 * Called when the system reads an unsupported audio file.
	 * @param data the resource in question.
	 * @param exception the exception that was thrown.
	 */
	public void errorUnsupportedResource(OALSoundResource data, UnsupportedAudioFileException exception);

	/**
	 * Called when the system fails reading a data resource.
	 * @param data the resource in question.
	 * @param exception the exception that was thrown.
	 */
	public void errorIO(OALSoundResource data, IOException exception);

}
