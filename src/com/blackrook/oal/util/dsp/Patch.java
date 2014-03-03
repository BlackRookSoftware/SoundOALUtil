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
package com.blackrook.oal.util.dsp;

import com.blackrook.commons.math.wave.WaveForm;
import com.blackrook.commons.math.wave.WaveFormType;

/**
 * An instrument patch that is sampled at different frequencies.
 * @author Matthew Tropiano
 */
public class Patch
{
	/** Macro for sounds that don't loop. */
	public static final int NO_LOOPING = -1;
	
	/** The wave form to sample from. */
	protected WaveFormType waveForm;

	/** Base frequency of the wave form in Hertz. */
	protected double frequency;
	/** Base sampling rate of the wave form in Samples per Second. */
	protected int samplingRate;
	/** The number of discrete samples in the waveform. */
	protected int samples;
	/** Loop sample offset. Less than 0 = no loop. */
	protected int loopOffset;
	/** Is the waveform to be treated as a sound clip? */
	protected boolean clip;

	/**
	 * Creates a new patch from a waveform,
	 * with a base frequency of 1.0 Hz, not looping, not a sound clip. 
	 * @param waveForm the waveform to use for the patch.
	 * @param samplingRate the sampling rate of the provided waveform.
	 *     This is used to figure out where on the wave to begin sampling.
	 * @param samples the amount of discrete samples in the provided waveform.
	 *     Even if the waveform provided has discrete info, it is still sampled as though
	 *     it were a function. Hence, the amount of actual samples must be provided.
	 */
	public Patch(WaveForm waveForm, int samplingRate, int samples)
	{
		this(waveForm, 1.0, samplingRate, samples, false, NO_LOOPING);
	}
	
	/**
	 * Creates a new patch from a waveform, not looping, not a sound clip. 
	 * @param waveForm the waveform to use for the patch.
	 * @param frequency the base frequency of the waveform (concert tone). 
     *     This is used during sampling to figure out the target 
     *     frequency from pitch or intended note.
	 * @param samplingRate the sampling rate of the provided waveform.
	 *     This is used to figure out where on the wave to begin sampling.
	 * @param samples the amount of discrete samples in the provided waveform.
	 *     Even if the waveform provided has discrete info, it is still sampled as though
	 *     it were a function. Hence, the amount of actual samples must be provided.
	 */
	public Patch(WaveForm waveForm, double frequency, int samplingRate, int samples)
	{
		this(waveForm, frequency, samplingRate, samples, false, NO_LOOPING);
	}
	
	/**
	 * Creates a new patch from a waveform, not a sound clip. 
	 * @param waveForm the waveform to use for the patch.
	 * @param frequency the base frequency of the waveform (concert tone). 
     *     This is used during sampling to figure out the target 
     *     frequency from pitch or intended note.
	 * @param samplingRate the sampling rate of the provided waveform.
	 *     This is used to figure out where on the wave to begin sampling.
	 * @param samples the amount of discrete samples in the provided waveform.
	 *     Even if the waveform provided has discrete info, it is still sampled as though
	 *     it were a function. Hence, the amount of actual samples must be provided.
	 * @param loopOffset the offset in samples to start looping past the end of the wave.
	 *     Can also be NO_LOOPING or less than 0 to disable this.
	 */
	public Patch(WaveForm waveForm, double frequency, int samplingRate, int samples, int loopOffset)
	{
		this(waveForm, frequency, samplingRate, samples, false, loopOffset);
	}
	
	/**
	 * Creates a new patch from a waveform.
	 * @param waveForm the waveform to use for the patch.
	 * @param frequency the base frequency of the waveform (concert tone). 
     *     This is used during sampling to figure out the target 
     *     frequency from pitch or intended note.
	 * @param samplingRate the sampling rate of the provided waveform.
	 *     This is used to figure out where on the wave to begin sampling.
	 * @param samples the amount of discrete samples in the provided waveform.
	 *     Even if the waveform provided has discrete info, it is still sampled as though
	 *     it were a function. Hence, the amount of actual samples must be provided.
	 * @param clip if true, this patch is treated like a sound clip.
	 *     If it is a clip, sampling past the end of the wave will yield 0.0 unless it has a
	 *     loop offset defined that is 0 or greater. Samplers and mixers also need to know
	 *     this in order to figure out whether to sample this from the beginning or not on
	 *     the next attack.   
	 * @param loopOffset the offset in samples to start looping past the end of the wave.
	 *     Can also be NO_LOOPING or less than 0 to disable this.
	 */
	public Patch(WaveForm waveForm, double frequency, int samplingRate, int samples, boolean clip, int loopOffset)
	{
		this.waveForm = waveForm;
		this.frequency = frequency;
		this.samplingRate = samplingRate;
		this.samples = samples;
		this.clip = clip;
		this.loopOffset = loopOffset;
	}

	/**
	 * Returns the result of a sample from this patch.
	 * @param frequency the target frequency of the waveform (concert tone). 
	 * @param time the amount of time along the patch to sample in seconds.
	 * @return a number between -1.0 and 1.0 describing the sample data.
	 */
	public double getSample(double frequency, double time)
	{
		double pitch = frequency / this.frequency;
		double samplePos = samplingRate * time * pitch;
		if (clip)
		{
			if (loopOffset >= 0 && loopOffset < samples)
				samplePos = ((samplePos - loopOffset) % (samples - loopOffset)) + loopOffset;
			else if (samplePos > samples)
				return 0.0;
		}
		else
			samplePos = samplePos % samples;
		return waveForm.getSample(samplePos / samples / waveForm.getAmplitude());
	}
	
	/**
	 * Returns the result of a sample from this patch.
	 * @param note the note frequency to use (concert tone). 
	 * @param time the amount of time along the patch to sample in seconds.
	 * @return a number between -1.0 and 1.0 describing the sample data.
	 */
	public double getSample(Note note, double time)
	{
		return getSample(note.getFrequency(), time);
	}

	public WaveFormType getWaveForm()
	{
		return waveForm;
	}

	/**
	 * Returns the base frequency of the patch's waveform.
	 */
	public double getFrequency()
	{
		return frequency;
	}

	/**
	 * Returns the sampling rate of the patch in discrete samples.
	 * This is used to figure out where on the wave to begin sampling.
	 */
	public int getSamplingRate()
	{
		return samplingRate;
	}

	/**
	 * Returns the offset in samples in which this patch loops.
	 * If NO_LOOPING or less than 0, it is disabled.
	 */
	public int getLoopOffset()
	{
		return loopOffset;
	}

	/**
	 * Returns true if this is a clip, false otherwise.
	 * If it is a clip, sampling past the end of the wave will yield 0.0 unless it has a
	 * loop offset defined that is 0 or greater. Samplers and mixers also need to know
	 * this in order to figure out whether to sample this from the beginning or not on
	 * the next attack.   
	 */
	public boolean isClip()
	{
		return clip;
	}
	
}
