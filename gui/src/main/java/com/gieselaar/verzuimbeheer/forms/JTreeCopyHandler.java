package com.gieselaar.verzuimbeheer.forms;

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
        TreePath path = tree.getSelectionPath();
        if(path != null) {
            clip.setContents(new StringSelection(path.toString()), null);
        }
    }
}
