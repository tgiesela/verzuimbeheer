package reportdatasources;

import java.io.File;
import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
import com.gieselaar.verzuimbeheer.reportservices.WerknemerAantallenInfo;
import com.gieselaar.verzuimbeheer.services.AfdelingInfo;
import com.gieselaar.verzuimbeheer.services.BedrijfsgegevensInfo;
import com.gieselaar.verzuimbeheer.services.VerzuimInfo.__gerelateerdheid;
import com.gieselaar.verzuimbeheer.services.WerkgeverInfo;
import com.gieselaar.verzuimbeheer.services.InfoBase.__geslacht;
import com.gieselaar.verzuimbeheer.services.VerzuimInfo.__vangnettype;
import com.gieselaar.verzuimbeheer.utils.RemoteInterfaces;
import com.gieselaar.verzuimbeheer.utils.ServiceCaller;
import com.gieselaar.verzuimbeheer.utils.ServiceLocator;
import com.gieselaar.verzuimbeheer.utils.ServiceLocatorException;

public class VerzuimOverzichtDatasource implements Comparator<ActueelVerzuimInfo>{
	protected static Logger log = Logger.getLogger("com.gieselaar"); 
	private Date startdatum;
	private Date einddatum;
	private LoginSessionRemote loginsession;
	private InstantieFacadeRemote instantieFacade;

	private List<ActueelVerzuimInfo> verzuimen = new ArrayList<>();
	private List<ActueelVerzuimInfo> verzuimenpermaand = new ArrayList<>();

	public static class verzuimcompare implements
			Comparator<ActueelVerzuimInfo> {
		public enum __sortcol {
			achternaam, werknemerid;
		}

		__sortcol sortcol;

		public verzuimcompare(__sortcol column) {
			sortcol = column;
		}

		@Override
		public int compare(ActueelVerzuimInfo o1, ActueelVerzuimInfo o2) {
			int i;
			i = o1.getWerkgeverid().compareTo(o2.getWerkgeverid());
			if (i != 0)
				return i;

			i = o1.getAfdelingid().compareTo(o2.getAfdelingid());
			if (i != 0)
				return i;

			switch (sortcol) {
			case werknemerid:
				i = o1.getWerknemerid().compareTo(o2.getWerknemerid());
				if (i != 0)
					return i;

				return o1.getVerzuimid().compareTo(o2.getVerzuimid());
			case achternaam:
				i = o1.getAchternaam().compareTo(o2.getAchternaam());
				if (i != 0)
					return i;

				return o1.getVerzuimid().compareTo(o2.getVerzuimid());
			default:
				break;
			}
			return 0;
		}
	}

	public VerzuimOverzichtDatasource(Date startdatum, Date einddatum,
			LoginSessionRemote loginsession) {
		this.startdatum = startdatum;
		this.einddatum = einddatum;
		this.loginsession = loginsession;

	}

	private boolean openVerzuiminPeriode(ActueelVerzuimInfo vzm,
			Date startdatum, Date einddatum) {
		if (vzm.getEinddatumverzuim() == null) {
			if (vzm.getStartdatumverzuim().before(einddatum)) {
				return true;
			} else {
				return false;
			}
		} else {
			// Einddatum is ingevuld
			if (vzm.getEinddatumverzuim().before(startdatum)) {
				return false;
			} else {
				if (vzm.getStartdatumverzuim().before(einddatum)) {
					return true;
				} else {
					return false;
				}
			}
		}
	}

