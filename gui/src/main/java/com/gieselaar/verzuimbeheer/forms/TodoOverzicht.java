package com.gieselaar.verzuimbeheer.forms;

import java.awt.Color;
import java.awt.Component;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.RowFilter;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.RowSorter.SortKey;
import javax.swing.table.TableModel;

import com.gieselaar.verzuimbeheer.baseforms.BaseDetailform.formMode;
import com.gieselaar.verzuimbeheer.baseforms.BaseListForm;
import com.gieselaar.verzuimbeheer.baseforms.DefaultListFormNotification;
import com.gieselaar.verzuimbeheer.baseforms.JDesktopPaneTGI;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.exceptions.PermissionException;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.services.ActiviteitInfo;
import com.gieselaar.verzuimbeheer.services.InfoBase;
import com.gieselaar.verzuimbeheer.services.InfoBase.persistenceaction;
import com.gieselaar.verzuimbeheer.services.InfoBase.persistencestate;
import com.gieselaar.verzuimbeheer.services.TodoFastInfo;
import com.gieselaar.verzuimbeheer.services.TodoInfo;
import com.gieselaar.verzuimbeheer.services.WerknemerFastInfo;
import com.gieselaar.verzuimbeheer.utils.ExceptionLogger;
import com.gieselaar.verzuimbeheer.utils.ServiceCaller;
import javax.swing.JCheckBox;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.JButton;

public class TodoOverzicht extends BaseListForm {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<TodoFastInfo> todos = null;
	private List<ActiviteitInfo> activiteiten;
	private JCheckBox chckbxAfgeslotenTonen;
	private JCheckBox chckbxToekomstigeTonen;
	private Component thisform = this;
	private JButton btnWerknemerverzuim;
	private TodoFastInfo todo = null;
	
