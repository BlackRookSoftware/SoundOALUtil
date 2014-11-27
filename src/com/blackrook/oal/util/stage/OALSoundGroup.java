/*******************************************************************************
 * Copyright (c) 2014 Black Rook Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.oal.util.stage;

/**
 * A sound group for OALSoundStage2D.
 * Sounds played within this group apply this group's gain and pitch characteristics
 * combined with the sound's characteristics.
 * <p>
 * These can also set a parent group that influences the gain and pitch of lower groups.
 * @author Matthew Tropiano
 */
public class OALSoundGroup
{
	/** Parent group. */
	private OALSoundGroup parent;
	
	/** Scalar bias for gain level. */
	private float gainBias;
	/** Scalar bias for pitch level. */
	private float pitchBias;
	/** All sounds played to this observer are relative to the listener, if set. */
	private boolean relative;
	/** Maximum amount of voices to use in this group. */
	private int maxVoices;

	public OALSoundGroup()
	{
		parent = null;
		gainBias = 1f;
		pitchBias = 1f;
		maxVoices = 0;
		relative = true;
	}
	
	/**
	 * Sets this group's parent, if any.
	 */
	public void setParent(OALSoundGroup parent)
	{
		this.parent = parent;
	}
	
	/**
	 * Gets the gain bias for this group (affected by parents, if any).
	 */
	public float getGainBias()
	{
		return (parent != null ? parent.getGainBias() : 1f) * gainBias;
	}

	/**
	 * Sets the gain bias for this group.
	 */
	public void setGainBias(float gainBias)
	{
		this.gainBias = gainBias;
	}

	/**
	 * Gets the pitch bias for this group (affected by parents, if any).
	 */
	public float getPitchBias()
	{
		return (parent != null ? parent.getPitchBias() : 1f) * pitchBias;
	}

	/**
	 * Sets the pitch bias for this group.
	 */
	public void setPitchBias(float pitchBias)
	{
		this.pitchBias = pitchBias;
	}

	/** 
	 * Gets if all sounds played to this group are relative to the listener object. 
	 */
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

	/**
	 * Gets the maximum amount of voices that can be played concurrently in this group.
	 * If this returns 0, there is no max (except for the global maximum). 
	 */
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
