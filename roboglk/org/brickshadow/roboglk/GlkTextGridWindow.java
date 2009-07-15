package org.brickshadow.roboglk;


/**
 * The base class for text grid windows. It provides do-nothing
 * implementations of window methods not applicable to text grid windows.
 */
public abstract class GlkTextGridWindow extends GlkTextWindow {
    
    /** Does nothing. */
    public final boolean drawInlineImage(int num, int alignment) {
        return false;
    }
    
    /** Does nothing. */
    public final boolean drawInlineImage(int num, int alignment,
            int width, int height) {
        return false;
    }
    
    /** Does nothing. */
    public final void flowBreak() {}
}
