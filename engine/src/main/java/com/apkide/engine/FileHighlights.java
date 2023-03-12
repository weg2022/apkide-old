package com.apkide.engine;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class FileHighlights implements Parcelable {
	public String filePath;
	public int size;
	public int[][] highlights;
	
	@Override
	public int describeContents() {
		return 0;
	}
	
	@Override
	public void writeToParcel(@NonNull Parcel dest, int flags) {
		dest.writeString(this.filePath);
		dest.writeInt(this.size);
		if (highlights != null) {
			for (int[] highlight : highlights) {
				dest.writeIntArray(highlight);
			}
		}
	}
	
	public void readFromParcel(@NonNull Parcel source) {
		this.filePath = source.readString();
		this.size = source.readInt();
		if (size <= 0) return;
		
		this.highlights = new int[size][3];
		for (int i = 0; i < size; i++) {
			source.readIntArray(highlights[i]);
		}
	}
	
	public FileHighlights() {
	}
	
	protected FileHighlights(@NonNull Parcel in) {
		this.filePath = in.readString();
		this.size = in.readInt();
		if (size <= 0) return;
		
		this.highlights = new int[size][3];
		for (int i = 0; i < size; i++) {
			in.readIntArray(highlights[i]);
		}
	}
	
	public static final Parcelable.Creator<FileHighlights> CREATOR = new Parcelable.Creator<FileHighlights>() {
		@NonNull
		@Override
		public FileHighlights createFromParcel(Parcel source) {
			return new FileHighlights(source);
		}
		
		@NonNull
		@Override
		public FileHighlights[] newArray(int size) {
			return new FileHighlights[size];
		}
	};
}
