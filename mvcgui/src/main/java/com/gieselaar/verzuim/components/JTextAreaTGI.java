package com.gieselaar.verzuim.components;

import java.awt.Component;
import java.awt.Font;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTextArea;

/**
 * 
 * Extended JTextArea.
 * It allows copy/paste functions and to use tab to escape from the textarea.
 * 
 * @author tonny
 *
 */
public class JTextAreaTGI extends JTextArea {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private JTextAreaTGI thisarea = this;
	public JTextAreaTGI(){
		super();
		super.setLineWrap(true);
		super.setWrapStyleWord(true);
		super.setFont(new Font("Tahoma", Font.PLAIN, 11));
		super.setFocusTraversalKeys(
				KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, null);
		super.setFocusTraversalKeys(
				KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, null);

		JPopupMenu popupMenu = new JPopupMenu();
		addPopup(this, popupMenu);
		
		JMenuItem mntmPaste = new JMenuItem("Paste");
		mntmPaste.setMnemonic(KeyEvent.VK_V);
		mntmPaste.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				thisarea.paste();
			}
		});
		popupMenu.add(mntmPaste);
		JMenuItem mntmCopy = new JMenuItem("Copy");
		mntmCopy.setMnemonic(KeyEvent.VK_C);
		mntmCopy.setIcon(null);
		mntmCopy.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				/*
				 * Als er geen tekst geselecteerd is, selecteer dan alles
				 */
				if (thisarea.getSelectedText() == null){
					thisarea.setSelectionStart(0);
					thisarea.setSelectionEnd(thisarea.getText().length());
				}
				thisarea.copy();
			}
		});
		popupMenu.add(mntmCopy);
	}
	private static void addPopup(Component component, final JPopupMenu popup) {
		component.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}
			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}
			private void showMenu(MouseEvent e) {
				popup.show(e.getComponent(), e.getX(), e.getY());
			}
		});
	}

}
