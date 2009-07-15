package org.brickshadow.roboglk;

/**
 * Window split method constants. See
 * {@link Glk#windowOpen(GlkWindow, int, int, int, int, GlkWindow[])}.
 */
public final class GlkWinMethod {
    
    /**
     * The window will be opened to the left of an existing window.
     */
    public static final int Left = 0x00;
    
    /**
     * The window will be opened to the right of an existing window.
     */
    public static final int Right = 0x01;
    
    /**
     * The window will be opened above an existing window.
     */
    public static final int Above = 0x02;
    
    /**
     * The window will be opened below an existing window.
     */
    public static final int Below = 0x03;
    
    private static final int DirMask = 0x0f;
    private static final int DivisionMask = 0xf0;
    
    /**
     * The window will take up a fixed amount of space, measured in
     * lines of text for text windows or pixels for graphics windows.
     */
    public static final int Fixed = 0x10;
    
    /**
     * The window will take up a percentage of the available space.
     */
    public static final int Proportional = 0x20;
    

    /**
     * Returns the direction of a split method mask.
     * 
     * @param method
     *           A split method mask.<p>
     * @return
     *           The direction of the split.
     */
    public static int dir(int method) {
        return method & DirMask;
    }

    /**
     * Returns the division type of a split method mask.
     * 
     * @param method
     *           The split method mask.<p>
     * @return
     *           The division type of the split.
     */
    public static int division(int method) {
        return method & DivisionMask;
    }

    private GlkWinMethod() {}
}
