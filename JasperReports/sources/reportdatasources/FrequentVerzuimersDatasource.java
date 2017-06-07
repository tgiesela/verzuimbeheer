package reportdatasources;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jfree.util.Log;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.util.JRLoader;

import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.exceptions.PermissionException;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.facades.InstantieFacadeRemote;
import com.gieselaar.verzuimbeheer.facades.LoginSessionRemote;
import com.gieselaar.verzuimbeheer.reportservices.WerknemerVerzuimInfo;
import com.gieselaar.verzuimbeheer.services.BedrijfsgegevensInfo;
import com.gieselaar.verzuimbeheer.utils.RemoteInterfaces;
import com.gieselaar.verzuimbeheer.utils.ServiceCaller;
import com.gieselaar.verzuimbeheer.utils.ServiceLocator;
import com.gieselaar.verzuimbeheer.utils.ServiceLocatorException;

public class FrequentVerzuimersDatasource {
	private Date startdatum;
	private Date einddatum;
	private LoginSessionRemote loginsession;
	private InstantieFacadeRemote instantieFacade;
	protected static Logger log = Logger.getLogger("com.gieselaar"); 
	
	public FrequentVerzuimersDatasource(Date startdatum, Date einddatum, LoginSessionRemote loginsession){
		this.startdatum = startdatum;
		this.einddatum = einddatum;
		this.loginsession = loginsession;
		
	}
	private List<WerknemerVerzuimInfo> getResultsets(Integer werkgeverid, Integer holdingid, Integer oeid, Date startdatum, Date eindatum, Integer minaantal)
	{
		List<WerknemerVerzuimInfo> verzuimen = new ArrayList<>();
		try {
    		verzuimen = ServiceCaller.reportFacade(loginsession).getWerknemerVerzuimen(werkgeverid, holdingid, -1, startdatum, einddatum, minaantal);
			return verzuimen;
    	
		} catch (Exception e1){
			log.error(e1);
		}
		return null;
	}
	public JasperPrint getReport(Integer werkgeverid, Integer holdingid, Integer oeid, Integer minaantal) throws ValidationException{
		try {
			instantieFacade = (InstantieFacadeRemote) ServiceLocator
					.getInstance().getRemoteHome(
							RemoteInterfaces.InstantieFacade.getValue(), 
							InstantieFacadeRemote.class);
			instantieFacade.setLoginSession(loginsession);

			List<BedrijfsgegevensInfo> bedrijfsgegevens = new ArrayList<>();
			bedrijfsgegevens.add(instantieFacade.allBedrijfsgegevens().get(0));
		
			List<WerknemerVerzuimInfo> wnvzmi = getResultsets(werkgeverid, holdingid, oeid, startdatum, einddatum, minaantal);
			if (wnvzmi.isEmpty()){
				throw new ValidationException("Er zijn geen gegevens gevonden in de opgegeven periode");
			}
			
			URL url = JRLoader.getResource("verzuim/FrequentVerzuim.jasper");
			if (url == null){
				throw new RuntimeException("Kan resource FrequentVerzuim.jasper niet vinden!");
			}
			String path = new File(url.getFile()).getParent();
	
			Map<String,Object> parameters = new HashMap<>();
			parameters.put("SUBREPORT_DIR", path);
			parameters.put(JRParameter.REPORT_LOCALE, new Locale("nl","NL"));
			parameters.put("startdatum", startdatum);
			parameters.put("einddatum",einddatum);
			parameters.put("minimumaantalverzuimen", minaantal);
			JasperReport report;
			parameters.put("Footer_datasource",bedrijfsgegevens);
			report = (JasperReport) JRLoader.loadObject(url);
			return JasperFillManager.fillReport(report, parameters, new JRBeanCollectionDataSource(wnvzmi));
			
		} catch (JRException|ServiceLocatorException|PermissionException|VerzuimApplicationException e) {
			log.error(e);
			return null;
		}
		
	}

}
