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

import com.blackrook.commons.linkedlist.Stack;
import com.blackrook.commons.math.RMath;
import com.blackrook.commons.math.wave.WaveForm;
import com.blackrook.commons.math.wave.WaveFormType;

/**
 * A chip simulates a sound producing unit that samples from a wave form
 * in order to be mixed later. Each chip has a set of "effect registers"
 * that affect what gets sampled and what sample is returned. 
 * <p> 
 * Frequency refers to the frequency of the desired note sample. 
 * Pitch refers to a scalar that logarithmically scales the desired frequency by octaves.
 * Gain refers to a scalar used that scales the magnitude of the value sampled 
 * from the current patch.
 * 
 * @author Matthew Tropiano
 */
public class Chip 
{
	/** Reference to current patch. */
	protected Patch currentPatch;
	/** Chip state stack. */
	protected Stack<State> stateStack;
	
	/**
	 * Creates a new chip with no Patch attached to it
	 * and a default state set.
	 */
	public Chip()
	{
		currentPatch = null;
		stateStack = new Stack<State>();
		pushState();
	}
	
	/**
	 * Returns a reference to the current state.
	 */
	public State getState()
	{
		return stateStack.peek();
	}
	
	/**
	 * Pushes a copy of the current chip state onto the stack.
	 */
	public void pushState()
	{
		if (stateStack.isEmpty())
			stateStack.add(new State());
		else
			stateStack.add(new State(getState()));
	}
	
	/**
	 * Pops the current chip state off of the stack.
	 * @return true if there was a state to pop, false otherwise.
	 */
	public boolean popState()
	{
		return stateStack.pop() != null;
	}
	
	/**
	 * Gets the current Patch to sample from.
	 */
	public Patch getPatch()
	{
		return currentPatch;
	}

	/**
	 * Sets the current Patch to sample from.
	 */
	public void setPatch(Patch currentPatch)
	{
		this.currentPatch = currentPatch;
	}


	/**
	 * Samples this chip according to its current state, center panning
	 * and fading, returning a -1.0 to 1.0 value representing waveform amplitude.
	 * @param time the time factor, in seconds.
	 */
	public double getSample(double time)
	{
		return getSample(0.0, 0.0, time);
	}
	
	/**
	 * Samples this chip according to its current state, 
	 * returning a -1.0 to 1.0 value representing waveform amplitude.
	 * @param panning the panning that this is being sampled FROM.
	 * @param time the time factor, in seconds.
	 */
	public double getSample(double panning, double time)
	{
		return getSample(panning, 0.0, time);
	}
	
	/**
	 * Samples this chip according to its current state, 
	 * returning a -1.0 to 1.0 value representing waveform amplitude.
	 * @param panning the panning that this is being sampled FROM.
	 * @param fading the fading that this is being sampled FROM.
	 * @param time the time factor, in seconds.
	 */
	public double getSample(double panning, double fading, double time)
	{
		State state = getState();
		if (stateIsMuteForSample(state, panning, fading, time))
			return 0.0;
		
		// FIXME Frequency tuning function of time or what?
		
		double sampleTime = (time - state.attackTime);
		double frequency = state.frequency * state.getPitchAtTime(sampleTime);
		
		if (frequency == 0.0)
			return 0.0;

		double sample = 0.0;
		
		sample = currentPatch.getSample(frequency, sampleTime);
		sample *= state.getGainAtTime(panning, fading, sampleTime);
		return RMath.clampValue(sample, -1.0, 1.0);
	}
	
	/**
	 * Tests a series of conditions for whether the current state will return a
	 * muted sample.
	 */
	protected boolean stateIsMuteForSample(State state, double panning, double fading, double time)
	{
		return
			currentPatch == null ||
			stateStack.isEmpty() ||
			state == null ||
			state.mute ||
			state.frequency <= 0.0 ||
			state.gainConstant == 0.0 ||
			state.pitchConstant == 0.0 ||
			state.attackTime < 0.0 ||
			Math.abs(panning - state.panning) >= 2.0 ||
			Math.abs(fading - state.fading) >= 2.0;
	}
	
	/**
	 * Modifiable chip state.
	 */
	public class State
	{
		/** Time of last attack in seconds. */
		protected double attackTime;
		/** Current pitch frequency (concert pitch). */
		protected double frequency;
		
