package org.brickshadow.roboglk;


/**
 * The base class for pair windows.
 */
public abstract class GlkPairWindow extends GlkInvisibleWindow {
    public abstract void setArrangement(int method, int size,
            GlkWindow key);
}
