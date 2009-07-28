package org.brickshadow.roboglk.window;

import android.app.Activity;

class UISync {
	private final Activity activity;
	private volatile boolean isWaiting;
	
	public UISync(Activity activity) {
		this.activity = activity;
	}
	
	public void waitFor(Runnable r) {
		synchronized(this) {
			isWaiting = true;
		}
		
		activity.runOnUiThread(r);
		
		synchronized(this) {
            try {        
                while (isWaiting) {
                    wait();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
	}
	
	public void stopWaiting(Runnable r) {
		synchronized(this) {
			boolean wasWaiting = isWaiting;
			if (r != null) {
				r.run();
			}
			isWaiting = false;
			if (wasWaiting) {
				notify();
			}
		}
	}
}
