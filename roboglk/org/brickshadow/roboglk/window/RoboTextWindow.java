package org.brickshadow.roboglk.window;


interface RoboTextWindow {
	/**
     * Handles normal characters during single-character input.
     * 
     * @param c a character.
     */
	void recordKey(char c);
	
	/**
     * Handles special keys during single-character input.
     * 
     * @param c a keycode
     */
	void recordKey(int c);
	
	/**
     * Handles line input.
     */
	void recordLine(char[] line, int len, boolean isEvent);
	
	/**
     * Called by the associated {@link TextIO} object in response to a
     * request for window size.
     * 
     * @param x the width of the window
     * @param y the height of the window
     */
	void setSize(int x, int y);
	
	/**
     * Called by the associated {@link TextIO} object when glk
     * cancels line input.
     * 
     * @param len the current input length
     */
	void setCurrInputLength(int len);

	/**
     * Called by the associated {@link TextIO} object in response to a
     * request for style distinguishability.
     */
	void setStyleDistinguish(boolean distinct);
	
	/**
     * Called by the associated {@link TextIO} object in response to a
     * request for style measurement.
     */
	void setStyleMeasure(boolean success, int val);
}
