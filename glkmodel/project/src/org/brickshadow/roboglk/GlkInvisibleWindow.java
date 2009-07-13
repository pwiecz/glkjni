package org.brickshadow.roboglk;


import java.nio.ByteBuffer;
import java.nio.IntBuffer;


abstract class GlkInvisibleWindow extends GlkWindow {
    protected final void clear() {}
    protected final void getSize(int[] dim) {}

    protected final void print(String str) {}
    protected final void requestCharEvent(boolean unicode) {}
    protected final void cancelCharEvent() {}
    protected final void requestLineEvent(ByteBuffer buf, int maxlen,
            int initlen) {}
    protected final void requestLineEventUni(IntBuffer buf, int maxlen,
            int initlen) {}
    protected final int cancelLineEvent() {
        return 0;
    }
    protected final void requestLinkEvent() {}
    protected final void cancelLinkEvent() {}
    protected final void setLinkValue(int val) {}
    protected final int measureStyle(int styl, int hint) {
        throw new RuntimeException();
    }
    protected final void setStyle(int val) {}
    protected final boolean distinguishStyles(int styl1, int styl2) {
        return false;
    }

    protected final void moveCursor(int xpos, int ypos) {}

    protected final void requestMouseEvent() {}
    protected final void cancelMouseEvent() {}

    protected final boolean drawInlineImage(int num, int alignment) {
        return false;
    }
    protected final boolean drawInlineImage(int num, int alignment,
            int width, int height) {
        return false;
    }
    protected final void flowBreak() {}

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
