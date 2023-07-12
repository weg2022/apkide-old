package com.apkide.ui.views.editor;

import static java.util.Objects.requireNonNullElseGet;

import java.util.Objects;


public class LineManger {

    public static String[] DELIMITERS = {"\r", "\n", "\r\n"};
    private static final String NO_DELIMITER = "";
    private final DelimiterInfo fDelimiterInfo = new DelimiterInfo();
    private Node fRoot = new Node(0, NO_DELIMITER);

    public LineManger() {
    }

    public Node nodeByOffset(final int offset) {
        int remaining = offset;
        Node node = fRoot;
        while (true) {
            if (node == null)
                fail(offset);

            if (remaining < node.offset) {
                node = node.left;
            } else {
                remaining -= node.offset;
                if (remaining < node.length
                        || remaining == node.length && node.right == null) {
                    break;
                }
                remaining -= node.length;
                node = node.right;
            }
        }

        return node;
    }


    private int lineByOffset(final int offset) {
        int remaining = offset;
        Node node = fRoot;
        int line = 0;

        while (true) {
            if (node == null)
                fail(offset);

            if (remaining < node.offset) {
                node = node.left;
            } else {
                remaining -= node.offset;
                line += node.line;
                if (remaining < node.length || remaining == node.length && node.right == null)
                    return line;

                remaining -= node.length;
                line++;
                node = node.right;
            }
        }
    }


    public Node nodeByLine(final int line) {
        int remaining = line;
        Node node = fRoot;

        while (true) {
            if (node == null)
                fail(line);

            if (remaining == node.line)
                break;
            if (remaining < node.line) {
                node = node.left;
            } else {
                remaining -= node.line + 1;
                node = node.right;
            }
        }

        return node;
    }


    private int offsetByLine(final int line) {
        int remaining = line;
        int offset = 0;
        Node node = fRoot;

        while (true) {
            if (node == null)
                fail(line);

            if (remaining == node.line)
                return offset + node.offset;

            if (remaining < node.line) {
                node = node.left;
            } else {
                remaining -= node.line + 1;
                offset += node.offset + node.length;
                node = node.right;
            }
        }
    }

    private void rotateLeft(Node node) {
        Node child = node.right;
        boolean leftChild = node.parent == null || node == node.parent.left;

        setChild(node.parent, child, leftChild);
        setChild(node, child.left, false);
        setChild(child, node, true);

        child.line += node.line + 1;
        child.offset += node.offset + node.length;
    }


    private void rotateRight(Node node) {
        Node child = node.left;

        boolean leftChild = node.parent == null || node == node.parent.left;

        setChild(node.parent, child, leftChild);
        setChild(node, child.right, true);
        setChild(child, node, false);

        node.line -= child.line + 1;
        node.offset -= child.offset + child.length;
    }


    private void setChild(Node parent, Node child, boolean isLeftChild) {
        if (parent == null) {
            fRoot = requireNonNullElseGet(child, () -> new Node(0, NO_DELIMITER));
        } else {
            if (isLeftChild)
                parent.left = child;
            else
                parent.right = child;
        }
        if (child != null)
            child.parent = parent;
    }

    private void singleLeftRotation(Node node, Node parent) {
        rotateLeft(parent);
        node.balance = 0;
        parent.balance = 0;
    }

    private void singleRightRotation(Node node, Node parent) {
        rotateRight(parent);
        node.balance = 0;
        parent.balance = 0;
    }

    private void rightLeftRotation(Node node, Node parent) {
        Node child = node.left;
        rotateRight(node);
        rotateLeft(parent);
        if (child.balance == 1) {
            node.balance = 0;
            parent.balance = -1;
            child.balance = 0;
        } else if (child.balance == 0) {
            node.balance = 0;
            parent.balance = 0;
        } else if (child.balance == -1) {
            node.balance = 1;
            parent.balance = 0;
            child.balance = 0;
        }
    }

    private void leftRightRotation(Node node, Node parent) {
        Node child = node.right;
        rotateLeft(node);
        rotateRight(parent);
        if (child.balance == -1) {
            node.balance = 0;
            parent.balance = 1;
            child.balance = 0;
        } else if (child.balance == 0) {
            node.balance = 0;
            parent.balance = 0;
        } else if (child.balance == 1) {
            node.balance = -1;
            parent.balance = 0;
            child.balance = 0;
        }
    }

