package com.gieselaar.verzuimbeheer.verzuimweb;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import org.primefaces.model.menu.DefaultMenuItem;
import org.primefaces.model.menu.DefaultMenuModel;
import org.primefaces.model.menu.MenuModel;

import com.gieselaar.verzuimbeheer.facades.LoginSessionRemote;

@ManagedBean
@SessionScoped
public class panelMenuClass {

	private LoginSessionRemote loginsession;
	private MenuModel model;
	public panelMenuClass(){
	}
	@PostConstruct
	public void init(){
		FacesContext context = FacesContext.getCurrentInstance();
		loginClass sessioncontext = (loginClass) context.getApplication()
				.evaluateExpressionGet(context, "#{loginClass}",
						loginClass.class);
		loginsession = sessioncontext.loginSession;
		
		model = new DefaultMenuModel();
	}
	private void addHome(){
		DefaultMenuItem item = new DefaultMenuItem();
		item.setValue("Verzuimbeheer");
		item.setCommand("#{panelMenuClass.navigateHome}");
		model.addElement(item);	
	}
	private void addWerkgeverDetail(){
		DefaultMenuItem item = new DefaultMenuItem();
		item.setValue("Werkgever");
		item.setCommand("#{panelMenuClass.navigateWerkgeverDetail}");
		model.addElement(item);	
	}
	private void addAfdelingen(){
		DefaultMenuItem item = new DefaultMenuItem();
		item.setValue("Afdelingen");
		item.setCommand("#{panelMenuClass.navigateAfdelingen}");
		model.addElement(item);	
	}
	private void addAfdeling(){
		DefaultMenuItem item = new DefaultMenuItem();
		item.setValue("Afdeling");
		item.setCommand("#{panelMenuClass.navigateAfdeling}");
		model.addElement(item);	
	}
	private void addWerknemers(){
		DefaultMenuItem item = new DefaultMenuItem();
		item.setValue("Werknemers");
		item.setCommand("#{panelMenuClass.navigateWerknemers}");
		model.addElement(item);	
	}
	private void addWerknemerDetail(){
		DefaultMenuItem item = new DefaultMenuItem();
		item.setValue("Werknemer");
		item.setCommand("#{panelMenuClass.navigateWerknemerDetail}");
		model.addElement(item);
	}
	private void addWerknemerVerzuimen(){
		DefaultMenuItem item = new DefaultMenuItem();
		item.setValue("Verzuimen");
		item.setCommand("#{panelMenuClass.navigateVerzuimen()}");
		model.addElement(item);
	}
	private void addWerknemerVerzuimDetail(){
		DefaultMenuItem item = new DefaultMenuItem();
		item.setValue("Verzuim");
		item.setCommand("#{panelMenuClass.navigateVerzuimDetail}");
		model.addElement(item);
	}
	/*
	private void addWerknemerVerzuimHerstel(){
		DefaultMenuItem item = new DefaultMenuItem();
		item.setValue("Herstel");
		item.setCommand("#{panelMenuClass.navigateVerzuimHerstel}");
		model.addElement(item);
	}
	*/
	private void addGebruiker(){
		DefaultMenuItem item = new DefaultMenuItem();
		item.setValue("Gebruiker");
		item.setCommand("#{panelMenuClass.navigateGebruiker}");
		model.addElement(item);	
	}
	private void addWachtWoordwijzigen(){
		DefaultMenuItem item = new DefaultMenuItem();
		item.setValue("Wachtwoord");
		item.setCommand("#{panelMenuClass.navigateWachtwoordWijzigen}");
		model.addElement(item);	
	}
	private void addVerzuimhistorie() {
		DefaultMenuItem item = new DefaultMenuItem();
		item.setValue("Verzuim historie");
		item.setCommand("#{panelMenuClass.navigateVerzuimhistorie()}");
		model.addElement(item);	
	}
	private void addVerzuimstatistiek() {
		DefaultMenuItem item = new DefaultMenuItem();
		item.setValue("Verzuim statistiek");
		item.setCommand("#{panelMenuClass.navigateVerzuimstatistiek()}");
		model.addElement(item);	
	}
	private void addWerknemerInformatiekaart() {
		DefaultMenuItem item = new DefaultMenuItem();
		item.setValue("Informatiekaart");
		item.setCommand("#{panelMenuClass.navigateVerzuimInformatieKaart()}");
		model.addElement(item);	
	}
	private void addWerknemerDocumenten() {
		DefaultMenuItem item = new DefaultMenuItem();
		item.setValue("Documenten");
		item.setCommand("#{panelMenuClass.navigateVerzuimDocumenten()}");
		model.addElement(item);	
	}
	public String navigateIndex(){
		model = new DefaultMenuModel();
		return "/index.xhtml?faces-redirect=true";
	}
	public String navigateHome(){
		model = new DefaultMenuModel();
		addHome();
		return "/main.xhtml?faces-redirect=true";
	}
	public String navigateWerkgeverDetail(){
		model = new DefaultMenuModel();
		addHome();
		addWerkgeverDetail();
		return "/werkgeverDetail.xhtml?faces-redirect=true";
	}
	public String navigateAfdelingen(){
		model = new DefaultMenuModel();
		addHome();
		addWerkgeverDetail();
		addAfdelingen();
		return "/afdelingen.xhtml?faces-redirect=true";
	}
	public String navigateAfdeling(){
		model = new DefaultMenuModel();
		addHome();
		addWerkgeverDetail();
		addAfdelingen();
		addAfdeling();
		return "/afdelingdetail.xhtml?faces-redirect=true";
	}
	public String navigateWerknemers(){
		model = new DefaultMenuModel();
		
		addHome();
		addWerknemers();
		return "/werknemers.xhtml?faces-redirect=true";
	}
	public String navigateWerknemerDetail(){
		model = new DefaultMenuModel();
		
		addHome();
		addWerknemers();
		addWerknemerDetail();
		return "/werknemerDetail.xhtml?faces-redirect=true";
	}
	public String navigateVerzuimhistorie() {
		model = new DefaultMenuModel();

		addHome();
		addWerknemers();
		addWerknemerDetail();
		addVerzuimhistorie();
		return "/verzuimhistorie.xhtml?faces-redirect=true";
	}	
	public String navigateVerzuimstatistiek() {
		model = new DefaultMenuModel();

		addHome();
		addWerkgeverDetail();
		addVerzuimstatistiek();
		return "/verzuimstatistiek.xhtml?faces-redirect=true";
	}	
	public String navigateVerzuimen(){
		model = new DefaultMenuModel();
		
		addHome();
		addWerknemers();
		addWerknemerDetail();
		addWerknemerVerzuimen();
		return "/werknemerverzuimen?faces-redirect=true";
	}
	public String navigateVerzuimDetail(){
		model = new DefaultMenuModel();
		
		addHome();
		addWerknemers();
		addWerknemerDetail();
		addWerknemerVerzuimen();
		addWerknemerVerzuimDetail();
		return "/verzuimDetail.xhtml?faces-redirect=true";
	}
	public String navigateVerzuimInformatieKaart() {
		model = new DefaultMenuModel();
		
		addHome();
		addWerknemers();
		addWerknemerDetail();
		addWerknemerVerzuimen();
		addWerknemerVerzuimDetail();
		addWerknemerInformatiekaart();
		return "/informatiekaart.xhtml?faces-redirect=true";
	}
	public String navigateVerzuimDocumenten() {
		model = new DefaultMenuModel();
		
		addHome();
		addWerknemers();
		addWerknemerDetail();
		addWerknemerVerzuimen();
		addWerknemerVerzuimDetail();
		addWerknemerDocumenten();
		return "/verzuimdocumenten.xhtml?faces-redirect=true";
	}
	public String navigateGebruiker(){
		model = new DefaultMenuModel();
		addHome();
		addGebruiker();
		return "/gebruiker.xhtml?faces-redirect=true";
	}
	public String navigateWachtwoordWijzigen(){
		model = new DefaultMenuModel();
		addHome();
		addWachtWoordwijzigen();
		return "/wachtwoordWijzigen.xhtml?faces-redirect=true";
	}
	public String logoff(){
		FacesContext context = FacesContext.getCurrentInstance();
		loginClass sessioncontext = (loginClass) context.getApplication()
				.evaluateExpressionGet(context, "#{loginClass}",
						loginClass.class);
		sessioncontext.TerminateSession();
		navigateIndex();
		return "loggedoff.xhtml?faces-redirect=true";
	}
	public void addMessage(String summary, String detail) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, summary, detail);
        FacesContext.getCurrentInstance().addMessage(null, message);
    }
	public MenuModel getModel() {
		return model;
	}
	public void setModel(MenuModel model) {
		this.model = model;
	}
}
