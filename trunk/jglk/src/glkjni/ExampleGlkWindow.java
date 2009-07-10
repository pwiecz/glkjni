package glkjni;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.brickshadow.jglk.GlkImageAlign;
import org.brickshadow.jglk.GlkStyle;
import org.brickshadow.jglk.GlkStyleHint;

/**
 * An example of the GlkWindow interface.
 * 
 * <h3>Implementation Notes</h3>
 * 
 * Instead of specifying separate interfaces for different kinds of
 * windows, GlkJNI forwards all Glk window functions through a single
 * Java interface. Frontends can define their own hierarchy of
 * sub-interfaces.
 * <p>
 * Implementations of window methods will not need to check that they
 * are appropriate for the type of the window they are called on; GlkJNI
 * checks this before forwarding calls to Java methods.
 * <p>
 * See the {@link glkjni glkjni package} for other general implementation
 * notes.
 */
public interface ExampleGlkWindow {

    /**
     * Forwarded from {@code glk_window_clear}.
     * Valid in all output windows.
     * <p>
     * Clears the window.
     */
    void clear();

    /**
     * Forwarded from {@code glk_window_get_size}.
     * Valid in all output windows.
     * <p>
     * Returns the size of the window in the {@code dim[]} array.
     * <ul>
     * <li><b>{@code dim[0]}</b> - the window width</li>
     * <li><b>{@code dim[1]}</b> - the window height</li>
     * 
     * @param dim
     *           The width and height of the window.<p>
     */
    void getSize(int[] dim);

    /**
     * Forwarded from {@code glk_request_mouse_event}.
     * Valid in all output windows.
     * <p>
     * Requests a mouse event for the window.
     */
    void requestMouseEvent();

    /**
     * Forwarded from {@code glk_cancel_mouse_event}.
     * Valid in all output windows.
     * <p>
     * Cancels mouse events for the window. This method may be called
     * when there is no pending request for a mouse event.
     */
    void cancelMouseEvent();

    /**
     * Forwarded from {@code glk_set_style}.
     * <p>
     * Changes the current output style of the text window.
     * 
     * @param val
     *           One of the constants in {@link GlkStyle}.<p>
     */
    void setStyle(int val);

    /**
     * Forwarded from {@code glk_style_measure}.
     * <p>
     * Returns the current value of a style attribute in the text window.
     * 
     * @param styl
     *           One of the constants in {@link GlkStyle}.<p>
     * @param hint
     *           One of the constants in {@link GlkStyleHint}.<p>
     * @throws RuntimeException
     *           if the style attribute cannot be measured.
     * @return
     *           The current value of the style attribute.
     */
    int measureStyle(int styl, int hint);

    /**
     * Forwarded from {@code glk_style_distinguish}.
     * <p>
     * Checks if two styles are visually distinguishable in the text
     * window.
     * 
     * @param styl1
     *           One of the constants in {@link GlkStyle}.<p>
     * @param styl2
     * 			 One of the constants in {@link GlkStyle}.<p>
     * @return
     *           True if the styles are visually distingushable.
     */
    boolean distinguishStyles(int styl1, int styl2);

    /**
     * Forwarded from the Glk printing functions.
     * <p>
     * Prints a string in the text window.
     * <p>
     * <b>NOTE:</b> GlkJNI buffers text output until the next call to
     * {@code glk_select}, {@code glk_poll}, {@code glk_set_style},
     * {@code glk_set_hyperlink}, {@code glk_request_line_input},
     * {@code glk_window_move_cursor}, or {@code glk_exit}.
     * 
     * @param str
     *           The string to print.<p>
     */
    void print(String str);

    /**
     * Forwarded from {@code glk_request_char_event} and
     * {@code glk_request_char_event_uni}.
     * <p>
     * Requests character input in the window. This method will not be
     * called when there is a pending request for character or line input.
     * 
     * @param unicode
     *           True if Unicode input is requested, false for Latin-1.
     */
    void requestCharEvent(boolean unicode);

    /**
     * Forwarded from {@code glk_cancel_char_event}.
     * <p>
     * Cancels character input for the text window. This method may be
     * called when there is no pending request for character input.
     */
    void cancelCharEvent();

    /**
     * Forwarded from {@code glk_request_line_event}.
     * <p>
     * Requests Latin-1 line input in the text window.
     * 
     * @param buf
     *           A buffer for storing the Latin-1 bytes of the user's
     *           input.<p>
     * @param maxlen
     *           The available length of the buffer.<p>
     * @param initlen
     *           If non-zero, the length of pre-existing data in the
     *           buffer.<p>
     */
    void requestLineEvent(ByteBuffer buf, int maxlen, int initlen);

    /**
     * Forwarded from {@code glk_request_line_event_uni}.
     * <p>
     * Requests Unicode line input in the text window.
     * 
     * @param buf
     *           A buffer for storing the Unicode codepoints of the user's
     *           input.<p>
     * @param maxlen
     *           The available length of the buffer.<p>
     * @param initlen
     *           If non-zero, the length of pre-existing data in the
     *           buffer.<p>
     */
    void requestLineEventUni(IntBuffer buf, int maxlen, int initlen);

    /**
     * Forwarded from {@code glk_window_set_hyperlink}.
     * <p>
     * Sets the link value for the current text position in the window.
     * 
     * @param val
     *           The link value.<p>
     */
    void setLinkValue(int val);

