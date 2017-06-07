package reportdatasources;

import com.gieselaar.verzuimbeheer.reportservices.ActueelVerzuimInfo;

public class ActueelVerzuimInfoExtended {
	private ActueelVerzuimInfo actueelverzuim;
	private Integer aantalditjaar;
	private Integer aantal12maanden;
	public Integer getAantalditjaar() {
		return aantalditjaar;
	}
	public void setAantalditjaar(Integer aantalditjaar) {
		this.aantalditjaar = aantalditjaar;
	}
	public Integer getAantal12maanden() {
		return aantal12maanden;
	}
	public void setAantal12maanden(Integer aantal12maanden) {
		this.aantal12maanden = aantal12maanden;
	}
	public ActueelVerzuimInfo getActueelverzuim() {
		return actueelverzuim;
	}
	public void setActueelverzuim(ActueelVerzuimInfo actueelverzuim) {
		this.actueelverzuim = actueelverzuim;
	}
	
	
}
