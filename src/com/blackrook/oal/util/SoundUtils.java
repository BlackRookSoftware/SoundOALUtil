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
package com.blackrook.oal.util;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

import com.blackrook.commons.math.RMath;
import com.blackrook.commons.math.wave.WaveFormType;

/**
 * Static sound function class.
 * @author Matthew Tropiano
 */
public final class SoundUtils
{
	private SoundUtils() {}
	
	/**
	 * Calculates an OpenAL pitch value using logarithmic semitones.
	 * This is relative to Middle C or no pitch variance (pitch = 1.0), so... 
	 * <ul>
	 * <li> 0 semitones is equivalent to a pitch of 1.0,
	 * <li> 12 semitones is equivalent to 2.0, 
	 * <li> 24 semitones is 4.0,
	 * <li> ...etc.
	 * </ul>
	 * The same works in the opposite direction: 
	 * <ul>
	 * <li> -12 semitones is 0.5,
	 * <li> -24 semitones is 0.25,
	 * <li> -36 semitones is 0.125,
	 * <li> ...etc.
	 * </ul>
	 * Every 12 semitones is one octave.
	 */
	public static float semitonesToPitch(float semitones)
	{
		return centsToPitch(semitones*100.0f);
	}
	
	/**
	 * Calculates an OpenAL pitch value by logarithmic cents.
	 * This is relative to Middle C or no pitch variance (pitch = 1.0), so... 
	 * <ul>
	 * <li> 0 cents is equivalent to a pitch of 1.0,
	 * <li> 1200 cents is equivalent to 2.0, 
	 * <li> 2400 cents is 4.0,
	 * <li> ...etc.
	 * </ul>
	 * The same works in the opposite direction: 
	 * <ul>
	 * <li> -1200 cents is 0.5,
	 * <li> -2400 cents is 0.25,
	 * <li> -3600 cents is 0.125,
	 * <li> ...etc.
	 * </ul>
	 * Every 1200 cents is one octave.
	 */
	public static float centsToPitch(float cents)
	{
		return (float)(Math.pow(2, cents/1200.0));
	}
	
	/**
	 * Calculates an OpenAL gain value by logarithmic decibels (sound pressure).
	 * This is relative to 0 dB or full gain (gain = 1.0), so... 
	 * <ul>
	 * <li> 0 decibels is equivalent to a gain of 1.0,
	 * <li> -10 decibels is 0.5,
	 * <li> -20 decibels is 0.25,
	 * <li> -30 decibels is 0.125,
	 * <li> ...etc.
	 * </ul>
	 * Every 10 decibels changes sound volume by a power of 2.
	 * Be aware that OpenAL clamps gain into the range (0 to 1).
	 * The value Negative Infinity is accepted in order to express dead silence.
	 */
	public static float decibelsToGain(float decibels)
	{
		if (decibels == Float.NEGATIVE_INFINITY)
			return 0.0f;
		return (float)(Math.pow(2, decibels/10.0));
	}
	
	/**
	 * Converts a sample in double-precision floating-point representation
	 * to an 8-bit unsigned PCM sample. Assumes amplitude of 1.0.
	 * @param sample the incoming sample.
	 * @return an 8-bit sample value.
	 */
	public static byte doubleToByteSample(double sample)
	{
		return doubleToByteSample(sample, 1.0);
	}
	
	/**
	 * Converts a sample in double-precision floating-point representation
	 * to an 8-bit unsigned PCM sample.
	 * @param sample the incoming sample.
	 * @param amplitude the amplitude of its originating wave (the threshold 
	 * for what is considered the highest point).
	 * @return an 8-bit sample value.
	 */
	public static byte doubleToByteSample(double sample, double amplitude)
	{
		return (byte)((int)(RMath.linearInterpolate(((sample/amplitude)+1.0)/2.0, 0, 255)) & 0x0ff);
	}
	
	/**
	 * Converts a sample in double-precision floating-point representation
	 * to an 16-bit signed PCM sample. Assumes amplitude of 1.0.
	 * @param sample the incoming sample.
	 * @return an 16-bit sample value.
	 */
	public static short doubleToShortSample(double sample)
	{
		return doubleToShortSample(sample, 1.0);
	}
	
