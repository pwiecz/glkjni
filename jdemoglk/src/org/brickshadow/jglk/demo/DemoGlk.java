package org.brickshadow.jglk.demo;

import glkjni.Glk;
import glkjni.GlkFactory;
import glkjni.GlkSChannel;
import glkjni.GlkWindow;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Scanner;

import javax.imageio.ImageIO;

import org.brickshadow.jglk.GlkEventType;
import org.brickshadow.jglk.GlkFileMode;
import org.brickshadow.jglk.GlkFileUsage;
import org.brickshadow.jglk.GlkGestalt;
import org.brickshadow.jglk.GlkKeycode;
import org.brickshadow.jglk.GlkWinType;
import org.brickshadow.jglk.blorb.BlorbReader;
import org.brickshadow.jglk.window.GlkTextBufferWindow;

/*
 * An example Java frontend for GlkJNI, using console I/O.
 * 
 * Since this is a bare-minimum implementation which only supports
 * a single text-buffer window (just like CheapGlk), it uses the
 * same class for both the primary Glk interface and the window
 * implementation.
 */
public class DemoGlk extends GlkTextBufferWindow implements Glk {
    /*
     * The width of the screen, set from the "glk.screenwidth"
     * property. Default 80.
     */
    private final int screenWidth;

    /*
     * The height of the screen, set from the "glk.screenheight"
     * property. Default 24.
     */
    private final int screenHeight;

    /* true when Latin-1 character input has been requested. */
    private boolean charRequest = false;

    /* true when Unicode character input has been requested. */
    private boolean charRequestUni = false;

    /* Non-null when Latin-1 line input has been requested. */
    private ByteBuffer lineBuf;

    /* Non-null when Unicode line input has been requested. */
    private IntBuffer lineBufUni;

    /* The length of the line input buffer (in bytes or codepoints). */
    private int lineBufLen;

    /* The id assigned to the window by GlkJNI, or 0 for "no windows". */
    private int windowId = 0;

    public DemoGlk() {
        screenWidth = getDimension("glk.screenwidth", 80);
        screenHeight = getDimension("glk.screenheight", 24);
        System.out.println("Welcome to the demo JNIGlk implementation.");
        System.out.println();
    }

    /*
     * Tries to interpret a system property as a positive integer; if
     * this fails, returns dflt.
     */
    private int getDimension(String key, int dflt) {
        String property = System.getProperty(key);
        if (property == null) {
            return dflt;
        }

        try {
            int value = Integer.parseInt(property);
            return (value < 0 ? dflt : value);
        } catch (NumberFormatException exc) {
            return dflt;
        }
    }

    /*
     * Reports on the capabilities of this implementation.
     */
    public int gestalt(int sel, int val, int[] arr) {
        int TRUE = 1;
        int FALSE = 0;

        switch (sel) {
        case GlkGestalt.Version:
            return 0x00000700;

        /*
         * The responses for CharInput and LineInput err on the side
         * of caution.
         */
        case GlkGestalt.CharInput:
            if (val >= 32 && val <= 127 || val == GlkKeycode.Return) {
                return TRUE;
            } else {
                return FALSE;
            }

        case GlkGestalt.LineInput:
            if (val >= 32 && val <= 127) {
                return TRUE;
            } else {
                return FALSE;
            }

        case GlkGestalt.CharOutput:
            if (arr != null) {
                arr[0] = 1;
            }
            if (val >= 32 && val <= 127) {
                return GlkGestalt.CharOutputExactPrint;
            } else {
                return GlkGestalt.CharOutputCannotPrint;
            }

        /*
         * Possibly a little white lie.
         */
        case GlkGestalt.Unicode:
            return TRUE;

        default:
            return FALSE;
        }
    }

    /*
     * "Opens" the single text-buffer window. All it has to do in this
     * implementation is store the window id and return a reference to
     * this object back to GlkJNI.
     */
    public void windowOpen(GlkWindow splitwin, int method,
            int size, int wintype, int id, GlkWindow[] wins) {

        if (splitwin != null || windowId != 0) {
            return;
        }
        if (wintype != GlkWinType.TextBuffer) {
            return;
        }

        /* Keep track of the provided window id.*/
        windowId = id;

        /* Give GlkJNI a reference to this window. */
        wins[0] = this;
    }

    /*
     * "Closes" the window.
     */
    public void windowClose(GlkWindow win) {
        if (win != this) {
            System.err.println("window_close: invalid id");
            return;
        }
        windowId = 0;
    }

    /*
     * A convenience method that checks for any kind of pending input
     * requests.
     */
    private boolean hasRequest() {
        return (charRequest
                || charRequestUni
                || (lineBuf != null)
                || (lineBufUni != null));
    }

