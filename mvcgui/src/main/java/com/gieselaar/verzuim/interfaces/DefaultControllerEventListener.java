package com.gieselaar.verzuim.interfaces;

import com.gieselaar.verzuim.controllers.AbstractController.__formmode;
import com.gieselaar.verzuimbeheer.services.InfoBase;

public class DefaultControllerEventListener implements ControllerEventListener {

	@Override
	public void refreshTable() {/* noop */}

	@Override
	public void setDetailFormmode(__formmode mode) {/* noop */}

	@Override
	public void setData(InfoBase info) {/* noop */}
	
	@Override
	public void close() {/* noop */}
	
	@Override
	public void rowSelected(int selectedRow, Object data) {/* noop */}

	@Override
	public void formClosed(ControllerEventListener ves) {
	}

}
