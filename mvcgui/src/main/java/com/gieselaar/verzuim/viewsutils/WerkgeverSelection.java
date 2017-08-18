package com.gieselaar.verzuim.viewsutils;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import com.gieselaar.verzuim.controllers.MDIMainController;
import com.gieselaar.verzuim.interfaces.WerkgeverNotification;
import com.gieselaar.verzuimbeheer.services.HoldingInfo;
import com.gieselaar.verzuimbeheer.services.WerkgeverInfo;
import com.gieselaar.verzuimbeheer.utils.TypeEntry;

import java.awt.EventQueue;
import java.util.Date;
import java.util.List;
public class WerkgeverSelection extends JPanel{

	private static final long serialVersionUID = 4264160456357805052L;

	private WerkgeverNotification eventNotifier;

	private JComboBox<TypeEntry> cmbHolding;
	private JComboBox<TypeEntry> cmbWerkgever;
	private VerzuimComboBoxModel werkgeverModel;
	private VerzuimComboBoxModel holdingModel;
	private JPanel panelWerkgeverSelection;
	private WerkgeverInfo werkgever = null;
	private HoldingInfo holding = null;
	private List<WerkgeverInfo> werkgevers;
	private List<HoldingInfo> holdings;
	private Date startperiode;
	private Date eindperiode;
	public enum __WerkgeverSelectionMode{
		SelectOne,
		SelectBoth;
	}
	public enum __WerkgeverFilter{
		None,
		Actief,
		Factureren
	}
	private __WerkgeverSelectionMode selectionmode = __WerkgeverSelectionMode.SelectBoth;
	private __WerkgeverFilter filtermode = __WerkgeverFilter.None;

