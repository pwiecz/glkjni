package org.brickshadow.roboglk;

/**
 * File usage constants.
 */
public final class GlkFileUsage {
    public static final int Data = 0x00;
    public static final int SavedGame = 0x01;
    public static final int Transcript = 0x02;
    public static final int InputRecord = 0x03;
    public static final int TypeMask = 0x0f;
    public static final int TextMode = 0x100;
    public static final int BinaryMode = 0x000;

    /**
     * Returns true if a usage mask indicates text mode.
     * 
     * @param usage
     *           A usage mask.<p>
     * @return
     *           True if the mask indicates text mode.
     */
    public static boolean isTextMode(int usage) {
        return ((usage & TextMode) != 0);
    }

    /**
     * Extracts the file type from a usage mask.
     * 
     * @param usage
     *           A usage mask.<p>
     * @return
     *           The file type.
     */
    public static int usageType(int usage) {
        int type = usage & TypeMask;
        if (type > InputRecord) {
            type = Data;
        }
        return type;
    }

    private GlkFileUsage() {}
}
