/*******************************************************************************
 * Copyright (c) 2014 Black Rook Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.oal.util.stage;

import java.io.IOException;
import java.io.PrintStream;

import javax.sound.sampled.UnsupportedAudioFileException;

import com.blackrook.oal.OALBuffer;
import com.blackrook.oal.OALSource;
import com.blackrook.oal.util.OALSoundResource;

/**
 * A sound stage listener that outputs information to print streams
 * depending on {@link OALSoundStage} events.
 * @author Matthew Tropiano
 */
public class OALSoundStageDebugListener implements OALSoundStageListener
{
	/** Output stream. */
	private PrintStream out;
	/** Error Output stream. */
	private PrintStream err;

	/**
	 * Creates a new listener that outputs to the system outputs
	 * for regular output and error output.
	 */
	public OALSoundStageDebugListener()
	{
		this(System.out, System.err);
	}
	
	/**
	 * Creates a new listener that outputs to specific {@link PrintStream}s
	 * for regular output and error output.
	 * @param out the output stream.
	 * @param err the error stream.
	 */
	public OALSoundStageDebugListener(PrintStream out, PrintStream err)
	{
		this.out = out;
		this.err = err;
	}

	@Override
	public void soundCached(OALSoundResource data, OALBuffer buffer)
	{
		out.printf("SoundCached: Sound was cached: \"%s\"\n", data.getName());
		out.printf("\tFormat %s\n", buffer.getFormat());
		out.printf("\tID: %d, %dHz, %d bytes\n", 
			buffer.getALId(), 
			buffer.getFrequency(),
			buffer.getSize()
			);
	}

	@Override
	public void soundReleased(OALSoundResource data, OALBuffer buffer)
	{
		out.printf("SoundReleased: Sound was released: \"%s\"\n", data.getName());
		out.printf("\tFormat %s\n", buffer.getFormat());
		out.printf("\tID: %d, %dHz, %d bytes\n", 
			buffer.getALId(), 
			buffer.getFrequency(),
			buffer.getSize()
			);
	}

	@Override
	public void soundPlayed(OALSoundResource data)
	{
		out.printf("SoundPlayed: Sound was played: \"%s\"\n", data.getName());
	}

	@Override
	public void soundStopped(OALSoundResource data)
	{
		out.printf("SoundStopped: Sound was stopped: \"%s\"\n", data.getName());
	}

	@Override
	public void soundStreamStarted(OALSoundResource data)
	{
		out.printf("SoundStreamStarted: Sound stream was started: \"%s\"\n", data.getName());
	}

	@Override
	public void soundStreamStopped(OALSoundResource data)
	{
		out.printf("SoundStreamStopped: Sound stream was stopped: \"%s\"\n", data.getName());
	}

	@Override
	public void sourcePlayed(OALSource source)
	{
		out.printf("SourcePlayed: Sound source was played. ID: %d\n", source.getALId());
	}

	@Override
	public void sourcePaused(OALSource source)
	{
		out.printf("SourcePaused: Sound source was paused. ID: %d\n", source.getALId());
	}

	@Override
	public void sourceRewound(OALSource source)
	{
		out.printf("SourceRewound: Sound source was rewound. ID: %d\n", source.getALId());
	}

	@Override
	public void sourceStopped(OALSource source)
	{
		out.printf("SourceRewound: Sound source was rewound. ID: %d\n", source.getALId());
	}

	@Override
	public void sourceBufferEnqueued(OALSource source, OALBuffer buffer)
	{
		out.printf("SourceBufferEnqueued: A buffer was enqueued on a sound source.\n");
		out.printf("\tFormat %s\n", buffer.getFormat());
		out.printf("\tID: %d, %dHz, %d bytes\n", 
			buffer.getALId(), 
			buffer.getFrequency(),
			buffer.getSize()
			);
		out.printf("\tSource ID: %d\n", source.getALId());
	}

	@Override
	public void sourceBufferDequeued(OALSource source, OALBuffer buffer)
	{
		out.printf("SourceBufferDequeued: A buffer was dequeued from a sound source.\n");
		out.printf("\tFormat %s\n", buffer.getFormat());
		out.printf("\tID: %d, %dHz, %d bytes\n", 
			buffer.getALId(), 
			buffer.getFrequency(),
			buffer.getSize()
			);
		out.printf("\tSource ID: %d\n", source.getALId());
	}

	@Override
	public void errorUnsupportedResource(OALSoundResource data, UnsupportedAudioFileException exception)
	{
		err.printf("ErrorUnsupportedResource: Sound resource \"%s\" is in an unsupported format.\n", data.getName());
		exception.printStackTrace(err);
	}

	@Override
	public void errorIO(OALSoundResource data, IOException exception)
	{
		err.printf("ErrorIO: Sound resource \"%s\" could not be read.\n", data.getName());
		exception.printStackTrace(err);
	}

}
