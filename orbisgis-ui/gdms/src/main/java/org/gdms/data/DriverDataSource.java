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
package org.gdms.data;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.log4j.Logger;

import org.gdms.data.edition.Commiter;
import org.gdms.data.indexes.FullIterator;
import org.gdms.data.indexes.IndexException;
import org.gdms.data.indexes.IndexQuery;
import org.gdms.data.indexes.IndexQueryException;
import org.gdms.data.indexes.ResultIterator;
import org.gdms.data.schema.Metadata;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;
import org.gdms.driver.DataSet;
import org.gdms.source.Source;

/**
 * Base class for all the DataSources that directly access a driver. getDriver()
 * returns a not null instance
 *
 * @author Fernando Gonzalez Cortes
 *
 */
public abstract class DriverDataSource extends DataSourceCommonImpl {

        private Source source;
        private List<DataSourceListener> listeners = new ArrayList<DataSourceListener>();
        private static final Logger LOG = Logger.getLogger(DriverDataSource.class);

        public DriverDataSource(Source source) {
                LOG.trace("Constructor");
                this.source = source;
        }

        @Override
        public void addDataSourceListener(DataSourceListener listener) {
                listeners.add(listener);
        }

        @Override
        public void removeDataSourceListener(DataSourceListener listener) {
                listeners.remove(listener);
        }

        protected void fireOpen(DataSource ds) {
                for (DataSourceListener listener : listeners) {
                        listener.open(ds);
                }
        }

        protected void fireCancel(DataSource ds) {
                for (DataSourceListener listener : listeners) {
                        listener.cancel(ds);
                }
        }

        protected void fireCommit(DataSource ds) {
                for (DataSourceListener listener : listeners) {
                        listener.commit(ds);
                }
        }

        @Override
        public Number[] getScope(int dimension) throws DriverException {
                return getDriverTable().getScope(dimension);
        }

        @Override
        public boolean isEditable() {
                return getDriver().isCommitable();
        }

        @Override
        public void commit() throws DriverException {
                throw new UnsupportedOperationException("This DataSource has "
                        + "no committing capabilities");
        }

        /**
         * @see org.gdms.driver.DataSet#getFieldValue(long, int)
         */
        @Override
        public synchronized Value getFieldValue(long rowIndex, int fieldId)
                throws DriverException {
                LOG.trace("Getting field at " + rowIndex);
                return getDriverTable().getFieldValue(rowIndex, fieldId);
        }

        /**
         * @see org.gdms.driver.DataSet#getRowCount()
         */
        @Override
        public long getRowCount() throws DriverException {
                if (getDriverTable() == null) {
                        throw new DriverException("The driver does not contains the table '"
                                + this.source.getDataSourceDefinition().getDriverTableName() + "'");
                }
                return getDriverTable().getRowCount();
        }

        /**
         * @see org.gdms.data.DataSource#getMetadata()
         */
        @Override
        public Metadata getMetadata() throws DriverException {
                return getDriver().getSchema().getTableByName(getDriverTableName());
        }

        @Override
        public Iterator<Integer> queryIndex(IndexQuery queryIndex)
                throws DriverException {
                try {
                        int[] ret = getDataSourceFactory().getIndexManager().queryIndex(
                                getName(), queryIndex);

                        if (ret != null) {
                                return new ResultIterator(ret);
                        } else {
                                return new FullIterator(this);
                        }
                } catch (IndexException e) {
                        throw new DriverException(e);
                } catch (IndexQueryException e) {
                        throw new DriverException(e);
                } catch (NoSuchTableException e) {
                        throw new DriverException(e);
                }
        }

        @Override
        public Commiter getCommiter() {
                return (Commiter) this;
        }

        @Override
        public String[] getReferencedSources() {
                return source.getReferencedSources();
        }

        @Override
        public Source getSource() {
                return source;
        }

        @Override
        public DataSet getDriverTable() {
                return getDriver().getTable(getDriverTableName());
        }

        @Override
        public String getDriverTableName() {
                return source.getDataSourceDefinition().getDriverTableName();
        }
}
