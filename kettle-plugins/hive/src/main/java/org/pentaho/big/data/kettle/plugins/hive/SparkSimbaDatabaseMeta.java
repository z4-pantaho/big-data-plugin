/*******************************************************************************
 * Pentaho Big Data
 * <p>
 * Copyright (C) 2002-2016 by Pentaho : http://www.pentaho.com
 * <p>
 * ******************************************************************************
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 ******************************************************************************/

package org.pentaho.big.data.kettle.plugins.hive;

import com.google.common.annotations.VisibleForTesting;
import org.pentaho.big.data.api.jdbc.DriverLocator;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.plugins.DatabaseMetaPlugin;
import org.pentaho.di.core.row.ValueMetaInterface;

@DatabaseMetaPlugin( type = "SPARKSIMBA", typeDescription = "SparkSQL" )
public class SparkSimbaDatabaseMeta extends BaseSimbaDatabaseMeta {

  @VisibleForTesting static final String JDBC_URL_PREFIX = "jdbc:spark://";
  @VisibleForTesting static final String DRIVER_CLASS_NAME = "org.apache.hive.jdbc.SparkSqlSimbaDriver";
  @VisibleForTesting static final String JAR_FILE = "SparkJDBC41.jar";
  @VisibleForTesting static final int DEFAULT_PORT = 10015;

  public SparkSimbaDatabaseMeta( DriverLocator driverLocator ) {
    super( driverLocator );
  }

  @Override public int[] getAccessTypeList() {
    return new int[] { DatabaseMeta.TYPE_ACCESS_NATIVE, DatabaseMeta.TYPE_ACCESS_JNDI };
  }

  @Override protected String getJdbcPrefix() {
    return JDBC_URL_PREFIX;
  }

  @Override
  public String getDriverClass() {
    return DRIVER_CLASS_NAME;
  }

  @Override
  public String getSQLQueryFields( String tableName ) {
    return "SELECT * FROM " + getTableReference( tableName ) + " LIMIT 0";
  }

  @Override
  public String getStartQuote() {
    return "`";
  }

  @Override
  public String getEndQuote() {
    return "`";
  }

  private String getTableReference( String tableName ) {
    if ( !isTableNameQualified( tableName ) ) {
      return getSchemaTableCombination( getDatabaseName(), tableName );
    } else {
      return tableName;
    }
  }

  private boolean isTableNameQualified( String tableName ) {
    String quotedDbName = getStartQuote() + getDatabaseName() + getEndQuote();
    return tableName.startsWith( getDatabaseName() ) || tableName.startsWith( quotedDbName );
  }

  @Override
  public String getSQLTableExists( String tablename ) {
    return "SELECT 1 FROM " + getTableReference( tablename );
  }

  @Override
  public String getTruncateTableStatement( String tableName ) {
    return "TRUNCATE TABLE " + getTableReference( tableName );
  }

  @Override
  public String getSQLColumnExists( String columnname, String tablename ) {
    return "SELECT " + columnname + " FROM " + getTableReference( tablename );
  }

  @Override
  public String getSelectCountStatement( String tableName ) {
    return SELECT_COUNT_STATEMENT + " " + getTableReference( tableName );
  }

  @Override
  public String getDropColumnStatement( String tablename, ValueMetaInterface v, String tk, boolean use_autoinc,
                                        String pk, boolean semicolon ) {
    throw new UnsupportedOperationException( "Cannot DROP columns with SparkSQL" );
  }

  @Override
  public String getAddColumnStatement( String tablename, ValueMetaInterface v, String tk, boolean useAutoinc,
                                       String pk, boolean semicolon ) {
    throw new UnsupportedOperationException( "Cannot ADD columns with SparkSQL" );
  }

  @Override
  public String getModifyColumnStatement( String tablename, ValueMetaInterface v, String tk, boolean useAutoinc,
                                          String pk, boolean semicolon ) {
    throw new UnsupportedOperationException( "Cannot MODIFY columns with SparkSQL" );
  }

  @Override
  public String[] getUsedLibraries() {
    return new String[] { JAR_FILE };
  }

  @Override
  public int getDefaultDatabasePort() {
    return DEFAULT_PORT;
  }
}
