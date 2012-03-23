package org.gdms.data.system;

import java.io.File;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.DataSourceDefinition;
import org.gdms.data.file.FileDataSourceAdapter;
import org.gdms.data.file.FileSourceDefinition;
import org.gdms.driver.FileDriver;
import org.gdms.driver.driverManager.DriverManager;
import org.gdms.source.SourceManager;
import org.gdms.source.directory.DefinitionType;
import org.gdms.source.directory.SystemDefinitionType;
import org.orbisgis.progress.ProgressMonitor;
import org.orbisgis.utils.I18N;

public class SystemSourceDefinition extends FileSourceDefinition {

        public SystemSourceDefinition(SystemSource systemSource) {
                super(systemSource.getFile(), DriverManager.DEFAULT_SINGLE_TABLE_NAME);
        }

        @Override
        public DataSource createDataSource(String tableName, ProgressMonitor pm)
                throws DataSourceCreationException {
                if (!file.exists()) {
                        throw new DataSourceCreationException(file + " "
                                + I18N.getString("gdms.datasource.error.noexits"));
                }
                (getDriver()).setDataSourceFactory(getDataSourceFactory());

                FileDataSourceAdapter ds = new FileDataSourceAdapter(
                        getSource(tableName), file, (FileDriver) getDriver(), false);
                return ds;
        }

        @Override
        public int getType() {
                return SourceManager.SYSTEM_TABLE;
        }

        @Override
        public DefinitionType getDefinition() {
                SystemDefinitionType ret = new SystemDefinitionType();
                ret.setPath(file.getAbsolutePath());
                return ret;
        }

        public static DataSourceDefinition createFromXML(
                SystemDefinitionType definitionType) {
                return new SystemSourceDefinition(new SystemSource(new File(
                        definitionType.getPath())));
        }
}
