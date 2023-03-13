package com.apkide.ui.services.highlighting;

import android.os.Parcel;
import android.os.Parcelable;

public class FileHighlighting implements Parcelable {
	public static class Highlight implements Parcelable {
		public int token;
		public int offset;
		public int length;
		
		public Highlight(int token, int offset, int length) {
			this.token = token;
			this.offset = offset;
			this.length = length;
		}
		
		@Override
		public int describeContents() {
			return 0;
		}
		
		@Override
		public void writeToParcel(Parcel dest, int flags) {
			dest.writeInt(this.token);
			dest.writeInt(this.offset);
			dest.writeInt(this.length);
		}
		
		public void readFromParcel(Parcel source) {
			this.token = source.readInt();
			this.offset = source.readInt();
			this.length = source.readInt();
		}
		
		public Highlight() {
		}
		
		protected Highlight(Parcel in) {
			this.token = in.readInt();
			this.offset = in.readInt();
			this.length = in.readInt();
		}
		
		public static final Parcelable.Creator<Highlight> CREATOR = new Parcelable.Creator<Highlight>() {
			@Override
			public Highlight createFromParcel(Parcel source) {
				return new Highlight(source);
			}
			
			@Override
			public Highlight[] newArray(int size) {
				return new Highlight[size];
			}
		};
	}
	public String filePath;
	public int size;
	public Highlight[] highlights;
	
	public FileHighlighting() {
	}
	
	public FileHighlighting(String filePath, int size, Highlight[] highlights) {
		this.filePath = filePath;
		this.size = size;
		this.highlights = highlights;
	}
	
	@Override
	public int describeContents() {
		return 0;
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.filePath);
		dest.writeInt(this.size);
		dest.writeTypedArray(this.highlights, flags);
	}
	
	public void readFromParcel(Parcel source) {
		this.filePath = source.readString();
		this.size = source.readInt();
		this.highlights = source.createTypedArray(Highlight.CREATOR);
	}
	
	protected FileHighlighting(Parcel in) {
		this.filePath = in.readString();
		this.size = in.readInt();
		this.highlights = in.createTypedArray(Highlight.CREATOR);
	}
	
	public static final Parcelable.Creator<FileHighlighting> CREATOR = new Parcelable.Creator<FileHighlighting>() {
		@Override
		public FileHighlighting createFromParcel(Parcel source) {
			return new FileHighlighting(source);
		}
		
		@Override
		public FileHighlighting[] newArray(int size) {
			return new FileHighlighting[size];
		}
	};
}
