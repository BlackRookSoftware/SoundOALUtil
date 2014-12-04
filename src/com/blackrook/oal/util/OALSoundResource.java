/*******************************************************************************
 * Copyright (c) 2014 Black Rook Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.oal.util;

/**
 * Object that describes the elements of a sound to be played in an OALSoundStage.
 * @author Matthew Tropiano
 */
public interface OALSoundResource
{
	/**
	 * Gets the name of this resource.
	 */
	public String getName();

	/**
	 * Gets the locator path of this handle.
	 * Usually this is a file path or URI or classpath or something.
	 */
	public String getPath();
	
	/**
	 * Gets this sound's initial pitch.
	 */
	public float getPitch();

	/**
	 * Gets this sound's initial gain.
	 */
	public float getGain();

	/**
	 * Gets this sound's pitch variance (when the sound is played, the pitch is randomly assigned within this range).
	 */
	public float getPitchVariance();

	/**
	 * Gets this sound's gain variance (when the sound is played, the gain is randomly assigned within this range).
	 */
	public float getGainVariance();

	/**
	 * Gets this sound's rolloff factor for gain attenuation by distance.
	 */
	public float getRolloff();

	/**
	 * Gets this sound's panning deadzone distance 
	 * (if the sound is inside this distance, it is not panned from the listener position).
	 */
	public float getPanningDeadzone();
	
	/**
	 * Gets this sound's attenuation start distance.
	 */
	public float getAttenuationDistance();
	
	/**
	 * Gets this sound's maximum attenuation start distance.
	 * If the rolloff is not 0, the sound gets culled.
	 */
	public float getMaxAttenuationDistance();
	
	/**
	 * Gets this sound's inner cone width for sound projection.
	 */
	public float getInnerConeAngle();
	
	/**
	 * Gets this sound's outer cone width for sound projection.
	 */
	public float getOuterConeAngle();
	
	/**
	 * Gets this sound's outer cone gain for sound projection.
	 */
	public float getOuterConeGain();
	
	/**
	 * Gets this sound's priority. Higher is better.
	 */
	public float getPriority();

	/**
	 * Is this sound meant to be streamed?
	 */
	public boolean isStreaming();

	/**
	 * Is this sound not panned at all (always played at center)?
	 */
	public boolean isNotPanned();

	/**
	 * Is this sound played looping until it is forced to stop?
	 */
	public boolean isLooping();

	/**
	 * Is this sound modulated by Doppler Effect (movement)?
	 */
	public boolean isNotDoppled();
	
	/**
	 * Is this sound modulated by Facing direction?
	 */
	public boolean isNotDirected();
	
	/**
	 * Gets how many of this particular sound can be playing at one time.
	 * If less than 1, there is no limit.
	 */
	public int getLimit();

	/**
	 * If this sound reaches the limit of how many times it is currently being played,
	 * will the additional sound be played with the older of the played sounds getting
	 * stopped?
	 */
	public boolean getStopsOldestSound();

	/**
	 * Must this sound be played, if it is in listenable range,
	 * even when no voices are available? If so, it will play once a voice is free.
	 */
	public boolean isAlwaysPlayed();

}
