package com.gieselaar.verzuimbeheer.components;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.swing.JCheckBox;
import javax.swing.JOptionPane;

import com.gieselaar.verzuimbeheer.baseforms.DefaultListFormNotification;
import com.gieselaar.verzuimbeheer.baseforms.JDesktopPaneTGI;
import com.gieselaar.verzuimbeheer.baseforms.ListFormNotification;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.exceptions.PermissionException;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.forms.TodoDetail;
import com.gieselaar.verzuimbeheer.services.ActiviteitInfo;
import com.gieselaar.verzuimbeheer.services.InfoBase;
import com.gieselaar.verzuimbeheer.services.TodoInfo;
import com.gieselaar.verzuimbeheer.services.VerzuimInfo;
import com.gieselaar.verzuimbeheer.utils.DateOnly;
import com.gieselaar.verzuimbeheer.utils.ExceptionLogger;
import com.gieselaar.verzuimbeheer.utils.ServiceCaller;
import com.gieselaar.verzuimbeheer.utils.ServiceLocatorException;

public class TodoPanel extends TablePanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private JCheckBox chckbxAfgeslotenTonen;
	private JCheckBox chckbxToekomstigeTonen;
	private Component thisform = this;
	private TodoPanel thisclass = this;
	private VerzuimInfo verzuim;
	private List<ActiviteitInfo> activiteiten;
	private ListFormNotification myListener = null;
	public TodoPanel(JDesktopPaneTGI mdiPanel) {
		super(mdiPanel);
		initialize_panel();
	}
	private void initialize_panel(){
		this.setDetailFormClass(TodoDetail.class, TodoInfo.class);
		this.addColumn("", null, 20);
		this.addColumn("Activiteit", null, 100);
		this.addColumn("Deadline", null, 80, Date.class);
		this.addColumn("Waarschuwen", null, 80, Date.class);
		this.addColumn("Herhalen", null, 60);
		chckbxToekomstigeTonen = new JCheckBox("Toekomstige tonen");
		chckbxToekomstigeTonen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (verzuim != null)
					displayTodos();
			}
		});
		chckbxAfgeslotenTonen = new JCheckBox("Afgesloten tonen");
		chckbxAfgeslotenTonen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (verzuim != null)
					displayTodos();
			}
		});
		this.getPanelAction().add(chckbxAfgeslotenTonen);
		this.getPanelAction().add(chckbxToekomstigeTonen);
		this.setEventNotifier(new DefaultListFormNotification() {

			@Override
			public void populateTableRequested() {
				if (thisclass.getMyListener() != null)
					thisclass.myListener.populateTableRequested();
				try {
					verzuim = ServiceCaller.verzuimFacade(getLoginSession()).getVerzuimDetails(verzuim.getId());
				} catch (PermissionException e) {
					ExceptionLogger.ProcessException(e,thisform);
				} catch (VerzuimApplicationException e) {
		        	ExceptionLogger.ProcessException(e,thisform);
				} catch (Exception e1){
		        	ExceptionLogger.ProcessException(e1,thisform);
				}
				displayTodos();
			}

			@Override
			public void newCreated(InfoBase info) {
				TodoInfo todoinfo = (TodoInfo) info;
				todoinfo.setVerzuimId(verzuim.getId());
				todoinfo.setVerzuim(verzuim);
				if (thisclass.getMyListener() != null)
					thisclass.myListener.newCreated(info);
			}

			@Override
			public __continue newButtonClicked() {
				if (thisclass.getMyListener() != null)
					return thisclass.myListener.newButtonClicked();
				else
					return __continue.allow;
			}

			@Override
			public __continue deleteButtonClicked(InfoBase info) {
				try {
					int rslt = JOptionPane.showConfirmDialog(thisform, "Weet u zeker dat u de ToDo wilt verwijderen?","",JOptionPane.YES_NO_OPTION);
					if (rslt == JOptionPane.YES_OPTION){
						ServiceCaller.verzuimFacade(getLoginSession()).deleteTodo((TodoInfo) info);
						displayTodos();
					}
					else
						return __continue.dontallow;
				} catch (ValidationException e) {
					ExceptionLogger.ProcessException(e,thisform,false);
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
			public void detailFormClosed(){
				displayTodos();
			}
		});
	}
	protected void displayTodos() {
		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		ActiviteitInfo act = null;
		Date vandaag = null;
		boolean tonen;
		try {
			Calendar cal = Calendar.getInstance();
			cal.setTime(new Date());
			vandaag = cal.getTime();
			vandaag = formatter.parse(formatter.format(vandaag));
		} catch (ParseException e) {
			e.printStackTrace();
			return;
		}
		this.deleteAllRows();
		int rownr = 0;
		if (verzuim.getTodos() != null) {
			for (TodoInfo t : verzuim.getTodos()) {
				Vector<Object> rowData = new Vector<Object>();
				for (ActiviteitInfo ai : activiteiten)
					if (ai.getId().equals(t.getActiviteitId())) {
						act = ai;
						break;
					}
				tonen = true;
				if (DateOnly.after(t.getDeadlinedatum(),vandaag) &&
					DateOnly.after(t.getWaarschuwingsdatum(),vandaag))
					if (!chckbxToekomstigeTonen.isSelected())
						tonen = false; /* Toekomstige todos worden niet getoond*/
				if (t.getVerzuimactiviteitId() != null)
					if (!chckbxAfgeslotenTonen.isSelected())
						tonen = false;/* uitgevoerde todos worden niet getoond*/

				if (tonen)
				{
					rowData.add("");
					rowData.add(act.getNaam());
					rowData.add(t.getDeadlinedatum());
					rowData.add(t.getWaarschuwingsdatum());

					if (t.getHerhalen())
						rowData.add("Ja");
					else
						rowData.add("Nee");
					this.addRow(rowData, t);
					rownr++;
					if (t.getVerzuimactiviteitId() != null)
						this.setCellColour(rownr - 1, 0, Color.BLUE);
					else if (DateOnly.before(t.getDeadlinedatum(),vandaag))
						this.setCellColour(rownr - 1, 0, Color.RED);
					else if (DateOnly.after(t.getWaarschuwingsdatum(),vandaag)
							|| DateOnly.equals(t.getWaarschuwingsdatum(),vandaag))
						this.setCellColour(rownr - 1, 0, Color.GREEN);
					else
						this.setCellColour(rownr - 1, 0, Color.ORANGE);
				}
			}
		}
		this.setMdiPanel(this.getMdiPanel());
	}
	public VerzuimInfo getVerzuim() {
		return verzuim;
	}
	public void setVerzuim(VerzuimInfo verzuim) {
		this.verzuim = verzuim;
		try {
			this.verzuim = ServiceCaller.verzuimFacade(getLoginSession()).getVerzuimDetails(this.verzuim.getId());
			if (activiteiten == null){
				activiteiten = ServiceCaller.verzuimFacade(getLoginSession()).getActiviteiten();
			}
		} catch (ServiceLocatorException e) {
        	ExceptionLogger.ProcessException(e,this,"Unable to connect to server");
		} catch (PermissionException e) {
			ExceptionLogger.ProcessException(e,thisform);
		} catch (VerzuimApplicationException e2) {
	    	ExceptionLogger.ProcessException(e2,thisform);
		} catch (Exception e1){
        	ExceptionLogger.ProcessException(e1,thisform);
		}
		if (this.verzuim != null)
			displayTodos();
	}
	public List<ActiviteitInfo> getActiviteiten() {
		return activiteiten;
	}
	public void setActiviteiten(List<ActiviteitInfo> activiteiten) {
		this.activiteiten = activiteiten;
	}
	public ListFormNotification getMyListener() {
		return myListener;
	}
	public void setMyListener(ListFormNotification myListener) {
		this.myListener = myListener;
	}
}
