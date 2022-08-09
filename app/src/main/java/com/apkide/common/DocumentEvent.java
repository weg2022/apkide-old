package com.apkide.common;

import java.util.Objects;

public class DocumentEvent {
    public Document document;
    public int offset;
    public int length;
    public String text;

    public DocumentEvent(){}

    public DocumentEvent(Document document, int offset, int length, String text) {
        this.document = document;
        this.offset = offset;
        this.length = length;
        this.text = text;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DocumentEvent that = (DocumentEvent) o;
        return offset == that.offset && length == that.length && document.equals(that.document) && text.equals(that.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(document, offset, length, text);
    }

}
