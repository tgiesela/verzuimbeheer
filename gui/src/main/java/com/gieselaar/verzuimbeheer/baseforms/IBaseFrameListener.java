package com.gieselaar.verzuimbeheer.baseforms;

public interface IBaseFrameListener {
	public enum formCloseAction{
		close,
		save,
		cancel;
	}
	void ClosedAfterSave();
	void ClosedAfterCancel();
	void Closed();
}

