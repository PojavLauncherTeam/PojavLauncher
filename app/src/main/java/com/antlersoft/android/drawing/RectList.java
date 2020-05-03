/**
 * Copyright (C) 2009 Michael A. MacDonald
 */
package com.antlersoft.android.drawing;

import android.graphics.Rect;

import java.util.ArrayList;

import com.antlersoft.util.ObjectPool;

/**
 * A list of rectangular regions that together represent an area of interest.  Provides
 * a set of operations that apply to the whole area, adding, changing and mutating the
 * rectangles in the list as required.
 * <p>
 * Invariants: None of the rectangles in the list overlap; no pair of rectangles in the list
 * together make a single rectangle (none share a complete side)
 * </p>
 * <p>
 * Instances of this class are not thread safe
 * </p>
 * @author Michael A. MacDonald
 *
 */
public class RectList {
	
	enum OverlapType {
		NONE,
		SAME,
		CONTAINS,
		CONTAINED_BY,
		COALESCIBLE,
		PARTIAL
	}
	
	static final int LEFT = 1;
	static final int TOP_LEFT = 2;
	static final int TOP = 4;
	static final int TOP_RIGHT = 8;
	static final int RIGHT = 16;
	static final int BOTTOM_RIGHT = 32;
	static final int BOTTOM = 64;
	static final int BOTTOM_LEFT = 128;
	
	/**
	 * The part left over when one rectangle is subtracted from another
	 * @author Michael A. MacDonald
	 *
	 */
	static class NonOverlappingPortion
	{
		Rect leftPortion;
		Rect topLeftPortion;
		Rect topPortion;
		Rect topRightPortion;
		Rect rightPortion;
		Rect bottomRightPortion;
		Rect bottomPortion;
		Rect bottomLeftPortion;
		
		int r1Owns;
		int r2Owns;
		int common;
		int adjacent;
		boolean horizontalOverlap;
		boolean verticalOverlap;
		
		Rect coalesced;
		
		NonOverlappingPortion()
		{
			leftPortion = new Rect();
			topLeftPortion = new Rect();
			topPortion = new Rect();
			topRightPortion = new Rect();
			rightPortion = new Rect();
			bottomRightPortion = new Rect();
			bottomPortion = new Rect();
			bottomLeftPortion = new Rect();
			coalesced = new Rect();
		}
		
		void setCornerOwnership(int side1, int side2, int corner)
		{
			int combined = (side1 | side2);
			if ((r1Owns & combined) == combined)
				r1Owns |= corner;
			else if ((r2Owns & combined) == combined)
				r2Owns |= corner;
		}
		
		void setCornerOwnership()
		{
			setCornerOwnership(LEFT,TOP,TOP_LEFT);
			setCornerOwnership(TOP,RIGHT,TOP_RIGHT);
			setCornerOwnership(BOTTOM,RIGHT,BOTTOM_RIGHT);
			setCornerOwnership(BOTTOM,LEFT,BOTTOM_LEFT);
		}
	
