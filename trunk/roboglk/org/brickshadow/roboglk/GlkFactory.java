package org.brickshadow.roboglk;

/**
 * Defines the methods needed to start an interpreter.
 * <p>
 * For example:
 * <pre>
 *   void startInterpreter() {
 *       Thread terpThread = new Thread(new Runnable() {
 *           public void run() {
 *               if (GlkFactory.startup("progname", "story.z5") {
 *                   int err = GlkFactory.run();
 *                   if (err == 0) {
 *                       // Handle normal interpreter exit
 *                   } else {
 *                       // Handle abnormal interpreter exit
 *                   }
 *               } else {
 *                   // Handle initialization failure
 *               }
 *       });
 *       terpThread.start();
 *   }
 * </pre>
 */
public class GlkFactory {

    /**
     * Call this method to initialize the interpreter. It is an error
     * to pass null arguments.
     * <p>
     * <b>Note:</b> various interpreters handle initialization failures
     * in different ways; those used with Android could be patched so
     * that this method could return a string describing the cause for
     * failure.
     * 
     * @param glk the {@link Glk Glk bridge object}.
     * @param args {@code args[0]} should be the program name;
     *             {@code args[1..n]} are the options to pass to the
     *             interpreter, including the story file name (which
     *             must be the last element)
     * @return {@code true} if the interpreter was successfully
     *         initialized.
     */
    public static native boolean startup(Glk glk, String[] args);
    
    /**
     * Call ths method to start the interpreter. All Glk bridge methods
     * will be called on the thread that this method is called from.
     * 
     * @return 0 if the interpreter exited normally
     */
    public static native int run();
}