    private Node insertAfter(Node node, int length, String delimiter) {
        Node added = new Node(length, delimiter);

        if (node.right == null)
            setChild(node, added, false);
        else
            setChild(successorDown(node.right), added, true);

        updateParentChain(added, length, 1);
        updateParentBalanceAfterInsertion(added);

        return added;
    }

    private void updateParentBalanceAfterInsertion(Node node) {
        Node parent = node.parent;
        while (parent != null) {
            if (node == parent.left)
                parent.balance--;
            else
                parent.balance++;

            switch (parent.balance) {
                case 1:
                case -1:
                    node = parent;
                    parent = node.parent;
                    continue;
                case -2:
                    rebalanceAfterInsertionLeft(node);
                    break;
                case 2:
                    rebalanceAfterInsertionRight(node);
                    break;
                case 0:
                    break;
                default:
            }
            return;
        }
    }


    private void rebalanceAfterInsertionRight(Node node) {
        Node parent = node.parent;
        if (node.balance == 1) {
            singleLeftRotation(node, parent);
        } else if (node.balance == -1) {
            rightLeftRotation(node, parent);
        }
    }


    private void rebalanceAfterInsertionLeft(Node node) {
        Node parent = node.parent;
        if (node.balance == -1) {
            singleRightRotation(node, parent);
        } else if (node.balance == 1) {
            leftRightRotation(node, parent);
        }
    }

    public void replace(int offset, int length, CharSequence text) {
        int remaining = offset;
        Node first = fRoot;
        int firstNodeOffset;

        while (true) {
            if (first == null)
                fail(offset);

            if (remaining < first.offset) {
                first = first.left;
            } else {
                remaining -= first.offset;
                if (remaining < first.length
                        || remaining == first.length && first.right == null) {
                    firstNodeOffset = offset - remaining;
                    break;
                }
                remaining -= first.length;
                first = first.right;
            }
        }

        Node last;
        if (offset + length < firstNodeOffset + first.length)
            last = first;
        else
            last = nodeByOffset(offset + length);

        int firstLineDelta = firstNodeOffset + first.length - offset;
        if (first == last)
            replaceInternal(first, text, length, firstLineDelta);
        else
            replaceFromTo(first, last, text, length, firstLineDelta);

    }


    private void replaceInternal(Node node, CharSequence text, int length, int firstLineDelta) {
        DelimiterInfo info = text == null ? null : nextDelimiterInfo(text, 0);

        if (info == null || info.delimiter == null) {
            int added = text == null ? 0 : text.length();
            updateLength(node, added - length);
        } else {
            int remainder = firstLineDelta - length;
            String remDelim = node.delimiter;

            int consumed = info.delimiterIndex + info.delimiterLength;
            int delta = consumed - firstLineDelta;
            updateLength(node, delta);
            node.delimiter = info.delimiter;

            info = nextDelimiterInfo(text, consumed);
            while (info != null) {
                int lineLen = info.delimiterIndex - consumed + info.delimiterLength;
                node = insertAfter(node, lineLen, info.delimiter);
                consumed += lineLen;
                info = nextDelimiterInfo(text, consumed);
            }

            insertAfter(node, remainder + text.length() - consumed, remDelim);
        }
    }


    private void replaceFromTo(Node node, Node last, CharSequence text, int length, int firstLineDelta) {

        Node successor = successor(node);
        while (successor != last) {
            length -= successor.length;
            Node toDelete = successor;
            successor = successor(successor);
            updateLength(toDelete, -toDelete.length);
        }

        DelimiterInfo info = text == null ? null : nextDelimiterInfo(text, 0);

        if (info == null || info.delimiter == null) {
            int added = text == null ? 0 : text.length();

            join(node, last, added - length);
        } else {

            int consumed = info.delimiterIndex + info.delimiterLength;
            updateLength(node, consumed - firstLineDelta);
            node.delimiter = info.delimiter;
            length -= firstLineDelta;

            info = nextDelimiterInfo(text, consumed);
            while (info != null) {
                int lineLen = info.delimiterIndex - consumed + info.delimiterLength;
                node = insertAfter(node, lineLen, info.delimiter);
                consumed += lineLen;
                info = nextDelimiterInfo(text, consumed);
            }

            updateLength(last, text.length() - consumed - length);
        }
    }


    private void join(Node one, Node two, int delta) {
        int oneLength = one.length;
        updateLength(one, -oneLength);
        updateLength(two, oneLength + delta);
    }