	/**
	 * Converts a sample in double-precision floating-point representation
	 * to an 16-bit signed PCM sample.
	 * @param sample the incoming sample.
	 * @param amplitude the amplitude of its originating wave (the threshold 
	 * for what is considered the highest point).
	 * @return an 16-bit sample value.
	 */
	public static short doubleToShortSample(double sample, double amplitude)
	{
		return (short)((int)(RMath.linearInterpolate(((sample/amplitude)+1.0)/2.0, -32768.0, 32767.0)) & 0x0ffff);
	}
	
	/**
	 * Converts a 16-bit signed sample to a double-precision sample.
	 */
	public static double pcmData16BitToSample(short data)
	{
		return RMath.getInterpolationFactor(data, -32768, 32767) * 2.0 - 1.0;
	}

	/**
	 * Converts an 8-bit unsigned sample to a double-precision sample.
	 */
	public static double pcmData8BitToSample(byte data)
	{
		return RMath.getInterpolationFactor(data & 0x0ff, 0, 255) * 2.0 - 1.0;
	}

	/**
	 * Converts an array of samples as 8-bits-per-sample PCM wave data.
	 * @param samples the incoming samples.
	 * @param amplitude the amplitude of its originating wave (the threshold 
	 * for what is considered the highest point).
	 * @return a non-direct buffer rewound to the beginning with the converted data.
	 */
	public static ByteBuffer asPCMData8Bit(double[] samples, double amplitude)
	{
		ByteBuffer bb = ByteBuffer.allocate(samples.length);
		bb.order(ByteOrder.nativeOrder());
		for (double d : samples)
			bb.put(doubleToByteSample(d, amplitude));
		bb.rewind();
		return bb;
	}
	
	/**
	 * Converts an array of samples as 16-bits-per-sample PCM wave data.
	 * This will produce a buffer in the current platform's NATIVE BYTE ORDER (endian mode).
	 * Those just generating waves for immediate playback should use this.
	 * @param samples the incoming samples.
	 * @param amplitude the amplitude of its originating wave (the threshold 
	 * for what is considered the highest point).
	 * @return a non-direct buffer rewound to the beginning with the converted data.
	 */
	public static ByteBuffer asPCMData16Bit(double[] samples, double amplitude)
	{
		return asPCMData16Bit(samples, amplitude, ByteOrder.nativeOrder());
	}
	
	/**
	 * Converts an array of samples as 16-bits-per-sample PCM wave data.
	 * This will produce a buffer in the desired byte order (endian mode).
	 * Those generating waves for storage should use this.
	 * @param samples the incoming samples.
	 * @param amplitude the amplitude of its originating wave (the threshold 
	 * for what is considered the highest point).
	 * @param order the desired byte ordering.
	 * @return a non-direct buffer rewound to the beginning with the converted 
	 * data in the desired byte ordering.
	 */
	public static ByteBuffer asPCMData16Bit(double[] samples, double amplitude, ByteOrder order)
	{
		ByteBuffer bb = ByteBuffer.allocate(samples.length*2);
		bb.order(order);
		for (double d : samples)
			bb.putShort(doubleToShortSample(d, amplitude));
		bb.rewind();
		return bb;
	}

	/**
	 * Generates a sound wave comprised of 64-bit floating point samples using the provided waveform.
	 * @param frequency the frequency of the output wave in hertz.
	 * @param sampleRate the sampling rate of this wave (how many samples are in one second). 
	 * @param gain the amplitude scalar value. a value of 1.0 is the baseline for an unaltered wave.
	 * a negative gain alters the wave's phase. 
	 * @param waveForm the source waveform to sample.
	 * @return an array of double-precision samples with an amplitude of 1.0 (samples are between -1.0 and 1.0).
	 */
	public static double[] createSoundWaveSamples(double frequency, int sampleRate, double gain, WaveFormType waveForm)
	{
		int samplesPerPeriod = (int)(sampleRate / frequency);
		double[] samples = new double[samplesPerPeriod];
		for (int i = 0; i < samples.length; i++)
			samples[i] = waveForm.getSample(((double)i/samplesPerPeriod) / waveForm.getAmplitude()) * gain;
		return samples;
	}
	
