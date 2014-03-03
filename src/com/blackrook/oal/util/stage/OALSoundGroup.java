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
 * A sound group for OALSoundStage2D.
 * Sounds played within this group apply this group's gain and pitch characteristics
 * combined with the object's characteristics.
 * @author Matthew Tropiano
 */
public interface OALSoundGroup
{
	/**
	 * Gets the gain bias for this group.
	 */
	public float getGainBias();

	/**
	 * Gets the pitch bias for this group.
	 */
	public float getPitchBias();

	/** 
	 * Gets if all sounds played to this group are relative to the listener object. 
	 */
	public boolean isRelative();

	/**
	 * Gets the maximum amount of voices that can be played concurrently in this group.
	 * If this returns 0, there is no max (except for the global maximum). 
	 */
	public int getMaxVoices();

}
