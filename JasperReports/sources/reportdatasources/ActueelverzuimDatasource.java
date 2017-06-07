package reportdatasources;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
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
import com.gieselaar.verzuimbeheer.reportservices.ActueelVerzuimInfo;
import com.gieselaar.verzuimbeheer.reportservices.VerzuimAantalInfo;
import com.gieselaar.verzuimbeheer.services.BedrijfsgegevensInfo;
import com.gieselaar.verzuimbeheer.utils.RemoteInterfaces;
import com.gieselaar.verzuimbeheer.utils.ServiceCaller;
import com.gieselaar.verzuimbeheer.utils.ServiceLocator;
import com.gieselaar.verzuimbeheer.utils.ServiceLocatorException;

public class ActueelverzuimDatasource {
	private Date startdatum;
	private Date einddatum;
	private LoginSessionRemote loginsession;
	private InstantieFacadeRemote instantieFacade;
	protected static Logger log = Logger.getLogger("com.gieselaar"); 
	
	public ActueelverzuimDatasource(Date startdatum, Date einddatum, LoginSessionRemote loginsession){
		this.startdatum = startdatum;
		this.einddatum = einddatum;
		this.loginsession = loginsession;
		
	}
	private List<ActueelVerzuimInfoExtended> getResultsets(Integer werkgeverid, Integer holdingid, Integer oeid, Date startdatum, Date eindatum)
	{
		HashMap<Integer, Integer> vajaar = new HashMap<>();
		HashMap<Integer, Integer> va12 = new HashMap<>();
		List<ActueelVerzuimInfo> verzuimen = new ArrayList<>();
		List<VerzuimAantalInfo> verzuimenditjaar = new ArrayList<>();
		List<VerzuimAantalInfo> verzuimen12maanden = new ArrayList<>();
		List<ActueelVerzuimInfoExtended> avzel = new ArrayList<>();
		try {
			verzuimen = ServiceCaller.reportFacade(loginsession).getActueelVerzuim(werkgeverid, holdingid, oeid, startdatum, einddatum,true);
			Calendar cal = Calendar.getInstance();
			cal.setTime(einddatum);
			cal.set(Calendar.MONTH,0);
			cal.set(Calendar.DATE,1);
			verzuimenditjaar = ServiceCaller.reportFacade(loginsession).getAantalVerzuimenInPeriode(werkgeverid, holdingid, oeid, cal.getTime(), einddatum,true);
			cal.setTime(einddatum);
			cal.add(Calendar.MONTH,-12);
			verzuimen12maanden = ServiceCaller.reportFacade(loginsession).getAantalVerzuimenInPeriode(werkgeverid, holdingid, oeid, cal.getTime(), einddatum,true);
			
			for (VerzuimAantalInfo vai: verzuimenditjaar){
				vajaar.put(vai.getWerknemerid(), vai.getAantalverzuimen());
			}
			for (VerzuimAantalInfo vai: verzuimen12maanden){
				va12.put(vai.getWerknemerid(), vai.getAantalverzuimen());
			}
			for (ActueelVerzuimInfo avz: verzuimen){
				ActueelVerzuimInfoExtended avie = new ActueelVerzuimInfoExtended();
				avie.setActueelverzuim(avz);
				avie.setAantalditjaar(vajaar.get(avz.getWerknemerid()));
				avie.setAantal12maanden(va12.get(avz.getWerknemerid()));
				avzel.add(avie);
			}
			return avzel;
    	
    	} catch (Exception e1) {
			log.error(e1);
		}
		return null;
	}
	public JasperPrint getReport(Integer werkgeverid, Integer holdingid, Integer oeid, String bedrijfsnaam, boolean redentonen) throws ValidationException{
		try {
			instantieFacade = (InstantieFacadeRemote) ServiceLocator
					.getInstance().getRemoteHome(
							RemoteInterfaces.InstantieFacade.getValue(), 
							InstantieFacadeRemote.class);
			instantieFacade.setLoginSession(loginsession);

			List<BedrijfsgegevensInfo> bedrijfsgegevens = new ArrayList<>();
			bedrijfsgegevens.add(instantieFacade.allBedrijfsgegevens().get(0));
		
			List<ActueelVerzuimInfoExtended> avile = getResultsets(werkgeverid, holdingid, oeid, startdatum, einddatum);
			if (avile == null || avile.isEmpty()){
				throw new ValidationException("Er zijn geen verzuimen gevonden in de opgegeven periode");
			}
			
			URL url = JRLoader.getResource("verzuim/Actueelverzuimbeknopt.jasper");
			if (url == null){
				throw new RuntimeException("Kan resource Actueelverzuimbeknopt.jasper niet vinden!");
			}
			String path = new File(url.getFile()).getParent();
	
			Map<String,Object> parameters = new HashMap<>();
			parameters.put("SUBREPORT_DIR", path);
			parameters.put(JRParameter.REPORT_LOCALE, new Locale("nl","NL"));
			parameters.put("startdatum", startdatum);
			parameters.put("einddatum",einddatum);
			parameters.put("Bedrijfsnaam", bedrijfsnaam);
			parameters.put("redenverzuimtonen", redentonen);
			JasperReport report;
			parameters.put("Footer_datasource",bedrijfsgegevens);
			report = (JasperReport) JRLoader.loadObject(url);
			return JasperFillManager.fillReport(report, parameters, new JRBeanCollectionDataSource(avile));
			
		} catch (JRException |ServiceLocatorException|PermissionException|VerzuimApplicationException e) {
			log.error(e);
			return null;
		}
		
	}

}
