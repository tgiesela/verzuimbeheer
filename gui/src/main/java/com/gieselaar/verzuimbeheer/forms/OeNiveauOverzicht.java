package com.gieselaar.verzuimbeheer.forms;

import java.awt.Window;
import java.util.Enumeration;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.gieselaar.verzuimbeheer.baseforms.BaseDetailform;
import com.gieselaar.verzuimbeheer.baseforms.JDesktopPaneTGI;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.exceptions.PermissionException;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.services.InfoBase;
import com.gieselaar.verzuimbeheer.services.OeNiveauInfo;
import com.gieselaar.verzuimbeheer.utils.ExceptionLogger;
import com.gieselaar.verzuimbeheer.utils.ServiceCaller;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import javax.swing.JScrollPane;
import javax.swing.JButton;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.ScrollPaneConstants;

import org.apache.log4j.Logger;

public class OeNiveauOverzicht extends BaseDetailform implements TreeSelectionListener {

	private static final long serialVersionUID = 1L;
	private JTree treeOEN = null;
	private DefaultTreeModel treemodel = null;
	private JButton btnWijzigen = null;
	private JButton btnToevoegen = null;
	private JButton btnVerwijderen = null; 
	private OeNiveauInfo selectedoeniveau = null;
	private List<OeNiveauInfo> oeniveaus;
	private boolean inValueChanged = false;
	
	private final static Logger logger = Logger.getLogger(OeNiveauOverzicht.class);
	public OeNiveauOverzicht (JDesktopPaneTGI mdiPanel) {
		super("Rapportage niveaus", mdiPanel);
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
		
		treeOEN = new JTree();
		treeOEN.setRootVisible(true);
		scrollPane.setViewportView(treeOEN);
		treeOEN.setModel(treemodel);
		treecellrenderer.setFont(treeOEN.getFont());
		treecellrenderer.setOpenIcon(UIManager.getIcon("Tree.openIcon"));
		treecellrenderer.setClosedIcon(UIManager.getIcon("Tree.closedIcon"));
		treeOEN.setCellRenderer(treecellrenderer);
		treeOEN.setRowHeight(0);
		treeOEN.setEditable(true);
		treeOEN.addTreeSelectionListener(this);
		
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
		
		treeOEN.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
	}
	