	private boolean verzuimOnderdrukken(ActueelVerzuimInfo vzm,
			Date startdatum, Date einddatum) {
		if (vzm.getStartdatumverzuim() == null)
			return true;
		else if (openVerzuiminPeriode(vzm, startdatum, einddatum)) {
			return false;
		} else {
			return true;
		}
	}
	private WerknemerAantallenInfo findAantallen(
			Integer afdelingid
		  , List<WerknemerAantallenInfo> aantallen
		  , __geslacht geslacht){
		for (WerknemerAantallenInfo wai : aantallen) {
			if (wai.getAfdelingId().equals(afdelingid) &&
				wai.getGeslacht() == geslacht) {
				return wai;
			}
		}
		return null;
	}
	private List<VerzuimGemiddelde> getVerzuimGemiddelden(
			List<ActueelVerzuimInfo> verzuimen, Date startdatum,
			Date einddatum, boolean inclusiefzwangerschap,
			List<WerkgeverInfo> werkgevers) throws VerzuimApplicationException,
			PermissionException, ServiceLocatorException {
		int aantalverzuimen;
		int verzuiminx;
		Integer currentWerkgeverId;
		Integer currentAfdelingId;
		Integer currentVerzuimId;
		VerzuimGemiddelde gem = null;
		ActueelVerzuimInfo vzm;
		List<VerzuimGemiddelde> gemList = new ArrayList<>();
		List<WerknemerAantallenInfo> wnraantallenstart;
		List<WerknemerAantallenInfo> wnraantalleneinde;
		List<WerknemerAantallenInfo> wnraantallen;

		// Eerst sorteren we de verzuimen op werkgeverid, afdelingid,
		// werknemerid, verzuimid
		Collections.sort(verzuimen, new verzuimcompare(
				verzuimcompare.__sortcol.werknemerid));
		aantalverzuimen = verzuimen.size();
		verzuiminx = 0;
		currentWerkgeverId = -1;
		currentAfdelingId = -1;
		currentVerzuimId = -1;

		for (WerkgeverInfo wg : werkgevers) {
			wnraantallenstart = ServiceCaller.reportFacade(loginsession)
					.getAantalWerknemersAfdeling(wg.getId(), startdatum);
			wnraantalleneinde = ServiceCaller.reportFacade(loginsession)
					.getAantalWerknemersAfdeling(wg.getId(), einddatum);
			wnraantallen = ServiceCaller.reportFacade(loginsession)
					.getAantalWerknemersAfdeling(wg.getId(), startdatum, einddatum);
			for (AfdelingInfo afd : wg.getAfdelings()) {
				gem = new VerzuimGemiddelde();
				gem.setWerkgeverid(wg.getId());
				gem.setAfdelingid(afd.getId());
				if (wg.getHoldingId() == null) {
					gem.setHoldingid(-1);
				} else {
					gem.setHoldingid(wg.getHoldingId());
				}
				gem.setWerkgeverWerkweek(wg.getWerkweek());
				gem.setAfdelingnaam(afd.getNaam());
				gem.setWerkgevernaam(wg.getNaam());
				if (wg.getHolding() != null)
					gem.setHoldingnaam(wg.getHolding().getNaam());
				else
					gem.setHoldingnaam("");

				WerknemerAantallenInfo wai;

				wai = findAantallen(afd.getId(), wnraantallenstart, __geslacht.MAN);
				if (wai != null){
					gem.setAantalmannenStartperiode(wai.getAantalwerknemers());
					gem.setUrenmannenStartperiode(wai.getTotaalurenwerknemers());
				}
				
				wai = findAantallen(afd.getId(), wnraantallenstart, __geslacht.VROUW);
				if (wai != null){
					gem.setAantalvrouwenStartperiode(wai.getAantalwerknemers());
					gem.setUrenvrouwenStartperiode(wai.getTotaalurenwerknemers());
				}
				
				wai = findAantallen(afd.getId(), wnraantallen, __geslacht.MAN);
				if (wai != null){
					gem.setUrenmannenPeriode(wai.getTotaalurenwerknemers());
				}
				
				wai = findAantallen(afd.getId(), wnraantallen, __geslacht.VROUW);
				if (wai != null){
					gem.setUrenvrouwenPeriode(wai.getTotaalurenwerknemers());
				}

				wai = findAantallen(afd.getId(), wnraantalleneinde, __geslacht.MAN);
				if (wai != null){
					gem.setAantalmannenEindperiode(wai.getAantalwerknemers());
					gem.setUrenmannenEindperiode(wai.getTotaalurenwerknemers());
				}
				wai = findAantallen(afd.getId(), wnraantalleneinde, __geslacht.VROUW);
				if (wai != null){
					gem.setAantalvrouwenEindperiode(wai.getAantalwerknemers());
					gem.setUrenvrouwenEindperiode(wai.getTotaalurenwerknemers());
				}
				gemList.add(gem);
			}
		}

		while (verzuiminx < aantalverzuimen) {
			vzm = verzuimen.get(verzuiminx);
			if (vzm.getWerkgeverid().equals(currentWerkgeverId)
					&& vzm.getAfdelingid().equals(currentAfdelingId)) {
				/* Zelfde afdeling */
			} else {
				/* Nieuwe afdeling/werkgever */
				currentAfdelingId = vzm.getAfdelingid();
				currentWerkgeverId = vzm.getWerkgeverid();
				gem = findAfdelingGemiddelde(gemList, currentWerkgeverId,
						currentAfdelingId);

			}

			if (currentVerzuimId.equals(vzm.getVerzuimid())
					|| vzm.getWerkgeverwerkweek().equals(new BigDecimal(0))) {
				/* skip */
			} else {
				/* nieuw verzuim */
				currentVerzuimId = vzm.getVerzuimid();
				if (!verzuimOnderdrukken(vzm, startdatum, einddatum)) {
					if (vzm.getGeslacht() == __geslacht.MAN.getValue()) {
						gem.setAantalverzuimenMannen(gem
								.getAantalverzuimenMannen() + 1);
						if (vzm.getVerzuimduurinperiode().compareTo(
								new BigDecimal(7)) != 1) {
							gem.setAantalverzuimenMannen_1_7(gem
									.getAantalverzuimenMannen_1_7() + 1);
						}
						if ((vzm.getVerzuimduurinperiode().compareTo(
								new BigDecimal(8)) != -1)
								&& (vzm.getVerzuimduurinperiode().compareTo(
										new BigDecimal(41)) != 1)) {
							gem.setAantalverzuimenMannen_8_41(gem
									.getAantalverzuimenMannen_8_41() + 1);
						}
						if ((vzm.getVerzuimduurinperiode().compareTo(
								new BigDecimal(42)) != -1)
								&& (vzm.getVerzuimduurinperiode().compareTo(
										new BigDecimal(91)) != 1)) {
							gem.setAantalverzuimenMannen_42_91(gem
									.getAantalverzuimenMannen_42_91() + 1);
						}
						if ((vzm.getVerzuimduurinperiode().compareTo(
								new BigDecimal(92)) != -1)
								&& (vzm.getVerzuimduurinperiode().compareTo(
										new BigDecimal(182)) != 1)) {
							gem.setAantalverzuimenMannen_92_182(gem
									.getAantalverzuimenMannen_92_182() + 1);
						}
						if (vzm.getVerzuimduurinperiode().compareTo(
								new BigDecimal(183)) != -1) {
							gem.setAantalverzuimenMannen_183_730(gem
									.getAantalverzuimenMannen_183_730() + 1);
						}
						if (!vzm.getStartdatumverzuim().before(startdatum)) {
							gem.setAantalnieuweverzuimenMannen(gem
									.getAantalnieuweverzuimenMannen() + 1);
						}
						if (vzm.getEinddatumverzuim() != null) {
							gem.setAantalbeeindigdeverzuimenMannen(gem
									.getAantalbeeindigdeverzuimenMannen() + 1);
							gem.setVerzuimduurMannen(gem.getVerzuimduurMannen()
									.add(vzm.getVerzuimduurinperiode()));
						}
					} else {
						gem.setAantalverzuimenVrouwen(gem
								.getAantalverzuimenVrouwen() + 1);
						if (vzm.getVerzuimduurinperiode().compareTo(
								new BigDecimal(7)) != 1) {
							gem.setAantalverzuimenVrouwen_1_7(gem
									.getAantalverzuimenVrouwen_1_7() + 1);
						}
						if ((vzm.getVerzuimduurinperiode().compareTo(
								new BigDecimal(8)) != -1)
								&& (vzm.getVerzuimduurinperiode().compareTo(
										new BigDecimal(41)) != 1)) {
							gem.setAantalverzuimenVrouwen_8_41(gem
									.getAantalverzuimenVrouwen_8_41() + 1);
						}
						if ((vzm.getVerzuimduurinperiode().compareTo(
								new BigDecimal(42)) != -1)
								&& (vzm.getVerzuimduurinperiode().compareTo(
										new BigDecimal(91)) != 1)) {
							gem.setAantalverzuimenVrouwen_42_91(gem
									.getAantalverzuimenVrouwen_42_91() + 1);
						}
						if ((vzm.getVerzuimduurinperiode().compareTo(
								new BigDecimal(92)) != -1)
								&& (vzm.getVerzuimduurinperiode().compareTo(
										new BigDecimal(182)) != 1)) {
							gem.setAantalverzuimenVrouwen_92_182(gem
									.getAantalverzuimenVrouwen_92_182() + 1);
						}
						if (vzm.getVerzuimduurinperiode().compareTo(
								new BigDecimal(183)) != -1) {
							gem.setAantalverzuimenVrouwen_183_730(gem
									.getAantalverzuimenVrouwen_183_730() + 1);
						}
						if (!vzm.getStartdatumverzuim().before(startdatum)) {
							gem.setAantalnieuweverzuimenVrouwen(gem
									.getAantalnieuweverzuimenVrouwen() + 1);
						}
						if (vzm.getEinddatumverzuim() != null) {
							gem.setAantalbeeindigdeverzuimenVrouwen(gem
									.getAantalbeeindigdeverzuimenVrouwen() + 1);
							gem.setVerzuimduurVrouwen(gem
									.getVerzuimduurVrouwen().add(
											vzm.getVerzuimduurinperiode()));
						}
					}
				}
			}

			/* Nu nog het herstel verwerken */

			BigDecimal duurinperiode = vzm
					.getVerzuimherstelduurnettoinperiode();
			boolean skipzwangerschap;

			if (inclusiefzwangerschap) {
				skipzwangerschap = false;
			} else {
				if (vzm.getVangnettype().equals(
						__vangnettype.ZIEKDOORZWANGERSCHAP.getValue())
						|| vzm.getVangnettype().equals(
								__vangnettype.ZWANGERSCHAP.getValue())) {
					skipzwangerschap = true;
				} else {
					skipzwangerschap = false;
				}
			}
			if (skipzwangerschap == true)
				;
			else if (vzm.getStartdatumverzuim() != null) {
				if (openVerzuiminPeriode(vzm, startdatum, einddatum)
						&& (vzm.getStartdatumverzuim() != null)) {
					if (vzm.getGeslacht() == __geslacht.MAN.getValue()) {
						if (vzm.getVerzuimduurinperiode().compareTo(
								new BigDecimal(7)) != 1) {
							gem.setVerzuimduurMannen_1_7(gem
									.getVerzuimduurMannen_1_7().add(
											duurinperiode));
						}
						if ((vzm.getVerzuimduurinperiode().compareTo(
								new BigDecimal(8)) != -1)
								&& (vzm.getVerzuimduurinperiode().compareTo(
										new BigDecimal(41)) != 1)) {
							gem.setVerzuimduurMannen_8_41(gem
									.getVerzuimduurMannen_8_41().add(
											duurinperiode));
						}
						if ((vzm.getVerzuimduurinperiode().compareTo(
								new BigDecimal(42)) != -1)
								&& (vzm.getVerzuimduurinperiode().compareTo(
										new BigDecimal(91)) != 1)) {
							gem.setVerzuimduurMannen_42_91(gem
									.getVerzuimduurMannen_42_91().add(
											duurinperiode));
						}
						if ((vzm.getVerzuimduurinperiode().compareTo(
								new BigDecimal(92)) != -1)
								&& (vzm.getVerzuimduurinperiode().compareTo(
										new BigDecimal(182)) != 1)) {
							gem.setVerzuimduurMannen_92_182(gem
									.getVerzuimduurMannen_92_182().add(
											duurinperiode));
						}
						if (vzm.getVerzuimduurinperiode().compareTo(
								new BigDecimal(183)) != -1) {
							gem.setVerzuimduurMannen_183_730(gem
									.getVerzuimduurMannen_183_730().add(
											duurinperiode));
						}

						gem.setVerzuimduurnettoMannen(gem
								.getVerzuimduurnettoMannen().add(duurinperiode));

					} else {
						if (vzm.getVerzuimduurinperiode().compareTo(
								new BigDecimal(7)) != 1) {
							gem.setVerzuimduurVrouwen_1_7(gem
									.getVerzuimduurVrouwen_1_7().add(
											duurinperiode));
						}
						if ((vzm.getVerzuimduurinperiode().compareTo(
								new BigDecimal(8)) != -1)
								&& (vzm.getVerzuimduurinperiode().compareTo(
										new BigDecimal(41)) != 1)) {
							gem.setVerzuimduurVrouwen_8_41(gem
									.getVerzuimduurVrouwen_8_41().add(
											duurinperiode));
						}
						if ((vzm.getVerzuimduurinperiode().compareTo(
								new BigDecimal(42)) != -1)
								&& (vzm.getVerzuimduurinperiode().compareTo(
										new BigDecimal(91)) != 1)) {
							gem.setVerzuimduurVrouwen_42_91(gem
									.getVerzuimduurVrouwen_42_91().add(
											duurinperiode));
						}
						if ((vzm.getVerzuimduurinperiode().compareTo(
								new BigDecimal(92)) != -1)
								&& (vzm.getVerzuimduurinperiode().compareTo(
										new BigDecimal(182)) != 1)) {
							gem.setVerzuimduurVrouwen_92_182(gem
									.getVerzuimduurVrouwen_92_182().add(
											duurinperiode));
						}
						if (vzm.getVerzuimduurinperiode().compareTo(
								new BigDecimal(183)) != -1) {
							gem.setVerzuimduurVrouwen_183_730(gem
									.getVerzuimduurVrouwen_183_730().add(
											duurinperiode));
						}

						gem.setVerzuimduurnettoVrouwen(gem
								.getVerzuimduurnettoVrouwen()
								.add(duurinperiode));
					}
					;
				}
				;
			}
			;

			verzuiminx++;
		}
		// Tenslotte sorteren we de verzuimen op werkgeverid, afdelingid,
		// werknemernaam, verzuimid
		Collections.sort(verzuimen, new verzuimcompare(
				verzuimcompare.__sortcol.achternaam));

		return gemList;
	}

