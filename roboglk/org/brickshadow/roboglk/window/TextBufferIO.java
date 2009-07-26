package org.brickshadow.roboglk.window;


import org.brickshadow.roboglk.window.RoboTextBufferWindow;

import android.text.Selection;
import android.text.Spannable;
import android.view.KeyEvent;
import android.view.View;


public class TextBufferIO extends TextIO {

    private boolean morePrompt;
    private int linesSinceInput;
    private int moreLines;
    private int inputLineStart;
    
    public TextBufferIO(final TextBufferView tv) {
    	super(tv);
    }
    
    protected boolean onViewKey(View v, int keyCode, KeyEvent event) {
        if (morePrompt && (event.getAction() == KeyEvent.ACTION_DOWN)) {
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
        
        return super.onViewKey(v, keyCode, event);
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
    protected final void textEcho(CharSequence str) {
    	textBufEcho(str);    	
    }
    
    @Override
    protected final void textEchoNewline() {
    	textBufEcho("\n");
    }

    @Override
    public final void doLineInput(boolean unicode, int maxlen,
            char[] initialChars) {
        
        super.doLineInput(unicode, maxlen, initialChars);
        
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
            return viewLines - 1;
        }
    }

    @Override
    public final void doPrint(String str) {
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
    
    /*
     * The following may become abstract or move up to TextIO?.
     */
    
    @Override
    public void doStyle(int style) {}
    
    @Override
    public boolean doDistinguishStyles(int styl1, int styl2) {
    	return false;
    }
    
    @Override
    public int doMeasureStyle(int styl, int hint)
    		throws StyleMeasurementException {
    	throw new StyleMeasurementException();
    }

    @Override
    public void doClear() {}
}
