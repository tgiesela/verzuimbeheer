package com.gieselaar.verzuimbeheer.forms;

import java.awt.Color;
import java.awt.Component;
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
import com.gieselaar.verzuimbeheer.services.InfoBase.persistenceaction;
import com.gieselaar.verzuimbeheer.services.InfoBase;
import com.gieselaar.verzuimbeheer.services.InfoBase.persistencestate;
import com.gieselaar.verzuimbeheer.services.WerkgeverInfo;
import com.gieselaar.verzuimbeheer.services.WerknemerFastInfo;
import com.gieselaar.verzuimbeheer.services.WerknemerInfo;
import com.gieselaar.verzuimbeheer.utils.ExceptionLogger;
import com.gieselaar.verzuimbeheer.utils.ServiceCaller;

import javax.swing.JCheckBox;
import javax.swing.table.TableModel;

import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.awt.event.ActionEvent;

public class WerknemerOverzicht extends BaseListForm {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9202374053287935929L;
	private List<WerknemerFastInfo> werknemers;
	private JCheckBox cbUitDienstTonen;
	private JCheckBox cbAlleenOpenVerzuimen;
	private Component thisform = this;
	private WerkgeverInfo werkgever = null;

	/**
	 * Create the frame.
	 */
	public WerknemerOverzicht(JDesktopPaneTGI mdiPanel) {
		super("Werknemers overzicht", mdiPanel);
		getPanelAction().setBounds(0, 399, 621, 33);

		cbUitDienstTonen = new JCheckBox("Uit dienst tonen");
		getPanelAction().add(cbUitDienstTonen);
		cbUitDienstTonen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cbUitDienstTonenClicked(e);
			}
		});
		cbUitDienstTonen.setSelected(false);

		cbAlleenOpenVerzuimen = new JCheckBox("Alleen open verzuimen");
		getPanelAction().add(cbAlleenOpenVerzuimen);
		cbAlleenOpenVerzuimen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cbAlleenOpenVerzuimenClicked(e);
			}
		});
		
		getScrollPane().setBounds(36, 40, 937, 348);
		initialize();
	}

	protected void cbUitDienstTonenClicked(ActionEvent e) {
		displayWerknemers(cbUitDienstTonen.isSelected(),
				cbAlleenOpenVerzuimen.isSelected());
	}

	protected void cbAlleenOpenVerzuimenClicked(ActionEvent e) {
		displayWerknemers(cbUitDienstTonen.isSelected(),
				cbAlleenOpenVerzuimen.isSelected());
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
					int rslt;
					rslt = JOptionPane.showConfirmDialog(thisform, "Weet u zeker dat u de werknemer wilt verwijderen?\n"+
							"Hiermee wordt alle informatie definitief verwijderd.","",JOptionPane.YES_NO_OPTION);
					if (rslt == JOptionPane.YES_OPTION){
						WerknemerFastInfo wnrf;
						WerknemerInfo wnr;
						wnrf = (WerknemerFastInfo)info;
						wnr = ServiceCaller.werknemerFacade(getLoginSession()).getWerknemer(wnrf.getId());
						if (wnr.getDienstVerbanden().size() > 1){
							rslt = JOptionPane.showConfirmDialog(thisform, "De werknemer heeft meerdere dienstverbanden.\n"+
									"Weer u zeker dat u alle dienstverbanden wilt verwijderen?","",JOptionPane.YES_NO_OPTION);
							if (rslt != JOptionPane.YES_OPTION){
								return __continue.dontallow;
							}
						}
						if (wnrf.getVzmcnt() > 0){
							rslt = JOptionPane.showConfirmDialog(thisform, "Er zijn meerdere verzuimen.\n"+
									"Weer u zeker dat u alle dienstverbanden wilt verwijderen?","",JOptionPane.YES_NO_OPTION);
							if (rslt != JOptionPane.YES_OPTION){
								return __continue.dontallow;
							}
						}
						if (wnrf.getOpenvzm() > 0){
							JOptionPane.showMessageDialog(thisform, "Er is nog een open verzuim.\n"+
								"verwijderen niet mogelijk","",JOptionPane.OK_OPTION);
							return __continue.dontallow;
						}else{
							wnr.setAction(persistenceaction.DELETE);
							wnr.setState(persistencestate.EXISTS);
							wnr.setId(wnrf.getId());
							ServiceCaller.werknemerFacade(getLoginSession())
									.deleteWerknemer(wnr);
						}
					}else{
						return __continue.dontallow;
					}
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
			public __continue detailButtonClicked(InfoBase info) {
				return __continue.allow;
			}

			@Override
			public void newCreated(InfoBase info) {
				if (werkgever != null) {
					((WerknemerFastInfo) info).setWerkgevernaam(werkgever
							.getNaam());
					((WerknemerFastInfo) info).setWerkgeverid(werkgever.getId());
				}
			}

			@Override
			public __continue filterButtonClicked(TableModel tableModel) {
				String filterText;
				//int colcount;
				// Column 11, 12, 13 bevatten telefoonnummer
				/*
				colcount = tableModel.getColumnCount();
				if (colcount != 13)
					throw new RuntimeException(
							"Logic Error: nr of columns in filtertable has changed");
				filterText = (String) tableModel.getValueAt(0, 10);
				if (filterText == null)
					filterText = (String) tableModel.getValueAt(0, 11);
				if (filterText == null)
					filterText = (String) tableModel.getValueAt(0, 12);
				if (filterText != null) {
					tableModel.setValueAt(filterText, 0, 10);
					tableModel.setValueAt(filterText, 0, 11);
					tableModel.setValueAt(filterText, 0, 12);
				}*/
				return __continue.allow;
			}

		});
		setBounds(100, 100, 999, 459);
		setDetailFormClass(WerknemerDetail.class, WerknemerFastInfo.class);

		addColumn("BSN", null, 60);
		addColumn("Werkgever", null, 100);
		addColumn("In dienst", null, 60, Date.class);
		addColumn("Uit dienst", null, 60, Date.class);
		addColumn("Achternaam", null, 120);
		addColumn("Voorl.", null, 40);
		addColumn("Geb. datum", null, 70, Date.class);
		addColumn("Gesl.", null, 40);
		addColumn("Pnr", null, 40);
		addColumn("Uren", null, 40, BigDecimal.class);
		// Let op wijzigen van volgorde hier heeft gevolgen voor event
		// filterButtonClickec
		addColumn("Mobiel", null, 70);
		addColumn("Telefoonprive", null, 70);
		addColumn("Afdelingnaam", null, 145);
	}

	private void displayWerknemers(boolean showUitDienst,
			boolean alleenOpenVerzuimen) {

		boolean openverzuimfound;

		deleteAllRows();
		List<? extends SortKey> keys = this.getSorter().getSortKeys();
		RowFilter<? super TableModel, ? super Integer> filter = this
				.getSorter().getRowFilter();
		this.getSorter().setSortKeys(null);
		this.getSorter().setRowFilter(null);
		WerknemerFastInfo.sort(werknemers,WerknemerFastInfo.__sortcol.WERKGEVERNAAM);
		for (WerknemerFastInfo w : werknemers) {
			if (w.getAction() == persistenceaction.DELETE)
				;
			else {
				Vector<Object> rowData = new Vector<Object>();
				rowData.add(w.getBurgerservicenummer());
				rowData.add(w.getWerkgevernaam());
				if (w.getVzmcnt() > 0)
					openverzuimfound = w.getOpenvzm() > 0;
				else
					openverzuimfound = false;
				rowData.add(w.getStartdatumcontract());
				rowData.add(w.getEinddatumcontract());
				if (w.getVoorvoegsel() == null || w.getVoorvoegsel().isEmpty())
					rowData.add(w.getAchternaam());
				else
					rowData.add(w.getAchternaam() + ", " + w.getVoorvoegsel());
				rowData.add(w.getVoorletters());
				rowData.add(w.getGeboortedatum());
				rowData.add(w.getGeslacht().toString());
				rowData.add(w.getPersoneelsnummer());
				rowData.add(w.getWerkweek());
				rowData.add(w.getMobiel());
				rowData.add(w.getTelefoonPrive());
				rowData.add(w.getAfdelingnaam());
				if (!w.isActief()) {
					if (showUitDienst)
						addRow(rowData, w, Color.GRAY);
				} else {
					if (openverzuimfound)
						addRow(rowData, w, Color.ORANGE);
					else if (alleenOpenVerzuimen)
						;
					else
						addRow(rowData, w);
				}
			}
		}
		this.getSorter().setRowFilter(filter);
		this.getSorter().setSortKeys(keys);

	}

	public void populateTable() {
		werkgever = (WerkgeverInfo) this.getParentInfo();
		try {
			if (werkgever == null) {
				werknemers = ServiceCaller.werknemerFacade(getLoginSession())
						.allWerknemers();
			} else
				werknemers = ServiceCaller.werknemerFacade(getLoginSession())
						.getWerknemersByWerkgever(werkgever.getId());
		} catch (PermissionException e) {
			ExceptionLogger.ProcessException(e, this);
			return;
		} catch (VerzuimApplicationException e2) {
			ExceptionLogger.ProcessException(e2, this);
			return;
		} catch (Exception e1) {
			ExceptionLogger.ProcessException(e1, this);
			return;
		}
		displayWerknemers(cbUitDienstTonen.isSelected(),
				cbAlleenOpenVerzuimen.isSelected());
	}
}
