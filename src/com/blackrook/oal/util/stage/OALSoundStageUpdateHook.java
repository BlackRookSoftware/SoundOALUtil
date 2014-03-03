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
 * An object that contains a method that is called every update if it is added
 * to the sound stage. This could be useful for sound group control and called
 * whenever {@link OALSoundStage#updateHooks()} is called.
 * @author Matthew Tropiano
 */
public interface OALSoundStageUpdateHook
{
	/** 
	 * Called whenever {@link OALSoundStage#updateHooks()} is called.
	 */
	public void onSoundUpdate();
	
}
