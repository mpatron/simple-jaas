package org.jobjects.jaas.persistance.jdbc.pool;
import java.util.Vector;

/**
 * Cette classe donne des informations du dictionaire de la base de donn�es.
 * @see org.jobjects.dao.jdbc.v2
 * @author Micka�l PATRON
 * @version $Id: ConnectionInfo.java,v 1.0 2001/05/22 21:10:35 curcuru Exp $
 */

public class JdbcSQLTypes
{
  Vector<JdbcType> types = new Vector<JdbcType>();

  public class JdbcType {
    String java_type;
    String jdbc_type;
    String sql_type;
  }
  
  private JdbcSQLTypes instance=null;
  public JdbcSQLTypes getInstace() {
    if(instance==null) {
      instance=new JdbcSQLTypes();
    }
    return instance;
  }
  private JdbcSQLTypes() {
    
    JdbcType type;
    //Oracle8
    
    type = new JdbcType();
    type.java_type="java.lang.Boolean";
    type.jdbc_type="BIT";
    type.sql_type="NUMBER(1)";
    types.add(type);
    
    type = new JdbcType();
    type.java_type="java.lang.Byte";
    type.jdbc_type="NUMERIC";
    type.sql_type="NUMBER(3)";
    types.add(type);
    
    type = new JdbcType();
    type.java_type="java.lang.Short";
    type.jdbc_type="NUMERIC";
    type.sql_type="NUMBER(5)";
    types.add(type);
    
    type = new JdbcType();
    type.java_type="java.lang.Integer";
    type.jdbc_type="INTEGER";
    type.sql_type="NUMBER(10)";
    types.add(type);
    
    type = new JdbcType();
    type.java_type="java.lang.Long";
    type.jdbc_type="BIGINT";
    type.sql_type="NUMBER(19)";
    types.add(type);
    
    type = new JdbcType();
    type.java_type="java.lang.Float";
    type.jdbc_type="REAL";
    type.sql_type="NUMBER(38,7)";
    types.add(type);
    
    type = new JdbcType();
    type.java_type="java.math.BigDecimal";
    type.jdbc_type="DECIMAL";
    type.sql_type="NUMBER(38,15)";
    types.add(type);
    
    type = new JdbcType();
    type.java_type="java.lang.Double";
    type.jdbc_type="DOUBLE";
    type.sql_type="NUMBER(38,15)";
    types.add(type);
    
    type = new JdbcType();
    type.java_type="java.lang.Character";
    type.jdbc_type="CHAR";
    type.sql_type="CHAR";
    types.add(type);
    
    type = new JdbcType();
    type.java_type="java.lang.String";
    type.jdbc_type="VARCHAR";
    type.sql_type="VARCHAR2(255)";
    types.add(type);
    
    type = new JdbcType();
    type.java_type="java.sql.Date";
    type.jdbc_type="DATE";
    type.sql_type="DATE";
    types.add(type);
    
    type = new JdbcType();
    type.java_type="java.sql.Time";
    type.jdbc_type="TIME";
    type.sql_type="DATE";
    types.add(type);
    
    type = new JdbcType();
    type.java_type="java.sql.Timestamp";
    type.jdbc_type="TIMESTAMP";
    type.sql_type="DATE";
    types.add(type);
    
    type = new JdbcType();
    type.java_type="java.lang.Object";
    type.jdbc_type="JAVA_OBJECT";
    type.sql_type="BLOB";
    types.add(type);
  }
}