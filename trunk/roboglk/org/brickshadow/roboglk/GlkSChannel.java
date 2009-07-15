package org.brickshadow.roboglk;

/**
 * A Glk sound channel
 */
public interface GlkSChannel {
    
    /**
     * Sets the volume for the channel. A newly created channel is at
     * full volume (0x10000). Setting the volume to zero is not supposed
     * to stop playback.
     * 
     * @param vol
     *           The volume.<p>
     */
    void setVolume(int vol);
    
    /**
     * Plays a sound on the channel. If a sound was already playing
     * on the channel, it must be stopped without notification.
     * <p>
     * A sound notification event will be generated, if requested, after
     * the last repetition has finished.
     * 
     * @param num the sound resource number
     * @param repeats the number of times to repeat the sound; it will be
     *                either -1 (meaning repeat forever) or a non-zero
     *                positive value
     * @param notify if non-zero, requests notification when the sound
     *               stops playing
     * @return {@code true} if the sound started playing
     */
    boolean play(int num, int repeats, int notify);
    
    /**
     * Stops the current sound without notification. If there is no
     * current sound, does nothing.
     */
    void stop();
}
