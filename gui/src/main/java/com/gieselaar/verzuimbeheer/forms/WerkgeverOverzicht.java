package com.gieselaar.verzuimbeheer.forms;


import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import java.awt.Color;
import java.awt.Component;

import com.gieselaar.verzuimbeheer.baseforms.BaseListForm;
import com.gieselaar.verzuimbeheer.baseforms.DefaultListFormNotification;
import com.gieselaar.verzuimbeheer.baseforms.JDesktopPaneTGI;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.exceptions.PermissionException;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.services.AdresInfo;
import com.gieselaar.verzuimbeheer.services.HoldingInfo;
import com.gieselaar.verzuimbeheer.services.InfoBase;
import com.gieselaar.verzuimbeheer.services.PakketInfo;
import com.gieselaar.verzuimbeheer.services.WerkgeverInfo;
import com.gieselaar.verzuimbeheer.utils.CursorController;
import com.gieselaar.verzuimbeheer.utils.ExceptionLogger;
import com.gieselaar.verzuimbeheer.utils.ServiceCaller;
import com.gieselaar.verzuimbeheer.utils.ServiceLocatorException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JCheckBox;

public class WerkgeverOverzicht extends BaseListForm{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7903414736706846177L;

	private ArrayList<WerkgeverInfo> werkgevers = null;
	private HashMap<Integer,String> holdingmap = null;
	private Component thisform = this;
	private JButton btnWerknemers; 
	private WerkgeverInfo werkgever = null;
	private JCheckBox chckbxAfgeslotenTonen = null;
	public WerkgeverOverzicht(JDesktopPaneTGI mdiPanel) {
		super("Overzicht werkgevers", mdiPanel);
		getPanelAction().setBounds(0, 400, 540, 33);
	
		btnWerknemers = new JButton("Werknemers...");
		getPanelAction().add(btnWerknemers);
		
		btnWerknemers.addActionListener(CursorController.createListener(this, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				btnWerknemersClicked();
			}
		}));
		initialize();
	}
	public void initialize(){
		getScrollPane().setBounds(36, 40, 670, 349);
		
		chckbxAfgeslotenTonen = new JCheckBox("Afgesloten tonen");
		chckbxAfgeslotenTonen.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				cbAfgeslotenTonenClicked();
			}
		});
		chckbxAfgeslotenTonen.setBounds(380, 405, 120, 23);
		getPanelAction().add(chckbxAfgeslotenTonen);
		this.setEventNotifier(new DefaultListFormNotification() {
			
			@Override
			public void populateTableRequested() {
				populateTable();
			}
			@Override
			public __continue deleteButtonClicked(InfoBase info) {
				try {
					int rslt = JOptionPane.showConfirmDialog(thisform, "Weet u zeker dat u de werkgever wilt verwijderen?","",JOptionPane.YES_NO_OPTION);
					if (rslt == JOptionPane.YES_OPTION)
						ServiceCaller.werkgeverFacade(getLoginSession()).deleteWerkgever((WerkgeverInfo)info);
					else
						return __continue.dontallow;
				} catch (ValidationException e) {
					ExceptionLogger.ProcessException(e,thisform,false);
				} catch (PermissionException | ServiceLocatorException | VerzuimApplicationException e) {
					ExceptionLogger.ProcessException(e,thisform);
				}
				return __continue.allow;
			}
			@Override
			public void rowSelected(InfoBase info) {
				werkgever = (WerkgeverInfo)info;
				enableButtons(true);
			}
			@Override
			public void newCreated(InfoBase info) {
				werkgever = (WerkgeverInfo)info;
				werkgever.setWerkweek(new BigDecimal(40));
				werkgever.setPakketten(new ArrayList<PakketInfo>());
			}
		});
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 751, 460);
		setDetailFormClass(WerkgeverDetail.class, WerkgeverInfo.class);
		
		addColumn("Naam", null, 150);
		addColumn("Holding", null, 150);
		addColumn("PC", null, 50);
		addColumn("Plaats",null,90);
		addColumn("Start",null,60, Date.class);
		addColumn("Einde",null,60, Date.class);
		
		enableButtons(false);
	}
	protected void cbAfgeslotenTonenClicked() {
		displayWerkgevers(chckbxAfgeslotenTonen.isSelected());
		
	}
	protected void btnWerknemersClicked() {
		JDesktopPane mdiPanel = this.getDesktopPane();
		WerknemerOverzicht dlgWerknemers = new WerknemerOverzicht(this.getMdiPanel());
		dlgWerknemers.setLoginSession(this.getLoginSession());
		dlgWerknemers.setParentInfo(werkgever);
		dlgWerknemers.ReloadTable();
		dlgWerknemers.setVisible(true);
		mdiPanel.add(dlgWerknemers);
		mdiPanel.setOpaque(true);
		mdiPanel.moveToFront(dlgWerknemers);
	}
	private void enableButtons(boolean rowSelected){
		if (rowSelected){
			btnWerknemers.setEnabled(true);
		}
		else {
			btnWerknemers.setEnabled(false);
		}
	}
	private void displayWerkgevers(boolean showAfgesloten){
		AdresInfo adr;
		enableButtons(false);
		deleteAllRows();
		
		WerkgeverInfo.sort(werkgevers, WerkgeverInfo.__sortcol.NAAM);
		
		for(WerkgeverInfo w :werkgevers)
		{
			Vector<Object> rowData = new Vector<>();
			rowData.add(w.getNaam());
			if (w.getHoldingId() == null){
				rowData.add(""); 
			}else{
			    rowData.add(holdingmap.get(w.getHoldingId()));
			}
			adr = w.getVestigingsAdres();
			if (adr == null)
			{
				rowData.add("");
				rowData.add("");
			}
			else
			{
				rowData.add(w.getVestigingsAdres().getPostcode());
				rowData.add(w.getVestigingsAdres().getPlaats());
			}
			rowData.add(w.getStartdatumcontract());
			rowData.add(w.getEinddatumcontract());
			if (w.isActief())
				addRow(rowData,w);
			else
				if (showAfgesloten)
					addRow(rowData,w,Color.GRAY);
		}
		enableButtons(false);
	}

	public void populateTable() {
		HoldingInfo holding;
		List<HoldingInfo> holdings = null;
		holding = (HoldingInfo) this.getParentInfo();
		try {
			if (holding == null){
				werkgevers = (ArrayList<WerkgeverInfo>) ServiceCaller.werkgeverFacade(getLoginSession()).allWerkgevers();
			}else{
				werkgevers = (ArrayList<WerkgeverInfo>) ServiceCaller.werkgeverFacade(getLoginSession()).getWerkgeversHolding(holding.getId());
			}
			holdings = ServiceCaller.werkgeverFacade(getLoginSession()).getHoldings();
			holdingmap = new HashMap<>();
			if (holdings != null){
				for (HoldingInfo hdg:holdings){
					holdingmap.put(hdg.getId(), hdg.getNaam());
				}
			}
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
		
		displayWerkgevers(chckbxAfgeslotenTonen.isSelected());
	}
}
