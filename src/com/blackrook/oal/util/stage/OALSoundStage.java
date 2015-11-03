/*******************************************************************************
 * Copyright (c) 2014 Black Rook Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.oal.util.stage;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.Random;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import com.blackrook.commons.hash.HashMap;
import com.blackrook.commons.hash.HashedQueueMap;
import com.blackrook.commons.linkedlist.Queue;
import com.blackrook.commons.list.List;
import com.blackrook.commons.math.RMath;
import com.blackrook.commons.math.geometry.Point3F;
import com.blackrook.commons.math.geometry.Vect3F;
import com.blackrook.oal.OALBuffer;
import com.blackrook.oal.OALListener;
import com.blackrook.oal.OALSystem;
import com.blackrook.oal.OALSource;
import com.blackrook.oal.OALSourceListener;
import com.blackrook.oal.JSPISoundHandle;
import com.blackrook.oal.enums.DistanceModel;
import com.blackrook.oal.exception.SoundException;
import com.blackrook.oal.util.OALBufferCache;
import com.blackrook.oal.util.OALSoundResource;

/**
 * A sound stage that holds a series of environmental characteristics.
 * that affect the playing of certain sounds sound objects 
 * that one can play sounds from.
 * <p>
 * <b>Once the OpenAL system has been wrapped in this object,
 * do NOT make calls exclusively to OpenAL Sources and Buffers,
 * like allocating or loading data or attaching Buffers to Sources.
 * It will cause stability problems!</b>
 * <p>
 * Please note that "effects" are not supported yet.
 * @author Matthew Tropiano
 */
public class OALSoundStage<T extends Object>
{
	/**
	 * Panning type for event type.
	 */
	public static enum EventType
	{
		PLAY,
		STOP,
		STOP_ALL,
		PAUSE,
		RESUME,
		PRECACHE
	}

	/**
	 * Panning type for sources with sounds that are not panned.
	 */
	public static enum NoPanType
	{
		/** On top of listener. */
		LISTENER,
		/** Front of listener. */
		LISTENER_FRONT;
	}
	
	/** Reference to Sound System. */
	private OALSystem soundSystemRef;
	/** Reference to model. */
	private OALSoundStageObjectModel<T> soundModel;
	
	// Virtual Layers =============================
	
	/** Random number generator. */
	private Random random;
	
	/** List of sound stage listeners. */
	private List<OALSoundStageListener> listeners;
	/** List of sound stage update hooks. */
	private List<OALSoundStageUpdateHook> updateHooks;

	// Control Queues ===========================

	/** List of streams. */
	private Queue<SourceStreamer> streams;
	/** Lookup of "primed" streams. */
	private HashMap<OALSoundResource, SourceStreamer> primedStreams;
	/** List of events to process to next event update. */
	private Queue<StageEvent> eventsToProcess;
	/** List of events to process that couldn't be processed. */
	private Queue<StageEvent> processDelay;

	/** Queue of used voices. */
	private Queue<Voice> usedVoices;
	/** Queue of dead voices. */
	private Queue<Voice> deadVoices;
	/** Queue of available voices. */
	private Queue<Voice> freeVoices;
	
	// Buffer Cache =============================

	/** Buffer cache. */
	private OALBufferCache bufferCache;

	// Playback Coefficients ====================
	
	/** Source "No-Panning" behavior. */
	private NoPanType sourceNoPan;

	/** Listener Master gain. */
	private float listenerGain;
	/** Listener orientation: up vector. */
	private Vect3F listenerUp;
	/** Listener orientation: facing vector. */
	private Vect3F listenerFacing;
	/** Listener orientation: position. */
	private Point3F listenerPosition;
	/** Listener orientation: velocity. */
	private Point3F listenerVelocity;

	private boolean listenerOrientationUpdate;
	private boolean listenerPositionUpdate;
	private boolean listenerGainUpdate;

	// State Objects ============================
	
	/** Sound to voice table. */
	private HashedQueueMap<OALSoundResource, Voice> soundsToVoice;
	/** Group to voice table. */
	private HashedQueueMap<OALSoundGroup, Voice> groupsToVoice; 
	/** Object to voice table. */
	private HashedQueueMap<T, Voice> objectsToVoice; 
	
	/** Is this paused? */
	private boolean allPaused;
	
	/** Update nanos - hooks. */
	private long updateHookNanos;
	/** Update nanos - events. */
	private long updateEventNanos;
	/** Update nanos - listener. */
	private long updateListenerNanos;
	/** Update nanos - voices. */
	private long updateVoiceNanos;
	/** Update nanos - streams. */
	private long updateStreamNanos;
	
	/** Listener placed on all sources. */
	private OALSourceListener SOURCE_LISTENER = new OALSourceListener()
	{
		@Override
		public void sourceBufferDequeued(OALSource source, OALBuffer buffer)
		{
			fireSourceBufferDequeuedEvent(source, buffer);
		}

		@Override
		public void sourceBufferEnqueued(OALSource source, OALBuffer buffer)
		{
			fireSourceBufferEnqueuedEvent(source, buffer);
		}

		@Override
		public void sourcePaused(OALSource source)
		{
			fireSourcePausedEvent(source);
		}

		@Override
		public void sourcePlayed(OALSource source)
		{
			fireSourcePlayedEvent(source);
		}

		@Override
		public void sourceRewound(OALSource source)
		{
			fireSourceRewoundEvent(source);
		}

		@Override
		public void sourceStopped(OALSource source)
		{
			fireSourceStoppedEvent(source);
		}
	};
	
	/** Current voice id. */
	private int currentVoiceId = 0;

		
	/**
	 * Creates a new OALSoundStage2D with UP TO numVoices
	 * voices, as some sound cards may not support that many.
	 * @param sys			reference to OALSoundSystem.
	 * @param numVoices		amount of desired voices.
	 * @param maxCacheBytes	the maximum amount of bytes used for buffer caching (0 or less = no limit).
	 * @throws IllegalArgumentException if numVoices is less than 1.
	 */
	public OALSoundStage(OALSystem sys, OALSoundStageObjectModel<T> model, int numVoices, int maxCacheBytes)
	{
		if (numVoices < 1)
			throw new IllegalArgumentException("The number of voices can't be less than 1.");
		
		random = new Random();
		soundSystemRef = sys;
		soundModel = model;
		bufferCache = new OALBufferCache(maxCacheBytes);
		listeners = new List<OALSoundStageListener>(2);
		updateHooks = new List<OALSoundStageUpdateHook>(2);
		soundsToVoice = new HashedQueueMap<OALSoundResource, Voice>(numVoices);
		groupsToVoice = new HashedQueueMap<OALSoundGroup, Voice>(numVoices);
		objectsToVoice = new HashedQueueMap<T, Voice>(numVoices);
		
		primedStreams = new HashMap<OALSoundResource, SourceStreamer>(3);
		streams = new Queue<SourceStreamer>();
		eventsToProcess = new Queue<StageEvent>();
		processDelay = new Queue<StageEvent>();
		usedVoices = new Queue<Voice>();
		freeVoices = new Queue<Voice>();
		deadVoices = new Queue<Voice>();
		
		sourceNoPan = NoPanType.LISTENER;
		listenerGain = 1.0f;
		listenerUp = new Vect3F(0, 1, 0);
		listenerFacing = new Vect3F(0, 0, -1);
		listenerPosition = new Point3F(0, 0, 0);
		listenerVelocity = new Point3F(0, 0, 0);
		listenerOrientationUpdate = true;
		listenerPositionUpdate = true;
		listenerGainUpdate = true;
		
		setDistanceModel(DistanceModel.INVERSE_DISTANCE_CLAMPED);

		try {
			for (int i = 0; i < numVoices; i++)
			{
				OALSource s = soundSystemRef.createSource();
				s.addSourceListener(SOURCE_LISTENER);
				Voice v = new Voice(s, this);
				freeVoices.add(v);
			}
		} catch (SoundException e) {}
	}
	
