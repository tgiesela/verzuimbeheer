package com.gieselaar.verzuimbeheer.reports;

import java.awt.EventQueue;
import java.sql.*;
import java.util.List;

import com.gieselaar.verzuimbeheer.exceptions.PermissionException;
import com.gieselaar.verzuimbeheer.facades.ReportFacadeRemote;
import com.gieselaar.verzuimbeheer.reportservices.ActueelVerzuimInfo;
import com.gieselaar.verzuimbeheer.utils.ServiceLocator;
import com.gieselaar.verzuimbeheer.utils.ServiceLocatorException;

@SuppressWarnings("unused")
public class VerzuimOverzichtBean {
	private Integer m_holdingId = null;
	private Integer m_werkgeverId = null;
	private ResultSet resultSet = null;
	private Connection connection = null;
	private String connectionURL = "jdbc:mysql://ubuntuglassfish:3306/verzuim"; //"jdbc:odbc:Xtreme";
	private String databaseClass = "sun.jdbc.odbc.JdbcOdbcDriver";
	private String queryAFD = " SELECT * FROM `verzuim`.`AFDELING`";
	private String queryAFD_WN = " SELECT * FROM `verzuim`.`AFDELING_HAS_WN`";
	private String queryDV = " SELECT * FROM `verzuim`.`DIENSTVERBAND`";
	private String queryWN = " SELECT * FROM `verzuim`.`WERKNEMER`";
	private String queryVZ = " SELECT * FROM `verzuim`.`VERZUIM`";
	private String queryVZH = " SELECT * FROM `verzuim`.`VERZUIMHERSTEL`";
	
	private ReportFacadeRemote report;
	public VerzuimOverzichtBean() {
		try {
			// Ensure database class exists
			Class.forName(databaseClass);
			// Create a connection
			connection = DriverManager.getConnection(connectionURL, "root", "Pinocchio_01");
		} 
		catch (ClassNotFoundException ex) {
			System.out.println("Ensure that database driver class is installed.");
			ex.printStackTrace();
		} 
		catch (SQLException ex) {
			System.out.println("SQL Exception #" + ex.getErrorCode() + " : " + ex.getLocalizedMessage());
			ex.printStackTrace();
		}
	}
	public List<ActueelVerzuimInfo> getOpenverzuimen(Integer werkgeverId, Integer HoldingId, Date startdatum, Date einddatum){
		return null ; //report.getOpenverzuimen(werkgeverId, HoldingId, startdatum, einddatum);
	}
	
	/*
	* Any function that returns a java.sql.ResultSet within
	* the Java class can be used in Crystal Reports to return
	* report data. The function can also be parameterized to
	* 'feed' in specific values to the function
	* when querying.
	*/
	public ResultSet getWerkgever() throws SQLException {
		String queryWG = " SELECT * FROM `verzuim`.`WERKGEVER` " +
			     " WHERE `verzuim`.`WERKGEVER`.`id` = " + m_werkgeverId;
		String queryWGH = " SELECT * FROM `verzuim`.`WERKGEVER`" + 
				  " JOIN `verzuim`.`HOLDING` on `verzuim`.`WERKGEVER`.`holding_id` = `verzuim`.`HOLDING`.`id` " +
				  "	WHERE `verzuim`.`HOLDING` = " + m_holdingId;
		// Create an SQL statement to execute
		Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
		if (m_holdingId == null)
			resultSet = statement.executeQuery(queryWG);
		else
			resultSet = statement.executeQuery(queryWGH);
		try {
			report = (ReportFacadeRemote) ServiceLocator
					.getInstance()
					.getRemoteHome(
							"java:global/verzuimbeheerEAR/verzuimbeheerEJB/ReportFacade!com.gieselaar.verzuimbeheer.facades.ReportFacadeRemote",
							ReportFacadeRemote.class);
		} catch (ServiceLocatorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		java.sql.ResultSetMetaData metadat = resultSet.getMetaData();
//		for (int i=1;i<metadat.getColumnCount();i++)
//			System.out.println("Tablename" + metadat.getTableName(i));
		return resultSet;
	}
	public ResultSet getAfdeling() throws SQLException {
		// Create an SQL statement to execute
		Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
		// Execute the select statement to populate the ResultSet
		resultSet = statement.executeQuery(queryAFD);
		return resultSet;
	}
	public ResultSet getAfdelingHasWerknemer() throws SQLException {
		// Create an SQL statement to execute
		Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
		// Execute the select statement to populate the ResultSet
		resultSet = statement.executeQuery(queryAFD_WN);
		return resultSet;
	}
	public ResultSet getWerknemer() throws SQLException {
		// Create an SQL statement to execute
		Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
		// Execute the select statement to populate the ResultSet
		resultSet = statement.executeQuery(queryWN);
		return resultSet;
	}
	public ResultSet getVerzuim() throws SQLException {
		// Create an SQL statement to execute
		Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
		// Execute the select statement to populate the ResultSet
		resultSet = statement.executeQuery(queryVZ);
		return resultSet;
	}
	public ResultSet getVerzuimherstel() throws SQLException {
		// Create an SQL statement to execute
		Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
		// Execute the select statement to populate the ResultSet
		resultSet = statement.executeQuery(queryVZH);
		return resultSet;
	}
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					//VerzuimOverzichtBean bean = new VerzuimOverzichtBean(234, null);
					//ResultSet set = bean.getResultSet();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

}
