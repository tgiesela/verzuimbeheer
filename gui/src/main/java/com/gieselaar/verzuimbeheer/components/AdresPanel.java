package com.gieselaar.verzuimbeheer.components;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import com.gieselaar.verzuimbeheer.components.JTextFieldTGI;
import com.gieselaar.verzuimbeheer.services.AdresInfo;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
public class AdresPanel extends JPanel{

	private static final long serialVersionUID = 4264160456357805052L;
	private JTextFieldTGI txtStraat;
	private JTextFieldTGI txtPlaats;
	private JTextFieldTGI txtHuisnr;
	private JTextFieldTGI txtHuisnrToevoeging;
	private JTextFieldTGI txtLand;
	private JTextFieldTGI txtPostcode;
	private JPanel panelAdres;
	private AdresInfo adres = null;
	public AdresPanel() {
		super();
		initialize();
	}
	public void initialize(){
		setLayout(null);
		panelAdres = new JPanel();
		panelAdres.setBorder(new TitledBorder(null, "Woonadres", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panelAdres.setBounds(0, 0, 403, 90);
		this.add(panelAdres);
		panelAdres.setLayout(null);

		JLabel lblStraatVA = new JLabel("Straat");
		lblStraatVA.setBounds(17, 19, 46, 14);
		panelAdres.add(lblStraatVA);

		txtStraat = new JTextFieldTGI();
		txtStraat.setBounds(103, 16, 152, 20);
		panelAdres.add(txtStraat);
		txtStraat.addFocusListener(new FocusListener() {
			
			@Override
			public void focusLost(FocusEvent e) {
		        if (txtHuisnr.getText().isEmpty() && txtHuisnrToevoeging.getText().isEmpty()){
		        	AdresInfo.extractHuisnummerfromStraat(txtStraat.getText(),adres);
		        	txtStraat.setText(adres.getStraat());
		        	txtHuisnr.setText(adres.getHuisnummer());
		        	txtHuisnrToevoeging.setText(adres.getHuisnummertoevoeging());
		        }
			}
			
			@Override
			public void focusGained(FocusEvent e) {
			}
		});
			
		JLabel lblPostcodeVA = new JLabel("Postcode");
		lblPostcodeVA.setBounds(17, 42, 87, 14);
		panelAdres.add(lblPostcodeVA);

		txtPlaats = new JTextFieldTGI();
		txtPlaats.setBounds(206, 39, 181, 20);
		panelAdres.add(txtPlaats);

		txtHuisnr = new JTextFieldTGI();
		txtHuisnr.setColumns(10);
		txtHuisnr.setBounds(279, 16, 46, 20);

		panelAdres.add(txtHuisnr);
		JLabel lblNrVA = new JLabel("Nr");
		lblNrVA.setBounds(260, 19, 17, 14);
		panelAdres.add(lblNrVA);

		txtHuisnrToevoeging = new JTextFieldTGI();
		txtHuisnrToevoeging.setColumns(10);
		txtHuisnrToevoeging.setBounds(328, 16, 28, 20);
		panelAdres.add(txtHuisnrToevoeging);

		txtLand = new JTextFieldTGI();
		txtLand.setColumns(10);
		txtLand.setBounds(103, 62, 152, 20);
		panelAdres.add(txtLand);

		JLabel lblLandVA = new JLabel("Land");
		lblLandVA.setBounds(17, 65, 46, 14);
		panelAdres.add(lblLandVA);

		txtPostcode = new JTextFieldTGI();
		txtPostcode.setColumns(10);
		txtPostcode.setBounds(103, 39, 53, 20);
		panelAdres.add(txtPostcode);

		JLabel lblPlaatsVA = new JLabel("Plaats");
		lblPlaatsVA.setBounds(166, 42, 39, 14);
		panelAdres.add(lblPlaatsVA);
	}
	public AdresInfo getAdres() {
        this.adres.setLand(txtLand.getText());
        this.adres.setStraat(txtStraat.getText());
        this.adres.setPostcode(txtPostcode.getText());
        this.adres.setPlaats(txtPlaats.getText());
        this.adres.setHuisnummer(txtHuisnr.getText());
        this.adres.setHuisnummertoevoeging(txtHuisnrToevoeging.getText());
        

        /* Het komt voor dat de postcode achteraan het adres staat
         * Dan doen we niets*/

        if (this.adres.getHuisnummer().isEmpty() && this.adres.getHuisnummertoevoeging().isEmpty()){
        	AdresInfo.extractHuisnummerfromStraat(this.adres.getStraat(), this.adres);
        }
        	        
		if (this.adres.isEmtpy())
			return null;
		else
			return this.adres;
	}
	public void setAdres(AdresInfo _adres) {
		this.adres = _adres;
        if (this.adres == null)
        	adres = new AdresInfo();
        txtLand.setText(adres.getLand());
        txtStraat.setText(adres.getStraat());
        txtPostcode.setText(adres.getPostcode());
        txtPlaats.setText(adres.getPlaats());
        txtHuisnr.setText(adres.getHuisnummer());
        txtHuisnrToevoeging.setText(adres.getHuisnummertoevoeging());
	}
	public String getAdresSoort() {
		TitledBorder border;
		border = (TitledBorder)panelAdres.getBorder();
		return border.getTitle();
	}
	public void setAdresSoort(String adresSoort) {
		TitledBorder border;
		border = (TitledBorder)panelAdres.getBorder();
		border.setTitle(adresSoort);
	}
}