	/**
	 * Adds an OALSoundStageListener to this environment.
	 */
	public void addStageListener(OALSoundStageListener listener)
	{
		listeners.add(listener);
	}
	
	/**
	 * Removes an OALSoundStageListener from this environment.
	 */
	public boolean removeStageListener(OALSoundStageListener listener)
	{
		return listeners.remove(listener);
	}
	
	/**
	 * Adds an {@link OALSoundStageUpdateHook} to this environment.
	 */
	public void addUpdateHook(OALSoundStageUpdateHook hook)
	{
		updateHooks.add(hook);
	}
	
	/**
	 * Removes an {@link OALSoundStageUpdateHook} from this environment.
	 */
	public boolean removeUpdateHook(OALSoundStageUpdateHook hook)
	{
		return updateHooks.remove(hook);
	}
	
	/**
	 * Sets the underlying distance attenuation model for this
	 * stage. Sets it on the underlying system.
	 * @param distanceModel the distance model to use.
	 */
	public void setDistanceModel(DistanceModel distanceModel)
	{
		soundSystemRef.setDistanceModel(distanceModel);
	}
	
	/**
	 * Sets the environment's speed of sound.
	 */
	public void setSpeedOfSound(float speed)
	{
		soundSystemRef.setSpeedOfSound(speed);
	}
	
	/**
	 * Sets the environment's Doppler factor.
	 * 0 = disabled.
	 */
	public void setDopplerFactor(float factor)
	{
		soundSystemRef.setDopplerFactor(factor);
	}
	
	/**
	 * Sets how the source is positioned if the sound it is playing is not panned. 
	 */
	public void setNoPanType(NoPanType noPanType)
	{
		sourceNoPan = noPanType;
	}
	
	/**
	 * Sets the master gain. 
	 * Affects the Master Listener object.
	 */
	public void setListenerGain(float gain)
	{
		listenerGain = gain;
		listenerGainUpdate = true;
	}
	
	/**
	 * Sets the position in space for the Master Listener.
	 * @param x the position's X-coordinate.
	 * @param y the position's Y-coordinate.
	 * @param z the position's Z-coordinate.
	 */
	public void setListenerPosition(float x, float y, float z)
	{
		listenerPosition.set(x, y, z);
		listenerPositionUpdate = true;
	}
	
	/**
	 * Sets the velocity for the Master Listener.
	 * @param x the velocity X-component.
	 * @param y the velocity Y-component.
	 * @param z the velocity Z-component.
	 */
	public void setListenerVelocity(float x, float y, float z)
	{
		listenerVelocity.set(x, y, z);
		listenerPositionUpdate = true;
	}
	
	/**
	 * Sets the upward orientation vector for the listener.
	 * @param x the vector X-component.
	 * @param y the vector Y-component.
	 * @param z the vector Z-component.
	 */
	public void setListenerUpwardOrientation(float x, float y, float z)
	{
		listenerUp.set(x, y, z);
		listenerOrientationUpdate = true;
	}
	
	/**
	 * Sets the facing orientation vector for the listener.
	 * @param x the vector X-component.
	 * @param y the vector Y-component.
	 * @param z the vector Z-component.
	 */
	public void setListenerFacingOrientation(float x, float y, float z)
	{
		listenerFacing.set(x, y, z);
		listenerOrientationUpdate = true;
	}
	
	/**
	 * Rotates the Master Listener's orientation about a set of axes.
	 * @param x the amount to rotate the listener about the X-axis in radians.
	 * @param y the amount to rotate the listener about the Y-axis in radians.
	 * @param z the amount to rotate the listener about the Z-axis in radians.
	 */
	public void rotateListener(float x, float y, float z)
	{
		listenerUp.rotateX(x);
		listenerUp.rotateY(y);
		listenerUp.rotateZ(z);
		listenerFacing.rotateX(x);
		listenerFacing.rotateY(y);
		listenerFacing.rotateZ(z);
		listenerOrientationUpdate = true;
	}
	
	/**
	 * Plays a sound resource from no particular object at position (0,0,0).
	 * @param resource the resource to play.
	 */
	public void play(OALSoundResource resource)
	{
		play(resource, null, null, 0, 1f, 1f);
	}
	
	/**
	 * Plays a sound resource from no particular object at position (0,0,0).
	 * @param resource the resource to play.
	 * @param group the group to use to influence playback characteristics.
	 */
	public void play(OALSoundResource resource, OALSoundGroup group)
	{
		play(resource, group, null, 0, resource.getGain(), resource.getPitch());
	}
	
	/**
	 * Plays a sound resource from no particular object at position (0,0,0).
	 * @param resource the resource to play.
	 * @param group the group to use to influence playback characteristics.
	 * @param gain the initial gain (overrides resource's initial gain, but not variance).
	 * @param pitch the initial pitch (overrides resource's initial pitch, but not variance).
	 */
	public void play(OALSoundResource resource, OALSoundGroup group, float gain, float pitch)
	{
		play(resource, group, null, 0, gain, pitch);
	}
	
	/**
	 * Plays a sound resource from an object's default channel (0).
	 * If the object's channel 0 is already bound to a playing voice,
	 * it is stopped and freed before a new voice is reallocated.
	 * Uses resource's initial gain and pitch.
	 * @param resource	the resource to play.
	 * @param object	the object source.
	 */
	public void play(OALSoundResource resource, T object)
	{
		play(resource, null, object, 0, 1f, 1f);
	}
	
