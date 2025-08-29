/*
 * Created by JFormDesigner on Thu Aug 28 16:01:57 CST 2025
 */

package io.sn.etoile.launch.ui;

import io.sn.etoile.launch.UIHandler;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ResourceBundle;

/**
 * @author Administrator
 */
public class MainFrame extends JFrame {
    public MainFrame() {
        initComponents();
    }

    private void mGithub(ActionEvent e) {
        UIHandler.INSTANCE.openBrowser(this, "https://github.com/freeze-dolphin/EtoileResurrection");
    }

    private void btnSlst(ActionEvent e) {
        UIHandler.Pack.INSTANCE.buttonSonglistSelection(this, e);
    }

    private void thisWindowClosed(WindowEvent e) {
        UIHandler.INSTANCE.thisWindowClosed(e);
    }

    private void toggleSongIdRegex(ActionEvent e) {
        UIHandler.Pack.INSTANCE.toggleSongIdRegex(this, e);
    }

    private void txtSongIdRegex(ActionEvent e) {
        UIHandler.Pack.INSTANCE.txtSongIdRegex(this, e);
    }

    private void comboSongId(ActionEvent e) {
        UIHandler.Pack.INSTANCE.comboSongId(this, e);
    }

    private void btnPack(ActionEvent e) {
        UIHandler.Pack.INSTANCE.btnPack(this, e, false);
    }

    private void btnPackAndReset(ActionEvent e) {
        UIHandler.Pack.INSTANCE.btnPack(this, e, true);
    }

    private void btnOutputDir(ActionEvent e) {
        UIHandler.Pack.INSTANCE.btnOutputDir(this, e);
    }

    private void mLaf(ActionEvent e) {
        UIHandler.INSTANCE.openBrowser(this, "https://www.formdev.com/flatlaf/");
    }

    private void btnResetPack(ActionEvent e) {
        UIHandler.INSTANCE.resetPackPage(this);
    }

    private void btnArcpkg(ActionEvent e) {
        UIHandler.Export.INSTANCE.btnArcpkg(this, e);
    }

    private void comboArcpkg(ActionEvent e) {
        UIHandler.Export.INSTANCE.comboArcpkg(this, e);
    }

    private void btnOutputDirExport(ActionEvent e) {
        UIHandler.Export.INSTANCE.btnOutputDirExport(this, e);
    }

    private void btnExport(ActionEvent e) {
        UIHandler.Export.INSTANCE.btnExport(this, e, false);
    }

    private void btnExportAndReset(ActionEvent e) {
        UIHandler.Export.INSTANCE.btnExport(this, e, true);
    }

    private void btnResetExport(ActionEvent e) {
        UIHandler.INSTANCE.resetExportPage(this);
    }

