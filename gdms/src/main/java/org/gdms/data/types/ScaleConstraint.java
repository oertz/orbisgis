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
package org.gdms.data.types;

import org.gdms.data.values.Value;

/**
 * Indicates the scale of the field.
 */
public final class ScaleConstraint extends AbstractIntConstraint {

	ScaleConstraint(byte[] constraintBytes) {
		super(constraintBytes);
	}

	public ScaleConstraint(int constraintValue) {
		super(constraintValue);
	}

        @Override
	public int getConstraintCode() {
		return Constraint.SCALE;
	}

        @Override
	public String check(Value value) {
		if (!value.isNull()) {
			double d = value.getAsDouble();
			String s = String.valueOf(d);
			String[] nums = s.split("\\Q.\\E");
			if (nums.length == 2) {
				int length = nums[1].length();
				if (length > constraintValue) {
					return "Too many digits. Only " + constraintValue
							+ " allowed";
				}
			}
		}
		return null;
	}

}