	/**
	 * Plays a sound resource from an object's default channel (0).
	 * If the object's channel 0 is already bound to a playing voice,
	 * it is stopped and freed before a new voice is reallocated.
	 * Uses resource's initial gain and pitch.
	 * @param resource	the resource to play.
	 * @param object the object source.
	 * @param group the group to use to influence playback characteristics.
	 */
	public void play(OALSoundResource resource, OALSoundGroup group, T object)
	{
		play(resource, group, object, 0, resource.getGain(), resource.getPitch());
	}
	
	/**
	 * Plays a sound resource from an object.
	 * If the object's specified channel is already bound to a playing voice,
	 * it is stopped and freed before a new voice is reallocated.
	 * @param resource the resource to play.
	 * @param group the group to use to influence playback characteristics.
	 * @param object the object source.
	 * @param channel the object's virtual channel.
	 */
	public void play(OALSoundResource resource, OALSoundGroup group, T object, int channel)
	{
		play(resource, group, object, channel, resource.getGain(), resource.getPitch());
	}

	/**
	 * Plays a sound resource from an object's default channel (0).
	 * If the object's channel 0 is already bound to a playing voice,
	 * it is stopped and freed before a new voice is reallocated.
	 * @param resource the resource to play.
	 * @param object the object source.
	 * @param gain the initial gain (overrides resource's initial gain, but not variance).
	 * @param pitch the initial pitch (overrides resource's initial pitch, but not variance).
	 */
	public void play(OALSoundResource resource, T object, float gain, float pitch)
	{
		play(resource, null, object, 0, gain, pitch);
	}
	
	/**
	 * Plays a sound resource from an object.
	 * If the object's specified channel is already bound to a playing voice,
	 * it is stopped and freed before a new voice is reallocated.
	 * @param resource	the resource to play.
	 * @param object the object source.
	 * @param channel the object's virtual channel.
	 * @param gain the initial gain (overrides resource's initial gain, but not variance).
	 * @param pitch the initial pitch (overrides resource's initial pitch, but not variance).
	 */
	public void play(OALSoundResource resource, T object, int channel, float gain, float pitch)
	{
		play(resource, null, object, channel, gain, pitch);
	}

	/**
	 * Plays a sound resource from an object.
	 * If the object's specified channel is already bound to a playing voice,
	 * it is stopped and freed before a new voice is reallocated.
	 * @param resource	the resource to play.
	 * @param group the group to use to influence playback characteristics.
	 * @param object the object source.
	 * @param gain the initial gain (overrides resource's initial gain, but not variance).
	 * @param pitch the initial pitch (overrides resource's initial pitch, but not variance).
	 */
	public void play(OALSoundResource resource, OALSoundGroup group, T object, float gain, float pitch)
	{
		play(resource, group, object, 0, gain, pitch);
	}

	/**
	 * Plays a sound resource from an object.
	 * If the object's specified channel is already bound to a playing voice,
	 * it is stopped and freed before a new voice is reallocated.
	 * @param resource the resource to play.
	 * @param group the group to use to influence playback characteristics.
	 * @param object the object source.
	 * @param channel the object's virtual channel.
	 * @param gain the initial gain (overrides resource's initial gain, but not variance).
	 * @param pitch the initial pitch (overrides resource's initial pitch, but not variance).
	 */
	public void play(OALSoundResource resource, OALSoundGroup group, T object, int channel, float gain, float pitch)
	{
		StageEvent sn = new StageEvent();
		sn.type = EventType.PLAY;
		sn.resource = resource;
		sn.object = object;
		sn.group = group;
		sn.channel = channel;
		sn.gain = gain;
		sn.pitch = pitch;
		enqueueEvent(sn);
	}

	/**
	 * Stops all objects playing a particular sound resource.
	 * @param resource	the resource to stop.
	 */
	public void stopSound(OALSoundResource resource)
	{
		StageEvent sn = new StageEvent();
		sn.type = EventType.STOP;
		sn.resource = resource;
		enqueueEvent(sn);
	}

	/**
	 * Stops a sound resource playing on an object's first channel (0).
	 * @param object	the object source.
	 */
	public void stopObject(T object)
	{
		StageEvent sn = new StageEvent();
		sn.type = EventType.STOP;
		sn.object = object;
		enqueueEvent(sn);
	}

	/**
	 * Stops a sound resource playing on an object.
	 * @param object	the object source.
	 * @param channel	the object's virtual channel.
	 */
	public void stopObject(T object, int channel)
	{
		StageEvent sn = new StageEvent();
		sn.type = EventType.STOP;
		sn.object = object;
		sn.channel = channel;
		enqueueEvent(sn);
	}

	/**
	 * Stops all sound resources playing in a group.
	 * @param group the group to stop.
	 */
	public void stopGroup(OALSoundGroup group)
	{
		StageEvent sn = new StageEvent();
		sn.type = EventType.STOP;
		sn.group = group;
		enqueueEvent(sn);
	}

	/**
	 * Stops all objects playing any sound.
	 */
	public void stopAll()
	{
		StageEvent sn = new StageEvent();
		sn.type = EventType.STOP_ALL;
		enqueueEvent(sn);
	}

	/**
	 * Pauses playback of all sounds on an object.
	 */
	public void pause(T object)
	{
		StageEvent sn = new StageEvent();
		sn.type = EventType.PAUSE;
		sn.object = object;
		enqueueEvent(sn);
	}
	
	/**
	 * Pauses playback of all sounds.
	 */
	public void pauseAll()
	{
		StageEvent sn = new StageEvent();
		sn.type = EventType.PAUSE;
		enqueueEvent(sn);
	}
	
	/**
	 * Is this currently paused?
	 */
	public boolean isPaused()
	{
		return allPaused;
	}

	/**
	 * Resumes playback of all sounds on an object.
	 */
	public void resume(T object)
	{
		StageEvent sn = new StageEvent();
		sn.type = EventType.RESUME;
		sn.object = object;
		enqueueEvent(sn);
	}
	
	/**
	 * Resumes playback of all sounds.
	 */
	public void resumeAll()
	{
		StageEvent sn = new StageEvent();
		sn.type = EventType.RESUME;
		enqueueEvent(sn);
	}
	
	/**
	 * Updates the voices, hooks, and streamers on this sound stage.
	 * If this is never called, all streaming sources will stop, 
	 * and no new sounds will start playing!
	 * <p><b>NOTE: If this is called, do not call updateHooks(), 
	 * updateListener(), updateVoices(), updateStreams(), or updateEvents() 
	 * in the same tick!</b>
	 */
	public void update()
	{
		updateHooks();
		updateListener();
		updateEvents();
		updateStreams();
		updateVoices();
	}
	
