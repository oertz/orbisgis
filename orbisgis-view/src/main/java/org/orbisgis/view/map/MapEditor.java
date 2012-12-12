/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2012 IRSTV (FR CNRS 2488)
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
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.orbisgis.view.map;

import com.vividsolutions.jts.geom.Envelope;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;
import java.beans.EventHandler;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URI;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.*;
import javax.swing.event.TreeExpansionListener;
import org.apache.log4j.Logger;
import org.orbisgis.core.DataManager;
import org.orbisgis.core.Services;
import org.orbisgis.core.common.IntegerUnion;
import org.orbisgis.core.layerModel.ILayer;
import org.orbisgis.core.layerModel.LayerException;
import org.orbisgis.core.layerModel.MapContext;
import org.orbisgis.core.layerModel.OwsMapContext;
import org.orbisgis.core.map.MapTransform;
import org.orbisgis.core.map.TransformListener;
import org.orbisgis.progress.NullProgressMonitor;
import org.orbisgis.view.background.BackgroundJob;
import org.orbisgis.view.background.BackgroundManager;
import org.orbisgis.view.components.actions.ActionCommands;
import org.orbisgis.view.components.actions.ActionTools;
import org.orbisgis.view.components.actions.DefaultAction;
import org.orbisgis.view.components.button.DropDownButton;
import org.orbisgis.view.docking.DockingPanelParameters;
import org.orbisgis.view.edition.EditableElement;
import org.orbisgis.view.edition.EditorDockable;
import org.orbisgis.view.edition.EditorManager;
import org.orbisgis.view.geocatalog.EditableSource;
import org.orbisgis.view.icons.OrbisGISIcon;
import org.orbisgis.view.map.ext.MapEditorAction;
import org.orbisgis.view.map.jobs.CreateSourceFromSelection;
import org.orbisgis.view.map.jobs.ReadMapContextJob;
import org.orbisgis.view.map.jobs.ZoomToSelection;
import org.orbisgis.view.map.mapsManager.MapsManager;
import org.orbisgis.view.map.mapsManager.TreeLeafMapContextFile;
import org.orbisgis.view.map.mapsManager.TreeLeafMapElement;
import org.orbisgis.view.map.tool.Automaton;
import org.orbisgis.view.map.tool.TransitionException;
import org.orbisgis.view.map.tools.*;
import org.orbisgis.view.workspace.ViewWorkspace;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * The Map Editor Panel
 */
