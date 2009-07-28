package org.brickshadow.roboglk.window;


import org.brickshadow.roboglk.GlkStyle;

import android.text.Editable;
import android.text.Selection;
import android.text.SpannableStringBuilder;
import android.text.method.TextKeyListener;
import android.view.KeyEvent;
import android.view.View;


abstract class TextIO {

	/**
     * The glk window wrapper associated with a view.
     */
    protected RoboTextWindow win;
    
    protected final TextWindowView tv;
    
    /**
     * During line input, the characters entered so far. It is the
     * resposibility of concrete subclasses to initialize this upon a
     * request for line input.
     */
    protected char[] inputChars;
    
    /**
     * During line input, the number of characters entered so far.
     */
    protected int currInputLength;
    
    private SpannableStringBuilder tb;
    private boolean charInput;
    private boolean lineInput;
    private final TextKeyListener listener;
    
    TextIO(TextWindowView tv) {
    	this.tv = tv;
    	currInputLength = 0;
    	
    	listener = TextKeyListener.getInstance(false, 
                TextKeyListener.Capitalize.NONE);
        tb = new SpannableStringBuilder(" ");
        
        tv.setOnKeyListener(new View.OnKeyListener() {
            
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                return onViewKey(v, keyCode, event);
            }
        });
        
        tv.setFocusableInTouchMode(true);
    }
	
	/**
     * Must return the width and height of the view. The width should
     * be the number of "0" (zero) characters that would fit on a line;
     * the height should be the number of lines of text that fit in the view.
     * Both measurements should be in terms of the normal font of the view.
     * 
     * @return a two-element array with the width and height of the window
     */
    public abstract int[] getWindowSize();
    
    void setWindow(RoboTextWindow win) {
        this.win = win;
    }
    
	/**
     * Called by the glk window wrapper to arrange for the window size
     * to be returned to the interpreter thread.
     */
    public final void requestWindowSize() {
        int[] size = getWindowSize();
        win.setSize(size[0], size[1]);
    }
    
    /**
     * Prepares a view for single-key input. After this method is called,
     * each key event should result in a call to {@link #sendCharToGlk(char)}
     * or {@link #sendKeyToGlk(int)}, without echoing the key to the screen.
     */
    public void doCharInput() {
        charInput = true;
        lineInput = false;
    }
    
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
    public void stopCharInput() {
        charInput = false;
    }
    
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
    public void doLineInput(boolean unicode, int maxlen,
            char[] initialChars) {
        
        inputChars = new char[maxlen];
        
        if (initialChars != null) {
            int len = initialChars.length;
            System.arraycopy(initialChars, 0, inputChars, 0, len);
            currInputLength = len;
        } else {
            currInputLength = 0;
        }
        
        lineInput = true;
        charInput = false;
    }
    
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
    public void stopLineInput() {
        lineInput = false;
    }
    
    /**
     * Prints a string in the view. Implementations are responsible for
     * maintaining the cursor position and scrolling the text.
     * 
     * @param str the string to print.
     */
    public abstract void doPrint(String str);
    
    /**
     * Clears the view.
     */
    public abstract void doClear();
    
    /**
     * Changes the display style for newly-printed text. 
     * @param style one of the {@link GlkStyle} constants
     */
    public abstract void doStyle(int style);
    
    /**
     * Returns true if the styles are visually distinct in the window.
     */
    public abstract boolean doDistinguishStyles(int styl1, int styl2);
    
    /**
     * Returns a measurement appropriate for the style and hint, or
     * throws a {@code StyleMeasurementException}.
     */
    public abstract int doMeasureStyle(int styl, int hint)
    throws StyleMeasurementException;
    
    protected boolean onViewKey(View v, int keyCode, KeyEvent event) {
    	if (!charInput && !lineInput) {
            return false;
        }
        
        if (tb.length() != 1) {
            tb = new SpannableStringBuilder(" ");
        }
        if (Selection.getSelectionEnd(tb) != 1 ||
                Selection.getSelectionStart(tb) != 1) {
            Selection.setSelection(tb, 1);
        }
        
        switch (event.getAction()) {
        case KeyEvent.ACTION_DOWN:
            listener.onKeyDown(v, tb, keyCode, event);
            break;
        case KeyEvent.ACTION_UP:
            listener.onKeyUp(v, tb, keyCode, event);
            break;
        }
        
        if (charInput) {
            return processSingleKey(keyCode);
        } else {
            return processLineKey(keyCode, event.getAction());
        }
    }
    
    private void endLineInput() {
    	lineInput = false;
        textEchoNewline();
        sendLineToGlk();
        extendHistory();
    }
    
    private boolean processSingleKey(int keyCode) {
        charInput = false;
        switch (tb.length()) {
        case 0: // delete
            sendKeyToGlk(KeyEvent.KEYCODE_DEL);
            return true;
        case 1: // special key
        	if (keyCode == KeyEvent.KEYCODE_MENU) {
        		return false;
        	}
        	if (keyCode == KeyEvent.KEYCODE_BACK) {
        		return false;
        	}
            sendKeyToGlk(keyCode);
            return true;
        case 2: // normal char
            sendCharToGlk(tb.charAt(1));
            return true;
        default:
            return false;
        }
    }
    
    private boolean processLineKey(int keyCode, int action) {
        switch (tb.length()) {
        case 0: // delete
            if (currInputLength == 0) {
                return true;
            } else {
                Editable text = tv.getEditableText();
                int len = text.length();
                text.delete(len - 1, len);
                currInputLength -=1;
                return true;
            }
            
        case 1: // special key
        	switch (keyCode) {
        	case KeyEvent.KEYCODE_MENU:
        	case KeyEvent.KEYCODE_BACK:
        		return false;
        	case KeyEvent.KEYCODE_DPAD_UP:
        		if (action == KeyEvent.ACTION_DOWN) {
        			historyPrev();
        		}
        		break;
        	case KeyEvent.KEYCODE_DPAD_DOWN:
        		if (action == KeyEvent.ACTION_DOWN) {
        			historyNext();
        		}
        		break;
        	default:
        		break;
        	}
            /* TODO: basic line editing/cursor movement */
            return true;
            
        case 2: // normal char
            char c = tb.charAt(1);
            
            if (c == '\n') {
                endLineInput();
                return true;
            }
            
            if (currInputLength == inputChars.length) {
                return true;
            }
            
            inputChars[currInputLength++] = c;
            
            textEcho(tb.subSequence(1, 2));
            return true;
            
        default:
            return false;
        }
    }
    
    protected abstract void historyPrev();
    
    protected abstract void historyNext();
    
    protected abstract void extendHistory();
    
    /* TODO: account for padding? */
    protected final int getViewLines() {
        return tv.getHeight() / tv.getLineHeight();
    }
    
    protected abstract void textEcho(CharSequence str);
    
    protected abstract void textEchoNewline();

    /**
     * Called by the glk window wrapper to find out if two styles
     * are visually distinct in the view.
     */
	public final void requestStyleDistinguish(int styl1, int styl2) {
		boolean distinct = doDistinguishStyles(styl1, styl2);
		win.setStyleDistinguish(distinct);
	}
	
	public final void requestStyleMeasure(int styl, int hint) {
		int val = 0;
		boolean success = false;
		
		try {
			val = doMeasureStyle(styl, hint);
			success = true;
		} catch (StyleMeasurementException e) {}
		
		win.setStyleMeasure(success, val);
	}
}
