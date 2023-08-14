package com.apkide.common;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

public abstract class NormalizedReader {

	private static final Object sLock=new Object();

	private static NormalizedReader sReader=new DefaultNormalizedReader();

	public static void set(@NonNull NormalizedReader reader){
		synchronized (sLock){
			sReader =reader;
		}
	}

	@NonNull
	public static NormalizedReader get(){
		synchronized (sLock){
			return sReader;
		}
	}


	@NonNull
	public abstract Reader createReader(@NonNull Reader reader)throws IOException;

	@NonNull
	public abstract Reader createBomReader(@NonNull InputStream inputStream, @Nullable String encoding)throws IOException;


	public static class DefaultNormalizedReader extends NormalizedReader{

		@NonNull
		@Override
		public Reader createReader(@NonNull Reader reader) {
			return new DefaultReader(reader);
		}

		@NonNull
		@Override
		public Reader createBomReader(@NonNull InputStream inputStream, @Nullable String encoding) throws IOException {
			return new UnicodeReader(inputStream,encoding);
		}
	}

	public static class DefaultReader extends Reader {
	    private Reader reader;
	    private boolean inCRLF;

	    public DefaultReader(@NonNull Reader reader) {
	        this.reader = reader;
	        this.inCRLF = false;
	    }

	    @Override
	    public int read(char[] chars, int off, int len) throws IOException {
	        int readLen = reader.read(chars, off, len);
	        if (readLen == -1) {
	            return -1;
	        } else {
	            boolean skipCR = inCRLF;
	            int destOff = off;
	            int srcOff = off;
	            int maxOff = off + readLen;

	            while (srcOff < maxOff) {
	                char c = chars[srcOff++];
	                switch (c) {
	                    case '\n':
	                        if (!skipCR)
	                            chars[destOff++] = '\n';

	                        skipCR = false;
	                        break;
	                    case '\r':
	                        chars[destOff++] = '\n';
	                        skipCR = true;
	                        break;
	                    default:
	                        chars[destOff++] = c;
	                        skipCR = false;
	                }
	            }

	            inCRLF = skipCR;
	            return destOff - off;
	        }
	    }

	    @Override
	    public void close() throws IOException {
	       reader.close();
		   reader = null;
	    }
	}
}
