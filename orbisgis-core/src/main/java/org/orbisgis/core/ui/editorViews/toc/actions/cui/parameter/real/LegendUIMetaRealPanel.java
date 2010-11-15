/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 *
 *
 *  Team leader Erwan BOCHER, scientific researcher,
 *
 *  User support leader : Gwendall Petit, geomatic engineer.
 *
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER, Pierre-Yves FADET, Alexis GUEGANNO, Maxence LAURENT
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
 * erwan.bocher _at_ ec-nantes.fr
 * gwendall.petit _at_ ec-nantes.fr
 */
package org.orbisgis.core.ui.editorViews.toc.actions.cui.parameter.real;

import javax.swing.Icon;

import org.orbisgis.core.ui.preferences.lookandfeel.OrbisGISIcon;

import org.orbisgis.core.renderer.se.parameter.real.Categorize2Real;
import org.orbisgis.core.renderer.se.parameter.real.Interpolate2Real;
import org.orbisgis.core.renderer.se.parameter.real.RealAttribute;
import org.orbisgis.core.renderer.se.parameter.real.RealLiteral;
import org.orbisgis.core.renderer.se.parameter.real.RealParameter;
import org.orbisgis.core.renderer.se.parameter.real.Recode2Real;

import org.orbisgis.core.renderer.se.parameter.string.StringAttribute;

import org.orbisgis.core.ui.editorViews.toc.actions.cui.LegendUIAbstractMetaPanel;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.LegendUIComponent;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.LegendUIController;

import org.orbisgis.core.ui.editorViews.toc.actions.cui.parameter.LegendUICategorizePanel;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.parameter.LegendUIPropertyNamePanel;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.parameter.LegendUIRecodePanel;

/**
 *
 * @author maxence
 */
public abstract class LegendUIMetaRealPanel extends LegendUIAbstractMetaPanel {

	private RealParameter real;
	private Class[] classes;
	private LegendUIComponent comp;

	public LegendUIMetaRealPanel(String name, LegendUIController controller, LegendUIComponent parent, RealParameter r, boolean isNullable) {
		super(name, controller, parent, 0, isNullable);

		this.real = r;

		classes = new Class[4];
		classes[0] = RealLiteral.class;
		classes[1] = RealAttribute.class;
		classes[2] = Categorize2Real.class;
		classes[3] = Recode2Real.class;

		comp = null;
		if (real != null) {
			comp = getCompForClass(r.getClass());
		}
	}

	@Override
	protected final LegendUIComponent getCompForClass(Class newClass) {

		if (newClass == RealLiteral.class) {
			RealLiteral literal;
			if (real instanceof RealLiteral) {
				literal = (RealLiteral) real;
			} else {
				literal = new RealLiteral();
			}

			return new LegendUIRealLiteralPanel("Constant " + getName(), controller, this, literal, false) {

				@Override
				protected void realChanged(RealLiteral real) {
					throw new UnsupportedOperationException("Unreachable code.");
				}
			};
		} else if (newClass == RealAttribute.class) {
			RealAttribute pName;
			if (real instanceof RealAttribute) {
				pName = (RealAttribute) real;
			} else {
				pName = new RealAttribute("");
			}


			return new LegendUIPropertyNamePanel("Attribute " + getName(), controller, this, pName, false) {

				@Override
				protected void turnOff() {
					throw new UnsupportedOperationException("Unreachable code.");
				}

				@Override
				protected void turnOn() {
					throw new UnsupportedOperationException("Unreachable code.");
				}
			};


		} else if (newClass == Categorize2Real.class) {
			Categorize2Real categorize;
			if (real instanceof Categorize2Real) {
				categorize = (Categorize2Real) real;
			} else {
				categorize = new Categorize2Real(new RealLiteral(),
						new RealLiteral(), new RealAttribute(""));
			}

			return new LegendUICategorizePanel("Categorized " + getName(), controller, this, categorize, false) {

				@Override
				protected void turnOff() {
					throw new UnsupportedOperationException("Unreachable code.");
				}

				@Override
				protected void turnOn() {
					throw new UnsupportedOperationException("Unreachable code.");
				}
			};

		} else if (newClass == Recode2Real.class) {
			Recode2Real recode;
			if (real instanceof Recode2Real) {
				recode = (Recode2Real) real;
			} else {
				recode = new Recode2Real(new RealLiteral(), new StringAttribute(""));
			}
			return new LegendUIRecodePanel("UniqueValue map " + getName(), controller, this, recode, false) {

				@Override
				protected void turnOff() {
					throw new UnsupportedOperationException("Unreachable code.");
				}

				@Override
				protected void turnOn() {
					throw new UnsupportedOperationException("Unreachable code.");
				}
			};


		} else if (newClass == Interpolate2Real.class) {
			Interpolate2Real interpol;
			if (real instanceof Interpolate2Real) {
				interpol = (Interpolate2Real) real;
			} else {
				interpol = new Interpolate2Real(new RealLiteral());
			}

			return null;
			//_return new LegendUIInterpolll...I("UniqueValue map " + name, controller, this, interpol);

			/*comps[4] = new LegendUIAlgebricalPanel("Alg. " + name, controller, this, real){
			@Override
			public void realChanged(RealParameter newReal) {
			switchTo(comps[4]);
			}
			};*/
		} else {
			return null;
		}

	}

	@Override
	public void init() {
		init(classes, comp);
	}

	/**
	 * use with caution !
	 * @param type
	 * @param comp
	 */
	@Override
	public void switchTo(LegendUIComponent comp) {
		if (comp != null) {
			this.real = ((LegendUIRealComponent) comp).getRealParameter();
			this.realChanged(real);
		} else {
			this.realChanged(null);
		}
	}

	@Override
	public Icon getIcon() {
		return OrbisGISIcon.PALETTE;
	}

	public abstract void realChanged(RealParameter newReal);

	@Override
	public Class getEditedClass() {
		return real.getClass();
	}
}
