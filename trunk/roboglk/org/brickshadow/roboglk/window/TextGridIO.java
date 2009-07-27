package org.brickshadow.roboglk.window;

public class TextGridIO extends TextIO {

	TextGridIO(TextGridView tv) {
		super(tv);
	}

	@Override
	public void doClear() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void doPrint(String str) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int[] getWindowSize() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void textEcho(CharSequence str) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Does nothing. Newlines after input are not echoed into a text grid
	 * window.
	 */
	@Override
	protected final void textEchoNewline() {}

	@Override
	public void doStyle(int style) {}
	
	@Override
	public boolean doDistinguishStyles(int styl1, int styl2) {
		return false;
	}

	@Override
	public int doMeasureStyle(int styl, int hint)
			throws StyleMeasurementException {
		throw new StyleMeasurementException();
	}

	/**
	 * Does nothing. History is not supported in text grid windows.
	 */
	@Override
	protected void extendHistory() {}

	/**
	 * Does nothing. History is not supported in text grid windows.
	 */
	@Override
	protected void historyNext() {}

	/**
	 * Does nothing. History is not supported in text grid windows.
	 */
	@Override
	protected void historyPrev() {}
}
