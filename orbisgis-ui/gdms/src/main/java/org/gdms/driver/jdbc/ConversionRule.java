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
package org.gdms.driver.jdbc;

import org.gdms.data.types.Type;
import org.gdms.data.types.TypeDefinition;

public interface ConversionRule extends TypeDefinition {

	/**
	 * Returns true if the rule can be applied to the specified type
	 *
         * @param type
         * @return
	 */
	boolean canApply(Type type);

	/**
	 * Gets the SQL representation of the specified type. fieldType will have
	 * the type code returned by getTypeCode()
	 *
	 * @param fieldName
	 *            Name of the field
	 * @param fieldType
	 *            GDMS Type of the field
	 * @return
	 */
	String getSQL(String fieldName, Type fieldType);

	/**
	 * Gets the name of the type in SQL statements
	 *
	 * @return
	 */
        @Override
	String getTypeName();

        @Override
	int[] getValidConstraints();
}
