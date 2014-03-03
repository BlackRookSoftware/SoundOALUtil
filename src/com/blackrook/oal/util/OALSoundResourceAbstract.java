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
package com.blackrook.oal.util;

/**
 * Abstract base class for OALSoundResources.
 * @author Matthew Tropiano
 */
public abstract class OALSoundResourceAbstract implements OALSoundResource
{
	private float priority;
	private float initPitch;
	private float initGain;
	private float pitchVariance;
	private float gainVariance;
	private float rolloff;
	private float panningDeadzone;
	private float attenuationDistance;
	private float maxAttenuationDistance;
	private float innerConeAngle;
	private float outerConeAngle;
	private float outerConeGain;
	private boolean streaming;
	private boolean looping;
	private boolean notDoppled;
	private boolean notDirected;
	private boolean notPanned;
	private boolean limitStop;
	private boolean mustBePlayed;
	private int limit;
	
	protected OALSoundResourceAbstract()
	{
		priority = 0f;
		initPitch = 1f;
		initGain = 1f;
		gainVariance = 0f;
		pitchVariance = 0f;
		rolloff = 1f;
		panningDeadzone = 0f;
		attenuationDistance = 1f;
		maxAttenuationDistance = 2f;
		innerConeAngle = 360f;
		outerConeAngle = 360f;
		outerConeGain = 1f;
		streaming = false;
		looping = false;
		notDoppled = false;
		notDirected = false;
		notPanned = false;
		limitStop = false;
		mustBePlayed = false;
		limit = 0;
	}
	
	/**
	 * Sets this Sound's initial pitch.
	 */
	public void setPriority(float val)
	{
		priority = val;
	}

	/**
	 * Sets this Sound's initial pitch.
	 */
	public void setInitPitch(float val)
	{
		initPitch = val;
	}

	/**
	 * Sets this Sound's initial gain.
	 */
	public void setInitGain(float val)
	{
		initGain = val;
	}

	/**
	 * Sets this Sound's pitch variance (when the sound is played, the pitch is randomly assigned within this range).
	 */
	public void setPitchVariance(float val)
	{
		pitchVariance = val;
	}

	/**
	 * Sets this Sound's gain variance (when the sound is played, the gain is randomly assigned within this range).
	 */
	public void setGainVariance(float val)
	{
		gainVariance = val;
	}

	/**
	 * Sets this sound's rolloff factor.
	 */
	public void setRolloff(float val)
	{
		rolloff = val;
	}

	/**
	 * Gets this sound's panning deadzone distance 
	 * (if the sound is inside this distance, it is not panned from the listener position).
	 */
	public void setPanningDeadzone(float val)
	{
		panningDeadzone = val;
	}

	/**
	 * Gets this sound's attenuation start distance.
	 */
	public void setAttenuationDistance(float val)
	{
		attenuationDistance = val;
	}

	/**
	 * Gets this sound's maximum attenuation start distance.
	 * If the rolloff is not 0, the sound gets culled.
	 */
	public void setMaxAttenuationDistance(float val)
	{
		maxAttenuationDistance = val;
	}

	/**
	 * Gets this sound's inner cone angle for sound projection.
	 */
	public void setInnerConeAngle(float val)
	{
		innerConeAngle = val;
	}

	/**
	 * Gets this sound's outer cone angle for sound projection.
	 */
	public void setOuterConeAngle(float val)
	{
		outerConeAngle = val;
	}

	/**
	 * Gets this sound's outer cone gain for sound projection.
	 */
	public void setOuterConeGain(float val)
	{
		outerConeGain = val;
	}

	/**
	 * Sets how many of this particular sound can be playing at one time.
	 * If less than 1, there is no limit.
	 */
	public void setLimit(int val)
	{
		limit = val;
	}

	/**
	 * Sets if this sound is meant to be streamed.
	 */
	public void setStreaming(boolean val)
	{
		streaming = val;
	}

	/**
	 * Sets if this sound's volume is not panned at all (always played at center).
	 */
	public void setNotPanned(boolean val)
	{
		notPanned = val;
	}

	/**
	 * Sets if this sound is played looping until it is forced to stop.
	 */
	public void setLooping(boolean val)
	{
		looping = val;
	}

	/**
	 * Sets if this sound is NOT modulated by Doppler Effect.
	 */
	public void setNotDoppled(boolean val)
	{
		notDoppled = val;
	}

	/**
	 * Sets if this sound is NOT modulated by facing direction.
	 */
	public void setNotDirected(boolean val)
	{
		notDirected = val;
	}

	/**
	 * Sets if this sound reaches the limit of how many times it is currently being played,
	 * if the additional sound be played with the older of the played sounds getting
	 * stopped.
	 */
	public void setLimitStopsOldestSound(boolean val)
	{
		limitStop = val;
	}

	/**
	 * Sets if this sound must be played, if it is in listenable range,
	 * even when no voices are available? If so, it will play once a voice is free.
	 */
	public void setMustBePlayed(boolean val)
	{
		mustBePlayed = val;
	}

	@Override
	public float getPriority()
	{
		return priority;
	}

	@Override
	public float getPitchVariance()
	{
		return pitchVariance;
	}

	@Override
	public float getGainVariance()
	{
		return gainVariance;
	}

	@Override
	public float getInitGain()
	{
		return initGain;
	}

	@Override
	public float getInitPitch()
	{
		return initPitch;
	}

	@Override
	public float getRolloff()
	{
		return rolloff;
	}
	
	@Override
	public boolean isNotDoppled()
	{
		return notDoppled;
	}

	@Override
	public boolean isNotDirected()
	{
		return notDirected;
	}

	@Override
	public boolean isLooping()
	{
		return looping;
	}

	@Override
	public boolean isNotPanned()
	{
		return notPanned;
	}

	@Override
	public boolean isStreaming()
	{
		return streaming;
	}

	@Override
	public int getLimit()
	{
		return limit;
	}

	@Override
	public boolean limitStopsOldestSound()
	{
		return limitStop;
	}

	@Override
	public boolean mustBePlayed()
	{
		return mustBePlayed;
	}

	@Override
	public float getPanningDeadzone()
	{
		return panningDeadzone;
	}

	@Override
	public float getAttenuationDistance()
	{
		return attenuationDistance;
	}

	@Override
	public float getMaxAttenuationDistance()
	{
		return maxAttenuationDistance;
	}

	@Override
	public float getInnerConeAngle()
	{
		return innerConeAngle;
	}

	@Override
	public float getOuterConeAngle()
	{
		return outerConeAngle;
	}

	@Override
	public float getOuterConeGain()
	{
		return outerConeGain;
	}

}
