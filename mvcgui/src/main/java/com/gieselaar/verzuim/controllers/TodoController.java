package com.gieselaar.verzuim.controllers;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.swing.JOptionPane;

import com.gieselaar.verzuim.interfaces.ControllerEventListener;
import com.gieselaar.verzuim.models.TodoModel;
import com.gieselaar.verzuim.utils.ExceptionLogger;
import com.gieselaar.verzuim.views.AbstractDetail;
import com.gieselaar.verzuim.views.TodoDetail;
import com.gieselaar.verzuim.views.TodoList;
import com.gieselaar.verzuim.views.WerknemerDetail;
import com.gieselaar.verzuim.viewsutils.ColorTableModel;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.facades.LoginSessionRemote;
import com.gieselaar.verzuimbeheer.services.ActiviteitInfo;
import com.gieselaar.verzuimbeheer.services.InfoBase;
import com.gieselaar.verzuimbeheer.services.TodoFastInfo;
import com.gieselaar.verzuimbeheer.services.TodoInfo;
import com.gieselaar.verzuimbeheer.services.InfoBase.persistenceaction;
import com.gieselaar.verzuimbeheer.services.InfoBase.persistencestate;
import com.gieselaar.verzuimbeheer.services.TodoInfo.__soort;
import com.gieselaar.verzuimbeheer.services.VerzuimActiviteitInfo;
import com.gieselaar.verzuimbeheer.services.WerknemerInfo;
import com.gieselaar.verzuimbeheer.utils.DateOnly;

public class TodoController extends AbstractController {

	private static final long serialVersionUID = 1L;

	public enum __todofields {
		UNKNOWN(-1), ACTIVITEIT(0), DEADLINE(1), HERHALEN(2), WAARSCHUWEN(3),
		INDICATOR(4), BSN(5), ACHTERNAAM(6), WERKGEVER(7), AANVANGVERZUIM(8),;
		private int value;

		__todofields(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}

		public static __todofields parse(int type) {
			__todofields field = UNKNOWN; // Default
			for (__todofields item : __todofields.values()) {
				if (item.getValue() == type) {
					field = item;
					break;
				}
			}
			return field;
		}
	}
	public enum __todocommands {
		UNKNOWN(-1), TODOTOEKOMSTIGETONEN(1), TODOAFGESLOTENTONEN(2), WERKNEMERTONEN(3);
		private int value;

		__todocommands(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}

		public static __todocommands parse(int type) {
			__todocommands field = null; // Default
			for (__todocommands item : __todocommands.values()) {
				if (item.getValue() == type) {
					field = item;
					break;
				}
			}
			return field;
		}

		public static __todocommands parse(String type) {
			__todocommands field = __todocommands.UNKNOWN; // Default
			for (__todocommands item : __todocommands.values()) {
				if (item.toString().equals(type)) {
					field = item;
					break;
				}
			}
			return field;
		}
	}

	private TodoModel model;
	
	private boolean toekomstigeTonen;
	private boolean afgeslotenTonen;
	private List<ActiviteitInfo> activiteiten;

	private Integer verzuimid;

	public TodoController(LoginSessionRemote session) {
		super(new TodoModel(session), null);
		this.model = (TodoModel) getModel();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		switch (__todocommands.parse(e.getActionCommand())) {
		case TODOTOEKOMSTIGETONEN:
			toekomstigeTonen = !toekomstigeTonen;
			for (ControllerEventListener l : views) {
				l.refreshTable();
			}
			break;
		case TODOAFGESLOTENTONEN:
			afgeslotenTonen = !afgeslotenTonen;
			if (afgeslotenTonen){
				try {
					if (verzuimid == null){
						model.getClosedTodos();
					}else{
						for (ControllerEventListener l : views) {
							l.refreshTable();
						}
					}
				} catch (VerzuimApplicationException e1) {
					ExceptionLogger.ProcessException(e1, null);
				}
			}else{
				for (ControllerEventListener l : views) {
					l.refreshTable();
				}
			}
			break;
		case WERKNEMERTONEN:
			WerknemerController controller = new WerknemerController(this.getModel().getSession());
			controller.setDesktoppane(getDesktoppane());
			controller.setMaincontroller(this.getMaincontroller());
			TodoFastInfo selectedtodo = (TodoFastInfo)selectedrow;
			WerknemerInfo werknemer = controller.getWerknemer(selectedtodo.getWerknemerid());
			WerknemerDetail frame = new WerknemerDetail(controller);
			frame.setFormmode(__formmode.UPDATE);
			frame.setData(werknemer);
			frame.setVisible(true);
			this.getDesktoppane().add(frame);
			this.getDesktoppane().setOpaque(true);
			this.getDesktoppane().moveToFront(frame);
			
			break;
		default:
			super.actionPerformed(e);
			break;
		}
	}
	@Override 
	public void rowSelected(int selectedRow, Object data) {
		selectedrow = data;
		for (ControllerEventListener l : views) {
			if (l instanceof TodoList){
				((TodoList)l).setRowSelected();
			}
		}
	}
	public void afrondenTodo(TodoInfo todo){
		VerzuimActiviteitInfo vai = new VerzuimActiviteitInfo();
		vai.setDatumactiviteit(new Date());
		vai.setDatumdeadline(todo.getDeadlinedatum());
		vai.setVerzuimId(todo.getVerzuimId());
		vai.setActiviteitId(todo.getActiviteitId());
		vai.setUser(model.getSession().getGebruiker().getId());
		todo.setVerzuimActiviteit(vai);
	}
	public void selectTodos() {
		try {
			verzuimid = null;
			model.getTodos();
			if (afgeslotenTonen){
				model.getClosedTodos();
			}
		} catch (VerzuimApplicationException e) {
			ExceptionLogger.ProcessException(e, this.getActiveForm());
		}
	}
	public void selectTodosVerzuim(Integer id) {
		try {
			verzuimid = id;
			model.getTodosVerzuim(id);
		} catch (VerzuimApplicationException e) {
			ExceptionLogger.ProcessException(e, this.getActiveForm());
		}
	}

