package org.orbisgis.core.ui.plugins.views.geocatalog.filters;

import org.gdms.source.SourceManager;

public class AlphanumericPlugIn implements IFilter {

	@Override
	public boolean accepts(SourceManager sm, String sourceName) {
		int type = sm.getSource(sourceName).getType();
		int spatial = SourceManager.VECTORIAL | SourceManager.RASTER
				| SourceManager.WMS;
		return (type & spatial) == 0;
	}

}