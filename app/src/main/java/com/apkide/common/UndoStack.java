package com.apkide.common;


import java.util.LinkedList;

 class UndoStack {
    private final Document buffer;
    private final LinkedList<Command> commands = new LinkedList<>();
    private boolean batchEdit = false;
    private int batchGroupId = 0;
    private int top = 0;
    private long lastEditTime = -1;

    public UndoStack(Document buffer) {
        this.buffer = buffer;
    }

    public void reset(){
        commands.clear();
        batchEdit=false;
        batchGroupId=0;
        top=0;
        lastEditTime=-1;
    }

    public void undo() {
        if (canUndo()) {
            Command lastUndone = commands.get(top - 1);
            int group = lastUndone.groupId;
            do {
                Command c = commands.get(top - 1);
                if (c.groupId != group)
                    break;

                c.undo();
                --top;
            }
            while (canUndo());
        }
    }


    public void redo() {
        if (canRedo()) {
            Command lastRedone = commands.get(top);
            int group = lastRedone.groupId;
            do {
                Command c = commands.get(top);
                if (c.groupId != group)
                    break;
                c.redo();
                ++top;
            }
            while (canRedo());
        }
    }


    public void captureInsert(int start, int length, long time) {
        boolean mergeSuccess = false;

        if (canUndo()) {
            Command c = commands.get(top - 1);

            if (c instanceof InsertCommand
                    && c.merge(start, length, time)) {
                mergeSuccess = true;
            } else
                c.record();
        }

        if (!mergeSuccess) {
            push(new InsertCommand(start, length, batchGroupId));
            if (!batchEdit)
                batchGroupId++;
        }
        lastEditTime = time;
    }


    public void captureDelete(int start, int length, long time) {
        boolean mergeSuccess = false;

        if (canUndo()) {
            Command c = commands.get(top - 1);

            if (c instanceof DeleteCommand
                    && c.merge(start, length, time)) {
                mergeSuccess = true;
            } else
                c.record();
        }

        if (!mergeSuccess) {
            push(new DeleteCommand(start, length, batchGroupId));

            if (!batchEdit)
                batchGroupId++;
        }
        lastEditTime = time;
    }

    private void push(Command c) {
        trimStack();
        top++;
        commands.add(c);
    }

    private void trimStack() {
        while (commands.size() > top) {
            commands.removeLast();
        }
    }

    public  boolean canUndo() {
        return top > 0;
    }

    public boolean canRedo() {
        return top < commands.size();
    }

    public boolean isBatchEdit() {
        return batchEdit;
    }

    public void beginBatchEdit() {
        if (batchEdit)return;
        batchEdit = true;
    }

    public void endBatchEdit() {
        if (!batchEdit)return;
        batchEdit = false;
        batchGroupId++;
    }


    private abstract static class Command {
        public final static long MERGE_TIME = 750000000; //750ms in nanoseconds

        public int start;
        public int length;
        public String content;
        public int groupId;

        public abstract void undo();

        public abstract void redo();

        public abstract void record();

        public abstract boolean merge(int start, int length, long time);
    }

    private class InsertCommand extends Command {

        public InsertCommand(int start, int length, int groupId) {
            this.start = start;
            this.length = length;
            this.groupId = groupId;
        }

        @Override
        public boolean merge(int newStart, int length, long time) {
            if (lastEditTime < 0) return false;

            if ((time - lastEditTime) < MERGE_TIME &&
                    newStart == start + this.length) {
                this.length += length;
                trimStack();
                return true;
            }

            return false;
        }

        @Override
        public void record() {
            content =buffer.getText(start, length);
        }

        @Override
        public void undo() {
            if (content == null) {
                record();
                buffer.shiftGapStart(-length);
            } else
                buffer.delete(start, length, 0, false);
        }

        @Override
        public void redo() {
            buffer.insert(start,content,  0, false);
        }

    }


    private class DeleteCommand extends Command {

        public DeleteCommand(int start, int length, int groupId) {
            this.start = start;
            this.length = length;
            this.groupId = groupId;
        }

        @Override
        public boolean merge(int newStart, int length, long time) {
            if (lastEditTime < 0) return false;

            if ((time - lastEditTime) < MERGE_TIME
                    && newStart == start - this.length - length + 1) {
                start = newStart;
                this.length += length;
                trimStack();
                return true;
            }

            return false;
        }

        @Override
        public void record() {
            content = buffer.getGapSub(length);
        }

        @Override
        public void undo() {
            if (content == null) {
                record();
                buffer.shiftGapStart(length);
            } else
                buffer.insert(start,content,  0, false);
        }

        @Override
        public void redo() {
            buffer.delete(start, length, 0, false);
        }

    }
}
