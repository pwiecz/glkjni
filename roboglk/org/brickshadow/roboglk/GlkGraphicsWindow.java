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
 * The base class for graphics windows. It provides do-nothing
 * implementations of window methods not applicable to graphics windows.
 */
public abstract class GlkGraphicsWindow implements GlkWindow {
    public final void cancelCharEvent() {}
    public final int cancelLineEvent() {
        return 0;
    }
    public final void cancelLinkEvent() {}
    public final boolean distinguishStyles(int styl1, int styl2) {
        return false;
    }
    public final boolean drawInlineImage(int num, int alignment) {
        return false;
    }
    public final boolean drawInlineImage(int num, int alignment,
            int width, int height) {
        return false;
    }
    public final void flowBreak() {}
    public final int measureStyle(int styl, int hint) {
        throw new RuntimeException();
    }
    public final void moveCursor(long xpos, long ypos) {}
    public final void print(String str) {}
    public final void requestCharEvent(boolean unicode) {}
    public final void requestLineEvent(ByteBuffer buf, int maxlen,
            int initlen) {}
    public final void requestLineEventUni(IntBuffer buf, int maxlen,
            int initlen) {}
    public final void requestLinkEvent() {}
    public final void setArrangement(int method, int size,
            GlkWindow key) {}
    public final void setLinkValue(int val) {}
    public final void setStyle(int val) {}
}
