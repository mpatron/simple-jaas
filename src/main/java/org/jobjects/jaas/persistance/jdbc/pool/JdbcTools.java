package org.jobjects.jaas.persistance.jdbc.pool;

/**
 * Cette classe donne des informations du dictionaire de la base de donn�es.
 * @author Micka�l PATRON
 * @version $Id: JdbcInfo.java,v 1.0 2001/05/22 21:10:35 curcuru Exp $
 */

public class JdbcTools
{
  public static String toName(String name) {
    name=name.substring(0,1).toUpperCase()
        +name.substring(1,name.length()).toLowerCase();
    return name;
  }
  //---------------------------------------------------------------------------

  public static String toLowerCase(String name) {
    return name.toLowerCase();
  }
  //---------------------------------------------------------------------------
}