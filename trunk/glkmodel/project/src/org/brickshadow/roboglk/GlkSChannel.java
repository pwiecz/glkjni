package org.brickshadow.roboglk;

/**
 * A Glk sound channel.
 * <p>
 * See the {@link glkjni glkjni package} for general implementation
 * notes.
 */
public interface GlkSChannel {

    /**
     * Forwarded from {@code glk_schannel_set_volume}.
     * <p>
     * Sets the volume for the channel.
     * 
     * @param vol
     *           The volume.<p>
     */
    void setVolume(int vol);

    /**
     * Forwarded from {@code glk_schannel_play_ext}.
     * <p>
     * Plays a sound on the channel. If a sound was already playing,
     * it is stopped.
     * 
     * @param num
     *           The number of the sound resource.<p>
     * @param repeats
     *           The number of times to repeat the sound. The value will
     *           either be -1 (meaning repeat forever) or a positive
     *           number.
     * @param notify
     *           True if a sound notification event should occur when
     *           the sound finishes playing.
     * @return
     *           True if the sound started playing.
     */
    boolean play(int num, int repeats, boolean notify);

    /**
     * Forwarded from {@code glk_schannel_stop}, or from
     * {@code glk_schannel_play_ext} when {@code repeats} is zero.
     * <p>
     * Stops the current sound. If there is no current sound, does
     * nothing.
     */
    void stop();
}
