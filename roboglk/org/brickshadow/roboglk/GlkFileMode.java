package org.brickshadow.roboglk;

/**
 * File mode constants.
 */
public interface GlkFileMode {
    
    /** Write mode. */
    int Write = 0x01;
    
    /** Read mode. */
    int Read = 0x02;
    
    /** Read/write mode. */
    int ReadWrite = 0x03;
    
    /** Append mode. */
    int WriteAppend = 0x05;
}
