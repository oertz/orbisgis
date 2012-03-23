/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able
 * to manipulate and create vector and raster spatial information. OrbisGIS
 * is distributed under GPL 3 license. It is produced  by the geo-informatic team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OrbisGIS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult:
 *    <http://orbisgis.cerma.archi.fr/>
 *    <http://sourcesup.cru.fr/projects/orbisgis/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
package org.gdms.driver;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Interface to implement by the drivers that use jdbc to access data
 * 
 * @author Fernando Gonzalez Cortes
 */
public interface DBDriver extends Driver {

	/**
	 * Provides connections to the database. Each invocation creates and returns
	 * a new connection. The connection are managed in upper layers
	 * 
	 * @param host
	 * 
	 * @param port
	 *            Port of the database management system. -1 means default port
         * @param ssl 
         * @param dbName
	 * 
	 * @param user
	 * 
	 * @param password
	 * 
	 * 
	 * @return Connection
	 * 
	 * @throws SQLException
	 *             If some error happens
	 */
	Connection getConnection(String host, int port, boolean ssl, String dbName, String user,
			String password) throws SQLException;

	/**
	 * Free any resource reserved in the open method
	 * 
	 * @param conn
	 * 
         * @throws DriverException
	 */
	void close(Connection conn) throws DriverException;

	/**
	 * Connects to the data source and reads the specified table of 
	 * the default schema in the database. Set this Table as current.
	 * 
	 * @param con
	 * 			Connection to the database
	 * @param tableName
	 *            Name of the table where the data is in
	 * 
	 * @throws DriverException
	 */

	void open(Connection con, String tableName) throws DriverException;
	
	/**
	 * Connects to the data source and reads the specified table in the
	 * specified schema. Set this Table and this schema as current.
	 * 
	 * @param con
	 * 			Connection to the database
	 * @param tableName
	 *          Name of the table where the data is in
	 * @param schemaName
	 * 			Name of the schema where the table is in
	 * 
	 * @throws DriverException
	 */
	void open(Connection con, String tableName, String schemaName) throws DriverException;

	
	/**
	 * Retrieves all Table names in a database (with types TABLES or VIEW)
	 * 
	 * @param c
	 * 		Connection to the database
	 * 
	 * @return table of Tables descriptions
	 * 
	 * @throws DriverException
	 */
	TableDescription[] getTables(Connection c) throws DriverException;

	
	/**
	 * Retrieves all Schema names in a database.
	 * 
	 * @param c : the connection
	 * @return a String table
	 * 
	 * @throws DriverException
	 */
	String[] getSchemas(Connection c) throws DriverException;

	
	/**
	 * Retrieves all Table names in a database, in specified catalog and schema
	 * and matches with a "tableNamePattern" and with some types.
	 * 
	 * @param c : the database connection.
	 * @param catalog 
	 * @param schemaPattern
	 * @param tableNamePattern
	 * @param types
	 * @return a table description of the Tables.
	 * @throws DriverException
	 * 
	 * @see {@link java.sql.DatabaseMetaData#getTables(String, String, String, String[])}
	 */
	TableDescription[] getTables(Connection c, String catalog, 
			String schemaPattern, String tableNamePattern, String[] types)	throws DriverException;
	

	
	/**
	 * Get the port the dbms accessed by this driver listen by default
	 * 
	 * @return the port number
	 */
	int getDefaultPort();

	/**
	 * Gets the array of the prefixes accepted by this driver
	 * 
	 * @return
	 */
	String[] getPrefixes();

        /**
         * Gets all non blocking errors that happened since the last
         * call to any DBDriver method that could throw DriverException
         * @return an array of exceptions, possibly empty
         */
        DriverException[] getLastNonBlockingErrors();

}