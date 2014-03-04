/*******************************************************************************
 * Copyright (c) 2014 Black Rook Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.oal.util.stage;

/**
 * A special "sound group" that affects a group of sound groups
 * contained by this class. The gain biases and pitch biases of each 
 * sub group are multiplied by the parent's gain biases and pitch biases.
 * @author Matthew Tropiano
 */
public class OALSoundGroupMulti
{
	/** Scalar bias for gain level. */
	private float gainBias;
	/** Scalar bias for pitch level. */
	private float pitchBias;
	/** All sounds played to this observer are relative to the listener, if set. */
	private boolean relative;
	/** This group's subgroups. */
	private SubGroup[] groups;
	
	/**
	 * Creates a sound group with an initial set of child subgroups.
	 * @param initialGroups the amount of subgroups.
	 */
	public OALSoundGroupMulti(int initialGroups)
	{
		groups = new SubGroup[initialGroups];
		for (int i = 0; i < initialGroups; i++)
			groups[i] = new SubGroup();
	}
	
	/** 
	 * Gets the number of groups that this set controls. 
	 */
	public int getGroupCount()
	{
		return groups.length;
	}
	
	/**
	 * Returns a subgroup by index.
	 * @return the desired group or null if the index provided is less than 0 or outside of the group bounds.
	 */
	public SubGroup getSubGroup(int index)
	{
		if (index < 0 || index >= groups.length)
			return null;
		
		return groups[index];
	}
	
	/**
	 * Gets the gain bias for this group.
	 */
	public float getGainBias()
	{
		return gainBias;
	}

	/**
	 * Sets the gain bias for this group.
	 */
	public void setGainBias(float gainBias)
	{
		this.gainBias = gainBias;
	}

	/**
	 * Gets the pitch bias for this group.
	 */
	public float getPitchBias()
	{
		return pitchBias;
	}

	/**
	 * Sets the pitch bias for this group.
	 */
	public void setPitchBias(float pitchBias)
	{
		this.pitchBias = pitchBias;
	}

	/**
	 * Gets if the groups under this one are relative to the listener.
	 */
	public boolean isRelative()
	{
		return relative;
	}

	/** 
	 * Gets if the groups under this one are relative to the listener.
	 */
	public void setRelative(boolean relative)
	{
		this.relative = relative;
	}

	/**
	 * SubGroup that extends its characteristics from its parent. 
	 */
	protected class SubGroup implements OALSoundGroup
	{
		/** Scalar bias for gain level. */
		private float subGainBias;
		/** Scalar bias for pitch level. */
		private float subPitchBias;

		SubGroup()
		{
			subGainBias = 1.0f;
			subPitchBias = 1.0f;
		}
		
		/** Sets this group's gain bias. */
		public void setGainBias(float bias)
		{
			subGainBias = bias;
		}
		
		/** Sets this group's pitch bias. */
		public void setPitchBias(float bias)
		{
			subPitchBias = bias;
		}
		
		@Override
		public float getGainBias()
		{
			return gainBias * subGainBias;
		}

		public float getSubGainBias()
		{
			return subGainBias;
		}

		@Override
		public float getPitchBias()
		{
			return pitchBias * subPitchBias;
		}

		@Override
		public boolean isRelative()
		{
			return relative;
		}

		@Override
		public int getMaxVoices()
		{
			return 0;
		}

	}
}
