/*******************************************************************************
 * Copyright (c) 2014 Black Rook Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.oal.util;

import java.io.File;

/**
 * Sound file resource loader.
 * @author Matthew Tropiano
 */
public class OALSoundFile extends OALSoundResourceAbstract
{
	/** The resource file. */
	private File file;

	public OALSoundFile(String fileName)
	{
		this(new File(fileName));
	}
	
	public OALSoundFile(File f)
	{
		file = f;
		setLooping(false);
		setNotDoppled(false);
		setNotDirected(false);
		setNotPanned(false);
		setStreaming(false);
		setLimitStopsOldestSound(false);
		setMustBePlayed(false);
	}
	
	@Override
	public String getName()
	{
		return file.getPath().substring(0, file.getPath().lastIndexOf("."));
	}
	
	@Override
	public int hashCode()
	{
		return file.getPath().hashCode();
	}
	
	@Override
	public String getPath()
	{
		return file.getPath();
	}

}
