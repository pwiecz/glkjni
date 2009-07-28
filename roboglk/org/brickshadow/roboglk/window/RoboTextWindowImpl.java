package org.brickshadow.roboglk.window;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.brickshadow.roboglk.GlkEventQueue;
import org.brickshadow.roboglk.GlkKeycode;
import org.brickshadow.roboglk.GlkWindow;

import android.app.Activity;
import android.view.KeyEvent;

class RoboTextWindowImpl implements RoboTextWindow {

    /*
     * Whether glk is expecting Unicode or Latin-1 input.
     */
    private boolean inputIsUnicode;
    
    /*
     * The maximum length of an input line that glk is prepared to
     * accept.
     */
    private int maxInputLength;
    
    /*
     * Buffers for line input.
     */
    private ByteBuffer latinBuffer;
    private IntBuffer unicodeBuffer;
    
    /* A reference to the glk event queue. */
    private final GlkEventQueue queue;
    
    /* The managed glk window.*/
    private final GlkWindow glkWindow;
    
    /* A bridge between glk i/o and Android i/o. */
    private final TextIO io;
    
    /*
     * The main activity.
     */
    private final Activity activity;
    
    /*
     * Variables for inter-thread communication, for the glk methods
     * which need to have some effect on the UI thread but which also
     * need to return a value to the interpreter thread.
     */
	private UISync uiWait;
    private volatile int currInputLength;
    private volatile int sizeX;
    private volatile int sizeY;
    private volatile boolean styleDistinct;
    private volatile boolean couldMeasure;
    private volatile int styleMeasure;
    
    public RoboTextWindowImpl(GlkEventQueue queue, GlkWindow glkWindow,
    		TextIO io, Activity activity) {
    	
    	this.queue = queue;
    	this.glkWindow = glkWindow;
    	this.io = io;
    	this.activity = activity;
    }
	
    private void processKey(int c) {
        queue.putEvent(GlkEventQueue.newCharInputEvent(glkWindow, c));
    }
    
	@Override
	public void recordKey(char c) {
		if (c == '\n') {
            processKey(GlkKeycode.Return);
        } else {
            if (!inputIsUnicode && c > 0xFF) {
                processKey('?');
            } else {
                processKey(c);
            }
        }
	}

	@Override
	public void recordKey(int c) {
		switch (c) {
        case KeyEvent.KEYCODE_DEL:
            processKey(GlkKeycode.Delete);
            break;
        case KeyEvent.KEYCODE_DPAD_UP:
            processKey(GlkKeycode.Up);
            break;
        case KeyEvent.KEYCODE_DPAD_DOWN:
            processKey(GlkKeycode.Down);
            break;
        case KeyEvent.KEYCODE_DPAD_LEFT:
            processKey(GlkKeycode.Left);
            break;
        case KeyEvent.KEYCODE_DPAD_RIGHT:
            processKey(GlkKeycode.Right);
            break;
        
        /* These values taken from zplet.Event */
        case 100033: // F1
            processKey(GlkKeycode.Func1);
            break;
        case 100034: // F2
            processKey(GlkKeycode.Func2);
            break;
        case 100035: // F3
            processKey(GlkKeycode.Func3);
            break;
        case 100036: // F4
            processKey(GlkKeycode.Func4);
            break;
        case 100037: // F5
            processKey(GlkKeycode.Func5);
            break;
        case 100038: // F6
            processKey(GlkKeycode.Func6);
            break;
        case 100039: // F7
            processKey(GlkKeycode.Func7);
            break;
        case 100040: // F8
            processKey(GlkKeycode.Func8);
            break;
        case 100041: // F9
            processKey(GlkKeycode.Func9);
            break;
        case 100042: // F10
            processKey(GlkKeycode.Func10);
            break;
        case 100043: // F11
            processKey(GlkKeycode.Func11);
            break;
        case 100044: // F12
            processKey(GlkKeycode.Func12);
            break;
        default:
            processKey(GlkKeycode.Unknown);
            break;
        }
	}

	@Override
	public void recordLine(char[] line, int len, boolean isEvent) {
		int inputLength = (len > maxInputLength ?
                maxInputLength : len);
        if (inputIsUnicode) {
            for (int i = 0; i < inputLength; i++) {
                unicodeBuffer.put(i, line[i]);
            }
        } else {
            for (int i = 0; i < inputLength; i++) {
                char c = (line[i] > 0xFF ? '?' : line[i]);
                latinBuffer.put(i, (byte) c);
            }
        }
        if (isEvent) {
            queue.putEvent(
            		GlkEventQueue.newLineInputEvent(glkWindow, inputLength));
        }
	}
	
