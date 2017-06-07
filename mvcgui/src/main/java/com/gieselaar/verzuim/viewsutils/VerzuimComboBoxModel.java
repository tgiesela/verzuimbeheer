package com.gieselaar.verzuim.viewsutils;

import javax.swing.DefaultComboBoxModel;

import com.gieselaar.verzuim.controllers.AbstractController;
import com.gieselaar.verzuimbeheer.utils.TypeEntry;

public class VerzuimComboBoxModel extends DefaultComboBoxModel<TypeEntry>{
	private static final long serialVersionUID = 1L;
	public VerzuimComboBoxModel(AbstractController controller){
		
	}
	

	public Integer getId(){
		return ((TypeEntry)this.getSelectedItem()).getValue();
	}
	public void setId(Integer id){
		if (id == null)
			id = -1;
		int i=0;
		while (i<this.getSize()){
			TypeEntry item = this.getElementAt(i);
			if (item.getValue() == id){
				this.setSelectedItem(item);
			}
			i++;
		}
	}
}
