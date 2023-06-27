package com.apkide.ui.services.navigate;

import android.text.TextUtils;

import com.apkide.ui.services.FileSystem;

import java.util.Stack;

public class NavigateService {

    private final Stack<FileSpan> forwardStacks = new Stack<>();
    private final Stack<FileSpan> backwardStacks = new Stack<>();
    private boolean enabled=true;

    public NavigateService() {

    }


    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void addFileSpan(FileSpan span) {
        if (!enabled || span == null || TextUtils.isEmpty(span.filePath))
            return;

        if (!backwardStacks.isEmpty() && backwardStacks.peek().isNeedUpdate(span)) {
            backwardStacks.pop();
        } else
            forwardStacks.clear();

        backwardStacks.add(span);
    }


    public void clear() {
        forwardStacks.clear();
        backwardStacks.clear();
    }

    public boolean canForward() {
        return !forwardStacks.isEmpty();
    }

    public boolean canBackward() {
        return backwardStacks.size() >= 2;
    }

    public FileSpan forward() {
        while (canForward()) {
            FileSpan span = forwardStacks.pop();
            if (FileSystem.exists(span.filePath)) {
                backwardStacks.add(span);
                return span;
            }
        }
        return null;
    }

    public FileSpan backward() {
        while (canBackward()) {
            FileSpan top = backwardStacks.pop();
            FileSpan span = backwardStacks.peek();
            if (FileSystem.exists(span.filePath)) {
                forwardStacks.push(top);
                return span;
            }
        }
        return null;
    }
}
