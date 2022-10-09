package com.apkide.component.editor.theme;

import android.annotation.NonNull;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.ArrayMap;

import java.util.Map;

public class ColorScheme implements Parcelable {
	
	private Map<Integer, TextAttribute> _attributeMap = new ArrayMap<>();
	private String _name;
	private boolean _dark;
	
	
	protected ColorScheme(String name, boolean dark) {
		_name = name;
		_dark = dark;
		_attributeMap.clear();
		registerDefaults(_attributeMap);
	}
	
	public void put(int id, TextAttribute attribute) {
		_attributeMap.put(id, attribute);
	}
	
	public TextAttribute get(int id) {
		return _attributeMap.get(id);
	}
	
	public boolean contains(int id) {
		return _attributeMap.containsKey(id);
	}
	
	
	protected void registerDefaults(final @NonNull Map<Integer, TextAttribute> attributes) {
		throw new RuntimeException();
	}
	
	public void resetDefaults() {
		_attributeMap.clear();
		registerDefaults(_attributeMap);
	}
	
	
	@Override
	public int describeContents() {
		return 0;
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(this._attributeMap.size());
		for (Map.Entry<Integer, TextAttribute> entry : this._attributeMap.entrySet()) {
			dest.writeValue(entry.getKey());
			dest.writeParcelable(entry.getValue(), flags);
		}
		dest.writeString(this._name);
		dest.writeByte(this._dark ? (byte) 1 : (byte) 0);
	}
	
	public void readFromParcel(Parcel source) {
		int _attributeMapSize = source.readInt();
		this._attributeMap = new ArrayMap<>(_attributeMapSize);
		for (int i = 0; i < _attributeMapSize; i++) {
			Integer key = (Integer) source.readValue(Integer.class.getClassLoader());
			TextAttribute value = source.readParcelable(TextAttribute.class.getClassLoader());
			this._attributeMap.put(key, value);
		}
		this._name = source.readString();
		this._dark = source.readByte() != 0;
	}
	
	protected ColorScheme(Parcel in) {
		int _attributeMapSize = in.readInt();
		this._attributeMap = new ArrayMap<>(_attributeMapSize);
		for (int i = 0; i < _attributeMapSize; i++) {
			Integer key = (Integer) in.readValue(Integer.class.getClassLoader());
			TextAttribute value = in.readParcelable(TextAttribute.class.getClassLoader());
			this._attributeMap.put(key, value);
		}
		this._name = in.readString();
		this._dark = in.readByte() != 0;
	}
	
	public static final Parcelable.Creator<ColorScheme> CREATOR = new Parcelable.Creator<>() {
		@Override
		public ColorScheme createFromParcel(Parcel source) {
			return new ColorScheme(source);
		}
		
		@Override
		public ColorScheme[] newArray(int size) {
			return new ColorScheme[size];
		}
	};
}
