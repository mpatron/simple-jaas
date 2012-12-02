package org.jobjects.jaas.persistance.jdbc.pool;

/**
 * Cette classe donne des informations du dictionaire de la base de donn�es.
 * @see org.jobjects.dao.jdbc.v2
 * @author Micka�l PATRON
 * @version $Id: ConnectionInfo.java,v 1.0 2001/05/22 21:10:35 curcuru Exp $
 */

public class JdbcField
{
  private String column_name;
  private int data_type;
  private boolean isNullable;
  private String java_type;
  private String preparedstatement;
  private int position;
  //---------------------------------------------------------------------------

  public final static String BOOLEAN="java.lang.Boolean";
  public final static String INTEGER="java.lang.Integer";
  public final static String LONG="java.lang.Long";
  public final static String FLOAT="java.lang.Float";
  public final static String DOUBLE="java.lang.Double";
  public final static String STRING="java.lang.String";
  //public final static String DATE="java.util.Date";
  public final static String DATE="java.sql.Timestamp";

  public final static String PREPAREDSTATEMENT_BOOLEAN="Boolean";
  public final static String PREPAREDSTATEMENT_INTEGER="Int";
  public final static String PREPAREDSTATEMENT_LONG="Long";
  public final static String PREPAREDSTATEMENT_FLOAT="Float";
  public final static String PREPAREDSTATEMENT_DOUBLE="Double";
  public final static String PREPAREDSTATEMENT_STRING="String";
  //public final static String PREPAREDSTATEMENT_DATE="Date";
  public final static String PREPAREDSTATEMENT_DATE="Timestamp";
  //---------------------------------------------------------------------------
  public JdbcField(String COLUMN_NAME,
                          short DATA_TYPE,
                          int COLUMN_SIZE,
                          int DECIMAL_DIGITS,
                          int NULLABLE,
                          int ORDINAL_POSITION)
  {
    this.column_name=COLUMN_NAME;
    this.data_type=DATA_TYPE;
    this.isNullable=(NULLABLE==1);
    this.position=ORDINAL_POSITION;
    switch(data_type)
    {
      case java.sql.Types.DECIMAL: //3
        if(DECIMAL_DIGITS==0)
        {
          if(COLUMN_SIZE<10)
          {
            if(COLUMN_SIZE==1) {
              java_type=JdbcField.BOOLEAN;
              preparedstatement=JdbcField.PREPAREDSTATEMENT_BOOLEAN;
            } else {
              java_type=JdbcField.INTEGER;
              preparedstatement=JdbcField.PREPAREDSTATEMENT_INTEGER;
            }
          } else {
            java_type=JdbcField.LONG;
            preparedstatement=JdbcField.PREPAREDSTATEMENT_LONG;
          }
        }
        else
        {
          if(COLUMN_SIZE<10) {
            java_type=JdbcField.FLOAT;
            preparedstatement=JdbcField.PREPAREDSTATEMENT_FLOAT;
          } else {
            java_type=JdbcField.DOUBLE;
            preparedstatement=JdbcField.PREPAREDSTATEMENT_DOUBLE;
          }
        }

      break;
      case java.sql.Types.VARCHAR: //12
        java_type=JdbcField.STRING;
        preparedstatement=JdbcField.PREPAREDSTATEMENT_STRING;
      break;
      case java.sql.Types.TIMESTAMP: //93
        java_type=JdbcField.DATE;
        preparedstatement=JdbcField.PREPAREDSTATEMENT_DATE;
      break;
      default:
        java_type=JdbcField.STRING;
        preparedstatement=JdbcField.PREPAREDSTATEMENT_STRING;
    }
  }
  //---------------------------------------------------------------------------

  public String getName()
  {
    return this.column_name;
  }
  //---------------------------------------------------------------------------

  public int getType()
  {
    return this.data_type;
  }
  //---------------------------------------------------------------------------

  public String getJavaType()
  {
    return this.java_type;
  }
  //---------------------------------------------------------------------------

  public String getPreparedStatementFct()
  {
    return this.preparedstatement;
  }
  //---------------------------------------------------------------------------

  public boolean isNullable()
  {
    return this.isNullable;
  }
  //---------------------------------------------------------------------------
  
  public int getPosition()
  {
    return this.position;
  }
  //---------------------------------------------------------------------------
}
