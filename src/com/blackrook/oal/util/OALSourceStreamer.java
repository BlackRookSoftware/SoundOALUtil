/*******************************************************************************
 * Copyright (c) 2014 Black Rook Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.oal.util;

import java.io.IOException;
import java.nio.ByteBuffer;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.UnsupportedAudioFileException;

import com.blackrook.commons.Ticker;
import com.blackrook.oal.JSPISoundHandle;
import com.blackrook.oal.OALBuffer;
import com.blackrook.oal.OALSource;

/**
 * Streaming utility object for piping an I/O Stream into a source.
 * If the stream should be looped, like as if it is from a file, then
 * "looping" should be set on THIS OBJECT, NOT ON THE SOURCE (if set on
 * the source, it will only loop the current buffer).  
 */
public class OALSourceStreamer
{
	/** Reference to encapsulated source. */
	protected OALSource source;
	/** Handle to the stream. */
	protected JSPISoundHandle soundHandle;

	/** Is this supposed to loop? */
	protected boolean looping;
	
	/** Reference to the decoder encapsulation itself. */
	protected JSPISoundHandle.Decoder decoderRef;
	/** Decoder format taken from the sound handle. */
	protected AudioFormat decoderFormat;
	/** Temporary buffer for the decoded data to be placed in an OAL buffer. */
	protected byte[] bytebuffer;
	
	/** The auto-streaming thread. */
	protected Ticker streamer;
	
	/** Number of uncompressed bytes read. */
	protected long bytesRead;
	
	/**
	 * Constructs a new Source Streamer.
	 * @param source the source that will playing the stream contents.
	 * @param streamBuffers the buffers used for the buffer queue, ordinarily two buffers.
	 * The data inside the buffers will be discarded entirely.
	 * @param soundHandle the handle to the audio stream.
	 * @param automatic should this spawn a thread to auto-update itself (if not, it must rely 
	 * on something calling {@link #update()})?
	 * @param looping should this loop from the beginning of the input once the end of the
	 * stream is reached or thought to be reached 
	 * (this will attempt to re-open the stream in order to restart and stitch together samples).
	 * @param bufferSize the size of each buffer in milliseconds (may be approximated).
	 * @throws UnsupportedAudioFileException if the file's audio format is unrecognized.
	 * @throws IOException if a read error occurs.
	 */
	public OALSourceStreamer(OALSource source, OALBuffer[] streamBuffers, 
		JSPISoundHandle soundHandle, boolean automatic, boolean looping, int bufferSize) 
		throws UnsupportedAudioFileException, IOException
	{
		this.source = source;
		this.soundHandle = soundHandle;
		this.looping = looping;
		
		reloadDecoder();

		int bytesPerChannelSample = decoderFormat.getChannels() * decoderFormat.getSampleSizeInBits() / 8;
		int sampleCount = (int)(decoderFormat.getSampleRate()/(bufferSize/1000f));
		int byteSize = sampleCount * bytesPerChannelSample;
		bytebuffer = new byte[byteSize];

		for (OALBuffer b : streamBuffers)
		{
			b.setSamplingRate((int)decoderFormat.getSampleRate());
			b.setFormatByChannelsAndBits(decoderFormat.getChannels(), decoderFormat.getSampleSizeInBits());
			int l = decoderRef.readPCMBytes(bytebuffer);
			b.loadPCMData(ByteBuffer.wrap(bytebuffer),l);
			bytesRead += l;
			source.enqueueBuffer(b);
		}
		
		if (automatic)
		{
			streamer = new Streamer(bufferSize/2);
			streamer.start();
		}
		
	}

	/**
	 * Restarts the decoder reload.
	 * @throws UnsupportedAudioFileException if the file's audio format is unrecognized.
	 * @throws IOException if a read error occurs.
	 */
	protected void reloadDecoder() throws UnsupportedAudioFileException, IOException
	{
		decoderRef = soundHandle.getDecoder();
		decoderFormat = decoderRef.getDecodedAudioFormat();
	}
	
	/**
	 * Updates the streamer.
	 * This checks how many buffers have been processed on the encapsulated source,
	 * and then dequeues those buffers, fills them with data, and re-enqueues them.
	 * This will also reload the stream if looping is set to true.
	 * <p><b> THIS DOES NOT NEED TO BE CALLED IF THIS WAS CONSTRUCTED 
	 * WITH AUTO-UPDATE BEING TRUE. DOING SO MAY CAUSE UNSTABLE EFFECTS.</b>
	 * @return the amount of bytes read back through the sound handle.
	 * @throws UnsupportedAudioFileException if the file's audio format is unrecognized.
	 * @throws IOException if a read error occurs.
	 */
	public int update() throws UnsupportedAudioFileException, IOException
	{
		int out = -1;
		int p = source.getProcessedBufferCount();
		while (p-- > 0 && out != 0)
		{
			OALBuffer b = source.dequeueBuffer();
			out = decoderRef.readPCMBytes(bytebuffer);
			if (out > 0)
			{
				bytesRead += out;
				b.loadPCMData(ByteBuffer.wrap(bytebuffer),out);
				source.enqueueBuffer(b);
			}
			else if (out == 0 && looping)
			{
				reloadDecoder();
				out = decoderRef.readPCMBytes(bytebuffer);
				if (out > 0)
				{
					bytesRead += out;
					b.loadPCMData(ByteBuffer.wrap(bytebuffer),out);
					source.enqueueBuffer(b);
				}
			}
		}
		return out;
	}

	/**
	 * Is this streamer in looping mode?
	 */
	public boolean isLooping()
	{
		return looping;
	}

	/**
	 * Sets if this streamer is in looping mode.
	 */
	public void setLooping(boolean looping)
	{
		this.looping = looping;
	}

	/**
	 * Gets the number of bytes read by this streamer.
	 */
	public long getBytesRead()
	{
		return bytesRead;
	}

	/**
	 * Special thread for streaming content from the provided handle.
	 * @author Matthew Tropiano
	 */
	protected class Streamer extends Ticker
	{
		Streamer(int bufferSize)
		{
			super(1000/bufferSize);
		}
		
		public void doTick(long tick)
		{
			if (source.isStopped())
				stop();
			try {
				update();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}
	
}