		/**
		 * Populates with the borders remaining when r2 is subtracted from r1
		 * @param r1
		 * @param r2
		 * @return
		 */
		OverlapType overlap(Rect r1, Rect r2)
		{
			r1Owns = 0;
			r2Owns = 0;
			common = 0;
			adjacent = 0;
			OverlapType result = OverlapType.NONE;
			horizontalOverlap = false;
			verticalOverlap = false;
			
			if (r1.left < r2.left)
			{
				leftPortion.left = topLeftPortion.left = bottomLeftPortion.left = r1.left;
				if (r2.left < r1.right) {
					leftPortion.right = topLeftPortion.right = bottomLeftPortion.right = topPortion.left = bottomPortion.left = r2.left;
					horizontalOverlap = true;
				} else {
					leftPortion.right = topLeftPortion.right = bottomLeftPortion.right = topPortion.left = bottomPortion.left = r1.right;
					if (r2.left == r1.right)
						adjacent |= LEFT;
				}
				r1Owns |= LEFT;
			}
			else
			{
				leftPortion.left = topLeftPortion.left = bottomLeftPortion.left = r2.left;
				if (r1.left < r2.right) {
					leftPortion.right = topLeftPortion.right = bottomLeftPortion.right = topPortion.left = bottomPortion.left = r1.left;
					horizontalOverlap = true;
				} else {
					leftPortion.right = topLeftPortion.right = bottomLeftPortion.right = topPortion.left = bottomPortion.left = r2.right;
					if ( r1.left == r2.right)
						adjacent |= RIGHT;
				}
				if (r2.left < r1.left)
					r2Owns |= LEFT;
				else
					common |= LEFT;
			}
			if (r1.top < r2.top)
			{
				topPortion.top = topLeftPortion.top = topRightPortion.top = r1.top;
				if (r2.top < r1.bottom) {
					topPortion.bottom = topLeftPortion.bottom = topRightPortion.bottom = leftPortion.top = rightPortion.top = r2.top;
					verticalOverlap = true;
				} else {
					topPortion.bottom = topLeftPortion.bottom = topRightPortion.bottom = leftPortion.top = rightPortion.top = r1.bottom;
					if (r2.top == r1.bottom)
						adjacent |= TOP;
				}
				r1Owns |= TOP;
			}
			else
			{
				topPortion.top = topLeftPortion.top = topRightPortion.top = r2.top;
				if (r1.top < r2.bottom) {
					topPortion.bottom = topLeftPortion.bottom = topRightPortion.bottom = leftPortion.top = rightPortion.top = r1.top;
					verticalOverlap = true;
				} else {
					topPortion.bottom = topLeftPortion.bottom = topRightPortion.bottom = leftPortion.top = rightPortion.top = r2.bottom;
					if (r1.top == r2.bottom)
						adjacent |= BOTTOM;
				}
				if (r2.top < r1.top)
					r2Owns |= TOP;
				else
					common |= TOP;
			}
			if (r1.right > r2.right)
			{
				rightPortion.right = topRightPortion.right = bottomRightPortion.right = r1.right;
				if (r2.right > r1.left) {
					rightPortion.left = topRightPortion.left = bottomRightPortion.left = topPortion.right = bottomPortion.right = r2.right;
					horizontalOverlap = true;
				} else {
					rightPortion.left = topRightPortion.left = bottomRightPortion.left = topPortion.right = bottomPortion.right = r1.left;
					if (r2.right == r1.left)
						adjacent |= RIGHT;
				}
				r1Owns |= RIGHT;
			}
			else
			{
				rightPortion.right = topRightPortion.right = bottomRightPortion.right = r2.right;
				if (r1.right > r2.left) {
					rightPortion.left = topRightPortion.left = bottomRightPortion.left = topPortion.right = bottomPortion.right = r1.right;
					horizontalOverlap = true;
				} else {
					rightPortion.left = topRightPortion.left = bottomRightPortion.left = topPortion.right = bottomPortion.right = r2.left;
					if (r1.right==r2.left)
						adjacent |= LEFT;
				}
				if (r2.right > r1.right)
					r2Owns |= RIGHT;
				else
					common |= RIGHT;
			}
			if (r1.bottom > r2.bottom)
			{
				bottomPortion.bottom = bottomLeftPortion.bottom = bottomRightPortion.bottom = r1.bottom;
				if (r2.bottom > r1.top) {
					bottomPortion.top = bottomLeftPortion.top = bottomRightPortion.top = leftPortion.bottom = rightPortion.bottom = r2.bottom;
					verticalOverlap = true;
				} else {
					bottomPortion.top = bottomLeftPortion.top = bottomRightPortion.top = leftPortion.bottom = rightPortion.bottom = r1.top;
					if (r2.bottom==r1.top)
						adjacent |= BOTTOM;
				}
				r1Owns |= BOTTOM;
			}
			else
			{
				bottomPortion.bottom = bottomLeftPortion.bottom = bottomRightPortion.bottom = r2.bottom;
				if (r1.bottom > r2.top) {
					bottomPortion.top = bottomLeftPortion.top = bottomRightPortion.top = leftPortion.bottom = rightPortion.bottom = r1.bottom;
					verticalOverlap = true;
				} else {
					bottomPortion.top = bottomLeftPortion.top = bottomRightPortion.top = leftPortion.bottom = rightPortion.bottom = r2.top;
					if (r1.bottom==r2.top)
						adjacent |= TOP;
				}
				if (r2.bottom > r1.bottom)
					r2Owns |= BOTTOM;
				else
					common |= BOTTOM;
			}
			if ( common == (LEFT|RIGHT|TOP|BOTTOM))
			{
				result = OverlapType.SAME;
			}
			else if ((common & (LEFT|RIGHT)) == (LEFT | RIGHT) && (verticalOverlap || (adjacent & (TOP | BOTTOM)) != 0))
			{
				result = OverlapType.COALESCIBLE;
				coalesced.left = r1.left;
				coalesced.right = r1.right;
				coalesced.top = topPortion.top;
				coalesced.bottom = bottomPortion.bottom;
			}
			else if ((common & (TOP | BOTTOM)) == (TOP | BOTTOM) && (horizontalOverlap || (adjacent & (LEFT | RIGHT)) != 0))
			{
				result = OverlapType.COALESCIBLE;
				coalesced.left = leftPortion.left;
				coalesced.right = rightPortion.right;
				coalesced.top = r1.top;
				coalesced.bottom = r1.bottom;
			}
			else if (verticalOverlap && horizontalOverlap) {
				if (r2Owns == 0)
				{
					result = OverlapType.CONTAINED_BY;
				}
				else if (r1Owns == 0)
				{
					result = OverlapType.CONTAINS;
				}
				else
				{
					// Partial overlap, non coalescible case
					result = OverlapType.PARTIAL;
					setCornerOwnership();
				}
			}
			return result;
		}
	}
	
