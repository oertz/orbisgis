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
package org.gdms.data.indexes;

import com.vividsolutions.jts.geom.Envelope;

import org.gdms.sql.evaluator.Expression;

public class ExpressionBasedSpatialIndexQuery implements SpatialIndexQuery {

        private Expression area;
        private String[] fieldName;

        public ExpressionBasedSpatialIndexQuery(Expression exp, String fieldName) {
                this.area = exp;
                this.fieldName = new String[]{fieldName};
        }

        @Override
        public Envelope getArea() throws IndexQueryException {
                return area.evaluate(null).getAsGeometry().getEnvelopeInternal();
        }

        @Override
        public String[] getFieldNames() {
                return fieldName;
        }

        @Override
        public boolean isStrict() {
                return false;
        }
//	public Field[] getFields() {
//		ArrayList<Field> ret = new ArrayList<Field>();
//		Field[] fields = area.getFieldReferences();
//		for (Field field : fields) {
//			ret.add(field);
//		}
//
//		return ret.toArray(new Field[ret.size()]);
//	}
}
