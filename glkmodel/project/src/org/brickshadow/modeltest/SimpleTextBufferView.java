package org.brickshadow.modeltest;


import android.content.Context;
import android.text.method.ArrowKeyMovementMethod;
import android.text.method.MovementMethod;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.TextView;


public class SimpleTextBufferView extends TextView {
    
    public SimpleTextBufferView(Context context) {
        super(context);
    }
    
    public SimpleTextBufferView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onCheckIsTextEditor() {
      return true;
    }
    
    @Override
    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
      return new RoboGlkInputConnection(this, false);
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
      return false;
    }

    
    @Override
    protected boolean getDefaultEditable() {
        return true;
    }

    @Override
    protected MovementMethod getDefaultMovementMethod() {
        return ArrowKeyMovementMethod.getInstance();
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        super.setText(text, BufferType.EDITABLE);
    }
    
}
