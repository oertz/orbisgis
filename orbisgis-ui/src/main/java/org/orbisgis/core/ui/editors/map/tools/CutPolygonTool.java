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
 *
 * Copyright (C) 2007-2008 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER, Pierre-Yves FADET, Antoine GOURLAY, Alexis GUEGANNO, Maxence LAURENT, Gwendall PETIT
 *
 * Copyright (C) 2011 Erwan BOCHER, Antoine GOURLAY, Alexis GUEGANNO, Maxence LAURENT, Gwendall PETIT
 *
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
 * info _at_ orbisgis.org
 */
package org.orbisgis.core.ui.editors.map.tools;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Polygon;
import java.util.ArrayList;
import java.util.Observable;

import javax.swing.AbstractButton;

import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.data.types.GeometryConstraint;
import org.gdms.driver.DriverException;
import org.orbisgis.core.layerModel.MapContext;
import org.orbisgis.core.ui.editors.map.tool.Handler;
import org.orbisgis.core.ui.editors.map.tool.MultiPolygonHandler;
import org.orbisgis.core.ui.editors.map.tool.PolygonHandler;
import org.orbisgis.core.ui.editors.map.tool.ToolManager;
import org.orbisgis.core.ui.editors.map.tool.TransitionException;
import org.orbisgis.core.ui.pluginSystem.PlugInContext;
import org.orbisgis.utils.I18N;

public class CutPolygonTool extends AbstractPolygonTool {

        AbstractButton button;

        @Override
        public AbstractButton getButton() {
                return button;
        }

        public void setButton(AbstractButton button) {
                this.button = button;
        }

        @Override
        public void update(Observable o, Object arg) {
                PlugInContext.checkTool(this);
        }

        @Override
        protected void polygonDone(Polygon pol,
                MapContext mc, ToolManager tm) throws TransitionException {
                SpatialDataSourceDecorator sds = mc.getActiveLayer().getSpatialDataSource();
                try {
                        ArrayList<Handler> handlers = tm.getCurrentHandlers();
                        for (Handler handler : handlers) {
                                if (handler instanceof MultiPolygonHandler) {
                                        MultiPolygonHandler mp = (MultiPolygonHandler) handler;
                                        updateGeometry(mp, pol, sds);

                                } else if (handler instanceof PolygonHandler) {
                                        PolygonHandler ph = (PolygonHandler) handler;
                                        updateGeometry(ph, pol, sds);
                                }

                        }

                } catch (DriverException e) {
                        throw new TransitionException("Cannot insert polygon", e);
                }
        }

        private void updateGeometry(PolygonHandler mp, Polygon pol, SpatialDataSourceDecorator sds) throws DriverException {
                Polygon polygon = (Polygon) sds.getGeometry(mp.getGeometryIndex());
                if (polygon.intersects(pol)) {
                        Geometry newGeomIntersect = polygon.intersection(pol);
                        Geometry newGeomDiff = polygon.difference(newGeomIntersect);
                        if (newGeomDiff.isValid()) {
                                polygon = (Polygon) newGeomDiff;
                        }
                }
                sds.setGeometry(mp.getGeometryIndex(), polygon);
        }

        private void updateGeometry(MultiPolygonHandler mp, Polygon pol, SpatialDataSourceDecorator sds) throws DriverException {
                Geometry geom = sds.getGeometry(mp.getGeometryIndex());

                ArrayList<Polygon> geometries = new ArrayList<Polygon>();
                for (int i = 0; i < geom.getNumGeometries(); i++) {
                        Polygon subGeom = (Polygon) geom.getGeometryN(i);
                        if (subGeom.intersects(pol)) {
                                Geometry newGeomIntersect = subGeom.intersection(pol);
                                Geometry newGeomDiff = subGeom.difference(newGeomIntersect);
                                if (newGeomDiff.isValid()) {
                                        geometries.add((Polygon) newGeomDiff);
                                } else {
                                        geometries.add(subGeom);
                                }
                        } else {
                                geometries.add(subGeom);
                        }
                }
                sds.setGeometry(mp.getGeometryIndex(), geom.getFactory().createMultiPolygon(geometries.toArray(new Polygon[geometries.size()])));
        }

        public boolean isEnabled(MapContext vc, ToolManager tm) {
                return (ToolUtilities.geometryTypeIs(vc, GeometryConstraint.POLYGON) || ToolUtilities.geometryTypeIs(vc, GeometryConstraint.MULTI_POLYGON)) && ToolUtilities.isActiveLayerEditable(vc) && ToolUtilities.isSelectionEqualsTo(vc, 1);
        }

        public boolean isVisible(MapContext vc, ToolManager tm) {
                return isEnabled(vc, tm);
        }

        public double getInitialZ(MapContext mapContext) {
                return ToolUtilities.getActiveLayerInitialZ(mapContext);
        }

        public String getName() {
                return I18N.getString("orbisgis.core.ui.editors.map.tool.polygon.cut");
        }
}