	/**
	 * Generates a mononatural PCM sound wave comprised of 8-bit samples using the provided waveform.
	 * @param frequency the frequency of the output wave in hertz.
	 * @param sampleRate the sampling rate of this wave (how many samples are in one second). 
	 * @param gain the amplitude scalar value. a value of 1.0 is the baseline for an unaltered wave.
	 * a negative gain alters the wave's phase. 
	 * @param waveForm the source waveform to sample.
	 * @return a non-direct buffer rewound to the beginning with the generated data.
	 */
	public static ByteBuffer createSoundWave8Bit(double frequency, int sampleRate, double gain, WaveFormType waveForm)
	{
		return asPCMData8Bit(createSoundWaveSamples(frequency, sampleRate, gain, waveForm), 1.0);
	}
	
	/**
	 * Generates a mononatural PCM sound wave comprised of 16-bit samples using the provided waveform.
	 * The generated wave is in the NATIVE byte order (endian mode). 
	 * @param frequency the frequency of the output wave in hertz.
	 * @param sampleRate the sampling rate of this wave (how many samples are in one second). 
	 * @param gain the amplitude scalar value. a value of 1.0 is the baseline for an unaltered wave.
	 * a negative gain alters the wave's phase. 
	 * @param waveForm the source waveform to sample.
	 * @return a non-direct buffer rewound to the beginning with the generated data.
	 */
	public static ByteBuffer createSoundWave16Bit(double frequency, int sampleRate, double gain, WaveFormType waveForm)
	{
		return asPCMData16Bit(createSoundWaveSamples(frequency, sampleRate, gain, waveForm), 1.0);
	}
	
	/**
	 * Interleaves multiple sets of samples into a single set of samples used for
	 * multichannel playback. The ordering of the parameters of this function influences
	 * the resulting placement of the samples.
	 * @param sampleList the list of sample strings to mux together in the specified order. Every string
	 * must be the SAME LENGTH.
	 * @return a single array of samples interleaved together, presumably to be converted to
	 * a multichannel set of PCM samples.
	 * @throws IllegalArgumentException if any of the sample list arrays are not the same length as the others.
	 */
	public static double[] muxSamples(double[] ... sampleList)
	{
		if (sampleList.length == 0)
			return new double[0];
		else if (sampleList.length == 1)
			return Arrays.copyOf(sampleList[0], sampleList[0].length);
		else
		{
			int len = sampleList[0].length;
			for (int i = 1; i < sampleList.length; i++)
				if (sampleList[i].length != len)
					throw new IllegalArgumentException("All sample lists are not the same length.");
			double[] out = new double[len*sampleList.length];
			int s = 0;
			for (int x = 0; x < len; x++)
				for (int i = 0; i < sampleList.length; i++)
					out[s++] = sampleList[i][x];
			return out;
		}
	}
	
	/**
	 * De-interleaves a set of samples into more than one channel.
	 * @param samples the list of samples to demux into separate channels.
	 * @param channels the number of channels.
	 * @return a multidimensional array of the samples separated into discrete
	 * sets of samples equal to the number of specified channels.
	 * @throws IllegalArgumentException if the length of <code>samples</code> is not divisible
	 * by <code>channels</code>.
	 */
	public static double[][] demuxSamples(double[] samples, int channels)
	{
		if (samples.length % channels != 0)
			throw new IllegalArgumentException("The sample list is not divisible by the number of channels.");
		
		if (channels == 1)
		{
			double[][] out = new double[1][];
			out[0] = Arrays.copyOf(samples, samples.length);
			return out;
		}
		else
		{
			double[][] out = new double[channels][samples.length / channels];
			for (int x = 0; x < samples.length; x++)
				out[x%channels][x/channels] = samples[x];
			return out;
		}
	}
	
}
