package com.gieselaar.verzuimbeheer.forms;

import java.awt.Color;
import java.awt.Component;
import java.math.BigDecimal;
import java.util.Date;
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
import com.gieselaar.verzuimbeheer.services.BtwInfo;
import com.gieselaar.verzuimbeheer.services.BtwInfo.__btwtariefsoort;
import com.gieselaar.verzuimbeheer.services.InfoBase.persistenceaction;
import com.gieselaar.verzuimbeheer.services.InfoBase;
import com.gieselaar.verzuimbeheer.services.TariefInfo;
import com.gieselaar.verzuimbeheer.services.WerkgeverInfo;
import com.gieselaar.verzuimbeheer.utils.ExceptionLogger;
import com.gieselaar.verzuimbeheer.utils.ServiceCaller;

import javax.swing.JCheckBox;
import javax.swing.table.TableModel;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class BtwOverzicht extends BaseListForm {


	/**
	 * 
	 */
	private static final long serialVersionUID = 9202374053287935929L;
	private List<BtwInfo> btwpercentages;
	private JCheckBox cbOudePercentagesTonen;
	private Component thisform = this;
	@SuppressWarnings("unused")
	private WerkgeverInfo werkgever = null;
	/**
	 * Create the frame.
	 */
	public BtwOverzicht(JDesktopPaneTGI mdiPanel) {
		super("BTW overzicht", mdiPanel);
		getPanelAction().setBounds(0, 399, 621, 33);
		
		cbOudePercentagesTonen = new JCheckBox("Oude percentages tonen");
		getPanelAction().add(cbOudePercentagesTonen);
		cbOudePercentagesTonen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cbOudePercentagesTonenClicked(e);
			}
		});
		cbOudePercentagesTonen.setSelected(false);
				
		getScrollPane().setBounds(36, 40, 730, 348);
		initialize();
	}


	protected void cbOudePercentagesTonenClicked(ActionEvent e) {
		displayTarieven(cbOudePercentagesTonen.isSelected());
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
					int rslt = JOptionPane.showConfirmDialog(thisform, "Weet u zeker dat u het BTW percentage wilt verwijderen?","",JOptionPane.YES_NO_OPTION);
					if (rslt == JOptionPane.YES_OPTION){
						ServiceCaller.werkgeverFacade(getLoginSession()).deleteTarief((TariefInfo)info);
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
				werkgever = (WerkgeverInfo) getParentInfo();
				BtwInfo btwpercentage = (BtwInfo)info;
				btwpercentage.setId(-1);
				btwpercentage.setIngangsdatum(new Date());
				btwpercentage.setEinddatum(null);
				btwpercentage.setBtwtariefsoort(__btwtariefsoort.HOOG);
				btwpercentage.setPercentage(new BigDecimal(0));
			}
			
		});
		setBounds(100, 100, 807, 459);
		setDetailFormClass(BtwDetail.class, BtwInfo.class);
		
		addColumn("Ingangsdatum",null,80, Date.class);
		addColumn("Einddatum",null,60, Date.class);
		addColumn("Percentage",null,60);
		addColumn("Soort",null,60);
	}

	private void displayTarieven(boolean showAfgeslotentarieven){
		deleteAllRows();
		List<? extends SortKey> keys = this.getSorter().getSortKeys();
		RowFilter<? super TableModel, ? super Integer> filter = this.getSorter().getRowFilter();
		this.getSorter().setSortKeys(null);
		this.getSorter().setRowFilter(null);
		for(BtwInfo w :btwpercentages)
		{
			if (w.getAction() == persistenceaction.DELETE)
				;
			else
			{
				Vector<Object> rowData = new Vector<Object>();
				rowData.add(w.getIngangsdatum());
				rowData.add(w.getEinddatum());
				rowData.add(w.getPercentage().toString());
				rowData.add(w.getBtwtariefsoort().toString());
				if (w.getEinddatum() != null)
				{
					if (showAfgeslotentarieven)
						addRow(rowData, w, Color.GRAY);
				}
				else
					addRow(rowData, w);
			}
		}
		this.getSorter().setRowFilter(filter);
		this.getSorter().setSortKeys(keys);
		
	}

	public void populateTable(){
		werkgever = (WerkgeverInfo)this.getParentInfo();
		try {
			btwpercentages = ServiceCaller.factuurFacade(getLoginSession()).allBtws();
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
		displayTarieven(cbOudePercentagesTonen.isSelected());
	}
}
