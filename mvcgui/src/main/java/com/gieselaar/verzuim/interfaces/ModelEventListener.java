package com.gieselaar.verzuim.interfaces;

public interface ModelEventListener {
	public abstract void listComplete(Object data);
	public abstract void rowUpdated(Object data);
	public abstract void rowAdded(Object data);
	public abstract void rowDeleted(Object data);
}
