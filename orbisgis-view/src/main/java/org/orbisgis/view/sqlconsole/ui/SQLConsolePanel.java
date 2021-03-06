/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. 
 * 
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
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
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.orbisgis.view.sqlconsole.ui;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.orbisgis.core.Services;
import org.orbisgis.sif.UIFactory;
import org.orbisgis.sif.components.OpenFilePanel;
import org.orbisgis.sif.components.SaveFilePanel;
import org.orbisgis.sqlparserapi.ScriptSplitterFactory;
import org.orbisgis.view.background.BackgroundManager;
import org.orbisgis.view.components.actions.ActionCommands;
import org.orbisgis.view.components.findReplace.FindReplaceDialog;
import org.orbisgis.view.icons.OrbisGISIcon;
import org.orbisgis.view.sqlconsole.actions.ExecuteScriptProcess;
import org.orbisgis.view.sqlconsole.blockComment.QuoteSQL;
import org.orbisgis.view.sqlconsole.codereformat.CodeReformator;
import org.orbisgis.view.sqlconsole.codereformat.CommentSpec;
import org.orbisgis.view.util.CommentUtil;
import org.orbisgis.viewapi.components.actions.DefaultAction;
import org.orbisgis.viewapi.sqlconsole.ui.ext.SQLAction;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import javax.sql.DataSource;
import javax.swing.*;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.beans.EventHandler;
import java.io.IOException;

/**
 * SQL Panel that contain a RSyntaxTextArea
 */
public class SQLConsolePanel extends JPanel {
        private static final long serialVersionUID = 1L;
        protected final static I18n I18N = I18nFactory.getI18n(SQLConsolePanel.class);
        private final static Logger LOGGER = Logger.getLogger("gui." + SQLConsolePanel.class);
        private static final String DEFAULT_STATUS_MESSAGE = I18n.marktr("Drop source here to get all table columns");
        private ScriptSplitterFactory splitterFactory;
        
        // Components
        private JToolBar infoToolBar;
        
        private RTextScrollPane centerPanel;
        private RSyntaxTextArea scriptPanel;
        private CodeReformator codeReformator;
        private JLabel statusMessage;
        private Timer messageCleanTimer;
        private int lastSQLStatementToReformatStart;
        private int lastSQLStatementToReformatEnd;
        private static final String MESSAGEBASE = "%d | %d | %s";
        private int line = 0;
        private int character = 0;
        private String message = "";
        static CommentSpec[] COMMENT_SPECS = new CommentSpec[]{
                new CommentSpec("/*", "*/"), new CommentSpec("--", "\n")};
        private FindReplaceDialog findReplaceDialog;
        private ActionCommands actions = new ActionCommands();
        private SQLFunctionsPanel sqlFunctionsPanel;
        private DefaultAction executeAction;
        private DefaultAction clearAction;
        private DefaultAction findAction;
        private DefaultAction quoteAction;
        private DefaultAction unQuoteAction;
        private DefaultAction commentAction;
        private DefaultAction blockCommentAction;
        private DefaultAction formatSQLAction;
        private DefaultAction saveAction;
        private DataSource dataSource;
        
        /**
         * Creates a console for sql.
         */
        public SQLConsolePanel(DataSource dataSource) {
                super(new BorderLayout());
                this.dataSource = dataSource;
                sqlFunctionsPanel = new SQLFunctionsPanel(dataSource);
                initActions();
                JPanel split = new JPanel();
                split.setLayout(new BorderLayout());
                split.add(sqlFunctionsPanel, BorderLayout.EAST);
                split.add(getCenterPanel(), BorderLayout.CENTER);
                add(split, BorderLayout.CENTER);
                add(getStatusToolBar(), BorderLayout.SOUTH);
        }

        /**
         * @param splitterFactory The component used to split sql script into single query
         */
        public void setSplitterFactory(ScriptSplitterFactory splitterFactory) {
            this.splitterFactory = splitterFactory;
        }

