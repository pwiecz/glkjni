package org.brickshadow.roboglk;


import java.io.File;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import android.os.Handler;
import android.os.Message;
import android.text.Selection;
import android.text.SpannableStringBuilder;
import android.text.method.TextKeyListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;


public class Glk extends GlkTextBufferWindow {

    private int windowId = 0;
    
    /*
     * Variables for keeping track of input requests.
     */
    boolean charInput;
    boolean lineInput;
    boolean uniInput;
    ByteBuffer lineBuf;
    IntBuffer lineUniBuf;
    int lineLenMax;
    int lineLenCur;
    
    /*
     * Probably this will change to some type of object which
     * accepts printing requests and handles all the messy details
     * of working with Editables and Selections.
     */
    private final TextView myView;
    
    /*
     * For sending messages to the text view on the UI thread.
     */
    private final Handler uiHandler;
    
    /*
     * Keyboard stuff.
     */
    private TextKeyListener listener;
    private SpannableStringBuilder tb;
    
    public final GlkEventQueue eventQueue;
    
    public Glk(TextView myView, Handler uiHandler) {
        this.myView = myView;
        this.uiHandler = uiHandler;
        eventQueue = new GlkEventQueue();
        listener = TextKeyListener.getInstance(false, TextKeyListener.Capitalize.NONE);
        tb = new SpannableStringBuilder(" ");
    }
    
    public int getId() {
        return windowId;
    }

    //--------------------------------------------------------------------
    // GLK METHODS
    
    public int gestalt(int sel, int val, int[] arr) {
        // not used by model.c, but really should be implemented
        return 0;
    }

    public File namedFile(String filename, int usage) {
        // too much work for this simple demo
        return null;
    }

    public File promptFile(int usage, int fmode) {
        // too much work for this simple demo
        return null;
    }
    
    private void translateEvent(Message msg, int[] event) {
        event[0] = msg.what;
        Object obj = msg.obj;
        if (obj == null) {
            event[1] = 0;
        } else {
            GlkWindow win = (GlkWindow) obj;
            event[1] = win.getId();
        }
        event[2] = msg.arg1;
        event[3] = msg.arg2;
    }
    
    public void select(int[] event) {
        Message msg = eventQueue.select();
        translateEvent(msg, event);
    }

    public void poll(int[] event) {
        Message msg = eventQueue.poll();
        translateEvent(msg, event);
    }

    public void exit() {}

    public void windowOpen(GlkWindow splitwin, int method, int size,
            int wintype, int id, GlkWindow[] wins) {
        
        if (splitwin != null || windowId != 0) {
            return;
        }
        if (wintype != GlkWinType.TextBuffer) {
            return;
        }

        /* Keep track of the provided window id.*/
        windowId = id;

        /* Give GlkJNI a reference to this window. */
        wins[0] = this;
    }

    public void windowClose(GlkWindow win) {
        if (win != this) {
            Log.e("roboglk", "window_close: invalid id");
            return;
        }
        
        windowId = 0;
    }

    public void setStyleHint(int wintype, int styl, int hint, int val) {}

    public void clearStyleHint(int wintype, int styl, int hint) {}

    public GlkSChannel createChannel() {
        return null;
    }

    public void destroyChannel(GlkSChannel schan) {}

    public void setSoundLoadHint(int num, boolean flag) {}

    public boolean getImageInfo(int num, int[] dim) {
        return false;
    }

    public void requestTimer(int millisecs) {}

    public void cancelTimer() {}
    
    //--------------------------------------------------------------------
    // METHODS CALLED FROM UI THREAD
    
    private void doLineInput() {
        myView.append("\n");
        eventQueue.putEvent(eventQueue.newLineInputEvent(this, lineLenCur));
        lineLenMax = 0;
        lineLenCur = 0;
    }
    
