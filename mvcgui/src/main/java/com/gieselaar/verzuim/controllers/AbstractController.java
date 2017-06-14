package com.gieselaar.verzuim.controllers;

import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.swing.DefaultListSelectionModel;
import javax.swing.JDesktopPane;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JRootPane;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.gieselaar.verzuim.interfaces.ControllerEventListener;
import com.gieselaar.verzuim.interfaces.ModelEventListener;
import com.gieselaar.verzuim.models.AbstractModel;
import com.gieselaar.verzuim.views.AbstractDetail;
import com.gieselaar.verzuim.views.AbstractList;
import com.gieselaar.verzuim.viewsutils.ColorTableModel;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.services.DocumentTemplateInfo;
import com.gieselaar.verzuimbeheer.services.GebruikerInfo;
import com.gieselaar.verzuimbeheer.services.InfoBase;
import com.gieselaar.verzuimbeheer.services.VerzuimInfo;
import com.gieselaar.verzuim.utils.ExceptionLogger;
import com.gieselaar.verzuimbeheer.utils.VerzuimProperties;
import com.gieselaar.verzuimbeheer.utils.VerzuimProperties.__verzuimproperty;
import com.gieselaar.verzuim.utils.WordDocument;

public abstract class AbstractController implements ModelEventListener, Serializable, ActionListener
{
	/**
	 * Controller is created by:
	 * 		 user action, such as create form
	 * 		 indirect by another controller to control a object on the form
	 * 
	 * Controller creates model for its own purposes to get and maintain data
	 * 
	 * Controller listens to the model to get informed when data changes.
	 * 
	 * Controller can filter data based on filters set by the user (via controller)
	 * 
	 * Controller can create detailview to view/change data.
	 * 
	 * A form can create controllers for specific form elements (tables). The data
	 * is maintained by the created controller and its models. 
	 * 
	 * The detailview is instructed via ControllerEvents such as:
	 * 
	 * 		SETMODE (New,Update)
	 * 		SETDATA (data needed by the detailview)
	 * 
	 * Database queries do not return data from the model. The model is queried 
	 * afterwards by the controller to get a (updated) list of the data.
	 */
	private static final long serialVersionUID = 1L;

	protected static Logger log = Logger.getLogger("com.gieselaar"); 

	public enum __controllerevents{
		UNKNOWN(-1), 
		CLOSEVIEW(1), 
		ROWSELECTED(2), 
		SHOWDETAILS(3), 
		SETLOGINSESSION(4), 
		SETDATA(5), 
		SETMODE(6),
		REFRESH(7);
		private int value;

		__controllerevents(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}
		public static __controllerevents parse(int type) {
			__controllerevents field = null; // Default
			for (__controllerevents item : __controllerevents.values()) {
				if (item.getValue() == type) {
					field = item;
					break;
				}
			}
			return field;
		}
		public static __controllerevents parse(String type) {
			__controllerevents field = __controllerevents.UNKNOWN; // Default
			for (__controllerevents item : __controllerevents.values()) {
				if (item.toString().equals(type)) {
					field = item;
					break;
				}
			}
			return field;
		}
	}
	public enum __formmode{
		NEW, UPDATE
	}
	public enum __selectfileoption{
		FILEONLY,
		FILEORDIRECTORY, 
		DIRECTORYONLY;
	}
	public enum __filedialogtype{
		SAVE,
		OPEN;
	}
	
	public String closeListActionCommand = "CloseListActionCommand"; 
	public String okDetailActionCommand = "OkDetailActionCommand"; 
	public String cancelDetailActionCommand = "CancelDetailActionCommand"; 
	protected class DetailForm{
		ControllerEventListener form;
		InfoBase data;
		public AbstractDetail detailform;
	}

	protected transient List<ControllerEventListener> views = new ArrayList<>();
	protected transient List<DetailForm> detailForms = new ArrayList<>();
	private AbstractModel model;
	private JDesktopPane desktoppane;
	private MDIMainController maincontroller;
	protected Object selectedrow;
	protected int selectedrowindex = -1;
	protected int selectedrowinview = -1;
	protected int rowcount = -1;

