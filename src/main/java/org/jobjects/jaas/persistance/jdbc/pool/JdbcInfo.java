package org.jobjects.jaas.persistance.jdbc.pool;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.Map.Entry;

/**
 * Cette classe donne des informations du dictionaire de la base de données.
 * @author Mickaël PATRON
 * @version $Id: JdbcInfo.java,v 1.0 2001/05/22 21:10:35 curcuru Exp $
 */

public class JdbcInfo
{
  private DatabaseMetaData metadata=null;
  private String schemaName=null;
  //---------------------------------------------------------------------------

 /**
   * Constructeur.
   * @param conn est la connection vers la base de donnée.
   * @param schemaName est le nom du schema de la base de donnée.
   */
  public JdbcInfo(Connection conn, String schemaName) throws SQLException
  {
    this.metadata = conn.getMetaData();
    this.schemaName = schemaName;
  }
  //---------------------------------------------------------------------------

 /**
   * La liste des tables parentes de la table
   * passée en paramètre. {@link #getChildTables(String) getComponentAt}
   * @param tableName est le nom de la table
   * @return un Vector contenant le nom des tables sous la forme d'éléments
   * de type String
   * @throws SQLException peut être lancé pour un problème de connexion.
   */
  public List<String> getParentTables(String tableName) throws SQLException
  {
    List<String> returnValue = new Vector<String>();
    ResultSet rs = metadata.getImportedKeys(null, schemaName, tableName);
    String chaine;
    while(rs.next())
    {
      chaine = rs.getString("FKTABLE_NAME");
      if(!returnValue.contains(chaine)) returnValue.add(chaine);
    }
    rs.close();
    return returnValue;
  }
  //---------------------------------------------------------------------------

 /**
   * Liste des tables enfantes de la table
   * passée en paramètre.
   * @param tableName est le nom de la table
   * @return un Vector contenant le nom des tables sous la forme d'éléments
   * de type String
   * @throws SQLException peut être lancé pour un problème de connexion.
   */
  public List<String> getChildTables(String tableName) throws SQLException
  {
    List<String> returnValue = new Vector<String>();
    ResultSet rs = metadata.getExportedKeys(null, schemaName, tableName);
    String chaine;
    while(rs.next())
    {
      chaine = rs.getString("FKTABLE_NAME");
      if(!returnValue.contains(chaine)) returnValue.add(chaine);
    }
    rs.close();
    return returnValue;
  }
  //---------------------------------------------------------------------------

 /**
   * Liste des noms des champs de la clef primaire
   * @param tableName est le nom de la table
   * @return un Vector contenant le nom des champs sous la forme d'éléments
   * de type String
   * @throws SQLException peut être lancé pour un problème de connexion.
   */
  public List<String> getPrimaryColumns(String tableName) throws SQLException
  {
    String chaine;
    List<String> returnValue = new Vector<String>();
    ResultSet rs = metadata.getPrimaryKeys(null, schemaName, tableName);
    while(rs.next())
    {
      chaine = rs.getString("COLUMN_NAME");
      if(!returnValue.contains(chaine)) returnValue.add(chaine);
    }
    rs.close();
    return returnValue;
  }
  //---------------------------------------------------------------------------

 /**
   * Liste des noms des champs de la clef primaire
   * qui provienneent des tables parentes.
   * @param tableName est le nom de la table
   * @return un Vector contenant le nom des champs sous la forme d'éléments
   * de type String
   * @throws SQLException peut être lancé pour un problème de connexion.
   */
  public List<String> getImportedPrimaryColumns(String tableName) throws SQLException
  {
    List<String> returnValue = new Vector<String>();
    ResultSet rs = metadata.getImportedKeys(null, schemaName, tableName);
    String chaine;
    while(rs.next())
    {
      chaine = rs.getString("FKCOLUMN_NAME");
      if(!returnValue.contains(chaine)) returnValue.add(chaine);
    }
    rs.close();
    return returnValue;
  }
  //---------------------------------------------------------------------------

 /**
   * Liste des noms des champs nom null.
   * @param tableName est le nom de la table
   * @return un Vector contenant le nom des tables sous la forme d'éléments
   * de type String
   * @throws SQLException peut être lancé pour un problème de connexion.
   */
  public List<String> getMandatoryColumns(String tableName) throws SQLException
  {
    List<String> returnValue = new Vector<String>();
    try
    {
      ResultSet rs = metadata.getIndexInfo(null, schemaName, tableName, false, true);  // SQLException ici
      String buffer;
      while(rs.next())
      { if(!rs.getBoolean("NON_UNIQUE"))
        { buffer = rs.getString("INDEX_NAME");
          if(buffer != null)
          { if(!buffer.equals(""))
            { returnValue.add(rs.getString("COLUMN_NAME"));
            }
          }
        }
      }
      rs.close();
    } catch( SQLException e) { e.printStackTrace(); }
    return returnValue;
  }
  //---------------------------------------------------------------------------

