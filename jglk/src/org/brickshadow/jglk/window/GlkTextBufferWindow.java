package org.brickshadow.jglk.window;


import java.nio.ByteBuffer;
import java.nio.IntBuffer;


/**
 * The base class for text buffer windows.
 */
public abstract class GlkTextBufferWindow extends GlkTextWindow {
    protected final void moveCursor(int xpos, int ypos) {}

    public abstract void clear();
    public abstract void getSize(int[] dim);
    public abstract void requestMouseEvent();
    public abstract void cancelMouseEvent();
    public abstract void requestCharEvent(boolean unicode);
    public abstract void cancelCharEvent();
    public abstract void requestLineEvent(ByteBuffer buf, int maxlen,
            int initlen);
    public abstract void requestLineEventUni(IntBuffer buf, int maxlen,
            int initlen);
    public abstract int cancelLineEvent();
    public abstract void setLinkValue(int val);
    public abstract void requestLinkEvent();
    public abstract void cancelLinkEvent();
    public abstract boolean drawInlineImage(int num, int alignment);
    public abstract boolean drawInlineImage(int num, int alignment,
            int width, int height);
    public abstract void flowBreak();
    public abstract void setStyle(int val);
    public abstract int measureStyle(int styl, int hint);
    public abstract boolean distinguishStyles(int styl1, int styl2);
    public abstract void print(String str);
}
