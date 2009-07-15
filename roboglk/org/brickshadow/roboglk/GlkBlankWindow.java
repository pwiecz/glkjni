package org.brickshadow.roboglk;


/**
 * The base class for blank windows. All window methods have a
 * do-nothing implementation.
 * <p>
 * A blank window cannot display output or receive input. Although its
 * size will be returned as zero, it still takes up screen space.
 */
public abstract class GlkBlankWindow extends GlkInvisibleWindow {
    
    /** Does nothing. */
    public final void setArrangement(int method, int size,
            GlkWindow key) {}
}
