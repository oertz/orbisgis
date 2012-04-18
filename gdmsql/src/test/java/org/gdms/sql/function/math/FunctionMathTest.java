/*
 * The GDMS library (Generic Datasources Management System)
 * is a middleware dedicated to the management of various kinds of
 * data-sources such as spatial vectorial data or alphanumeric. Based
 * on the JTS library and conform to the OGC simple feature access
 * specifications, it provides a complete and robust API to manipulate
 * in a SQL way remote DBMS (PostgreSQL, H2...) or flat files (.shp,
 * .csv...). It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 * 
 * 
 * Team leader : Erwan BOCHER, scientific researcher,
 * 
 * User support leader : Gwendall Petit, geomatic engineer.
 * 
 * Previous computer developer : Pierre-Yves FADET, computer engineer, Thomas LEDUC, 
 * scientific researcher, Fernando GONZALEZ CORTES, computer engineer.
 * 
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 * 
 * Copyright (C) 2010 Erwan BOCHER, Alexis GUEGANNO, Maxence LAURENT, Antoine GOURLAY
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
package org.gdms.sql.function.math;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import org.gdms.SQLBaseTest;
import org.gdms.data.DataSource;
import org.gdms.data.SQLDataSourceFactory;

/**
 *
 * @author Antoine Gourlay
 */
public class FunctionMathTest {

        private static SQLDataSourceFactory dsf;
        
        @Before
        public void setUp() throws Exception {
                dsf = new SQLDataSourceFactory();
                dsf.setTempDir(SQLBaseTest.backupDir.getAbsolutePath());
                dsf.setResultDir(SQLBaseTest.backupDir);
        }

        @After
        public void tearDown() throws Exception {
                dsf.freeResources();
        }

        @Test
        public void testLn() throws Exception {
                DataSource d = dsf.getDataSourceFromSQL("SELECT ln(2.0);");
                d.open();
                assertEquals(Math.log(2.0), d.getDouble(0, 0), 10E-15);
                d.close();

                d = dsf.getDataSourceFromSQL("SELECT ln(NULL);");
                d.open();
                assertTrue(d.getFieldValue(0, 0).isNull());
                d.close();
        }

        @Test
        public void testLog() throws Exception {
                DataSource d = dsf.getDataSourceFromSQL("SELECT log(2.0);");
                d.open();
                assertEquals(Math.log10(2.0), d.getDouble(0, 0), 10E-15);
                d.close();

                d = dsf.getDataSourceFromSQL("SELECT ln(NULL);");
                d.open();
                assertTrue(d.getFieldValue(0, 0).isNull());
                d.close();
        }

        @Test
        public void testCbrt() throws Exception {
                DataSource d = dsf.getDataSourceFromSQL("SELECT cbrt(2.0);");
                d.open();
                assertEquals(Math.cbrt(2.0), d.getDouble(0, 0), 10E-15);
                d.close();

                d = dsf.getDataSourceFromSQL("SELECT cbrt(NULL);");
                d.open();
                assertTrue(d.getFieldValue(0, 0).isNull());
                d.close();
        }
}
