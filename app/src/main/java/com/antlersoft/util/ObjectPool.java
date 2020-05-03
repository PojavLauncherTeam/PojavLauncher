/**
 * Copyright (C) 2009 Michael A. MacDonald
 */
package com.antlersoft.util;

/**
 * A pool of reusable object of a given type.  You get the object from a Entry, which you get
 * by calling reserve().  When you are done with the object, you call release() passing the Entry.
 * <p>
 * Failing to call release() does not leak memory--but you will not get the benefits
 * of reusing the object. You will run into contention issues if you
 * call release() while still holding a reference to the pool object.
 * @author Michael A. MacDonald
 *
 */
public abstract class ObjectPool<R> {
	public static class Entry<S> {
		S item;
		Entry<S> nextEntry;
		
		Entry(S i, Entry<S> n)
		{
			item = i;
			nextEntry = n;
		}
		
		public S get() {
			return item;
		}
	}
	
	private Entry<R> next;
	public ObjectPool()
	{
		next = null;
	}
	
	public Entry<R> reserve()
	{
		if (next == null)
		{
			next = new Entry<R>(itemForPool(), null);
		}
		Entry<R> result = next;
		next = result.nextEntry;
		result.nextEntry = null;
		
		return result;
	}
	
	public void release(Entry<R> entry)
	{
		entry.nextEntry = next;
		next = entry;
	}
	
	protected abstract R itemForPool();
}
