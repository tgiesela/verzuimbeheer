package com.gieselaar.verzuimbeheer.forms;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.swing.JCheckBox;
import javax.swing.JOptionPane;

import com.gieselaar.verzuimbeheer.baseforms.BaseListForm;
import com.gieselaar.verzuimbeheer.baseforms.DefaultListFormNotification;
import com.gieselaar.verzuimbeheer.baseforms.JDesktopPaneTGI;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.exceptions.PermissionException;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.services.AdresInfo;
import com.gieselaar.verzuimbeheer.services.ContactpersoonInfo;
import com.gieselaar.verzuimbeheer.services.HoldingInfo;
import com.gieselaar.verzuimbeheer.services.InfoBase;
import com.gieselaar.verzuimbeheer.utils.ExceptionLogger;
import com.gieselaar.verzuimbeheer.utils.ServiceCaller;


public class HoldingOverzicht extends BaseListForm {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<HoldingInfo> holdings = null;
	private Component thisform = this;
	private JCheckBox chckbxAfgeslotenTonen = null;	

	/**
	 * Create the frame.
	 */
	public HoldingOverzicht(JDesktopPaneTGI mdiPanel) {
		super("Overzicht holdings",mdiPanel);
		getPanelAction().setBounds(0, 330, 554, 33);
		chckbxAfgeslotenTonen = new JCheckBox("Afgesloten tonen");
		getPanelAction().add(chckbxAfgeslotenTonen);
		chckbxAfgeslotenTonen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cbAfgeslotenTonenClicked(e);
			}
		});
		initialize();
	}
	void initialize(){

		this.setEventNotifier(new DefaultListFormNotification() {
			
			@Override
			public void populateTableRequested() {
				populateTable();
			}
			@Override
			public __continue deleteButtonClicked(InfoBase info) {
				try {
					int rslt = JOptionPane.showConfirmDialog(thisform, "Weet u zeker dat u de holding wilt verwijderen?","",JOptionPane.YES_NO_OPTION);
					if (rslt == JOptionPane.YES_OPTION)
						ServiceCaller.werkgeverFacade(getLoginSession()).deleteHolding((HoldingInfo)info);
					else
						return __continue.dontallow;
				} catch (PermissionException e) {
		        	ExceptionLogger.ProcessException(e,null);
				} catch (ValidationException e) {
					ExceptionLogger.ProcessException(e,null,false);
				} catch (VerzuimApplicationException e) {
		        	ExceptionLogger.ProcessException(e,null);
				} catch (Exception e1){
		        	ExceptionLogger.ProcessException(e1,null);
				}
				return __continue.allow;
			}
			@Override
			public void newCreated(InfoBase info) {
				((HoldingInfo)info).setPostAdres(new AdresInfo());
				((HoldingInfo)info).setVestigingsAdres(new AdresInfo());
				((HoldingInfo)info).setContactpersoon(new ContactpersoonInfo());
			}
		});
		getScrollPane().setBounds(36, 40, 694, 281);
		
		setBounds(100, 100, 758, 390);
		getContentPane().setLayout(null);
		setDetailFormClass(HoldingDetail.class, HoldingInfo.class);
		
		addColumn("Naam",null,150);
		addColumn("Plaats",null, 200);
		addColumn("Start",null,60, Date.class);
		addColumn("Einde",null,60, Date.class);

	}
	protected void cbAfgeslotenTonenClicked(ActionEvent e) {
		displayHoldings(chckbxAfgeslotenTonen.isSelected());
		
	}
	void displayHoldings(boolean showAfgesloten){

		if (holdings != null)
			HoldingInfo.sort(holdings, HoldingInfo.__sortcol.NAAM);
			for(HoldingInfo a :holdings)
			{
				Vector<Object> rowData = new Vector<Object>();
				rowData.add(a.getNaam());
				rowData.add(a.getVestigingsAdres().getPlaats());
				rowData.add(a.getStartdatumcontract());
				rowData.add(a.getEinddatumcontract());
				if (a.isActief())
					addRow(rowData,a);
				else
					if (showAfgesloten)
						addRow(rowData,a,Color.GRAY);
			}
	}
	
	protected void populateTable() {
		try {
			holdings = ServiceCaller.werkgeverFacade(getLoginSession()).getHoldings();
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
		displayHoldings(chckbxAfgeslotenTonen.isSelected());
	}

}
