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


/**
 * The base class for text buffer and text grid windows. It provides
 * do-nothing implementations for methods not applicable to text windows.
 */
abstract class GlkTextWindow implements GlkWindow {
    
    /** Does nothing. */
    public final void setArrangement(int method, int size,
            GlkWindow key) {}

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
