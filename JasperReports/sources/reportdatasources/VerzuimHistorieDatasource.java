package reportdatasources;

import java.io.File;
import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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
import com.gieselaar.verzuimbeheer.facades.ReportFacadeRemote;
import com.gieselaar.verzuimbeheer.reportservices.ActueelVerzuimInfo;
import com.gieselaar.verzuimbeheer.services.BedrijfsgegevensInfo;
import com.gieselaar.verzuimbeheer.utils.RemoteInterfaces;
import com.gieselaar.verzuimbeheer.utils.ServiceLocator;
import com.gieselaar.verzuimbeheer.utils.ServiceLocatorException;

public class VerzuimHistorieDatasource {

	private Date startdatum;
	private Date einddatum;
	private LoginSessionRemote loginsession;
	private InstantieFacadeRemote instantieFacade;
	
	public VerzuimHistorieDatasource(Date startdatum, Date einddatum, LoginSessionRemote loginsession){
		this.startdatum = startdatum;
		this.einddatum = einddatum;
		this.loginsession = loginsession;
		
	}
	private boolean verzuiminverslagperiode(Date vzmperstart, Date vzmpereind,
			Date verslagdatumstart, Date verslagdatumeind) {

		if (vzmperstart.before(verslagdatumstart)) {
			if (vzmpereind.after(verslagdatumstart))
				return true;
		} else // gelijk of groter dan start
		if (vzmperstart.before(verslagdatumeind)) {
			return true;
		}

		return false;
	}
	public JasperPrint getReport(Integer werknemer) throws ValidationException{
		List<ActueelVerzuimInfo> verzuimen;
		List<VerzuimHistorieGrafiek> vhgl = new ArrayList<>();
		ReportFacadeRemote reportRemote = null;
		try {
			reportRemote = (ReportFacadeRemote) ServiceLocator
					.getInstance().getRemoteHome(
							RemoteInterfaces.ReportFacade.getValue(),
							ReportFacadeRemote.class);
			instantieFacade = (InstantieFacadeRemote) ServiceLocator
					.getInstance().getRemoteHome(
							RemoteInterfaces.InstantieFacade.getValue(), 
							InstantieFacadeRemote.class);
			reportRemote.setLoginSession(loginsession);
			instantieFacade.setLoginSession(loginsession);

			List<BedrijfsgegevensInfo> bedrijfsgegevens = new ArrayList<>();
			bedrijfsgegevens.add(instantieFacade.allBedrijfsgegevens().get(0));
		
			verzuimen = reportRemote.getWerknemerVerzuimen(werknemer,
						startdatum, einddatum);
			if (verzuimen.isEmpty()){
				throw new ValidationException("Er zijn geen verzuimen gevonden in de opgegeven periode");
			}
			
			// Vullen van arraylist voor grafiek
			// voor elke week een rij
			Date verslagdatumstart = startdatum;
			Date verslagdatumeind = null;
			Date vzmperstart, vzmpereind;
			BigDecimal vzmpercentage = BigDecimal.ZERO;
			int currentverzuimid = -1;
			ActueelVerzuimInfo avzi = null;
			int inx = 0;
			Calendar datumcal = Calendar.getInstance();
			Calendar datumeinde = Calendar.getInstance();
			datumcal.setTime(verslagdatumstart);

			vzmpereind = startdatum;
			vzmperstart = startdatum;
			while (verslagdatumstart.before(einddatum)) {
				if (!vzmpereind.after(verslagdatumstart)) { // verzuim periode
															// is al geeindigd
															// voor de
															// verslagperiode
					if (inx < verzuimen.size()) // dan het volgende
												// verzuimherstel gebruiken
					{
						avzi = verzuimen.get(inx);
						if (avzi.getVerzuimid() == currentverzuimid) {
							vzmperstart = vzmpereind;
							datumeinde.setTime(avzi.getDatumHerstel());
							vzmpercentage = avzi.getVerzuimpercentage();
						} else {
							currentverzuimid = avzi.getVerzuimid();
							vzmperstart = avzi.getStartdatumverzuim();
							vzmpercentage = avzi.getVerzuimpercentage();
							if (avzi.getHerstelid() == 0){
								datumeinde.setTime(einddatum);
							}else{
								datumeinde.setTime(avzi.getDatumHerstel());
							}
						}
						datumeinde.add(Calendar.DAY_OF_MONTH, avzi.getVerzuimherstelduurinperiode().intValue());
						vzmpereind = datumeinde.getTime();
						inx = inx + 1;
					}else{
						if (avzi.getEinddatumverzuim() == null){
							vzmpereind = einddatum; 
						}
					}
				}

				/*
				 * 1. Het huidige verzuim(herstel) valt binnen de verslagperiode
				 * 2. Het huidige verzuim(herstel) start na de verslagperiode 3.
				 * Het huidige verzuim(herstel) valt voor de verslagperiode
				 */
				datumcal.add(Calendar.DAY_OF_MONTH, 7);
				verslagdatumeind = datumcal.getTime();

				VerzuimHistorieGrafiek vhg = new VerzuimHistorieGrafiek();
				vhg.setStartdate(datumcal.getTime());
				vhg.setPercentage(BigDecimal.ZERO);
				if (verzuiminverslagperiode(vzmperstart, vzmpereind,
						verslagdatumstart, verslagdatumeind)) {
					vhg.setPercentage(vzmpercentage);
				}

				vhgl.add(vhg);
				verslagdatumstart = verslagdatumeind;
			}
			URL url = JRLoader.getResource("verzuim/WerknemerVerzuimHistorie.jasper");
			if (url == null){
				System.out.println("Kan resource WerknemerVerzuimHistorie.jasper niet vinden!");
				throw new RuntimeException("Kan resource WerknemerVerzuimHistorie.jasper niet vinden!");
			}
			String path = new File(url.getFile()).getParent();
	
			Map<String,Object> parameters = new HashMap<String, Object>();
			parameters.put("SUBREPORT_DIR", path);
			parameters.put(JRParameter.REPORT_LOCALE, new Locale("nl","NL"));
			parameters.put("startdatum", startdatum);
			parameters.put("einddatum",einddatum);
			JasperReport report;
			parameters.put("Footer_datasource",bedrijfsgegevens);
			parameters.put("Grafiek_datasource",vhgl);
			report = (JasperReport) JRLoader.loadObject(url);
			JasperPrint print = JasperFillManager.fillReport(report, parameters, new JRBeanCollectionDataSource(verzuimen));
			return print;
			
		} catch (JRException e) {
			throw new ValidationException("Probleem bij aanmaken rapport: " + e.getMessage());
		} catch (ServiceLocatorException e) {
			throw new ValidationException("Probleem bij aanmaken rapport" + e.getMessage());
		} catch (PermissionException e) {
			throw new ValidationException("Probleem bij aanmaken rapport: " + e.getMessage());
		} catch (VerzuimApplicationException e) {
			throw new ValidationException("Probleem bij aanmaken rapport: " + e.getMessage());
		}
		
	}

}
