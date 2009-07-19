package org.brickshadow.modeltest;

import java.io.File;

import org.brickshadow.roboglk.Glk;
import org.brickshadow.roboglk.GlkEventQueue;
import org.brickshadow.roboglk.GlkSChannel;
import org.brickshadow.roboglk.GlkWinType;
import org.brickshadow.roboglk.GlkWindow;
import org.brickshadow.roboglk.window.RoboTextBufferWindow;

import android.app.Activity;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;

public class ModelGlk implements Glk {

    private final GlkEventQueue eventQueue;
    
    private final Activity activity;
    
    private RoboTextBufferWindow mainWin;
    private final TextView tv;
    
    public ModelGlk(Activity activity, TextView tv) {
        this.activity = activity;
        this.tv = tv;
        eventQueue = new GlkEventQueue();
    }
    
    @Override
    public void cancelTimer() {}

    @Override
    public void clearStyleHint(int wintype, int styl, int hint) {}

    @Override
    public GlkSChannel createChannel() {
        return null;
    }

    @Override
    public void destroyChannel(GlkSChannel schan) {}

    @Override
    public int gestalt(int sel, int val, int[] arr) {
        return 0;
    }

    @Override
    public boolean getImageInfo(int num, int[] dim) {
        return false;
    }

    @Override
    public File namedFile(String filename, int usage) {
        return null;
    }

    @Override
    public void poll(int[] event) {
        Message msg = eventQueue.poll();
        GlkEventQueue.translateEvent(msg, event);
    }

    @Override
    public File promptFile(int usage, int fmode) {
        return null;
    }

    @Override
    public void requestTimer(int millisecs) {}

    @Override
    public void select(int[] event) {
        Message msg = eventQueue.select();
        GlkEventQueue.translateEvent(msg, event);
    }

    @Override
    public void setSoundLoadHint(int num, boolean flag) {}

    @Override
    public void setStyleHint(int wintype, int styl, int hint, int val) {}

    @Override
    public void windowClose(GlkWindow win) {
        if (win != mainWin) {
            Log.e("roboglk", "window_close: invalid id");
            return;
        }
        
        mainWin = null;
    }

    @Override
    public void windowOpen(GlkWindow splitwin, int method, int size,
            int wintype, int id, GlkWindow[] wins) {

        if (splitwin != null && mainWin != null) {
            return;
        }
        if (wintype != GlkWinType.TextBuffer) {
            return;
        }
        
        wins[0] = new RoboTextBufferWindow(
                activity,
                eventQueue,
                new ModelTextBufferIO(tv),
                id);
    }

}
