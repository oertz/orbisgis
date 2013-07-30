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
package org.orbisgis.view.toc.actions.cui.legends.panels;

import org.orbisgis.legend.structure.stroke.ConstantColorAndDashesPSLegend;
import org.orbisgis.legend.structure.stroke.constant.ConstantPenStroke;
import org.orbisgis.legend.structure.stroke.constant.ConstantPenStrokeLegend;
import org.orbisgis.legend.structure.stroke.constant.NullPenStrokeLegend;
import org.orbisgis.legend.thematic.SymbolizerLegend;
import org.orbisgis.legend.thematic.constant.IUniqueSymbolLine;
import org.orbisgis.sif.ComponentUtil;
import org.orbisgis.view.toc.actions.cui.components.CanvasSE;
import org.orbisgis.view.toc.actions.cui.legends.AbstractFieldPanel;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created with IntelliJ IDEA.
 * User: adam
 * Date: 29/07/13
 * Time: 16:40
 * To change this template use File | Settings | File Templates.
 */
public class LinePanel extends UniqueSymbolPanel {

    private static final I18n I18N = I18nFactory.getI18n(LinePanel.class);

    private ConstantPenStroke penStrokeMemory;

    private final boolean displayUom;
    private final boolean isLineOptional;
    private final boolean penStrokeIsConstant;

    private JCheckBox lineCheckBox;
    private ColorLabel colorLabel;
    private LineUOMComboBox lineUOMComboBox;
    private LineWidthSpinner lineWidthSpinner;
    private LineOpacitySpinner lineOpacitySpinner;
    private DashArrayField dashArrayField;

    public LinePanel(IUniqueSymbolLine legend,
                     CanvasSE preview,
                     String title,
                     boolean displayUom,
                     boolean isLineOptional) {
        super(legend, preview, title);
        this.displayUom = displayUom;
        this.isLineOptional = isLineOptional;
        penStrokeIsConstant =
                getLegend().getPenStroke() instanceof ConstantPenStrokeLegend;
        penStrokeMemory = getLegend().getPenStroke();
        init(displayUom);
        addComponents();
    }

    @Override
    protected IUniqueSymbolLine getLegend() {
        return (IUniqueSymbolLine) legend;
    }

    /**
     * Initialize the elements.
     *
     * @param displayUom      Whether the stroke UOM should be displayed
     */
    private void init(boolean displayUom) {
        colorLabel = new ColorLabel(preview, penStrokeMemory.getFillLegend());
        if (displayUom) {
            lineUOMComboBox =
                    new LineUOMComboBox((SymbolizerLegend) legend, preview);
        }
        if (isLineOptional) {
            lineCheckBox = new JCheckBox(I18N.tr("Enable"));
            lineCheckBox.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    onClickLineCheckBox();
                }
            });
        }
        lineWidthSpinner =
                new LineWidthSpinner(penStrokeMemory, preview);
        lineOpacitySpinner =
                new LineOpacitySpinner(penStrokeMemory.getFillLegend(), preview);
        dashArrayField =
                new DashArrayField((ConstantColorAndDashesPSLegend) penStrokeMemory, preview);
    }

    @Override
    protected void addComponents() {
        // Enable checkbox (if optional).
        if (isLineOptional) {
            add(lineCheckBox, "align l");
            lineCheckBox.setSelected(true);
        } else {
            // Just add blank space
            add(Box.createGlue());
        }
        // Line color
        add(colorLabel);

        // Unit of measure - line width
        if (displayUom) {
            JLabel uom = new JLabel(I18N.tr(AbstractFieldPanel.LINE_WIDTH_UNIT));
            add(uom);
            add(lineUOMComboBox, AbstractFieldPanel.COMBO_BOX_CONSTRAINTS);
        }
        // Line width
        add(new JLabel(I18N.tr(AbstractFieldPanel.WIDTH)));
        add(lineWidthSpinner, "growx");
        // Line opacity
        add(new JLabel(I18N.tr(AbstractFieldPanel.OPACITY)));
        add(lineOpacitySpinner, "growx");
        // Dash array
        add(new JLabel(I18N.tr(AbstractFieldPanel.DASH_ARRAY)));

        add(dashArrayField, "growx");
        if (isLineOptional) {
            setLineFieldsState(penStrokeIsConstant);
        }
    }

    /**
     * Change the state of all the fields used for the line configuration.
     *
     * @param enable
     */
    private void setLineFieldsState(boolean enable) {
        ComponentUtil.setFieldState(enable, colorLabel);
        if (displayUom) {
            if (lineUOMComboBox != null) {
                ComponentUtil.setFieldState(enable, lineUOMComboBox);
            }
        }
        ComponentUtil.setFieldState(enable, lineWidthSpinner);
        ComponentUtil.setFieldState(enable, lineOpacitySpinner);
        ComponentUtil.setFieldState(enable, dashArrayField);
    }

    /**
     * If the stroke is optional, a {@code JCheckBox} will be added in the
     * UI to let the user enable or disable the line configuration. In fact,
     * clicking on it will recursively enable or disable the containers
     * contained in the configuration panel.
     */
    private void onClickLineCheckBox() {
        if (lineCheckBox.isSelected()) {
            getLegend().setPenStroke(penStrokeMemory);
            setLineFieldsState(true);
        } else {
            // Remember the old configuration.
            penStrokeMemory = getLegend().getPenStroke();
            // We must replace the old PenStroke representation with
            // its null representation.
            getLegend().setPenStroke(new NullPenStrokeLegend());
            setLineFieldsState(false);
        }
        preview.imageChanged();
    }
}
