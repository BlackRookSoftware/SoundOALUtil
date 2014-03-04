/*******************************************************************************
 * Copyright (c) 2014 Black Rook Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.oal.util.stage;

/**
 * A sound source to be used by a sound stage.
 * @author Matthew Tropiano
 */
public interface OALSoundStageObject
{
	/**
	 * Gets the X-coordinate center of the object.
	 */
	public float getSoundPositionX();

	/**
	 * Gets the Y-coordinate center of the object.
	 */
	public float getSoundPositionY();

	/**
	 * Gets the Z-coordinate center of the object.
	 */
	public float getSoundPositionZ();
	
	/**
	 * Gets the X-velocity of the object.
	 */
	public float getSoundVelocityX();

	/**
	 * Gets the Y-velocity of the object.
	 */
	public float getSoundVelocityY();

	/**
	 * Gets the Z-velocity of the object.
	 */
	public float getSoundVelocityZ();
	
	/**
	 * Gets the facing direction vector X component.
	 */
	public float getSoundDirectionX();

	/**
	 * Gets the facing direction vector Y component.
	 */
	public float getSoundDirectionY();

	/**
	 * Gets the facing direction vector Z component.
	 */
	public float getSoundDirectionZ();

}
