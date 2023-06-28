package com.apkide.ui.browsers.file;

import static android.view.LayoutInflater.from;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.apkide.common.FileUtils;
import com.apkide.ui.R;
import com.apkide.ui.browsers.Browser;
import com.apkide.ui.services.FileSystem;
import com.apkide.ui.views.trees.TreeNode;
import com.apkide.ui.views.trees.TreeViewAdapter;
import com.apkide.ui.views.trees.TreeViewHolder;
import com.apkide.ui.views.trees.TreeViewHolderFactory;

import java.util.Collections;
import java.util.List;

@SuppressLint("InflateParams")
public class FileBrowser extends LinearLayout implements Browser {
    private RecyclerView fileListView;
    private SharedPreferences preferences;

    private TreeViewAdapter fileListAdapter;

    private ImageView moreMenuButton;

    public FileBrowser(Context context) {
        super(context);
        setLayoutParams(new LinearLayout.LayoutParams(-1, -1));

        View headerView = from(getContext()).inflate(R.layout.browser_header, null);
        View contentView = from(getContext()).inflate(R.layout.browser_file, null);
        removeAllViews();
        setOrientation(VERTICAL);
        addView(headerView, 0);
        addView(contentView, 1);

        headerView.setClickable(true);

        preferences = getContext().getSharedPreferences("file_browser", Context.MODE_PRIVATE);

        fileListAdapter = new TreeViewAdapter(new TreeViewHolderFactory() {
            @Override
            public TreeViewHolder getTreeViewHolder(View view, int layout) {
                return new FileItemViewHolder(view);
            }
        });

        fileListAdapter.setTreeNodeClickListener((treeNode, view) -> {

        });

        fileListAdapter.setTreeNodeLongClickListener(new TreeViewAdapter.OnTreeNodeLongClickListener() {
            @Override
            public boolean onTreeNodeLongClick(TreeNode treeNode, View view) {
                return false;
            }
        });

        fileListView = findViewById(R.id.fileBrowserFileList);
        fileListView.setLayoutManager(new LinearLayoutManager(getContext()));
        fileListView.setNestedScrollingEnabled(false);
        fileListView.setAdapter(fileListAdapter);

        TextView headerName = findViewById(R.id.browserHeaderLabel);
        headerName.setText("Files");
        ImageView headerIcon = findViewById(R.id.browserHeaderIcon);
        headerIcon.setImageResource(R.mipmap.folder);
        moreMenuButton = findViewById(R.id.moreMenuButton);
        moreMenuButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    private void load() {
        String rootPath = preferences.getString("root", "");
        if (TextUtils.isEmpty(rootPath)) {
            rootPath = FileSystem.getSDCardPath();
        }
        TreeNode root = new TreeNode("Root", -1);
        sync(root, rootPath);
        fileListAdapter.updateTreeNodes(root.getChildren());
        fileListAdapter.expandAll();
    }

    public void sync(TreeNode root, String rootPath) {
        List<String> list = FileSystem.getChildEntries(rootPath);
        Collections.sort(list, (o1, o2) -> {
            if (FileSystem.isDirectory(o1) && FileSystem.isFile(o2)) {
                return -1;
            }
            if (FileSystem.isFile(o1) && FileSystem.isDirectory(o2)) {
                return 1;
            }
            return FileUtils.getFileName(o1).compareTo(FileUtils.getFileName(o2));
        });
        for (String filePath : list) {
            if (FileSystem.isDirectory(filePath)) {
                TreeNode child = new TreeNode(filePath, R.layout.browser_file_item);
                sync(child, filePath);
                root.addChild(child);
            } else {
                root.addChild(new TreeNode(filePath, R.layout.browser_file_item));
            }
        }
    }


    @Override
    public void apply() {
        requestFocus();
        load();
    }

    private static class FileItemViewHolder extends TreeViewHolder {

        private final TextView fileTitle;
        private final ImageView fileIcon;

        public FileItemViewHolder(@NonNull View itemView) {
            super(itemView);
            fileTitle = itemView.findViewById(R.id.fileTitle);
            fileIcon = itemView.findViewById(R.id.fileIcon);
        }

        @Override
        public void bindTreeNode(TreeNode node) {
            super.bindTreeNode(node);
            String filePath = node.getValue().toString();
            String fileName = FileUtils.getFileName(filePath);
            this.fileTitle.setText(fileName);

            if (FileSystem.isNormalDirectory(filePath)){
                fileIcon.setImageResource(fileName.startsWith(".") ? R.mipmap.folder_hidden :
                        node.isExpanded() ? R.mipmap.folder_open : R.mipmap.folder
                );
            }else{
                if (FileSystem.isDirectory(filePath)){
                    if (FileSystem.isJar(filePath)){
                        fileIcon.setImageResource(FileTypes.getIcon(fileName));
                    }else{
                        fileIcon.setImageResource(fileName.startsWith(".") ? R.mipmap.folder_hidden :
                                node.isExpanded() ? R.mipmap.folder_open : R.mipmap.folder
                        );
                    }
                }else{
                    fileIcon.setImageResource(FileTypes.getIcon(fileName));
                }
            }
        }
    }
}
