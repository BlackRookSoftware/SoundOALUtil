/*******************************************************************************
 * Copyright (c) 2014 Black Rook Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.oal.util.dsp;

/**
 * This class is an abstraction of series of audio circuits that takes a
 * single signal and modifies it.
 * @author Matthew Tropiano
 */
public abstract class Filter
{
	/**
	 * Filters a set of samples and returns it into another one.
	 * This method should follow this policy: the content of samplesIn is not changed,
	 * but the content of samplesOut <i>will</i> change. If samplesOut == samplesIn,
	 * this should still work as intended.
	 * @param samplesIn the audio samples to filter.
	 * @param offsetIn the offset into the set of samples to start filtering.
	 * @param samplesOut the output of the audio filter.
	 * @param offsetOut the offset into the output set of samples to put the combined samples.
	 * @param length the amount of samples to filter.
	 */
	public abstract void filterInline(double[] samplesIn, int offsetIn, double[] samplesOut, int offsetOut, int length);
	
	/**
	 * Filters a set of samples and returns it into another one.
	 * This method should follow this policy: the content of samplesIn is not changed,
	 * but the content of samplesOut <i>will</i> change. If samplesOut == samplesIn,
	 * this should still work as intended.
	 * @param samplesIn the audio samples to filter.
	 * @param samplesOut the output of the audio filter.
	 */
	public void filterInline(double[] samplesIn, double[] samplesOut)
	{
		filterInline(samplesIn, 0, samplesOut, 0, samplesIn.length);
	}
	
	/**
	 * Compresses a signal and returns it into a new array of samples.
	 * @param samples the audio samples to filter.
	 */
	public double[] filter(double[] samples)
	{
		double[] out = new double[samples.length];
		filterInline(samples, out);
		return out;
	}

}