	private VerzuimGemiddelde findAfdelingGemiddelde(
			List<VerzuimGemiddelde> gemList, Integer currentWerkgeverId,
			Integer currentAfdelingId) {
		/*
		 * Opzoeken van de Verzuimgemiddelde voor een specifieke afdeling
		 */
		for (VerzuimGemiddelde g : gemList) {
			if (g.getWerkgeverid().equals(currentWerkgeverId)
					&& g.getAfdelingid().equals(currentAfdelingId))
				return g;
		}
		VerzuimGemiddelde g = new VerzuimGemiddelde();
		g.setAfdelingid(currentAfdelingId);
		g.setWerkgeverid(currentWerkgeverId);
		return g;
	}

	private List<VerzuimGemiddelde> getResultsets(Integer werkgeverid,
			Integer holdingid, Integer oeid, Date startdatum, Date eindatum,
			boolean inclusiefzwangerschap) throws ValidationException, ServiceLocatorException {
		List<WerkgeverInfo> werkgevers;
		try {
			verzuimen = ServiceCaller.reportFacade(loginsession)
					.getActueelVerzuim(werkgeverid, holdingid, oeid, startdatum,
							einddatum, inclusiefzwangerschap);
			verzuimenpermaand = ServiceCaller.reportFacade(loginsession)
					.getVerzuimPerMaand(werkgeverid, holdingid, startdatum,
							einddatum, inclusiefzwangerschap);
			werkgevers = ServiceCaller.reportFacade(loginsession)
					.getWerkgevers(werkgeverid, holdingid);

			List<VerzuimGemiddelde> gemInfoList = getVerzuimGemiddelden(
					verzuimen, startdatum, einddatum, inclusiefzwangerschap,
					werkgevers);

			addOntbrekendeAfdelingen(verzuimen, werkgevers, gemInfoList);
			
			if (verzuimen.isEmpty() || verzuimenpermaand.isEmpty()) {
				throw new ValidationException(
						"Er zijn geen verzuimen gevonden in de opgegeven periode");
			}
			return gemInfoList;

		} catch (PermissionException | VerzuimApplicationException e1) {
			log.error(e1);
		}
		return null;
	}

