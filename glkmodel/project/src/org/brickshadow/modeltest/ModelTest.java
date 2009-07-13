package org.brickshadow.modeltest;


import org.brickshadow.roboglk.Glk;
import org.brickshadow.roboglk.GlkFactory;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;


public class ModelTest extends Activity {
    Glk glk;
    TextView tv;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        initView();

        glk = new Glk(tv, new Handler());
        
        startInterpreter();
    }
    
    private void initView() {
        //setContentView(R.layout.main);
        //tv = (TextView) findViewById(R.id.body);
        tv = new TextView(this);
        
        tv.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                /*
                 * Normally there would have to be a way to associate
                 * a view with a GlkWindow; in this implementation there
                 * is only one window to dispatch key events to.
                 */
                return glk.keyInput(v, keyCode, event);
            }
        });
        
        tv.setFocusable(true);
        tv.setCursorVisible(false);
        setContentView(tv);
    }
    
    private void startInterpreter() {
        Thread terpThread = new Thread(new Runnable() {
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