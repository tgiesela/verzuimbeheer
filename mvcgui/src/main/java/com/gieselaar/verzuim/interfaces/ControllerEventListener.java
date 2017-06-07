package com.gieselaar.verzuim.interfaces;

import com.gieselaar.verzuim.controllers.AbstractController.__formmode;
import com.gieselaar.verzuimbeheer.services.InfoBase;

public interface ControllerEventListener {
	public abstract void refreshTable();
	public abstract void setDetailFormmode(__formmode mode);
	public abstract void setData(InfoBase info);
	public abstract void close();
	public abstract void rowSelected(int selectedRow, Object data);
	public abstract void formClosed(ControllerEventListener ves);
}
