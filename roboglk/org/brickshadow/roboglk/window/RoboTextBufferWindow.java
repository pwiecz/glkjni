package org.brickshadow.roboglk.window;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.brickshadow.roboglk.BlorbResource;
import org.brickshadow.roboglk.GlkEventQueue;
import org.brickshadow.roboglk.GlkTextBufferWindow;

import android.app.Activity;


public class RoboTextBufferWindow extends GlkTextBufferWindow
implements RoboTextWindow {

    /* The window id. */
    private final int windowId;
    
    /* A bridge between glk i/o and Android i/o. */
    private final TextBufferIO io;
    
    /* The main activity. */
    private final Activity activity;
    
    private final RoboTextWindowImpl winImpl;
    
    /**
     * Creates a new glk window wrapper for a text buffer window.
     * 
     * @param id
     *           the window id
     * @param queue
     *           the glk event queue
     * @param io
     *           the {@code IOTextbuffer} object to associate with the
     *           window 
     */
    public RoboTextBufferWindow(Activity activity, GlkEventQueue queue,
            TextBufferIO io, int id) {
    	
    	winImpl = new RoboTextWindowImpl(queue, this, io, activity);
        this.activity = activity;
        this.io = io;
        io.setWindow(this);
        windowId = id;
    }
    
    @Override
    public int getId() {
        return windowId;
    }
    
    /**
     * Handles normal characters during single-character input.
     * 
     * @param c a character.
     */
    public void recordKey(char c) {
        winImpl.recordKey(c);
    }
    
    /**
     * Handles special keys during single-character input.
     * 
     * @param c a keycode
     */
    public void recordKey(int c) {
        winImpl.recordKey(c);
    }
    
    /**
     * Handles line input.
     */
    public void recordLine(char[] line, int len, boolean isEvent) {
        winImpl.recordLine(line, len, isEvent);
    }

    @Override
    public void clear() {
    	winImpl.clear();
    }
    
    @Override
    public void getSize(int[] dim) {
        winImpl.getSize(dim);
    }
    
    public void setSize(int x, int y) {
        winImpl.setSize(x, y);
    }
    
    @Override
    public void print(String str) {
        winImpl.print(str);
    }

    @Override
    public void requestCharEvent(boolean unicode) {
        winImpl.requestCharEvent(unicode);
    }

    @Override
    public void cancelCharEvent() {
        winImpl.cancelCharEvent();
    }
    
    @Override
    public void requestLineEvent(ByteBuffer buf, int maxlen, int initlen) {
    	// TODO: remember in TextGridWindow to adjust len to screen width
        winImpl.requestLineEvent(buf, maxlen, initlen);
    }

    @Override
    public void requestLineEventUni(IntBuffer buf, int maxlen, int initlen) {
    	// TODO: remember in TextGridWindow to adjust len to screen width
        winImpl.requestLineEventUni(buf, maxlen, initlen);
    }
    
    @Override
    public int cancelLineEvent() {
        return winImpl.cancelLineEvent();
    }
    
    /**
     * Called by the associated {@link TextBufferIO} object when glk
     * cancels line input.
     * 
     * @param len the current input length
     */
    public void setCurrInputLength(int len) {
        winImpl.setCurrInputLength(len);
    }

    @Override
    public boolean drawInlineImage(BlorbResource bres, int alignment) {
        /*
         * Not currently implemented. (it would be here and not in
         * RoboTextWindowImpl).
         */
        return false;
    }

    @Override
    public boolean drawInlineImage(BlorbResource bres, int alignment, int width,
            int height) {
    	/*
         * Not currently implemented. (it would be here and not in
         * RoboTextWindowImpl).
         */
        return false;
    }

    @Override
    public void flowBreak() {
    	/*
         * Not currently implemented. (it would be here and not in
         * RoboTextWindowImpl).
         */
    }

    @Override
    public void requestMouseEvent() {
        /* TODO: implement in RoboTextWinImpl */
    }
    
    @Override
    public void cancelMouseEvent() {
    	/* TODO: implement in RoboTextWinImpl */
    }
    
    @Override
    public void setLinkValue(int val) {
    	/* TODO: implement in RoboTextWinImpl */
    }
    
    @Override
    public void requestLinkEvent() {
    	/* TODO: implement in RoboTextWinImpl */
    }
    
    @Override
    public void cancelLinkEvent() {
    	/* TODO: implement in RoboTextWinImpl */
    }

    @Override
    public void setStyle(int val) {
        winImpl.setStyle(val);
    }
    
    @Override
    public int measureStyle(int styl, int hint) {
        return winImpl.measureStyle(styl, hint);
    }
    
    @Override
    public void setStyleMeasure(boolean success, int val) {
    	winImpl.setStyleMeasure(success, val);
    }
    

    @Override
    public boolean distinguishStyles(int styl1, int styl2) {
        return winImpl.distinguishStyles(styl1, styl2);
    }
    
    @Override
    public void setStyleDistinguish(boolean distinct) {
    	winImpl.setStyleDistinguish(distinct);
    }

}
