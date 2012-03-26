/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
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
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * OrbisGIS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 *
 * or contact directly:
 * info@orbisgis.org
 */
package org.gdms.data.types;

import org.orbisgis.utils.ByteUtils;

abstract class AbstractIntConstraint extends AbstractConstraint {

	protected int constraintValue;

	AbstractIntConstraint(final int constraintValue) {
		this.constraintValue = constraintValue;
	}

	AbstractIntConstraint(byte[] constraintBytes) {
		this(ByteUtils.bytesToInt(constraintBytes));
	}

        @Override
	public String getConstraintValue() {
		return Integer.toString(constraintValue);
	}

        @Override
	public byte[] getBytes() {
		return ByteUtils.intToBytes(constraintValue);
	}

        @Override
	public int getType() {
		return CONSTRAINT_TYPE_INTEGER_LITERAL;
	}
        
        /**
         * We're dealing with an Int value, we can retrieve it directly, and not as a String.
         * @return 
         *      the int embedded in this constraint.
         */
        public int getConstraintIntValue(){
                return constraintValue;
        }
}