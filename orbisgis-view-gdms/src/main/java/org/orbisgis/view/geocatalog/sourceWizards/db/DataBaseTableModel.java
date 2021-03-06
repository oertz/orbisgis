/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier
 * SIG" team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2014 IRSTV (FR CNRS 2488)
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
 * or contact directly: info_at_ orbisgis.org
 */
package org.orbisgis.view.geocatalog.sourceWizards.db;

import java.util.ArrayList;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;
import org.cts.crs.CoordinateReferenceSystem;
import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.NoSuchTableException;
import org.gdms.driver.DriverException;
import org.gdms.source.SourceManager;
import org.orbisgis.core.DataManager;
import org.orbisgis.core.Services;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * This class is used to populate an {@code AbstractTableModel} with 
 * the metadata available on a datasource selected in the GeoCatalog.
 * @author Erwan Bocher
 * @author Alexis Guéganno
 */
public class DataBaseTableModel extends AbstractTableModel {

    private static final Logger LOGGER = Logger.getLogger(DataBaseTableModel.class);
    private static final I18n I18N = I18nFactory.getI18n(DataBaseTableModel.class);
    private final String[] sourceNames;
    //Status -> the status of the datasource (exported, in error)
    //Source name -> the name of the datasource available in the geocatalog
    //Table name -> the name of the datasource exported in the database
    //Schema -> the name of schema in the database
    //Input field and output field -> the name of the default geometry field.
    //Input CRS -> The name of the input CRS is the table is spatial
    //Output EPSG -> The EPSG code used to export the table
    //Export -> A boolean value to choose if the datasource must be (or not) exported
    private static final String[] COLUMN_NAMES = new String[]{"Status", "Source name", "Table name", "Schema",
        "Input field", "Output field", "Input CRS", "Output EPSG", "Export"};
    private ArrayList<DataBaseRow> data = new ArrayList<DataBaseRow>();
    private boolean isEditable = false;

    /**
     * Build a new {@code DataBaseTableModel} using the {@code Source} instances
     * registered in {@code sourceManager} with names {@code sourceNames}.
     *
     * @param sourceManager
     * @param sourceNames
     */
    public DataBaseTableModel(SourceManager sourceManager, String[] sourceNames) {
        this.sourceNames = sourceNames;
        init(sourceManager);

    }

    /**
     * Update the status of the all rows to allow cell edition. This update is
     * done when a connection is opened.
     *
     * @param isEditable
     */
    public void setEditable(boolean isEditable) {
        this.isEditable = isEditable;
    }

    /**
     * Create the panel to display the list of tables. Displayed tables are the
     * ones that are neither raster and nor system tables.
     *
     * @param sourceManager The {@code SourceManager} used to retrieve sources.
     */
    private void init(SourceManager sourceManager) {
        try {
            DataManager dm = Services.getService(DataManager.class);
            DataSourceFactory dsf = dm.getDataSourceFactory();
            final int validType =  SourceManager.VECTORIAL | SourceManager.RASTER
                        | SourceManager.STREAM | SourceManager.SYSTEM_TABLE;
            for (String sourceName : sourceNames) {
                int type = sourceManager.getSource(sourceName).getType();
                if ((validType & type) == 0) {
                    DataBaseRow row = new DataBaseRow(sourceName, sourceName, "public", "No geometry",
                            "No geometry", DataBaseRow.DEFAULT_EPSG, DataBaseRow.DEFAULT_EPSG, Boolean.TRUE);
                    //We don't need to call setSpatial : isSpatial is false
                    //by default.
                    data.add(row);
                } else if ((type & SourceManager.VECTORIAL) == SourceManager.VECTORIAL) {
                    DataSource ds = dsf.getDataSource(sourceName);
                    ds.open();
                    String geomField = ds.getFieldName(ds.getSpatialFieldIndex());
                    CoordinateReferenceSystem crs = ds.getCRS();
                    String crsInformation = DataBaseRow.DEFAULT_CRS;
                    int epsgCode = DataBaseRow.DEFAULT_EPSG;
                    if (crs != null) {
                        try {
                            String identifiers = crs.getAuthorityName();
                            if (!identifiers.isEmpty() && identifiers.equalsIgnoreCase("epsg")) {
                                epsgCode = Integer.valueOf(crs.getAuthorityKey());
                                crsInformation = identifiers;
                            } else {
                                crsInformation = crs.getName();
                            }
                        } catch (NumberFormatException e) {
                            epsgCode = -1;
                        }
                    }
                    ds.close();
                    DataBaseRow row = new DataBaseRow(sourceName, sourceName,
                            "public", geomField, geomField, epsgCode, epsgCode, Boolean.TRUE);
                    row.setCrsInformation(crsInformation);
                    row.setSpatial(true);
                    data.add(row);
                }
            }
        } catch (NoSuchTableException ex) {
            LOGGER.error(I18N.tr("This table does not exist."), ex);
        } catch (DataSourceCreationException ex) {
            LOGGER.error(I18N.tr("The required DataSource could not be created."), ex);
        } catch (DriverException e) {
            LOGGER.error(I18N.tr("Cannot connect to the database."), e);
        }
    }

    @Override
    public int getRowCount() {
        return data.size();
    }

    @Override
    public int getColumnCount() {
        return COLUMN_NAMES.length;
    }

    @Override
    public Object getValueAt(int row, int col) {
        return data.get(row).getValue(col);
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        if (isEditable) {
            if (columnIndex == 1 || columnIndex == 4 || columnIndex == 6) {
                return false;
            }
            DataBaseRow row = data.get(rowIndex);
            if (!row.isSpatial()) {
                if ((columnIndex == 5) || (columnIndex == 7)) {
                    return false;
                }
            } else {
                //Ensure all CRS cases
                String rowCRSInformation = row.getCrsInformation();
                if (rowCRSInformation.equals(DataBaseRow.DEFAULT_CRS) && (columnIndex == 7)) {
                    //The user cannot change the output crs code.
                    // The -1 code will be used.
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        DataBaseRow row = data.get(rowIndex);
        row.setValue(aValue, columnIndex);
        data.set(rowIndex, row);
        fireTableRowsUpdated(rowIndex, rowIndex);
    }

    @Override
    public String getColumnName(int col) {
        return COLUMN_NAMES[col];
    }

    @Override
    public Class getColumnClass(int col) {
        return getValueAt(0, col).getClass();
    }

    /**
     * Returns all cell values as a object for a given row
     *
     * @param row
     * @return
     */
    public Object[] getObjects(int row) {
        return data.get(row).getObjects();
    }

    /**
     * Returns the row at index {@code rowIndex}.
     *
     * @param rowIndex
     * @return
     */
    public DataBaseRow getRow(int rowIndex) {
        return data.get(rowIndex);
    }

    /**
     * Check if one row is selected
     *
     * @return
     */
    public boolean isOneRowSelected() {
        for (DataBaseRow row : data) {
            if (row.isExport()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns all rows
     *
     * @return
     */
    public ArrayList<DataBaseRow> getData() {
        return data;
    }
}
