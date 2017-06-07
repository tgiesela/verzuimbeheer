package com.gieselaar.verzuimbeheer.forms;

import java.awt.Color;
import java.awt.Component;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.swing.JOptionPane;
import javax.swing.RowFilter;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.RowSorter.SortKey;








import com.gieselaar.verzuimbeheer.baseforms.BaseListForm;
//import com.gieselaar.verzuimbeheer.baseforms.BaseListFormOld;
import com.gieselaar.verzuimbeheer.baseforms.DefaultListFormNotification;
import com.gieselaar.verzuimbeheer.baseforms.JDesktopPaneTGI;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.exceptions.PermissionException;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.services.HoldingInfo;
import com.gieselaar.verzuimbeheer.services.InfoBase.persistenceaction;
import com.gieselaar.verzuimbeheer.services.InfoBase;
import com.gieselaar.verzuimbeheer.services.TariefInfo.__tariefperiode;
import com.gieselaar.verzuimbeheer.services.TariefInfo;
import com.gieselaar.verzuimbeheer.services.WerkgeverInfo;
import com.gieselaar.verzuimbeheer.utils.ExceptionLogger;
import com.gieselaar.verzuimbeheer.utils.ServiceCaller;

import javax.swing.JCheckBox;
import javax.swing.table.TableModel;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class TariefOverzicht extends BaseListForm {


	/**
	 * 
	 */
	private static final long serialVersionUID = 9202374053287935929L;
	private List<TariefInfo> tarieven;
	private JCheckBox cbAfgeslotentarieventonen;
	private Component thisform = this;
	private WerkgeverInfo werkgever = null;
	private HoldingInfo holding = null;
	/**
	 * Create the frame.
	 */
	public TariefOverzicht(JDesktopPaneTGI mdiPanel) {
		super("Tarieven overzicht", mdiPanel);
		getPanelAction().setBounds(0, 399, 621, 33);
		
		cbAfgeslotentarieventonen = new JCheckBox("Afgesloten tarieven tonen");
		getPanelAction().add(cbAfgeslotentarieventonen);
		cbAfgeslotentarieventonen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cbUitDienstTonenClicked(e);
			}
		});
		cbAfgeslotentarieventonen.setSelected(false);
				
		getScrollPane().setBounds(36, 40, 730, 348);
		initialize();

		List<RowSorter.SortKey> sortKeys;
		sortKeys = new ArrayList<RowSorter.SortKey>();
        sortKeys.add(new RowSorter.SortKey(0, SortOrder.ASCENDING));
        this.getSorter().setSortKeys(sortKeys);
        this.getSorter().sort();
	}


	protected void cbUitDienstTonenClicked(ActionEvent e) {
		displayTarieven(cbAfgeslotentarieventonen.isSelected());
	}
	protected void cbAlleenOpenVerzuimenClicked(ActionEvent e) {
		displayTarieven(cbAfgeslotentarieventonen.isSelected());
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
					int rslt = JOptionPane.showConfirmDialog(thisform, "Weet u zeker dat u het tarief wilt verwijderen?","",JOptionPane.YES_NO_OPTION);
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
				TariefInfo tarief = (TariefInfo)info;
				tarief.setWerkgeverId(null);
				tarief.setHoldingId(null);
				tarief.setIngangsdatum(new Date());
				tarief.setEinddatum(null);
				tarief.setAansluitkosten(new BigDecimal(0));
				tarief.setAansluitkostenPeriode(__tariefperiode.JAAR);
				tarief.setAbonnement(new BigDecimal(0));
				tarief.setAbonnementPeriode(__tariefperiode.MAAND);
				tarief.setDatumAansluitkosten(new Date());
				tarief.setHuisbezoekTarief(new BigDecimal(0));
				tarief.setHuisbezoekZaterdagTarief(new BigDecimal(0));
				tarief.setKmTarief(new BigDecimal(0));
				tarief.setMaandbedragSecretariaat(new BigDecimal(0));
				tarief.setOmschrijvingFactuur("");
				tarief.setSecretariaatskosten(new BigDecimal(0));
				tarief.setSociaalbezoekTarief(new BigDecimal(0));
				tarief.setSpoedbezoekTarief(new BigDecimal(0));
				tarief.setSpoedbezoekZelfdedagTarief(new BigDecimal(0));
				tarief.setStandaardHuisbezoekTarief(new BigDecimal(0));
				tarief.setTelefonischeControleTarief(new BigDecimal(0));
				tarief.setUurtariefNormaal(new BigDecimal(0));
				tarief.setUurtariefWeekend(new BigDecimal(0));
				tarief.setVasttariefhuisbezoeken(false);
				if (werkgever != null){
					tarief.setWerkgeverId(werkgever.getId());
					/**
					 * zoek het meest recente tarief en neem de gegevens over
					 */
					initializetarief(tarief);
				} else {
					if (holding != null){
						tarief.setHoldingId(holding.getId());
						initializetarief(tarief);
					}
				}
			}
			private void initializetarief(TariefInfo tarief) {
				if (tarieven != null){
					for (TariefInfo t:tarieven){
						if (t.getEinddatum() == null){
							tarief.setAansluitkosten(t.getAansluitkosten());
							tarief.setAansluitkostenPeriode(t.getAansluitkostenPeriode());
							tarief.setAbonnement(t.getAbonnement());
							tarief.setAbonnementPeriode(t.getAbonnementPeriode());
							tarief.setDatumAansluitkosten(t.getDatumAansluitkosten());
							tarief.setHuisbezoekTarief(t.getHuisbezoekTarief());
							tarief.setHuisbezoekZaterdagTarief(t.getHuisbezoekZaterdagTarief());
							tarief.setKmTarief(t.getKmTarief());
							tarief.setMaandbedragSecretariaat(t.getMaandbedragSecretariaat());
							tarief.setOmschrijvingFactuur(t.getOmschrijvingFactuur());
							tarief.setSecretariaatskosten(t.getSecretariaatskosten());
							tarief.setSociaalbezoekTarief(t.getSociaalbezoekTarief());
							tarief.setSpoedbezoekTarief(t.getSpoedbezoekTarief());
							tarief.setSpoedbezoekZelfdedagTarief(t.getSpoedbezoekZelfdedagTarief());
							tarief.setStandaardHuisbezoekTarief(t.getStandaardHuisbezoekTarief());
							tarief.setTelefonischeControleTarief(t.getTelefonischeControleTarief());
							tarief.setUurtariefNormaal(t.getUurtariefNormaal());
							tarief.setUurtariefWeekend(t.getUurtariefWeekend());
							tarief.setVasttariefhuisbezoeken(t.getVasttariefhuisbezoeken());
							break;
						}
					}
				}
			}
			
		});
		setBounds(100, 100, 807, 459);
		setDetailFormClass(TariefDetail.class, TariefInfo.class);
		
		addColumn("Werkgever",null,120, Date.class);
		addColumn("Ingangsdatum",null,80, Date.class);
		addColumn("Einddatum",null,60, Date.class);
		addColumn("Abonnement",null,60, BigDecimal.class);
		addColumn("Periode",null,60);
		addColumn("Aansluitkosten", null,60, BigDecimal.class);
		addColumn("Periode", null,60 );
	}

	private void displayTarieven(boolean showAfgeslotentarieven){
		deleteAllRows();
		List<? extends SortKey> keys = this.getSorter().getSortKeys();
		RowFilter<? super TableModel, ? super Integer> filter = this.getSorter().getRowFilter();
		this.getSorter().setSortKeys(null);
		this.getSorter().setRowFilter(null);
		for(TariefInfo w :tarieven)
		{
			if (w.getAction() == persistenceaction.DELETE)
				;
			else
			{
				Vector<Object> rowData = new Vector<Object>();
				rowData.add(w.getWerkgevernaam());
				rowData.add(w.getIngangsdatum());
				rowData.add(w.getEinddatum());
				rowData.add(w.getAbonnement());
				rowData.add(w.getAbonnementPeriode().toString());
				rowData.add(w.getAansluitkosten());
				rowData.add(w.getAansluitkostenPeriode().toString());
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
		try {
			if (this.getParentInfo() instanceof WerkgeverInfo)
				werkgever = (WerkgeverInfo)this.getParentInfo();
			else
				holding = (HoldingInfo)this.getParentInfo();
			if (werkgever == null && holding == null){
				tarieven = ServiceCaller.werkgeverFacade(getLoginSession()).getTarieven();
			}else{
				if (holding != null){
					tarieven = ServiceCaller.werkgeverFacade(getLoginSession()).getTarievenByHolding(holding.getId());
				}else{
					tarieven = ServiceCaller.werkgeverFacade(getLoginSession()).getTarievenByWerkgever(werkgever.getId());
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
		displayTarieven(cbAfgeslotentarieventonen.isSelected());
	}
}
