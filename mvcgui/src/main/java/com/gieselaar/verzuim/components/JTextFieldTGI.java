package com.gieselaar.verzuim.components;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;

public class JTextFieldTGI extends JTextField implements FocusListener{

	private static final long serialVersionUID = -6117369620755680773L;

	private JTextField thisfield;
	private JMenuItem mntmPaste;
	private JMenuItem mntmCopy;
	public JTextFieldTGI(){
		super();
		thisfield = this;
		addFocusListener(this);

		JPopupMenu popupMenu = new JPopupMenu();
		addPopup(this, popupMenu);
		
		mntmPaste = new JMenuItem("Paste");
		mntmPaste.setIcon(null);
		mntmPaste.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				thisfield.paste();
			}
		});
		popupMenu.add(mntmPaste);
		mntmCopy = new JMenuItem("Copy");
		mntmCopy.setIcon(null);
		mntmCopy.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				thisfield.copy();
			}
		});
		popupMenu.add(mntmCopy);
	}
	@Override
	public void focusGained(FocusEvent e) {
		JTextField fld = (JTextField) e.getComponent();
		fld.selectAll();
	}

	@Override
	public void focusLost(FocusEvent e) {
	}
	
	private void addPopup(final Component component, final JPopupMenu popup) {
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
