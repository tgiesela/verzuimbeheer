package com.gieselaar.verzuim.views;

import java.util.List;
import com.gieselaar.verzuim.controllers.AbstractController;
import com.gieselaar.verzuim.controllers.CascodeController;
import com.gieselaar.verzuim.controllers.CascodeController.__cascodefields;
import com.gieselaar.verzuim.utils.ExceptionLogger;
import com.gieselaar.verzuim.viewsutils.ColorTableModel;
import com.gieselaar.verzuim.viewsutils.VerzuimComboBoxModel;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.services.CascodeInfo;
import com.gieselaar.verzuimbeheer.utils.TypeEntry;

import javax.swing.JComboBox;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class CascodeList extends AbstractList{

	private static final long serialVersionUID = 1L;
	private JComboBox<TypeEntry> cmbCascodeGroep;
	private VerzuimComboBoxModel cascodegroepModel;
	private CascodeController cascodecontroller;
	
	public CascodeList(AbstractController controller) {
		super("Overzicht cascodes", controller);
		cascodecontroller = (CascodeController) controller;
		initialize();

		addColumn(__cascodefields.NAAM.getValue(),"Naam",150);
		addColumn(__cascodefields.OMSCHRIJVING.getValue(),"Omschrijving",150);
		
		try {
			cascodecontroller.selectCascodegroepen();
		} catch (VerzuimApplicationException e) {
			ExceptionLogger.ProcessException(e, this);
		}
		cascodecontroller.updateComboModelCascodegroepen((VerzuimComboBoxModel) cmbCascodeGroep.getModel());
	}
	public void initialize(){
		cmbCascodeGroep = new JComboBox<>();
		cmbCascodeGroep.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cmbCascodeGroepClicked(e);
			}
		});
		cmbCascodeGroep.setBounds(36, 11, 251, 20);
		this.getDatatable().getPanelAction().add(cmbCascodeGroep);
		
		cascodegroepModel = new VerzuimComboBoxModel(controller);
		cmbCascodeGroep.setModel(cascodegroepModel);
	}
	@Override
	public void refreshTable() {
		List<CascodeInfo> cascodes;
		cascodes = cascodecontroller.getCascodesList();

		/* Save and remove sorts and filters before populating the table */
		this.getDatatable().disableSorter();
		
		cascodecontroller.getTableModel(cascodes, (ColorTableModel) this.getTable().getModel(), colsinview);

		/* Add sorts and filters again */
		this.getDatatable().enableSorter();
	}
	
	protected void cmbCascodeGroepClicked(ActionEvent e) {
		Integer selectedcascodegroepid;
		selectedcascodegroepid = cascodegroepModel.getId();
		try {
			cascodecontroller.selectCascodes(selectedcascodegroepid);
		} catch (VerzuimApplicationException e1) {
			ExceptionLogger.ProcessException(e1, this);
		}
	}
}
