package org.brickshadow.roboglk.window;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.TextView;

public abstract class TextWindowView extends TextView {

	public TextWindowView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	public TextWindowView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
	
	public TextWindowView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
      return false;
    }
}
