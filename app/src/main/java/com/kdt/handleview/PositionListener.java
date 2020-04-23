/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
 * This class has been splited from android/widget/Editor$HandleView.java
 */
package com.kdt.handleview;

import android.view.*;

public class PositionListener implements ViewTreeObserver.OnPreDrawListener {
	// 3 handles
	// 3 ActionPopup [replace, suggestion, easyedit] (suggestionsPopup first hides the others)
	// 1 CursorAnchorInfoNotifier
	private final int MAXIMUM_NUMBER_OF_LISTENERS = 7;
	private ViewPositionListener[] mPositionListeners =
	new ViewPositionListener[MAXIMUM_NUMBER_OF_LISTENERS];
	private boolean mCanMove[] = new boolean[MAXIMUM_NUMBER_OF_LISTENERS];
	private boolean mPositionHasChanged = true;
	// Absolute position of the TextView with respect to its parent window
	private int mPositionX, mPositionY;
	private int mNumberOfListeners;
	private boolean mScrollHasChanged;
	final int[] mTempCoords = new int[2];
	private View mView;
	
	public PositionListener(View view) {
		mView = view;
	}

	public void addSubscriber(ViewPositionListener positionListener, boolean canMove) {
		if (mNumberOfListeners == 0) {
			updatePosition();
			ViewTreeObserver vto = mView.getViewTreeObserver();
			vto.addOnPreDrawListener(this);
		}

		int emptySlotIndex = -1;
		for (int i = 0; i < MAXIMUM_NUMBER_OF_LISTENERS; i++) {
			ViewPositionListener listener = mPositionListeners[i];
			if (listener == positionListener) {
				return;
			} else if (emptySlotIndex < 0 && listener == null) {
				emptySlotIndex = i;
			}
		}

		mPositionListeners[emptySlotIndex] = positionListener;
		mCanMove[emptySlotIndex] = canMove;
		mNumberOfListeners++;
	}

	public void removeSubscriber(ViewPositionListener positionListener) {
		for (int i = 0; i < MAXIMUM_NUMBER_OF_LISTENERS; i++) {
			if (mPositionListeners[i] == positionListener) {
				mPositionListeners[i] = null;
				mNumberOfListeners--;
				break;
			}
		}

		if (mNumberOfListeners == 0) {
			ViewTreeObserver vto = mView.getViewTreeObserver();
			vto.removeOnPreDrawListener(this);
		}
	}

	public int getPositionX() {
		return mPositionX;
	}

	public int getPositionY() {
		return mPositionY;
	}

	@Override
	public boolean onPreDraw() {
		updatePosition();

		for (int i = 0; i < MAXIMUM_NUMBER_OF_LISTENERS; i++) {
			if (mPositionHasChanged || mScrollHasChanged || mCanMove[i]) {
				ViewPositionListener positionListener = mPositionListeners[i];
				if (positionListener != null) {
					positionListener.updatePosition(mPositionX, mPositionY,
													mPositionHasChanged, mScrollHasChanged);
				}
			}
		}

		mScrollHasChanged = false;
		return true;
	}

	private void updatePosition() {
		mView.getLocationInWindow(mTempCoords);

		mPositionHasChanged = mTempCoords[0] != mPositionX || mTempCoords[1] != mPositionY;

		mPositionX = mTempCoords[0];
		mPositionY = mTempCoords[1];
	}

	public void onScrollChanged() {
		mScrollHasChanged = true;
	}
}
