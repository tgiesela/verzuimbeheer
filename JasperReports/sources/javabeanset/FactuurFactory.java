package javabeanset;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import com.gieselaar.verzuimbeheer.services.BedrijfsgegevensInfo;
import com.gieselaar.verzuimbeheer.services.FactuurTotaalInfo;
import com.gieselaar.verzuimbeheer.services.FactuurregelbezoekInfo;
import com.gieselaar.verzuimbeheer.services.TariefInfo.__tariefperiode;
import com.gieselaar.verzuimbeheer.services.WerkgeverInfo;
import com.gieselaar.verzuimbeheer.services.WerkzaamhedenInfo;

public class FactuurFactory {

	public static Collection<FactuurTotaalInfo> generateCollection() {
		Vector<FactuurTotaalInfo> collection = new Vector<FactuurTotaalInfo>();
		//FactuurTotaalInfo fti = new FactuurTotaalInfo();
		FactuurTotaalInfo factuur = new FactuurTotaalInfo(); 
		factuur.setAanmaakdatum(new Date());
		factuur.setAansluitkosten(BigDecimal.ZERO);
		factuur.setAansluitkostenperiode(__tariefperiode.JAAR);
		factuur.setAantalmedewerkers(1);
		factuur.setAbonnementskosten(BigDecimal.ONE);
		factuur.setFactuurnr(2005001);
		factuur.setJaar(2005);
		factuur.setMaand(4);
		factuur.setOmschrijvingfactuur("");
		collection.add(factuur);
		return collection;
	}

	public static List<BedrijfsgegevensInfo> getBedrijfsgegevens(){
		List<BedrijfsgegevensInfo> bedrijfsgegevens = new ArrayList<>();
		BedrijfsgegevensInfo info = new BedrijfsgegevensInfo();
		info.setNaam("De Vos verzuimbeheer");
		bedrijfsgegevens.add(info);
		return bedrijfsgegevens;
	}
	public static List<WerkgeverInfo> getWerkgever(){
		List<WerkgeverInfo> bedrijfsgegevens = new ArrayList<>();
		WerkgeverInfo info = new WerkgeverInfo();
		info.setNaam("De Vos verzuimbeheer");
		bedrijfsgegevens.add(info);
		return bedrijfsgegevens;
	}
	public static List<FactuurregelbezoekInfo> getfactuurregelbezoek(){
		List<FactuurregelbezoekInfo> factuurregels = new ArrayList<>();
		FactuurregelbezoekInfo info = new FactuurregelbezoekInfo();
		info.setCasemanagementkosten(BigDecimal.ZERO);
		info.setFactuurid(1);
		info.setId(1);
		info.setKilometerkosten(BigDecimal.ZERO);
		info.setKilometertarief(BigDecimal.ZERO);
		info.setOverigekosten(BigDecimal.ZERO);
		info.setUurkosten(BigDecimal.ZERO);
		info.setUurtarief(BigDecimal.ZERO);
		info.setVastekosten(BigDecimal.ZERO);
		WerkzaamhedenInfo wi = new WerkzaamhedenInfo();
		info.setWerkzaamheden(wi);
		info.setWerkzaamhedenid(0);
		factuurregels.add(info);
		return factuurregels;
	}
}
