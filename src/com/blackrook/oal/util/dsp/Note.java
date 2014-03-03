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

import static com.blackrook.oal.util.SoundUtils.semitonesToPitch;

/**
 * Enumeration of musical notes.
 * @author Matthew Tropiano
 */
public enum Note
{
	C0        (22.5 * semitonesToPitch(-9)),
	C_SHARP0  (22.5 * semitonesToPitch(-8)),
	D0        (22.5 * semitonesToPitch(-7)),
	E_FLAT0   (22.5 * semitonesToPitch(-6)),
	E0        (22.5 * semitonesToPitch(-5)),
	F0        (22.5 * semitonesToPitch(-4)),
	F_SHARP0  (22.5 * semitonesToPitch(-3)),
	G0        (22.5 * semitonesToPitch(-2)),
	G_SHARP0  (22.5 * semitonesToPitch(-1)),
	A0        (22.5 * semitonesToPitch(0)),
	B_FLAT0   (22.5 * semitonesToPitch(1)),
	B0        (22.5 * semitonesToPitch(2)),
	C1        (55.0 * semitonesToPitch(-9)),
	C_SHARP1  (55.0 * semitonesToPitch(-8)),
	D1        (55.0 * semitonesToPitch(-7)),
	E_FLAT1   (55.0 * semitonesToPitch(-6)),
	E1        (55.0 * semitonesToPitch(-5)),
	F1        (55.0 * semitonesToPitch(-4)),
	F_SHARP1  (55.0 * semitonesToPitch(-3)),
	G1        (55.0 * semitonesToPitch(-2)),
	G_SHARP1  (55.0 * semitonesToPitch(-1)),
	A1        (55.0 * semitonesToPitch(0)),
	B_FLAT1   (55.0 * semitonesToPitch(1)),
	B1        (55.0 * semitonesToPitch(2)),
	C2        (110.0 * semitonesToPitch(-9)),
	C_SHARP2  (110.0 * semitonesToPitch(-8)),
	D2        (110.0 * semitonesToPitch(-7)),
	E_FLAT2   (110.0 * semitonesToPitch(-6)),
	E2        (110.0 * semitonesToPitch(-5)),
	F2        (110.0 * semitonesToPitch(-4)),
	F_SHARP2  (110.0 * semitonesToPitch(-3)),
	G2        (110.0 * semitonesToPitch(-2)),
	G_SHARP2  (110.0 * semitonesToPitch(-1)),
	A2        (110.0 * semitonesToPitch(0)),
	B_FLAT2   (110.0 * semitonesToPitch(1)),
	B2        (110.0 * semitonesToPitch(2)),
	C3        (220.0 * semitonesToPitch(-9)),
	C_SHARP3  (220.0 * semitonesToPitch(-8)),
	D3        (220.0 * semitonesToPitch(-7)),
	E_FLAT3   (220.0 * semitonesToPitch(-6)),
	E3        (220.0 * semitonesToPitch(-5)),
	F3        (220.0 * semitonesToPitch(-4)),
	F_SHARP3  (220.0 * semitonesToPitch(-3)),
	G3        (220.0 * semitonesToPitch(-2)),
	G_SHARP3  (220.0 * semitonesToPitch(-1)),
	A3        (220.0 * semitonesToPitch(0)),
	B_FLAT3   (220.0 * semitonesToPitch(1)),
	B3        (220.0 * semitonesToPitch(2)),
	C4        (440.0 * semitonesToPitch(-9)),
	C_SHARP4  (440.0 * semitonesToPitch(-8)),
	D4        (440.0 * semitonesToPitch(-7)),
	E_FLAT4   (440.0 * semitonesToPitch(-6)),
	E4        (440.0 * semitonesToPitch(-5)),
	F4        (440.0 * semitonesToPitch(-4)),
	F_SHARP4  (440.0 * semitonesToPitch(-3)),
	G4        (440.0 * semitonesToPitch(-2)),
	G_SHARP4  (440.0 * semitonesToPitch(-1)),
	A4        (440.0 * semitonesToPitch(0)),
	B_FLAT4   (440.0 * semitonesToPitch(1)),
	B4        (440.0 * semitonesToPitch(2)),
	C5        (880.0 * semitonesToPitch(-9)),
	C_SHARP5  (880.0 * semitonesToPitch(-8)),
	D5        (880.0 * semitonesToPitch(-7)),
	E_FLAT5   (880.0 * semitonesToPitch(-6)),
	E5        (880.0 * semitonesToPitch(-5)),
	F5        (880.0 * semitonesToPitch(-4)),
	F_SHARP5  (880.0 * semitonesToPitch(-3)),
	G5        (880.0 * semitonesToPitch(-2)),
	G_SHARP5  (880.0 * semitonesToPitch(-1)),
	A5        (880.0 * semitonesToPitch(0)),
	B_FLAT5   (880.0 * semitonesToPitch(1)),
	B5        (880.0 * semitonesToPitch(2)),
	C6        (1760.0 * semitonesToPitch(-9)),
	C_SHARP6  (1760.0 * semitonesToPitch(-8)),
	D6        (1760.0 * semitonesToPitch(-7)),
	E_FLAT6   (1760.0 * semitonesToPitch(-6)),
	E6        (1760.0 * semitonesToPitch(-5)),
	F6        (1760.0 * semitonesToPitch(-4)),
	F_SHARP6  (1760.0 * semitonesToPitch(-3)),
	G6        (1760.0 * semitonesToPitch(-2)),
	G_SHARP6  (1760.0 * semitonesToPitch(-1)),
	A6        (1760.0 * semitonesToPitch(0)),
	B_FLAT6   (1760.0 * semitonesToPitch(1)),
	B6        (1760.0 * semitonesToPitch(2)),
	C7        (3520.0 * semitonesToPitch(-9)),
	C_SHARP7  (3520.0 * semitonesToPitch(-8)),
	D7        (3520.0 * semitonesToPitch(-7)),
	E_FLAT7   (3520.0 * semitonesToPitch(-6)),
	E7        (3520.0 * semitonesToPitch(-5)),
	F7        (3520.0 * semitonesToPitch(-4)),
	F_SHARP7  (3520.0 * semitonesToPitch(-3)),
	G7        (3520.0 * semitonesToPitch(-2)),
	G_SHARP7  (3520.0 * semitonesToPitch(-1)),
	A7        (3520.0 * semitonesToPitch(0)),
	B_FLAT7   (3520.0 * semitonesToPitch(1)),
	B7        (3520.0 * semitonesToPitch(2)),
	C8        (7040.0 * semitonesToPitch(-9)),
	C_SHARP8  (7040.0 * semitonesToPitch(-8)),
	D8        (7040.0 * semitonesToPitch(-7)),
	E_FLAT8   (7040.0 * semitonesToPitch(-6)),
	E8        (7040.0 * semitonesToPitch(-5)),
	F8        (7040.0 * semitonesToPitch(-4)),
	F_SHARP8  (7040.0 * semitonesToPitch(-3)),
	G8        (7040.0 * semitonesToPitch(-2)),
	G_SHARP8  (7040.0 * semitonesToPitch(-1)),
	A8        (7040.0 * semitonesToPitch(0)),
	B_FLAT8   (7040.0 * semitonesToPitch(1)),
	B8        (7040.0 * semitonesToPitch(2)),
	C9        (14080.0 * semitonesToPitch(-9)),
	C_SHARP9  (14080.0 * semitonesToPitch(-8)),
	D9        (14080.0 * semitonesToPitch(-7)),
	E_FLAT9   (14080.0 * semitonesToPitch(-6)),
	E9        (14080.0 * semitonesToPitch(-5)),
	F9        (14080.0 * semitonesToPitch(-4)),
	F_SHARP9  (14080.0 * semitonesToPitch(-3)),
	G9        (14080.0 * semitonesToPitch(-2)),
	G_SHARP9  (14080.0 * semitonesToPitch(-1)),
	A9        (14080.0 * semitonesToPitch(0)),
	B_FLAT9   (14080.0 * semitonesToPitch(1)),
	B9        (14080.0 * semitonesToPitch(2)),
	C10       (28160.0 * semitonesToPitch(-9)),
	C_SHARP10 (28160.0 * semitonesToPitch(-8)),
	D10       (28160.0 * semitonesToPitch(-7)),
	E_FLAT10  (28160.0 * semitonesToPitch(-6)),
	E10       (28160.0 * semitonesToPitch(-5)),
	F10       (28160.0 * semitonesToPitch(-4)),
	F_SHARP10 (28160.0 * semitonesToPitch(-3)),
	G10       (28160.0 * semitonesToPitch(-2)),
	G_SHARP10 (28160.0 * semitonesToPitch(-1)),
	A10       (28160.0 * semitonesToPitch(0)),
	B_FLAT10  (28160.0 * semitonesToPitch(1)),
	B10       (28160.0 * semitonesToPitch(2));
	
	/** Note's concert frequency. */
	private final double frequency;
	
	private Note(double frequency)
	{
		this.frequency = frequency;
	}
	
	/** Returns the concert frequency of this Note. */
	public double getFrequency()
	{
		return frequency;
	}
	
	/** Returns the sampling rate at which this note's frequency deteriorates/aliases. */
	public double getNyquistFrequency()
	{
		return frequency * 2;
	}
	
}
