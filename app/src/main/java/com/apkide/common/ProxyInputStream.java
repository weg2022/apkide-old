package com.apkide.common;


import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public abstract class ProxyInputStream extends FilterInputStream {
	
	public ProxyInputStream(final InputStream proxy) {
		super(proxy);
	}
	
	@Override
	public int read() throws IOException {
		try {
			beforeRead(1);
			final int b = in.read();
			afterRead(b != -1 ? 1 : -1);
			return b;
		} catch (final IOException e) {
			handleIOException(e);
			return -1;
		}
	}
	
	@Override
	public int read(final byte[] bts) throws IOException {
		try {
			beforeRead(bts==null?0:bts.length);
			final int n = in.read(bts);
			afterRead(n);
			return n;
		} catch (final IOException e) {
			handleIOException(e);
			return -1;
		}
	}
	
	@Override
	public int read(final byte[] bts, final int off, final int len) throws IOException {
		try {
			beforeRead(len);
			final int n = in.read(bts, off, len);
			afterRead(n);
			return n;
		} catch (final IOException e) {
			handleIOException(e);
			return -1;
		}
	}
	
	@Override
	public long skip(final long ln) throws IOException {
		try {
			return in.skip(ln);
		} catch (final IOException e) {
			handleIOException(e);
			return 0;
		}
	}
	
	@Override
	public int available() throws IOException {
		try {
			return super.available();
		} catch (final IOException e) {
			handleIOException(e);
			return 0;
		}
	}
	
	@Override
	public void close() throws IOException {
		IOUtils.safeClose(in);
	}
	
	@Override
	public synchronized void mark(final int readlimit) {
		in.mark(readlimit);
	}
	
	@Override
	public synchronized void reset() throws IOException {
		try {
			in.reset();
		} catch (final IOException e) {
			handleIOException(e);
		}
	}
	
	@Override
	public boolean markSupported() {
		return in.markSupported();
	}
	
	@SuppressWarnings("unused")
	protected void beforeRead(final int n) throws IOException {
	}
	
	@SuppressWarnings("unused")
	protected void afterRead(final int n) throws IOException {
	}
	
	protected void handleIOException(final IOException e) throws IOException {
		throw e;
	}
	
}
