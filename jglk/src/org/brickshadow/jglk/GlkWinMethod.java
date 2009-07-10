package org.brickshadow.jglk;

/**
 * Window split method constants.
 * @author sean
 *
 */
public final class GlkWinMethod {
    public static final int Left = 0x00;
    public static final int Right = 0x01;
    public static final int Above = 0x02;
    public static final int Below = 0x03;
    public static final int DirMask = 0x0f;
    public static final int Fixed = 0x10;
    public static final int Proportional = 0x20;
    public static final int DivisionMask = 0xf0;

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