		/** Current mute state. */
		protected boolean mute;
		
		/** Current change in gain scalar, constant. */
		protected double gainConstant;
		/** Current change in gain scalar, linear. */
		protected double gainLinear;
		/** Current change in gain scalar, quadratic (change in linear). */
		protected double gainQuad;
	
		/** Current change in pitch scalar, constant. */
		protected double pitchConstant;
		/** Current change in pitch scalar, linear. */
		protected double pitchLinear;
		/** Current change in pitch scalar, quadratic (change in linear). */
		protected double pitchQuad;

		/** Current tremolo waveform (assumed period is 1). */
		protected WaveFormType tremoloWaveForm;
		/** Current tremolo amplitude (waveform attenuation in gain, offset). */
		protected double tremoloOffset;
		/** Current tremolo amplitude (waveform attenuation in gain, amplitude). */
		protected double tremoloAmplitude;
		/** Current tremolo frequency (waveform attenuation in gain, frequency). */
		protected double tremoloFrequency;

		/** Current vibrato waveform (assumed period is 1). */
		protected WaveFormType vibratoWaveForm;
		/** Current vibrato offset (waveform attenuation in pitch, offset). */
		protected double vibratoOffset;
		/** Current vibrato amplitude (waveform attenuation in pitch, amplitude). */
		protected double vibratoAmplitude;
		/** Current vibrato frequency (waveform attenuation in pitch, frequency). */
		protected double vibratoFrequency;

		/** Current chord for combined sampling. */
		protected Chord chord;
		/** Current time separation for chord arpeggiation. */
		protected double chordArpeggiation;
		
		/** Channel panning control (X). */
		protected double panning;
		/** Channel fading control (Y). */
		protected double fading;
		
		/** Creates a new chip state with its defaults set. */
		State()
		{
			reset();
		}

		/** Creates a new chip state by copying an existing one. */
		State(State state)
		{
			attackTime = state.attackTime;
			frequency = state.frequency;
			
			mute = state.mute;
			
			gainConstant = state.gainConstant;
			gainLinear = state.gainLinear;
			gainQuad = state.gainQuad;

			tremoloWaveForm = state.tremoloWaveForm;
			tremoloAmplitude = state.tremoloAmplitude;
			tremoloFrequency = state.tremoloFrequency;
			tremoloOffset = state.tremoloOffset;
			
			pitchConstant = state.pitchConstant;
			pitchLinear = state.pitchLinear;
			pitchQuad = state.pitchQuad;

			vibratoAmplitude = state.vibratoAmplitude;
			vibratoFrequency = state.vibratoFrequency;
			vibratoWaveForm = state.vibratoWaveForm;
			vibratoOffset = state.vibratoOffset;

			chord = state.chord;
			chordArpeggiation = state.chordArpeggiation;
			
			panning = state.panning;
			fading = state.fading;
		}
		
		/**
		 * Resets chip state to its default state.
		 * <ul>
		 * <li>attackTime = 0</li>
		 * <li>frequency = 1</li>
		 * <li>mute = true</li>
		 * <li>gainConst = 1</li>
		 * <li>gainLinear = 0</li>
		 * <li>gainQuad = 0</li>
		 * <li>tremoloAmplitude = 0</li>
		 * <li>tremoloFrequency = 0</li>
		 * <li>tremoloOffset = 0</li>
		 * <li>tremoloWaveForm = SINE</li>
		 * <li>pitchConst = 1</li>
		 * <li>pitchLinear = 0</li>
		 * <li>pitchQuad = 0</li>
		 * <li>vibratoAmplitude = 0</li>
		 * <li>vibratoFrequency = 0</li>
		 * <li>vibratoOffset = 0</li>
		 * <li>vibratoWaveForm = SINE</li>
		 * <li>chord = null</li>
		 * <li>chordArpeggiation = 0</li>
		 * <li>panning = 0</li>
		 * <li>fading = 0</li>
		 * </ul>
		 */
		public void reset()
		{
			attackTime = 0L;
			frequency = 1.0;
			
			mute = true;
			
			gainConstant = 1.0;
			gainLinear = 0.0;
			gainQuad = 0.0;

			pitchConstant = 1.0;
			pitchLinear = 0.0;
			pitchQuad = 0.0;

			tremoloWaveForm = WaveForm.SINE;
			tremoloAmplitude = 0.0;
			tremoloFrequency = 0.0;
			tremoloOffset = 0.0;
			
			vibratoWaveForm = WaveForm.SINE;
			vibratoAmplitude = 0.0;
			vibratoFrequency = 0.0;
			vibratoOffset = 0.0;
			
			chord = null;
			chordArpeggiation = 0.0;

			panning = 0.0;
			fading = 0.0;
		}

