package org.brickshadow.roboglk.window;


import org.brickshadow.roboglk.window.RoboTextBufferWindow;
import org.brickshadow.roboglk.window.TextBufferIO;
import org.brickshadow.roboglk.window.TextBufferView;

import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.method.TextKeyListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;


public class StandardTextBufferIO extends TextBufferIO {

    private boolean charInput;
    private boolean lineInput;
    
    protected final TextBufferView tv;
    
    private final TextKeyListener listener;
    private SpannableStringBuilder tb;
    
    private boolean morePrompt;
    private int linesSinceInput;
    private int moreLines;
    private int inputLineStart;
    
    public StandardTextBufferIO(final TextBufferView tv) {
        this.tv = tv;
        
        listener = TextKeyListener.getInstance(false, 
                TextKeyListener.Capitalize.NONE);
        tb = new SpannableStringBuilder(" ");
        
        tv.setOnKeyListener(new View.OnKeyListener() {
            
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                return onViewKey(v, keyCode, event);
            }
        });
    }
    
    protected boolean onViewKey(View v, int keyCode, KeyEvent event) {
    	int action = event.getAction();
        
        if (morePrompt && (action == KeyEvent.ACTION_DOWN)) {
            int viewLines = getViewLines();
            int scrollLines =
                ((moreLines > viewLines) ? viewLines : moreLines);
            tv.scrollBy(0, scrollLines * tv.getLineHeight());
            moreLines -= scrollLines;
            
            if (moreLines == 0) {
                morePrompt = false; // TODO: hide the prompt!

                textBufPrint("");
            }
            return true;
        }
        
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
        
        switch (action) {
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
            return processLineKey(keyCode);
        }
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
    
    private boolean processLineKey(int keyCode) {
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
        	if (keyCode == KeyEvent.KEYCODE_MENU) {
        		return false;
        	}
        	if (keyCode == KeyEvent.KEYCODE_BACK) {
        		return false;
        	}
            /* TODO: basic line editing/cursor movement */
            return true;
            
        case 2: // normal char
            char c = tb.charAt(1);
            
            if (c == '\n') {
                lineInput = false;
                textBufEcho("\n");
                sendLineToGlk();
                return true;
            }
            
            if (currInputLength == inputChars.length) {
                return true;
            }
            
            inputChars[currInputLength++] = c;
            
            textBufEcho(tb.subSequence(1, 2));
            return true;
            
        default:
            return false;
        }
    }
    
    public void setWindow(RoboTextBufferWindow win) {
        this.win = win;
    }
    
    protected void cursorOff() {
    	Spannable text = (Spannable) tv.getText();
    	Selection.removeSelection(text);
    }
    
    protected void cursorToEnd() {
    	cursorToEnd(0);
    }
    
    protected void cursorToEnd(int back) {
    	Spannable text = (Spannable) tv.getText();
    	int len = text.length();
    	Selection.setSelection(text, len + back);
    }
    
    /* TODO: account for padding? */
    private int getViewLines() {
        return tv.getHeight() / tv.getLineHeight();
    }
    
    /* Prints text and decides if the MORE prompt is needed. */
    private void textBufPrint(CharSequence str) {
        
        /* If the cursor offset equals the length of the text, append()
         * will advance the cursor (and automatically scroll) whether
         * we want it to or not. So we back the cursor up until we know
         * if we should scroll to end or display the MORE prompt.  
         */
        cursorToEnd(-1);
        
        int oldLineCount = tv.getLineCount();
        tv.append(str);
        int linesAdded = tv.getLineCount() - oldLineCount;
        
        if (morePrompt) {
            moreLines += linesAdded;
            return;
        }
        
        if (needsMorePrompt(linesAdded)) {
            return;
        }
        
        cursorToEnd();
    }
    
    private boolean needsMorePrompt(int linesAdded) {
        linesSinceInput += linesAdded;
        
        int viewLines = getViewLines();
        // TODO: maybe >= instead? or >= viewLines - 1 ?
        if (linesSinceInput > viewLines) {
            moreLines = linesSinceInput - viewLines;
            morePrompt = true;
            
            cursorOff();
            tv.scrollBy(0, inputLineStart * tv.getLineHeight());
            
            // TODO: actually show the prompt!
            
            return true;
        } else {
            inputLineStart -= linesAdded;
        }
        
        return false;
    }
    

    /* When echoing text, we don't have to worry about the MORE prompt,
     * so we always advance the cursor to the end of the text.
     */
    private void textBufEcho(CharSequence str) {
        tv.append(str);
        cursorToEnd();
    }
    
    @Override
    public void doCharInput() {
        charInput = true;
        lineInput = false;
    }

    @Override
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
        
        linesSinceInput = 0;
        
        inputLineStart = computeInputLineStart();
    }
    
    /* TODO: This will have to take clear() into account. */
    private int computeInputLineStart() {
        int lineCount = tv.getLineCount();
        int viewLines = getViewLines();
        if (lineCount < viewLines) {
            return lineCount;
        } else {
            return viewLines;
        }
    }

    @Override
    public void doPrint(String str) {
        textBufPrint(str);
    }

    /**
     * The default implementation returns (0, 0). Subclasses should
     * override this and calculate the size with respect to the font
     * used for {@code GlkStyle.Normal}.
     */
    @Override
    public int[] getWindowSize() {
        return new int[] { 0, 0 };
    }

    @Override
    public void stopCharInput() {
        charInput = false;
    }

    @Override
    public void stopLineInput() {
        lineInput = false;
    }
    
    @Override
    public void doStyle(int style) {}

    @Override
    public void doClear() {}
}
