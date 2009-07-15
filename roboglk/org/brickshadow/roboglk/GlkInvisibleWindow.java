/* This file is a part of roboglk.
 * Copyright (c) 2009 Edward McCardell
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.brickshadow.roboglk;


import java.nio.ByteBuffer;
import java.nio.IntBuffer;


/**
 * A base class for pair windows and blank windows. It provides
 * default do-nothing implementations for nearly all of the window
 * methods.
 */
abstract class GlkInvisibleWindow implements GlkWindow {
    /** Does nothing. */
    
    public final void clear() {}
    
    /** Does nothing. */
    public final void getSize(int[] dim) {}
    
    /** Does nothing. */
    public final void print(String str) {}
    
    /** Does nothing. */
    public final void requestCharEvent(boolean unicode) {}
    
    /** Does nothing. */
    public final void cancelCharEvent() {}
    
    /** Does nothing. */
    public final void requestLineEvent(ByteBuffer buf, int maxlen,
            int initlen) {}
    
    /** Does nothing. */
    public final void requestLineEventUni(IntBuffer buf, int maxlen,
            int initlen) {}
    
    /** Does nothing. */
    public final int cancelLineEvent() {
        return 0;
    }
    
    /** Does nothing. */
    public final void requestLinkEvent() {}
    
    /** Does nothing. */
    public final void cancelLinkEvent() {}
    
    /** Does nothing. */
    public final void setLinkValue(int val) {}
    
    /** Does nothing. */
    public final int measureStyle(int styl, int hint) {
        throw new RuntimeException();
    }
    
    /** Does nothing. */
    public final void setStyle(int val) {}
    
    /** Does nothing. */
    public final boolean distinguishStyles(int styl1, int styl2) {
        return false;
    }
    
    /** Does nothing. */
    public final void moveCursor(int xpos, int ypos) {}

    /** Does nothing. */
    public final void requestMouseEvent() {}
    
    /** Does nothing. */
    public final void cancelMouseEvent() {}

    /** Does nothing. */
    public final boolean drawInlineImage(int num, int alignment) {
        return false;
    }
    
    /** Does nothing. */
    public final boolean drawInlineImage(int num, int alignment,
            int width, int height) {
        return false;
    }
    
    /** Does nothing. */
    public final void flowBreak() {}

    /** Does nothing. */
    public final boolean drawImage(int num, int x, int y) {
        return false;
    }
    
    /** Does nothing. */
    public final boolean drawImage(int num, int x, int y, int width,
            int height) {
        return false;
    }
    
    /** Does nothing. */
    public final void setBackgroundColor(int color) {}
    
    /** Does nothing. */
    public final void eraseRect(int left, int top, int width,
            int height) {}
    
    /** Does nothing. */
    public final void fillRect(int color, int left, int top, int width,
            int height) {}
}
