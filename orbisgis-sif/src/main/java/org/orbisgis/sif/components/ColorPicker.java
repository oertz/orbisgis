/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 * 
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
package org.orbisgis.sif.components;

import java.awt.Color;
import java.awt.Component;
import java.net.URL;
import javax.swing.JPanel;
import org.orbisgis.sif.UIPanel;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * ColorPicker.java
 *
 * Created on 23 de febrero de 2008, 16:53
 *
 * @author david
 */
public class ColorPicker extends JPanel implements UIPanel {

        private final static I18n i18n = I18nFactory.getI18n(ColorPicker.class);

        /**
         * Creates new form ColorPicker
         */
        public ColorPicker() {
                initComponents(null);
        }

        public ColorPicker(Color initialColor) {
                initComponents(initialColor);
        }

        /**
         * This method is called from within the constructor to initialize the
         * form. WARNING: Do NOT modify this code. The content of this method is
         * always regenerated by the Form Editor.
         */
        // <editor-fold defaultstate="collapsed" desc=" Generated Code
        // ">//GEN-BEGIN:initComponents
        private void initComponents(Color initialColor) {
                if (initialColor == null) {
                        jColorChooser1 = new javax.swing.JColorChooser();
                } else {
                        jColorChooser1 = new javax.swing.JColorChooser(initialColor);
                }

                javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
                this.setLayout(layout);
                layout.setHorizontalGroup(layout.createParallelGroup(
                        javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                        layout.createSequentialGroup().addContainerGap().addComponent(
                        jColorChooser1, javax.swing.GroupLayout.PREFERRED_SIZE,
                        javax.swing.GroupLayout.DEFAULT_SIZE,
                        javax.swing.GroupLayout.PREFERRED_SIZE).addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE,
                        Short.MAX_VALUE)));
                layout.setVerticalGroup(layout.createParallelGroup(
                        javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                        layout.createSequentialGroup().addContainerGap().addComponent(
                        jColorChooser1, javax.swing.GroupLayout.PREFERRED_SIZE,
                        javax.swing.GroupLayout.DEFAULT_SIZE,
                        javax.swing.GroupLayout.PREFERRED_SIZE).addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE,
                        Short.MAX_VALUE)));

        }// </editor-fold>//GEN-END:initComponents
        // Variables declaration - do not modify//GEN-BEGIN:variables
        private javax.swing.JColorChooser jColorChooser1;

        // End of variables declaration//GEN-END:variables
        public Color getColor() {
                return jColorChooser1.getColor();
        }

        @Override
        public Component getComponent() {
                return this;
        }

        @Override
        public URL getIconURL() {
                return null;
        }

        @Override
        public String getInfoText() {
                return i18n.tr("sif.ColorPicker.InfoText");
        }

        @Override
        public String getTitle() {
                return i18n.tr("sif.ColorPicker.Title");
        }
        

        @Override
        public String validateInput() {
                return null;
        }
}
