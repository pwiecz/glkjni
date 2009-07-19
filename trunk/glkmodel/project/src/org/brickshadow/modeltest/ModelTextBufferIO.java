package org.brickshadow.modeltest;

import org.brickshadow.roboglk.window.RoboTextBufferWindow;
import org.brickshadow.roboglk.window.TextBufferIO;

import android.text.Selection;
import android.text.SpannableStringBuilder;
import android.text.method.TextKeyListener;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

public class ModelTextBufferIO extends TextBufferIO {

    private boolean charInput;
    private boolean lineInput;
    private final TextView tv;
    private final TextKeyListener listener;
    private SpannableStringBuilder tb;
    
    public ModelTextBufferIO(TextView tv) {
        this.tv = tv;
        
        listener = TextKeyListener.getInstance(false, 
                TextKeyListener.Capitalize.NONE);
        tb = new SpannableStringBuilder(" ");
        
        tv.setOnKeyListener(new View.OnKeyListener() {
            
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
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
                    return processLineKey(keyCode);
                }
            }
        });
    }
    
    private boolean processSingleKey(int keyCode) {
        charInput = false;
        switch (tb.length()) {
        case 0: // delete
            sendKeyToGlk(KeyEvent.KEYCODE_DEL);
            return true;
        case 1: // special key
            sendKeyToGlk(keyCode);
            return true;
        case 2: // normal char
            sendCharToGlk(tb.charAt(1));
            return true;
        default:
            return false;
        }
    }
    
    /*
     * TODO: it might be possible, if a decent printing method were in
     *       place, to simply record the necessary info in inputChars
     *       and currInput length and then return false to not consume
     *       the event but rather let the TextView handle it. I don't
     *       know exactly how an editable TextView works, though.
     */
    private boolean processLineKey(int keyCode) {
        switch (tb.length()) {
        case 0: // delete
            if (currInputLength == 0) {
                return true;
            } else {
                /* TODO: actually handle deletion. */
                return true;
            }
            
        case 1: // special key
            /* TODO: basic line editing/cursor movement */
            return true;
            
        case 2: // normal char
            char c = tb.charAt(1);
            
            if (c == '\n') {
                sendLineToGlk();
                lineInput = false;
                
                /* TODO: this is not ideal. */
                tv.append("\n");
                return true;
            }
            
            if (currInputLength == inputChars.length) {
                return true;
            }
            
            inputChars[currInputLength++] = c;
            
            /* TODO: a better way to echo */
            tv.append(tb.subSequence(1, 2));
            return true;
            
        default:
            return false;
        }
    }
    
    public void setWindow(RoboTextBufferWindow win) {
        this.win = win;
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
    }

    @Override
    public void doPrint(String str) {
        /*
         * TODO: this is what really needs work.
         */
        tv.append(str);
    }

    @Override
    public int[] getWindowSize() {
        /* TODO: return the real size. */
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

}
