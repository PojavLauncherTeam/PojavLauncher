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
package net.kdt.pojavlaunch.customcontrols.handleview;

import android.graphics.drawable.*;
import android.view.*;

import net.kdt.pojavlaunch.customcontrols.buttons.ControlButton;


public class SelectionEndHandleView extends HandleView {
    public SelectionEndHandleView(ControlButton view) {
        super(view);
    }

    @Override
    protected int getHotspotX(Drawable drawable, boolean isRtlRun) {
        if (isRtlRun) {
            return (drawable.getIntrinsicWidth() * 3) / 4;
        } else {
            return drawable.getIntrinsicWidth() / 4;
        }
    }

    @Override
    protected int getHorizontalGravity(boolean isRtlRun) {
        return isRtlRun ? Gravity.LEFT : Gravity.RIGHT;
    }

    @Override
    public int getCurrentCursorOffset() {
        return 0; // mView.getSelectionEnd();
    }

    public void show(ControlButton button){
        super.show();
        showActionPopupWindow(0, button);
    }

    @Override
    public void updateSelection(int offset) {
        // Selection.setSelection((Spannable) mView.getText(), mView.getSelectionStart(), offset);
        updateDrawable();
    }

    @Override
    public void updatePosition(float x, float y) {
        // updatePosition((int) x, (int) y, false, false);
        positionAtCursorOffset(0, false);
    }

    @Override
    public boolean onLongClick(View view) {
        //TODO stub
        return false;
    }
}