	void getSize(int[] dim) {
		uiWait = new UISync(activity);
		uiWait.waitFor(new Runnable() {
            public void run() {
                io.requestWindowSize();
            }
        });
        
        dim[0] = sizeX;
        dim[1] = sizeY;
	}
	
	@Override
	public void setSize(final int x, final int y) {
		uiWait.stopWaiting(new Runnable() {
			public void run() {
				sizeX = x;
	            sizeY = y;
			}
		});
		uiWait = null;
	}
	
	boolean distinguishStyles(final int styl1, final int styl2) {
		uiWait = new UISync(activity);
		uiWait.waitFor(new Runnable() {
            public void run() {
                io.requestStyleDistinguish(styl1, styl2);
            }
        });
		
		return styleDistinct;
	}
	
	@Override
	public void setStyleDistinguish(final boolean distinct) {
		uiWait.stopWaiting(new Runnable (){
			public void run() {
	            styleDistinct = distinct;
			}
		});
	}
	
	int measureStyle(final int styl, final int hint) {
		uiWait = new UISync(activity);
		uiWait.waitFor(new Runnable() {
            public void run() {
                io.requestStyleMeasure(styl, hint);
            }
        });
		
		if (!couldMeasure) {
			throw new StyleMeasurementException();
		}		
		return styleMeasure;
	}
	
	@Override
	public void setStyleMeasure(final boolean success, final int val) {
		uiWait.stopWaiting(new Runnable() {
			public void run() {
	            couldMeasure = success;
	            styleMeasure = val;
			}
		});
	}

	private char[] getInitialChars(ByteBuffer lbuf, IntBuffer ubuf, int maxlen,
			int initlen) {

		char[] initialChars = null;
        
        /*
         * I think that very few games make use of the "initial input"
         * feature, so this code should not have too great an impact
         * on performance. It would also be possible but tedious to
         * push this logic down into C code in glkjni.
         */
        if (initlen != 0) {
            if (lbuf != null) {
                initialChars = new char[initlen];
                for (int i = 0; i < initlen; i++) {
                    initialChars[i] = (char) lbuf.get(i);
                }
            } else {
                StringBuilder initialString = new StringBuilder(initlen);
                for (int i = 0; i < initlen; i++) {
                    int c = ubuf.get(i);
                    /*
                     * It is unlikely but legal for a glk program to use
                     * Unicode characters outside the BMP.
                     */
                    if (c > 0xFFFF) {
                        c -= 0x10000;
                        int surr1 = 0xD800 | (c >> 10);
                        int surr2 = 0xDC00 | (c & 0x3FF);
                        initialString.append((char) surr1);
                        initialString.append((char) surr2);
                    } else {
                        initialString.append((char) c);
                    }
                }
                int len = initialString.length();
                if (len > maxlen) {
                    len = maxlen;
                }
                initialChars = new char[len];
                initialString.getChars(0, len, initialChars, 0);
            }
        }
        
        return initialChars;
	}

	private void lineRequest(final ByteBuffer lbuf, final IntBuffer ubuf,
			final int maxlen, final int initlen) {
	        
		final char[] iChars =
			getInitialChars(lbuf, ubuf, maxlen, initlen);
	        
		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				latinBuffer = lbuf;
				unicodeBuffer = ubuf;
				maxInputLength = maxlen;
				inputIsUnicode = (ubuf != null);
				io.doLineInput(inputIsUnicode, maxlen, iChars);
			}
		});
	}
	
	void requestLineEvent(ByteBuffer buf, int maxlen, int initlen) {
		lineRequest(buf, null, maxlen, initlen);
	}

	void requestLineEventUni(IntBuffer buf, int maxlen, int initlen) {
		lineRequest(null, buf, maxlen, initlen);
	}

	void clear() {
    	activity.runOnUiThread(new Runnable() {
    		@Override
    		public void run() {
    			io.doClear();
    		}
    	});
    }
	
	void print(final String str) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                io.doPrint(str);
            }
        });
    }
	
	void requestCharEvent(final boolean unicode) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                inputIsUnicode = unicode;
                io.doCharInput();
            } 
        });
    }
	
	void cancelCharEvent() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                io.stopCharInput();
            }
        });
    }
	
	int cancelLineEvent() {
		uiWait = new UISync(activity);
		uiWait.waitFor(new Runnable() {
            @Override
            public void run() {
                io.stopLineInputAndGetLength();
            }
        });
        
        return currInputLength;
    }
	
	@Override
	public void setCurrInputLength(final int len) {
		uiWait.stopWaiting(new Runnable() {
			public void run() {
	            currInputLength = len;
			}
		});
		uiWait = null;
	}

	
	void setStyle(final int val) {
        activity.runOnUiThread(new Runnable() {
        	@Override
        	public void run() {
        		io.doStyle(val);
        	}
        });
    }
}