public class MapEditor extends JPanel implements EditorDockable, TransformListener   {
    private static final I18n I18N = I18nFactory.getI18n(MapEditor.class);
    private static final Logger GUILOGGER = Logger.getLogger("gui."+MapEditor.class);
    //The UID must be incremented when the serialization is not compatible with the new version of this class
    private static final long serialVersionUID = 1L; 
    private MapControl mapControl = new MapControl();
    private MapContext mapContext = null;
    private MapElement mapElement;
    private DockingPanelParameters dockingPanelParameters;
    private MapTransferHandler dragDropHandler;
    private MapStatusBar mapStatusBar = new MapStatusBar();
    //This timer will fetch the cursor component coordinates
    //Then translate to the map coordinates and send it to
    //the MapStatusBar
    private Timer CursorCoordinateLookupTimer;
    private static final int CURSOR_COORDINATE_LOOKUP_INTERVAL = 100; //Ms
    private Point lastCursorPosition = new Point();
    private Point lastTranslatedCursorPosition = new Point();
    private AtomicBoolean initialised = new AtomicBoolean(false);
    private MapsManager mapsManager = new MapsManager();
    private JLayeredPane layeredPane = new JLayeredPane();
    private ComponentListener sizeListener = EventHandler.create(ComponentListener.class,this,"updateMapControlSize",null,"componentResized");
    private PropertyChangeListener modificationListener = EventHandler.create(PropertyChangeListener.class,this,"onMapModified");
    private ActionCommands actions = new ActionCommands();
    /**
     * Constructor
     */
    public MapEditor() {
        super(new BorderLayout());
        dockingPanelParameters = new DockingPanelParameters();
        dockingPanelParameters.setName("map_editor");
        updateMapLabel();
        dockingPanelParameters.setTitleIcon(OrbisGISIcon.getIcon("map"));
        dockingPanelParameters.setMinimizable(false);
        dockingPanelParameters.setExternalizable(false);
        dockingPanelParameters.setCloseable(false);
        dockingPanelParameters.setLayout(new MapEditorPersistance());
        layeredPane.add(mapControl,1);
        layeredPane.add(mapsManager,0);
        mapsManager.setVisible(false);
        add(layeredPane, BorderLayout.CENTER);
        add(mapStatusBar, BorderLayout.PAGE_END);
        mapControl.setDefaultTool(new ZoomInTool());
        //Declare Tools of Map Editors
        //Add the tools in the docking Panel title
        dockingPanelParameters.setToolBar(createToolBar());
        //add(createToolBar(),BorderLayout.NORTH);
        //Set the Drop target
        dragDropHandler = new MapTransferHandler();        
        this.setTransferHandler(dragDropHandler);
    }
    private void updateMapLabel() {
                if (mapElement == null) {
                        dockingPanelParameters.setTitle(I18N.tr("Map"));
                } else {
                        if (mapElement.isModified()) {
                                dockingPanelParameters.setTitle(I18N.tr("Map Editor \"{0}\" [Modified]",mapElement.getMapContext().getTitle()));
                        } else {
                                dockingPanelParameters.setTitle(I18N.tr("Map Editor \"{0}\"",mapElement.getMapContext().getTitle()));
                        }
                }
        }
    public void onUserSetScaleDenominator(PropertyChangeEvent pce) throws PropertyVetoException {
            long newScale = (Long)pce.getNewValue();
            if(newScale<1) {
                    throw new PropertyVetoException(I18N.tr("The value of the scale denominator must be equal or greater than 1"),pce);
            }
            mapControl.getMapTransform().setScaleDenominator(newScale);
    }
        /**
         * Notifies this component that it now has a parent component. When this
         * method is invoked, the chain of parent components is set up with
         * KeyboardAction event listeners.
         */
        @Override
        public void addNotify() {
                super.addNotify();
                if (!initialised.getAndSet(true)) {
                        addComponentListener(sizeListener);
                        // Read the default map context file
                        initMapContext();
                        //Register listener
                        dragDropHandler.getTransferEditableEvent().addListener(this, EventHandler.create(MapTransferHandler.EditableTransferListener.class, this, "onDropEditable", "editableList"));
                        mapControl.addMouseMotionListener(EventHandler.create(MouseMotionListener.class, this, "onMouseMove", "point", "mouseMoved"));
                        mapStatusBar.addVetoableChangeListener(
                                MapStatusBar.PROP_USER_DEFINED_SCALE_DENOMINATOR,
                                EventHandler.create(VetoableChangeListener.class, this,
                                "onUserSetScaleDenominator", ""));
                        // Load the Remote Map Catalog servers and keep track for updates
                        mapsManager.setServerList(getMapEditorPersistance().getMapCatalogUrlList());
                        // When the tree is expanded update the manager size
                        mapsManager.getTree().addComponentListener(sizeListener);
                        mapsManager.getTree().addTreeExpansionListener(EventHandler.create(TreeExpansionListener.class,this,"updateMapControlSize"));
                        
                }
        }

        private void initMapContext() {
                BackgroundManager backgroundManager = Services.getService(BackgroundManager.class);
                ViewWorkspace viewWorkspace = Services.getService(ViewWorkspace.class);
                
                File serialisedMapContextPath = new File(viewWorkspace.getMapContextPath() + File.separator, getMapEditorPersistance().getDefaultMapContext());
                if(!serialisedMapContextPath.exists()) {
                        createDefaultMapContext();
                } else {
                        TreeLeafMapElement defaultMap = (TreeLeafMapElement)mapsManager.getFactoryManager().create(serialisedMapContextPath);
                        try {
                                MapElement mapElement = defaultMap.getMapElement(new NullProgressMonitor());
                                backgroundManager.backgroundOperation(new ReadMapContextJob(mapElement));
                        } catch(IllegalArgumentException ex) {
                                //Map XML is invalid
                                GUILOGGER.warn(I18N.tr("Fail to load the map context, starting with an empty map context"),ex);
                                createDefaultMapContext();
                        }
                }
        }

