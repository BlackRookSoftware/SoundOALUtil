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
package com.blackrook.oal.util.dsp.filter;

import com.blackrook.commons.math.RMath;
import com.blackrook.oal.util.dsp.Filter;

/**
 * A signal filter that will normalize a signal to a specified sample level.
 * @author Matthew Tropiano
 */
public class Normalizer extends Filter
{
	/** The target magnitude to filter. */
	protected double magnitude;
	
	/**
	 * Creates a new Normalizer that will normalize all of the 
	 * samples in the signal to a magnitude of 1.0.
	 */
	public Normalizer()
	{
		this(1);
	}

	/**
	 * Creates a new Normalizer that will normalize all of the 
	 * samples in the signal to a magnitude of 1.0.
	 */
	public Normalizer(double magnitude)
	{
		this.magnitude = magnitude;
	}
	
	@Override
	public void filterInline(double[] samplesIn, int offsetIn, double[] samplesOut, int offsetOut, int length)
	{
		double maxMagnitude = 0.0;
		for (int i = 0; i < length; i++)
			maxMagnitude = Math.max(maxMagnitude, Math.abs(samplesIn[i + offsetIn]));

		for (int i = 0; i < length; i++)
			samplesOut[i + offsetOut] = 
				RMath.getInterpolationFactor(Math.abs(samplesIn[i + offsetIn]), 0.0, maxMagnitude) * 
				magnitude * (samplesIn[i + offsetIn] < 0 ? -1 : 1);
	}

	/**
	 * Returns the target magnitude used for this filter.
	 */
	public double getMagnitude()
	{
		return magnitude;
	}

	/**
	 * Sets the target magnitude used for this filter.
	 */
	public void setMagnitude(double magnitude)
	{
		this.magnitude = magnitude;
	}

}
