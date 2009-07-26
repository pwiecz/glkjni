package org.brickshadow.roboglk.window;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.brickshadow.roboglk.GlkEventQueue;
import org.brickshadow.roboglk.GlkTextGridWindow;

import android.app.Activity;

public class RoboTextGridWindow extends GlkTextGridWindow
implements RoboTextWindow {
	
	/* The window id. */
    private final int windowId;
    
    /* A bridge between glk i/o and Android i/o. */
    private final TextGridIO io;
    
    /* The main activity. */
    private final Activity activity;
    
    private final RoboTextWindowImpl winImpl;
    
    public RoboTextGridWindow(Activity activity, GlkEventQueue queue,
            TextGridIO io, int id) {
    	
    	winImpl = new RoboTextWindowImpl(queue, this, io, activity);
    	this.activity = activity;
        this.io = io;
        io.setWindow(this);
    	windowId = id;
    }

	@Override
	public void cancelCharEvent() {
		winImpl.cancelCharEvent();
	}

	@Override
	public int cancelLineEvent() {
		return winImpl.cancelLineEvent();
	}

	@Override
	public void cancelLinkEvent() {
		/* TODO: implement in RoboTextWinImpl */
	}

	@Override
	public void cancelMouseEvent() {
		/* TODO: implement in RoboTextWinImpl */
	}

	@Override
	public void clear() {
		winImpl.clear();
	}

	@Override
	public boolean distinguishStyles(int styl1, int styl2) {
		return winImpl.distinguishStyles(styl1, styl2);
	}

	@Override
	public int getId() {
		return windowId;
	}

	@Override
	public void getSize(int[] dim) {
		winImpl.getSize(dim);
	}

	@Override
	public int measureStyle(int styl, int hint) {
		return winImpl.measureStyle(styl, hint);
	}

	@Override
	public void moveCursor(int xpos, int ypos) {
		/*
		 * TODO: implement in TextGridIO
		 */
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
	public void requestLineEvent(ByteBuffer buf, int maxlen, int initlen) {
		winImpl.requestLineEvent(buf, maxlen, initlen);
	}

	@Override
	public void requestLineEventUni(IntBuffer buf, int maxlen, int initlen) {
		winImpl.requestLineEventUni(buf, maxlen, initlen);
	}

	@Override
	public void requestLinkEvent() {
		/* TODO: implement in RoboTextWinImpl */
	}

	@Override
	public void requestMouseEvent() {
		/* TODO: implement in RoboTextWinImpl */
	}

	@Override
	public void setLinkValue(int val) {
		/* TODO: implement in RoboTextWinImpl */
	}

	@Override
	public void setStyle(int val) {
		winImpl.setStyle(val);
	}

	@Override
	public void recordKey(char c) {
		winImpl.recordKey(c);
	}

	@Override
	public void recordKey(int c) {
		winImpl.recordKey(c);
	}

	@Override
	public void recordLine(char[] line, int len, boolean isEvent) {
		winImpl.recordLine(line, len, isEvent);
	}

	@Override
	public void setCurrInputLength(int len) {
		winImpl.setCurrInputLength(len);
	}

	@Override
	public void setSize(int x, int y) {
		winImpl.setSize(x, y);
	}

	@Override
	public void setStyleDistinguish(boolean distinct) {
		winImpl.setStyleDistinguish(distinct);
	}

	@Override
	public void setStyleMeasure(boolean success, int val) {
		winImpl.setStyleMeasure(success, val);
	}

}