       /**
        * Create the default map context, create it if the map folder is empty
        */
        private static void createDefaultMapContext() {
                BackgroundManager backgroundManager = Services.getService(BackgroundManager.class);
                ViewWorkspace viewWorkspace = Services.getService(ViewWorkspace.class);

                MapContext defaultMapContext = new OwsMapContext();             
                

                //Load the map context
                File mapContextFolder = new File(viewWorkspace.getMapContextPath());
                if (!mapContextFolder.exists()) {
                        mapContextFolder.mkdir();
                }
                File mapContextFile = new File(mapContextFolder, I18N.tr("MyMap.ows"));
                
                if (!mapContextFile.exists()) {
                        //Create an empty map context
                        TreeLeafMapContextFile.createEmptyMapContext(mapContextFile);
                }                
                try {
                        defaultMapContext.read(new FileInputStream(mapContextFile));
                } catch (FileNotFoundException ex) {
                        GUILOGGER.error(I18N.tr("The saved map context cannot be read, starting with an empty map context."), ex);
                } catch (IllegalArgumentException ex) {
                        GUILOGGER.error(I18N.tr("The saved map context cannot be read, starting with an empty map context."), ex);
                }
                MapElement editableMap = new MapElement(defaultMapContext, mapContextFile);
                backgroundManager.backgroundOperation(new ReadMapContextJob(editableMap));
        }
        
        /**
         * Compute the appropriate components bounds for MapControl
         * and MapsManager and apply theses bounds
         */
        public void updateMapControlSize() {
                SwingUtilities.invokeLater(new Runnable() {

                        @Override
                        public void run() {
                                doUpdateMapControlSize();
                        }
                });
        }
        private void doUpdateMapControlSize() {
                mapControl.setBounds(0,0,layeredPane.getWidth(),layeredPane.getHeight());
                if(mapsManager.isVisible()) {
                        Dimension mapsManagerPreferredSize = mapsManager.getMinimalComponentDimension();
                        int hPos = layeredPane.getWidth() - Math.min(mapsManagerPreferredSize.width,layeredPane.getWidth());
                        mapsManager.setBounds(hPos,0,Math.min(mapsManagerPreferredSize.width,layeredPane.getWidth()),Math.min(mapsManagerPreferredSize.height,layeredPane.getHeight()));
                        mapsManager.revalidate();
                }                
        }
    /**
     * The user Drop a list of Editable
     * @param editableList
     */
    public void onDropEditable(EditableElement[] editableList) {
        BackgroundManager bm = Services.getService(BackgroundManager.class);
        //Load the layers in the background
        bm.nonBlockingBackgroundOperation(new DropDataSourceProcess(editableList));
    }
    
