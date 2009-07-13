package org.brickshadow.roboglk;

public class GlkFactory {
    public static final String storyPath = null;

    public static native boolean startup(Glk glk, String[] args);
    
    public static native int run();
}
