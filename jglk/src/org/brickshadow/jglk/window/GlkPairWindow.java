package org.brickshadow.jglk.window;


import glkjni.GlkWindow;


/**
 * The base class for pair windows.
 */
public abstract class GlkPairWindow extends GlkInvisibleWindow {
    public abstract void setArrangement(int method, int size,
            GlkWindow key);
}