	private Integer selectedHolding;
	private Integer selectedWerkgever;
	private MDIMainController maincontroller;
	public WerkgeverSelection(__WerkgeverSelectionMode mode, __WerkgeverFilter filter) {
		super();
		selectionmode = mode;
		filtermode = filter;
		startperiode = eindperiode = new Date();
		initialize();
	}
	public WerkgeverSelection(__WerkgeverSelectionMode mode, __WerkgeverFilter filter, Date start, Date eind) {
		super();
		selectionmode = mode;
		filtermode = filter;
		startperiode = start;
		eindperiode = eind;
		initialize();
	}
	public void initialize(){
		setLayout(null);
		panelWerkgeverSelection = new JPanel();
		panelWerkgeverSelection.setBorder(new TitledBorder(null, null, TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panelWerkgeverSelection.setBounds(0, 1, 371, 54);
		this.add(panelWerkgeverSelection);
		panelWerkgeverSelection.setLayout(null);

		JLabel lblStraatVA = new JLabel("Holding");
		lblStraatVA.setBounds(10, 9, 76, 14);
		panelWerkgeverSelection.add(lblStraatVA);

		cmbHolding = new JComboBox<>();
		cmbHolding.setBounds(96, 6, 265, 20);
		cmbHolding.addPopupMenuListener(new PopupMenuListener() {
			
			@Override
			public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
			}
			
			@Override
			public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
				EventQueue.invokeLater(new Runnable() {
					@Override
					public void run() {
						HoldingSelected();
					}

					private void HoldingSelected() {
						holdingModel = (VerzuimComboBoxModel)cmbHolding.getModel();
						Integer holding = holdingModel.getId();
						if (!holding.equals(selectedHolding) &&
							eventNotifier != null && eventNotifier.holdingSelected(holding)){
							return;
						}
						selectedHolding = holding;
						if (selectedHolding != -1){
							if (selectionmode == __WerkgeverSelectionMode.SelectBoth){
								/*
								 * Werkgever en holding mogen allebei geselecteerd worden
								 * Werkgever moet wel bij holding horen
								 */
								populateWerkgevers(selectedHolding);
							}else{
								/*
								 * Holding selected. cmbWerkgever moet op nvt([]) worden gezet.
								 */
								werkgeverModel = (VerzuimComboBoxModel)cmbWerkgever.getModel();
								werkgeverModel.setId(-1);
							}
						}else{
							/*
							 * Holding niet meer geselecteerd
							 */
							populateWerkgevers(null);
						}
					}
				});
			}
			
			@Override
			public void popupMenuCanceled(PopupMenuEvent e) {
			}
		});
		panelWerkgeverSelection.add(cmbHolding);
			
		cmbWerkgever = new JComboBox<>();
		cmbWerkgever.setBounds(96, 29, 265, 20);
		cmbWerkgever.setModel(new VerzuimComboBoxModel(maincontroller));
		cmbWerkgever.addPopupMenuListener(new PopupMenuListener() {
			
			@Override
			public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
			}
			
			@Override
			public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
				EventQueue.invokeLater(new Runnable() {
					@Override
					public void run() {
						WerkgeverSelected();
					}

					private void WerkgeverSelected() {
						Integer werkgever = ((VerzuimComboBoxModel)cmbWerkgever.getModel()).getId();
						Integer savedwerkgever = selectedWerkgever;
						Integer savedholding = selectedHolding;
						setWerkgeverId(werkgever);
						if (selectedWerkgever != -1){
							if (selectionmode == __WerkgeverSelectionMode.SelectBoth){
								for (WerkgeverInfo wg: werkgevers){
									if (wg.getId().equals(selectedWerkgever)){
										if (wg.getHoldingId() != null){
											setHoldingId(wg.getHoldingId());
											holdingModel.setId(wg.getHoldingId());
											break;
										}
									}
								}
							}else{
								/*
								 * Werkgever selected. cmbHolding moet op nvt([]) worden gezet.
								 */
								holdingModel = (VerzuimComboBoxModel)cmbHolding.getModel();
								for (int i=0;i<holdingModel.getSize();i++)
								{
									TypeEntry holdingtype = holdingModel.getElementAt(i);
									if (holdingtype.getValue() == -1) {
										setHoldingId(selectedHolding);
										holdingModel.setSelectedItem(holdingtype);
										break;
									}
								}
							}
						}
						/*
						 * Notify form that item has changed. If not allowed, all values will be reset to
						 * original settings
						 */
						if (!werkgever.equals(savedwerkgever) &&
							eventNotifier != null && 
							eventNotifier.werkgeverSelected(werkgever)){
							setWerkgeverId(savedwerkgever);
							setHoldingId(savedholding);
							return;
						}
					}
				});
			}
			
			@Override
			public void popupMenuCanceled(PopupMenuEvent e) {
			}
		});
		panelWerkgeverSelection.add(cmbWerkgever);
		
		JLabel lblWerkgever = new JLabel("Werkgever");
		lblWerkgever.setBounds(10, 32, 76, 14);
		panelWerkgeverSelection.add(lblWerkgever);
		
		cmbWerkgever.setEnabled(false);
		cmbHolding.setEnabled(false);
	}
	public WerkgeverInfo getWerkgever() {
		if (selectedWerkgever == null || selectedWerkgever.intValue() == -1)
			return null;
		for (WerkgeverInfo w: werkgevers){
			if (w.getId().intValue() == selectedWerkgever.intValue()){
				return w;
			}
		}
		return werkgever;
	}
	public Integer getWerkgeverId(){
		WerkgeverInfo w = getWerkgever();
		if (w == null){
			return null;
		}else{
			return w.getId();
		}
	}
	public void setWerkgeverId(Integer id){
		werkgeverModel = (VerzuimComboBoxModel) cmbWerkgever.getModel();
		werkgeverModel.setId(id);
		selectedWerkgever = id;
	}
	public void setWerkgever(WerkgeverInfo werkgever) {
		this.werkgever = werkgever;
		setWerkgeverId(this.werkgever.getId());
	}
	public HoldingInfo getHolding() {
		if (selectedHolding == null){
			return null;
		}
		if (selectedHolding.intValue() == -1){
			return null;
		}
		for (HoldingInfo h:holdings){
			if (h.getId().intValue() == selectedHolding.intValue()){
				return h;
			}
		}
		return holding;
	}
	public Integer getHoldingId(){
		HoldingInfo h = getHolding();
		if (h == null){
			return null;
		}else{
			return h.getId();
		}
	}
	public void setHoldingId(Integer id){
		holdingModel = (VerzuimComboBoxModel) cmbHolding.getModel();
		holdingModel.setId(id);
		selectedHolding = id;
	}
	public void setHolding(HoldingInfo holding) {
		this.holding = holding;
		setHoldingId(this.holding.getId());
	}
	public Date getStartperiode() {
		return startperiode;
	}
	public void setStartperiode(Date startperiode) {
		this.startperiode = startperiode;
	}
	public Date getEindperiode() {
		return eindperiode;
	}
	public void setEindperiode(Date eindperiode) {
		this.eindperiode = eindperiode;
	}
	private void populateWerkgevers(Integer holdingId){
		boolean select;
		werkgeverModel = new VerzuimComboBoxModel(maincontroller);
		cmbWerkgever.setModel(werkgeverModel);
		selectedWerkgever = -1;
		werkgeverModel.addElement(new TypeEntry(-1,"[]"));
		WerkgeverInfo.sort(werkgevers, WerkgeverInfo.__sortcol.NAAM);
		for (WerkgeverInfo w: werkgevers)
		{
			select = true;
			if (selectionmode == __WerkgeverSelectionMode.SelectBoth){
				if (filtermode == __WerkgeverFilter.Actief &&
					(!(w.isActief(startperiode) || w.isActief(eindperiode)))){
					select = false;
				}
				if (holdingId != null && holdingId != -1){
					if (w.getHoldingId() == null){
						select = false;
					}else{
						if (!w.getHoldingId().equals(holdingId)){
							select = false;
						}
					}
				} else {
					if (filtermode == __WerkgeverFilter.Factureren && !w.getFactureren()){
						select = false;
					}
				}
				if (select) {
					TypeEntry wg = new TypeEntry(w.getId(), w.getNaam());
					werkgeverModel.addElement(wg);
				}
			}else{
				
				if (filtermode == __WerkgeverFilter.Factureren && !w.getFactureren()){
					select = false;
				}
				if (filtermode == __WerkgeverFilter.Actief && !w.isActief()){
					select = false;
				}
				if (select) {
					TypeEntry wg = new TypeEntry(w.getId(), w.getNaam());
					werkgeverModel.addElement(wg);
				}
			}
		}
	}
	private void setWerkgevers(List<WerkgeverInfo> werkgevers) {
		this.werkgevers = werkgevers;
		cmbWerkgever.setEnabled(true);
		populateWerkgevers(null);
	}
	private void setHoldings(List<HoldingInfo> holdings) {
		boolean select;
		this.holdings = holdings;
		cmbHolding.setEnabled(true);
		holdingModel = new VerzuimComboBoxModel(maincontroller);
		cmbHolding.setModel(holdingModel);
		selectedHolding = -1;
		holdingModel.addElement(new TypeEntry(-1,"[]"));
		HoldingInfo.sort(holdings, HoldingInfo.__sortcol.NAAM);
		for (HoldingInfo h : holdings) {
			select = true;
			if (filtermode == __WerkgeverFilter.Factureren && !h.isFactureren()){
				select = false;
			}
			if (select) {
				TypeEntry hi = new TypeEntry(h.getId(), h.getNaam());
				holdingModel.addElement(hi);
			}
		}
	}
	public WerkgeverNotification getEventNotifier() {
		return eventNotifier;
	}
	public void setEventNotifier(WerkgeverNotification eventNotifier) {
		this.eventNotifier = eventNotifier;
	}
	public void setMaincontroller(MDIMainController maincontroller) {
		this.maincontroller = maincontroller;
		holdings = maincontroller.getHoldings();
		werkgevers = maincontroller.getWerkgevers();
		setWerkgevers(werkgevers);
		setHoldings(holdings);
	}
}
