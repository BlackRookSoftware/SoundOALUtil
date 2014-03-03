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

/**
 * This class is an abstraction of series of audio circuits that takes two
 * signals and mixes them together in some way.
 * @author Matthew Tropiano
 */
public abstract class Combiner
{
	/**
	 * Combines two sets of samples and returns it into another one.
	 * The policy of a combiner is that samplesA and samplesB do not change at all,
	 * and all arrays must be equal in length with each other. 
	 * @param samplesA the first set of samples.
	 * @param offsetA the offset into the first set of samples to start from.
	 * @param samplesB the second set of samples.
	 * @param out the output set of samples, changed after execution of this function.
	 * @param offsetOut the offset into the output set of samples to put the combined samples.
	 * @param length the amount of samples to combine.
	 */
	public abstract void combineInline(double[] samplesA, int offsetA, double[] samplesB, int offsetB, double[] out, int offsetOut, int length);
	
	/**
	 * Combines two sets of samples and returns it into another one.
	 * The policy of a combiner is that samplesA and samplesB do not change at all,
	 * and all arrays must be equal in length with each other.
	 * <p>This is equivalent to <code>combineInline(samplesA, 0, samplesB, 0, out, 0)</code>.
	 * @param samplesA the first set of samples.
	 * @param samplesB the second set of samples.
	 * @param out the output set of samples, changed after execution of this function.
	 */
	public void combineInline(double[] samplesA, double[] samplesB, double[] out)
	{
		combineInline(samplesA, 0, samplesB, 0, out, 0, samplesA.length);
	}
	
	/**
	 * Combines two sets of samples and returns it into a new array of samples.
	 * The policy of a combiner is that samplesA and samplesB do not change at all,
	 * and both arrays must be equal in length with each other. 
	 * @param samplesA the first set of samples.
	 * @param samplesB the second set of samples.
	 */
	public double[] combine(double[] samplesA, double[] samplesB)
	{
		double[] out = new double[samplesA.length];
		combineInline(samplesA, samplesB, out);
		return out;
	}

}
