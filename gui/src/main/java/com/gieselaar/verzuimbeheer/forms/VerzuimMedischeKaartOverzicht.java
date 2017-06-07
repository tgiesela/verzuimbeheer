package com.gieselaar.verzuimbeheer.forms;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.UIManager;

import com.gieselaar.verzuimbeheer.baseforms.BaseDetailform;
import com.gieselaar.verzuimbeheer.baseforms.JDesktopPaneTGI;
import com.gieselaar.verzuimbeheer.components.JTextAreaTGI;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.exceptions.PermissionException;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.forms.VerzuimMedischeKaartOverzicht.KaartInfo;
import com.gieselaar.verzuimbeheer.services.GebruikerInfo;
import com.gieselaar.verzuimbeheer.services.InfoBase;
import com.gieselaar.verzuimbeheer.services.VerzuimInfo;
import com.gieselaar.verzuimbeheer.services.VerzuimMedischekaartInfo;
import com.gieselaar.verzuimbeheer.utils.DateOnly;
import com.gieselaar.verzuimbeheer.utils.ExceptionLogger;
import com.gieselaar.verzuimbeheer.utils.ServiceCaller;

import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.plaf.ColorUIResource;
import javax.swing.text.BadLocationException;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import javax.swing.JScrollPane;
import javax.swing.JButton;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

import javax.swing.ScrollPaneConstants;

public class VerzuimMedischeKaartOverzicht extends BaseDetailform {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTree treeMK = null;
	private DefaultTreeModel treemodel = null;
	private VerzuimInfo verzuim = null;
	private KaartInfo selectedkaart = null;
	private JButton btnWijzigen = null;
	private JButton btnToevoegen = null;
	private JButton btnVerwijder = null;
	private Hashtable<Integer, String> hmGebruikers = null; 
	public JTextAreaTGI hiddenTextArea = null;
	
