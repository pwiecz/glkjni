package org.brickshadow.modeltest;


import org.brickshadow.roboglk.Glk;
import org.brickshadow.roboglk.GlkFactory;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;


public class ModelTest extends Activity {
    Glk glk;
    TextView tv;
    Thread terpThread;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        tv = new SimpleTextBufferView(this);
        glk = new ModelGlk(this, tv);
        
        tv.setFocusable(true);
        tv.setCursorVisible(false);
        setContentView(tv);
        
        startInterpreter();
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