	/**
	 * Updates the OpenAL listener.
	 * Called by update(), but exposed to developers here for
	 * those who want to fine-tune stage update frequencies.
	 * <p>If this is never called, no listener attributes will change.
	 */
	public void updateListener()
	{
		long nanotime = System.nanoTime();
		OALListener listener = soundSystemRef.getListener();
		if (listenerGainUpdate)
		{
			listener.setGain(listenerGain);
			listenerGainUpdate = false;
		}
		if (listenerOrientationUpdate)
		{
			listener.setTop(listenerUp.x, listenerUp.y, listenerUp.z);
			listener.setFacing(listenerFacing.x, listenerFacing.y, listenerFacing.z);
			listenerOrientationUpdate = false;
		}
		if (listenerPositionUpdate)
		{
			listener.setPosition(listenerPosition.x, listenerPosition.y, listenerPosition.z);
			listener.setVelocity(listenerVelocity.x, listenerVelocity.y, listenerVelocity.z);
			listenerPositionUpdate = false;
		}
		updateListenerNanos = System.nanoTime() - nanotime;
	}
	
	/**
	 * Updates the events pending to be processed.
	 * Called by update(), but exposed to developers here for
	 * those who want to fine-tune stage update frequencies.
	 * <p>If this is never called, either by update() or directly,
	 * no new events, like effect changes, sounds to play, or sounds
	 * to stop, will ever be processed.
	 */
	public void updateEvents()
	{
		long nanotime = System.nanoTime();
		while (!processDelay.isEmpty())
			enqueueEvent(processDelay.dequeue());

		while (!eventsToProcess.isEmpty())
		{
			StageEvent event = dequeueEvent();
			switch (event.type)
			{
				case PLAY:
					if (!handlePlayEvent(event))
						processDelay.enqueue(event);
					break;
				case STOP:
					handleStopEvent(event);
					break;
				case STOP_ALL:
					handleStopAllEvent(event);
					break;
				case PAUSE:
					handlePauseEvent(event);
					break;
				case RESUME:
					handleResumeEvent(event);
					break;
				case PRECACHE:
					handlePrecacheEvent(event);
					break;
			}
		}
		cleanUpDeadVoices();
		updateEventNanos = System.nanoTime() - nanotime;
	}
	
	/**
	 * Updates the active streams.
	 * Called by update(), but exposed to developers here for
	 * those who want to fine-tune stage update frequencies.
	 * <p>If this is never called, either by update() or directly,
	 * none of the playing streaming sound data will be updated, and
	 * voices playing streaming sources will terminated unexpectedly.
	 * Streaming data will also not be loaded if this is not called.
	 */
	public void updateStreams()
	{
		long nanotime = System.nanoTime();
		Iterator<SourceStreamer> sit = streams.iterator();
		while (sit.hasNext())
		{
			SourceStreamer stream = sit.next();
			if (!stream.sourceRef.isPlaying() && !stream.sourceRef.isPaused())
			{
				for (OALBuffer buf : stream.sourceRef.dequeueAllBuffers())
				{
					buf.destroy();
					fireSoundReleasedEvent(stream.resourceRef, buf);
				}
				fireSoundStreamStoppedEvent(stream.resourceRef);
				sit.remove();
			}
			else
			{
				try {
					stream.streamUpdate();
				} catch (IOException e) {
					fireErrorIO(stream.resourceRef, e);
					stream.sourceRef.stop();
				} catch (UnsupportedAudioFileException e) {
					fireErrorUnsupportedResource(stream.resourceRef, e);
					stream.sourceRef.stop();
				}
			}
		}
		updateStreamNanos = System.nanoTime() - nanotime;
	}

	/**
	 * Updates the active voices.
	 * Called by update(), but exposed to developers here for
	 * those who want to fine-tune stage update frequencies.
	 * <p>If this is never called, either by update() or directly,
	 * no voice attributes like pitch, panning, or gain attenuation 
	 * will be updated, nor will used voices be freed.
	 */
	public void updateVoices()
	{
		long nanotime = System.nanoTime();
		Iterator<Voice> it = usedVoices.iterator();
		while (it.hasNext())
		{
			Voice voice = it.next();
			if (voice.source.isPlaying() || voice.source.isPaused())
				voice.update();
		}
		
		cleanUpDeadVoices();
		updateVoiceNanos = System.nanoTime() - nanotime;
	}
	
	/**
	 * Updates the sound stage hooks.
	 * Called by update(), but exposed to developers here for
	 * those who want to fine-tune stage update frequencies.
	 * <p>If this is never called, either by update() or directly,
	 * no added hooks will be called.
	 */
	public void updateHooks()
	{
		long nanotime = System.nanoTime();
		for (OALSoundStageUpdateHook hook : updateHooks)
			hook.onSoundUpdate();
		updateHookNanos = System.nanoTime() - nanotime;
	}
	
	/**
	 * Returns the amount of voices managed by this Sound Stage.
	 */
	public int getVoiceCount()
	{
		return getAvailableVoices() + getUsedVoices();
	}

	/**
	 * Returns the amount of voices that are available.
	 */
	public int getAvailableVoices()
	{
		return freeVoices.size();
	}

	/**
	 * Returns the amount of voices that are used.
	 */
	public int getUsedVoices()
	{
		return usedVoices.size();
	}

	/**
	 * Returns the amount of voices playing a specific sound.
	 */
	public int getVoiceCountForSound(OALSoundResource sound)
	{
		return soundsToVoice.containsKey(sound) ? soundsToVoice.get(sound).size() : 0;
	}

	/**
	 * Returns the amount of voices playing connected to a specific object.
	 */
	public int getVoiceCountForObject(T object)
	{
		return objectsToVoice.containsKey(object) ? objectsToVoice.get(object).size() : 0;
	}

	/**
	 * Returns the amount of voices playing connected to a specific group.
	 */
	public int getVoiceCountForGroup(OALSoundGroup group)
	{
		return groupsToVoice.containsKey(group) ? groupsToVoice.get(group).size() : 0;
	}

	/**
	 * Precaches a series of sound resources. Will NOT cache sounds
	 * if they designated as not cacheable or if they are streaming: instead,
	 * they are "primed" - which means that it is prebuffered and ready to be played later.
	 * @param resources	the list of resources to cache.
	 */
	public void cacheSounds(OALSoundResource ... resources)
	{
		for (OALSoundResource resource : resources)
		{
			// streams are "primed," not cached - they are still disposable.
			if (resource.isStreaming())
			{
				try {
					SourceStreamer ss = new SourceStreamer(resource);
					primedStreams.put(resource, ss);
				} catch (UnsupportedAudioFileException e) {
					fireErrorUnsupportedResource(resource, e);
				} catch (IOException e) {
					fireErrorIO(resource, e);
				}
			}
			else
			{
				OALBuffer buf = null; 
				try {
					buf = soundSystemRef.createBuffer(getSoundDataForResource(resource));
					bufferCache.addBuffer(resource, buf);
					fireSoundCachedEvent(resource, buf);
				} catch (UnsupportedAudioFileException e) {
					if (buf != null) buf.destroy();
					fireErrorUnsupportedResource(resource, e);
				} catch (IOException e) {
					if (buf != null) buf.destroy();
					fireErrorIO(resource, e);
				}
			}
		}
	}

