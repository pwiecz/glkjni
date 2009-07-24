package org.brickshadow.roboglk.window;

import org.brickshadow.roboglk.GlkStyle;


/**
 * Text-buffer I/O methods. Each view that will display a text-buffer
 * window needs to be associated with an instance of an implementation
 * of this class.
 * <p>
 * The abstract {@code doXXXX} methods are called by glk (but on the
 * UI thread); the {@code sendXXXToGlk} methods are to be called in
 * response to Android input events.
 */
public abstract class TextBufferIO {

    /*
     * Implementation notes: it is likely that this class as well
     * as (the yet-to-be-written) TextGridIO will be refactored as
     * subclasses of an TextIO class. 
     */
    
    /**
     * The glk window wrapper associated with a view.
     */
    protected RoboTextBufferWindow win;
    
    /**
     * During line input, the number of characters entered so far.
     */
    protected int currInputLength;
    
    /**
     * During line input, the characters entered so far. It is the
     * resposibility of concrete subclasses to initialize this upon a
     * request for line input.
     */
    protected char[] inputChars;
    
    /**
     * Creates a new {@code TextBufferIO}.
     */
    public TextBufferIO() {
        currInputLength = 0;
    }
    
    public void setWindow(RoboTextBufferWindow win) {
        this.win = win;
    }

    /**
     * Prepares a view for single-key input. After this method is called,
     * each key event should result in a call to {@link #sendCharToGlk(char)}
     * or {@link #sendKeyToGlk(int)}, without echoing the key to the screen.
     */
    public abstract void doCharInput();
    
    /**
     * Sends a unicode char to the glk window wrapper.
     * <p>
     * This should not be called outside of a
     * {@code doCharInput()/stopCharInput()} series of events (but if it
     * is, glkjni will ignore any character input event that is generated
     * by the glk window wrapper).
     * 
     * @param c
     *           a character
     */
    public final void sendCharToGlk(char c) {
        /* 
         * This assumes that all special keys are indeed handled
         * by calls to sendKeyToGlk().
         */
        win.recordKey(c);
    }
    
    /**
     * Call this to send a non-printing keypress to the glk window wrapper.
     * <p>
     * This should not be called outside of a
     * {@code doCharInput()/stopCharInput()} series of events (but if it
     * is, glkjni will ignore any character input event that is generated
     * by the glk window wrapper).
     * 
     * @param keycode
     *           an Android keycode
     */
    public final void sendKeyToGlk(int keycode) {
        win.recordKey(keycode);
    }
    
    /**
     * Cancels single-key input in a view.
     */
    public abstract void stopCharInput();
    
    /**
     * Prepares a view for line input. After this method is called, key
     * events should be handled as line input (echoed to the screen, and
     * perhaps with support for basic line editing).
     * <p>
     * While input is occurring, the current number of characters input
     * must be stored in {@link #currInputLength}, and the actual characters
     * must be placed, as they are entered, in {@link #inputChars}.
     * <p>
     * When input is finished, {@link #sendLineToGlk()} should be called.
     * <p>
     * Implementations should use the values of {@code maxlen}
     * and {@code unicode} to guide their behavior.
     * 
     * @param unicode
     *           if unicode input was requested
     * @param maxlen
     *           the maximum input length that glk is expecting
     * @param initialChars
     *           if non-null, text that should be displayed as though the
     *           player had typed it as the beginning of the input.
     */
    public abstract void doLineInput(boolean unicode, int maxlen,
            char[] initialChars);
    
    /**
     * Sends a line of input to the glk window wrapper.
     * <p>
     * This should not be called outside of a
     * {@code doLineInput()/stopLineInput()} series of events (but if it
     * is, glkjni will ignore any line input event that is generated
     * by the glk window wrapper).
     * <p>
     * The input array may be modified by this method.
     * 
     * @param line a line of input
     */
    public final void sendLineToGlk() {
        win.recordLine(inputChars, currInputLength, true);
    }
    
    /**
     * Called to cancel line input. This is a wrapper which takes care
     * of communicating the current input length back to the glk window
     * wrapper and then calls {@link #stopLineInput()}.
     */
    public final void stopLineInputAndGetLength() {
        win.recordLine(inputChars, currInputLength, false);
        win.setCurrInputLength(currInputLength);
        stopLineInput();
    }
    
    /**
     * Cancels line input from a view.
     */
    public abstract void stopLineInput();
    
    /**
     * Must return the width and height of the view. The width should
     * be the number of "0" (zero) characters that would fit on a line;
     * the height should be the number of lines of text that fit in the view.
     * Both measurements should be in terms of the normal font of the view.
     * 
     * @return a two-element array with the width and height of the window
     */
    public abstract int[] getWindowSize();
    
    /**
     * Called by the glk window wrapper to arrange for the window size
     * to be returned to the interpreter thread.
     */
    public final void requestWindowSize() {
        int[] size = getWindowSize();
        win.setSize(size[0], size[1]);
    }
    
    /**
     * Prints a string in the view. Implementations are responsible for
     * maintaining the cursor position and scrolling the text.
     * 
     * @param str the string to print.
     */
    public abstract void doPrint(String str);
    
    /**
     * Changes the display style for newly-printed text. 
     * @param style one of the {@link GlkStyle} constants
     */
    public abstract void doStyle(int style);
    
    /**
     * Clears the view.
     */
    public abstract void doClear();
}