    private void updateLength(Node node, int delta) {
        node.length += delta;

        int lineDelta;
        boolean delete = node.length == 0 && !Objects.equals(node.delimiter, NO_DELIMITER);
        if (delete)
            lineDelta = -1;
        else
            lineDelta = 0;

        if (delta != 0 || lineDelta != 0)
            updateParentChain(node, delta, lineDelta);

        if (delete)
            delete(node);
    }

    private void updateParentChain(Node node, int deltaLength, int deltaLines) {
        updateParentChain(node, null, deltaLength, deltaLines);
    }


    private void updateParentChain(Node from, Node to, int deltaLength, int deltaLines) {
        Node parent = from.parent;
        while (parent != to) {
            if (from == parent.left) {
                parent.offset += deltaLength;
                parent.line += deltaLines;
            }
            from = parent;
            parent = from.parent;
        }
    }


    private void delete(Node node) {

        Node parent = node.parent;
        Node toUpdate;
        boolean lostLeftChild;
        boolean isLeftChild = parent == null || node == parent.left;

        if (node.left == null || node.right == null) {
            Node replacement = node.left == null ? node.right : node.left;
            setChild(parent, replacement, isLeftChild);
            toUpdate = parent;
            lostLeftChild = isLeftChild;
        } else if (node.right.left == null) {

            Node replacement = node.right;
            setChild(parent, replacement, isLeftChild);
            setChild(replacement, node.left, true);
            replacement.line = node.line;
            replacement.offset = node.offset;
            replacement.balance = node.balance;
            toUpdate = replacement;
            lostLeftChild = false;
        } else {
            Node successor = successor(node);

            toUpdate = successor.parent;
            lostLeftChild = true;

            updateParentChain(successor, node, -successor.length, -1);

            setChild(toUpdate, successor.right, true);
            setChild(successor, node.right, false);
            setChild(successor, node.left, true);
            setChild(parent, successor, isLeftChild);

            successor.line = node.line;
            successor.offset = node.offset;
            successor.balance = node.balance;
        }

        updateParentBalanceAfterDeletion(toUpdate, lostLeftChild);
    }


    private void updateParentBalanceAfterDeletion(Node node, boolean wasLeftChild) {
        while (node != null) {
            if (wasLeftChild)
                node.balance++;
            else
                node.balance--;

            Node parent = node.parent;
            if (parent != null)
                wasLeftChild = node == parent.left;

            switch (node.balance) {
                case 1:
                case -1:
                    return;
                case -2:
                    if (rebalanceAfterDeletionRight(node.left))
                        return;
                    break;
                case 2:
                    if (rebalanceAfterDeletionLeft(node.right))
                        return;
                    break;
                case 0:
                    break;
                default:

            }

            node = parent;
        }
    }

    private boolean rebalanceAfterDeletionLeft(Node node) {
        Node parent = node.parent;
        if (node.balance == 1) {
            singleLeftRotation(node, parent);
            return false;
        } else if (node.balance == -1) {
            rightLeftRotation(node, parent);
            return false;
        } else if (node.balance == 0) {
            rotateLeft(parent);
            node.balance = -1;
            parent.balance = 1;
            return true;
        } else {

            return true;
        }
    }

    private boolean rebalanceAfterDeletionRight(Node node) {
        Node parent = node.parent;
        if (node.balance == -1) {
            singleRightRotation(node, parent);
            return false;
        } else if (node.balance == 1) {
            leftRightRotation(node, parent);
            return false;
        } else if (node.balance == 0) {
            rotateRight(parent);
            node.balance = 1;
            parent.balance = -1;
            return true;
        } else {

            return true;
        }
    }


    private Node successor(Node node) {
        if (node.right != null)
            return successorDown(node.right);

        return successorUp(node);
    }

    private Node successorUp(final Node node) {
        Node child = node;
        Node parent = child.parent;
        while (parent != null) {
            if (child == parent.left)
                return parent;
            child = parent;
            parent = child.parent;
        }

        return null;
    }


    private Node successorDown(Node node) {
        Node child = node.left;
        while (child != null) {
            node = child;
            child = node.left;
        }
        return node;
    }


    private void fail(int offset) {
        throw new IllegalArgumentException(String.valueOf(offset));
    }

    public String[] getLegalLineDelimiters() {
        return DELIMITERS;
    }