	private void addOntbrekendeAfdelingen(
			List<ActueelVerzuimInfo> vzm,
			List<WerkgeverInfo> werkgevers, 
			List<VerzuimGemiddelde> gemInfoList) {
		boolean afdelingfound;
		
		if (vzm == null){
			vzm = new ArrayList<>();
		}
		Collections.sort(vzm, this);
		
		for (WerkgeverInfo wgi:werkgevers){
			for (AfdelingInfo ai: wgi.getAfdelings()){
				afdelingfound = false;
				for (ActueelVerzuimInfo vi: vzm){
					if (vi.getAfdelingid().equals(ai.getId())){
						afdelingfound = true;
						break;
					}
				}
				if (!afdelingfound){
					if (!afdelingHasWerknemersinperiode(wgi.getId(), ai.getId(), gemInfoList)){
						log.debug("Ontbrekende afdeling " + ai.getNaam() + " wordt NIET toegevoegd");
						break;
					}
							
					log.debug("Ontbrekende afdeling " + ai.getNaam() + " wordt toegevoegd");
					ActueelVerzuimInfo newavz = new ActueelVerzuimInfo();
					newavz.setAchternaam("");
					newavz.setAfdelingid(ai.getId());
					newavz.setAfdelingnaam(ai.getNaam());
					newavz.setWerkgeverid(wgi.getId());
					newavz.setWerkgevernaam(wgi.getNaam());
					newavz.setWerknemerid(-1);
					newavz.setHoldingid(wgi.getHoldingId());
					if (wgi.getHoldingId() == null){
						newavz.setHoldingnaam("");
					}else{
						newavz.setHoldingnaam(wgi.getHolding().getNaam());
					}
					newavz.setGerelateerdheid(__gerelateerdheid.NVT);
					newavz.setVangnettype(__vangnettype.NVT.getValue());
					newavz.setWerkgeverwerkweek(wgi.getWerkweek());
					vzm.add(newavz);
				}
			}
		}
	}

