package org.brickshadow.jglk.blorb;


import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Map;


public class BlorbReader {
    public static BlorbReader newBlorbReader(String filename) {
        if (filename == null) {
            return null;
        }

        try {
            return new BlorbReader(filename);
        } catch (IOException e) {
            return null;
        }
    }
    
    public static int chunkId(String idString) {
        int idInt = 0;
        for (int i = 0; i < 4; i++) {
            char ch = idString.charAt(i);
            idInt += ((ch & 0xFF) << (3 - i) * 8);
        }
        return idInt;
    }
    
    public static final int ID_FORM = chunkId("FORM");
    
    public static final int ID_PICT = chunkId("Pict");
    public static final int ID_SND = chunkId("Snd ");
    
    public static final int ID_OGGV = chunkId("OGGV");
    public static final int ID_MOD = chunkId("MOD ");

    public static final int ID_PNG = chunkId("PNG ");
    public static final int ID_JPEG = chunkId("JPEG");
    
    
    public static long u32(int num) {
        return ((long)num) & 0xFFFFFFFFL;
    }
    
    private Map<Integer, Map<Long, Long>> posMap =
        new HashMap<Integer, Map<Long, Long>>();
    private final String filename;
    private RandomAccessFile file;
    
    private BlorbReader(String filename) throws IOException {
        this.filename = filename;
        
        try {
            file = new RandomAccessFile(filename, "r");
            checkBlorbFile();
            readBlorbMap();
        } catch (IOException e1) {
            if (file != null) {
                try {
                    file.close();
                } catch (IOException e2) {}
            }
            throw e1;
        }
        
    }
    
    private long getResourcePos(int id, long num) {
        Map<Long, Long> typeMap = posMap.get(id);
        if (typeMap == null) {
            return 0;
        }
        Long pos = typeMap.get(num);
        if (pos == null) {
            return 0;
        }
        return pos;
    }
    
    private InputStream getResourceStream(int id, long num,
            boolean copy) {
        long pos = getResourcePos(id, num);
        if (pos == 0) {
            return null;
        }

        int length = 0;
        long start = 0;
        int chunkid = 0;
        try {
            file.seek(pos);
            chunkid = file.readInt();
            length = file.readInt();
            if (chunkid == ID_FORM) {
                length += 8;
                start = pos;
            } else {
                start = pos + 8;
            }
            if (length < 0) {
                return null;
            }
        } catch (IOException e) {
            return null;
        }
        
        try {
            if (copy) {
                return new RAFileInputStream(filename, start, length,
                        chunkid);
            } else {
                return new RAFileInputStream(file, start, length,
                        chunkid);
            }
        } catch (IOException e) {
            return null;
        }
    }
    
    public InputStream getImageStream(long num, boolean copy) {
        return getResourceStream(ID_PICT, num, copy);
    }
    
    public InputStream getSoundStream(long num, boolean copy) {
        return getResourceStream(ID_SND, num, copy);
    }
    
    private void checkBlorbFile()
    throws IOException {
        if (file.readInt() != chunkId("FORM")) {
            throw new IOException();
        }
        file.readInt();
        if (file.readInt() != chunkId("IFRS")) {
            throw new IOException();
        }
    }
    
    private void readBlorbMap()
    throws IOException {
        if (file.readInt() != chunkId("RIdx")) {
            throw new IOException();
        }
        file.readInt();
        long numResources = u32(file.readInt());
        for (long r = 0; r < numResources; r++) {
            int id = file.readInt();
            long num = u32(file.readInt());
            long start = u32(file.readInt());
            addToPosMap(id, num, start);
        }
    }
    
    private void addToPosMap(int id, long num, long start) {
        Map<Long, Long> typeMap = posMap.get(id);
        if (typeMap == null) {
            typeMap = new HashMap<Long, Long>();
            posMap.put(id, typeMap);
        }
        if (!typeMap.containsKey(num)) {
            typeMap.put(num, start);
        }
    }
}
