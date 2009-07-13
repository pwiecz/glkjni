package org.brickshadow.roboglk;

import java.util.LinkedList;

import android.os.Message;

public class GlkEventQueue {
    private LinkedList<Message> selectQueue = new LinkedList<Message>();
    private LinkedList<Message> pollQueue = new LinkedList<Message>();
    
    public Message newEmptyEvent() {
        Message msg = Message.obtain();
        msg.what = GlkEventType.None;
        msg.obj = null;
        msg.arg1 = 0;
        msg.arg2 = 0;
        return msg;
    }
    
    public Message newCharInputEvent(GlkWindow win, int c) {
        Message msg = Message.obtain();
        msg.what = GlkEventType.CharInput;
        msg.obj = win;
        msg.arg1 = c;
        msg.arg2 = 0;
        return msg;
    }
    
    public Message newLineInputEvent(GlkWindow win, int len) {
        Message msg = Message.obtain();
        msg.what = GlkEventType.LineInput;
        msg.obj = win;
        msg.arg1 = len;
        msg.arg2 = 0;
        return msg;
    }
    
    // Call only from interpreter thread
    public synchronized Message poll() {
        return pollQueue.poll();
    }
    
    // Call only from interpreter thread
    public synchronized Message select() {
        try {
            while (selectQueue.isEmpty() && pollQueue.isEmpty()) {
                wait();
            }
            Message msg;
            msg = selectQueue.poll();
            if (msg != null) {
                return msg;
            }
            // Can't get here unless pollqueue is non-empty
            return pollQueue.poll();
            
        } catch (InterruptedException e) {
            // TODO: throw a more useful exception
            throw new RuntimeException();
        }
    }
    
    // Call only from ui thread
    public synchronized void putEvent(Message msg) {
        boolean wasEmpty = selectQueue.isEmpty() && pollQueue.isEmpty();
        
        switch (msg.what) {
        case GlkEventType.Arrange:
        case GlkEventType.Redraw:
        case GlkEventType.SoundNotify:
        case GlkEventType.Timer:
            pollQueue.add(msg);
            break;
        default:
            selectQueue.add(msg);
            break;
        }
        
        if (wasEmpty) {
            notify();
        }
    }
}
