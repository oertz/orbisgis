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
package org.gdms.data.edition;

import org.junit.Before;
import org.junit.Test;
import org.gdms.TestBase;
import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.memory.MemorySourceDefinition;
import org.gdms.driver.driverManager.DriverManager;
import org.gdms.source.SourceManager;

import static org.junit.Assert.*;

public class IsEditableTests extends TestBase {

	private DataSourceFactory dsf;

	@Test
        public void testObject() throws Exception {
		DataSource ds = dsf.getDataSource("readObject");
		assertFalse(ds.isEditable());
		ds = dsf.getDataSource("readWriteObject");
		assertFalse(ds.isEditable());
		ReadDriver.isEditable = true;
		assertTrue(ds.isEditable());
	}

	@Test
        public void testFile() throws Exception {
		DataSource ds = dsf.getDataSource("readFile");
		assertFalse(ds.isEditable());
		ds = dsf.getDataSource("readWriteFile");
		assertFalse(ds.isEditable());
		ReadDriver.isEditable = true;
		assertTrue(ds.isEditable());
	}

	@Test
        public void testDB() throws Exception {
		DataSource ds = dsf.getDataSource("readDB");
		assertFalse(ds.isEditable());
		ds = dsf.getDataSource("readWriteDB");
		assertFalse(ds.isEditable());
		ReadDriver.isEditable = true;
		assertTrue(ds.isEditable());
	}

	@Before
	public void setUp() throws Exception {
		ReadDriver.initialize();

		dsf = new DataSourceFactory();
                dsf.setTempDir(TestBase.backupDir.getAbsolutePath());
                dsf.setResultDir(TestBase.backupDir);
		DriverManager dm = new DriverManager();
		dm.registerDriver(ReadDriver.class);
		SourceManager sourceManager = dsf.getSourceManager();
		sourceManager.setDriverManager(dm);

		sourceManager.register("readObject", new MemorySourceDefinition(
				new ReadDriver(),"main"));
		sourceManager.register("readWriteObject", new MemorySourceDefinition(
				new ReadAndWriteDriver(),"main"));
		sourceManager.register("readFile", new FakeFileSourceDefinition(
				new ReadDriver()));
		sourceManager.register("readWriteFile", new FakeFileSourceDefinition(
				new ReadAndWriteDriver()));
		sourceManager.register("readDB", new FakeDBTableSourceDefinition(
				new ReadDriver(), "jdbc:executefailing"));
		sourceManager.register("readWriteDB", new FakeDBTableSourceDefinition(
				new ReadAndWriteDriver(), "jdbc:closefailing"));
	}
}