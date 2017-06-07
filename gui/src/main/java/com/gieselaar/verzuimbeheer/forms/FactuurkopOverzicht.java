package com.gieselaar.verzuimbeheer.forms;

import java.awt.Component;
import java.util.List;
import java.util.Vector;

import javax.swing.JOptionPane;
import javax.swing.RowFilter;
import javax.swing.RowSorter.SortKey;











import com.gieselaar.verzuimbeheer.baseforms.BaseListForm;
//import com.gieselaar.verzuimbeheer.baseforms.BaseListFormOld;
import com.gieselaar.verzuimbeheer.baseforms.DefaultListFormNotification;
import com.gieselaar.verzuimbeheer.baseforms.JDesktopPaneTGI;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.exceptions.PermissionException;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.services.FactuurkopInfo;
import com.gieselaar.verzuimbeheer.services.InfoBase.persistenceaction;
import com.gieselaar.verzuimbeheer.services.InfoBase;
import com.gieselaar.verzuimbeheer.utils.ExceptionLogger;
import com.gieselaar.verzuimbeheer.utils.ServiceCaller;

import javax.swing.table.TableModel;

public class FactuurkopOverzicht extends BaseListForm {


	/**
	 * 
	 */
	private static final long serialVersionUID = 9202374053287935929L;
	private List<FactuurkopInfo> factuurkoppen;
	private Component thisform = this;
	/**
	 * Create the frame.
	 */
	public FactuurkopOverzicht(JDesktopPaneTGI mdiPanel) {
		super("Factuurkoppen overzicht", mdiPanel);
		getPanelAction().setBounds(0, 399, 621, 33);
		
		getScrollPane().setBounds(36, 40, 730, 348);
		initialize();
	}

	public void initialize() {
		this.setEventNotifier(new DefaultListFormNotification() {
			
			@Override
			public void populateTableRequested() {
				populateTable();
			}
			@Override
			public __continue deleteButtonClicked(InfoBase info) {
				try {
					int rslt = JOptionPane.showConfirmDialog(thisform, "Weet u zeker dat u de factuurkop wilt verwijderen?","",JOptionPane.YES_NO_OPTION);
					if (rslt == JOptionPane.YES_OPTION){
						ServiceCaller.factuurFacade(getLoginSession()).deleteFactuurkop((FactuurkopInfo)info);
					}
					else
						return __continue.dontallow;
				} catch (ValidationException e) {
					ExceptionLogger.ProcessException(e,thisform,false);
				} catch (PermissionException e) {
					ExceptionLogger.ProcessException(e,thisform);
				} catch (VerzuimApplicationException e2) {
		        	ExceptionLogger.ProcessException(e2,thisform);
				} catch (Exception e1){
		        	ExceptionLogger.ProcessException(e1,thisform);
				}
				return __continue.allow;
			}
			@Override
			public __continue detailButtonClicked(InfoBase info){
				return __continue.allow;
			}

			@Override
			public void newCreated(InfoBase info) {
				FactuurkopInfo factuurkop = (FactuurkopInfo)info;
				factuurkop.setId(-1);
			}
			
		});
		setBounds(100, 100, 807, 459);
		setDetailFormClass(FactuurkopDetail.class, FactuurkopInfo.class);
		
		addColumn("Omschrijving",null,120);
		addColumn("Prioriteit",null,60);
	}

	private void displayFactuurkoppen(){
		deleteAllRows();
		List<? extends SortKey> keys = this.getSorter().getSortKeys();
		RowFilter<? super TableModel, ? super Integer> filter = this.getSorter().getRowFilter();
		this.getSorter().setSortKeys(null);
		this.getSorter().setRowFilter(null);
		for(FactuurkopInfo w :factuurkoppen)
		{
			if (w.getAction() == persistenceaction.DELETE)
				;
			else
			{
				Vector<Object> rowData = new Vector<Object>();
				rowData.add(w.getOmschrijving());
				rowData.add(Integer.toString(w.getPrioriteit()));
				addRow(rowData, w);
			}
		}
		this.getSorter().setRowFilter(filter);
		this.getSorter().setSortKeys(keys);
		
	}

	public void populateTable(){
		try {
			factuurkoppen = ServiceCaller.factuurFacade(getLoginSession()).getFactuurkoppen();
		} catch (PermissionException e) {
			ExceptionLogger.ProcessException(e,this);
			return;
		} catch (VerzuimApplicationException e2) {
        	ExceptionLogger.ProcessException(e2,this);
        	return;
		} catch (Exception e1){
        	ExceptionLogger.ProcessException(e1,this);
        	return;
		}
		displayFactuurkoppen();
	}
}
