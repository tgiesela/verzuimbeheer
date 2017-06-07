package com.gieselaar.verzuimbeheer.baseforms;

public interface JFrameTGINotification {
	public void rowSelected(Object info);
	public void rowDoubleClicked(Object info);
	public void colSizeChanged(JTableTGI table, int colIndex, int newWidth);
}
