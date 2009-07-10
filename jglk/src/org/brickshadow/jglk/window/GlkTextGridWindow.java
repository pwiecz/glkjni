package org.brickshadow.jglk.window;


import java.nio.ByteBuffer;
import java.nio.IntBuffer;


/**
 * The base class for text grid windows.
 */
public abstract class GlkTextGridWindow extends GlkTextWindow {
    protected final boolean drawInlineImage(int num, int alignment) {
        return false;
    }
    protected final boolean drawInlineImage(int num, int alignment,
            int width, int height) {
        return false;
    }
    protected final void flowBreak() {}

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
    public abstract void setStyle(int val);
    public abstract int measureStyle(int styl, int hint);
    public abstract boolean distinguishStyles(int styl1, int styl2);
    public abstract void print(String str);
    public abstract void moveCursor(int xpos, int ypos);
}
