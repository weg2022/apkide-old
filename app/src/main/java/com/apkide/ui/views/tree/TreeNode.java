package com.apkide.ui.views.tree;

import java.util.List;

public interface TreeNode<V> {
	
	int getType();
	
	void setLevel(int level);
	
	int getLevel();
	
	void setValue(V value);
	
	V getValue();
	
	void setSelected(boolean selected);
	
	boolean isSelected();
	
	void setExpanded(boolean expanded);
	
	boolean isExpanded();
	
	void setParent(TreeNode<?> parent);
	
	TreeNode<?> getParent();
	
	boolean hasParent();
	
	List<?> getChildren();
	
	TreeNode<?> getChild(int index);
	
	void addChild(TreeNode<?> child);
	
	void removeChild(TreeNode<?> child);
	
	void removeChild(int index);
	
	void removeAllChildren();
	
	int getChildCount();
	
	boolean hasChildren();
}