 /**
   * Liste des noms des champs de la clef primaire
   * qui sont propre à la table et qui ne font pas réference aux tables parentes.
   * @param tableName est le nom de la table
   * @return un Vector contenant le nom des tables sous la forme d'éléments
   * de type String
   * @throws SQLException peut être lancé pour un problème de connexion.
   */
  public List<String> getOwnPrimaryColumns(String tableName) throws SQLException
  {
    String chaine;

    Vector<String> Imported_v = new Vector<String>();
    try
    {
      ResultSet Imported_rs = metadata.getImportedKeys(null, schemaName, tableName);
      while(Imported_rs.next())
      {
        chaine = Imported_rs.getString("FKCOLUMN_NAME");
        if(!Imported_v.contains(chaine)) Imported_v.add(chaine);
      }
      Imported_rs.close();
    } catch (Exception e) { e.printStackTrace(); }

    Vector<String> returnValue = new Vector<String>();
    try
    {
      ResultSet rs = metadata.getPrimaryKeys(null, schemaName, tableName);
      while(rs.next())
      {
        chaine = rs.getString("COLUMN_NAME");
        if(!returnValue.contains(chaine)) returnValue.add(chaine);
      }
      rs.close();
    } catch (Exception e) { e.printStackTrace(); }

    @SuppressWarnings( "unchecked" )
    Vector<String> clone = (Vector<String>)returnValue.clone();
    int length = clone.size();
    for(int i=0; i <length; i++)
    {
      chaine = (String)clone.get(i);
      if(Imported_v.contains(chaine)) returnValue.removeElement(chaine);
    }
    return returnValue;
  }
  //---------------------------------------------------------------------------

 /**
   * Liste des noms des champs.
   * @param tableName est le nom de la table
   * @return un Vector contenant le nom des champs sous la forme d'éléments
   * de type String
   * @throws SQLException peut être lancé pour un problème de connexion.
   */
  public List<String> getColumnNames(String tableName) throws SQLException
  {
    List<String> returnValue = new Vector<String>();
    ResultSet rs = metadata.getColumns(null, schemaName, tableName, "%");
    String chaine;
    while(rs.next())
    {
      chaine = rs.getString("COLUMN_NAME");
      if(!returnValue.contains(chaine)) returnValue.add(chaine);
    }
    rs.close();
    return returnValue;
  }
  //---------------------------------------------------------------------------

 /**
   * Liste des noms des tables.
   * @return un Vector contenant le nom des tables sous la forme d'éléments
   * de type String
   * @throws SQLException peut être lancé pour un problème de connexion.
   */
  public List<String> getTables() throws SQLException
  {
    List<String> returnValue = new Vector<String>();
    ResultSet rs = metadata.getTables(null, schemaName, "%", new String[]{"TABLE"});
    String chaine;
    while(rs.next())
    {
      chaine = rs.getString("TABLE_NAME");
      if(!returnValue.contains(chaine)) returnValue.add(chaine);
    }
    rs.close();
    return returnValue;
  }
  //---------------------------------------------------------------------------

 /**
   * Affiche sur la sortie standard le contenu du RecordSet.
   * @param rs est le RecordSet à Afficher
   * @return rien.
   */
  public static void Affiche(ResultSet rs)
  {
    try
    {
      Vector<String[]> lignes = new Vector<String[]>();

      ResultSetMetaData rsmd = rs.getMetaData();
      int colcount = rsmd.getColumnCount();

      String[] chaine = new String[colcount];
      int[] colsize = new int[colcount];

      for(int i=1; i<=colcount; i++)
      {
        chaine[i-1] = rsmd.getColumnName(i);
        colsize[i-1] =  chaine[i-1].length();
      }
      lignes.add(chaine);


      while(rs.next())
      {
        chaine = new String[colcount];
        for(int i=1; i<=colcount; i++)
        {
          chaine[i-1] = rs.getString(i);
          if(chaine[i-1] != null)
          {
            if(colsize[i-1] < chaine[i-1].length())
            {
              colsize[i-1] =  chaine[i-1].length();
            }
          }
        }
        lignes.add(chaine);
      }
      rs.close();

      int diff;
      for(int i=0; i<lignes.size(); i++)
      {
        chaine = (String[])lignes.get(i);
        for(int j=0; j<chaine.length; j++)
        {
          if(chaine[j]==null) chaine[j] = "Null";
          diff = colsize[j] - chaine[j].length();
          System.out.print(chaine[j] + " ");
          for(int k=0; k<diff; k++) System.out.print(" ");
          System.out.print(" ");
        }
        System.out.println("");
      }
    }
    catch(Exception e) { e.printStackTrace(System.err); }
  }
  //------------------------------------------------------------------------------

 /**
   * Liste des noms des champs.
   * @param tableName est le nom de la table
   * @return un Vector contenant les champs de la tables sous la forme d'éléments
   * de type ConnectionField
   * @throws SQLException peut être lancé pour un problème de connexion.
   */
  public List<JdbcField> getColumns(String tableName) throws SQLException
  {
    List<JdbcField> returnValue = new Vector<JdbcField>();
    ResultSet rs = metadata.getColumns(null, schemaName, tableName, "%");
    while(rs.next())
    {
      JdbcField cf = new JdbcField(rs.getString("COLUMN_NAME"),
                          (short)rs.getInt("DATA_TYPE"),
                          rs.getInt("COLUMN_SIZE"),
                          rs.getInt("DECIMAL_DIGITS"),
                          rs.getInt("NULLABLE"),
                          rs.getInt("ORDINAL_POSITION"));
      returnValue.add(cf);
    }
    rs.close();
    return returnValue;
  }
  //----------------------------------------------------------------------------------------------------