	public DefaultMutableTreeNode addObject(Object child) {
	    DefaultMutableTreeNode parentNode = null;
	    TreePath parentPath = treeOEN.getSelectionPath();

	    if (parentPath == null) {
	        //There is no selection. Default to the root node.
	        parentNode = (DefaultMutableTreeNode) treeOEN.getModel().getRoot();;
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
	    DefaultTreeModel model = (DefaultTreeModel) treeOEN.getModel();
	    model.insertNodeInto(childNode, parent,parent.getChildCount());

	    //Make sure the user can see the lovely new node.
	    if (shouldBeVisible) {
	        treeOEN.scrollPathToVisible(new TreePath(childNode.getPath()));
	    }
	    return childNode;
	}	
	protected void btnToevoegenClicked(ActionEvent e) {
		/* er moet een niveau worden toegevoegd als child onder de huidige geselecteerd row */

		OeNiveauInfo parentoeniveau;
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)treeOEN.getLastSelectedPathComponent();
		OeNiveauInfo newniveau = new OeNiveauInfo();
		newniveau.setNaam("<nieuw>");
        if (node == null){
        	/* no row selected 
        	 * add row to root node
        	 */
        	
        	node = (DefaultMutableTreeNode) treeOEN.getModel().getRoot();
        	newniveau.setParentoeniveauId(null);
        	newniveau.setOeniveau(1);
        }else{
        	if (node.getUserObject() instanceof OeNiveauWrapper){
        		OeNiveauWrapper wrapper = (OeNiveauWrapper) node.getUserObject();
        		parentoeniveau = wrapper.getOeniveau();
        		newniveau.setParentoeniveauId(parentoeniveau.getId());
        		newniveau.setOeniveau(parentoeniveau.getOeniveau()+1);
        	}
        	else
        	{
            	newniveau.setParentoeniveauId(null);
            	newniveau.setOeniveau(1);
        	}
        }
        
        addObject(new OeNiveauWrapper(newniveau));

        OeNiveauDetail dlgOeNiveauDetail = new OeNiveauDetail(( JFrame )SwingUtilities.getAncestorOfClass(Window.class, btnToevoegen),true);
		dlgOeNiveauDetail.setLoginSession(this.getLoginSession());
		dlgOeNiveauDetail.setInfo(newniveau);
		dlgOeNiveauDetail.setVisible(true);
		if (dlgOeNiveauDetail.isResult()){
			displayOestructuur();
		}
	}
	protected void btnWijzigenClicked(ActionEvent e) {
		if (selectedoeniveau == null)
			return;
		
		OeNiveauDetail dlgOeNiveauDetail = new OeNiveauDetail(( JFrame )SwingUtilities.getAncestorOfClass(Window.class, btnToevoegen),true);
		dlgOeNiveauDetail.setLoginSession(this.getLoginSession());
		dlgOeNiveauDetail.setInfo(selectedoeniveau);
		dlgOeNiveauDetail.setVisible(true);
		displayOestructuur();
	}
	protected void btnVerwijderenClicked(ActionEvent e) {
		if (selectedoeniveau == null)
			return;
		
		try {
			ServiceCaller.werkgeverFacade(getLoginSession()).deleteOeniveau(selectedoeniveau);
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
	private void addSubniveaus(int id, DefaultMutableTreeNode parent){
		for (OeNiveauInfo subniveau:oeniveaus){
			if (subniveau.getParentoeniveauId() == null)
				/* Top level oeniveau */;
			else
			if (subniveau.getParentoeniveauId() == id){
				DefaultMutableTreeNode child = new DefaultMutableTreeNode(new OeNiveauWrapper(subniveau)); 
				parent.add(child);
				addSubniveaus(subniveau.getId(), child);
			}
		}
	}
	void displayOestructuur(){
		DefaultMutableTreeNode root;
		DefaultMutableTreeNode treeroot;
		
		try {
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
		treeroot = new DefaultMutableTreeNode("Rapportage niveaus");
		if (oeniveaus != null)
			for(OeNiveauInfo a :oeniveaus)
			{
				if (a.getOeniveau() == 1){
					root = new DefaultMutableTreeNode(new OeNiveauWrapper(a));
					addSubniveaus(a.getId(), root);
					treeroot.add(root);
				}
			}
		
		
        btnWijzigen.setEnabled(false);
        btnVerwijderen.setEnabled(false);
		treemodel.setRoot(treeroot);
		expandAll(treeOEN);
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
/*
                int index = parent.getIndex(selected)+1;  
                String userObject = String.valueOf(System.currentTimeMillis());  
                DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(userObject);  
                JTree tree = (JTree) e.getSource();  
                DefaultTreeModel model = (DefaultTreeModel) tree.getModel();  
                model.insertNodeInto(newNode, parent, index);  
                tree.setSelectionPath(treePath.getParentPath().pathByAddingChild(newNode));  
*/
                DefaultMutableTreeNode node = (DefaultMutableTreeNode)treeOEN.getLastSelectedPathComponent();

        		if (node == null)
        		  	//Nothing is selected.  
        		   return;
        		
        		OeNiveauWrapper nodeInfo = (OeNiveauWrapper)selected.getUserObject();
        		selectedoeniveau = nodeInfo.getOeniveau();
        		btnWijzigen.setEnabled(true);
        		btnVerwijderen.setEnabled(true);
            } finally {  
                inValueChanged = false;  
            }  
        }  
	}
	private class OeNiveauWrapper{
		private OeNiveauInfo oeniveau;
		OeNiveauWrapper(OeNiveauInfo o){
			oeniveau = o;
		}
		public OeNiveauInfo getOeniveau(){
			return this.oeniveau;
		}
		public String toString(){
			return oeniveau.getNaam();
		}
	}
}
