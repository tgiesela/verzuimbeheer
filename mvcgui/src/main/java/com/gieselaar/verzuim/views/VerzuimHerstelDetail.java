package com.gieselaar.verzuim.views;

import java.beans.PropertyVetoException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.List;

import com.gieselaar.verzuim.components.JTextAreaTGI;
import com.gieselaar.verzuim.components.JTextFieldTGI;
import com.gieselaar.verzuim.controllers.AbstractController;
import com.gieselaar.verzuim.controllers.VerzuimherstellenController;
import com.gieselaar.verzuim.utils.ExceptionLogger;
import com.gieselaar.verzuim.viewsutils.VerzuimComboBoxModel;
import com.gieselaar.verzuimbeheer.services.GebruikerInfo;
import com.gieselaar.verzuimbeheer.services.InfoBase;
import com.gieselaar.verzuimbeheer.services.VerzuimHerstelInfo;
import com.gieselaar.verzuimbeheer.services.VerzuimInfo.__meldingswijze;
import com.gieselaar.verzuimbeheer.utils.TypeEntry;
import com.michaelbaranov.microba.calendar.DatePicker;

import javax.swing.JOptionPane;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JComboBox;

public class VerzuimHerstelDetail extends AbstractDetail {

	private static final long serialVersionUID = 1L;
	private JTextFieldTGI txtHerstel;
	private JTextFieldTGI txtHerstelAT;
	private DatePicker dtpDatumherstel;
	private DatePicker dtpMeldingsdatum;
	private JTextAreaTGI taOpmerkingen;
	private VerzuimHerstelInfo verzuimherstel;
	private List<GebruikerInfo> gebruikers;
	private JComboBox<TypeEntry> cmbMeldingswijze;
	private JTextFieldTGI txtGebruiker;
	