	/**
	 * Returns the distance between the listener and an object, according to category rules.
	 */
	public float getDistance(OALSoundGroup cat, T object)
	{
		double x,y,z;
		if (cat.isRelative())
		{
			x = soundModel.getSoundPositionX(object);
			y = soundModel.getSoundPositionY(object);
			z = soundModel.getSoundPositionZ(object);
		}
		else
		{
			OALListener listener = soundSystemRef.getListener();
			Point3F p = listener.getPosition();
			x = soundModel.getSoundPositionX(object) - p.x;
			y = soundModel.getSoundPositionY(object) - p.y;
			z = soundModel.getSoundPositionZ(object) - p.z;
		}
		return (float)Math.sqrt(x*x + y*y + z*z);
	}
	
	/**
	 * Returns the amount of time in nanoseconds that it took to 
	 * complete an event processing update.
	 */
	public long getUpdateEventNanos()
	{
		return updateEventNanos;
	}

	/**
	 * Returns the amount of time in nanoseconds that it took to 
	 * complete a listener update.
	 */
	public long getUpdateListenerNanos()
	{
		return updateListenerNanos;
	}

	/**
	 * Returns the amount of time in nanoseconds that it took to 
	 * complete a voice update.
	 */
	public long getUpdateVoiceNanos()
	{
		return updateVoiceNanos;
	}

	/**
	 * Returns the amount of time in nanoseconds that it took to 
	 * complete a stream update.
	 */
	public long getUpdateStreamNanos()
	{
		return updateStreamNanos;
	}

	/**
	 * Returns the amount of time in nanoseconds that it took to 
	 * complete a sound stage hook update.
	 */
	public long getUpdateHookNanos()
	{
		return updateHookNanos;
	}
	
	/**
	 * Stops all voices, frees all buffers and sources cached.
	 */
	public void shutDown()
	{
		OALBufferCache c = bufferCache;
		bufferCache = null;
		c.destroy();
		if  (!primedStreams.isEmpty())
		{
			Iterator<SourceStreamer> it = primedStreams.valueIterator();
			SourceStreamer ss = null;
			while(it.hasNext())
			{
				ss = it.next();
				for (OALBuffer b : ss.buffers)
					b.destroy();
			}
			primedStreams.clear();
		}
		while (!usedVoices.isEmpty())
		{
			Voice v = usedVoices.dequeue();
			v.source.stop();
			v.source.destroy();
		}
		while (!freeVoices.isEmpty())
		{
			Voice v = freeVoices.dequeue();
			v.source.stop();
			v.source.destroy();
		}
		soundsToVoice.clear();
		objectsToVoice.clear();
		groupsToVoice.clear();
	}

	@Override
	public void finalize() throws Throwable
	{
		shutDown();
		super.finalize();
	}

	/**
	 * Handles a precache event.
	 */
	private void handlePrecacheEvent(StageEvent event)
	{
		if (event.resource != null)
		{
			cacheSounds(event.resource);
		}
	}
	
	/**
	 * Handles a stop all event.
	 */
	private void handleStopAllEvent(StageEvent event)
	{
		for (Voice voice : usedVoices)
		{
			// should get cleaned up on next update.
			voice.source.stop();
		}
	}
	
	/**
	 * Handles a sound stop event.
	 */
	private void handleStopEvent(StageEvent event)
	{
		Queue<Voice> voicesToStop = null;
		
		if (event.group != null)
			voicesToStop = groupsToVoice.get(event.group); 
		else if (event.resource != null)
			voicesToStop = soundsToVoice.get(event.resource);
		else if (event.object != null)
		{
			Voice v = null;
			if (event.channel != null)
				v = getVoiceForObject(event.object, event.channel);
			
			if (v != null)
			{
				// should get cleaned up on next update.
				v.source.stop();
				return;
			}
			
			voicesToStop = objectsToVoice.get(event.object);
		}
		else
			voicesToStop = usedVoices;
		
		if (voicesToStop != null) for (Voice voice : voicesToStop)
		{
			// should get cleaned up on next update.
			voice.source.stop();
		}
	}
	
	/**
	 * Handles a pause event.
	 */
	private void handlePauseEvent(StageEvent event)
	{
		// pause an object's sounds.
		if (event.object != null)
		{
			Queue<Voice> voices = objectsToVoice.get(event.object);
			if (voices != null) for (Voice v : voices)
			{
				v.source.pause();
			}
		}
		// pause all sounds (no object, no sound name).
		else if (!allPaused)
		{
			for (Voice v : usedVoices)
				v.source.pause();
			allPaused = true;
		}
	}

	/**
	 * Handles a resume event.
	 */
	private void handleResumeEvent(StageEvent event)
	{
		// resume an object's sounds.
		if (event.object != null)
		{
			Queue<Voice> voices = objectsToVoice.get(event.object);
			if (voices != null) for (Voice v : voices)
			{
				v.source.play();
			}
		}
		// resume all sounds (no object, no sound name).
		else if (allPaused)
		{
			for (Voice v : usedVoices)
				v.source.play();
			allPaused = false;
		}
	}

	/**
	 * Handles a sound play event.
	 * Returns true if handled, false if this is to be belayed.
	 */
	private boolean handlePlayEvent(StageEvent event)
	{
		Voice voice = null;

		if (cannotPlaySound(event))
			return true;
		cleanUpDeadVoices();
		try{
			if (event.resource.isStreaming())
				voice = getVoiceForStream(event.resource);
			else
				voice = getVoiceForSound(event.resource);
		} catch (IOException e) {
			fireErrorIO(event.resource, e);
			return true;
		} catch (UnsupportedAudioFileException e) {
			fireErrorUnsupportedResource(event.resource, e);
			return true;
		}
		
		if (voice != null)
		{
			voice.initialize(event);
			addBindingsForVoice(voice);
			usedVoices.enqueue(voice);
			voice.source.play();
			fireSoundPlayedEvent(event.resource);
			return true; 
		}
		// voice not allocated
		else if (event.resource.isAlwaysPlayed())
			return false;
		
		return true;
	}
	
