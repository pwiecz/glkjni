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