    /*
     * Waits for input (if any has been requested) and returns events
     * to GlkJNI,
     */
    public void select(int[] event) {
        if (windowId == 0 || !hasRequest()) {
            while (true) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    System.exit(0);
                }
            }
        }

        event[1] = windowId;
        event[3] = 0;

        /*
         * Blocks until a line of input has been entered. As with
         * CheapGlk, character input is really just line input that
         * ignores everything past the first character.
         */
        String inLine = new Scanner(System.in).nextLine();
        int c;

        /* Character input. */
        if (charRequest || charRequestUni) {
            /* Translate empty input into the Return keycode */
            if ("".equals(inLine)) {
                c = GlkKeycode.Return;
            }
            /* Translate characters outside Latin-1 to "?" */
            else {
                c = inLine.charAt(0);
                if (charRequest && (c > 0xFF)) {
                    c = '?';
                }
            }

            /* Clear the character input request. */
            charRequest = false;
            charRequestUni = false;

            event[0] = GlkEventType.CharInput;
            event[2] = c;
        }
        /* Line input. */
        else {
            int len = inLine.length();
            if (len > lineBufLen) {
                len = lineBufLen;
            }
            inLine = inLine.substring(0, len);

            /* Latin-1 line input. */
            if (lineBuf != null) {
                for (int i = 0; i < len; i++) {
                    c = inLine.charAt(i);
                    if (c > 0xFF) {
                        lineBuf.put((byte) '?');
                    } else {
                        lineBuf.put((byte) c);
                    }
                }
            }
            /* Unicode line input. */
            else {
                for (int i = 0; i < len; i++) {
                    lineBufUni.put(inLine.codePointAt(i));
                }
            }

            event[0] = GlkEventType.LineInput;
            event[2] = len;
        }
    }

    /*
     * We don't generate any of the kinds of events that poll is
     * supposed to return, so just return an empty event.
     */
    public void poll(int[] event) {
        event[0] = GlkEventType.None;
        event[1] = 0;	// no window id
        event[2] = 0;	// no val1
        event[3] = 0;	// no val2
    }

    /*
     * Returns a File object in reponse to glk_fileref_create_by_name.
     */
    public File namedFile(String filename, int usage) {
        /* Handle any directory components.*/
        String newName = filename
        .replace('/', '-')
        .replace('\\', '-')
        .replace(':', '-')
        .replace(';', '-');

        /*
         * A real implementation could have some way--maybe based on
         * user preference--to set the directory for the file. This
         * one just uses the current working directory.
         */
        return new File(".", newName);
    }

    /*
     * Prompts for a filename in reponse to glk_fileref_create_by_prompt.
     */
    public File promptFile(int usage, int fmode) {
        String prompt;

        switch (usage & GlkFileUsage.TypeMask) {
        case GlkFileUsage.SavedGame:
            prompt = "Enter saved game";
            break;
        case GlkFileUsage.Transcript:
            prompt = "Enter transcript file";
            break;
        case GlkFileUsage.InputRecord:
            prompt = "Enter command record file";
            break;
        case GlkFileUsage.Data:
        default:
            prompt = "Enter data file";
            break;
        }

        if (fmode == GlkFileMode.Read)
            prompt += " to load: ";
        else
            prompt += " to store: ";

        System.out.print(prompt);
        String filename = new Scanner(System.in).nextLine();

        if ("".equals(filename)) return null;

        File file = new File(filename);
        if (file.isAbsolute()) {
            return new File(filename);
        } else {
            return new File(".", filename);
        }
    }

    /*
     * Called during glk_exit.
     */
    public void exit() {}

    /*------------------------------------------------------------------*/
    /* Glk methods not implemented. */

    public void setStyleHint(int wintype, int styl, int hint, int val) {}
    public void clearStyleHint(int wintype, int styl, int hint) {}

    public boolean distinguishStyles(int wintype, int styl1, int styl2) {
        return false;
    }

    public void requestTimer(int millisecs) {}
    public void cancelTimer() {}

    public boolean getImageInfo(int num, int[] dim) {
        return false;
    }

    public GlkSChannel createChannel() {
        return null;
    }
    public void destroyChannel(GlkSChannel schan) {}

    public void setSoundLoadHint(int num, boolean flag) {}

    /*------------------------------------------------------------------*/
    /* Text-buffer window methods.                                      */

    public void print(String str) {
        System.out.print(str);
    }

    public void requestCharEvent(boolean unicode) {
        charRequest = true;
        charRequestUni = unicode;
    }

    public void cancelCharEvent() {
        charRequest = false;
        charRequestUni = false;
    }

    public void requestLineEvent(ByteBuffer buf, int maxlen,
            int initlen) {
        lineBuf = buf;
        lineBufLen = maxlen;
    }

    public void requestLineEventUni(IntBuffer buf, int maxlen,
            int initlen) {
        lineBufUni = buf;
        lineBufLen = maxlen;
    }

    public int cancelLineEvent() {
        lineBuf = null;
        lineBufUni = null;
        lineBufLen = 0;
        return 0;
    }

    public void getSize(int[] dim) {
        dim[0] = screenWidth;
        dim[1] = screenHeight;
    }

    public void clear() {
        for (int i = 0; i < screenHeight; i++) {
            System.out.println();
        }
    }
    
    /*
     * This is just an example of reading an image resource from a blorb
     * file.
     */
    public boolean drawInlineImage(int num, int alignment) {
        BlorbReader bReader = GlkFactory.getBlorbReader();
        if (bReader == null) {
            return false;
        }
        
        InputStream imagestr = bReader.getImageStream(num, false);
        if (imagestr == null) {
            return false;
        }
        
        try {
            BufferedImage image = ImageIO.read(imagestr);
            if (image == null) {
                return false;
            }
            // Draw the image and return true
            return false;
        } catch (IOException e) {
            return false;
        }
    }

    /*------------------------------------------------------------------*/
    /* Other text-buffer window methods not implemented.                */

    public int measureStyle(int styl, int hint) {
        throw new RuntimeException();
    }

    public void setLinkValue(int val) {}
    public void requestLinkEvent() {}
    public void cancelLinkEvent() {}
    public void requestMouseEvent() {}
    public void cancelMouseEvent() {}
    public void setStyle(int val) {}

    public void flowBreak() {}

    public boolean drawInlineImage(int num, int alignment, int width,
            int height) {
        return false;
    }

    public boolean distinguishStyles(int styl1, int styl2) {
        return false;
    }

}
