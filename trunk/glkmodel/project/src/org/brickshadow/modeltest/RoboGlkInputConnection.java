package org.brickshadow.modeltest;


import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.BaseInputConnection;
import android.view.inputmethod.InputConnection;


public class RoboGlkInputConnection extends BaseInputConnection implements InputConnection {

    public RoboGlkInputConnection(View targetView, boolean fullEditor) {
        super(targetView, fullEditor);
      }

      @Override
      public boolean sendKeyEvent(KeyEvent event) {
         return super.sendKeyEvent(event);
      }

      @Override
      public boolean performEditorAction(int editorAction) {
        sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER));
        return super.performEditorAction(editorAction);
      }
      
}
