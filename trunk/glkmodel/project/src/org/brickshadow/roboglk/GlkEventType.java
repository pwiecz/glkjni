package org.brickshadow.roboglk;

/**
 * Event types.
 */
public interface GlkEventType {
    int None = 0;
    int Timer = 1;
    int CharInput = 2;
    int LineInput = 3;
    int MouseInput = 4;
    int Arrange = 5;
    int Redraw = 6;
    int SoundNotify = 7;
    int HyperLink = 8;
}
