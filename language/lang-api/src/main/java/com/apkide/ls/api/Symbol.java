package com.apkide.ls.api;

import androidx.annotation.NonNull;

import com.apkide.ls.api.util.Location;

import java.io.Serializable;

public class Symbol implements Serializable {

	private static final long serialVersionUID = -1741804405311470010L;
	@NonNull
	public String name;
	@NonNull
	public SymbolKind kind;
	public boolean deprecated;
	@NonNull
	public Location location;

	public Symbol(@NonNull String name, @NonNull SymbolKind kind, @NonNull Location location) {
		this(name,kind,false,location);
	}

	public Symbol(@NonNull String name, @NonNull SymbolKind kind, boolean deprecated, @NonNull Location location) {
		this.name = name;
		this.kind = kind;
		this.deprecated = deprecated;
		this.location = location;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Symbol symbol = (Symbol) o;

		if (deprecated != symbol.deprecated) return false;
		if (!name.equals(symbol.name)) return false;
		if (kind != symbol.kind) return false;
		return location.equals(symbol.location);
	}

	@Override
	public int hashCode() {
		int result = name.hashCode();
		result = 31 * result + kind.hashCode();
		result = 31 * result + (deprecated ? 1 : 0);
		result = 31 * result + location.hashCode();
		return result;
	}
}
