package com.apkide.ls.api;

import androidx.annotation.NonNull;

import com.apkide.ls.api.util.Range;

import java.io.Serializable;

public class Diagnostic implements Serializable {

	private static final long serialVersionUID = 5759109355070140871L;
	@NonNull
	public Range range;
	@NonNull
	public DiagnosticSeverity severity;
	@NonNull
	public String code,source,message;

	public Diagnostic(@NonNull Range range, @NonNull DiagnosticSeverity severity, @NonNull String code, @NonNull String source, @NonNull String message) {
		this.range = range;
		this.severity = severity;
		this.code = code;
		this.source = source;
		this.message = message;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Diagnostic that = (Diagnostic) o;

		if (!range.equals(that.range)) return false;
		if (severity != that.severity) return false;
		if (!code.equals(that.code)) return false;
		if (!source.equals(that.source)) return false;
		return message.equals(that.message);
	}

	@Override
	public int hashCode() {
		int result = range.hashCode();
		result = 31 * result + severity.hashCode();
		result = 31 * result + code.hashCode();
		result = 31 * result + source.hashCode();
		result = 31 * result + message.hashCode();
		return result;
	}
}
