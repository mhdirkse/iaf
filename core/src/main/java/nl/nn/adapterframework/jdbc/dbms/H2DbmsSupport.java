/*
   Copyright 2015, 2019 Nationale-Nederlanden

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/
package nl.nn.adapterframework.jdbc.dbms;

import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import nl.nn.adapterframework.jdbc.JdbcException;
import nl.nn.adapterframework.jdbc.QueryContext;
import nl.nn.adapterframework.util.JdbcUtil;

/**
 * Support for H2.
 * 
 * @author Jaco de Groot
 */
public class H2DbmsSupport extends GenericDbmsSupport {
	public final static String dbmsName = "H2";

	@Override
	public int getDatabaseType() {
		return DbmsSupportFactory.DBMS_H2;
	}

	@Override
	public String getDbmsName() {
		return dbmsName;
	}

	@Override
	public String getSchema(Connection conn) throws JdbcException {
		return JdbcUtil.executeStringQuery(conn, "SELECT SCHEMA()");
	}

	public boolean isTablePresent(Connection conn, String schemaName, String tableName) throws JdbcException {
		return doIsTablePresent(conn, "INFORMATION_SCHEMA.TABLES", "TABLE_SCHEMA", "TABLE_NAME", schemaName, tableName.toUpperCase());
	}

	@Override
	public boolean isTableColumnPresent(Connection conn, String schemaName, String tableName, String columnName) throws JdbcException {
		return doIsTableColumnPresent(conn, "INFORMATION_SCHEMA.COLUMNS", "TABLE_SCHEMA", "TABLE_NAME", "COLUMN_NAME", schemaName, tableName, columnName);
	}

	@Override
	public String getIbisStoreSummaryQuery() {
		return "select type, slotid, formatdatetime(MESSAGEDATE,'yyyy-MM-dd') msgdate, count(*) msgcount from ibisstore group by slotid, type, formatdatetime(MESSAGEDATE,'yyyy-MM-dd') order by type, slotid, formatdatetime(MESSAGEDATE,'yyyy-MM-dd')";
	}

	@Override
	public void convertQuery(QueryContext queryContext, String sqlDialectFrom) throws SQLException, JdbcException {
		if (isQueryConversionRequired(sqlDialectFrom)) {
			if (OracleDbmsSupport.dbmsName.equalsIgnoreCase(sqlDialectFrom)) {
				List<String> multipleQueries = splitQuery(queryContext.getQuery());
				StringBuilder sb = new StringBuilder();
				for (String singleQuery : multipleQueries) {
					QueryContext singleQueryContext = new QueryContext(singleQuery, queryContext.getQueryType(), queryContext.getParameterList());
					String convertedQuery = OracleToH2Translator.convertQuery(singleQueryContext, multipleQueries.size() == 1);
					if (convertedQuery != null) {
						sb.append(convertedQuery);
						if (singleQueryContext.getQueryType()!=null && !singleQueryContext.getQueryType().equals(queryContext.getQueryType())) {
							queryContext.setQueryType(singleQueryContext.getQueryType());
						}
					}
				}
				queryContext.setQuery(sb.toString());
			} else {
				warnConvertQuery(sqlDialectFrom);
			}
		}
	}
	
	@Override
	public Object getClobUpdateHandle(ResultSet rs, int column) throws SQLException, JdbcException {
		Clob clob=rs.getStatement().getConnection().createClob();
		return clob;
	}

	@Override
	public Object getClobUpdateHandle(ResultSet rs, String column) throws SQLException, JdbcException {
		Clob clob=rs.getStatement().getConnection().createClob();
		return clob;
	}

	
	@Override
	public Object getBlobUpdateHandle(ResultSet rs, int column) throws SQLException, JdbcException {
		Blob blob=rs.getStatement().getConnection().createBlob();
		return blob;
	}
	@Override
	public Object getBlobUpdateHandle(ResultSet rs, String column) throws SQLException, JdbcException {
		Blob blob=rs.getStatement().getConnection().createBlob();
		return blob;
	}

}