		/**
		 * Gets the time of last attack in seconds.
		 */
		public double getAttackTime()
		{
			return attackTime;
		}

		/**
		 * Sets the time of last attack in seconds.
		 */
		public void setAttackTime(double attackTime)
		{
			this.attackTime = attackTime;
		}

		/**
		 * Get current pitch frequency (concert pitch).
		 * This is the base absolute output frequency.
		 */
		public double getFrequency()
		{
			return frequency;
		}

		/**
		 * Set current pitch frequency (concert pitch).
		 * This is the base absolute output frequency.
		 */
		public void setFrequency(double frequency)
		{
			this.frequency = frequency;
		}

		/**
		 * Set current pitch frequency (concert pitch).
		 * This is the base absolute output frequency.
		 */
		public void setFrequency(Note note)
		{
			this.frequency = note.getFrequency();
		}

		/**
		 * Is the chip muted?
		 * If so, then it will only output 0.0 upon sampling.
		 */
		public boolean isMute()
		{
			return mute;
		}

		/**
		 * Sets if this chip is muted.
		 * If so, then it will only output 0.0 upon sampling.
		 */
		public void setMute(boolean mute)
		{
			this.mute = mute;
		}

		/**
		 * Get the current gain attenuation scalar of all samples
		 * from this chip, constant coefficient. A value of 1.0 is no attenuation.
		 */
		public double getGainConstant()
		{
			return gainConstant;
		}

		/**
		 * Set the current gain attenuation scalar of all samples
		 * from this chip, constant coefficient.
		 */
		public void setGainConstant(double gainConstant)
		{
			this.gainConstant = gainConstant;
		}

		/**
		 * Get the current gain attenuation scalar of all samples
		 * from this chip, linear coefficient (constant change over time).
		 */
		public double getGainLinear()
		{
			return gainLinear;
		}

		/**
		 * Set the current gain attenuation scalar of all samples
		 * from this chip, linear coefficient (constant change over time).
		 */
		public void setGainLinear(double gainLinear)
		{
			this.gainLinear = gainLinear;
		}

		/**
		 * Get the current gain attenuation scalar of all samples
		 * from this chip, quadratic coefficient (change in constant change over time).
		 */
		public double getGainQuad()
		{
			return gainQuad;
		}

		/**
		 * Set the current gain attenuation scalar of all samples
		 * from this chip, quadratic coefficient (change in constant change over time).
		 */
		public void setGainQuad(double gainQuad)
		{
			this.gainQuad = gainQuad;
		}

		/**
		 * Returns the waveform to use for sampling tremolo.
		 * Wave is assumed to have a frequency of 1 Hz. 
		 */
		public WaveFormType getTremoloWaveForm()
		{
			return tremoloWaveForm;
		}

		/**
		 * Sets the waveform to use for sampling tremolo.
		 * Wave is assumed to have a frequency of 1 Hz. 
		 */
		public void setTremoloWaveForm(WaveFormType tremoloWaveForm)
		{
			this.tremoloWaveForm = tremoloWaveForm;
		}

		/**
		 * Gets the current gain variance amplitude of all samples
		 * from this chip (tremolo).
		 */
		public double getTremoloAmplitude()
		{
			return tremoloAmplitude;
		}

		/**
		 * Sets the current gain variance amplitude of all samples
		 * from this chip (tremolo).
		 */
		public void setTremoloAmplitude(double tremoloAmplitude)
		{
			this.tremoloAmplitude = tremoloAmplitude;
		}

		/**
		 * Gets the current gain variance frequency of all samples
		 * from this chip (tremolo). Setting this to 0 or less turns it off.
		 */
		public double getTremoloFrequency()
		{
			return tremoloFrequency;
		}

