package com.gieselaar.verzuim.views;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.gieselaar.verzuim.controllers.AbstractController;
import com.gieselaar.verzuim.controllers.AbstractController.__filedialogtype;
import com.gieselaar.verzuim.controllers.AbstractController.__selectfileoption;
import com.gieselaar.verzuim.utils.ExceptionLogger;
import com.gieselaar.verzuimbeheer.services.InfoBase;
import javax.swing.JPanel;
import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFileChooser;

public abstract class AbstractWizard extends AbstractDetail {
	private static final long serialVersionUID = 1L;
	private JButton btnPrev;
	private JButton btnNext;
	private JPanel panelWizard;
	private CardLayout panels;
	
	public AbstractWizard(String title, AbstractController controller) {
		super(title, controller);
		initialize();
	}
	private void initialize(){
		
		panelWizard = new JPanel();
		panelWizard.setBounds(27, 22, 619, 322);
		getContentPane().add(panelWizard);
		panelWizard.setLayout(new CardLayout(0, 0));
		panels = (CardLayout) panelWizard.getLayout();

		btnPrev = new JButton("Prev");
		btnPrev.setBounds(27, 355, 89, 23);
		btnPrev.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				panels.previous(panelWizard);
			}
		});
		getContentPane().add(btnPrev);
		
		btnNext = new JButton("Next");
		btnNext.setBounds(124, 355, 89, 23);
		btnNext.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				panels.next(panelWizard);
			}
		});
		getContentPane().add(btnNext);
		
		JButton btnBewaar = new JButton("Tijdelijk opslaan...");
		btnBewaar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveIntermediateresults();
			}
		});
		btnBewaar.setBounds(235, 355, 129, 23);
		getContentPane().add(btnBewaar);
		
		JButton btnOphalen = new JButton("Ophalen...");
		btnOphalen.setBounds(373, 355, 105, 23);
		btnOphalen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				loadIntermerdiateresults();
			}
		});
		getContentPane().add(btnOphalen);
	}

	protected abstract void saveIntermediateresults();
	protected abstract void loadIntermerdiateresults();
	@Override
	public InfoBase collectData() {
		return null;
	}
	public JButton getBtnPrev() {
		return btnPrev;
	}
	public JButton getBtnNext() {
		return btnNext;
	}
	public JPanel getPanelWizard() {
		return panelWizard;
	}
	public void setPanelWizard(JPanel panelWizard) {
		this.panelWizard = panelWizard;
	}
	public void savetoFile(ObjectNode rootnode) {
		ObjectMapper jsonobject = new ObjectMapper(); 
		File filetosaveto = controller.selectFilename(__selectfileoption.FILEONLY,__filedialogtype.SAVE, "", ".json");
		if (filetosaveto != null){
		    FileOutputStream fos = null;
		    try {
		        fos = new FileOutputStream(filetosaveto);
		        String jsonstring = jsonobject.writerWithDefaultPrettyPrinter().writeValueAsString(rootnode);
		        fos.write(jsonstring.getBytes());
		    } catch (IOException ex) {
		    	ExceptionLogger.ProcessException(ex,null);
		    } finally {
		        if (fos != null) {
		            try {
		                fos.flush();
		                fos.close();
		            } catch (IOException ex) {
		            	ExceptionLogger.ProcessException(ex,null);
		            }
		        }
		    }
			
		}
	}
	public JsonNode loadfromFile() {
		ObjectMapper jsonobject = new ObjectMapper(); 
		File filetoloadfrom = controller.selectFilename(__selectfileoption.FILEONLY,__filedialogtype.OPEN, "", ".json");
		if (filetoloadfrom != null){
		    FileOutputStream fos = null;
		    try {
		        JsonNode rootnode = jsonobject.readTree(filetoloadfrom);
		        return rootnode;
		    } catch (IOException ex) {
		    	ExceptionLogger.ProcessException(ex,null);
		    } finally {
		        if (fos != null) {
		            try {
		                fos.flush();
		                fos.close();
		            } catch (IOException ex) {
		            	ExceptionLogger.ProcessException(ex,null);
		            }
		        }
		    }
			
		}
		return null;
	}
}