	public TodoOverzicht(JDesktopPaneTGI mdiPanel) {
		super("Overzicht todos", mdiPanel);
		getPanelAction().setBounds(0, 483, 594, 33);
		chckbxAfgeslotenTonen = new JCheckBox("Afgesloten tonen");
		getPanelAction().add(chckbxAfgeslotenTonen);
		
		chckbxToekomstigeTonen = new JCheckBox("Toekomstige tonen");
		getPanelAction().add(chckbxToekomstigeTonen);
		
		btnWerknemerverzuim = new JButton("Werkn/Vzm...");
		btnWerknemerverzuim.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnWerknemerverzuimClicked(e);
			}
		});
		getPanelAction().add(btnWerknemerverzuim);
		chckbxToekomstigeTonen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				displayTodos();
			}
		});
		chckbxAfgeslotenTonen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				displayTodos();
			}
		});
		getScrollPane().setBounds(36, 40, 660, 432);
		initialize();
		
	}
	protected void btnWerknemerverzuimClicked(ActionEvent e) {
		JDesktopPaneTGI mdiPanel = this.getMdiPanel();
		WerknemerDetail dlgWerknemerdetail;
		dlgWerknemerdetail = new WerknemerDetail(mdiPanel);
		dlgWerknemerdetail.setMode(formMode.Update);
		dlgWerknemerdetail.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		dlgWerknemerdetail.setLoginSession(getLoginSession());
		dlgWerknemerdetail.setVisible(true);
		WerknemerFastInfo wfi = new WerknemerFastInfo();
		wfi.setId(todo.getWerknemerid());
		dlgWerknemerdetail.setInfo(wfi);
		mdiPanel.add(dlgWerknemerdetail);
		mdiPanel.setOpaque(true);
		mdiPanel.moveToFront(dlgWerknemerdetail);
	}
	public void initialize() {
		setDetailFormClass(TodoDetail.class, TodoInfo.class);
		setBounds(100, 100, 744, 543);
		this.addColumn("", null, 20);
		this.addColumn("Deadline",null,80, Date.class);
		this.addColumn("Activiteit", null, 100);
		this.addColumn("BSN",null,80);
		this.addColumn("Achternaam",null,120);
		this.addColumn("Werkgever",null,120);
		this.addColumn("Aanvang vzm",null,80, Date.class);
		this.setEventNotifier(new DefaultListFormNotification() {
			
			@Override
			public void populateTableRequested() {
				populateTable();
			}
			@Override
			public void newCreated(InfoBase info) {
				JOptionPane.showMessageDialog(thisform, "Aanmaken nieuwe kan Todo alleen via Verzuimen");
			}
			@Override 
			public __continue newButtonClicked(){
				JOptionPane.showMessageDialog(thisform, "Aanmaken nieuwe kan Todo alleen via Verzuimen");
				return __continue.dontallow;
			}
			@Override
			public __continue deleteButtonClicked(InfoBase info) {
				try {
					int rslt = JOptionPane.showConfirmDialog(thisform, "Weet u zeker dat u de Todo wilt verwijderen?","",JOptionPane.YES_NO_OPTION);
					if (rslt == JOptionPane.YES_OPTION){
						TodoFastInfo tfi = (TodoFastInfo)info;
						TodoInfo ti = new TodoInfo();
						ti.setAction(persistenceaction.DELETE);
						ti.setState(persistencestate.EXISTS);
						ti.setVersion(tfi.getVersion());
						ti.setId(tfi.getId());
						ti.setDeadlinedatum(tfi.getDeadlinedatum());
						ti.setWaarschuwingsdatum(tfi.getWaarschuwingsdatum());
						ti.setSoort(tfi.getSoort());
						ti.setUser(tfi.getUser());
						ti.setHerhalen(tfi.getHerhalen());
						ti.setVerzuimId(tfi.getVerzuimId());
						ServiceCaller.verzuimFacade(getLoginSession()).deleteTodo(ti);
					}
					else
						return __continue.dontallow;
				} catch (ValidationException e) {
					ExceptionLogger.ProcessException(e,thisform,false);
				} catch (PermissionException e) {
					ExceptionLogger.ProcessException(e,thisform);
				} catch (VerzuimApplicationException e) {
		        	ExceptionLogger.ProcessException(e,thisform);
				} catch (Exception e1){
		        	ExceptionLogger.ProcessException(e1,thisform);
				}
				return __continue.allow;
			}
			@Override
			public void rowSelected(InfoBase info){
				btnWerknemerverzuim.setEnabled(true);
				todo = (TodoFastInfo)info;
			}
		});
		
	}
	void populateTable(){
		try {
			todos = ServiceCaller.verzuimFacade(getLoginSession()).getTodosFast();
			activiteiten = ServiceCaller.verzuimFacade(getLoginSession()).getActiviteiten();
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
		displayTodos();
		btnWerknemerverzuim.setEnabled(false);
		todo = null;
	}
	private void displayTodos(){
		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		ActiviteitInfo act = null;
		Date vandaag = null;
		boolean tonen;
		
		try {
			vandaag = formatter.parse(formatter.format(new Date()));
		} catch (ParseException e) {
			e.printStackTrace();
			return;
		}
		int rownr = 0;

		List<? extends SortKey> keys = this.getSorter().getSortKeys();
		RowFilter<? super TableModel, ? super Integer> filter = this
				.getSorter().getRowFilter();
		this.getSorter().setSortKeys(null);
		this.getSorter().setRowFilter(null);

		deleteAllRows();
		if (todos != null)
		{
			for (TodoFastInfo t:todos){
				Vector<Object> rowData = new Vector<Object>();
				rowData.add("");
				for (ActiviteitInfo ai: activiteiten)
					if (ai.getId().equals(t.getActiviteitId())){
						act = ai;
						break;
					}
				if (t.getDeadlinedatum() == null)
					rowData.add("");
				else
					rowData.add(t.getDeadlinedatum());
				rowData.add(act.getNaam());
				rowData.add(t.getBurgerservicenummer());
				rowData.add(t.getAchternaam());
				rowData.add(t.getWerkgevernaam());
				rowData.add(t.getStartdatumverzuim());
				
				tonen = true;
				if (t.getDeadlinedatum().after(vandaag) &&
					t.getWaarschuwingsdatum().after(vandaag))
					if (!chckbxToekomstigeTonen.isSelected())
						tonen = false; /* Toekomstige todos worden niet getoond*/
				if (t.getVerzuimactiviteitId() != null)
					if (!chckbxAfgeslotenTonen.isSelected())
						tonen = false;/* uitgevoerde todos worden niet getoond*/
				
				if (tonen)
				{
					this.addRow(rowData,t);
					rownr++;
					if (t.getVerzuimactiviteitId() != null)
						this.setCellColour(rownr-1,0,Color.BLUE);
					else
						if (t.getDeadlinedatum().before(vandaag))
							this.setCellColour(rownr-1,0,Color.RED);
						else
							if (t.getWaarschuwingsdatum().after(vandaag))
								this.setCellColour(rownr-1,0,Color.GREEN);
							else
								this.setCellColour(rownr-1,0,Color.ORANGE);
				}
			}
		}
		this.getSorter().setRowFilter(filter);
		this.getSorter().setSortKeys(keys);
	}
}