	/**
	 * Performs checks on an incoming event to free potential
	 * voices and check if the particular sound on the event shouldn't/can't be played.
	 * @param event the incoming PLAY event.
	 * @return true if this method has decided that this sound cannot be played, false otherwise.
	 */
	private boolean cannotPlaySound(StageEvent event)
	{
		// check if sound is too far away.
		if (event.group != null 
				&& event.resource.getRolloff() != 0f 
				&& getDistance(event.group, event.object) > event.resource.getMaxAttenuationDistance())
			return true;

		Voice voice = null;

		// check if limit reached.
		if (event.resource.getLimit() > 0)
		{
			int count = getVoiceCountForSound(event.resource);
			if (count < event.resource.getLimit())
				return false;
			
			if (event.resource.getStopsOldestSound())
			{
				voice = soundsToVoice.dequeue(event.resource);
				removeVoiceForObject(voice.object, voice);
				removeVoiceForGroup(voice.group, voice);
				resetVoice(voice);
				return false;
			}
			else
				return true;
		}
		// get group limit
		else if (event.group != null 
			&& event.group.getMaxVoices() > 0 
			&& getVoiceCountForGroup(event.group) >= event.group.getMaxVoices())
		{
			return true;
		}
		else
		{
			if (event.object != null)
				voice = getVoiceForObject(event.object, event.channel);
			if (voice != null && voice.sound.getPriority() <= event.resource.getPriority())
			{
				removeVoiceForObjectAndChannel(voice.object, voice.channel);
				removeVoiceForSound(voice.sound, voice);
				removeVoiceForGroup(voice.group, voice);
				resetVoice(voice);
			}
			return false;
		}
	}

	
	/**
	 * Opens an audio stream using a sound resource's path.
	 * This assumes that the path is a file path (this should be
	 * overridden if this is not the case).
	 * @return	an open stream for reading the resource.
	 * @throws IOException 	if an error occurred from the read.
	 * @throws UnsupportedAudioFileException if the path refers to an unsupported audio type.
	 */
	protected AudioInputStream openStreamForAudio(OALSoundResource resource) 
		throws UnsupportedAudioFileException, IOException
	{
		File inFile = new File(resource.getPath());
		if (!inFile.exists())
			throw new IOException("Could not open sound resource '"+resource.getName()+"'.");
		return AudioSystem.getAudioInputStream(inFile);
	}

	/**
	 * Creates a sound data object from a resource.
	 */
	protected JSPISoundHandle getSoundDataForResource(OALSoundResource resource) 
		throws UnsupportedAudioFileException, IOException
	{
		AudioInputStream in = openStreamForAudio(resource);
		if (in == null)
			throw new IOException("Resource "+resource.getPath()+" could not be opened.");
		return new JSPISoundHandle(resource.getPath(), in);
	}

	/**
	 * Ensures that a single thread adds an event to process.
	 */
	protected void enqueueEvent(StageEvent event)
	{
		synchronized (eventsToProcess)
		{
			eventsToProcess.enqueue(event);
		}
	}
	
	/**
	 * Ensures that a single thread removes an event to process.
	 */
	protected StageEvent dequeueEvent()
	{
		StageEvent out = null;
		synchronized (eventsToProcess)
		{
			out = eventsToProcess.dequeue();
		}
		return out;
	}
	
	/**
	 * Gets the voice for an object and channel that is currently playing.
	 * Returns null if it is not found.
	 */
	protected Voice getVoiceForObject(T object, Integer channel)
	{
		if (channel == null)
			return null;
		Queue<Voice> list = objectsToVoice.get(object);
		if (list != null) for (Voice v : list)
		{
			if (v.channel == channel)
				return v;
		}
		return null;
	}

	/**
	 * Gets an available voice with sound data loaded into it.
	 * If the data has been loaded before, it will be obtained from the
	 * internal bank. If this obtains a Voice that already has a sound playing on it,
	 * it will be stopped.
	 * @param resource the sound resource to load, or retrieve if already in memory.
	 * @return	the next available voice ready to play (and with Type and
	 * 			global characteristics added) or NULL if no voice is available 
	 * 			to play the sound.
	 * @throws UnsupportedAudioFileException if the audio file type is not supported.
	 * @throws IOException if the resource couldn't be read.
	 * @throws SoundException if a Buffer can't be allocated.
	 */
	protected Voice getVoiceForSound(OALSoundResource resource) throws UnsupportedAudioFileException, IOException
	{
		Voice out = acquireVoice();
		if (out != null)
		{
			out.reset();
			OALBuffer buf = null;
			if ((buf = bufferCache.getBuffer(resource)) == null)
			{
				cacheSounds(resource);
				buf = bufferCache.getBuffer(resource);
			}
			out.source.setBuffer(buf);
		}
		return out;
	}
	
	/**
	 * Gets an available voice with sound data loaded into it, with two
	 * enqueued buffers for streaming purposes and spawns a thread for
	 * streaming the data once the source on the voice is played.
	 * The data is always pulled from the disk or whatever medium that the
	 * data lies on, and is never cached.
	 * @return	the next available voice ready to play or NULL if no voice is available 
	 * 			to play the stream.
	 * @throws IOException if the resource couldn't be read off of disk.
	 * @throws SoundException if two Buffers can't be allocated.
	 */
	protected Voice getVoiceForStream(OALSoundResource resource)
		throws UnsupportedAudioFileException, IOException
	{
		Voice out = acquireVoice();
		if (out != null)
		{
			out.reset();
			// get if already primed.
			SourceStreamer ss = primedStreams.get(resource);
			if (ss != null)
				primedStreams.removeUsingKey(resource);
			else
				ss = new SourceStreamer(resource);
			ss.attachToSource(out.source);
			streams.add(ss);
			fireSoundStreamStartEvent(resource);				
		}
		return out;
	}
	
	/**
	 * Removes the sound to voice binding.
	 * Returns false if it is not found.
	 */
	protected boolean removeVoiceForSound(OALSoundResource sound, Voice voice)
	{
		if (sound != null)
			return soundsToVoice.removeValue(sound, voice);
		return false;
	}

	/**
	 * Removes the object to voice binding.
	 * Returns false if it is not found.
	 */
	protected boolean removeVoiceForObject(T object, Voice voice)
	{
		if (object != null)
			return objectsToVoice.removeValue(object, voice);
		return false;
	}

	/**
	 * Removes the object and channel binding for a voice.
	 * Returns null if it is not found.
	 */
	protected Voice removeVoiceForObjectAndChannel(T object, int channel)
	{
		Queue<Voice> list = objectsToVoice.get(object);
		if (list == null)
			return null;
		Iterator<Voice> it = list.iterator();
		while (it.hasNext())
		{
			Voice voice = it.next();
			if (voice.channel == channel)
			{
				Voice out = voice;
				it.remove();
				return out;
			}
		}
		return null;
	}

	/**
	 * Removes the voice for a group.
	 * Returns false if it is not found.
	 */
	protected boolean removeVoiceForGroup(OALSoundGroup group, Voice voice)
	{
		if (group != null)
			return groupsToVoice.removeValue(group, voice);
		return false;
	}

	/**
	 * Removes all bindings for voice.
	 */
	protected void removeBindingsForVoice(Voice voice)
	{
		removeVoiceForSound(voice.sound, voice);
		removeVoiceForObject(voice.object, voice);
		removeVoiceForGroup(voice.group, voice);
	}
	
