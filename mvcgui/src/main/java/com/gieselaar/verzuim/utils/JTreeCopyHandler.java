package com.gieselaar.verzuim.utils;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.TransferHandler;
import javax.swing.tree.TreePath;

public class JTreeCopyHandler extends TransferHandler {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public void exportToClipboard(JComponent comp, Clipboard clip, int action) {
        JTree tree = (JTree)comp;
        TreePath path = tree.getSelectionPath();
        if(path != null) {
            clip.setContents(new StringSelection(path.getLastPathComponent().toString()), null);
        }
    }
    public void exportAllToClipboard(JComponent comp, Clipboard clip, int action) {
        JTree tree = (JTree)comp;
        TreePath[] pathes = tree.getSelectionPaths();
        StringBuilder completeText = new StringBuilder();
        for (TreePath p:pathes){
            if(p != null) {
            	completeText.append(p.getLastPathComponent().toString() + "\n");
            }
        }
        clip.setContents(new StringSelection(completeText.toString()), null);
    }
}
