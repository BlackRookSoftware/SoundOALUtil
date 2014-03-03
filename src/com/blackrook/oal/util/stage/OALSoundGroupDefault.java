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
package com.blackrook.oal.util.stage;

/**
 * A sound group abstract for OALSoundStage2D.
 * Youcan set the characteristics of this sound group.
 * @author Matthew Tropiano
 */
public class OALSoundGroupDefault implements OALSoundGroup
{
	/** Scalar bias for gain level. */
	private float gainBias;
	/** Scalar bias for pitch level. */
	private float pitchBias;
	/** All sounds played to this observer are relative to the listener, if set. */
	private boolean relative;
	/** Maximum amount of voices to use in this group. */
	private int maxVoices;

	/**
	 * Creates a new OALSoundGroup.
	 */
	public OALSoundGroupDefault()
	{
		this(0);
	}

	/**
	 * Creates a new OALSoundGroup.
	 */
	public OALSoundGroupDefault(int maxVoices)
	{
		gainBias = 1.0f;
		pitchBias = 1.0f;
		relative = false;
		this.maxVoices = maxVoices;
	}

	@Override
	public float getGainBias()
	{
		return gainBias;
	}

	/**
	 * Sets the gain bias for this observer.
	 */
	public void setGainBias(float gainBias)
	{
		this.gainBias = gainBias;
	}

	@Override
	public float getPitchBias()
	{
		return pitchBias;
	}

	/**
	 * Sets the pitch bias for this observer.
	 */
	public void setPitchBias(float pitchBias)
	{
		this.pitchBias = pitchBias;
	}

	@Override
	public boolean isRelative()
	{
		return relative;
	}

	/** 
	 * Sets if all sounds played in this group are relative to the listener object. 
	 */
	public void setRelative(boolean relative)
	{
		this.relative = relative;
	}

	@Override
	public int getMaxVoices()
	{
		return maxVoices;
	}

	/**
	 * Sets the maximum amount of voices that can be played concurrently in this group. 
	 */
	public void setMaxVoices(int maxVoices)
	{
		this.maxVoices = maxVoices;
	}
}
