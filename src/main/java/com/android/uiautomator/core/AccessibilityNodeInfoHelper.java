/*
 * Copyright (C) 2012 The Android Open Source Project
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

package com.android.uiautomator.core;

import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.display.DisplayManagerGlobal;
import android.util.LongArray;
import android.view.Display;
import android.view.accessibility.AccessibilityNodeInfo;

/**
 * This class contains static helper methods to work with
 * {@link AccessibilityNodeInfo}
 */
public class AccessibilityNodeInfoHelper {

    /**
     * Returns the node's bounds clipped to the size of the display
     *
     * @param node
     * @return null if node is null, else a Rect containing visible bounds
     */
    public static Rect getVisibleBoundsInScreen(AccessibilityNodeInfo node) {
        if (node == null) {
            return null;
        }

        // targeted node's bounds
        Rect nodeRect = new Rect();
        node.getBoundsInScreen(nodeRect);

        Rect displayRect = new Rect();
        Display display = DisplayManagerGlobal.getInstance()
                .getRealDisplay(Display.DEFAULT_DISPLAY);
        Point outSize = new Point();
        display.getSize(outSize);
        displayRect.top = 0;
        displayRect.left = 0;
        displayRect.right = outSize.x;
        displayRect.bottom = outSize.y;

        nodeRect.intersect(displayRect);
        return nodeRect;
    }

    public static int getIndex(AccessibilityNodeInfo node) {
        if (node == null) {
            return -1;
        }

        AccessibilityNodeInfo parent = node.getParent();
        if (parent == null) {
            return 0;
        }

        for (int i = 0; i < parent.getChildCount(); i ++) {
            AccessibilityNodeInfo child = parent.getChild(i);
            if (node.equals(child)) {
                return i;
            }
        }

        return -1;
    }

    public static Long[] getChildIds(AccessibilityNodeInfo node) {
        if (node == null) {
            return null;
        }

        int count = node.getChildCount();
        if (count == 0) {
            return new Long[0];
        }

        Long[] ids = new Long[count];
        LongArray idsLongArray = node.getChildNodeIds();

        for (int i = 0; i < count; i ++) {
            ids[i] = idsLongArray.get(i);
        }

        return ids;
    }

    public static String safeCharSequenceToString(CharSequence sequence) {
        if (sequence == null) { return ""; }
        return sequence.toString();
    }
}