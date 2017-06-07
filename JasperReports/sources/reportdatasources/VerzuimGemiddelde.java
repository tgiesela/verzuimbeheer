package reportdatasources;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class VerzuimGemiddelde {

	private int aantalmannenStartperiode;
	private int aantalvrouwenStartperiode;
	private int aantalmannenEindperiode;
	private int aantalvrouwenEindperiode;
	private BigDecimal urenmannenStartperiode;
	private BigDecimal urenmannenEindperiode;
	private BigDecimal urenmannenPeriode;
	private BigDecimal urenvrouwenStartperiode;
	private BigDecimal urenvrouwenEindperiode;
	private BigDecimal urenvrouwenPeriode;
	private int aantalverzuimenMannen;
	private int aantalverzuimenVrouwen;
	private int aantalnieuweverzuimenMannen;
	private int aantalnieuweverzuimenVrouwen;
	private int aantalbeeindigdeverzuimenMannen;
	private int aantalbeeindigdeverzuimenVrouwen;
	private int aantalverzuimenMannen_1_7;
	private int aantalverzuimenVrouwen_1_7;
	private int aantalverzuimenMannen_8_41;
	private int aantalverzuimenVrouwen_8_41;
	private int aantalverzuimenMannen_42_91;
	private int aantalverzuimenVrouwen_42_91;
	private int aantalverzuimenMannen_92_182;
	private int aantalverzuimenVrouwen_92_182;
	private int aantalverzuimenMannen_183_730;
	private int aantalverzuimenVrouwen_183_730;
	private BigDecimal verzuimduurMannen_1_7;
	private BigDecimal verzuimduurVrouwen_1_7;
	private BigDecimal verzuimduurMannen_8_41;
	private BigDecimal verzuimduurVrouwen_8_41;
	private BigDecimal verzuimduurMannen_42_91;
	private BigDecimal verzuimduurVrouwen_42_91;
	private BigDecimal verzuimduurMannen_92_182;
	private BigDecimal verzuimduurVrouwen_92_182;
	private BigDecimal verzuimduurMannen_183_730;
	private BigDecimal verzuimduurVrouwen_183_730;
	private BigDecimal verzuimduurMannen;
	private BigDecimal verzuimduurVrouwen;
	private BigDecimal verzuimduurnettoMannen;
	private BigDecimal verzuimduurnettoVrouwen;
	
	private Integer werkgeverid;
	private Integer afdelingid;
	private Integer holdingid;
	private BigDecimal werkgeverWerkweek;
	private String werkgevernaam;
	private String afdelingnaam;
	private String holdingnaam;

	public VerzuimGemiddelde(){
		verzuimduurMannen_1_7 = new BigDecimal(0);
		verzuimduurVrouwen_1_7 = new BigDecimal(0);
		verzuimduurMannen_8_41 = new BigDecimal(0);
		verzuimduurVrouwen_8_41 = new BigDecimal(0);
		verzuimduurMannen_42_91 = new BigDecimal(0);
		verzuimduurVrouwen_42_91 = new BigDecimal(0);
		verzuimduurMannen_92_182 = new BigDecimal(0);
		verzuimduurVrouwen_92_182 = new BigDecimal(0);
		verzuimduurMannen_183_730 = new BigDecimal(0);
		verzuimduurVrouwen_183_730 = new BigDecimal(0);
		verzuimduurMannen = new BigDecimal(0);
		verzuimduurVrouwen = new BigDecimal(0);
		verzuimduurnettoMannen = new BigDecimal(0);
		verzuimduurnettoVrouwen = new BigDecimal(0);
		werkgeverWerkweek = new BigDecimal(0);
		urenmannenStartperiode = new BigDecimal(0);
		urenmannenEindperiode = new BigDecimal(0);
		urenvrouwenStartperiode = new BigDecimal(0);
		urenvrouwenEindperiode = new BigDecimal(0);
		urenmannenPeriode = new BigDecimal(0);
		urenvrouwenPeriode = new BigDecimal(0);
		
		werkgeverid = -1;
		afdelingid = -1;
		holdingid = -1;
		
	}
	public int getAantalmannenStartperiode() {
		return aantalmannenStartperiode;
	}
	public void setAantalmannenStartperiode(int aantalmannenStartperiode) {
		this.aantalmannenStartperiode = aantalmannenStartperiode;
	}
	public int getAantalvrouwenStartperiode() {
		return aantalvrouwenStartperiode;
	}
	public void setAantalvrouwenStartperiode(int aantalvrouwenStartperiode) {
		this.aantalvrouwenStartperiode = aantalvrouwenStartperiode;
	}
	public int getAantalmannenEindperiode() {
		return aantalmannenEindperiode;
	}
	public void setAantalmannenEindperiode(int aantalmannenEindperiode) {
		this.aantalmannenEindperiode = aantalmannenEindperiode;
	}
	public int getAantalvrouwenEindperiode() {
		return aantalvrouwenEindperiode;
	}
	public void setAantalvrouwenEindperiode(int aantalvrouwenEindperiode) {
		this.aantalvrouwenEindperiode = aantalvrouwenEindperiode;
	}
	public BigDecimal getUrenmannenStartperiode() {
		return urenmannenStartperiode;
	}
	public void setUrenmannenStartperiode(BigDecimal urenmannenStartperiode) {
		this.urenmannenStartperiode = urenmannenStartperiode;
	}
	public BigDecimal getUrenmannenEindperiode() {
		return urenmannenEindperiode;
	}
	public void setUrenmannenEindperiode(BigDecimal urenmannenEindperiode) {
		this.urenmannenEindperiode = urenmannenEindperiode;
	}
	public BigDecimal getUrenmannenPeriode() {
		return urenmannenPeriode;
	}
	public void setUrenmannenPeriode(BigDecimal urenmannenPeriode) {
		this.urenmannenPeriode = urenmannenPeriode;
	}
	public BigDecimal getUrenvrouwenStartperiode() {
		return urenvrouwenStartperiode;
	}
	public void setUrenvrouwenStartperiode(BigDecimal urenvrouwenStartperiode) {
		this.urenvrouwenStartperiode = urenvrouwenStartperiode;
	}
	public BigDecimal getUrenvrouwenEindperiode() {
		return urenvrouwenEindperiode;
	}
	public void setUrenvrouwenEindperiode(BigDecimal urenvrouwenEindperiode) {
		this.urenvrouwenEindperiode = urenvrouwenEindperiode;
	}
	public BigDecimal getUrenvrouwenPeriode() {
		return urenvrouwenPeriode;
	}
	public void setUrenvrouwenPeriode(BigDecimal urenvrouwenPeriode) {
		this.urenvrouwenPeriode = urenvrouwenPeriode;
	}
	public int getAantalverzuimenMannen() {
		return aantalverzuimenMannen;
	}
	public void setAantalverzuimenMannen(int aantalverzuimenMannen) {
		this.aantalverzuimenMannen = aantalverzuimenMannen;
	}
	public int getAantalverzuimenVrouwen() {
		return aantalverzuimenVrouwen;
	}
	public void setAantalverzuimenVrouwen(int aantalverzuimenVrouwen) {
		this.aantalverzuimenVrouwen = aantalverzuimenVrouwen;
	}
	public int getAantalnieuweverzuimenMannen() {
		return aantalnieuweverzuimenMannen;
	}
	public void setAantalnieuweverzuimenMannen(int aantalnieuweverzuimenMannen) {
		this.aantalnieuweverzuimenMannen = aantalnieuweverzuimenMannen;
	}
	public int getAantalnieuweverzuimenVrouwen() {
		return aantalnieuweverzuimenVrouwen;
	}
	public void setAantalnieuweverzuimenVrouwen(int aantalnieuweverzuimenVrouwen) {
		this.aantalnieuweverzuimenVrouwen = aantalnieuweverzuimenVrouwen;
	}
	public int getAantalbeeindigdeverzuimenMannen() {
		return aantalbeeindigdeverzuimenMannen;
	}
	public void setAantalbeeindigdeverzuimenMannen(
			int aantalbeeindigdeverzuimenMannen) {
		this.aantalbeeindigdeverzuimenMannen = aantalbeeindigdeverzuimenMannen;
	}
	public int getAantalbeeindigdeverzuimenVrouwen() {
		return aantalbeeindigdeverzuimenVrouwen;
	}
	public void setAantalbeeindigdeverzuimenVrouwen(
			int aantalbeeindigdeverzuimenVrouwen) {
		this.aantalbeeindigdeverzuimenVrouwen = aantalbeeindigdeverzuimenVrouwen;
	}
	public int getAantalverzuimenMannen_1_7() {
		return aantalverzuimenMannen_1_7;
	}
	public void setAantalverzuimenMannen_1_7(int aantalverzuimenMannen_1_7) {
		this.aantalverzuimenMannen_1_7 = aantalverzuimenMannen_1_7;
	}
	public int getAantalverzuimenVrouwen_1_7() {
		return aantalverzuimenVrouwen_1_7;
	}
	public void setAantalverzuimenVrouwen_1_7(int aantalverzuimenVrouwen_1_7) {
		this.aantalverzuimenVrouwen_1_7 = aantalverzuimenVrouwen_1_7;
	}
	public int getAantalverzuimenMannen_8_41() {
		return aantalverzuimenMannen_8_41;
	}
	public void setAantalverzuimenMannen_8_41(int aantalverzuimenMannen_8_41) {
		this.aantalverzuimenMannen_8_41 = aantalverzuimenMannen_8_41;
	}
	public int getAantalverzuimenVrouwen_8_41() {
		return aantalverzuimenVrouwen_8_41;
	}
	public void setAantalverzuimenVrouwen_8_41(int aantalverzuimenVrouwen_8_41) {
		this.aantalverzuimenVrouwen_8_41 = aantalverzuimenVrouwen_8_41;
	}
	public int getAantalverzuimenMannen_42_91() {
		return aantalverzuimenMannen_42_91;
	}
	public void setAantalverzuimenMannen_42_91(int aantalverzuimenMannen_42_91) {
		this.aantalverzuimenMannen_42_91 = aantalverzuimenMannen_42_91;
	}
	public int getAantalverzuimenVrouwen_42_91() {
		return aantalverzuimenVrouwen_42_91;
	}
	public void setAantalverzuimenVrouwen_42_91(int aantalverzuimenVrouwen_42_91) {
		this.aantalverzuimenVrouwen_42_91 = aantalverzuimenVrouwen_42_91;
	}
	public int getAantalverzuimenMannen_92_182() {
		return aantalverzuimenMannen_92_182;
	}
	public void setAantalverzuimenMannen_92_182(int aantalverzuimenMannen_92_182) {
		this.aantalverzuimenMannen_92_182 = aantalverzuimenMannen_92_182;
	}
	public int getAantalverzuimenVrouwen_92_182() {
		return aantalverzuimenVrouwen_92_182;
	}
	public void setAantalverzuimenVrouwen_92_182(int aantalverzuimenVrouwen_92_182) {
		this.aantalverzuimenVrouwen_92_182 = aantalverzuimenVrouwen_92_182;
	}
	public int getAantalverzuimenMannen_183_730() {
		return aantalverzuimenMannen_183_730;
	}
	public void setAantalverzuimenMannen_183_730(int aantalverzuimenMannen_183_730) {
		this.aantalverzuimenMannen_183_730 = aantalverzuimenMannen_183_730;
	}
	public int getAantalverzuimenVrouwen_183_730() {
		return aantalverzuimenVrouwen_183_730;
	}
	public void setAantalverzuimenVrouwen_183_730(int aantalverzuimenVrouwen_183_730) {
		this.aantalverzuimenVrouwen_183_730 = aantalverzuimenVrouwen_183_730;
	}
	public BigDecimal getVerzuimduurMannen_1_7() {
		return verzuimduurMannen_1_7;
	}
	public void setVerzuimduurMannen_1_7(BigDecimal verzuimduurMannen_1_7) {
		this.verzuimduurMannen_1_7 = verzuimduurMannen_1_7;
	}
	public BigDecimal getVerzuimduurVrouwen_1_7() {
		return verzuimduurVrouwen_1_7;
	}
	public void setVerzuimduurVrouwen_1_7(BigDecimal verzuimduurVrouwen_1_7) {
		this.verzuimduurVrouwen_1_7 = verzuimduurVrouwen_1_7;
	}
	public BigDecimal getVerzuimduurMannen_8_41() {
		return verzuimduurMannen_8_41;
	}
	public void setVerzuimduurMannen_8_41(BigDecimal verzuimduurMannen_8_41) {
		this.verzuimduurMannen_8_41 = verzuimduurMannen_8_41;
	}
	public BigDecimal getVerzuimduurVrouwen_8_41() {
		return verzuimduurVrouwen_8_41;
	}
	public void setVerzuimduurVrouwen_8_41(BigDecimal verzuimduurVrouwen_8_41) {
		this.verzuimduurVrouwen_8_41 = verzuimduurVrouwen_8_41;
	}
	public BigDecimal getVerzuimduurMannen_42_91() {
		return verzuimduurMannen_42_91;
	}
	public void setVerzuimduurMannen_42_91(BigDecimal verzuimduurMannen_42_91) {
		this.verzuimduurMannen_42_91 = verzuimduurMannen_42_91;
	}
	public BigDecimal getVerzuimduurVrouwen_42_91() {
		return verzuimduurVrouwen_42_91;
	}
	public void setVerzuimduurVrouwen_42_91(BigDecimal verzuimduurVrouwen_42_91) {
		this.verzuimduurVrouwen_42_91 = verzuimduurVrouwen_42_91;
	}
	public BigDecimal getVerzuimduurMannen_92_182() {
		return verzuimduurMannen_92_182;
	}
	public void setVerzuimduurMannen_92_182(BigDecimal verzuimduurMannen_92_182) {
		this.verzuimduurMannen_92_182 = verzuimduurMannen_92_182;
	}
	public BigDecimal getVerzuimduurVrouwen_92_182() {
		return verzuimduurVrouwen_92_182;
	}
	public void setVerzuimduurVrouwen_92_182(BigDecimal verzuimduurVrouwen_92_182) {
		this.verzuimduurVrouwen_92_182 = verzuimduurVrouwen_92_182;
	}
	public BigDecimal getVerzuimduurMannen_183_730() {
		return verzuimduurMannen_183_730;
	}
	public void setVerzuimduurMannen_183_730(BigDecimal verzuimduurMannen_183_730) {
		this.verzuimduurMannen_183_730 = verzuimduurMannen_183_730;
	}
	public BigDecimal getVerzuimduurVrouwen_183_730() {
		return verzuimduurVrouwen_183_730;
	}
	public void setVerzuimduurVrouwen_183_730(BigDecimal verzuimduurVrouwen_183_730) {
		this.verzuimduurVrouwen_183_730 = verzuimduurVrouwen_183_730;
	}
	public BigDecimal getVerzuimduurnettoMannen() {
		return verzuimduurnettoMannen;
	}
	public void setVerzuimduurnettoMannen(BigDecimal verzuimduurnettoMannen) {
		this.verzuimduurnettoMannen = verzuimduurnettoMannen;
	}
	public BigDecimal getVerzuimduurnettoVrouwen() {
		return verzuimduurnettoVrouwen;
	}
	public void setVerzuimduurnettoVrouwen(BigDecimal verzuimduurnettoVrouwen) {
		this.verzuimduurnettoVrouwen = verzuimduurnettoVrouwen;
	}
	public BigDecimal getVerzuimduurMannen() {
		return verzuimduurMannen;
	}
	public void setVerzuimduurMannen(BigDecimal verzuimduurMannen) {
		this.verzuimduurMannen = verzuimduurMannen;
	}
	public BigDecimal getVerzuimduurVrouwen() {
		return verzuimduurVrouwen;
	}
	public void setVerzuimduurVrouwen(BigDecimal verzuimduurVrouwen) {
		this.verzuimduurVrouwen = verzuimduurVrouwen;
	}
	public Integer getWerkgeverid() {
		return werkgeverid;
	}
	public void setWerkgeverid(Integer werkgeverid) {
		this.werkgeverid = werkgeverid;
	}
	public Integer getAfdelingid() {
		return afdelingid;
	}
	public void setAfdelingid(Integer afdelingid) {
		this.afdelingid = afdelingid;
	}
	public Integer getHoldingid() {
		return holdingid;
	}
	public void setHoldingid(Integer holdingid) {
		this.holdingid = holdingid;
	}
	
	
	public BigDecimal getWerkgeverWerkweek() {
		return werkgeverWerkweek;
	}
	public void setWerkgeverWerkweek(BigDecimal werkgeverWerkweek) {
		this.werkgeverWerkweek = werkgeverWerkweek;
	}
	public BigDecimal getGemFTEMannen(){
		//return (urenmannenStartperiode.add(urenmannenEindperiode).divide(new BigDecimal(2)).divide(werkgeverWerkweek,2,RoundingMode.HALF_UP));
		return (urenmannenPeriode.divide(werkgeverWerkweek,2,RoundingMode.HALF_UP));
	}
	public BigDecimal getGemFTEVrouwen(){
		//return (urenvrouwenStartperiode.add(urenvrouwenEindperiode).divide(new BigDecimal(2)).divide(werkgeverWerkweek,2,RoundingMode.HALF_UP));
		return (urenvrouwenPeriode.divide(werkgeverWerkweek,2,RoundingMode.HALF_UP));		
	}
	public Integer getGemAantalMannen(){
		return (aantalmannenStartperiode+aantalmannenEindperiode)/2;
	}
	public Integer getGemAantalVrouwen(){
		return (aantalvrouwenStartperiode+aantalvrouwenEindperiode)/2;
	}
	public BigDecimal getGemVerzuimduurMannen(){
		if (aantalbeeindigdeverzuimenMannen > 0)
			return (verzuimduurMannen.divide(new BigDecimal(aantalbeeindigdeverzuimenMannen),2,RoundingMode.HALF_UP));
		else
			return new BigDecimal(0);
	}
	public BigDecimal getGemVerzuimduurVrouwen(){
		if (aantalbeeindigdeverzuimenVrouwen > 0)
			return (verzuimduurVrouwen.divide(new BigDecimal(aantalbeeindigdeverzuimenVrouwen),2,RoundingMode.HALF_UP));
		else
			return new BigDecimal(0);
	}
	public String getWerkgevernaam() {
		return werkgevernaam;
	}
	public void setWerkgevernaam(String werkgevernaam) {
		this.werkgevernaam = werkgevernaam;
	}
	public String getAfdelingnaam() {
		return afdelingnaam;
	}
	public void setAfdelingnaam(String afdelingnaam) {
		this.afdelingnaam = afdelingnaam;
	}
	public String getHoldingnaam() {
		return holdingnaam;
	}
	public void setHoldingnaam(String holdingnaam) {
		this.holdingnaam = holdingnaam;
	}
}