	/**
	 * Adds all bindings for voice.
	 */
	protected void addBindingsForVoice(Voice voice)
	{
		if (voice.sound != null)
			soundsToVoice.enqueue(voice.sound, voice);
		if (voice.object != null)
			objectsToVoice.enqueue(voice.object, voice);
		if (voice.group != null)
			groupsToVoice.enqueue(voice.group, voice);
	}
	
	/**
	 * Gets an unused voice.
	 */
	protected Voice acquireVoice()
	{
		Voice out = null;
	
		if (!freeVoices.isEmpty())
			out = freeVoices.dequeue();
		
		return out;
	}

	/**
	 * Stops a voice.
	 */
	protected void resetVoice(Voice voice)
	{
		voice.object = null;
		voice.channel = -1;
		voice.source.stop();
		voice.source.setBuffer(null);
		fireSoundStoppedEvent(voice.sound);
	}
	
	/**
	 * Finds dead used voices and frees them.
	 */
	protected void cleanUpDeadVoices()
	{
		Iterator<Voice> it = usedVoices.iterator();
		while (it.hasNext())
		{
			Voice voice = it.next();
			if (!voice.source.isPlaying() && !voice.source.isPaused())
			{
				removeVoiceForSound(voice.sound, voice);
				removeVoiceForObject(voice.object, voice);
				removeVoiceForGroup(voice.group, voice);
				deadVoices.enqueue(voice);
			}
		}

		while (!deadVoices.isEmpty())
		{
			Voice deadvoice = deadVoices.dequeue();
			resetVoice(deadvoice);
			usedVoices.remove(deadvoice);
			freeVoices.add(deadvoice);
		}
	}
	
	/**
	 * Fires a sound cached event.
	 */
	protected void fireSoundCachedEvent(OALSoundResource data, OALBuffer buffer)
	{
		for (OALSoundStageListener l : listeners)
			l.soundCached(data, buffer);
	}

	/**
	 * Fires a sound released event.
	 */
	protected void fireSoundReleasedEvent(OALSoundResource data, OALBuffer buffer)
	{
		for (OALSoundStageListener l : listeners)
			l.soundReleased(data, buffer);
	}

	/**
	 * Fires a sound played event.
	 */
	protected void fireSoundPlayedEvent(OALSoundResource data)
	{
		for (OALSoundStageListener l : listeners)
			l.soundPlayed(data);
	}

	/**
	 * Fires a sound stopped event.
	 */
	protected void fireSoundStoppedEvent(OALSoundResource data)
	{
		for (OALSoundStageListener l : listeners)
			l.soundStopped(data);
	}

	/**
	 * Fires a sound stream start event.
	 */
	protected void fireSoundStreamStartEvent(OALSoundResource data)
	{
		for (OALSoundStageListener l : listeners)
			l.soundStreamStarted(data);
	}

	/**
	 * Fires a sound stream stopped event.
	 */
	protected void fireSoundStreamStoppedEvent(OALSoundResource data)
	{
		for (OALSoundStageListener l : listeners)
			l.soundStreamStopped(data);
	}

	/**
	 * Fires a source played event. 
	 */
	protected void fireSourcePlayedEvent(OALSource source)
	{
		for (OALSoundStageListener l : listeners)
			l.sourcePlayed(source);
	}

	/**
	 * Fires a source paused event. 
	 */
	protected void fireSourcePausedEvent(OALSource source)
	{
		for (OALSoundStageListener l : listeners)
			l.sourcePaused(source);
	}

	/**
	 * Fires a source rewound event. 
	 */
	protected void fireSourceRewoundEvent(OALSource source)
	{
		for (OALSoundStageListener l : listeners)
			l.sourceRewound(source);
	}

	/**
	 * Fires a source stopped event. 
	 */
	protected void fireSourceStoppedEvent(OALSource source)
	{
		for (OALSoundStageListener l : listeners)
			l.sourceStopped(source);
	}

	/**
	 * Fires a source buffer dequeued event. 
	 */
	protected void fireSourceBufferDequeuedEvent(OALSource source, OALBuffer buffer)
	{
		for (OALSoundStageListener l : listeners)
			l.sourceBufferDequeued(source, buffer);
	}

	/**
	 * Fires a source buffer enqueued event. 
	 */
	protected void fireSourceBufferEnqueuedEvent(OALSource source, OALBuffer buffer)
	{
		for (OALSoundStageListener l : listeners)
			l.sourceBufferEnqueued(source, buffer);
	}

	/**
	 * Fired when the system reads an unsupported audio file.
	 */
	protected void fireErrorUnsupportedResource(OALSoundResource data, UnsupportedAudioFileException exception)
	{
		for (OALSoundStageListener l : listeners)
			l.errorUnsupportedResource(data, exception);
	}

	/**
	 * Fired when the system fails reading a data resource.
	 */
	protected void fireErrorIO(OALSoundResource data, IOException exception)
	{
		for (OALSoundStageListener l : listeners)
			l.errorIO(data, exception);
	}

	/**
	 * Sound events for synchronizing changes in the sound system.
	 */
	protected class StageEvent
	{		
		public EventType type;
		public OALSoundResource resource;
		public OALSoundGroup group;
		public T object;
		public Integer channel;
		public float gain;
		public float pitch;
		
		public StageEvent()
		{ 
			type = null;
			resource = null;
			group = null;
			object = null;
			channel = null;
			gain = 1.0f;
			pitch = 1.0f;
		}
		
		// for debugging purposes.
		@Override
		public String toString()
		{
			StringBuilder sb = new StringBuilder();
			sb.append("StageEvent");
			switch (type)
			{
				case PLAY:
					sb.append(" PLAY ");
					sb.append(object.toString());
					sb.append(' ');
					sb.append(resource.toString());
					sb.append(' ');
					sb.append(channel);
					sb.append(' ');
					sb.append(gain);
					sb.append(' ');
					sb.append(pitch);
					break;
				case STOP:
					sb.append(" STOP ");
					sb.append(object.toString());
					sb.append(' ');
					sb.append(channel);
					break;
				case STOP_ALL:
					sb.append(" STOP ALL ");
					if (resource != null)
						sb.append(resource.toString());
					break;
				case PAUSE:
					sb.append(" PAUSE");
					break;
				case RESUME:
					sb.append(" RESUME");
					break;
				case PRECACHE:
					sb.append(" PRECACHE ");
					sb.append(resource.toString());
					break;
				/*
				case EFFECT_APPLY:
					sb.append(" APPLY EFFECT ");
					break;
				case EFFECT_REMOVE:
					sb.append(" REMOVE EFFECT ");
					break;
					*/
				default:
					sb.append(" MISC");
					break;
			}
			return sb.toString();
		}
		
	}

	/**
	 * Virtual voices.
	 */
	protected class Voice
	{
		public static final int TYPE_NONE = -1;
		
		/** Voice's id. */
		int id;

