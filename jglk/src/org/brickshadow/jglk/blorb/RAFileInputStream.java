package org.brickshadow.jglk.blorb;


import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;


/**
 * An {@code InputStream} backed by a segment of a
 * {@code RandomAccessFile}.
 */
public class RAFileInputStream extends InputStream {

    private final RandomAccessFile file;
    private final long eof;
    private long mark;
    private final int length;
    private final int chunkid;
    private boolean copy;

    /**
     * Creates a new {@code RAFileInputStream} from an already-opened
     * {@code RandomAccessFile}. It is not safe to mix operations on
     * this object with operations on the underlying file.
     * 
     * @param file
     *           The underlying file.
     * @param start
     *           The file position that the {@code RAFileInputStream} will
     *           start at.
     * @param length
     *           The maximum length that the {@code RAFileInputStream}
     *           will be able to read.
     */
    public RAFileInputStream(RandomAccessFile file, long start,
            int length, int chunkid) throws IOException {
        this.file = file;
        this.length = length;
        this.chunkid = chunkid;
        eof = start + length;
        mark = start;
        file.seek(start);
        copy = false;
    }
    
    /**
     * Opens a {@code RandomAccessFile} and creates a
     * {@code RAFileInputStream} from it.
     * 
     * @param filename
     *           The name of the file to open.
     * @param start
     *           The file position that the {@code RAFileInputStream} will
     *           start at.
     * @param length
     *           The maximum length that the {@code RAFileInputStream}
     *           will be able to read.
     * @throws IOException
     *           if the file cannot be opened.
     */
    public RAFileInputStream(String filename, long start,
            int length, int chunkid) throws IOException {
        this(new RandomAccessFile(filename, "r"), start, length, chunkid);
        copy = true;
    }
    
    /**
     * Returns the maximum length that this {@code RAFileInputStream}
     * will be able to read.
     * 
     * @return
     *           the maximum length
     */
    public int getLength() {
        return length;
    }
    
    /**
     * Returns the chunk id of the chunk represented by this
     * {@code RAFileInputStream}.
     * 
     * @return
     *           the chunk id
     */
    public int getChunkId() {
        return chunkid;
    }

    public int read() throws IOException {
        return (file.getFilePointer() >= eof) ? -1 : file.read();
    }

    public int read(byte[] b, int off, int len) throws IOException {
        long pos = file.getFilePointer();
        if (pos >= eof) {
            return -1;
        }
        int readLength = (pos + len > eof) ? (int)(eof - pos) : len;
        return file.read(b, off, readLength);
    }

    public int available() throws IOException {
        return (int)(eof - file.getFilePointer());
    }

    public boolean markSupported() {
        return true;
    }

    public void mark(int readlimit) {
        try {
            mark = file.getFilePointer();
        } catch (IOException e) {}
    }

    public void reset() throws IOException {
        file.seek(mark);
    }

    public void close() {
        if (copy) {
            try {
                file.close();
            } catch (IOException e) {}
        }
    }
}