    /**
     * Load a new map context
     * @param element 
     */
    private void loadMap(MapElement element) {
        try {      
            removeListeners();    
            mapElement = element;
            mapContext = (MapContext) element.getObject();
            //We (unfortunately) need a cross reference here : this way, we'll
            //be able to retrieve the MapTransform from the Toc..
            element.setMapEditor(this);
            mapControl.setMapContext(mapContext);
            mapControl.getMapTransform().setExtent(mapContext.getBoundingBox());
            mapControl.setElement(this);
            mapControl.initMapControl();
            CursorCoordinateLookupTimer = new Timer(CURSOR_COORDINATE_LOOKUP_INTERVAL,
                    EventHandler.create(ActionListener.class,this,"onReadCursorMapCoordinate"));
            CursorCoordinateLookupTimer.setRepeats(false);
            CursorCoordinateLookupTimer.start();
            // Update the default map context path with the relative path
            ViewWorkspace viewWorkspace = Services.getService(ViewWorkspace.class);
            URI rootDir =(new File(viewWorkspace.getMapContextPath()+File.separator)).toURI();
            String relative = rootDir.relativize(element.getMapContextFile().toURI()).getPath();
            getMapEditorPersistance().setDefaultMapContext(relative);
            // Set the loaded map hint to the MapCatalog
            mapsManager.setLoadedMap(element.getMapContextFile());
            // Update the editor label with the new editable name
            updateMapLabel();
            mapElement.addPropertyChangeListener(MapElement.PROP_MODIFIED, modificationListener);
            repaint();
        } catch (IllegalStateException ex) {
            GUILOGGER.error(ex);
        } catch (TransitionException ex) {
            GUILOGGER.error(ex);
        }        
    }
    private MapEditorPersistance getMapEditorPersistance() {
            return ((MapEditorPersistance)dockingPanelParameters.getLayout());
    }
    /**
     * MouseMove event on the MapControl
     * @param mousePoition x,y position of the event relative to the MapControl component.
     */
    public void onMouseMove(Point mousePoition) {
            lastCursorPosition = mousePoition;
    }

        @Override
        public void removeNotify() {
                super.removeNotify();
                if(CursorCoordinateLookupTimer!=null) {
                        CursorCoordinateLookupTimer.stop();
                        CursorCoordinateLookupTimer=null;
                }
                removeListeners();
        }

        
        public MapContext getMapContext() {
                return mapContext;
        }
    
    
    
    /**
     * This method is called by the timer called CursorCoordinateLookupTimer
     * This function fetch the cursor coordinates (pixel)
     * then translate to the map coordinates and send it to
     * the MapStatusBar
     */
    public void onReadCursorMapCoordinate() {
            try {
                if(!lastTranslatedCursorPosition.equals(lastCursorPosition)) {
                        lastTranslatedCursorPosition=lastCursorPosition;
                        Point2D mapCoordinate = mapControl.getMapTransform().toMapPoint(lastCursorPosition.x, lastCursorPosition.y);
                        mapStatusBar.setCursorCoordinates(mapCoordinate);
                }                
            } finally {
                if(CursorCoordinateLookupTimer!=null) {
                        CursorCoordinateLookupTimer.start();
                }
            }
    }

