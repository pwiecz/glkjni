package org.brickshadow.modeltest;


import org.brickshadow.roboglk.Glk;
import org.brickshadow.roboglk.GlkFactory;
import org.brickshadow.roboglk.window.TextBufferView;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;


public class ModelTest extends Activity {
    Glk glk;
    TextBufferView tv;
    Thread terpThread;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        tv = new TextBufferView(this);
        glk = new ModelGlk(this, tv);
        
        tv.setFocusable(true);
        setContentView(tv);
    }
    

    /**
     * Waits for the main window to be focused before starting the
     * interpreter. In a real app, the interpreter is not run on startup
     * so you shouldn't have to worry about this.
     */
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (terpThread == null && hasFocus == true) {
            startInterpreter();
        }
    }
    
    private void startInterpreter() {
        terpThread = new Thread(new Runnable() {
           @Override
            public void run() {
               String[] args = new String[] {"model"};
               if (GlkFactory.startup(glk, args)) {
                   GlkFactory.run();
               }
               Log.i("model", "The interpreter has finished");
               finish();
            } 
        });
        terpThread.start();
    }
    
    static {
        System.loadLibrary("model");
    }
}