        /**
         * Create actions instances
         * 
         * Each action is put in the Popup menu and the tool bar
         * Their shortcuts are registered also in the editor
         */
        private void initActions() {
                //Execute Action
                executeAction = new DefaultAction(SQLAction.A_EXECUTE,
                        I18N.tr("Execute"),
                        I18N.tr("Run SQL statements"),
                        OrbisGISIcon.getIcon("execute"),
                        EventHandler.create(ActionListener.class,this,"onExecute"),
                        KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, InputEvent.CTRL_DOWN_MASK)
                        ).setLogicalGroup("custom");
                actions.addAction(executeAction);
                //Clear action
                clearAction = new DefaultAction(SQLAction.A_CLEAR,
                        I18N.tr("Clear"),
                        I18N.tr("Erase the content of the editor"),
                        OrbisGISIcon.getIcon("erase"),
                        EventHandler.create(ActionListener.class,this,"onClear"),
                        null
                       ).setLogicalGroup("custom").setAfter(SQLAction.A_EXECUTE);
                actions.addAction(clearAction);
                //Find action
                findAction=new DefaultAction(SQLAction.A_SEARCH,
                        I18N.tr("Search.."),
                        I18N.tr("Search text in the document"),
                        OrbisGISIcon.getIcon("find"),
                        EventHandler.create(ActionListener.class,this,"openFindReplaceDialog"),
                        KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_DOWN_MASK)
                       ).addStroke(KeyStroke.getKeyStroke(KeyEvent.VK_H, InputEvent.CTRL_DOWN_MASK))
                        .setLogicalGroup("custom");
                actions.addAction(findAction);
                
                //Quote
                quoteAction = new DefaultAction(SQLAction.A_QUOTE,
                        I18N.tr("Quote"),
                        I18N.tr("Quote selected text"),
                        null,
                        EventHandler.create(ActionListener.class,this,"onQuote"),
                        KeyStroke.getKeyStroke(KeyEvent.VK_SLASH, InputEvent.SHIFT_DOWN_MASK)
                       ).setLogicalGroup("format");
                actions.addAction(quoteAction);
                //unQuote
                unQuoteAction =new DefaultAction(SQLAction.A_UNQUOTE,
                        I18N.tr("Un Quote"),
                        I18N.tr("Un Quote selected text"),
                        null,
                        EventHandler.create(ActionListener.class,this,"onUnQuote"),
                        KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SLASH, InputEvent.SHIFT_DOWN_MASK)
                       ).setLogicalGroup("format");
                actions.addAction(unQuoteAction);

                // Comment/Uncomment
                commentAction = new DefaultAction(SQLAction.A_COMMENT,
                        I18N.tr("(Un)comment"),
                        I18N.tr("(Un)comment the selected text"),
                        null,
                        EventHandler.create(ActionListener.class, this, "onComment"),
                        KeyStroke.getKeyStroke("alt C")
                ).setLogicalGroup("format");
                actions.addAction(commentAction);

                // Block Comment/Uncomment
                blockCommentAction = new DefaultAction(SQLAction.A_BLOCKCOMMENT,
                        I18N.tr("Block (un)comment"),
                        I18N.tr("Block (un)comment the selected text."),
                        null,
                        EventHandler.create(ActionListener.class, this, "onBlockComment"),
                        KeyStroke.getKeyStroke("alt shift C")
                ).setLogicalGroup("format");
                actions.addAction(blockCommentAction);

                //Format SQL
                formatSQLAction = new DefaultAction(SQLAction.A_FORMAT,
                        I18N.tr("Format"),
                        I18N.tr("Format editor content"),
                        null,
                        EventHandler.create(ActionListener.class,this,"onFormatCode"),
                        KeyStroke.getKeyStroke("alt shift F")
                       ).setLogicalGroup("format");
                actions.addAction(formatSQLAction);
                
                //Save
                saveAction = new DefaultAction(SQLAction.A_SAVE,
                        I18N.tr("Save"),
                        I18N.tr("Save the editor content into a file"),
                        OrbisGISIcon.getIcon("save"),
                        EventHandler.create(ActionListener.class,this,"onSaveFile"),
                        KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK)
                       ).setLogicalGroup("custom");
                actions.addAction(saveAction);
                //Open action
                actions.addAction(new DefaultAction(SQLAction.A_OPEN,
                        I18N.tr("Open"),
                        I18N.tr("Load a file in this editor"),
                        OrbisGISIcon.getIcon("open"),
                        EventHandler.create(ActionListener.class,this,"onOpenFile"),
                        KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK)
                ).setLogicalGroup("custom"));
                //ShowHide function list
                actions.addAction(new DefaultAction(SQLAction.A_SQL_LIST,
                        I18N.tr("SQL list"),
                        I18N.tr("Show/Hide SQL function list"),
                        OrbisGISIcon.getIcon("builtinfunctionmap"),
                        EventHandler.create(ActionListener.class,sqlFunctionsPanel,"switchPanelVisibilityState"),
                        null).setLogicalGroup("custom"));
        }

        /**
         * Get the ActionCommands instance use by SQLConsole.
         * @return ActionCommands instance
         */
        public ActionCommands getActions() {
            return actions;
        }
        
        private RTextScrollPane getCenterPanel() {
                if (centerPanel == null) {
                        scriptPanel = new RSyntaxTextArea();
                        scriptPanel.setSyntaxEditingStyle(RSyntaxTextArea.SYNTAX_STYLE_SQL);
                        scriptPanel.setLineWrap(true);
                        scriptPanel.setClearWhitespaceLinesEnabled(true);
                        scriptPanel.setMarkOccurrences(false);
                        scriptPanel.setTabsEmulated(true);
                        scriptPanel.setTabSize(4);
                        actions.setAccelerators(scriptPanel);
                        //TODO track language support bundles

                        codeReformator = new CodeReformator(";",
                                COMMENT_SPECS);
                        scriptPanel.addCaretListener(EventHandler.create(CaretListener.class,this,"onScriptPanelCaretUpdate"));
                        scriptPanel.getDocument().addDocumentListener(EventHandler.create(DocumentListener.class, this, "onUserSelectionChange"));
                       
                        //Add custom actions
                        scriptPanel.getPopupMenu().addSeparator();
                        actions.registerContainer(scriptPanel.getPopupMenu());
                        centerPanel = new RTextScrollPane(scriptPanel);
                        onUserSelectionChange();
                }
                return centerPanel;
        }
        /**
         * Run the Sql commands stored in the editor
         */
        public void onExecute() {      
                if (scriptPanel.getDocument().getLength() > 0) {
                    BackgroundManager bm = Services.getService(BackgroundManager.class);
                    bm.nonBlockingBackgroundOperation(new ExecuteScriptProcess(this, dataSource, splitterFactory));
                }
        }
               
        /**
         * Open a dialog that let the user to select a file and save the content
         * of the sql editor into this file.
         */
        public void onSaveFile() {
                final SaveFilePanel outfilePanel = new SaveFilePanel(
                        "sqlConsoleOutFile", I18N.tr("Save script"));
                outfilePanel.addFilter("sql", I18N.tr("SQL script (*.sql)"));
                outfilePanel.loadState();
                if (UIFactory.showDialog(outfilePanel)) {
                        try {
                        FileUtils.writeStringToFile(outfilePanel.getSelectedFile(), scriptPanel.getText());
                        } catch (IOException e1) {
                                LOGGER.error(I18N.tr("IO error."), e1);
                                return;
                        }
                        setStatusMessage(I18N.tr("The file has been saved."));

                } else {
                        setStatusMessage("");
                }
        }

        /**
         * Open a dialog that let the user to select a file
         * and add or replace the content of the sql editor.
         */
        public void onOpenFile() {
                final OpenFilePanel inFilePanel = new OpenFilePanel("sqlConsoleInFile",
                        I18N.tr("Open script"));
                inFilePanel.addFilter("sql", I18N.tr("SQL script (*.sql)"));
                inFilePanel.loadState();
                if (UIFactory.showDialog(inFilePanel)) {
                        int answer = JOptionPane.NO_OPTION;
                        if (scriptPanel.getDocument().getLength() > 0) {
                                answer = JOptionPane.showConfirmDialog(
                                        this,
                                        I18N.tr("Do you want to clear all before loading the file ?"),
                                        I18N.tr("Open file"),
                                        JOptionPane.YES_NO_CANCEL_OPTION);
                        }

                        String text;
                        try {
                                text = FileUtils.readFileToString(inFilePanel.getSelectedFile());
                        } catch (IOException e1) {
                                LOGGER.error(I18N.tr("IO error."), e1);
                                return;
                        }
                        
                        if (answer == JOptionPane.YES_OPTION) {
                                scriptPanel.setText(text);
                        } else if (answer == JOptionPane.NO_OPTION) {
                                scriptPanel.append(text);
                        }
                }
        }

        
        /**
         * Add quote to the selected sql
         */
        public void onQuote() {
                QuoteSQL.quoteSQL(this, false);
        }
        /**
         * Remove quote to the selected sql
         */
        public void onUnQuote() {
                QuoteSQL.unquoteSQL(this);
        }

        /**
         * (Un)comment the selected text.
         */
        public void onComment() {
            CommentUtil.commentOrUncommentSQL(scriptPanel);
        }

        /**
         * Block (un)comment the selected text.
         */
        public void onBlockComment() {
            CommentUtil.blockCommentOrUncomment(scriptPanel);
        }

        /**
         * Format SQL code
         */
        public void onFormatCode() {
                replaceCurrentSQLStatement(
                codeReformator.reformat(getCurrentSQLStatement()));
        }
        
        /**
         * Prompt the user to accept the document cleaning.
         */
        public void onClear() {
                if(scriptPanel.getDocument().getLength()!=0) {
                        int answer = JOptionPane.showConfirmDialog(this,
                                I18N.tr("Do you want to clear the contents of the console?"),
                                I18N.tr("Clear script"), JOptionPane.YES_NO_OPTION);
                        if (answer == JOptionPane.YES_OPTION) {
                                scriptPanel.setText("");
                        }
                }
        }
        /**
         * Update the row:column label
         */
        public void onScriptPanelCaretUpdate() {
                line = scriptPanel.getCaretLineNumber() + 1;
                character = scriptPanel.getCaretOffsetFromLineStart();
                setStatusMessage(message);
        }
        private JToolBar getStatusToolBar() {

                if (infoToolBar == null) {
                        infoToolBar = new JToolBar();
                        infoToolBar.setTransferHandler(new ScriptPanelTransferHandler(scriptPanel, dataSource));
                        statusMessage = new JLabel();
                        infoToolBar.add(statusMessage);
                        infoToolBar.setFloatable(false);

                        messageCleanTimer = new Timer(5000, new ActionListener() {

                                @Override
                                public void actionPerformed(ActionEvent e) {
                                        setStatusMessage("");
                                }
                        });
                        messageCleanTimer.setRepeats(false);
                        setStatusMessage("");
                }

                return infoToolBar;
        }

        public final void setStatusMessage(String messageToShow) {
                this.message = messageToShow;
                if (!message.isEmpty()) {
                    messageCleanTimer.restart();
                } else {
                    // Empty message mean show default message
                    messageToShow = I18N.tr(DEFAULT_STATUS_MESSAGE);
                }
                statusMessage.setText(String.format(MESSAGEBASE, line, character, messageToShow));
        }

        public void setCharacter(int character) {
                this.character = character;
        }

        public void setLine(int line) {
                this.line = line;
        }

        public String getText() {
                return scriptPanel.getText();
        }

        public RSyntaxTextArea getScriptPanel() {
                return scriptPanel;
        }

        public String getCurrentSQLStatement() {
                String sql = scriptPanel.getSelectedText();
                lastSQLStatementToReformatEnd = scriptPanel.getSelectionEnd();
                lastSQLStatementToReformatStart = scriptPanel.getSelectionStart();
                if (sql == null || sql.trim().length() == 0) {
                        sql = getText();
                        lastSQLStatementToReformatEnd = -2;
                        // int[] bounds = getBoundsOfCurrentSQLStatement();
                        //
                        // if (bounds[0] >= bounds[1]) {
                        // sql = "";
                        // } else {
                        // sql = sql.substring(bounds[0], bounds[1]).trim();
                        // }
                }
                return sql != null ? sql : "";
        }

        public void replaceCurrentSQLStatement(String st) {

                if (lastSQLStatementToReformatStart >= lastSQLStatementToReformatEnd) {
                        scriptPanel.replaceRange(st, 0, scriptPanel.getDocument().getLength());
                } else {
                        scriptPanel.replaceRange(st, lastSQLStatementToReformatStart,
                                lastSQLStatementToReformatEnd);
                }
        }

        public int[] getBoundsOfCurrentSQLStatement() {
                int[] bounds = new int[2];
                bounds[0] = scriptPanel.getSelectionStart();
                bounds[1] = scriptPanel.getSelectionEnd();

                if (bounds[0] == bounds[1]) {
                        bounds = getSqlBoundsBySeparatorRule(scriptPanel.getCaretPosition());
                }

                return bounds;
        }

        private int[] getSqlBoundsBySeparatorRule(int iCaretPos) {
                int[] bounds = new int[2];

                String sql = getText();

                bounds[0] = lastIndexOfStateSep(sql, iCaretPos);
                bounds[1] = indexOfStateSep(sql, iCaretPos);

                return bounds;

        }

        private static int indexOfStateSep(String sql, int pos) {
                int ix = pos;

                int newLinteCount = 0;
                for (;;) {
                        if (sql.length() == ix) {
                                return sql.length();
                        }

                        if (false == Character.isWhitespace(sql.charAt(ix))) {
                                newLinteCount = 0;
                        }

                        if ('\n' == sql.charAt(ix)) {
                                ++newLinteCount;
                                if (2 == newLinteCount) {
                                        return ix - 1;
                                }
                        }

                        ++ix;
                }
        }

        private static int lastIndexOfStateSep(String sql, int pos) {
                int ix = pos;

                int newLinteCount = 0;
                for (;;) {

                        if (ix == sql.length()) {
                                if (ix == 0) {
                                        return ix;
                                } else {
                                        ix--;
                                }
                        }

                        if (false == Character.isWhitespace(sql.charAt(ix))) {
                                newLinteCount = 0;
                        }

                        if ('\n' == sql.charAt(ix)) {
                                ++newLinteCount;
                                if (2 == newLinteCount) {
                                        return ix + newLinteCount;
                                }
                        }

                        if (0 == ix) {
                                return 0 + newLinteCount;
                        }

                        --ix;
                }
        }

        public void insertString(String string) throws BadLocationException {
                scriptPanel.getDocument().insertString(
                        scriptPanel.getDocument().getLength(), string, null);
        }

        public void freeResources() {
                //TODO untrack and uninstall language support bundles
                if(messageCleanTimer!=null) {
                        messageCleanTimer.stop();
                }
        }

        /**
         * Open one instance of the find replace dialog
         */
        public void openFindReplaceDialog() {
                if (findReplaceDialog == null) {
                        findReplaceDialog = new FindReplaceDialog(scriptPanel,(JFrame)getTopLevelAncestor());
                }
                findReplaceDialog.setAlwaysOnTop(true);
                findReplaceDialog.setVisible(true);
        }
        
         /**
         * Change the status of the button when the console is empty or not.
         */
        public void onUserSelectionChange(){
                String text = scriptPanel.getText().trim();
                if (text.isEmpty()) {
                        executeAction.setEnabled(false);
                        clearAction.setEnabled(false);
                        saveAction.setEnabled(false);
                        findAction.setEnabled(false);
                        quoteAction.setEnabled(false);
                        unQuoteAction.setEnabled(false);
                        commentAction.setEnabled(false);
                        blockCommentAction.setEnabled(false);
                        formatSQLAction.setEnabled(false);
                }
                else{
                        executeAction.setEnabled(true);
                        clearAction.setEnabled(true);
                        saveAction.setEnabled(true);
                        findAction.setEnabled(true);
                        quoteAction.setEnabled(true);
                        unQuoteAction.setEnabled(true);
                        commentAction.setEnabled(true);
                        blockCommentAction.setEnabled(true);
                        formatSQLAction.setEnabled(true);
                }
        }
}
