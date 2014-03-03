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

import java.util.Comparator;

import com.blackrook.commons.hash.HashMap;
import com.blackrook.commons.list.ComparatorList;
import com.blackrook.oal.OALBuffer;

/**
 * A Buffer cache used for caching often-used buffers 
 * so that they don't need to be reloaded.
 */
public class OALBufferCache
{
	/** Comparator for resource names. */
	protected static Comparator<Node> NAME_COMPARATOR = new Comparator<Node>()
	{
		@Override
		public int compare(Node n1, Node n2)
		{
			return n1.resource.getPath().compareTo(n2.resource.getPath());
		}
	};
	
	/** Comparator for buffer sizes. */
	protected static Comparator<Node> SIZE_COMPARATOR = new Comparator<Node>()
	{
		@Override
		public int compare(Node n1, Node n2)
		{
			return n2.buffer.getSize() - n1.buffer.getSize();
		}
	};
		
	protected int maxByteSize;
	protected int currBytes;
	
	private ComparatorList<Node> buffersBySize;
	private HashMap<OALSoundResource, Node> buffersByName;
	
	/**
	 * Creates a new buffer cache with a set amount of byte capacity.
	 */
	public OALBufferCache(int maxByteSize)
	{
		this.maxByteSize = maxByteSize;
		currBytes = 0;
		buffersBySize = new ComparatorList<Node>(SIZE_COMPARATOR, 20);
		buffersByName = new HashMap<OALSoundResource,Node>(20);
	}
	
	public void addBuffer(OALSoundResource resource, OALBuffer buffer)
	{
		if (buffersByName.containsKey(resource))
			return;
		
		buffer.getSize();
		currBytes += buffer.getSize();
		while (maxByteSize > 0 && currBytes > maxByteSize)
			removeLargestBuffer().destroy();
		
		Node n = new Node(resource, buffer);
		buffersByName.put(resource, n);
		buffersBySize.add(n);
	}

	/**  
	 * Gets an existing buffer.
	 * Null if not found. 
	 */
	public OALBuffer getBuffer(OALSoundResource resource)
	{
		Node n = buffersByName.get(resource);
		if (n == null)
			return null;
		return n.buffer;
	}
	
	/**
	 * Removes the largest buffer.
	 */
	public OALBuffer removeLargestBuffer()
	{
		if (!buffersBySize.isEmpty())
		{
			Node n = buffersBySize.removeIndex(0);
			currBytes -= n.buffer.getSize();
			buffersByName.removeUsingKey(n.resource);
			return n.buffer;
		}
		return null;
	}

	/**
	 * Destroys all buffers and stuff.
	 */
	public void destroy()
	{
		while (!buffersBySize.isEmpty())
			removeLargestBuffer().destroy();
		buffersByName.clear();
	}
	
	@Override
	public void finalize() throws Throwable
	{
		destroy();
		super.finalize();
	}
	
	/** Node class for combining resources with buffers. */
	public class Node
	{
		OALSoundResource resource;
		OALBuffer buffer;
		
		public Node(OALSoundResource res, OALBuffer buf)
		{
			resource = res;
			buffer = buf;
		}
	}
	
}