    public boolean keyInput(View v, int keyCode, KeyEvent event) {
        if (!charInput && (lineLenMax == 0)) {
            return false;
        }
        if (tb.length() != 1)
            tb = new SpannableStringBuilder(" ");
        if (Selection.getSelectionEnd(tb) != 1 ||
                Selection.getSelectionStart(tb) != 1)
            Selection.setSelection(tb, 1);
        switch (event.getAction()) {
        case KeyEvent.ACTION_DOWN:
            listener.onKeyDown(v, tb, keyCode, event);
            break;
        case KeyEvent.ACTION_UP:
            listener.onKeyUp(v, tb, keyCode, event);
            break;
        }
        switch (tb.length()) {
        case 0:  // delete
            // delete not implemented
            return false;
        case 1:  // arrow, shift, click, etc
            // not implemented
            return false;
        case 2:  // insert one char
            break;
        }
        
        char c = tb.charAt(1);
        
        if (!uniInput && c > 0xFF) {
            return false;
        }
        
        int ch;
        if (c == '\n') {
            ch = GlkKeycode.Return;
        } else {
            ch = c;
        }

        if (charInput) {
            // TODO (but model.c doesn't use it)
        }
        
        if (lineLenMax > 0) {
            if (ch == GlkKeycode.Return) {
                doLineInput();
            } else {
                myView.append(tb.subSequence(1, 2));
                if (lineBuf != null) {
                    // for Latin-1 input
                    lineBuf.put((byte) ch);
                } else {
                    lineUniBuf.put(ch);
                }
                lineLenCur++;
                if (lineLenCur == lineLenMax) {
                    doLineInput();
                }
            }
        }
        // else if line input:
        //    if enter, generate event (turn off input)
        //    echo the key
        //    else if input is maxlen, generate event (turn off input)
        //

        return true;
    }

    /*--------------------------------------------------------------------
     * TEXT BUFFER WINDOW METHODS
     */
    
    @Override
    public void cancelCharEvent() {
        uiHandler.post(new Runnable() {
            public void run() {
                charInput = false;
                uniInput = false;
            } 
        });
    }

    @Override
    public int cancelLineEvent() {
        uiHandler.post(new Runnable() {
            public void run() {
                lineBuf = null;
                lineUniBuf = null;
                uniInput = false;
                lineLenMax = 0;
                lineLenCur = 0;
            } 
        });
        return 0;
    }

    @Override
    public void clear() {
        uiHandler.post(new Runnable() {
            public void run() {
                myView.setText("");
            } 
        });
    }

    @Override
    public void print(final String str) {
        uiHandler.post(new Runnable() {
            public void run() {
                myView.append(str);
            }
        });
    }

    @Override
    public void requestCharEvent(final boolean unicode) {
        uiHandler.post(new Runnable() {
            public void run() {
                charInput = true;
                uniInput = unicode;
            } 
        });
    }

    @Override
    public void requestLineEvent(final ByteBuffer buf, final int maxlen, final int initlen) {
        uiHandler.post(new Runnable() {
            public void run() {
                lineBuf = buf;
                lineLenMax = maxlen;
                lineLenCur = initlen;
                uniInput = false;
            } 
        });
    }

    @Override
    public void requestLineEventUni(final IntBuffer buf, final int maxlen, final int initlen) {
        uiHandler.post(new Runnable() {
            public void run() {
                lineUniBuf = buf;
                lineLenMax = maxlen;
                lineLenCur = initlen;
                uniInput = true;
            } 
        });
    }

    @Override
    public void cancelLinkEvent() {}

    @Override
    public void cancelMouseEvent() {}
    
    @Override
    public boolean distinguishStyles(int styl1, int styl2) {
        return false;
    }

    @Override
    public boolean drawInlineImage(int num, int alignment) {
        // Pertains to inline images, which will probably
        // not be supported.
        return false;
    }

    @Override
    public boolean drawInlineImage(int num, int alignment, int width,
            int height) {
        // Pertains to inline images, which will probably
        // not be supported.
        return false;
    }

    @Override
    public void flowBreak() {
        // Pertains to inline images, which will probably
        // not be supported.
    }

    @Override
    public void getSize(int[] dim) {
        // not used by model.c, but should be implemented even if
        // you are only going for minimal conformance with the Glk
        // spec
    }

    @Override
    public int measureStyle(int styl, int hint) {
        // The C code interprets this exception as
        // "implementation cannot measure the style"
        throw new RuntimeException();
    }
    
    @Override
    public void requestLinkEvent() {}

    @Override
    public void requestMouseEvent() {}

    @Override
    public void setLinkValue(int val) {}

    @Override
    public void setStyle(int val) {}
    
    
}
