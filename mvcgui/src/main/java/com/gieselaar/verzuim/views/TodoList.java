package com.gieselaar.verzuim.views;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.gieselaar.verzuim.controllers.TodoController;
import com.gieselaar.verzuim.controllers.TodoController.__todocommands;
import com.gieselaar.verzuim.controllers.TodoController.__todofields;
import com.gieselaar.verzuim.viewsutils.ColorTableModel;
import com.gieselaar.verzuim.viewsutils.CursorController;
import com.gieselaar.verzuimbeheer.services.TodoFastInfo;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.RowSorter;
import javax.swing.SortOrder;

public class TodoList extends AbstractList {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private TodoController todocontroller;
	private JButton btnWerknemer;
	/* Variables */
	/**
	 * Create the frame.
	 */
	public TodoList(TodoController controller) {
		super("Overzicht todos",controller);
		initialize();
		
		todocontroller = controller;
		addColumn(__todofields.INDICATOR.getValue(),"", 20);
		addColumn(__todofields.DEADLINE.getValue(),"Deadline", 80, Date.class);
		addColumn(__todofields.WAARSCHUWEN.getValue(),"Waarschuwen", 80, Date.class);
		addColumn(__todofields.ACTIVITEIT.getValue(),"Activiteit", 100);
		addColumn(__todofields.BSN.getValue(),"BSN", 80);
		addColumn(__todofields.ACHTERNAAM.getValue(),"Achternaam", 120);
		addColumn(__todofields.WERKGEVER.getValue(),"Werkgever", 120);
		addColumn(__todofields.AANVANGVERZUIM.getValue(),"Aanvang vzm", 80, Date.class);
		addColumn(__todofields.HERHALEN.getValue(),"Herhalen", 60);

		List<RowSorter.SortKey> sortKeys;
		sortKeys = new ArrayList<>();
        sortKeys.add(new RowSorter.SortKey(1, SortOrder.ASCENDING));
        getSorter().setSortKeys(sortKeys);
        getSorter().sort();
	}
	private void initialize() {
		JCheckBox chckbxToekomstigeTonen = new JCheckBox("Toekomstige tonen");
		chckbxToekomstigeTonen.setSelected(false);
		chckbxToekomstigeTonen.setBounds(297, 422, 104, 23);
		chckbxToekomstigeTonen.setActionCommand(__todocommands.TODOTOEKOMSTIGETONEN.toString());
		chckbxToekomstigeTonen.addActionListener(CursorController.createListener(this,controller));
		DatatablePanel datatablePanel = super.getDatatable();
		datatablePanel.getPanelAction().setBounds(36, 467, 659, 30);
		datatablePanel.getPanelAction().add(chckbxToekomstigeTonen);
		
		JCheckBox chckbxAfgeslotenTonen = new JCheckBox("Afgesloten tonen");
		chckbxAfgeslotenTonen.setBounds(403, 422, 133, 23);
		chckbxAfgeslotenTonen.setActionCommand(__todocommands.TODOAFGESLOTENTONEN.toString());
		chckbxAfgeslotenTonen.addActionListener(CursorController.createListener(this,controller));
		super.getDatatable().getPanelAction().add(chckbxAfgeslotenTonen);

		btnWerknemer = new JButton("Werknr/vzm...");
		btnWerknemer.setSelected(false);
		btnWerknemer.setActionCommand(__todocommands.WERKNEMERTONEN.toString());
		btnWerknemer.addActionListener(CursorController.createListener(this, controller));
		super.getDatatable().getPanelAction().add(btnWerknemer);
	}
	@Override
	public void refreshTable() {
		List<TodoFastInfo> todos;
		todos = todocontroller.getTodoList();

		/* Save and remove sorts and filters before populating the table */
		this.getDatatable().disableSorter();

		TodoFastInfo.sort(todos,TodoFastInfo.__sortcol.DATUMDEADLINE);
		todocontroller.getTableModel(todos, (ColorTableModel) this.getTable().getModel(), colsinview);

		/* Add sorts and filters again */
		this.getDatatable().enableSorter();
		btnWerknemer.setEnabled(false);
	}
	public void setRowSelected() {
		btnWerknemer.setEnabled(true);
	}
}