    private void txtVersionExport(ActionEvent e) {
        UIHandler.Export.INSTANCE.txtVersionExport(this, e);
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        ResourceBundle bundle = ResourceBundle.getBundle("lang.MainFrame");
        menuBar = new JMenuBar();
        mAbout = new JMenu();
        mLaf = new JMenuItem();
        separator2 = new JSeparator();
        mGithub = new JMenuItem();
        tabbedPane = new JTabbedPane();
        tabStart = new JPanel();
        usageHint = new JLabel();
        tabPack = new JPanel();
        viewPackHeader = new JPanel();
        _emptyPack = new JPanel();
        viewPackHeaderRect = new JPanel();
        viewPackHeaderContent = new JPanel();
        labelSlst = new JLabel();
        labelSongId = new JLabel();
        _labelPlaceholder1 = new JLabel();
        labelPrefix = new JLabel();
        labelOutputDir = new JLabel();
        labelOutput = new JLabel();
        viewPackMain = new JPanel();
        paramSlst = new JPanel();
        txtSlst = new JTextField();
        btnSlst = new JButton();
        paramSongIdCombo = new JPanel();
        comboSongId = new JComboBox();
        paramSongIdRe = new JPanel();
        txtSongIdRegex = new JTextField();
        toggleSongIdRegex = new JCheckBox();
        txtPrefixPack = new JTextField();
        paramOutputDir = new JPanel();
        txtOutputDir = new JTextField();
        btnOutputDir = new JButton();
        sclPackOutput = new JScrollPane();
        txtPackOutput = new JTextArea();
        progPack = new JProgressBar();
        areaExecutions = new JPanel();
        btnPack = new JButton();
        btnPackAndReset = new JButton();
        btnResetPack = new JButton();
        _emptyEast = new JPanel();
        _emptySouth = new JPanel();
        tabExport = new JPanel();
        viewExportHeader = new JPanel();
        _emptyExport = new JPanel();
        viewExportHeaderRect = new JPanel();
        viewExportHeaderContent = new JPanel();
        labelArcpkgs = new JLabel();
        labelPackToExport = new JLabel();
        labelVersionExport = new JLabel();
        labelPrefix2 = new JLabel();
        labelOutputDir2 = new JLabel();
        labelOutput2 = new JLabel();
        viewExportMain = new JPanel();
        paramArcpkg = new JPanel();
        comboArcpkg = new JComboBox();
        btnArcpkg = new JButton();
        txtPackExport = new JTextField();
        txtVersionExport = new JTextField();
        txtPrefixExport = new JTextField();
        paramOutputDir2 = new JPanel();
        txtOutputDirExport = new JTextField();
        btnOutputDirExport = new JButton();
        sclExportOutput = new JScrollPane();
        txtExportOutput = new JTextArea();
        progExport = new JProgressBar();
        areaExecutions2 = new JPanel();
        btnExport = new JButton();
        btnExportAndReset = new JButton();
        btnResetExport = new JButton();
        _emptyEast2 = new JPanel();
        _emptySouth2 = new JPanel();

        //======== this ========
        setPreferredSize(new Dimension(500, 400));
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(500, 400));
        setTitle("EtoileResurrection");
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                thisWindowClosed(e);
            }
        });
        var contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        //======== menuBar ========
        {

            //======== mAbout ========
            {

                //---- mLaf ----
                mLaf.setText("FlatLaf");
                mLaf.addActionListener(e -> mLaf(e));
                mAbout.add(mLaf);
                mAbout.add(separator2);

                //---- mGithub ----
                mGithub.addActionListener(e -> mGithub(e));
                mAbout.add(mGithub);
            }
            menuBar.add(mAbout);
        }
        setJMenuBar(menuBar);

        //======== tabbedPane ========
        {
            tabbedPane.setTabPlacement(SwingConstants.LEFT);

            //======== tabStart ========
            {
                tabStart.setLayout(new BorderLayout());

                //---- usageHint ----
                usageHint.setHorizontalAlignment(SwingConstants.CENTER);
                usageHint.setFont(usageHint.getFont().deriveFont(usageHint.getFont().getStyle() & ~Font.BOLD));
                usageHint.setForeground(Color.gray);
                tabStart.add(usageHint, BorderLayout.CENTER);
            }
            tabbedPane.addTab(bundle.getString("MainFrame.tabStart.tab.title"), tabStart);

            //======== tabPack ========
            {
                tabPack.setLayout(new BorderLayout(4, 0));

                //======== viewPackHeader ========
                {
                    viewPackHeader.setLayout(new BoxLayout(viewPackHeader, BoxLayout.X_AXIS));

                    //======== _emptyPack ========
                    {
                        _emptyPack.setPreferredSize(new Dimension(8, 0));
                        _emptyPack.setLayout(new BorderLayout());
                    }
                    viewPackHeader.add(_emptyPack);

                    //======== viewPackHeaderRect ========
                    {
                        viewPackHeaderRect.setLayout(new BorderLayout());

                        //======== viewPackHeaderContent ========
                        {
                            viewPackHeaderContent.setLayout(new BoxLayout(viewPackHeaderContent, BoxLayout.Y_AXIS));

                            //---- labelSlst ----
                            labelSlst.setHorizontalAlignment(SwingConstants.LEFT);
                            labelSlst.setMinimumSize(new Dimension(54, 27));
                            labelSlst.setMaximumSize(new Dimension(54, 27));
                            labelSlst.setPreferredSize(new Dimension(54, 27));
                            viewPackHeaderContent.add(labelSlst);

                            //---- labelSongId ----
                            labelSongId.setHorizontalAlignment(SwingConstants.LEFT);
                            labelSongId.setMaximumSize(new Dimension(54, 27));
                            labelSongId.setMinimumSize(new Dimension(54, 27));
                            labelSongId.setPreferredSize(new Dimension(54, 27));
                            viewPackHeaderContent.add(labelSongId);

                            //---- _labelPlaceholder1 ----
                            _labelPlaceholder1.setHorizontalAlignment(SwingConstants.LEFT);
                            _labelPlaceholder1.setMaximumSize(new Dimension(54, 27));
                            _labelPlaceholder1.setMinimumSize(new Dimension(54, 27));
                            _labelPlaceholder1.setPreferredSize(new Dimension(54, 27));
                            viewPackHeaderContent.add(_labelPlaceholder1);

                            //---- labelPrefix ----
                            labelPrefix.setHorizontalAlignment(SwingConstants.LEFT);
                            labelPrefix.setMaximumSize(new Dimension(54, 27));
                            labelPrefix.setMinimumSize(new Dimension(54, 27));
                            labelPrefix.setPreferredSize(new Dimension(54, 27));
                            viewPackHeaderContent.add(labelPrefix);

                            //---- labelOutputDir ----
                            labelOutputDir.setHorizontalAlignment(SwingConstants.LEFT);
                            labelOutputDir.setMaximumSize(new Dimension(54, 27));
                            labelOutputDir.setMinimumSize(new Dimension(54, 27));
                            labelOutputDir.setPreferredSize(new Dimension(54, 27));
                            viewPackHeaderContent.add(labelOutputDir);

                            //---- labelOutput ----
                            labelOutput.setHorizontalAlignment(SwingConstants.LEFT);
                            labelOutput.setMaximumSize(new Dimension(54, 27));
                            labelOutput.setMinimumSize(new Dimension(54, 27));
                            labelOutput.setPreferredSize(new Dimension(54, 27));
                            viewPackHeaderContent.add(labelOutput);
                        }
                        viewPackHeaderRect.add(viewPackHeaderContent, BorderLayout.NORTH);
                    }
                    viewPackHeader.add(viewPackHeaderRect);
                }
                tabPack.add(viewPackHeader, BorderLayout.WEST);

                //======== viewPackMain ========
                {
                    viewPackMain.setLayout(new BoxLayout(viewPackMain, BoxLayout.Y_AXIS));

                    //======== paramSlst ========
                    {
                        paramSlst.setLayout(new BoxLayout(paramSlst, BoxLayout.X_AXIS));

                        //---- txtSlst ----
                        txtSlst.setMaximumSize(new Dimension(2147483647, 27));
                        txtSlst.setEditable(false);
                        paramSlst.add(txtSlst);

                        //---- btnSlst ----
                        btnSlst.setMargin(new Insets(2, 4, 2, 4));
                        btnSlst.addActionListener(e -> btnSlst(e));
                        paramSlst.add(btnSlst);
                    }
                    viewPackMain.add(paramSlst);

                    //======== paramSongIdCombo ========
                    {
                        paramSongIdCombo.setLayout(new BoxLayout(paramSongIdCombo, BoxLayout.X_AXIS));

                        //---- comboSongId ----
                        comboSongId.setMaximumSize(new Dimension(32767, 27));
                        comboSongId.setEnabled(false);
                        comboSongId.addActionListener(e -> comboSongId(e));
                        paramSongIdCombo.add(comboSongId);
                    }
                    viewPackMain.add(paramSongIdCombo);

                    //======== paramSongIdRe ========
                    {
                        paramSongIdRe.setLayout(new BoxLayout(paramSongIdRe, BoxLayout.X_AXIS));

                        //---- txtSongIdRegex ----
                        txtSongIdRegex.setMaximumSize(new Dimension(2147483647, 27));
                        txtSongIdRegex.setEnabled(false);
                        txtSongIdRegex.addActionListener(e -> txtSongIdRegex(e));
                        paramSongIdRe.add(txtSongIdRegex);

                        //---- toggleSongIdRegex ----
                        toggleSongIdRegex.setText("re");
                        toggleSongIdRegex.setEnabled(false);
                        toggleSongIdRegex.addActionListener(e -> toggleSongIdRegex(e));
                        paramSongIdRe.add(toggleSongIdRegex);
                    }
                    viewPackMain.add(paramSongIdRe);

                    //---- txtPrefixPack ----
                    txtPrefixPack.setMaximumSize(new Dimension(2147483647, 27));
                    txtPrefixPack.setEnabled(false);
                    txtPrefixPack.addActionListener(e -> txtSongIdRegex(e));
                    viewPackMain.add(txtPrefixPack);

                    //======== paramOutputDir ========
                    {
                        paramOutputDir.setLayout(new BoxLayout(paramOutputDir, BoxLayout.X_AXIS));

                        //---- txtOutputDir ----
                        txtOutputDir.setMaximumSize(new Dimension(2147483647, 27));
                        txtOutputDir.setEnabled(false);
                        txtOutputDir.setEditable(false);
                        paramOutputDir.add(txtOutputDir);

                        //---- btnOutputDir ----
                        btnOutputDir.setMargin(new Insets(2, 4, 2, 4));
                        btnOutputDir.setEnabled(false);
                        btnOutputDir.addActionListener(e -> btnOutputDir(e));
                        paramOutputDir.add(btnOutputDir);
                    }
                    viewPackMain.add(paramOutputDir);

                    //======== sclPackOutput ========
                    {

                        //---- txtPackOutput ----
                        txtPackOutput.setEditable(false);
                        txtPackOutput.setEnabled(false);
                        sclPackOutput.setViewportView(txtPackOutput);
                    }
                    viewPackMain.add(sclPackOutput);
                    viewPackMain.add(progPack);

                    //======== areaExecutions ========
                    {
                        areaExecutions.setLayout(new BoxLayout(areaExecutions, BoxLayout.X_AXIS));

                        //---- btnPack ----
                        btnPack.setEnabled(false);
                        btnPack.addActionListener(e -> btnPack(e));
                        areaExecutions.add(btnPack);

                        //---- btnPackAndReset ----
                        btnPackAndReset.setEnabled(false);
                        btnPackAndReset.addActionListener(e -> btnPackAndReset(e));
                        areaExecutions.add(btnPackAndReset);

                        //---- btnResetPack ----
                        btnResetPack.setEnabled(false);
                        btnResetPack.addActionListener(e -> btnResetPack(e));
                        areaExecutions.add(btnResetPack);
                    }
                    viewPackMain.add(areaExecutions);
                }
                tabPack.add(viewPackMain, BorderLayout.CENTER);

                //======== _emptyEast ========
                {
                    _emptyEast.setPreferredSize(new Dimension(8, 0));
                    _emptyEast.setLayout(new BorderLayout());
                }
                tabPack.add(_emptyEast, BorderLayout.EAST);

                //======== _emptySouth ========
                {
                    _emptySouth.setPreferredSize(new Dimension(0, 8));
                    _emptySouth.setLayout(new BorderLayout());
                }
                tabPack.add(_emptySouth, BorderLayout.SOUTH);
            }
            tabbedPane.addTab(bundle.getString("MainFrame.tabPack.tab.title_2"), tabPack);

            //======== tabExport ========
            {
                tabExport.setLayout(new BorderLayout());

                //======== viewExportHeader ========
                {
                    viewExportHeader.setLayout(new BoxLayout(viewExportHeader, BoxLayout.X_AXIS));

                    //======== _emptyExport ========
                    {
                        _emptyExport.setPreferredSize(new Dimension(8, 0));
                        _emptyExport.setLayout(new BorderLayout());
                    }
                    viewExportHeader.add(_emptyExport);

                    //======== viewExportHeaderRect ========
                    {
                        viewExportHeaderRect.setLayout(new BorderLayout());

                        //======== viewExportHeaderContent ========
                        {
                            viewExportHeaderContent.setLayout(new BoxLayout(viewExportHeaderContent, BoxLayout.Y_AXIS));

                            //---- labelArcpkgs ----
                            labelArcpkgs.setHorizontalAlignment(SwingConstants.LEFT);
                            labelArcpkgs.setMinimumSize(new Dimension(54, 27));
                            labelArcpkgs.setMaximumSize(new Dimension(54, 27));
                            labelArcpkgs.setPreferredSize(new Dimension(54, 27));
                            viewExportHeaderContent.add(labelArcpkgs);

                            //---- labelPackToExport ----
                            labelPackToExport.setText("Pack:");
                            labelPackToExport.setHorizontalAlignment(SwingConstants.LEFT);
                            labelPackToExport.setMaximumSize(new Dimension(54, 27));
                            labelPackToExport.setMinimumSize(new Dimension(54, 27));
                            labelPackToExport.setPreferredSize(new Dimension(54, 27));
                            viewExportHeaderContent.add(labelPackToExport);

                            //---- labelVersionExport ----
                            labelVersionExport.setText("Version:");
                            labelVersionExport.setHorizontalAlignment(SwingConstants.LEFT);
                            labelVersionExport.setMaximumSize(new Dimension(54, 27));
                            labelVersionExport.setMinimumSize(new Dimension(54, 27));
                            labelVersionExport.setPreferredSize(new Dimension(54, 27));
                            viewExportHeaderContent.add(labelVersionExport);

                            //---- labelPrefix2 ----
                            labelPrefix2.setHorizontalAlignment(SwingConstants.LEFT);
                            labelPrefix2.setMaximumSize(new Dimension(54, 27));
                            labelPrefix2.setMinimumSize(new Dimension(54, 27));
                            labelPrefix2.setPreferredSize(new Dimension(54, 27));
                            viewExportHeaderContent.add(labelPrefix2);

                            //---- labelOutputDir2 ----
                            labelOutputDir2.setHorizontalAlignment(SwingConstants.LEFT);
                            labelOutputDir2.setMaximumSize(new Dimension(54, 27));
                            labelOutputDir2.setMinimumSize(new Dimension(54, 27));
                            labelOutputDir2.setPreferredSize(new Dimension(54, 27));
                            viewExportHeaderContent.add(labelOutputDir2);

                            //---- labelOutput2 ----
                            labelOutput2.setHorizontalAlignment(SwingConstants.LEFT);
                            labelOutput2.setMaximumSize(new Dimension(54, 27));
                            labelOutput2.setMinimumSize(new Dimension(54, 27));
                            labelOutput2.setPreferredSize(new Dimension(54, 27));
                            viewExportHeaderContent.add(labelOutput2);
                        }
                        viewExportHeaderRect.add(viewExportHeaderContent, BorderLayout.NORTH);
                    }
                    viewExportHeader.add(viewExportHeaderRect);
                }
                tabExport.add(viewExportHeader, BorderLayout.WEST);

                //======== viewExportMain ========
                {
                    viewExportMain.setLayout(new BoxLayout(viewExportMain, BoxLayout.Y_AXIS));

                    //======== paramArcpkg ========
                    {
                        paramArcpkg.setLayout(new BoxLayout(paramArcpkg, BoxLayout.X_AXIS));

                        //---- comboArcpkg ----
                        comboArcpkg.setMaximumSize(new Dimension(32767, 27));
                        comboArcpkg.addActionListener(e -> comboArcpkg(e));
                        paramArcpkg.add(comboArcpkg);

                        //---- btnArcpkg ----
                        btnArcpkg.setMargin(new Insets(2, 4, 2, 4));
                        btnArcpkg.addActionListener(e -> btnArcpkg(e));
                        paramArcpkg.add(btnArcpkg);
                    }
                    viewExportMain.add(paramArcpkg);

                    //---- txtPackExport ----
                    txtPackExport.setMaximumSize(new Dimension(2147483647, 27));
                    txtPackExport.setEnabled(false);
                    viewExportMain.add(txtPackExport);

                    //---- txtVersionExport ----
                    txtVersionExport.setMaximumSize(new Dimension(2147483647, 27));
                    txtVersionExport.setEnabled(false);
                    txtVersionExport.setToolTipText("Determine the version when this song is added. (Default: 1.0)");
                    txtVersionExport.addActionListener(e -> txtVersionExport(e));
                    viewExportMain.add(txtVersionExport);

                    //---- txtPrefixExport ----
                    txtPrefixExport.setMaximumSize(new Dimension(2147483647, 27));
                    txtPrefixExport.setEnabled(false);
                    viewExportMain.add(txtPrefixExport);

                    //======== paramOutputDir2 ========
                    {
                        paramOutputDir2.setLayout(new BoxLayout(paramOutputDir2, BoxLayout.X_AXIS));

                        //---- txtOutputDirExport ----
                        txtOutputDirExport.setMaximumSize(new Dimension(2147483647, 27));
                        txtOutputDirExport.setEnabled(false);
                        paramOutputDir2.add(txtOutputDirExport);

                        //---- btnOutputDirExport ----
                        btnOutputDirExport.setMargin(new Insets(2, 4, 2, 4));
                        btnOutputDirExport.setEnabled(false);
                        btnOutputDirExport.addActionListener(e -> btnOutputDirExport(e));
                        paramOutputDir2.add(btnOutputDirExport);
                    }
                    viewExportMain.add(paramOutputDir2);

                    //======== sclExportOutput ========
                    {

                        //---- txtExportOutput ----
                        txtExportOutput.setEditable(false);
                        txtExportOutput.setEnabled(false);
                        sclExportOutput.setViewportView(txtExportOutput);
                    }
                    viewExportMain.add(sclExportOutput);
                    viewExportMain.add(progExport);

                    //======== areaExecutions2 ========
                    {
                        areaExecutions2.setLayout(new BoxLayout(areaExecutions2, BoxLayout.X_AXIS));

                        //---- btnExport ----
                        btnExport.setEnabled(false);
                        btnExport.addActionListener(e -> btnExport(e));
                        areaExecutions2.add(btnExport);

                        //---- btnExportAndReset ----
                        btnExportAndReset.setEnabled(false);
                        btnExportAndReset.addActionListener(e -> btnExportAndReset(e));
                        areaExecutions2.add(btnExportAndReset);

                        //---- btnResetExport ----
                        btnResetExport.setEnabled(false);
                        btnResetExport.addActionListener(e -> btnResetExport(e));
                        areaExecutions2.add(btnResetExport);
                    }
                    viewExportMain.add(areaExecutions2);
                }
                tabExport.add(viewExportMain, BorderLayout.CENTER);

                //======== _emptyEast2 ========
                {
                    _emptyEast2.setPreferredSize(new Dimension(8, 0));
                    _emptyEast2.setLayout(new BorderLayout());
                }
                tabExport.add(_emptyEast2, BorderLayout.EAST);

                //======== _emptySouth2 ========
                {
                    _emptySouth2.setPreferredSize(new Dimension(0, 8));
                    _emptySouth2.setLayout(new BorderLayout());
                }
                tabExport.add(_emptySouth2, BorderLayout.SOUTH);
            }
            tabbedPane.addTab(bundle.getString("MainFrame.tabExport.tab.title"), tabExport);
        }
        contentPane.add(tabbedPane, BorderLayout.CENTER);

        initComponentsI18n();

        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
    }

    public void initComponentsI18n() {
        // JFormDesigner - Component i18n initialization - DO NOT MODIFY  //GEN-BEGIN:initI18n  @formatter:off
        ResourceBundle bundle = ResourceBundle.getBundle("lang.MainFrame");
        mAbout.setText(bundle.getString("MainFrame.mAbout.text"));
        mGithub.setText(bundle.getString("MainFrame.mGithub.text"));
        usageHint.setText(bundle.getString("MainFrame.usageHint.text"));
        tabbedPane.setTitleAt(0, bundle.getString("MainFrame.tabStart.tab.title"));
        labelSlst.setText(bundle.getString("MainFrame.labelSlst.text"));
        labelSongId.setText(bundle.getString("MainFrame.labelSongId.text"));
        labelPrefix.setText(bundle.getString("MainFrame.labelPrefix.text"));
        labelOutputDir.setText(bundle.getString("MainFrame.labelOutputDir.text"));
        labelOutput.setText(bundle.getString("MainFrame.labelOutput.text"));
        btnSlst.setText(bundle.getString("MainFrame.btnSlst.text"));
        toggleSongIdRegex.setToolTipText(bundle.getString("MainFrame.toggleSongIdRegex.toolTipText"));
        txtPrefixPack.setToolTipText(bundle.getString("MainFrame.txtPrefixPack.toolTipText"));
        btnOutputDir.setText(bundle.getString("MainFrame.btnOutputDir.text"));
        btnPack.setText(bundle.getString("MainFrame.btnPack.text"));
        btnPackAndReset.setText(bundle.getString("MainFrame.btnPackAndReset.text"));
        btnResetPack.setText(bundle.getString("MainFrame.btnResetPack.text"));
        tabbedPane.setTitleAt(1, bundle.getString("MainFrame.tabPack.tab.title_2"));
        labelArcpkgs.setText(bundle.getString("MainFrame.labelArcpkgs.text"));
        labelPrefix2.setText(bundle.getString("MainFrame.labelPrefix2.text"));
        labelOutputDir2.setText(bundle.getString("MainFrame.labelOutputDir2.text"));
        labelOutput2.setText(bundle.getString("MainFrame.labelOutput2.text"));
        btnArcpkg.setText(bundle.getString("MainFrame.btnArcpkg.text"));
        txtPackExport.setToolTipText(bundle.getString("MainFrame.txtPackExport.toolTipText"));
        txtPrefixExport.setToolTipText(bundle.getString("MainFrame.txtPrefixExport.toolTipText"));
        btnOutputDirExport.setText(bundle.getString("MainFrame.btnOutputDirExport.text"));
        btnExport.setText(bundle.getString("MainFrame.btnExport.text"));
        btnExportAndReset.setText(bundle.getString("MainFrame.btnExportAndReset.text"));
        btnResetExport.setText(bundle.getString("MainFrame.btnResetExport.text"));
        tabbedPane.setTitleAt(2, bundle.getString("MainFrame.tabExport.tab.title"));
        // JFormDesigner - End of component i18n initialization  //GEN-END:initI18n  @formatter:on
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    public JMenuBar menuBar;
    public JMenu mAbout;
    public JMenuItem mLaf;
    public JSeparator separator2;
    public JMenuItem mGithub;
    public JTabbedPane tabbedPane;
    public JPanel tabStart;
    public JLabel usageHint;
    public JPanel tabPack;
    public JPanel viewPackHeader;
    public JPanel _emptyPack;
    public JPanel viewPackHeaderRect;
    public JPanel viewPackHeaderContent;
    public JLabel labelSlst;
    public JLabel labelSongId;
    public JLabel _labelPlaceholder1;
    public JLabel labelPrefix;
    public JLabel labelOutputDir;
    public JLabel labelOutput;
    public JPanel viewPackMain;
    public JPanel paramSlst;
    public JTextField txtSlst;
    public JButton btnSlst;
    public JPanel paramSongIdCombo;
    public JComboBox comboSongId;
    public JPanel paramSongIdRe;
    public JTextField txtSongIdRegex;
    public JCheckBox toggleSongIdRegex;
    public JTextField txtPrefixPack;
    public JPanel paramOutputDir;
    public JTextField txtOutputDir;
    public JButton btnOutputDir;
    public JScrollPane sclPackOutput;
    public JTextArea txtPackOutput;
    public JProgressBar progPack;
    public JPanel areaExecutions;
    public JButton btnPack;
    public JButton btnPackAndReset;
    public JButton btnResetPack;
    public JPanel _emptyEast;
    public JPanel _emptySouth;
    public JPanel tabExport;
    public JPanel viewExportHeader;
    public JPanel _emptyExport;
    public JPanel viewExportHeaderRect;
    public JPanel viewExportHeaderContent;
    public JLabel labelArcpkgs;
    public JLabel labelPackToExport;
    public JLabel labelVersionExport;
    public JLabel labelPrefix2;
    public JLabel labelOutputDir2;
    public JLabel labelOutput2;
    public JPanel viewExportMain;
    public JPanel paramArcpkg;
    public JComboBox comboArcpkg;
    public JButton btnArcpkg;
    public JTextField txtPackExport;
    public JTextField txtVersionExport;
    public JTextField txtPrefixExport;
    public JPanel paramOutputDir2;
    public JTextField txtOutputDirExport;
    public JButton btnOutputDirExport;
    public JScrollPane sclExportOutput;
    public JTextArea txtExportOutput;
    public JProgressBar progExport;
    public JPanel areaExecutions2;
    public JButton btnExport;
    public JButton btnExportAndReset;
    public JButton btnResetExport;
    public JPanel _emptyEast2;
    public JPanel _emptySouth2;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
