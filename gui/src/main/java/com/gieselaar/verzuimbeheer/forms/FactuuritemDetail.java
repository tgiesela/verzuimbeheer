package com.gieselaar.verzuimbeheer.forms;

import javax.swing.JOptionPane;

import com.gieselaar.verzuimbeheer.baseforms.BaseDetailform;
import com.gieselaar.verzuimbeheer.baseforms.JDesktopPaneTGI;
import com.gieselaar.verzuimbeheer.baseforms.WerkgeverNotification;
import com.gieselaar.verzuimbeheer.components.JTextAreaTGI;
import com.gieselaar.verzuimbeheer.components.WerkgeverSelection;
import com.gieselaar.verzuimbeheer.components.WerkgeverSelection.__WerkgeverFilter;
import com.gieselaar.verzuimbeheer.components.WerkgeverSelection.__WerkgeverSelectionMode;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.exceptions.PermissionException;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.services.FactuurcategorieInfo;
import com.gieselaar.verzuimbeheer.services.FactuuritemInfo;
import com.gieselaar.verzuimbeheer.services.FactuurkopInfo;
import com.gieselaar.verzuimbeheer.services.HoldingInfo;
import com.gieselaar.verzuimbeheer.services.InfoBase;
import com.gieselaar.verzuimbeheer.services.WerkgeverInfo;
import com.gieselaar.verzuimbeheer.utils.ExceptionLogger;
import com.gieselaar.verzuimbeheer.utils.ServiceCaller;
import com.gieselaar.verzuimbeheer.utils.ServiceLocatorException;
import com.gieselaar.verzuimbeheer.utils.TypeEntry;
import com.michaelbaranov.microba.calendar.DatePicker;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import java.beans.PropertyVetoException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.List;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class FactuuritemDetail extends BaseDetailform {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private FactuuritemInfo factuuritem;
	private WerkgeverInfo werkgever;

	private DatePicker dtpDatum;
	private JComboBox<TypeEntry> cmbFactuurkoppen;
	private JComboBox<TypeEntry> cmbFactuurcategorien;
	private List<WerkgeverInfo> werkgevers;
	private List<HoldingInfo> holdings;
	private List<FactuurkopInfo> factuurkoppen;
	private List<FactuurcategorieInfo> factuurcategorien;
	private JFormattedTextField txtBedrag;
	private JTextAreaTGI taOmschrijving;

	private WerkgeverSelection werkgeverSelection;
	private BaseDetailform thisform = this;

	/**
	 * Create the frame.
	 */
	public FactuuritemDetail(JDesktopPaneTGI mdiPanel) {
		super("Beheer losse factuuritems", mdiPanel);
		initialize();
	}

	private void initialize() {
		setBounds(0, 237, 527, 354);
		getContentPane().setLayout(null);

		NumberFormat amountFormat = NumberFormat.getNumberInstance();
		amountFormat.setMaximumFractionDigits(2);
		amountFormat.setMinimumFractionDigits(2);

		dtpDatum = new DatePicker();
		dtpDatum.setBounds(98, 77, 97, 21);
		dtpDatum.setDateFormat(new SimpleDateFormat("dd-MM-yyyy"));
		getContentPane().add(dtpDatum);

		JLabel lblIngangsdatum = new JLabel("Datum");
		lblIngangsdatum.setBounds(10, 77, 86, 14);
		getContentPane().add(lblIngangsdatum);

		cmbFactuurkoppen = new JComboBox<>();
		cmbFactuurkoppen.setBounds(98, 100, 238, 20);
		cmbFactuurkoppen.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				cmbFactuurkoppenClicked(e);

			}
		});
		getContentPane().add(cmbFactuurkoppen);

		JLabel lblActiviteit = new JLabel("Factuurgroep");
		lblActiviteit.setBounds(10, 100, 65, 14);
		getContentPane().add(lblActiviteit);

		cmbFactuurcategorien = new JComboBox<>();
		cmbFactuurcategorien.setBounds(98, 123, 238, 20);
		getContentPane().add(cmbFactuurcategorien);

		JLabel lblSoortHuisbezoek = new JLabel("Factuurcategorie");
		lblSoortHuisbezoek.setBounds(10, 123, 105, 14);
		getContentPane().add(lblSoortHuisbezoek);

		txtBedrag = new JFormattedTextField(amountFormat);
		txtBedrag.setBounds(98, 146, 76, 20);
		getContentPane().add(txtBedrag);
		txtBedrag.setColumns(10);

		JLabel lblBedrag = new JLabel("Bedrag");
		lblBedrag.setBounds(10, 146, 95, 14);
		getContentPane().add(lblBedrag);

		JLabel lblOmschrijving = new JLabel("Omschrijving");
		lblOmschrijving.setBounds(10, 166, 95, 14);
		getContentPane().add(lblOmschrijving);

		taOmschrijving = new JTextAreaTGI();
		taOmschrijving.setBounds(98, 169, 337, 117);
		taOmschrijving.setFont(cmbFactuurcategorien.getFont());
		getContentPane().add(taOmschrijving);
		
		werkgeverSelection = new WerkgeverSelection(
				__WerkgeverSelectionMode.SelectBoth,
				__WerkgeverFilter.Factureren);
		werkgeverSelection.setBounds(0, 11, 374, 56);
		getContentPane().add(werkgeverSelection);
		werkgeverSelection.setEventNotifier(new WerkgeverNotification() {

			@Override
			public boolean werkgeverSelected(Integer werkgeverid) {
				factuuritem.setWerkgeverid(werkgeverid);
				try {
					werkgever = ServiceCaller
							.werkgeverFacade(getLoginSession()).getWerkgever(
									werkgeverid);
				} catch (ServiceLocatorException e) {
					ExceptionLogger.ProcessException(e, thisform,
							"Unable to connect to server");
					return false;
				} catch (PermissionException e) {
					ExceptionLogger.ProcessException(e, thisform);
					return false;
				} catch (VerzuimApplicationException e) {
					ExceptionLogger.ProcessException(e, thisform);
					return false;
				} catch (Exception e1) {
					ExceptionLogger.ProcessException(e1, thisform);
					return false;
				}

				EnableDisableFields();
				return false;
			}

			@Override
			public boolean holdingSelected(Integer holdingid) {
				factuuritem.setHoldingid(holdingid);
				EnableDisableFields();
				return false;
			}
		});
	}

	protected void cmbFactuurkoppenClicked(ActionEvent e) {
		EnableDisableFields();
		TypeEntry type = (TypeEntry) cmbFactuurkoppen.getSelectedItem();
		DefaultComboBoxModel<TypeEntry> factuurcatgorienModel = new DefaultComboBoxModel<TypeEntry>();
		cmbFactuurcategorien.setModel(factuurcatgorienModel);
		if (type != null) {
			for (FactuurkopInfo fk : factuurkoppen) {
				if (fk.getId().equals(type.getValue())) {
					for (FactuurcategorieInfo g : factuurcategorien) {
						if (g.getFactuurkopid().equals(fk.getId())) {
							TypeEntry gt = new TypeEntry(g.getId(),
									g.getOmschrijving());
							factuurcatgorienModel.addElement(gt);
							if (gt.getValue() == factuuritem.getFactuurcategorieid()){
								cmbFactuurcategorien.setSelectedItem(gt);
							}
						}
					}
					break;
				}
			}
		}
	}

	public void setInfo(InfoBase info) {
		try {
			factuuritem = (FactuuritemInfo) info;
			werkgevers = ServiceCaller.werkgeverFacade(getLoginSession())
					.allWerkgeversList();
			holdings = ServiceCaller.werkgeverFacade(getLoginSession())
					.getHoldings();
			factuurkoppen = ServiceCaller.factuurFacade(getLoginSession())
					.getFactuurkoppen();
			factuurcategorien = ServiceCaller.factuurFacade(getLoginSession())
					.getFactuurcategorien();
		} catch (ServiceLocatorException e) {
			ExceptionLogger.ProcessException(e, this,
					"Unable to connect to server");
			return;
		} catch (PermissionException e) {
			ExceptionLogger.ProcessException(e, this);
			return;
		} catch (VerzuimApplicationException e) {
			ExceptionLogger.ProcessException(e, this);
			return;
		} catch (Exception e1) {
			ExceptionLogger.ProcessException(e1, this);
			return;
		}

		werkgeverSelection.setWerkgevers(werkgevers);
		werkgeverSelection.setHoldings(holdings);
		
		DefaultComboBoxModel<TypeEntry> factuurkoppenModel = new DefaultComboBoxModel<TypeEntry>();
		for (FactuurkopInfo g : factuurkoppen) {
			TypeEntry gt = new TypeEntry(g.getId(), g.getOmschrijving());
			factuurkoppenModel.addElement(gt);
		}
		cmbFactuurkoppen.setModel(factuurkoppenModel);

		displayFactuuritem();
		activateListener();
	}

	private void EnableDisableFields() {
	}

	private void displayFactuuritem() {
		try {
			dtpDatum.setDate(factuuritem.getDatum());

			werkgeverSelection.setHoldingId(factuuritem.getHoldingid());
			werkgeverSelection.setWerkgeverId(factuuritem.getWerkgeverid());
			
			for (int i = 0; i < cmbFactuurcategorien.getModel().getSize(); i++) {
				TypeEntry soort = (TypeEntry) cmbFactuurcategorien.getModel()
						.getElementAt(i);
				if (soort.getValue() == factuuritem.getFactuurcategorieid()) {
					cmbFactuurcategorien.getModel().setSelectedItem(soort);
					break;
				}
			}
			cmbFactuurkoppenClicked(null);

			for (FactuurcategorieInfo fc : factuurcategorien) {
				if (fc.getId().equals(factuuritem.getFactuurcategorieid())) {
					for (int j = 0; j < cmbFactuurkoppen.getModel().getSize(); j++) {
						TypeEntry type = (TypeEntry) cmbFactuurkoppen
								.getModel().getElementAt(j);
						if (type.getValue() == fc.getFactuurkopid()) {
							cmbFactuurkoppen.getModel().setSelectedItem(type);
							break;
						}
					}
				}
			}

			taOmschrijving.setText(factuuritem.getOmschrijving());
			txtBedrag.setValue(factuuritem.getBedrag());

			EnableDisableFields();

		} catch (PropertyVetoException e) {
			e.printStackTrace();
			ExceptionLogger.ProcessException(e, this);
		}
	}

	private BigDecimal formatBigDecimal(DecimalFormat format, String value)
			throws ValidationException {
		BigDecimal bedrag;
		ParsePosition pos = new ParsePosition(0);
		bedrag = (BigDecimal) format.parse(value, pos);
		if (pos.getIndex() < value.length()) {
			throw new ValidationException("Ongeldige tekens in bedrag");
		}
		return bedrag;
	}

	protected void okButtonClicked(ActionEvent e) {
		DecimalFormat format = new DecimalFormat("##0,00");
		format.setParseBigDecimal(true);

		if (factuuritem.getWerkgeverid() == -1) {
			JOptionPane.showMessageDialog(this, "Geen werkgever geselecteerd.");
			return;
		}

		try {

			factuuritem.setDatum(dtpDatum.getDate());
			factuuritem.setBedrag(formatBigDecimal(format, txtBedrag.getText()));

			if (factuuritem.getHoldingid() == null){
				if (werkgeverSelection.getHolding() != null){
					factuuritem.setHoldingid(werkgeverSelection.getHolding().getId());
				}
			}
			if (factuuritem.getWerkgeverid() == null || factuuritem.getWerkgeverid().equals(-1)){
				throw new ValidationException("Geen werkgever geselecteerd");
			}
			TypeEntry categorie = (TypeEntry) cmbFactuurcategorien
					.getSelectedItem();
			if (categorie == null){
				throw new ValidationException("Geen categorie geselecteerd");
			}
			factuuritem.setFactuurcategorieid(categorie.getValue());
			factuuritem.setOmschrijving(taOmschrijving.getText());
			if (this.getMode() == formMode.New){
				factuuritem.setUserid(this.getLoginSession().getGebruiker().getId());
			}

		} catch (ValidationException e1) {
			ExceptionLogger.ProcessException(e1, this);
			return;
		}
		if (this.getLoginSession() != null) {
			try {
				factuuritem.validate();
				switch (this.getMode()) {
				case New:
					ServiceCaller.factuurFacade(getLoginSession())
							.addFactuuritem(factuuritem);
					break;
				case Update:
					ServiceCaller.factuurFacade(getLoginSession())
							.updateFactuuritem(factuuritem);
					break;
				case Delete:
					ServiceCaller.factuurFacade(getLoginSession())
							.deleteFactuuritem(factuuritem);
					break;
				}
				if (this.getMode() != formMode.New){
					 super.okButtonClicked(e);
				}else{
					FactuuritemInfo newfii = new FactuuritemInfo();
					newfii.setWerkgeverid(factuuritem.getWerkgeverid());
					newfii.setDatum(factuuritem.getDatum());
					newfii.setHoldingid(factuuritem.getHoldingid());
					newfii.setFactuurcategorieid(factuuritem.getFactuurcategorieid());
					newfii.setBedrag(BigDecimal.ZERO);
					this.setInfo(newfii);
				}
			} catch (ValidationException e1) {
				ExceptionLogger.ProcessException(e1, this, false);
			} catch (PermissionException e1) {
				ExceptionLogger.ProcessException(e1, this);
			} catch (VerzuimApplicationException e1) {
				ExceptionLogger.ProcessException(e1, this);
			} catch (Exception e1) {
				ExceptionLogger.ProcessException(e1, this);
			}
		} else
			JOptionPane.showMessageDialog(this,
					"Logic error: loginSession is null");

	}
	protected void cancelButtonClicked(ActionEvent e) {
		/*
		 * We beschouwen cancel hier al ok, zodat het werkzaamhedenoverzicht
		 * weer wordt ververst.
		 */
		super.okButtonClicked(e);
	}
	
}