		/**
		 * Sets the current gain variance frequency of all samples
		 * from this chip (tremolo). Setting this to 0 or less turns it off.
		 */
		public void setTremoloFrequency(double tremoloFrequency)
		{
			this.tremoloFrequency = tremoloFrequency;
		}

		/**
		 * Gets the current gain variance offset of all samples
		 * from this chip (tremolo).
		 */
		public double getTremoloOffset()
		{
			return tremoloOffset;
		}

		/**
		 * Sets the current gain variance offset of all samples
		 * from this chip (tremolo).
		 */
		public void setTremoloOffset(double tremoloOffset)
		{
			this.tremoloOffset = tremoloOffset;
		}

		/**
		 * Gets the current pitch adjustment scalar of all samples
		 * from this chip, constant coefficient.
		 * A value of 1.0 is no adjustment. Scalar is in octaves, so
		 * 2.0 would be up one octave, 0.5 would be down one, et cetera.
		 */
		public double getPitchConstant()
		{
			return pitchConstant;
		}

		/**
		 * Sets the current pitch adjustment scalar of all samples
		 * from this chip, constant coefficient.
		 */
		public void setPitchConstant(double pitchConstant)
		{
			this.pitchConstant = pitchConstant;
		}

		/**
		 * Gets the current pitch adjustment scalar of all samples
		 * from this chip, linear coefficient (constant change over time).
		 */
		public double getPitchLinear()
		{
			return pitchLinear;
		}

		/**
		 * Sets the current pitch adjustment scalar of all samples
		 * from this chip, linear coefficient (constant change over time).
		 */
		public void setPitchLinear(double pitchLinear)
		{
			this.pitchLinear = pitchLinear;
		}

		/**
		 * Gets the current pitch adjustment scalar of all samples
		 * from this chip, quadratic coefficient (change in constant change over time).
		 */
		public double getPitchQuad()
		{
			return pitchQuad;
		}

		/**
		 * Sets the current pitch adjustment scalar of all samples
		 * from this chip, quadratic coefficient (change in constant change over time).
		 */
		public void setPitchQuad(double pitchQuad)
		{
			this.pitchQuad = pitchQuad;
		}

		/**
		 * Returns the waveform to use for sampling vibrato.
		 * Wave is assumed to have a frequency of 1 Hz. 
		 */
		public WaveFormType getVibratoWaveForm()
		{
			return vibratoWaveForm;
		}

		/**
		 * Sets the waveform to use for sampling vibrato.
		 * Wave is assumed to have a frequency of 1 Hz. 
		 */
		public void setVibratoWaveForm(WaveFormType vibratoWaveForm)
		{
			this.vibratoWaveForm = vibratoWaveForm;
		}

		/**
		 * Gets the current pitch variance amplitude of all samples
		 * from this chip (vibrato).
		 */
		public double getVibratoAmplitude()
		{
			return vibratoAmplitude;
		}

		/**
		 * Sets the current pitch variance amplitude of all samples
		 * from this chip (vibrato).
		 */
		public void setVibratoAmplitude(double vibratoAmplitude)
		{
			this.vibratoAmplitude = vibratoAmplitude;
		}

		/**
		 * Gets the current pitch variance frequency of all samples
		 * from this chip (vibrato). Setting this to 0 or less turns it off.
		 */
		public double getVibratoFrequency()
		{
			return vibratoFrequency;
		}

		/**
		 * Sets the current pitch variance frequency of all samples
		 * from this chip (vibrato). Setting this to 0 or less turns it off.
		 */
		public void setVibratoFrequency(double vibratoFrequency)
		{
			this.vibratoFrequency = vibratoFrequency;
		}

		/**
		 * Gets the current pitch variance offset of all samples
		 * from this chip (vibrato).
		 */
		public double getVibratoOffset()
		{
			return vibratoOffset;
		}

		/**
		 * Sets the current pitch variance offset of all samples
		 * from this chip (vibrato).
		 */
		public void setVibratoOffset(double vibratoOffset)
		{
			this.vibratoOffset = vibratoOffset;
		}

		/**
		 * Gets the chord effect on this chip state.
		 * If this is null, it's off.
		 */
		public Chord getChord()
		{
			return chord;
		}

		/**
		 * Sets the chord effect on this chip state.
		 * If this is null, it's off.
		 */
		public void setChord(Chord chord)
		{
			this.chord = chord;
		}

