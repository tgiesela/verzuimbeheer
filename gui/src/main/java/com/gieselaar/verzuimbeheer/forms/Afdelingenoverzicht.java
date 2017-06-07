package com.gieselaar.verzuimbeheer.forms;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.JOptionPane;

import com.gieselaar.verzuimbeheer.baseforms.BaseListForm;
import com.gieselaar.verzuimbeheer.baseforms.DefaultListFormNotification;
import com.gieselaar.verzuimbeheer.baseforms.JDesktopPaneTGI;
import com.gieselaar.verzuimbeheer.forms.AfdelingDetail;
import com.gieselaar.verzuimbeheer.services.AfdelingInfo;
import com.gieselaar.verzuimbeheer.services.ContactpersoonInfo;
import com.gieselaar.verzuimbeheer.services.InfoBase;
import com.gieselaar.verzuimbeheer.services.InfoBase.persistenceaction;
import com.gieselaar.verzuimbeheer.services.WerkgeverInfo;

public class Afdelingenoverzicht extends BaseListForm{

	/**
	 * 
	 */
	private static final long serialVersionUID = 870224421995495395L;
	private List<AfdelingInfo> afdelingen = null;
	private WerkgeverInfo werkgever = null;
	/**
	 * Create the frame.
	 */
	public Afdelingenoverzicht(JDesktopPaneTGI mdiPanel) {
		super("Afdelingen overzicht", mdiPanel);
		initialize();
	}
	public void initialize(){
		this.setEventNotifier(new DefaultListFormNotification() {
			
			@Override
			public void populateTableRequested() {
				populateTable();
			}
			@Override
			public __continue deleteButtonClicked(InfoBase info) {
				int rslt = JOptionPane.showConfirmDialog(null, "Weet u zeker dat u de afdeling wilt verwijderen?","",JOptionPane.YES_NO_OPTION);
				if (rslt == JOptionPane.YES_OPTION){
					AfdelingInfo afdeling = (AfdelingInfo)info;
					afdeling.setAction(persistenceaction.DELETE);
					return __continue.allow;
				}
				else
					return __continue.dontallow;
			}
			@Override
			public void newCreated(InfoBase info) {
				AfdelingInfo _info = (AfdelingInfo)info;
				werkgever = (WerkgeverInfo) getParentInfo();
				_info.setWerkgeverId(werkgever.getId());
			}
			@Override
			public void newInfoAdded(InfoBase info) {
				afdelingen.add((AfdelingInfo)info);
			}
		});
		setBounds(100, 100, 677, 423);
		setDetailFormClass(AfdelingDetail.class, AfdelingInfo.class);
		addColumn("Naam", null );
		addColumn("Contactpersoon",null);
	}
	@Override
	public void setParentInfo(InfoBase info){
		super.setParentInfo(info);
		werkgever = (WerkgeverInfo) this.getParentInfo();
		afdelingen = werkgever.getAfdelings();
	}
	public void populateTable(){
		ContactpersoonInfo cps = null;
		deleteAllRows();
		if  (afdelingen != null)
		{
			for(AfdelingInfo w :afdelingen)
			{
				if (w.getAction() != persistenceaction.DELETE)
				{
					Vector<Object> rowData = new Vector<Object>();
					rowData.add(w.getNaam());
					
					cps = w.getContactpersoon();
					if (cps == null)
						rowData.add("");
					else
						rowData.add(cps.getAchternaam());
					
					addRow(rowData, w);
				}
			}
		}
		else
		{
			afdelingen = new ArrayList<AfdelingInfo>();
			werkgever.setAfdelings(afdelingen);
		}
	}
}
