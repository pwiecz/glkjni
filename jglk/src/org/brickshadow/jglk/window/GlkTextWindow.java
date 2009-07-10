package org.brickshadow.jglk.window;

import glkjni.GlkWindow;

abstract class GlkTextWindow extends GlkWindow {
    protected final void setArrangement(int method, int size,
            GlkWindow key) {}

    protected final boolean drawImage(int num, int x, int y) {
        return false;
    }
    protected final boolean drawImage(int num, int x, int y, int width,
            int height) {
        return false;
    }
    protected final void setBackgroundColor(int color) {}
    protected final void eraseRect(int left, int top, int width,
            int height) {}
    protected final void fillRect(int color, int left, int top, int width,
            int height) {}
}
