package org.brickshadow.jglk.window;


import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import glkjni.GlkWindow;


/**
 * The base class for graphics windows.
 */
public abstract class GlkGraphicsWindow extends GlkWindow {
    protected final void cancelCharEvent() {}
    protected final int cancelLineEvent() {
        return 0;
    }
    protected final void cancelLinkEvent() {}
    protected final boolean distinguishStyles(int styl1, int styl2) {
        return false;
    }
    protected final boolean drawInlineImage(int num, int alignment) {
        return false;
    }
    protected final boolean drawInlineImage(int num, int alignment,
            int width, int height) {
        return false;
    }
    protected final void flowBreak() {}
    protected final int measureStyle(int styl, int hint) {
        throw new RuntimeException();
    }
    protected final void moveCursor(long xpos, long ypos) {}
    protected final void print(String str) {}
    protected final void requestCharEvent(boolean unicode) {}
    protected final void requestLineEvent(ByteBuffer buf, int maxlen,
            int initlen) {}
    protected final void requestLineEventUni(IntBuffer buf, int maxlen,
            int initlen) {}
    protected final void requestLinkEvent() {}
    protected final void setArrangement(int method, int size,
            GlkWindow key) {}
    protected final void setLinkValue(int val) {}
    protected final void setStyle(int val) {}

    public abstract void clear();

    public abstract void getSize(int[] dim);
    public abstract void requestMouseEvent();
    public abstract void cancelMouseEvent();
    public abstract boolean drawImage(int num, int x, int y);
    public abstract boolean drawImage(int num, int x, int y, int width,
            int height);
    public abstract void setBackgroundColor(int color);
    public abstract void eraseRect(int left, int top, int width,
            int height);
    public abstract void fillRect(int color, int left, int top,
            int width, int height);
}
