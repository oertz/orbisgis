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
package org.gdms.spatial;

import org.junit.Test;
import org.junit.Before;
import org.gdms.TestBase;
import org.gdms.data.DataSource;
import org.gdms.data.schema.Metadata;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.memory.MemoryDataSetDriver;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

import static org.junit.Assert.*;

public class SpatialDriverMetadataTest extends TestBase {

        @Test
        public void testHasSpatialField() throws Exception {
                String[] resources = super.getSpatialResources();
                for (String resource : resources) {
                        testHasSpatialField(resource);
                }
        }

        private void testHasSpatialField(String dsName) throws Exception {
                DataSource sds = dsf.getDataSource(dsName);
                sds.open();
                Metadata sdm = sds.getMetadata();
                boolean has = false;
                for (int i = 0; i < sdm.getFieldCount(); i++) {
                        if ((sdm.getFieldType(i).getTypeCode() & Type.GEOMETRY) != 0) {
                                has = true;
                                break;
                        }
                }

                assertTrue(has);
                sds.close();
        }

        @Test
        public void testFullExtentWhenDriverDoesntProvideIt() throws Exception {
                MemoryDataSetDriver driver = new MemoryDataSetDriver(
                        new String[]{"geom"}, new Type[]{TypeFactory.createType(Type.GEOMETRY)});
                GeometryFactory gf = new GeometryFactory();
                Geometry geom = gf.createMultiPoint(new Point[]{
                                gf.createPoint(new Coordinate(10, 10)),
                                gf.createPoint(new Coordinate(1340, 13460)),
                                gf.createPoint(new Coordinate(13450, 120)),});
                driver.addValues(new Value[]{ValueFactory.createValue(geom)});
                DataSource ds = dsf.getDataSource(driver, "main");
                ds.open();
                assertEquals(geom.getEnvelopeInternal(), ds.getFullExtent());
                ds.close();
        }

        @Before
        @Override
        public void setUp() throws Exception {
                this.setWritingTests(false);
                super.setUp();
        }
}