    /**
     * Forwarded from {@code glk_request_hyperlink_event}.
     * <p>
     * Requests hyperlink input in the text window. This method will not
     * be called when there is a pending request for hyperlink input.
     */
    void requestLinkEvent();

    /**
     * Forwarded from {@code glk_cancel_hyperlink_event}.
     * <p>
     * Cancels hyperlink input for the text window. This method may be
     * called when there is no pending request for hyperlink input.
     */
    void cancelLinkEvent();

    /**
     * Forwarded from {@code glk_cancel_line_event}.
     * <p>
     * Cancels line input for the window. This method may be called
     * when there is no pending request for a line input event.
     * 
     * @return
     *           The number of characters, if any, already entered
     *           during an active line input request.
     */
    int cancelLineEvent();

    /**
     * Forwarded from {@code glk_window_move_cursor}.
     * <p>
     * Positions the cursor in the text grid window.
     * 
     * @param xpos
     *           The x position of the cursor. If this is past the end of
     *           a line, the cursor moves to the beginning of the next
     *           line.<p>
     * @param ypos
     *           The y position of the cursor. If this is greater than the
     *           height of the window, the cursor goes "off screen" and
     *           further printing has no effect.
     */
    void moveCursor(int xpos, int ypos);

    /**
     * Forwarded from {@code glk_window_set_arrangement}.
     * Valid in pair windows.
     * <p>
     * Rearranges the children of the pair window.
     * @param method
     *           The new split method.<p>
     * @param size
     *           The new size constraint.<p>
     * @param key
     *           The new key window.<p>
     */
    void setArrangement(int method, int size, GlkWindow key);

    /**
     * Forwarded from {@code glk_image_draw}.
     * <p>
     * Draws an image in the text buffer window.
     * 
     * @param num
     *           The image resource number.<p>
     * @param alignment
     *           One of the constants in {@link GlkImageAlign}.<p>
     * @return
     *           True if the image was drawn.
     */
    boolean drawInlineImage(int num, int alignment);

    /**
     * Forwarded from {@code glk_image_draw}.
     * <p>
     * Draws a scaled image in the text buffer window.
     * 
     * @param num
     *           The image resource number.<p>
     * @param alignment
     *           One of the constants in {@link GlkImageAlign}.<p>
     * @param width
     *           The display width of the image.<p>
     * @param height
     *           The display height of the image.<p>
     * @return
     *           True if the image was drawn.
     */
    boolean drawInlineImage(int num, int alignment, int width, int height);

    /**
     * Forwarded from {@code glk_window_flow_break}.
     * <p>
     * Breaks the text in the window below a margin image.
     */
    void flowBreak();

    /**
     * Forwarded from {@code glk_image_draw}.
     * Valid in graphics windows.
     * <p>
     * Draws an image in the window.
     * 
     * @param num
     *           The number of the image resource.<p>
     * @param x
     *           The x coordinate of the image.<p>
     * @param y
     *           The y coordinate of the image.<p>
     * @return
     *           True if the image was drawn.
     */
    boolean drawImage(int num, int x, int y);

    /**
     * Forwarded from {@code glk_image_draw}.
     * Valid in graphics windows.
     * <p>
     * Draws an image in the window, scaled to a certain size. The x and
     * y coordinate may lie outside the window; width and height will
     * be non-zero and positive.
     * 
     * @param num
     *           The number of the image resource.<p>
     * @param x
     *           The x coordinate of the image.<p>
     * @param y
     *           The y coordinate of the image.<p>
     * @param width
     *           The display width of the image.<p>
     * @param height
     *           The display height of the image.<p>
     * @return
     *           True if the image was drawn.
     */
    boolean drawImage(int num, int x, int y, int width, int height);

    /**
     * Forwarded from {@code glk_window_set_background_color}.
     * Valid in graphics windows.
     * <p>
     * Sets the background color of the window. This does not take effect
     * until the next clear or redraw.
     * 
     * @param color
     *           The background color.<p>
     */
    void setBackgroundColor(int color);

    /**
     * Forwarded from {@code glk_window_erase_rect}.
     * Valid in graphics windows.
     * <p>
     * Clears a rectangle with the window's background color. The
     * dimensions of the rectangle might not lie entirely within the
     * window; the width and height will be non-zero and positive.
     * 
     * @param left
     *           The leftmost x coordinate of the rectangle.<p>
     * @param top
     *           The topmost y coordinate of the rectangle.<p>
     * @param width
     *           The width of the rectangle.<p>
     * @param height
     *           The height of the rectangle.<p>
     */
    void eraseRect(int left, int top, int width, int height);

    /**
     * Forwarded from {@code glk_window_fill_rect}.
     * Valid in graphics windows.
     * <p>
     * Fills a rectangle with a certain color.  The dimensions of the
     * rectangle might not lie entirely within the window; the width and
     * height will be non-zero and positive.
     * 
     * @param color
     *           The fill color.<p>
     * @param left
     *           The leftmost x coordinate of the rectangle.<p>
     * @param top
     *           The topmost y coordinate of the rectangle.<p>
     * @param width
     *           The width of the rectangle.<p>
     * @param height
     *           The height of the rectangle.<p>
     */
    void fillRect(int color, int left, int top, int width, int height);
}
