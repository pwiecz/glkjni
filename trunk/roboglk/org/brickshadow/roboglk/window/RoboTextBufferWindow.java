package org.brickshadow.roboglk.window;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.brickshadow.roboglk.GlkEventQueue;
import org.brickshadow.roboglk.GlkKeycode;
import org.brickshadow.roboglk.GlkTextBufferWindow;

import android.app.Activity;
import android.view.KeyEvent;

public class RoboTextBufferWindow extends GlkTextBufferWindow {

    /* The window id. */
    private final int windowId;
    
    /* A bridge between glk i/o and Android i/o. */
    private final TextBufferIO io;
    
    /* A reference to the glk event queue. */
    private final GlkEventQueue queue;
    
    /*
     * The main activity.
     */
    private final Activity activity;
    
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
    
    /*
     * Variables for inter-thread communication, for the glk methods
     * which need to have some effect on the UI thread but which also
     * need to return a value to the interpreter thread.
     */
    private volatile boolean waitingForIO;
    private volatile int currInputLength;
    private volatile int sizeX;
    private volatile int sizeY;
    
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
        this.activity = activity;
        this.queue = queue;
        this.io = io;
        io.setWindow(this);
        windowId = id;
    }
    
    @Override
    public int getId() {
        return windowId;
    }
    
    private void processKey(int c) {
        queue.putEvent(GlkEventQueue.newCharInputEvent(this, c));
    }
    
    /**
     * Handles normal characters during single-character input.
     * 
     * @param c a character.
     */
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
    
    /**
     * Handles special keys during single-character input.
     * 
     * @param c a keycode
     */
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
    
    /**
     * Handles line input.
     */
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
            queue.putEvent(GlkEventQueue.newLineInputEvent(this, inputLength));
        }
    }

    @Override
    public void clear() {
    	activity.runOnUiThread(new Runnable() {
    		@Override
    		public void run() {
    			io.doClear();
    		}
    	});
    }
    
    @Override
    public void getSize(int[] dim) {
        synchronized(this) {
            waitingForIO = true;
        }
        
        int x = 0;
        int y = 0;
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                io.requestWindowSize();
            }
        });
        
        synchronized(this) {
            try {        
                while (waitingForIO) {
                    wait();
                }
                x = sizeX;
                y = sizeY;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        dim[0] = x;
        dim[1] = y;
    }
    
    public void setSize(int x, int y) {
        synchronized(this) {
            boolean wasWaiting = waitingForIO;
            sizeX = x;
            sizeY = y;
            waitingForIO = false;
            if (wasWaiting) {
                notify();
            }
        }
    }
    
    @Override
    public void print(final String str) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                io.doPrint(str);
            }
        });
    }

    @Override
    public void requestCharEvent(final boolean unicode) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                inputIsUnicode = unicode;
                io.doCharInput();
            } 
        });
    }

    @Override
    public void cancelCharEvent() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                io.stopCharInput();
            }
        });
    }

    private void lineRequest(final ByteBuffer lbuf, final IntBuffer ubuf,
            final int maxlen, final int initlen) {
        
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
        
        final char[] iChars = initialChars;
        
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
    
    @Override
    public void requestLineEvent(ByteBuffer buf, int maxlen, int initlen) {
        lineRequest(buf, null, maxlen, initlen);
    }

    @Override
    public void requestLineEventUni(IntBuffer buf, int maxlen, int initlen) {
        lineRequest(null, buf, maxlen, initlen);
    }
    
    @Override
    public int cancelLineEvent() {
        synchronized(this) {
            waitingForIO = true;
        }
        
        int currLen = 0;
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                io.stopLineInputAndGetLength();
            }
        });
        
        synchronized(this) {
            try {        
                while (waitingForIO) {
                    wait();
                }
                currLen = currInputLength;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        
        return currLen;
    }
    
    /**
     * Called by the associated {@link TextBufferIO} object when glk
     * cancels line input.
     * 
     * @param len the current input length
     */
    public void setCurrInputLength(int len) {
        synchronized(this) {
            boolean wasWaiting = waitingForIO;
            currInputLength = len;
            waitingForIO = false;
            if (wasWaiting) {
                notify();
            }
        }
    }

    @Override
    public boolean drawInlineImage(int num, int alignment) {
        /*
         * Not currently implemented.
         */
        return false;
    }

    @Override
    public boolean drawInlineImage(int num, int alignment, int width,
            int height) {
        /* Not currently implemented. */
        return false;
    }

    @Override
    public void flowBreak() {
        /* Not currently implemented. */
    }

    @Override
    public void requestMouseEvent() {
        /* Not currently implemented. */
    }
    
    @Override
    public void cancelMouseEvent() {
        /* Not currently implemented. */
    }
    
    @Override
    public void setLinkValue(int val) {
        /* Not currently implemented. */
    }
    
    @Override
    public void requestLinkEvent() {
        /* Not currently implemented. */
    }
    
    @Override
    public void cancelLinkEvent() {
        /* Not currently implemented. */
    }

    @Override
    public void setStyle(final int val) {
        activity.runOnUiThread(new Runnable() {
        	@Override
        	public void run() {
        		io.doStyle(val);
        	}
        });
    }
    
    @Override
    public int measureStyle(int styl, int hint) {
        /*
         * Not currently implemented.
         * 
         * Throwing an exception is the correct behavior for a window
         * that cannot return a value for the given style and hint.
         * 
         * This will likely be implemented (or forwarded to) a static method
         * applicable to all instances of text-buffer windows.
         */
        throw new RuntimeException();
    }
    

    @Override
    public boolean distinguishStyles(int styl1, int styl2) {
        /*
         * Not currently implemented.
         * 
         * This will likely be implemented (or forwarded to) a static method
         * applicable to all instances of text-buffer windows.
         */
        return false;
    }

}