	private boolean afdelingHasWerknemersinperiode(Integer werkgeverid, Integer afdelingid, List<VerzuimGemiddelde> gemInfoList) {
		for (VerzuimGemiddelde g: gemInfoList){
			if (g.getWerkgeverid().equals(werkgeverid) && 
				g.getAfdelingid().equals(afdelingid)){
				if (g.getAantalmannenStartperiode() > 0  ||
					g.getAantalvrouwenStartperiode() > 0 ||
					g.getAantalmannenEindperiode() > 0 || 
					g.getAantalvrouwenEindperiode() > 0){
					return true;
				}
			}
		}
		return false;
	}

	public JasperPrint getReport(Integer werkgeverid, Integer holdingid,
			Integer oeid, String bedrijfsnaam, boolean inclusiefzwangerschap)
			throws ValidationException, ServiceLocatorException, VerzuimApplicationException {

		try {
			instantieFacade = (InstantieFacadeRemote) ServiceLocator
					.getInstance().getRemoteHome(
							RemoteInterfaces.InstantieFacade.getValue(),
							InstantieFacadeRemote.class);
			instantieFacade.setLoginSession(loginsession);

			List<BedrijfsgegevensInfo> bedrijfsgegevens = new ArrayList<>();
			bedrijfsgegevens.add(instantieFacade.allBedrijfsgegevens().get(0));

			List<VerzuimGemiddelde> avile = getResultsets(werkgeverid,
					holdingid, oeid, startdatum, einddatum,
					inclusiefzwangerschap);
			if (avile == null || avile.isEmpty()) {
				throw new ValidationException(
						"Er zijn geen verzuimen gevonden in de opgegeven periode");
			}

			URL url = JRLoader
					.getResource("verzuim/VerzuimOverzicht.jasper");
			if (url == null) {
				throw new RuntimeException(
						"Kan resource VerzuimOverzicht.jasper niet vinden!");
			}
			String path = new File(url.getFile()).getParent();

			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("SUBREPORT_DIR", path);
			parameters.put(JRParameter.REPORT_LOCALE, new Locale("nl", "NL"));
			parameters.put("startdatum", startdatum);
			parameters.put("einddatum", einddatum);
			parameters.put("Bedrijfsnaam", bedrijfsnaam);
			parameters.put("inclusiefzwangerschap", inclusiefzwangerschap);
			parameters.put("Footer_datasource", bedrijfsgegevens);
			parameters.put("Gemiddelden_datasource", avile);
			parameters.put("Grafiek_datasource", verzuimenpermaand);
			parameters.put("holdingid", holdingid);
			JasperReport report;
			report = (JasperReport) JRLoader.loadObject(url);
			return JasperFillManager.fillReport(report,
					parameters, new JRBeanCollectionDataSource(verzuimen));

		} catch (JRException | PermissionException e) {
			log.error(e);
			return null;
		}

	}

	@Override
	public int compare(ActueelVerzuimInfo arg0, ActueelVerzuimInfo arg1) {
		int i;
		i = arg0.getWerkgeverid().compareTo(arg1.getWerkgeverid());
		if (i != 0)
			return i;

		i = arg0.getAfdelingid().compareTo(arg1.getAfdelingid());
	    return i;
	}

}