	private VerzuimherstellenController verzuimherstelcontroller; 
	public VerzuimHerstelDetail(AbstractController controller) {
		super("Verzuim herstel", controller);
		verzuimherstelcontroller = (VerzuimherstellenController) controller;
		initialize();
	}
	public void setData(InfoBase info){
		verzuimherstel = (VerzuimHerstelInfo)info;
		displayHerstel();
	}
	private void displayHerstel(){	
		VerzuimComboBoxModel meldingswijzeModel;
		NumberFormat nf = NumberFormat.getNumberInstance();
		DecimalFormat df = (DecimalFormat)nf;
		df.applyPattern("##0.00");
		
		gebruikers = verzuimherstelcontroller.getMaincontroller().getGebruikers();

		meldingswijzeModel = verzuimherstelcontroller.getMaincontroller().getEnumModel(__meldingswijze.class);
        cmbMeldingswijze.setModel(meldingswijzeModel);
        if (verzuimherstel.getMeldingswijze() != null){
        	((VerzuimComboBoxModel)cmbMeldingswijze.getModel()).setId(verzuimherstel.getMeldingswijze().getValue());
        }

        try {
			dtpDatumherstel.setDate(verzuimherstel.getDatumHerstel());
			dtpMeldingsdatum.setDate(verzuimherstel.getMeldingsdatum());
			txtHerstel.setText(df.format(verzuimherstel.getPercentageHerstel()));
			txtHerstelAT.setText(df.format(verzuimherstel.getPercentageHerstelAT()));
		} catch (PropertyVetoException e) {
			ExceptionLogger.ProcessException(e,this);		
		}

        for (GebruikerInfo gi: gebruikers){
			if (gi.getId().equals(verzuimherstel.getUser())){
				txtGebruiker.setText(gi.getAchternaam());
				break;
			}
        }
        taOpmerkingen.setText(verzuimherstel.getOpmerkingen());
	}
	void initialize(){
		setBounds(100, 100, 450, 368);
		dtpDatumherstel = new DatePicker();
		dtpDatumherstel.setBounds(144, 36, 83, 21);
		dtpDatumherstel.setDateFormat(new SimpleDateFormat("dd-MM-yyyy"));
		getContentPane().add(dtpDatumherstel);
		
		dtpMeldingsdatum = new DatePicker();
		dtpMeldingsdatum.setBounds(144, 59, 83, 21);
		dtpMeldingsdatum.setDateFormat(new SimpleDateFormat("dd-MM-yyyy"));
		getContentPane().add(dtpMeldingsdatum);
		
		txtHerstel = new JTextFieldTGI();
		txtHerstel.setBounds(144, 82, 83, 20);
		getContentPane().add(txtHerstel);
		txtHerstel.setColumns(10);
		
		txtHerstelAT = new JTextFieldTGI();
		txtHerstelAT.setBounds(144, 105, 83, 20);
		getContentPane().add(txtHerstelAT);
		txtHerstelAT.setColumns(10);
		
		JLabel lblHersteldatum = new JLabel("Hersteldatum");
		lblHersteldatum.setBounds(10, 39, 69, 14);
		getContentPane().add(lblHersteldatum);
		
		JLabel lblMeldingsdatum = new JLabel("Meldingsdatum");
		lblMeldingsdatum.setBounds(10, 62, 95, 14);
		getContentPane().add(lblMeldingsdatum);
		
		JLabel lblPercentageHerstel = new JLabel("Percentage herstel");
		lblPercentageHerstel.setBounds(10, 85, 95, 14);
		getContentPane().add(lblPercentageHerstel);
		
		JLabel lblPercentageHerstelAt = new JLabel("Percentage herstel AT");
		lblPercentageHerstelAt.setBounds(10, 108, 121, 14);
		getContentPane().add(lblPercentageHerstelAt);
		
		JLabel lblOpmerkingen = new JLabel("Opmerkingen");
		lblOpmerkingen.setBounds(10, 164, 69, 14);
		getContentPane().add(lblOpmerkingen);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(144, 164, 280, 133);
		getContentPane().add(scrollPane);
		
		taOpmerkingen = new JTextAreaTGI();
		scrollPane.setViewportView(taOpmerkingen);
		
		JLabel lblUitgevoerdDoor = new JLabel("Ingevoerd door");
		lblUitgevoerdDoor.setBounds(10, 131, 95, 14);
		getContentPane().add(lblUitgevoerdDoor);
		
		txtGebruiker = new JTextFieldTGI();
		txtGebruiker.setEditable(false);
		txtGebruiker.setEnabled(false);
		txtGebruiker.setBounds(144, 128, 134, 20);
		getContentPane().add(txtGebruiker);
		txtGebruiker.setColumns(10);
		
		JLabel lblNewLabel = new JLabel("via:");
		lblNewLabel.setBounds(237, 62, 31, 14);
		getContentPane().add(lblNewLabel);
		
		cmbMeldingswijze = new JComboBox<TypeEntry>();
		cmbMeldingswijze.setBounds(264, 59, 127, 20);
		getContentPane().add(cmbMeldingswijze);
	}
/*
	protected void okButtonClicked() {

        try {
        	if (this.getFormmode() == __formmode.NEW){
        		verzuimherstelcontroller.addData(verzuimherstel, cleanupTodo);
        	}else{
        		verzuimherstelcontroller.updateData(verzuimherstel, cleanupTodo);
        	}
		    controller.closeView(this);
        }catch (VerzuimApplicationException e1) {
			ExceptionLogger.ProcessException(e1, this);
		}
	}
*/	
	@Override
	public InfoBase collectData() {
		ParsePosition pos = new ParsePosition(0);
		DecimalFormat format = new DecimalFormat("##0,00");
		BigDecimal percentage;
		format.setParseBigDecimal(true);
		
		verzuimherstel.setDatumHerstel(dtpDatumherstel.getDate());
		verzuimherstel.setMeldingsdatum(dtpMeldingsdatum.getDate());
		verzuimherstel.setOpmerkingen(taOpmerkingen.getText());
		int meldingswijze = ((VerzuimComboBoxModel)cmbMeldingswijze.getModel()).getId();
		verzuimherstel.setMeldingswijze(__meldingswijze.parse(meldingswijze));

		percentage = (BigDecimal)format.parse(txtHerstel.getText(),pos);
		if (pos.getIndex() < txtHerstel.getText().length())
		{
			JOptionPane.showMessageDialog(this, "Herstel percentage bevat ongeldige tekens");
			return null;
		}
		verzuimherstel.setPercentageHerstel(percentage);
		
		pos.setIndex(0);
		percentage = (BigDecimal)format.parse(txtHerstelAT.getText(),pos);
		if (pos.getIndex() < txtHerstelAT.getText().length())
		{
			JOptionPane.showMessageDialog(this, "Herstel percentage AT bevat ongeldige tekens");
			return null;
		}
		verzuimherstel.setPercentageHerstelAT(percentage);

		return verzuimherstel;
	}
}