	public TodoInfo getTodo(Integer todoid) {
		try {
			return model.getTodoDetails(todoid);
		} catch (VerzuimApplicationException e) {
			ExceptionLogger.ProcessException(e, this.getActiveForm());
		}
		return null;
	}

	private TodoInfo createNewTodo() {
		TodoInfo todo = new TodoInfo();
		todo.setUser(model.getSession().getGebruiker().getId());
		todo.setAchternaam(model.getSession().getGebruiker().getAchternaam());
		todo.setDeadlinedatum(new Date());
		todo.setHerhalen(false);
		todo.setSoort(__soort.HANDMATIG);
		if (this.verzuimid == null){
			JOptionPane.showMessageDialog(getActiveForm(), "Aanmaken nieuwe todo kan alleen via verzuim.");
			return null;
		}
		todo.setVerzuimId(verzuimid);
		todo.setWaarschuwingsdatum(new Date());
		todo.setAanmaakdatum(new Date());
		return todo;
	}

	public void addData(InfoBase data) throws VerzuimApplicationException {
		TodoInfo todo = (TodoInfo)data;
		try {
			todo.validate();
			model.addTodo(todo);
		} catch (ValidationException e) {
			throw new VerzuimApplicationException(e, "Opslaan todo niet geslaagd.");
		}
	}

	public void saveData(InfoBase data) throws VerzuimApplicationException {
		TodoInfo todo = (TodoInfo)data;
		try {
			/* Nodig zolang afdelingen niet direct in database worden gezet */
			/* TODO zet afdelingen direct in database */
			todo.validate();
			model.saveTodo(todo);
		} catch (ValidationException e) {
			throw new VerzuimApplicationException(e, "Opslaan todo niet geslaagd.");
		}
	}

	public void showRow(ControllerEventListener ves, Object data) {
		if (!isShowAllowed((InfoBase)data))
			return;
		TodoFastInfo wfi = (TodoFastInfo) data;
		TodoInfo todo = this.getTodo(wfi.getId());
		AbstractDetail form = super.createDetailForm(todo, TodoDetail.class, __formmode.UPDATE);
		super.showRow(ves, form, data);
	}

	public void addRow(ControllerEventListener ves) {
		if (!isNewAllowed())
			return;
		TodoInfo todo = createNewTodo();
		AbstractDetail form = super.createDetailForm(todo, TodoDetail.class, __formmode.NEW);
		super.addRow(ves, form);
	}
	@Override
	public void deleteRow(Object data) throws VerzuimApplicationException {
		if (!isDeleteAllowed((InfoBase)data)){
			return;
		}
		TodoFastInfo todof = (TodoFastInfo) data;
		TodoInfo todo = new TodoInfo();
		todo.setAction(persistenceaction.DELETE);
		todo.setState(persistencestate.EXISTS);
		todo.setId(todof.getId());
		model.deleteTodo(todo);

	}

