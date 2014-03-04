/*******************************************************************************
 * Copyright (c) 2014 Black Rook Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.oal.util.dsp;

import com.blackrook.oal.util.SoundUtils;

/**
 * This class describes an abstract representation of a musical chord.
 * Basically, this object stores a set of pitch scalars that when sampled
 * together, create chords, and when sampled successively, create arpeggios. 
 * @author Matthew Tropiano
 */
public class Chord 
{
	/** Basis for a major triad chord. */
	public static final Chord MAJOR_TRIAD = new Chord(0, 4, 7);
	/** Basis for a minor triad chord. */
	public static final Chord MINOR_TRIAD = new Chord(0, 3, 7);
	/** Basis for a diminished triad chord. */
	public static final Chord DIMINISHED_TRIAD = new Chord(0, 3, 6);
	/** Basis for a augmented triad chord. */
	public static final Chord AUGMENTED_TRIAD = new Chord(0, 4, 8);
	
	/** Basis for a diminished seventh chord. */
	public static final Chord DIMINISHED_SEVENTH = new Chord(0, 3, 6, 9);
	/** Basis for a half diminished seventh chord. */
	public static final Chord HALF_DIMINISHED_SEVENTH = new Chord(0, 3, 6, 10);
	/** Basis for a minor seventh chord. */
	public static final Chord MINOR_SEVENTH = new Chord(0, 3, 7, 10);
	/** Basis for a major minor seventh chord. */
	public static final Chord MAJOR_MINOR_SEVENTH = new Chord(0, 3, 7, 11);
	/** Basis for a augmented seventh chord. */
	public static final Chord AUGMENTED_SEVENTH = new Chord(0, 4, 8, 10);
	/** Basis for a dominant seventh chord. */
	public static final Chord DOMINANT_SEVENTH = new Chord(0, 4, 7, 10);
	/** Basis for a major seventh chord. */
	public static final Chord MAJOR_SEVENTH = new Chord(0, 4, 7, 11);
	/** Basis for a augmented major seventh chord. */
	public static final Chord AUGMENTED_MAJOR_SEVENTH = new Chord(0, 4, 8, 11);
	
	/** The set of pitch scalars that make up the inversions of the chord. */
	protected final double[] inversions; 
	
	/**
	 * Creates a new chord from a set of semitone offsets
	 * starting with the root to each successive inversion. 
	 */
	public Chord(int ... semitones)
	{
		inversions = new double[semitones.length];
		for (int i = 0; i < semitones.length; i++)
			inversions[i] = SoundUtils.semitonesToPitch(semitones[i]);
	}
	
	/**
	 * Gets a reference to the array of inversions, pitch scalars,
	 * that make up the chord.
	 */
	public double[] getInversions()
	{
		return inversions;
	}
	
}
