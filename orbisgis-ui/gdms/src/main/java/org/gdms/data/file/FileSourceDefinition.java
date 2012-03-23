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
package org.gdms.data.file;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.log4j.Logger;

import org.gdms.data.AbstractDataSourceDefinition;
import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.DataSourceDefinition;
import org.gdms.driver.DriverException;
import org.gdms.driver.DriverUtilities;
import org.gdms.driver.FileDriver;
import org.gdms.driver.FileReadWriteDriver;
import org.gdms.driver.Driver;
import org.gdms.driver.DataSet;
import org.gdms.driver.driverManager.DriverLoadException;
import org.gdms.driver.driverManager.DriverManager;
import org.gdms.driver.gdms.GdmsDriver;
import org.gdms.driver.shapefile.ShapefileDriver;
import org.gdms.source.directory.DefinitionType;
import org.gdms.source.directory.FileDefinitionType;
import org.orbisgis.progress.ProgressMonitor;
import org.orbisgis.utils.FileUtils;
import org.orbisgis.utils.I18N;

/**
 * Definition of file sources
 * 
 */
public class FileSourceDefinition extends AbstractDataSourceDefinition {

        protected File file;
        private int cachedType = -1;
        private String tableName;
        private static final Logger LOG = Logger.getLogger(FileSourceDefinition.class);
        private List<String> sourceDependencies = new ArrayList<String>();

        public FileSourceDefinition(File file, String tableName) {
                LOG.trace("Constructor");
                this.file = file;
                this.tableName = tableName == null ? DriverManager.DEFAULT_SINGLE_TABLE_NAME : tableName;
        }

        public FileSourceDefinition(File file, String[] dependingSources, String tableName) {
                LOG.trace("Constructor");
                this.file = file;
                this.tableName = tableName == null ? DriverManager.DEFAULT_SINGLE_TABLE_NAME : tableName;

                // adding dependencies
                if (dependingSources != null) {
                        sourceDependencies.addAll(Arrays.asList(dependingSources));
                }
        }

        public FileSourceDefinition(String fileName, String tableName) {
                this(new File(fileName), tableName);
        }

        @Override
        public DataSource createDataSource(String sourceName, ProgressMonitor pm)
                throws DataSourceCreationException {
                LOG.trace("Creating datasource");
                if (!file.exists()) {
                        throw new DataSourceCreationException(file + " "
                                + I18N.getString("gdms.datasource.error.noexits"));
                }
                Driver driver = getDriver();
                driver.setDataSourceFactory(getDataSourceFactory());

                FileDataSourceAdapter ds = new FileDataSourceAdapter(
                        getSource(sourceName), file, (FileDriver) driver, true);
                LOG.trace("Datasource created");
                return ds;
        }

        @Override
        protected Driver getDriverInstance() {
                return DriverUtilities.getDriver(getDataSourceFactory().getSourceManager().getDriverManager(), file);
        }

        public File getFile() {
                return file;
        }

        @Override
        public void createDataSource(DataSet contents, ProgressMonitor pm)
                throws DriverException {
                LOG.trace("Writing datasource to file");
                FileReadWriteDriver d = (FileReadWriteDriver) getDriver();
                d.setDataSourceFactory(getDataSourceFactory());

                boolean fileExisted = file.exists();
                try {
                        d.writeFile(file, contents, pm);
                } catch (DriverException e) {
                        if (!fileExisted) {
                                boolean ok = file.delete();
                                if (!ok) {
                                        LOG.warn("Failed to cleanup after error: deletion of "
                                                + file.getName() + " failed.");
                                }
                        }
                        throw e;
                }
        }

        @Override
        public DefinitionType getDefinition() {
                FileDefinitionType ret = new FileDefinitionType();
                ret.setPath(file.getAbsolutePath());
                return ret;
        }

        public static DataSourceDefinition createFromXML(
                FileDefinitionType definitionType) {
                return new FileSourceDefinition(definitionType.getPath(), definitionType.getTableName());
        }

        @Override
        public String calculateChecksum(DataSource open) throws DriverException {
                long lastModified = file.lastModified();
                return Long.toString(lastModified);
        }

        @Override
        public boolean equals(Object obj) {
                if (obj instanceof FileSourceDefinition) {
                        FileSourceDefinition fdsd = (FileSourceDefinition) obj;
                        return (file.equals(fdsd.file));
                } else {
                        return false;
                }
        }

        @Override
        public int hashCode() {
                return 9701 + (this.file != null ? this.file.getAbsolutePath().hashCode() : 0);
        }

        @Override
        public int getType() {
                if (cachedType == -1) {
                        try {
                                if (getDriver().getTypeName().equals("GDMS")) {
                                        GdmsDriver d = (GdmsDriver) getDriver();
                                        if (d.getFile().exists()) {
                                                d.open();
                                                int type = d.getType();
                                                d.close();
                                                cachedType = type;
                                        } else {
                                                cachedType = super.getType();
                                        }
                                } else {
                                        cachedType = super.getType();
                                }

                        } catch (DriverException ex) {
                                LOG.warn("Failed to get FSD type from driver. Going for default.", ex);
                                cachedType = super.getType();
                        }
                }
                return cachedType;
        }

        @Override
        public String getDriverTableName() {
                return tableName;
        }

        @Override
        public List<String> getSourceDependencies() throws DriverException {
                return sourceDependencies;
        }

        @Override
        public void delete() {
                Driver driver = getDriver();
                if (driver instanceof ShapefileDriver) {
                        try {
                                FileUtils.deleteSHPFiles(file);
                        } catch (IOException ex) {
                                throw new DriverLoadException("Cannot purge the file", ex);
                        }
                } else if (driver instanceof GdmsDriver) {
                        FileUtils.deleteFile(file);
                }
        }

        @Override
        public final void refresh() {
                cachedType = -1;
        }
}
