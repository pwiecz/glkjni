package org.brickshadow.roboglk.window;


import android.content.Context;
import android.text.method.MovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.util.AttributeSet;


/**
 * A TextView class for text-buffer windows. The main reason for this
 * class is to override {@link #getDefaultMovementMethod()} to return
 * a {@code ScrollingMovementMethod}; this is important for correct 
 * handling of the MORE prompt.
 */
public class TextBufferView extends TextWindowView {

    public TextBufferView(Context context) {
        super(context);
    }
    
    public TextBufferView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    
    public TextBufferView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected MovementMethod getDefaultMovementMethod() {
        return ScrollingMovementMethod.getInstance();
    }

}