	/**
	 * Up to 8 Rect objects 
	 * @author Michael A. MacDonald
	 *
	 */
	static class NonOverlappingRects
	{
		ObjectPool.Entry<Rect>[] rectEntries;
		int count;
		static final int MAX_RECTS = 8;
		
		NonOverlappingRects()
		{
			rectEntries = new ObjectPool.Entry[MAX_RECTS];
		}
		
		private void addOwnedRect(int owner, int direction, ObjectPool<Rect> pool, Rect r)
		{
			if ((owner & direction)==direction)
			{
				ObjectPool.Entry<Rect> entry = pool.reserve();
				rectEntries[count++] = entry;
				entry.get().set(r);
			}
		}
		
		void Populate(NonOverlappingPortion p, ObjectPool<Rect> pool, int owner)
		{
			count = 0;
			for (int i=0; i<MAX_RECTS; i++)
				rectEntries[i] = null;
			addOwnedRect(owner,BOTTOM_LEFT,pool,p.bottomLeftPortion);
			addOwnedRect(owner,BOTTOM,pool,p.bottomPortion);
			addOwnedRect(owner,BOTTOM_RIGHT,pool,p.bottomRightPortion);
			addOwnedRect(owner,BOTTOM,pool,p.bottomPortion);
			addOwnedRect(owner,TOP_RIGHT,pool,p.topRightPortion);
			addOwnedRect(owner,TOP,pool,p.topPortion);
			addOwnedRect(owner,TOP_LEFT,pool,p.topLeftPortion);
			addOwnedRect(owner,LEFT,pool,p.leftPortion);
		}
	}
		
	private ArrayList<ObjectPool.Entry<Rect>> list;
	private ObjectPool<Rect> pool;
	private ObjectPool<NonOverlappingRects> nonOverlappingRectsPool = new ObjectPool<NonOverlappingRects>() {

		/* (non-Javadoc)
		 * @see com.antlersoft.util.ObjectPool#itemForPool()
		 */
		@Override
		protected NonOverlappingRects itemForPool() {
			return new NonOverlappingRects();
		}
		
	};
	private ObjectPool<ArrayList<ObjectPool.Entry<Rect>>> listRectsPool = new ObjectPool<ArrayList<ObjectPool.Entry<Rect>>>() {

		/* (non-Javadoc)
		 * @see com.antlersoft.util.ObjectPool#itemForPool()
		 */
		@Override
		protected ArrayList<Entry<Rect>> itemForPool() {
			return new ArrayList<Entry<Rect>>(NonOverlappingRects.MAX_RECTS);
		}
	};
	private NonOverlappingPortion nonOverlappingPortion;
	
	public RectList(ObjectPool<Rect> pool)
	{
		this.pool = pool;
		list = new ArrayList<ObjectPool.Entry<Rect>>();
		nonOverlappingPortion = new NonOverlappingPortion();
	}
	
	public int getSize()
	{
		return list.size();
	}
	
	public Rect get(int i)
	{
		return list.get(i).get();
	}
	
	/**
	 * Remove all rectangles from the list and release them from the pool
	 */
	public void clear()
	{
		for (int i=list.size()-1; i>=0; i--)
		{
			ObjectPool.Entry<Rect> r = list.get(i);
			pool.release(r);
		}
		list.clear();
	}

