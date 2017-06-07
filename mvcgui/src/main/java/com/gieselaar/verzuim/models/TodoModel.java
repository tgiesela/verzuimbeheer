package com.gieselaar.verzuim.models;

import java.util.ArrayList;
import java.util.List;

import com.gieselaar.verzuim.interfaces.ModelEventListener;
import com.gieselaar.verzuimbeheer.exceptions.PermissionException;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.facades.LoginSessionRemote;
import com.gieselaar.verzuimbeheer.services.TodoFastInfo;
import com.gieselaar.verzuimbeheer.services.TodoInfo;
import com.gieselaar.verzuimbeheer.utils.ServiceCaller;
import com.gieselaar.verzuimbeheer.utils.ServiceLocatorException;

public class TodoModel extends AbstractModel {
	private static final long serialVersionUID = 1L;
	private List<TodoFastInfo> todos;
	private List<TodoFastInfo> closedtodos;
	private Integer verzuimid = null; 
	private boolean inclusiefafgesloten = false;
	public TodoModel(LoginSessionRemote session){
		super(session);
	}

	public void getTodos() throws VerzuimApplicationException {
		try {
			this.verzuimid = null;
			todos = ServiceCaller.verzuimFacade(this.getSession()).getOpenTodosFast();
			for (ModelEventListener ml: this.changelisteners){
				ml.listComplete(todos);
			}
		} catch (PermissionException | ServiceLocatorException e) {
			throw new VerzuimApplicationException(e, e.getMessage());
		}
	}
	public void getClosedTodos() throws VerzuimApplicationException {
		try {
			inclusiefafgesloten = true;
			this.verzuimid = null;
			closedtodos = ServiceCaller.verzuimFacade(this.getSession()).getClosedTodosFast();
			for (ModelEventListener ml: this.changelisteners){
				ml.listComplete(todos);
			}
		} catch (PermissionException | ServiceLocatorException e) {
			throw new VerzuimApplicationException(e, e.getMessage());
		}
	}
	public void getTodosVerzuim(Integer verzuimid) throws VerzuimApplicationException {
		try {
			this.verzuimid = verzuimid;
			todos = ServiceCaller.verzuimFacade(this.getSession()).getTodosVerzuim(verzuimid);
			for (ModelEventListener ml: this.changelisteners){
				ml.listComplete(todos);
			}
		} catch (PermissionException | ServiceLocatorException e) {
			throw new VerzuimApplicationException(e, e.getMessage());
		}
	}
	public TodoInfo getTodoDetails(int todoid) throws VerzuimApplicationException  {
		try {
			return ServiceCaller.verzuimFacade(getSession()).getTodo(todoid);
		} catch (PermissionException | ServiceLocatorException e) {
			throw new VerzuimApplicationException(e, e.getMessage());
		}
	}
	public void saveTodo(TodoInfo todo) throws VerzuimApplicationException {
		boolean found = false;
		try {
			ServiceCaller.verzuimFacade(getSession()).updateTodo(todo);
			/* Now also the list has to be updated */
			/* First we remove it from todos and closedtodos
			 * Then we will will add it to todos or closedtodos
			 * depending on the fact that is a closed one or not
			 */
			for (TodoFastInfo w: todos){
				if (w.getId().equals(todo.getId())){
					found = true;
					todos.remove(w);
					break;
				}
			}
			if (!found && closedtodos != null){
				for (TodoFastInfo w: closedtodos){
					if (w.getId().equals(todo.getId())){
						closedtodos.remove(w);
						break;
					}
				}
			}
			TodoFastInfo todoNew = getTodoFast(todo.getId());
			if (todo.getVerzuimactiviteitId() != null){
				if (closedtodos != null){
					closedtodos.add(todoNew);
				}
			}else{
				todos.add(todoNew);
			}
			for (ModelEventListener ml: this.changelisteners){
				ml.rowUpdated(todo);
			}
		} catch (PermissionException | ServiceLocatorException | ValidationException e) {
			throw new VerzuimApplicationException(e, e.getMessage());
		}	
	}

	private TodoFastInfo getTodoFast(Integer id) throws VerzuimApplicationException, PermissionException, ServiceLocatorException {
		return ServiceCaller.verzuimFacade(getSession()).getTodoFast(id);
	}

	public void addTodo(TodoInfo todo) throws VerzuimApplicationException {
		try {
			TodoInfo todoNew = ServiceCaller.verzuimFacade(getSession()).addTodo(todo);
			TodoFastInfo todoFastNew = getTodoFast(todoNew.getId());
			todos.add(todoFastNew);
			for (ModelEventListener ml: this.changelisteners){
				ml.rowAdded(todo);
			}
		} catch (PermissionException | ServiceLocatorException | ValidationException e) {
			throw new VerzuimApplicationException(e, e.getMessage());
		}	
	}

	public List<TodoFastInfo> getTodoFast() {
		return todos;
	}

	public List<TodoFastInfo> getAllTodoFast() {
		List<TodoFastInfo> alltodos = new ArrayList<>();
		alltodos.addAll(todos);
		if (closedtodos != null){
			alltodos.addAll(closedtodos);
		}
		return alltodos;
	}

	public void deleteTodo(TodoInfo todo) throws VerzuimApplicationException {
		boolean found = false;
		try {
			ServiceCaller.verzuimFacade(getSession()).deleteTodo(todo);
			for (TodoFastInfo w: todos){
				if (w.getId().equals(todo.getId())){
					todos.remove(w);
					found = true;
					break;
				}
			}
			if (!found && closedtodos != null){
				for (TodoFastInfo w: closedtodos){
					if (w.getId().equals(todo.getId())){
						closedtodos.remove(w);
						break;
					}
				}
			}
			for (ModelEventListener ml: this.changelisteners){
				ml.rowDeleted(todo);
			}
		} catch (PermissionException | ServiceLocatorException | ValidationException e) {
			throw new VerzuimApplicationException(e, e.getMessage());
		}	
	}

	@Override
	public void refreshDatabase() throws VerzuimApplicationException {
		if (verzuimid != null){
			this.getTodosVerzuim(verzuimid);
		}else{
			this.getTodos();
			if (inclusiefafgesloten){
				this.getClosedTodos();
			}
		}
	}

}
