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
package com.blackrook.oal.util.dsp.combiner;

import com.blackrook.oal.util.dsp.Combiner;

/**
 * This combiner adds two signals together.
 * It does NOT do any clipping - the samples are just added without any attenuating.
 * @author Matthew Tropiano
 */
public class Adder extends Combiner
{
	@Override
	public void combineInline(double[] samplesA, int offsetA, double[] samplesB, int offsetB, double[] out, int offsetOut, int length)
	{
		for (int i = 0; i < length; i++)
			out[i + offsetOut] = samplesA[i + offsetA] + samplesB[i + offsetB];
	}
}