	private void recursiveAdd(ObjectPool.Entry<Rect> toAdd, int level)
	{
		if (level>=list.size())
		{
			list.add(toAdd);
			return;
		}
		Rect addRect = toAdd.get();
		ObjectPool.Entry<Rect> thisEntry = list.get(level);
		Rect thisRect = thisEntry.get();
		switch (nonOverlappingPortion.overlap(thisRect, addRect))
		{
		case NONE :
			recursiveAdd(toAdd,level + 1);
			break;
		case SAME :
		case CONTAINS :
			pool.release(toAdd);
			break;
		case CONTAINED_BY :
			pool.release(thisEntry);
			list.remove(level);
			recursiveAdd(toAdd,level);
			break;
		case COALESCIBLE :
			pool.release(thisEntry);
			list.remove(level);
			addRect.set(nonOverlappingPortion.coalesced);
			recursiveAdd(toAdd,0);
			break;
		case PARTIAL :
			pool.release(toAdd);
			ObjectPool.Entry<NonOverlappingRects> rectsEntry = nonOverlappingRectsPool.reserve();
			NonOverlappingRects rects = rectsEntry.get();
			rects.Populate(nonOverlappingPortion,pool,nonOverlappingPortion.r2Owns);
			for (int i=0; i<rects.count; i++)
			{
				recursiveAdd(rects.rectEntries[i],0);
			}
			nonOverlappingRectsPool.release(rectsEntry);
			break;
		}
	}
	
	/**
	 * Add a rectangle to the region of interest
	 * @param toAdd
	 */
	public void add(Rect toAdd)
	{
		// Create a copy of the rect to work with
		ObjectPool.Entry<Rect> entry = pool.reserve();
		Rect r = entry.get();
		r.set(toAdd);
		recursiveAdd(entry,0);
	}
	
	/**
	 * Change the rectangle of interest to include only those portions
	 * that fall inside bounds.
	 * @param bounds
	 */
	public void intersect(Rect bounds)
	{
		int size = list.size();
		ObjectPool.Entry<ArrayList<ObjectPool.Entry<Rect>>> listEntry = listRectsPool.reserve();
		ArrayList<ObjectPool.Entry<Rect>> newList = listEntry.get();
		newList.clear();
		for (int i=0; i<size; i++)
		{
			ObjectPool.Entry<Rect> entry = list.get(i);
			Rect rect = entry.get();
			if (rect.intersect(bounds))
			{
				newList.add(entry);
			}
			else
				pool.release(entry);
		}
		list.clear();
		size = newList.size();
		for (int i=0; i<size; i++)
		{
			recursiveAdd(newList.get(i),0);
		}
		listRectsPool.release(listEntry);
	}
	
	/**
	 * Determines if a rectangle intersects any part of the area of interest
	 * @param r
	 * @return True if r intersects any rectangle in the list
	 */
	public boolean testIntersect(Rect r)
	{
		int l = list.size();
		
		for (int i = 0; i<l; i++)
		{
			if (list.get(i).get().intersects(r.left, r.top, r.right, r.bottom))
				return true;
		}
		return false;
	}
	
	/**
	 * Remove the rectangle from the area of interest.
	 * @param toSubtract
	 */
	public void subtract(Rect toSubtract)
	{
		int size = list.size();
		ObjectPool.Entry<ArrayList<ObjectPool.Entry<Rect>>> listEntry = listRectsPool.reserve();
		ArrayList<ObjectPool.Entry<Rect>> newList = listEntry.get();
		newList.clear();
		for (int i=0; i<size; i++)
		{
			ObjectPool.Entry<Rect> entry = list.get(i);
			Rect rect = entry.get();
			switch (nonOverlappingPortion.overlap(rect, toSubtract))
			{
			case SAME:
				pool.release(entry);
				newList.clear();
				list.remove(i);
				return;
			case CONTAINED_BY:
				pool.release(entry);
				list.remove(i);
				i--;
				size--;
				break;
			case NONE:
				break;
			case COALESCIBLE:
				if (!nonOverlappingPortion.verticalOverlap || ! nonOverlappingPortion.horizontalOverlap)
					break;
			case CONTAINS :
				nonOverlappingPortion.setCornerOwnership();
			case PARTIAL :
			{
				ObjectPool.Entry<NonOverlappingRects> rectsEntry = nonOverlappingRectsPool.reserve();
				NonOverlappingRects rects = rectsEntry.get();
				rects.Populate(nonOverlappingPortion, pool, nonOverlappingPortion.r1Owns);
				pool.release(entry);
				list.remove(i);
				i--;
				size--;
				for (int j=0; j<rects.count; j++)
				{
					newList.add(rects.rectEntries[j]);
				}
				nonOverlappingRectsPool.release(rectsEntry);
			}
			}
		}
		size = newList.size();
		for (int i=0; i<size; i++)
		{
			recursiveAdd(newList.get(i),0);
		}
		listRectsPool.release(listEntry);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb=new StringBuilder();
		sb.append("{\n");
		for (int i=0; i<getSize(); i++)
		{
			sb.append(get(i).toString());
			sb.append("\n");
		}
		sb.append("}");
		return sb.toString();
	}
}
