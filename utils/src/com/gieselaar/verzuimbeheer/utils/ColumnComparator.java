package com.gieselaar.verzuimbeheer.utils;

import java.util.Comparator;

public class ColumnComparator implements Comparator<Object> {
	  protected boolean isSortAsc;

	  public ColumnComparator( boolean sortAsc) {
	    isSortAsc = sortAsc;
	  }

	  public int compare(Object o1, Object o2) {
		  if (!(o1 instanceof Integer) || !(o2 instanceof Integer))
			  return 0;
		  Integer s1 = (Integer) o1;
		  Integer s2 = (Integer) o2;
		  int result = 0;
		  result = s1.compareTo(s2);
		  if (!isSortAsc)
			  result = -result;
		  return result;
	  }

	  public boolean equals(Object obj) {
		  if (obj instanceof ColumnComparator) {
			  ColumnComparator compObj = (ColumnComparator) obj;
			  return compObj.isSortAsc == isSortAsc;
		  }
		  return false;
	  }
}