		/**
		 * Gets the chord arpeggiation time modulo.
		 * If this is 0 or less, this is turned off and the 
		 * chord, if any, is played harmoniously.
		 */
		public double getChordArpeggiation()
		{
			return chordArpeggiation;
		}

		/**
		 * Sets the chord arpeggiation time modulo.
		 * If this is 0 or less, this is turned off and the 
		 * chord, if any, is played harmoniously.
		 */
		public void setChordArpeggiation(double chordArpeggiation)
		{
			this.chordArpeggiation = chordArpeggiation;
		}

		/**
		 * Gets the current panning. Affects the gain of specific channels depending
		 * on its value. A value of -1 is completely left, 0 is center (both), and 1 is
		 * completely right.
		 */
		public double getPanning()
		{
			return panning;
		}

		/**
		 * Sets the current panning. Affects the gain of specific channels depending
		 * on its value. A value of -1 is completely left, 0 is center (both), and 1 is
		 * completely right.
		 */
		public void setPanning(double panning)
		{
			this.panning = panning;
		}

		/**
		 * Gets the current fading. Affects the gain of specific channels depending
		 * on its value. A value of -1 is completely back, 0 is center (both), and 1 is
		 * completely front.
		 */
		public double getFading()
		{
			return fading;
		}

		/**
		 * Sets the current fading. Affects the gain of specific channels depending
		 * on its value. A value of -1 is completely back, 0 is center (both), and 1 is
		 * completely front.
		 */
		public void setFading(double fading)
		{
			this.fading = fading;
		}

		/**
		 * Is the arpeggio mode active, based on chip state?
		 */
		public boolean isArpeggioActive()
		{
			return chord != null && chordArpeggiation > 0.0;
		}
		
		/**
		 * Is the chord mode active, based on chip state?
		 */
		public boolean isChordActive()
		{
			return chord != null && chordArpeggiation <= 0.0;
		}
		
		/**
		 * Is the tremolo mode active, based on chip state?
		 */
		public boolean isTremoloActive()
		{
			return tremoloWaveForm != null &&  tremoloFrequency > 0.0 &&  tremoloAmplitude != 0.0;
		}
		
		/**
		 * Is the vibrato mode active, based on chip state?
		 */
		public boolean isVibratoActive()
		{
			return vibratoWaveForm != null &&  vibratoFrequency > 0.0 &&  vibratoAmplitude != 0.0;
		}
		
		/**
		 * Returns the gain scalar for a designated time.
		 * @param pan the panning that this is being sampled FROM.
		 * @param fade the fading that this is being sampled FROM.
		 * @param time the time factor.
		 * @return the total pitch scalar for the provided parameters.
		 */
		public double getGainAtTime(double pan, double fade, double time)
		{
			double out = (gainConstant + gainLinear*time + gainQuad*time*time);
			if (isTremoloActive())
				out += (tremoloAmplitude * (
						tremoloWaveForm.getSample(time * tremoloFrequency) / tremoloWaveForm.getAmplitude()) + 
						tremoloOffset);
			double p = Math.abs(pan - panning);
			double f = Math.abs(fade - fading);
			p = p > 1.0 ? p - 1.0 : 1.0;
			f = f > 1.0 ? f - 1.0 : 1.0;
			if (p != 1.0 && f != 1.0)
				out *= Math.sqrt(p*p + f*f);
			else if (p != 1.0)
				out *= p;
			else if (f != 1.0)
				out *= f;
			return Math.max(out, 0);
		}

		/**
		 * Returns the pitch scalar for a designated time.
		 * @param time the time factor.
		 * @return the total pitch scalar for the provided parameters.
		 */
		public double getPitchAtTime(double time)
		{
			double out = (pitchConstant + pitchLinear*time + pitchQuad*time*time);
			if (isVibratoActive())
				out += (vibratoAmplitude * 
						(vibratoWaveForm.getSample(time * vibratoFrequency) / vibratoWaveForm.getAmplitude()) + 
						vibratoOffset);
			
			if (isArpeggioActive())
			{
				double arptime = chordArpeggiation * chord.inversions.length;
				out += chord.inversions[(int)((time % arptime) / chordArpeggiation) % chord.inversions.length]; 
			}
			
			return Math.max(out, 0);
		}
		
	}
	
	
}