	private AbstractDetail detailform = null;
	private AbstractList listform = null;
	public AbstractController(AbstractModel model, JDesktopPane desktoppane){
		this.model = model;
		this.desktoppane = desktoppane;
		model.addModelEventListener(this);
	}
	@Override 
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals(closeListActionCommand)){
			for (ControllerEventListener cel: views){
				if (cel instanceof AbstractList){
					closeView(cel);
					return;
				}
			}
		}else if (e.getActionCommand().equals(cancelDetailActionCommand)){
			AbstractDetail view = getContainingForm(e.getSource());
			if (view == null){
				return;
			}
			for (ControllerEventListener cel: views){
				if (cel.equals(view)){
					if (view.isDatachanged())
					{
						int choice = JOptionPane.showConfirmDialog(view, "Gegevens niet opgeslagen. Als u doorgaat gaan de wijzigingen verloren. Weet u het zeker?","Waarschuwing", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
						if (choice != JOptionPane.YES_OPTION)
							return;
					}
					closeView(cel);
					view.setVisible(false);
					return;
				}
			}
		}else if (e.getActionCommand().equals(okDetailActionCommand)){
			AbstractDetail view = getContainingForm(e.getSource());
			if (view == null){
				return;
			}
			for (ControllerEventListener cel: views){
				if (cel.equals(view)){
					InfoBase data = view.collectData();
					if (data == null){ 
						/* no data from view */
						return;
					}
					try {
						if (view.getFormmode() == __formmode.NEW){
							addData(data);
						}else{
							saveData(data);
						}
					} catch (VerzuimApplicationException e1) {
						ExceptionLogger.ProcessException(e1, view);
						return;
					}
					if (view.getCloseAfterSave()){
						closeView(cel);
						view.setVisible(false);
					}else{
						dataSaved(view);
					}
					return;
				}
			}
		}
	}
	/**
	 * Override this function to initialize the form after the save action
	 * has completed. This is only useful when setCloseAfterSave in the form
	 * has been set to false;
	 * 
	 * @param view
	 */
	public void dataSaved(AbstractDetail view) {
	}
	protected AbstractDetail getContainingForm(Object source) {
		Container container = ((Container) source).getParent();
		while (!(container instanceof JRootPane)){
			container = container.getParent();
		}
		container = container.getParent();
		if (container instanceof AbstractDetail){
			return (AbstractDetail)container;
		}else{
			return null;
		}
	}
	public ControllerEventListener addControllerListener(ControllerEventListener ves){
		views.add(ves);
		log.debug(this.getClass().toString() + " Adding view to Listeners: " + ves.toString());
		return ves;
	}
	public void deleteControllerListener(ControllerEventListener ves){
		if (views.contains(ves)){
			log.debug(this.getClass().toString() + " Deleting view from Listeners: " + ves.toString());
			views.remove(ves);
		}
		for (DetailForm form: detailForms){
			if (form.detailform.equals(ves)){
				form.detailform.dispose();
				form.detailform = null;
				detailForms.remove(form);
				break;
			}
		}
	}
	public void deleteViewAll(List<ControllerEventListener> listeners){
		for (ControllerEventListener l: listeners){
			deleteControllerListener(l);
		}
	}
	public abstract void saveData(InfoBase data) throws VerzuimApplicationException;
	public abstract void addData(InfoBase data) throws VerzuimApplicationException;
	
	public void cancelUpdate(ControllerEventListener ves) {
		closeView(ves);
	}

	public void closeView(ControllerEventListener ves){
		deleteControllerListener(ves);
		ves.close();
		for (ControllerEventListener l: views){
			l.formClosed(ves);
		}
		if (views.isEmpty()){
			model.deleteModelEventListener(this);
		}
	}
	/*
	 * ModelEventListener default implementation
	 */
	@Override
	public void listComplete(Object data) {
		for (ControllerEventListener l: views){
			l.refreshTable();
		}
	}
	@Override
	public void rowUpdated(Object data) {
		for (ControllerEventListener l: views){
			l.refreshTable();
		}
		if (maincontroller != null){
			maincontroller.modelUpdated(data.getClass());
		}
	}
	@Override
	public void rowAdded(Object data) {
		for (ControllerEventListener l: views){
			l.refreshTable();
		}
		if (maincontroller != null){
			maincontroller.modelUpdated(data.getClass());
		}
	}
	@Override
	public void rowDeleted(Object data) {
		for (ControllerEventListener l: views){
			l.refreshTable();
		}
		if (maincontroller != null){
			maincontroller.modelUpdated(data.getClass());
		}
	}
	
	/*
	 * Getters and setters
	 */
	public AbstractModel getModel() {
		return model;
	}
	public JDesktopPane getDesktoppane() {
		return desktoppane;
	}
	public void setDesktoppane(JDesktopPane pane){
		this.desktoppane = pane;
	}
	public MDIMainController getMaincontroller() {
		return maincontroller;
	}
	public void setMaincontroller(MDIMainController maincontroller) {
		this.maincontroller = maincontroller;
	}
	public GebruikerInfo getGebruiker(){
		return model.getSession().getGebruiker();
	}
	/*
	 * Action Listeners for the table and action routines 
	 */
	public void tableClicked(ControllerEventListener ves, JTable table, MouseEvent e) {
		int row;
		row = table.getSelectedRow();
		if (row < 0){
		    /* Row is filtered out */
		}else {
			if (e != null && (e.getClickCount() > 1)) {
				showRow(ves, selectedrow);
			}
		}
	}
	public void tableSelectionChanged(ControllerEventListener ves, JTable table, ListSelectionEvent e) {
		if (!e.getValueIsAdjusting()){
			DefaultListSelectionModel model = (DefaultListSelectionModel)e.getSource();
			selectedrowinview = model.getMinSelectionIndex();
			if (selectedrowinview >= 0){
				selectedrowindex = table.convertRowIndexToModel(selectedrowinview);
	
				ColorTableModel tablemodel = (ColorTableModel) table.getModel();
				selectedrow = tablemodel.getRowData(selectedrowindex);
				rowSelected(selectedrowinview, selectedrow);
	
				rowcount = tablemodel.getRowCount();
				for (ControllerEventListener l:views){
					l.rowSelected(selectedrowinview, selectedrow);
				}
			}
		}
	}
	
	public void  rowSelected(int selectedRow, Object data) {
	}

	public void deleteRow(Object data) throws VerzuimApplicationException{
		if (!isDeleteAllowed((InfoBase)data))
			return;
	}
	protected abstract boolean isDeleteAllowed(InfoBase data);
	public abstract void showRow(ControllerEventListener ves, Object data);
	public void showRow(ControllerEventListener ves, AbstractDetail detailform, Object data){
		if (!isShowAllowed((InfoBase)data)){
			return;
		}
		
		DetailForm form = new DetailForm();
		form.form = ves;
		form.data = (InfoBase) data;
		form.detailform = detailform;
		detailForms.add(form);
	}
	protected abstract boolean isShowAllowed(InfoBase data);
	public abstract void addRow(ControllerEventListener ves);
	public void addRow(ControllerEventListener ves, AbstractDetail detailform) {
		if (!isNewAllowed()){
			return;
		}
		
		DetailForm form = new DetailForm();
		form.form = ves;
		form.detailform = detailform;
		form.data = null;
		detailForms.add(form);
	}
	protected abstract boolean isNewAllowed();

	public AbstractDetail createDetailForm(Object data, Class<?> formclass, __formmode mode) {
		try {
			AbstractDetail form = (AbstractDetail) formclass.getConstructor(AbstractController.class).newInstance(this);
			ControllerEventListener listener = form;
			listener.setDetailFormmode(mode);
			listener.setData((InfoBase)data);
			form.activateListener(form);
			form.setDatachanged(false);
			this.getDesktoppane().add(form);
			this.getDesktoppane().moveToFront(form);
			form.setVisible(true);
			this.setDetailform(form);
			return form;
		} catch (InstantiationException|IllegalAccessException	 |IllegalArgumentException|
				 SecurityException	   |InvocationTargetException|NoSuchMethodException e1) {
        	ExceptionLogger.ProcessException(e1,null);
		}
		return null;
	}

	public void refreshDatabase() throws VerzuimApplicationException {
		model.refreshDatabase();
	}

	public void exportToExcel(JTable table) {
		// Vraag om naam excel bestand
		String documentname = "export.xls";
		String headerLabel;

		File selectedFile = null;

		selectedFile = selectFilename(__selectfileoption.FILEONLY,__filedialogtype.SAVE,"",".xls");
		if (selectedFile == null)
			return;
		else {
			if (selectedFile.exists()){
				if (JOptionPane.showConfirmDialog(null, "Bestand "
						+ selectedFile.getAbsolutePath()
						+ " bestaat al. Wilt u het verwijderen?", "Verwijderen",
						JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION){
					if (!selectedFile.delete()){
						JOptionPane.showMessageDialog(null, "Kan bestand niet verwijderen.");
						return;
					}
				}
				else
					return;
			}
			documentname = selectedFile.getAbsolutePath();
		}
		
		//
        // Create an instance of HSSFWorkbook.
        //
        HSSFWorkbook workbook = new HSSFWorkbook();

        //
        // Create two sheets in the excel document and name it First Sheet and
        // Second Sheet.
        //
        HSSFSheet sheet = workbook.createSheet("Blad 1");
        
		DefaultTableModel exportmodel = (DefaultTableModel)table.getModel();
		TableColumnModel columnmodel = table.getColumnModel();
        HSSFRow rowHeader = sheet.createRow(0);
		for (int i=0;i<exportmodel.getColumnCount();i++){
			headerLabel = (String) columnmodel.getColumn(i).getHeaderValue();
	        HSSFCell cellA = rowHeader.createCell(i);
	        cellA.setCellValue(new HSSFRichTextString(headerLabel));
		}
		for (int row=0;row<table.getRowCount();row++)
		{
			HSSFRow excelRow = sheet.createRow(row+1);
			int actualrow = table.convertRowIndexToModel(row);
			for (int column=0;column<exportmodel.getColumnCount();column++)
			{
				HSSFCell cell = excelRow.createCell(column);
				Object value = ((Vector<?>)exportmodel.getDataVector().elementAt(actualrow)).elementAt(column);
				if (value == null)
					cell.setCellValue("");
				else
				if (value instanceof Date){
		        	String strDate = new SimpleDateFormat("dd-MM-yyyy").format((Date)value);
					cell.setCellValue(strDate);
		        }
				else
					cell.setCellValue(value.toString());
			}
		}
		
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(new File(documentname));
            workbook.write(fos);
        } catch (IOException ex) {
        	ExceptionLogger.ProcessException(ex,null);
        } finally {
            if (fos != null) {
                try {
                    fos.flush();
                    fos.close();
                } catch (IOException ex) {
                	ExceptionLogger.ProcessException(ex,null);
                }
            }
        }
	}
	public File selectFilename(__selectfileoption option, __filedialogtype type, String currentselection, String assureextension) {
		File selectedfile;
		String lastsavedir;
		JFileChooser fd = new JFileChooser();
		int retval;
		
		VerzuimProperties verzuimProps = new VerzuimProperties();
		if (currentselection == null || currentselection.isEmpty()){
			lastsavedir = (String) verzuimProps
					.getProperty(__verzuimproperty.lastdocsavedir);
		} else{
			fd.setSelectedFile(new File(currentselection));
			lastsavedir = currentselection;
		}

		switch (option){
		case FILEONLY:
			fd.setFileSelectionMode(JFileChooser.FILES_ONLY);
			break;
		case FILEORDIRECTORY:
			fd.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
			break;
		case DIRECTORYONLY:
			fd.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			break;
		default:
			break;
		}
		if (lastsavedir != null)
			fd.setCurrentDirectory(new File(lastsavedir));
		switch (type){
		case OPEN:
			fd.setDialogType(JFileChooser.OPEN_DIALOG);
			retval = fd.showOpenDialog(null);
			break;
		case SAVE:
			fd.setDialogType(JFileChooser.SAVE_DIALOG);
			retval = fd.showSaveDialog(null);
			break;
		default:
			retval = JFileChooser.CANCEL_OPTION;
			break;
		}
		if (retval == JFileChooser.APPROVE_OPTION) {
			selectedfile = fd.getSelectedFile(); 
			lastsavedir = selectedfile.getPath();
			verzuimProps.saveProperties();
			if (assureextension == null || assureextension.isEmpty()){
				return selectedfile;
			}else{
				String filename = selectedfile.getAbsolutePath();
				if (filename.endsWith(assureextension))
					return selectedfile;
				else {
					return new File(filename + assureextension);
				}
			}
		} else
			return null;

	}
	public void genereerDocument(VerzuimInfo verzuim, Integer templateid) {
		List<DocumentTemplateInfo> templates = maincontroller.getDocumentTemplates();
		for (DocumentTemplateInfo template:templates){
			if (template.getId().equals(templateid)){
				WordDocument doc = new WordDocument(null, this.model.getSession());
				try {
					doc.GenerateDocument(verzuim, template);
				} catch (IOException | ValidationException e1) {
					ExceptionLogger.ProcessException(e1,null);
				}
				break;
			}
		}
	}
	public void open(URI uri) throws ValidationException {
		String os = System.getProperty("os.name").toLowerCase();
		try {
			String filename = URLDecoder.decode(uri.getSchemeSpecificPart(), "UTF-8");
			File filetoopen = new File(filename);
			if (filetoopen.exists()){
				if (os.indexOf("win") >= 0) {
					Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + "\"" + uri.getScheme() + ":" + filename + "\"");
				} else{
					Runtime.getRuntime().exec(new String[] { "/usr/bin/open", URLDecoder.decode(uri.getPath(), "UTF-8") });
				}
			}else{
				throw new ValidationException("Bestand niet gevonden: " + filename);
			}
		} catch (IOException e) {
			ExceptionLogger.ProcessException(e, this.getActiveForm());
		}
	}
	public AbstractList getListform() {
		return listform;
	}
	public void setListform(AbstractList listform) {
		this.listform = listform;
	}
	public AbstractDetail getDetailform() {
		return detailform;
	}
	public void setDetailform(AbstractDetail detailform) {
		this.detailform = detailform;
	}
	protected Component getActiveForm(){
		if (detailform != null){
			return detailform;
		}else{
			return listform;
		}
	}
}
