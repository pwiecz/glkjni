package org.brickshadow.roboglk;

/**
 * Return values for {@link Glk#gestalt(int, int, int[])} when
 * the {@code sel} argument is {code GlkGestalt.CharOutput}.
 * <p>
 * It it probably safe to return {@code ExactPrint} for all printable
 * characters.
 */
public interface GlkCharOutput {
    /**
     * Indicates that a character cannot be printed.
     */
    int CannotPrint = 0;
    
    /**
     * Indicates that an approximation of a character will be printed.
     * For example, an accented character may be printed without the
     * accent.
     */
    int ApproxPrint = 1;
    
    /**
     * Indicates that a character can be printed exactly.
     */
    int ExactPrint = 2;
}
