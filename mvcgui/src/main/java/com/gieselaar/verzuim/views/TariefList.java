package com.gieselaar.verzuim.views;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import com.gieselaar.verzuim.controllers.TariefController;
import com.gieselaar.verzuim.controllers.TariefController.__tariefcommands;
import com.gieselaar.verzuim.controllers.TariefController.__tarieffields;
import com.gieselaar.verzuim.viewsutils.ColorTableModel;
import com.gieselaar.verzuim.viewsutils.CursorController;
import com.gieselaar.verzuimbeheer.services.TariefInfo;
import javax.swing.JCheckBox;

public class TariefList extends AbstractList {


	private static final long serialVersionUID = 1L;
	private TariefController tariefcontroller;
	/**
	 * Create the frame.
	 */
	public TariefList(TariefController controller) {
		super("Tarieven overzicht", controller);
		tariefcontroller = controller;
		initialize();

		addColumn(__tarieffields.WERKGEVER.getValue(),"Werkgever",120, Date.class);
		addColumn(__tarieffields.INGANGSDATUM.getValue(),"Ingangsdatum",80, Date.class);
		addColumn(__tarieffields.EINDDATUM.getValue(),"Einddatum",60, Date.class);
		addColumn(__tarieffields.ABONNEMENT.getValue(),"Abonnement",60, BigDecimal.class);
		addColumn(__tarieffields.PERIODEABONNEMENT.getValue(),"Periode",60);
		addColumn(__tarieffields.AANSLUITKOSTEN.getValue(),"Aansluitkosten", 60, BigDecimal.class);
		addColumn(__tarieffields.PERIODEAANSLUITKOSTEN.getValue(),"Periode", 60 );
		
		List<RowSorter.SortKey> sortKeys;
		sortKeys = new ArrayList<>();
        sortKeys.add(new RowSorter.SortKey(0, SortOrder.ASCENDING));
        
        this.getDatatable().getSorter().setSortKeys(sortKeys);
        this.getDatatable().getSorter().sort();
	
	}

	public void initialize() {

		JCheckBox cbAfgeslotenTonen = new JCheckBox("Afgesloten tarieven tonen");
		cbAfgeslotenTonen.setSelected(false);
		cbAfgeslotenTonen.setBounds(297, 422, 104, 23);
		cbAfgeslotenTonen.setActionCommand(__tariefcommands.TARIEFAFGESLOTENTONEN.toString());
		cbAfgeslotenTonen.addActionListener(CursorController.createListener(this,controller));
		super.getDatatable().getPanelAction().add(cbAfgeslotenTonen);
		
	}

	@Override
	public void refreshTable() {
		List<TariefInfo> tarieven;
		tarieven = tariefcontroller.getTarievenList();

		/* Save and remove sorts and filters before populating the table */
		this.getDatatable().disableSorter();

		tariefcontroller.getTableModel(tarieven, (ColorTableModel) this.getTable().getModel(), colsinview);

		/* Add sorts and filters again */
		this.getDatatable().enableSorter();
	}
}