    protected DelimiterInfo nextDelimiterInfo(CharSequence text, int offset) {
        char ch;
        int length = text.length();
        for (int i = offset; i < length; i++) {

            ch = text.charAt(i);
            if (ch == '\r') {

                if (i + 1 < length) {
                    if (text.charAt(i + 1) == '\n') {
                        fDelimiterInfo.delimiter = DELIMITERS[2];
                        fDelimiterInfo.delimiterIndex = i;
                        fDelimiterInfo.delimiterLength = 2;
                        return fDelimiterInfo;
                    }
                }

                fDelimiterInfo.delimiter = DELIMITERS[0];
                fDelimiterInfo.delimiterIndex = i;
                fDelimiterInfo.delimiterLength = 1;
                return fDelimiterInfo;

            } else if (ch == '\n') {

                fDelimiterInfo.delimiter = DELIMITERS[1];
                fDelimiterInfo.delimiterIndex = i;
                fDelimiterInfo.delimiterLength = 1;
                return fDelimiterInfo;
            }
        }

        return null;
    }

    public String getLineDelimiter(int line) {
        Node node = nodeByLine(line);
        return Objects.equals(node.delimiter, NO_DELIMITER) ? null : node.delimiter;
    }

    public int lineCount(CharSequence text) {
        int count = 0;
        int start = 0;
        DelimiterInfo delimiterInfo = nextDelimiterInfo(text, start);
        while (delimiterInfo != null && delimiterInfo.delimiterIndex > -1) {
            ++count;
            start = delimiterInfo.delimiterIndex + delimiterInfo.delimiterLength;
            delimiterInfo = nextDelimiterInfo(text, start);
        }
        return count;
    }

    public int getLineCount() {
        Node node = fRoot;
        int lines = 0;
        while (node != null) {
            lines += node.line + 1;
            node = node.right;
        }
        return lines;
    }

    public int getLineCount(int offset, int length) {
        if (length == 0)
            return 1;

        int startLine = lineByOffset(offset);
        int endLine = lineByOffset(offset + length);

        return endLine - startLine + 1;
    }

    public int getLineStart(int line) {
        return offsetByLine(line);
    }

    public int getFullLineLength(int line) {
        Node node = nodeByLine(line);
        return node.length;
    }

    public int getLineLength(int line) {
        Node node = nodeByLine(line);
        return node.pureLength();
    }

    public int findLineNumber(int offset) {
        return lineByOffset(offset);
    }

    public LineInformation getLineInformationAtOffset(final int offset) {
        int remaining = offset;
        Node node = fRoot;
        int lineOffset;

        while (true) {
            if (node == null)
                fail(offset);

            if (remaining < node.offset) {
                node = node.left;
            } else {
                remaining -= node.offset;
                if (remaining < node.length
                        || remaining == node.length && node.right == null) {
                    lineOffset = offset - remaining;
                    break;
                }
                remaining -= node.length;
                node = node.right;
            }
        }
        return new LineInformation(lineOffset, node.pureLength(), node.delimiter);
    }

    public LineInformation getLineInformation(int line) {
        try {
            int remaining = line;
            int offset = 0;
            Node node = fRoot;

            while (true) {
                if (node == null)
                    fail(line);

                if (remaining == node.line) {
                    offset += node.offset;
                    break;
                }
                if (remaining < node.line) {
                    node = node.left;
                } else {
                    remaining -= node.line + 1;
                    offset += node.offset + node.length;
                    node = node.right;
                }
            }
            return new LineInformation(offset, node.pureLength(), node.delimiter);
        } catch (IllegalArgumentException x) {
            if (line > 0 && line == getLineCount()) {
                line = line - 1;

                int remaining = line;
                int offset = 0;
                Node node = fRoot;

                while (true) {
                    if (node == null)
                        fail(line);

                    if (remaining == node.line) {
                        offset += node.offset;
                        break;
                    }
                    if (remaining < node.line) {
                        node = node.left;
                    } else {
                        remaining -= node.line + 1;
                        offset += node.offset + node.length;
                        node = node.right;
                    }
                }
                Node last = node;
                if (last.length > 0)
                    return new LineInformation(offset + last.length, 0, last.delimiter);
            }
            throw x;
        }
    }

    public void set(CharSequence text) {
        fRoot = new Node(0, NO_DELIMITER);
        replace(0, 0, text);
    }

    private static class DelimiterInfo {
        public int delimiterIndex;
        public int delimiterLength;
        public String delimiter;
    }

    private static class Node {

        int line;

        int offset;

        int length;

        String delimiter;

        Node parent;

        Node left;

        Node right;

        byte balance;

        Node(int length, String delimiter) {
            this.length = length;
            this.delimiter = delimiter;
        }

        int pureLength() {
            return length - delimiter.length();
        }
    }

}


