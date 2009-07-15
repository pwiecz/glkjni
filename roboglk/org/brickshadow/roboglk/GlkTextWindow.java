package org.brickshadow.roboglk;


/**
 * The base class for text buffer and text grid windows. It provides
 * do-nothing implementations for methods not applicable to text windows.
 */
abstract class GlkTextWindow implements GlkWindow {
    
    /** Does nothing. */
    public final void setArrangement(int method, int size,
            GlkWindow key) {}

    /** Does nothing. */
    public final boolean drawImage(int num, int x, int y) {
        return false;
    }
    
    /** Does nothing. */
    public final boolean drawImage(int num, int x, int y, int width,
            int height) {
        return false;
    }
    
    /** Does nothing. */
    public final void setBackgroundColor(int color) {}
    
    /** Does nothing. */
    public final void eraseRect(int left, int top, int width,
            int height) {}
    
    /** Does nothing. */
    public final void fillRect(int color, int left, int top, int width,
            int height) {}
}
