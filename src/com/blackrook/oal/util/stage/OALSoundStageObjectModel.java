/*******************************************************************************
 * Copyright (c) 2014 Black Rook Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.oal.util.stage;

/**
 * Sound stage model.
 * @author Matthew Tropiano
 */
public interface OALSoundStageObjectModel<T extends Object>
{
	/**
	 * Gets the position, x-axis, of the object source of a sound.
	 */
	public float getSoundPositionX(T object);

	/**
	 * Gets the position, y-axis, of the object source of a sound.
	 */
	public float getSoundPositionY(T object);

	/**
	 * Gets the position, z-axis, of the object source of a sound.
	 */
	public float getSoundPositionZ(T object);

	/**
	 * Gets the velocity, x-component, of the object source of a sound.
	 */
	public float getSoundVelocityX(T object);

	/**
	 * Gets the velocity, y-component, of the object source of a sound.
	 */
	public float getSoundVelocityY(T object);

	/**
	 * Gets the velocity, z-component, of the object source of a sound.
	 */
	public float getSoundVelocityZ(T object);

	/**
	 * Gets the direction vector, x-component, of the object source of a sound.
	 */
	public float getSoundDirectionX(T object);

	/**
	 * Gets the direction vector, y-component, of the object source of a sound.
	 */
	public float getSoundDirectionY(T object);

	/**
	 * Gets the direction vector, z-component, of the object source of a sound.
	 */
	public float getSoundDirectionZ(T object);

}
