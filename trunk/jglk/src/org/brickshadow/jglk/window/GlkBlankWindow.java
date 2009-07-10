package org.brickshadow.jglk.window;


import glkjni.GlkWindow;


/**
 * The base class for blank windows.
 */
public abstract class GlkBlankWindow extends GlkInvisibleWindow {
    protected final void setArrangement(int method, int size,
            GlkWindow key) {}
}