	public VerzuimMedischeKaartOverzicht (JDesktopPaneTGI mdiPanel) {
		super("Medische kaart overzicht", mdiPanel);
		initialize();
	}
	private void initialize(){
		setBounds(100, 100, 640, 556);
		getContentPane().setLayout(null);
		treemodel = new DefaultTreeModel(null);
		ModifiedTreeCellRenderer treecellrenderer = new ModifiedTreeCellRenderer();
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setBounds(23, 26, 461, 449);
		getContentPane().add(scrollPane);
		
		treeMK = new JTree();
		treeMK.setRootVisible(false);
		scrollPane.setViewportView(treeMK);
		treeMK.setModel(treemodel);
		treecellrenderer.setFont(treeMK.getFont());
		treeMK.setCellRenderer(treecellrenderer);
		treeMK.setRowHeight(0);
		treeMK.setTransferHandler(new JTreeCopyHandler());

		JPopupMenu popupMenu = new JPopupMenu();
		addPopup(treeMK, popupMenu);
		
		JMenuItem mntmCopy = new JMenuItem("Copy");
		mntmCopy.setIcon(null);
		mntmCopy.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
				treeMK.getTransferHandler().exportToClipboard(treeMK, clipboard, TransferHandler.COPY);
			}
		});
		popupMenu.add(mntmCopy);
		JMenuItem mntmPaste = new JMenuItem("Paste");
		mntmPaste.setIcon(null);
		mntmPaste.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				newFromClipboard();
			}
		});
		popupMenu.add(mntmPaste);
		
		btnToevoegen = new JButton("Toevoegen...");
		btnToevoegen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnToevoegenClicked(e);
			}
		});
		btnToevoegen.setBounds(507, 24, 106, 23);
		getContentPane().add(btnToevoegen);
		
		btnWijzigen = new JButton("Wijzigen...");
		btnWijzigen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnWijzigenClicked(e);
			}
		});
		btnWijzigen.setBounds(507, 50, 106, 23);
		getContentPane().add(btnWijzigen);
		
		hiddenTextArea = new JTextAreaTGI();
		hiddenTextArea.setEditable(false);
		hiddenTextArea.setEnabled(false);
		hiddenTextArea.setBounds(507, 102, 100, 70);
		hiddenTextArea.setVisible(false);
		hiddenTextArea.setFont(getContentPane().getFont());
		getContentPane().add(hiddenTextArea);
		
		btnVerwijder = new JButton("Verwijder...");
		btnVerwijder.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnVerwijderenClicked(e);
			}
		});
		btnVerwijder.setBounds(507, 76, 106, 23);
		getContentPane().add(btnVerwijder);

		treeMK.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		treeMK.addTreeSelectionListener(new TreeSelectionListener() {
			
			@Override
			public void valueChanged(TreeSelectionEvent e) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode)
                        treeMK.getLastSelectedPathComponent();

				 /* if nothing is selected */ 
			     if (node == null) return;
			
			     /* retrieve the node that was selected and find the userobject we need*/ 
			     Object nodeInfo = node.getUserObject();
			     if (node.isLeaf())
			     {
			    	 selectedkaart = (KaartInfo)nodeInfo;
			    	 btnWijzigen.setEnabled(true);
			    	 btnVerwijder.setEnabled(true);
			     }
			     else
			     {
			    	 //TreeNode x = node.getLastChild();
			    	 selectedkaart = null;
			    	 btnWijzigen.setEnabled(false);
			    	 btnVerwijder.setEnabled(false);
			    	 /*
			    	 if (x.isLeaf())
			    	 {
			    		 nodeInfo = ((DefaultMutableTreeNode) x).getUserObject();
				    	 selectedkaart = (KaartInfo)nodeInfo;
				    	 btnWijzigen.setEnabled(true);
				    	 btnVerwijder.setEnabled(true);			    	 
				     }
				     */
				    	  
			     }
			}
		});
	}
	protected void newFromClipboard() {
		VerzuimMedischekaartInfo medischekaart = new VerzuimMedischekaartInfo();
		VerzuimMedischeKaartDetail dlgMedischekaart = new VerzuimMedischeKaartDetail(( JFrame )SwingUtilities.getAncestorOfClass(Window.class, btnToevoegen),true);
		medischekaart.setVerzuimId(verzuim.getId());
		try {
			medischekaart.setMedischekaart(Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor).toString());
		} catch (HeadlessException | UnsupportedFlavorException | IOException e) {
        	ExceptionLogger.ProcessException(e,this,"Cannot access clipboard");
        	return;
		}
		dlgMedischekaart.setLoginSession(this.getLoginSession());
		dlgMedischekaart.setInfo(medischekaart);
		dlgMedischekaart.setVisible(true);
		if (dlgMedischekaart.isResult()){
			verzuim.getVerzuimmedischekaarten().add(medischekaart);
			displayMedischeKaart();
		}
	}
	protected void btnVerwijderenClicked(ActionEvent e) {
		try {
			ServiceCaller.verzuimFacade(getLoginSession()).deleteMedischekaart(selectedkaart.medischekaart);
			verzuim.getVerzuimmedischekaarten().remove(selectedkaart.medischekaart);
		} catch (ValidationException e1) {
        	ExceptionLogger.ProcessException(e1,this,false);
        	return;
		} catch (PermissionException e1) {
			ExceptionLogger.ProcessException(e1,this);
			return;
		} catch (VerzuimApplicationException e1) {
        	ExceptionLogger.ProcessException(e1,this);
        	return;
		} catch (Exception e1){
        	ExceptionLogger.ProcessException(e1,this);
        	return;
		}
		displayMedischeKaart();
	}
	protected void btnToevoegenClicked(ActionEvent e) {
		VerzuimMedischekaartInfo medischekaart = new VerzuimMedischekaartInfo();
		VerzuimMedischeKaartDetail dlgMedischekaart = new VerzuimMedischeKaartDetail(( JFrame )SwingUtilities.getAncestorOfClass(Window.class, btnToevoegen),true);
		medischekaart.setVerzuimId(verzuim.getId());
		dlgMedischekaart.setLoginSession(this.getLoginSession());
		dlgMedischekaart.setInfo(medischekaart);
		dlgMedischekaart.setVisible(true);
		if (dlgMedischekaart.isResult()){
			verzuim.getVerzuimmedischekaarten().add(medischekaart);
			displayMedischeKaart();
		}
	}
	protected void btnWijzigenClicked(ActionEvent e) {
		VerzuimMedischeKaartDetail dlgMedischekaart = new VerzuimMedischeKaartDetail(( JFrame )SwingUtilities.getAncestorOfClass(Window.class, btnToevoegen),true);
		dlgMedischekaart.setLoginSession(this.getLoginSession());
		dlgMedischekaart.setInfo(selectedkaart.medischekaart);
		dlgMedischekaart.setVisible(true);
		displayMedischeKaart();
	}
	public class KaartInfo {
        public VerzuimMedischekaartInfo medischekaart = null;
        private String text;
        private Object object;
		public KaartInfo(String displaytext, Object displayobject) {
        	medischekaart = null;
        	text = displaytext;
			setObject(displayobject);
        }

		public KaartInfo(VerzuimMedischekaartInfo kaart, Object displayobject) {
		/*
		 * displayobject: JTextArea die hidden op scherm staat.
		 * 			      deze wordt gebruikt om de exacte lengte van de text
		 * 				  te kunnen bepalen. Dit lukt anders niet goed in
		 * 				  ModifiedTreeCellRenderer
		 */
			medischekaart = kaart;
			text = null;
			setObject(displayobject);
		}

		public String toString() {
			if (medischekaart != null)
				return medischekaart.getMedischekaart();
			else
				return text;
        }
		public String getText() {
			if (medischekaart == null)
				return text;
			else
				return medischekaart.getMedischekaart();
		}
		public void setText(String text) {
			this.text = text;
		}

		public Object getObject() {
			return object;
		}

		public void setObject(Object object) {
			this.object = object;
		}
    }
	 public void expandAll(JTree tree) {
		    TreeNode root = (TreeNode) tree.getModel().getRoot();
		    expandAll(tree, new TreePath(root));
     }

	 private void expandAll(JTree tree, TreePath parent) {
		 TreeNode node = (TreeNode) parent.getLastPathComponent();
		 if (node.getChildCount() >= 0) {
			 for (Enumeration<?> e = node.children(); e.hasMoreElements();) {
				 TreeNode n = (TreeNode) e.nextElement();
				 TreePath path = parent.pathByAddingChild(n);
				 expandAll(tree, path);
			 }
	    }
	    tree.expandPath(parent);
	 }
	public void setInfo(InfoBase info){
		verzuim = (VerzuimInfo) info;
		displayMedischeKaart();
	}
	public void setInfo(InfoBase info, List<GebruikerInfo> gebruikers){
		setGebruikers(gebruikers);
		setInfo(info);
	}
	void displayMedischeKaart(){
		DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
		Date currentDate = null;
		Integer currentUser = -1;
		DefaultMutableTreeNode root = new DefaultMutableTreeNode("Medische kaart");
		DefaultMutableTreeNode dateChild = null;
		DefaultMutableTreeNode userChild = null;
		DefaultMutableTreeNode textChild;
		try {
			verzuim = ServiceCaller.verzuimFacade(getLoginSession()).getVerzuimDetails(verzuim.getId());
		} catch (PermissionException e1) {
			ExceptionLogger.ProcessException(e1,this);
			return;
		} catch (VerzuimApplicationException e1) {
        	ExceptionLogger.ProcessException(e1,this);
        	return;
		} catch (Exception e1){
        	ExceptionLogger.ProcessException(e1,this);
        	return;
		}

		treemodel.setRoot(null);
        hiddenTextArea.setPreferredSize(new Dimension(300,400));
		for (VerzuimMedischekaartInfo ki: verzuim.getVerzuimmedischekaarten())
		{
			if (currentDate == null || !DateOnly.equals(currentDate, ki.getWijzigingsdatum())){
				currentDate = ki.getWijzigingsdatum();
				dateChild = new DefaultMutableTreeNode(new KaartInfo(formatter.format(ki.getWijzigingsdatum()),hiddenTextArea));
				root.add(dateChild);
				currentUser = -1;
			}
			if (currentUser != ki.getUser()){
				if (hmGebruikers == null)
					userChild = new DefaultMutableTreeNode(new KaartInfo("?",hiddenTextArea));
				else{
					String username = hmGebruikers.get(ki.getUser());
					if (username == null)
						userChild = new DefaultMutableTreeNode(new KaartInfo("?",hiddenTextArea));
					else
						userChild = new DefaultMutableTreeNode(new KaartInfo(username,hiddenTextArea));
				}
				currentUser = ki.getUser();
			}
			dateChild.add(userChild);
			textChild = new DefaultMutableTreeNode(new KaartInfo(ki,hiddenTextArea));
			userChild.add(textChild);
		}
		btnWijzigen.setEnabled(false);
		btnVerwijder.setEnabled(false);
		treemodel.setRoot(root);
		expandAll(treeMK);
	}
	public void setGebruikers(List<GebruikerInfo> gebruikers) {
		hmGebruikers = new Hashtable<>();
		for (GebruikerInfo g: gebruikers)
		{
			hmGebruikers.put(g.getId(), g.getVoornaam() + " " + g.getTussenvoegsel() + " " +  g.getAchternaam());
		}
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
class ModifiedTreeCellRenderer extends DefaultTreeCellRenderer {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected JLabel icon;
    protected TreeTextArea text;
    
    public ModifiedTreeCellRenderer() {
    	super();
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        icon = new JLabel() {
            /**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
            public void setBackground(Color color) {
                if (color instanceof ColorUIResource)
                color = null;
                super.setBackground(color);
            }
        };
        text = new TreeTextArea();
        text.setFont(icon.getFont());
    }
    
    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean isSelected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        String stringValue = tree.convertValueToText(value, isSelected, expanded, leaf, row, hasFocus);
        if (value != null && value instanceof DefaultMutableTreeNode)
        {
        	DefaultMutableTreeNode node = (DefaultMutableTreeNode)value;
        	Object userObject = node.getUserObject();
        	if (userObject instanceof KaartInfo)
        	{
        		KaartInfo kaart = (KaartInfo)userObject;
        		
        		if (kaart.getObject() instanceof JTextAreaTGI){
        		
	        		JTextAreaTGI usertext = (JTextAreaTGI)kaart.getObject();
			        usertext.setLineWrap(true);
			        usertext.setWrapStyleWord(true);
			        try {
			        	usertext.setSize(400,100);
			            usertext.setPreferredSize(new Dimension(400, 0));
			            usertext.setText(kaart.getText());
						Rectangle r = usertext.modelToView(usertext.getDocument().getLength());
			            usertext.setPreferredSize(new Dimension(400, r.y + r.height));
				        text.setFont(usertext.getFont());
			            text.setPreferredSize(usertext.getPreferredSize());
				        text.setText(kaart.getText());
			        } catch (BadLocationException e) {
						e.printStackTrace();
						return null;
					}
        		}
        		else{
		            text.setPreferredSize(new Dimension(400, 20));
            		text.setText(kaart.getText());
        		}
        		
        		setEnabled(tree.isEnabled());
        		text.setSelect(isSelected);
        		text.setFocus(hasFocus);
        		
        	}
        	else
        	{
        		text.setText(stringValue);
        	}
    		if (leaf) {
    			icon.setIcon(UIManager.getIcon("Tree.leafIcon"));
    			icon.setIcon(null);
    		} else if (expanded) {
    			icon.setIcon(UIManager.getIcon("Tree.openIcon"));
    		} else {
    			icon.setIcon(UIManager.getIcon("Tree.closedIcon"));
    		}
        }
        return text;
    }
    @Override
    public void setBackground(Color color) {
        if (color instanceof ColorUIResource)
            color = null;
        super.setBackground(color);
    }
 
    class TreeTextArea extends JTextAreaTGI {
        /**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		Dimension preferredSize;
        
        TreeTextArea() {
            setLineWrap(true);
            setWrapStyleWord(true);
            setOpaque(true);
        }
 
        @Override
        public void setBackground(Color color) {
            if (color instanceof ColorUIResource)
            color = null;
            super.setBackground(color);
        }
 
        void setSelect(boolean isSelected) {
            Color bColor;
            if (isSelected) {
                bColor = UIManager.getColor("Tree.selectionBackground");
            } else {
                bColor = UIManager.getColor("Tree.textBackground");
            }
            super.setBackground(bColor);
        }
 
        void setFocus(boolean hasFocus) {
            if (hasFocus) {
                Color lineColor = UIManager.getColor("Tree.selectionBorderColor");
                setBorder(BorderFactory.createLineBorder(lineColor));
            } else {
                setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
            }
        }
    }
}
