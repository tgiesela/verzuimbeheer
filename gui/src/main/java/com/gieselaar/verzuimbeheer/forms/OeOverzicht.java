package com.gieselaar.verzuimbeheer.forms;

import java.awt.Window;
import java.util.Enumeration;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.gieselaar.verzuimbeheer.baseforms.BaseDetailform;
import com.gieselaar.verzuimbeheer.baseforms.JDesktopPaneTGI;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.exceptions.PermissionException;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.services.InfoBase;
import com.gieselaar.verzuimbeheer.services.OeInfo;
import com.gieselaar.verzuimbeheer.services.OeNiveauInfo;
import com.gieselaar.verzuimbeheer.utils.ExceptionLogger;
import com.gieselaar.verzuimbeheer.utils.ServiceCaller;
import javax.swing.JButton;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.apache.log4j.Logger;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class OeOverzicht extends BaseDetailform implements TreeSelectionListener {

	private static final long serialVersionUID = 1L;
	private JTree treeOE = null;
	private DefaultTreeModel treemodel = null;
	private JButton btnWijzigen = null;
	private JButton btnToevoegen = null;
	private JButton btnVerwijderen = null; 
	private OeInfo selectedoe = null;
	private List<OeInfo> oes;
	private List<OeNiveauInfo> oeniveaus;
	private boolean inValueChanged = false;
		
	private final static Logger logger = Logger.getLogger(OeOverzicht.class);
	public OeOverzicht (JDesktopPaneTGI mdiPanel) {
		super("Rapportage structuur", mdiPanel);
		initialize();
	}
	private void initialize(){
		setBounds(100, 100, 640, 556);
		getContentPane().setLayout(null);
		treemodel = new DefaultTreeModel(null);
		DefaultTreeCellRenderer treecellrenderer = new DefaultTreeCellRenderer();
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setBounds(23, 26, 461, 449);
		getContentPane().add(scrollPane);
		
		treeOE = new JTree();
		treeOE.setRootVisible(true);
		scrollPane.setViewportView(treeOE);
		treeOE.setModel(treemodel);
		treecellrenderer.setFont(treeOE.getFont());
		treecellrenderer.setOpenIcon(UIManager.getIcon("Tree.openIcon"));
		treecellrenderer.setClosedIcon(UIManager.getIcon("Tree.closedIcon"));
		treeOE.setCellRenderer(treecellrenderer);
		treeOE.setRowHeight(0);
		treeOE.setEditable(true);
		treeOE.addTreeSelectionListener(this);
		
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
		
		btnVerwijderen = new JButton("Verwijderen...");
		btnVerwijderen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnVerwijderenClicked(e);
			}
		});
		btnVerwijderen.setBounds(507, 76, 106, 23);
		getContentPane().add(btnVerwijderen);
		
		treeOE.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
	}
	
	public DefaultMutableTreeNode addObject(Object child) {
	    DefaultMutableTreeNode parentNode = null;
	    TreePath parentPath = treeOE.getSelectionPath();

	    if (parentPath == null) {
	        //There is no selection. Default to the root node.
	        parentNode = (DefaultMutableTreeNode) treeOE.getModel().getRoot();;
	    } else {
	        parentNode = (DefaultMutableTreeNode)
	                     (parentPath.getLastPathComponent());
	    }

	    return addObject(parentNode, child, true);
	}

	public DefaultMutableTreeNode addObject(DefaultMutableTreeNode parent,
	                                        Object child,
	                                        boolean shouldBeVisible) {
	    DefaultMutableTreeNode childNode =
	            new DefaultMutableTreeNode(child);
	    DefaultTreeModel model = (DefaultTreeModel) treeOE.getModel();
	    model.insertNodeInto(childNode, parent,parent.getChildCount());

	    //Make sure the user can see the lovely new node.
	    if (shouldBeVisible) {
	        treeOE.scrollPathToVisible(new TreePath(childNode.getPath()));
	    }
	    return childNode;
	}	
	protected void btnToevoegenClicked(ActionEvent e) {
		/* er moet een niveau worden toegevoegd als child onder de huidige geselecteerd row */

		OeInfo parentoeniveau;
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)treeOE.getLastSelectedPathComponent();
		OeInfo newoe = new OeInfo();
		newoe.setNaam("<nieuw>");
        if (node == null){
        	/* no row selected 
        	 * add row to root node
        	 */
        	
        	node = (DefaultMutableTreeNode) treeOE.getModel().getRoot();
        	newoe.setParentoeId(null);
        }else{
        	if (node.getUserObject() instanceof OeWrapper){
        		OeWrapper wrapper = (OeWrapper) node.getUserObject();
        		parentoeniveau = wrapper.getOeniveau();
        		newoe.setParentoeId(parentoeniveau.getId());
        		/*
        		 * Assumption is we are adding a child oe.
        		 * We have to find the next level in the hierarchy
        		 */
        		for (OeNiveauInfo oni: oeniveaus){
        			if (oni.getParentoeniveauId() != null){
        				if (oni.getParentoeniveauId().equals(parentoeniveau.getOeniveau().getId())){
        					newoe.setOeniveau(oni);
        					break;
        				}
        			}
        		}
        	}
        	else
        	{
            	newoe.setParentoeId(null);
        	}
        }
        
        addObject(new OeWrapper(newoe));

        OeDetail dlgOeDetail = new OeDetail(( JFrame )SwingUtilities.getAncestorOfClass(Window.class, btnToevoegen),true);
		dlgOeDetail.setLoginSession(this.getLoginSession());
		dlgOeDetail.setInfo(newoe);
		dlgOeDetail.setVisible(true);
		if (dlgOeDetail.isResult()){
			displayOestructuur();
		}
	}
	protected void btnWijzigenClicked(ActionEvent e) {
		if (selectedoe == null)
			return;
		
		OeDetail dlgOeDetail = new OeDetail(( JFrame )SwingUtilities.getAncestorOfClass(Window.class, btnToevoegen),true);
		dlgOeDetail.setLoginSession(this.getLoginSession());
		dlgOeDetail.setInfo(selectedoe);
		dlgOeDetail.setVisible(true);
		displayOestructuur();
	}
	protected void btnVerwijderenClicked(ActionEvent e) {
		if (selectedoe == null)
			return;
		
		try {
			ServiceCaller.werkgeverFacade(getLoginSession()).deleteOe(selectedoe);
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
		displayOestructuur();
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

		displayOestructuur();
	}
	private void addOes(int id, DefaultMutableTreeNode parent){
		for (OeInfo subniveau:oes){
			if (subniveau.getParentoeId() == null)
				/* Top level oe */;
			else
			if (subniveau.getParentoeId() == id){
				DefaultMutableTreeNode child = new DefaultMutableTreeNode(new OeWrapper(subniveau)); 
				parent.add(child);
				addOes(subniveau.getId(), child);
			}
		}
	}
	void displayOestructuur(){
		DefaultMutableTreeNode root;
		DefaultMutableTreeNode treeroot;
		
		try {
			oes = ServiceCaller.werkgeverFacade(getLoginSession()).getOes();
			oeniveaus = ServiceCaller.werkgeverFacade(getLoginSession()).getOeNiveaus();
		} catch (PermissionException e) {
			ExceptionLogger.ProcessException(e,this);
			return;
		} catch (VerzuimApplicationException e) {
        	ExceptionLogger.ProcessException(e,this);
        	return;
		} catch (Exception e1){
        	ExceptionLogger.ProcessException(e1,this);
        	return;
		}
		treeroot = new DefaultMutableTreeNode("Rapportage structuren");
		if (oes != null)
			for(OeInfo a :oes)
			{
				if (a.getOeniveau() != null){
					if (a.getOeniveau().getOeniveau() == 1){
						root = new DefaultMutableTreeNode(new OeWrapper(a));
						addOes(a.getId(), root);
						treeroot.add(root);
					}
				}
			}
		
		
        btnWijzigen.setEnabled(false);
        btnVerwijderen.setEnabled(false);
		treemodel.setRoot(treeroot);
		expandAll(treeOE);
	}

	
	@Override
	public void valueChanged(TreeSelectionEvent e) {
		if (!inValueChanged) {  
            inValueChanged = true;  
            try {  
                TreePath treePath = e.getPath();  
                DefaultMutableTreeNode selected = (DefaultMutableTreeNode) treePath.getLastPathComponent();  
                DefaultMutableTreeNode parent = (DefaultMutableTreeNode) selected.getParent();  
                if (parent == null) {  
                    logger.debug("path too short");  
                    return;  
                }

                DefaultMutableTreeNode node = (DefaultMutableTreeNode)treeOE.getLastSelectedPathComponent();

        		if (node == null)
        		  	//Nothing is selected.  
        		   return;
        		
        		OeWrapper nodeInfo = (OeWrapper)selected.getUserObject();
        		selectedoe = nodeInfo.getOeniveau();
        		btnWijzigen.setEnabled(true);
        		btnVerwijderen.setEnabled(true);
            } finally {  
                inValueChanged = false;  
            }  
        }  
	}
	private class OeWrapper{
		private OeInfo oeniveau;
		OeWrapper(OeInfo o){
			oeniveau = o;
		}
		public OeInfo getOeniveau(){
			return this.oeniveau;
		}
		public String toString(){
			return oeniveau.getNaam();
		}
	}
}
