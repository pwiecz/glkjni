package org.brickshadow.roboglk.window;

import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.BaseInputConnection;
import android.view.inputmethod.InputConnection;

public class RoboInputConnection extends BaseInputConnection implements InputConnection {

	public RoboInputConnection(View targetView, boolean fullEditor) {
		super(targetView, fullEditor);
	}

	@Override
	public boolean sendKeyEvent(KeyEvent event) {
		Log.i("roboglk", "RoboInputConnection.sendKeyEvent");
		return super.sendKeyEvent(event);
	}

	@Override
	public boolean performEditorAction(int editorAction) {
		Log.i("roboglk", "RoboInputConnection.performEditorAction");
		sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER));
		return super.performEditorAction(editorAction);
	}
}
