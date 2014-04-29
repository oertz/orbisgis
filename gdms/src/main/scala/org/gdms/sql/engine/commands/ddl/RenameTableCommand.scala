/**
 * The GDMS library (Generic Datasource Management System)
 * is a middleware dedicated to the management of various kinds of
 * data-sources such as spatial vectorial data or alphanumeric. Based
 * on the JTS library and conform to the OGC simple feature access
 * specifications, it provides a complete and robust API to manipulate
 * in a SQL way remote DBMS (PostgreSQL, H2...) or flat files (.shp,
 * .csv...).
 *
 * Gdms is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2014 IRSTV FR CNRS 2488
 *
 * This file is part of Gdms.
 *
 * Gdms is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * Gdms is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Gdms. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 *
 * or contact directly:
 * info@orbisgis.org
 */
package org.gdms.sql.engine.commands.ddl

import org.gdms.data.NoSuchTableException
import org.gdms.sql.engine.SemanticException
import org.gdms.sql.engine.commands.Command
import org.gdms.sql.engine.commands.OutputCommand
import org.gdms.sql.engine.GdmSQLPredef._
import org.orbisgis.progress.ProgressMonitor

/**
 * Renames a table.
 * 
 * @param name current name of the table
 * @param new name of the table
 * @author Antoine Gourlay
 * @since 0.1
 */
class RenameTableCommand(name: String, newname: String) extends Command with OutputCommand {

  override def doPrepare() = {
    // checks the table exists
    if (!dsf.getSourceManager.exists(name)) {
      throw new NoSuchTableException(name)
    }
    // check the name is available
    if (dsf.getSourceManager.exists(newname)) {
      throw new SemanticException("There already is a table named '" + newname + "' registered.")
    }
  }
  
  protected final def doWork(r: Iterator[RowStream])(implicit pm: Option[ProgressMonitor]) = {
    
    dsf.getSourceManager.rename(name, newname)
    
    Iterator.empty
  }

  val getResult = null

  // no result
  override val getMetadata = null
}