	public void getTableModel(List<TodoFastInfo> todos, ColorTableModel tblmodel, List<Integer> colsinview) {
		/*
		 * Assumption: Data in todos is already filtered
		 */

		boolean tonen;
		int rownr = 0;
		ColorTableModel colormodel;
		
		activiteiten = this.getMaincontroller().getActiviteiten();
		
		Date vandaag = new Date();
		
		colormodel = (ColorTableModel) tblmodel;

		tblmodel.setRowCount(0);
		tblmodel.setColumnCount(colsinview.size());
		for (TodoFastInfo todo : todos) {
			List<Object> colsinmodel = new ArrayList<>();

			tonen = true;

			if (DateOnly.after(todo.getDeadlinedatum(),vandaag) &&
					DateOnly.after(todo.getWaarschuwingsdatum(),vandaag)){
				if (!toekomstigeTonen){
					tonen = false; /* Toekomstige todos worden niet getoond*/
				}
			}
			if (todo.getVerzuimactiviteitId() != null){
				if (!afgeslotenTonen){
					tonen = false;/* uitgevoerde todos worden niet getoond*/
				}
			}
			if (tonen){
				setColumnValues(colsinmodel, todo, colsinview);
				tblmodel.addRow(colsinmodel, todo);
				if (todo.getVerzuimactiviteitId() != null)
					colormodel.setCellColour(rownr, 0, Color.BLUE);
				else if (DateOnly.before(todo.getDeadlinedatum(),vandaag))
					colormodel.setCellColour(rownr, 0, Color.RED);
				else if (DateOnly.after(todo.getWaarschuwingsdatum(),vandaag)
						|| DateOnly.equals(todo.getWaarschuwingsdatum(),vandaag))
					colormodel.setCellColour(rownr, 0, Color.GREEN);
				else
					colormodel.setCellColour(rownr, 0, Color.ORANGE);
				rownr++;
			}
		}
	}

	private void setColumnValues(List<Object> colsinmodel, TodoFastInfo wfi, List<Integer> colsinview) {
		ActiviteitInfo act;
		for (int i = 0; i < colsinview.size(); i++) {
			__todofields val = __todofields.parse(colsinview.get(i));
			switch (val) {
			case ACTIVITEIT:
				act = lookupActiviteit(wfi.getActiviteitId());
				colsinmodel.add(i, act.getNaam());
				break;
			case DEADLINE:
				colsinmodel.add(i, wfi.getDeadlinedatum());
				break;
			case HERHALEN:
				if (wfi.getHerhalen())
					colsinmodel.add(i,"Ja");
				else
					colsinmodel.add(i,"Nee");
				break;
			case WAARSCHUWEN:
				colsinmodel.add(i, wfi.getWaarschuwingsdatum());
				break;
			case INDICATOR:
				colsinmodel.add(i,"");
				break;
			case AANVANGVERZUIM:
				colsinmodel.add(i,wfi.getStartdatumverzuim());
				break;
			case ACHTERNAAM:
				colsinmodel.add(i,wfi.getAchternaam());
				break;
			case BSN:
				colsinmodel.add(i,wfi.getBurgerservicenummer());
				break;
			case WERKGEVER:
				colsinmodel.add(i,wfi.getWerkgevernaam());
				break;
			default:
				break;
			}
		}
	}

	private ActiviteitInfo lookupActiviteit(int activiteitId) {
		for (ActiviteitInfo ai : activiteiten){
			if (ai.getId().equals(activiteitId)) {
				return ai;
			}
		}
		return null;
	}

	public void applyFilters(List<TodoFastInfo> werknemers) {
		Date vandaag = new Date();
		if (!toekomstigeTonen) {
			for (Iterator<TodoFastInfo> iter = werknemers.listIterator(); iter.hasNext();) {
				TodoFastInfo todo = iter.next();
				if (DateOnly.after(todo.getDeadlinedatum(),vandaag) &&
						DateOnly.after(todo.getWaarschuwingsdatum(),vandaag)){				
					iter.remove();
				}
			}
		}
		if (!afgeslotenTonen) {
			for (Iterator<TodoFastInfo> iter = werknemers.listIterator(); iter.hasNext();) {
				TodoFastInfo todo = iter.next();
				if (todo.getVerzuimactiviteitId() != null) {
					iter.remove();
				}
			}
		}
	}

	public List<TodoFastInfo> getTodoList() {
		if (afgeslotenTonen){
			return model.getAllTodoFast();
		}else{
			return model.getTodoFast();
		}
	}

	private boolean confirmDelete(TodoInfo wnr, TodoFastInfo wnrf) {
		int rslt;
		rslt = JOptionPane
				.showConfirmDialog(null,"Weet u zeker dat u de ToDo wilt verwijderen?",
						"", JOptionPane.YES_NO_OPTION);
		if (rslt == JOptionPane.YES_OPTION) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	protected boolean isDeleteAllowed(InfoBase data) {
		TodoFastInfo todofast;
		TodoInfo todo;
		todofast = (TodoFastInfo) data;
		try {
			todo = model.getTodoDetails(todofast.getId());
			return confirmDelete(todo, todofast);
		} catch (VerzuimApplicationException e) {
			ExceptionLogger.ProcessException(e, this.getActiveForm());
		}
		return false;
	}

	@Override
	protected boolean isShowAllowed(InfoBase data) {
		return true;
	}

	@Override
	protected boolean isNewAllowed() {
		if (verzuimid == null){
			JOptionPane.showMessageDialog(getActiveForm(), "Aanmaken nieuwe todo kan alleen via verzuim");
			return false;
		}
		return true;
	}

	public void setVerzuim(Object object) {
	}
}