  public Map<String, JdbcField> getFieldsColumn(String tableName) throws SQLException
  {
    Hashtable<String, JdbcField> returnValue = new Hashtable<String, JdbcField>();
    ResultSet rs = metadata.getColumns(null, schemaName, tableName, "%");
    while(rs.next())
    {
      String name=rs.getString("COLUMN_NAME");
      JdbcField cf = new JdbcField(name,
                          (short)rs.getInt("DATA_TYPE"),
                          rs.getInt("COLUMN_SIZE"),
                          rs.getInt("DECIMAL_DIGITS"),
                          rs.getInt("NULLABLE"),
                          rs.getInt("ORDINAL_POSITION") );
      returnValue.put(name, cf);
    }
    rs.close();
    return returnValue;
  }
  //----------------------------------------------------------------------------------------------------
  
  public Map<String, JdbcField> getFieldsPrimaryColumn(String tableName) throws SQLException
  {
    String chaine;
    Map<String, JdbcField> fields = getFieldsColumn(tableName);
    Map<String, JdbcField> returnValue = new Hashtable<String, JdbcField>();
    ResultSet rs = metadata.getPrimaryKeys(null, schemaName, tableName);
    while(rs.next())
    {
      chaine = rs.getString("COLUMN_NAME");
      returnValue.put(chaine, fields.get(chaine));
    }
    rs.close();
    return returnValue;
  }
  //----------------------------------------------------------------------------------------------------

  public Map<String, JdbcField> getFieldsNotNullableColumn(String tableName) throws SQLException
  {
    Map<String, JdbcField> returnValue = new Hashtable<String, JdbcField>();
    Map<String, JdbcField> fields = getFieldsColumn(tableName);
    try
    {
      ResultSet rs = metadata.getIndexInfo(null, schemaName, tableName, false, true);  // SQLException ici
      String buffer;
      while(rs.next())
      { if(!rs.getBoolean("NON_UNIQUE"))
        { buffer = rs.getString("INDEX_NAME");
          if(buffer != null)
          { if(!buffer.equals(""))
            { String chaine=rs.getString("COLUMN_NAME");
              returnValue.put(chaine, fields.get(chaine));
            }
          }
        }
      }
      rs.close();
    } catch( SQLException e) { e.printStackTrace(); }
    return returnValue;
  }
  //----------------------------------------------------------------------------------------------------

  public Map<String, JdbcField> getFieldsNotNullableAndPrimariesColumn(String tableName) throws SQLException
  {
    Map<String, JdbcField> primaries=getFieldsPrimaryColumn(tableName);
    Map<String, JdbcField> notnullables=getFieldsNotNullableColumn(tableName);
    Set<Map.Entry<String, JdbcField>> s= primaries.entrySet();
    for (Iterator<Entry<String, JdbcField>> iterator = s.iterator(); iterator.hasNext();) {
      Entry<String, JdbcField> entry = iterator.next();
      JdbcField cf = entry.getValue();
      if(!notnullables.containsKey(cf.getName())) {
        notnullables.put(cf.getName(), cf);
      }
    }
    
//    Enumeration e = primaries.elements();
//    while(e.hasMoreElements()) {
//      JdbcField cf = (JdbcField)e.nextElement();
//      if(!notnullables.contains(cf.getName())) {
//        notnullables.put(cf.getName(), cf);
//      }
//    }
    return notnullables;
  }
  //----------------------------------------------------------------------------------------------------

  public List<String> getForeighTables(String tableName) throws SQLException
  {
    List<String> returnValue = new Vector<String>();
    try {
      ResultSet rs = metadata.getImportedKeys(null, schemaName, tableName);
      while(rs.next()) {
        String chaine = rs.getString("PKTABLE_NAME");
        if(!returnValue.contains(chaine)){
          returnValue.add(chaine);
        }
      }
      rs.close();
    } catch( SQLException e) { e.printStackTrace(); }
    return returnValue;
  }
  //----------------------------------------------------------------------------------------------------

  public Map<String, JdbcField> getForeighFieldsForTables(String tableName, String foreighTablename) throws SQLException
  {
    Map<String, JdbcField> returnValue = new Hashtable<String, JdbcField>();
    Map<String, JdbcField> fields = getFieldsColumn(tableName);
    try {
      ResultSet rs = metadata.getImportedKeys(null, schemaName, tableName);
      while(rs.next()) {
        String chaine = rs.getString("PKTABLE_NAME");
        if(chaine.equals(foreighTablename)) {
          String fieldname = rs.getString("PKCOLUMN_NAME");
          returnValue.put(fieldname, fields.get(fieldname));
        }
      }
      rs.close();
    } catch( SQLException e) { e.printStackTrace(); }
    return returnValue;
  }
  //----------------------------------------------------------------------------------------------------

}