		/** Voice's attached OpenAL source. */
		OALSource source;
		/** Voice's sound resource link (sound being played back). */
		OALSoundResource sound;
		/** The group that this sound stage is a part of. */
		OALSoundGroup group;
		/** The object that is the source of the playback. */
		T object;
		/** The virtual channel for this stage. */
		Integer channel;
		/** Initial intended pitch for the voice. */
		float initPitch;
		/** Initial intended gain for the voice. */
		float initGain;
		
		Voice(OALSource s, OALSoundStage<T> stage)
		{
			source = s;
			id = currentVoiceId++;
		}
		
		public void initialize(StageEvent event)
		{
			sound = event.resource;
			object = event.object;
			channel = event.channel;

			float pvar = sound.getPitchVariance();
			float gvar = sound.getGainVariance();
			initGain = event.gain + RMath.randFloat(random, -gvar, gvar);
			initPitch = event.pitch + RMath.randFloat(random, -pvar, pvar);

			if (!sound.isStreaming() && sound.isLooping())
				source.setLooping(true);
			
			group = event.group;

			source.setPosition(0f, 0f, 0f);
			source.setVelocity(0f, 0f, 0f);
			source.setDirection(0f, 0f, 0f);
			source.setRelative(group == null || (group != null && group.isRelative()));
			source.setRolloff(sound.getRolloff());
			
			source.setReferenceDistance(sound.getAttenuationDistance());
			source.setMaxDistance(sound.getMaxAttenuationDistance());
			
			source.setInnerConeAngle(sound.getInnerConeAngle());
			source.setOuterConeAngle(sound.getOuterConeAngle());
			source.setOuterConeGain(sound.getOuterConeGain());
			
			update();
		}
		
		public void update()
		{
			float totalGain = initGain; 
			float totalPitch = initPitch;
			
			if (group != null)
			{
				totalGain *= group.getGainBias();
				totalPitch *= group.getPitchBias();
				
				if (object != null)
				{
					if (!sound.isNotPanned() && 
						(getDistance(group, object) > sound.getPanningDeadzone()))
					{
						source.setPosition(
							soundModel.getSoundPositionX(object),
							soundModel.getSoundPositionY(object), 
							soundModel.getSoundPositionZ(object)
						);
					}
					else
					{
						if (sourceNoPan != null) switch (sourceNoPan)
						{
							default:
							case LISTENER:
								source.setPosition(
									listenerPosition.x, 
									listenerPosition.y, 
									listenerPosition.z
								);
								break;
							case LISTENER_FRONT:
								source.setPosition(
									listenerPosition.x + listenerFacing.x, 
									listenerPosition.y + listenerFacing.y, 
									listenerPosition.z + listenerFacing.z
								);
								break;
						}
						else
							source.setPosition(
								listenerPosition.x, 
								listenerPosition.y, 
								listenerPosition.z
							);
					}

					if (!sound.isNotDoppled())
					{
						source.setVelocity(
							soundModel.getSoundVelocityX(object),
							soundModel.getSoundVelocityY(object), 
							soundModel.getSoundVelocityZ(object)
						);
					}
					
					if (!sound.isNotDirected())
					{
						source.setDirection(
							soundModel.getSoundDirectionX(object),
							soundModel.getSoundDirectionY(object), 
							soundModel.getSoundDirectionZ(object)
						);
					}
				}
			}

			source.setGain(totalGain);
			source.setPitch(totalPitch);
		}

		public void reset()
		{
			if (!source.isStopped())
			{
				source.stop();
				if (sound != null)
					fireSoundStoppedEvent(sound);
			}
			source.reset();
			source.rewind();
		}

		@Override
		@SuppressWarnings({ "rawtypes", "unchecked" })
		public boolean equals(Object obj)
		{
			if (obj instanceof OALSoundStage.Voice)
				return equals((OALSoundStage.Voice)obj);
			else
				return super.equals(obj);
		}
		
		public boolean equals(Voice v)
		{
			return id == v.id;
		}

		@Override
		public String toString()
		{
			StringBuilder sb = new StringBuilder();
			sb.append("VOICE id: ");
			sb.append(id);
			sb.append(" Source OAL Id: ");
			sb.append(source.getALId());
			return sb.toString();
		}
	}
	
	/**
	 * The streamer object made for each streaming source.  
	 */
	protected final class SourceStreamer
	{
		protected OALSource sourceRef;
		protected OALBuffer[] buffers;
		protected OALSoundResource resourceRef;
		protected JSPISoundHandle dataRef;
		protected JSPISoundHandle.Decoder decoderRef;
		protected AudioFormat decoderFormat;

		protected byte[] bytebuffer;
		
		SourceStreamer(OALSoundResource resource) throws UnsupportedAudioFileException, IOException
		{
			resourceRef = resource;
			startDecoder();
			buffers = soundSystemRef.createBuffers(2);
			for (OALBuffer b : buffers)
			{
				b.setSamplingRate((int)decoderFormat.getSampleRate());
				b.setFormatByChannelsAndBits(decoderFormat.getChannels(), decoderFormat.getSampleSizeInBits());
				int l = decoderRef.readPCMBytes(bytebuffer);
				b.loadPCMData(ByteBuffer.wrap(bytebuffer),l);
			}
		}

		public void attachToSource(OALSource source)
		{
			sourceRef = source;
			sourceRef.enqueueBuffers(buffers);
		}
		
		public void startDecoder() throws UnsupportedAudioFileException, IOException
		{
			dataRef = getSoundDataForResource(resourceRef);
			decoderRef = dataRef.getDecoder();
			decoderFormat = decoderRef.getDecodedAudioFormat();
			bytebuffer = new byte[(int)decoderFormat.getSampleRate()*decoderFormat.getChannels()*(decoderFormat.getSampleSizeInBits()/8)];
		}
		
		/**
		 * Returns true if this streamer is not yet attached to a source.
		 */
		public boolean isPrimed()
		{
			return sourceRef == null;
		}
		
		// returns the amount of bytes loaded.
		public int streamUpdate() throws UnsupportedAudioFileException, IOException
		{
			int out = -1;
			int p = sourceRef.getProcessedBufferCount();
			while (p-- > 0 && out != 0)
			{
				OALBuffer b = sourceRef.dequeueBuffer();
				out = decoderRef.readPCMBytes(bytebuffer);
				if (out > 0)
				{
					b.loadPCMData(ByteBuffer.wrap(bytebuffer),out);
					sourceRef.enqueueBuffer(b);
				}
				else if (out == 0 && resourceRef.isLooping())
				{
					startDecoder();
					out = decoderRef.readPCMBytes(bytebuffer);
					if (out > 0)
					{
						b.loadPCMData(ByteBuffer.wrap(bytebuffer),out);
						sourceRef.enqueueBuffer(b);
					}
				}
			}
			return out;
		}
		
	}
	
}
