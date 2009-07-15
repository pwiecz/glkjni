package org.brickshadow.roboglk;


/**
 * The base class for text buffer windows. It provides do-nothing
 * implementations for window methods not applicable to text buffer
 * windows.
 */
public abstract class GlkTextBufferWindow extends GlkTextWindow {
    
    /** Does nothing. */
    public final void moveCursor(int xpos, int ypos) {}
}
