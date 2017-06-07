package com.gieselaar.verzuimbeheer.components;

import java.util.List;
import javax.faces.model.DataModel;

public class PagedListDataModel extends DataModel<Object>{

private int rowIndexInView = -1;
private int totalNumRows;
private int pageSize;
private List<?> list;
private int pageIndex = 1;

	public PagedListDataModel() {
		super();
	}
	private int rowIndexViewToModelIndex(){
		return ((pageIndex - 1) * pageSize + rowIndexInView);
	}
	
	public PagedListDataModel(List<?> list, int totalNumRows, int pageSize) {
		super();
		setWrappedData(list);
		this.totalNumRows = totalNumRows;
		this.pageSize = pageSize;
		this.pageIndex = 1;
	}
	public void setPageNumber(int pagenumber){
		if (pagenumber > ((totalNumRows-1) / pageSize)+1)
			pageIndex = ((totalNumRows-1) / pageSize)+1;
		else
			if (pagenumber < 1)
				pageIndex = 1;
			else
				pageIndex = pagenumber;
	}
	public String nextPage(){
		setPageNumber(pageIndex+1);
		return "";
	}
	public String prevPage(){
		setPageNumber(pageIndex-1);
		return "";
	}
	public int getPageNumber(){
		return pageIndex;
	}
	public boolean isRowAvailable() {
		if (list == null)
			return false;
	
		int rowIndex = rowIndexViewToModelIndex();
		if(rowIndex >=0 && rowIndex < list.size() && rowIndexInView < pageSize)
			return true;
		else
			return false;
	}
	@Override
	public int getRowCount() {
		return pageSize;
	}
	
	public Object getRowData() {
		if(list == null)
			return null;
		else if(!isRowAvailable())
			throw new IllegalArgumentException();
		else {
			if (rowIndexInView > pageSize)
				return null;
			else{
				int dataIndex = rowIndexViewToModelIndex();
				return list.get(dataIndex);
			}
		}
	}
	
	public int getRowIndex() {
		return rowIndexInView;
	}
	
	public void setRowIndex(int rowIndex) {
		this.rowIndexInView = rowIndex;
	}
	
	public Object getWrappedData() {
		return list;
	}
	
	public void setWrappedData(Object list) {
		this.list = (List<?>) list;
	}
}