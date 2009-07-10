package glkjni;


/**
 * An example of a class which meets defines the methods required by
 * {@code glkjni.GlkFactory}.
 */
public class ExampleGlkFactory {
    /**
     * The absolute path of the story file. This is set from GlkJNI
     * in response to a call to {@code glkext_set_story_path}, and will
     * not be valid until after {@link #startup(Glk, String[])) is
     * called.
     */
    public static final String storyPath = null;

    /**
     * Calls the C startup code of a Glk program. This should not
     * be called from anywhere except {@link #newInstance(String[])}.
     * <p>
     * All interactions between GlkJNI and this Java frontend will
     * occur on the thread that this method is called from.
     * <p>
     * The implementation of this method is provided by the GlkJNI
     * library.
     * 
     * @param glk
     *           The {@link Glk} object that handles calls from C code
     *           into the Java frontend.<p>
     * @param args
     *           The arguments to pass to the program. The first element
     *           of this array should be the name used to start the
     *           program, as in C.
     * @return
     *           True if the C startup code succeeded.
     */
    public static native boolean startup(Glk glk, String[] args);

    /**
     * Called by GlkJNI at the start of program execution.
     * <p>
     * This method takes the place of the {@code main()} method
     * in a standalone Java program. Along with any initialization
     * and setup it needs to perform for the Java frontend, it must
     * create an appropriate instance of an implementation of {@link Glk}
     * and call the {@link #startup(Glk, String[])} method.
     * <p>
     * It may need to process frontend-specific command-line arguments;
     * these should not be included in the array passed to
     * {@link #startup(Glk, String[])}.
     * 
     * @param args
     *           All of the command-line arguments, including the program
     *           name, but excluding those recognized by the Java VM.<p>
     */
    public static void newInstance(String[] args) {}

}
