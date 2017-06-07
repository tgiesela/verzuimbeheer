package com.gieselaar.verzuim.controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import com.gieselaar.verzuim.interfaces.ControllerEventListener;
import com.gieselaar.verzuim.models.WerkgeverModel;
import com.gieselaar.verzuim.utils.ExceptionLogger;
import com.gieselaar.verzuim.viewsutils.ColorTableModel;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.facades.LoginSessionRemote;
import com.gieselaar.verzuimbeheer.services.InfoBase;
import com.gieselaar.verzuimbeheer.services.ImportResult;

public class ImportController extends AbstractController {

	private static final long serialVersionUID = 1L;

	public enum __importresultfields {
		UNKNOWN(-1), RESULTAAT(0), INPUT(1), FOUTMELDING(2);
		private int value;

		__importresultfields(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}

		public static __importresultfields parse(int type) {
			__importresultfields field = UNKNOWN; // Default
			for (__importresultfields item : __importresultfields.values()) {
				if (item.getValue() == type) {
					field = item;
					break;
				}
			}
			return field;
		}
	}
	public enum __importbestandfields {
		UNKNOWN(-1), RESULTAAT(0), BESTAND(1), WERKGEVER(2);
		private int value;

		__importbestandfields(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}

		public static __importbestandfields parse(int type) {
			__importbestandfields field = UNKNOWN; // Default
			for (__importbestandfields item : __importbestandfields.values()) {
				if (item.getValue() == type) {
					field = item;
					break;
				}
			}
			return field;
		}
	}
	private WerkgeverModel model;
	private String documentnaam;

	public ImportController(LoginSessionRemote session) {
		super(new WerkgeverModel(session), null);
		this.model = (WerkgeverModel)getModel();
	}

	@Override
	public void addData(InfoBase data) throws VerzuimApplicationException {
		ImportResult info = (ImportResult)data;
		try {
			info.validate();
		} catch (ValidationException e) {
			throw new VerzuimApplicationException(e, "Opslaan results niet geslaagd.");
		}
	}
	@Override
	public void saveData(InfoBase data) throws VerzuimApplicationException {
		ImportResult info = (ImportResult)data;
		try {
			info.validate();
		} catch (ValidationException e) {
			throw new VerzuimApplicationException(e, "Opslaan results niet geslaagd.");
		}
	}
	@Override
	public void showRow(ControllerEventListener ves, Object data) {
		if (!isShowAllowed((InfoBase)data))
			return;
		if (!documentnaam.isEmpty()){
			URI uri;
			try {
				uri = new URI("File://"
						+ URLEncoder.encode(documentnaam, "UTF-8"));
				open(uri);
			} catch (UnsupportedEncodingException | URISyntaxException | ValidationException e) {
				ExceptionLogger.ProcessException(e, this.getActiveForm());
			}
		}
	}
	@Override
	public void addRow(ControllerEventListener ves) {
		if (!isNewAllowed())
			return;
	}
	@Override
	public void deleteRow(Object data) throws VerzuimApplicationException {
		if (!isDeleteAllowed((InfoBase)data)){
			return;
		}
	}

	public void getTableModel(List<ImportResult> results, ColorTableModel tblmodel, List<Integer> colsinview) {
		tblmodel.setRowCount(0);
		tblmodel.setColumnCount(colsinview.size());
		if (results != null){
			for (ImportResult info : results) {
				List<Object> colsinmodel = new ArrayList<>();
	
				setColumnValues(colsinmodel, info, colsinview);
				tblmodel.addRow(colsinmodel, info);
			}
		}
	}
	private void setColumnValues(List<Object> colsinmodel, ImportResult wfi, List<Integer> colsinview) {
		for (int i = 0; i < colsinview.size(); i++) {
			__importresultfields val = __importresultfields.parse(colsinview.get(i));
			switch (val) {
			case FOUTMELDING:
				if (wfi.isImportok()){
					colsinmodel.add(i, "");
				}else{
					if (wfi.isWarning()){
						colsinmodel.add(i, "WARN: " + wfi.getErrorMessage());
					}else{
						colsinmodel.add(i, wfi.getErrorMessage());
					}
				}
				break;
			case INPUT:
				colsinmodel.add(i, wfi.getSourceLine());
				break;
			case RESULTAAT:
				Integer rslt = wfi.getResult();
				colsinmodel.add(i, rslt.toString());
				break;
			default:
				break;
			}
		}
	}

	@Override
	protected boolean isDeleteAllowed(InfoBase data) {
		return false;
	}

	@Override
	protected boolean isShowAllowed(InfoBase data) {
		return true;
	}

	@Override
	protected boolean isNewAllowed() {
		return false;
	}

	public void afsluitenDienstverbanden(String docnaam, Integer holdingid, String separator, boolean veldnamenrij) {
		documentnaam = docnaam;
		byte[] barray = fileToBytearray(docnaam);
		try {
			model.afsluitenDienstverbanden(holdingid, separator, barray, veldnamenrij);
		} catch (VerzuimApplicationException e) {
			ExceptionLogger.ProcessException(e, this.getActiveForm());
		}
	}

	private byte[] fileToBytearray(String docnaam) {
		File csvFile = new File(docnaam);
		FileInputStream input;
		byte[] barray = null;
		try {
			input = new FileInputStream(csvFile);
    		barray = new byte[(int) csvFile.length()];
    		input.read(barray);
    		input.close();
		} catch (FileNotFoundException e1) {
        	ExceptionLogger.ProcessException(e1,null);
		} catch (IOException e1) {
        	ExceptionLogger.ProcessException(e1,null);
		}
		return barray;
	}

	public List<ImportResult> getImportResults() {
		return model.getImportResults();
	}

	public void importeerUren(String docnaam, String separator, boolean veldnamenrij) {
		documentnaam = docnaam;
		byte[] barray = fileToBytearray(docnaam);
		try{
			model.importUren( barray, separator, veldnamenrij);
		} catch (VerzuimApplicationException e) {
			ExceptionLogger.ProcessException(e, this.getActiveForm());
		}
	}

	public void importWerknemers(String docnaam, int werkgeverid, String separator, boolean veldnamenrij) {
		documentnaam = docnaam;
		byte[] barray = fileToBytearray(docnaam);
		try{
			model.importWerknemers( werkgeverid, barray, separator, veldnamenrij);
		} catch (VerzuimApplicationException e) {
			ExceptionLogger.ProcessException(e, this.getActiveForm());
		}
	}

	public void setImportresults(List<ImportResult> results) {
		model.setImportresults(results);
	}

	public void setDocumentnaam(String name) {
		documentnaam = name;
	}
}
