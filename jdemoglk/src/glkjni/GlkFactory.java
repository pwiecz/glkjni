package glkjni;

import org.brickshadow.jglk.blorb.BlorbReader;
import org.brickshadow.jglk.demo.DemoGlk;

public class GlkFactory {
    public static final String storyPath = null;

    public static native boolean startup(Glk glk, String[] args);
    
    private static BlorbReader bReader;

    public static void newInstance(String[] args) {
        if (!startup(new DemoGlk(), args)) {
            System.exit(0);
        }
        bReader = BlorbReader.newBlorbReader(storyPath);
    }
    
    public static BlorbReader getBlorbReader() {
        return bReader;
    }
}
