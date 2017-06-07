package com.gieselaar.verzuim.views;

import com.gieselaar.verzuim.components.JTextFieldTGI;
import com.gieselaar.verzuim.controllers.AbstractController;
import com.gieselaar.verzuim.controllers.FactuurcategorieController;
import com.gieselaar.verzuim.viewsutils.VerzuimComboBoxModel;
import com.gieselaar.verzuimbeheer.services.FactuurcategorieInfo;
import com.gieselaar.verzuimbeheer.services.InfoBase;
import com.gieselaar.verzuimbeheer.services.BtwInfo.__btwtariefsoort;
import com.gieselaar.verzuimbeheer.utils.TypeEntry;
import javax.swing.JLabel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;

public class FactuurcategorieDetail extends AbstractDetail {

	private static final long serialVersionUID = 1L;
	private FactuurcategorieInfo factuurcategorie;
	private JComboBox<TypeEntry> cmbBtwSoort;
	private JComboBox<TypeEntry> cmbFactuurkoppen;
	private VerzuimComboBoxModel btwModel;
	private VerzuimComboBoxModel factuurkopModel;
	private JTextFieldTGI txtOmschrijving;
	private JCheckBox chckbxIsOmzet;
	private FactuurcategorieController factuurcategoriecontroller; 

	/**
	 * Create the frame.
	 */
	public FactuurcategorieDetail(AbstractController controller) {
		super("Beheer factuur categorien", controller);
		factuurcategoriecontroller = (FactuurcategorieController) controller;
		initialize();
	}
	private void initialize() {
		setBounds(50, 50, 439, 175);

		cmbBtwSoort = new JComboBox<>();
		cmbBtwSoort.setBounds(122, 3, 239, 20);
		getContentPane().add(cmbBtwSoort);
		
		JLabel lblBtwSoort = new JLabel("BTW soort");
		lblBtwSoort.setBounds(10, 8, 65, 14);
		getContentPane().add(lblBtwSoort);
		
		JLabel lblOmschrijving = new JLabel("Omschrijving");
		lblOmschrijving.setBounds(10, 87, 95, 14);
		getContentPane().add(lblOmschrijving);
		
		txtOmschrijving = new JTextFieldTGI();
		txtOmschrijving.setBounds(123, 83, 238, 20);
		getContentPane().add(txtOmschrijving);
		txtOmschrijving.setColumns(10);
		
		JLabel lblFactuurkop = new JLabel("Factuurkop");
		lblFactuurkop.setBounds(10, 32, 65, 14);
		getContentPane().add(lblFactuurkop);
		
		cmbFactuurkoppen = new JComboBox<TypeEntry>();
		cmbFactuurkoppen.setBounds(122, 27, 239, 20);
		getContentPane().add(cmbFactuurkoppen);
		
		chckbxIsOmzet = new JCheckBox("is omzet");
		chckbxIsOmzet.setBounds(117, 53, 97, 23);
		getContentPane().add(chckbxIsOmzet);
		
	}
	@Override
	public void setData(InfoBase info){
		factuurcategorie = (FactuurcategorieInfo)info;
		
		factuurkopModel = factuurcategoriecontroller.getComboModelFactuurkoppen();
		cmbFactuurkoppen.setModel(factuurkopModel);

		btwModel = controller.getMaincontroller().getEnumModel(__btwtariefsoort.class);
		cmbBtwSoort.setModel(btwModel);

		displayFactuurcategorie();
	}
	private void displayFactuurcategorie() {
		txtOmschrijving.setText(factuurcategorie.getOmschrijving());
		if (factuurcategorie.getBtwcategorie() != null){
			btwModel.setId(factuurcategorie.getBtwcategorie().getValue());
		}else{
			btwModel.setId(-1);
		}
		factuurkopModel.setId(factuurcategorie.getFactuurkopid());

		if (factuurcategorie.isIsomzet())
			chckbxIsOmzet.setSelected(true);
		else
			chckbxIsOmzet.setSelected(false);
			
	}
	@Override
	public InfoBase collectData(){
		factuurcategorie.setBtwcategorie(__btwtariefsoort.parse(btwModel.getId()));
		factuurcategorie.setFactuurkopid(factuurkopModel.getId());
		factuurcategorie.setIsomzet(chckbxIsOmzet.isSelected());
		factuurcategorie.setOmschrijving(txtOmschrijving.getText());
		return factuurcategorie;
	}
}
