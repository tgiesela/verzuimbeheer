package reportdatasources;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.gieselaar.verzuimbeheer.services.GebruikerInfo;
import com.gieselaar.verzuimbeheer.services.HoldingInfo;
import com.gieselaar.verzuimbeheer.services.WerkgeverInfo;
import com.gieselaar.verzuimbeheer.services.WerkzaamhedenInfo;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

public class WerkzaamhedenDatasource {

	private List<WerkzaamhedenInfoReport> werkzaamhedenreport = new ArrayList<WerkzaamhedenInfoReport>();
	private HashMap<Integer, WerkgeverInfo> hmWerkgever = new HashMap<>();
	private HashMap<Integer, HoldingInfo> hmHolding = new HashMap<>();
	private HashMap<Integer, String> hmGebruiker = new HashMap<>();
	private List<WerkgeverInfo> werkgevers = null;
	private List<HoldingInfo> holdings = null;
	public WerkzaamhedenDatasource(List<WerkgeverInfo> werkgevers, List<GebruikerInfo> gebruikers, List<HoldingInfo> holdings){
		this.werkgevers = werkgevers;
		this.holdings = holdings;
		for (WerkgeverInfo wgi: this.werkgevers){
			hmWerkgever.put(wgi.getId(), wgi);
		}
		
		for (HoldingInfo hi: this.holdings){
			hmHolding.put(hi.getId(), hi);
		}

		for (GebruikerInfo gi: gebruikers){
			hmGebruiker.put(gi.getId(), gi.getVoornaam() + " " + gi.getAchternaam());
		}
	}
	public JRDataSource getDataSource(List<WerkzaamhedenInfo> werkzaamheden, Integer gebruiker, Integer werkgever, Integer holding) {
		for (WerkzaamhedenInfo wi: werkzaamheden){
			WerkzaamhedenInfoReport wir = new WerkzaamhedenInfoReport();
			wir.setWerkzaamheid(wi);
			WerkgeverInfo w = (WerkgeverInfo)hmWerkgever.get(wi.getWerkgeverid());
			if (holding.equals(-1)){
				wir.setWerkgevernaam(w.getNaam());
			}else{
				HoldingInfo h = (HoldingInfo)hmHolding.get(w.getHoldingId());
				wir.setWerkgevernaam(h.getNaam());
			}
			wir.setGebruikernaam(hmGebruiker.get(wi.getUserid()));
			werkzaamhedenreport.add(wir);
		}
		return new JRBeanCollectionDataSource(werkzaamhedenreport);
	}
}
