package org.jobjects.jaas.persistance.jdbc.pool;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.util.Iterator;
import java.util.Properties;

/**
 * Cette classe donne des informations du dictionaire de la base de donn�es.
 * @author Micka�l PATRON
 * @version $Id: JdbcInfo.java,v 1.0 2001/05/22 21:10:35 curcuru Exp $
 */

public class JdbcTest
{
  public static void main(String args[]) throws Exception
  {
    String driverClassName = "org.jobjects.dao.jdbc.v2.JdbcDriver";
    String url = "jdbc:jobjects:pool:AQUILON";

    Driver driver = (Driver)Class.forName(driverClassName).newInstance();
    DriverManager.registerDriver(driver);

    //Connection connection = DriverManager.getConnection(url);
    Properties info = new Properties();
    info.setProperty("t1", "valeur1");
    //Connection connection = DriverManager.getConnection(url, info);
    Connection connection = DriverManager.getConnection(url);
    /*
    Statement stmt = connection.createStatement();
    ResultSet rs = stmt.executeQuery("select sysdate from dual");
    JdbcInfo.Affiche(rs);
    rs.close();
    stmt.close();
    */
    //JdbcInfo.Affiche(connection.getMetaData().getImportedKeys("", "CATALOG", "PRODUCT_DATES"));
    JdbcInfo jdbcinfo = new JdbcInfo(connection, "CATALOG");
    String tablename = "PRODUCTS";
    System.out.println(connection.getMetaData().getProcedureTerm());
    
    Iterator<JdbcField> it = jdbcinfo.getColumns(tablename.toUpperCase()).iterator();
    while (it.hasNext()) {
      JdbcField jdbcField = (JdbcField) it.next();
      System.out.println(jdbcField.getName());
    }
    
//    Enumeration e = jdbcinfo.getColumns(tablename.toUpperCase()).elements();
//    while(e.hasMoreElements()) {
//      JdbcField jdbcfield=(JdbcField)e.nextElement();
//      System.out.println(jdbcfield.getName());
//    }
    
    connection.close();
    DriverManager.deregisterDriver(driver);
  }
}