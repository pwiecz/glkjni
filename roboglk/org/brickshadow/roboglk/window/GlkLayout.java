package org.brickshadow.roboglk.window;


import org.brickshadow.roboglk.GlkEventQueue;
import org.brickshadow.roboglk.GlkWinType;
import org.brickshadow.roboglk.GlkWindow;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.AbsoluteLayout;


public abstract class GlkLayout extends AbsoluteLayout {

	protected class Group {
		public final GlkWindow win;
		public final View view;
		
		public Group(GlkWindow win, View view) {
			this.win = win;
			this.view = view;
		}
	}
	
	protected final Activity activity;
	protected GlkEventQueue queue;
	
	public GlkLayout(Activity activity) {
		super(activity);
		this.activity = activity;
		setFocusableInTouchMode(true);
	}
	
	@Override
	protected LayoutParams generateDefaultLayoutParams() {
		return new LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT, 0, 0);
	}
	
	public void initialize(GlkEventQueue queue) {
		this.queue = queue;
		// TODO: remove all child views (on ui thread)
	}

	private Group mainPair;
	private Group newGroup;
	private UISync uiWait;
	
	public final GlkWindow[] addGlkWindow(GlkWindow splitwin, int method,
			int size, final int wintype, final int id) {
		
		// TODO: only until more window types are supported
		if (mainPair != null && splitwin != null) {
			return new GlkWindow[] { null, null };
		}
		if (wintype != GlkWinType.TextBuffer) {
			return new GlkWindow[] { null, null };
		}
		
		uiWait = new UISync(activity);
		uiWait.waitFor(new Runnable() {
			public void run() {
				newGroup = createGroup(wintype, id);
				if (newGroup != null) {
					addView(newGroup.view);
					newGroup.view.requestFocus();
				}
				uiWait.stopWaiting(null);
			}
		});

		if (newGroup == null) {
			return new GlkWindow[] { null, null };
		}
		
		mainPair = newGroup;
		return new GlkWindow[] { mainPair.win, null };
	}
	
	
	
	public final void removeGlkWindow(GlkWindow win) {
		if (win != mainPair.win) {
			Log.e("roboglk", "window_close: invalid id");
            return;
		}
		
		activity.runOnUiThread(new Runnable() {
			public void run() {
				mainPair.view.setVisibility(GONE); // needed?
				removeView(mainPair.view);
				mainPair = null;
			}
		});
	}
	
	protected abstract Group createGroup(int wintype, int id);
}