    /**
     * MapEditor tools declaration
     */
    private void createActions() {
        // Navigation tools
        actions.addAction(new AutomatonAction(MapEditorAction.A_ZOOM_IN,new ZoomInTool()).setLogicalGroup("navigation"));
        actions.addAction(new AutomatonAction(MapEditorAction.A_ZOOM_OUT,new ZoomOutTool()).setLogicalGroup("navigation"));
        actions.addAction(new AutomatonAction(MapEditorAction.A_PAN,new PanTool()).setLogicalGroup("navigation"));
        actions.addAction(new DefaultAction(MapEditorAction.A_FULL_EXTENT,I18N.tr("Full extent"),
                OrbisGISIcon.getIcon("world"),EventHandler.create(ActionListener.class,this,"onFullExtent"))
                .setToolTipText(I18N.tr("Zoom to show all geometries")).setLogicalGroup("navigation"));

        // Selection tools
        actions.addAction(new AutomatonAction(MapEditorAction.A_SELECTION,new SelectionTool()).setLogicalGroup("selection"));
        actions.addAction(new DefaultAction(MapEditorAction.A_CLEAR_SELECTION, I18N.tr("Clear selection"),
                OrbisGISIcon.getIcon("edit-clear"),EventHandler.create(ActionListener.class,this,"onClearSelection"))
                .setToolTipText(I18N.tr("Clear all selected geometries of all layers")).setLogicalGroup("selection"));
        actions.addAction(new DefaultAction(MapEditorAction.A_ZOOM_SELECTION, I18N.tr("Zoom to selection"),
                OrbisGISIcon.getIcon("zoom_selected"),EventHandler.create(ActionListener.class,this,"onZoomToSelection"))
                .setToolTipText(I18N.tr("Zoom to visible selected geometries")).setLogicalGroup("selection"));
        actions.addAction(new DefaultAction(MapEditorAction.A_DATA_SOURCE_FROM_SELECTION, I18N.tr("Create a datasource"),
                OrbisGISIcon.getIcon("table_go"),
                EventHandler.create(ActionListener.class,this,"onCreateDataSourceFromSelection"))
                .setToolTipText(I18N.tr("Create a datasource from a selection")).setLogicalGroup("selection"));

        // Measure tools
        actions.addAction(new DefaultAction(MapEditorAction.A_MEASURE_GROUP,I18N.tr("Mesure tools")).setMenuGroup(true));
        actions.addAction(new AutomatonAction(MapEditorAction.A_MEASURE_LINE,new MesureLineTool())
                .setParent(MapEditorAction.A_MEASURE_GROUP));
        actions.addAction(new AutomatonAction(MapEditorAction.A_MEASURE_POLYGON,new MesurePolygonTool())
                .setParent(MapEditorAction.A_MEASURE_GROUP));
        actions.addAction(new AutomatonAction(MapEditorAction.A_COMPASS,new CompassTool())
                .setParent(MapEditorAction.A_MEASURE_GROUP));

        // Drawing tools
        actions.addAction(new DefaultAction(MapEditorAction.A_DRAWING_GROUP,I18N.tr("Graphic tools")).setMenuGroup(true));
        actions.addAction(new AutomatonAction(MapEditorAction.A_FENCE,new FencePolygonTool())
                .setParent(MapEditorAction.A_DRAWING_GROUP));
        actions.addAction(new AutomatonAction(MapEditorAction.A_PICK_COORDINATES, new PickCoordinatesPointTool())
                .setParent(MapEditorAction.A_DRAWING_GROUP));

        // Maps manager
        actions.addAction(new DefaultAction(MapEditorAction.A_MAP_TREE, I18N.tr("Maps tree"),
                OrbisGISIcon.getIcon("map"),
                EventHandler.create(ActionListener.class,this,"onShowHideMapsTree"))
                .setToolTipText(I18N.tr("Show/Hide maps tree")));
    }


    private JToolBar createToolBar() {
        JToolBar toolBar = new JToolBar();
        createActions();
        actions.registerContainer(toolBar);
        return toolBar;
    }

    /**
     * User click on the Show/Hide maps tree
     */
    public void onShowHideMapsTree() {
            if(!mapsManager.isVisible()) {
                    mapsManager.setVisible(true);
                    updateMapControlSize();
            } else {
                    mapsManager.setVisible(false);
            }
    }

    /**
     * The user click on the button Full Extent
     */
    public void onFullExtent() {
        mapControl.getMapTransform().setExtent(mapContext.getLayerModel().getEnvelope());
    }
    
    /**
     * The user click on the button clear selection
     */
    public void onClearSelection() {
            for(ILayer layer : mapContext.getLayers()) {
                    if(!layer.acceptsChilds()) {
                        layer.setSelection(new IntegerUnion());
                    }
            }
    }
    
    /**
     * The user click on the button Zoom to selection
     */
    public void onZoomToSelection() {
            BackgroundManager bm = Services.getService(BackgroundManager.class);
            bm.backgroundOperation(new ZoomToSelection(mapContext, mapContext.getLayers()));
    }
    
    
     /**
     * The user can export the selected features into a new datasource
     */
    public void onCreateDataSourceFromSelection() {
            ILayer[] layers = mapContext.getSelectedLayers();
            if(layers!=null|| layers.length>0){
            for (ILayer layer : layers) {
                    Set<Integer> selection = layer.getSelection();
                    if(!selection.isEmpty()){
                    BackgroundManager bm = Services.getService(BackgroundManager.class);
                    bm.backgroundOperation(new CreateSourceFromSelection(layer.getDataSource(), selection));
            }       }
            }
    }
        
    /**
     * Give information on the behaviour of this panel related to the current
     * docking system
     * @return The panel parameter instance
     */
    @Override
    public DockingPanelParameters getDockingParameters() {
        return dockingPanelParameters;
    }

    @Override
    public JComponent getComponent() {
        return this;
    }
    /**
     * The user click on a Map Tool
     * @param automaton 
     */
    public void onToolSelected(Automaton automaton) {
        GUILOGGER.debug("Choose the tool named "+automaton.getName());
        try {
            mapControl.setTool(automaton);
        } catch (TransitionException ex) {
            GUILOGGER.error(I18N.tr("Unable to choose this tool"),ex);
        }
    }

    /**
     * Gets the {@link MapControl} linked to this {@code MapEditor}.
     * @return
     */
    public MapControl getMapControl(){
            return mapControl;
    }

    @Override
    public void extentChanged(Envelope oldExtent, MapTransform mapTransform) {
            //Update the scale in the MapEditor status bar
            mapStatusBar.setScaleDenominator(mapTransform.getScaleDenominator());
    }

    @Override
    public void imageSizeChanged(int oldWidth, int oldHeight, MapTransform mapTransform) {
        //do nothing
    }

        @Override
        public boolean match(EditableElement editableElement) {
                return editableElement instanceof MapElement;
        }

        @Override
        public EditableElement getEditableElement() {
                return mapElement;
        }

        @Override
        public void setEditableElement(EditableElement editableElement) {
                if(editableElement instanceof MapElement) {
                        loadMap((MapElement)editableElement);
                }
        }

    private class AutomatonAction extends DefaultAction {
        private Automaton automaton;

        private AutomatonAction(String actionId, Automaton automaton) {
            super(actionId, automaton.getName());
            this.automaton = automaton;
            putValue(Action.SMALL_ICON,automaton.getImageIcon());
            putValue(Action.LARGE_ICON_KEY,automaton.getImageIcon());
            putValue(Action.SHORT_DESCRIPTION,automaton.getTooltip());
            putValue(ActionTools.TOGGLE_GROUP,"automatons"); //radio group
        }

        @Override
        public void actionPerformed(ActionEvent ae) {
            onToolSelected(automaton);
        }
    }
    /**
     * Remove the listeners on the current loaded document
     */
    private void removeListeners() {
            if(mapElement!=null) {
                    mapElement.removePropertyChangeListener(modificationListener);
            }
    }
    
    /**
     * The editable modified state is switching
     */
    public void onMapModified() {
            SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                                updateMapLabel();
                                mapsManager.updateDiskTree();
                        }
                });
    }
    /**
     * This task is created when the user Drag Source from GeoCatalog
     * to the map directly. The layer is created directly on the root.
     */
    private class DropDataSourceProcess implements BackgroundJob {
        private EditableElement[] editableList;

        public DropDataSourceProcess(EditableElement[] editableList) {
            this.editableList = editableList;
        }
        
        @Override
        public void run(org.orbisgis.progress.ProgressMonitor pm) {
            DataManager dataManager = Services.getService(DataManager.class);
            ILayer dropLayer = mapContext.getLayerModel();
            int i=0;
            for(EditableElement eElement : editableList) {
                pm.progressTo(100 * i++ / editableList.length);
                if(eElement instanceof EditableSource) {
                    String sourceName = ((EditableSource)eElement).getId();
                    try {
                        dropLayer.addLayer(dataManager.createLayer(sourceName));
                    } catch (LayerException e) {
                        //This layer can not be inserted, we continue to the next layer
                        GUILOGGER.warn(I18N.tr("Unable to create and drop the layer"),e);
                    }
                } else if(eElement instanceof MapElement) {
                        final MapElement mapElement = (MapElement)eElement;
                        mapElement.open(pm);
                        SwingUtilities.invokeLater(new Runnable() {
                                                @Override
                                                public void run() {                                                        
                                                        EditorManager em = Services.getService(EditorManager.class);                        
                                                        em.openEditable(mapElement);  
                                                }
                                        });                              
                        return;
                }
            }
        }

        @Override
        public String getTaskName() {
            return I18N.tr("Load the data source droped into the map editor.");
        }    
        
    }
}
