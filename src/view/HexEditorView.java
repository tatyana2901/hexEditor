package view;

import model.HexTableModel;
import view.components.*;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ActionListener;

public class HexEditorView extends JFrame {
    private TableModel tableModel;
    private FileSelectionPanel fileSelectionPanel;
    private PaginationPanel paginationPanel;
    private LinesAndItemsSettingsPanel settingsPanel;
    private BlockBytesMenuBar blockBytesMenuBar;
    private LabelInfoPanel labelInfoPanel;
    private JTable dataTable;

    //ЗАМЕНИТЬ НА ИНТЕРФЕЙСЫ????
    public HexEditorView(TableModel tableModel, FileSelectionPanel fileSelectionPanel,
                         PaginationPanel paginationPanel, LinesAndItemsSettingsPanel settingsPanel,
                         BlockBytesMenuBar blockBytesMenuBar, LabelInfoPanel labelInfoPanel) {
        super("HexEditor");

        this.tableModel = tableModel;
        this.fileSelectionPanel = fileSelectionPanel;
        this.paginationPanel = paginationPanel;
        this.settingsPanel = settingsPanel;
        this.blockBytesMenuBar = blockBytesMenuBar;
        this.labelInfoPanel = labelInfoPanel;
        this.dataTable = new JTable(tableModel);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(Box.createVerticalGlue());
        panel.add(Box.createRigidArea(new Dimension(10, 10)));
        getContentPane().add(panel);
        setPreferredSize(new Dimension(260, 220));


        JScrollPane scrollPane = new JScrollPane(dataTable);

        panel.add(fileSelectionPanel);
        panel.add(scrollPane);
        panel.add(paginationPanel);
        panel.add(settingsPanel);
        setJMenuBar(blockBytesMenuBar);
        panel.add(labelInfoPanel);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }

    //GETTERS

    public JFileChooser getFileChooser() {
        return fileSelectionPanel.getFileChooser();
    }

    public JLabel getLabel() {
        return fileSelectionPanel.getLabel();
    }

    public JTextField getPageNumber() {
        return paginationPanel.getPageNumberField();
    }

    public JSpinner getLinesPerPage() {
        return settingsPanel.getLinesPerPage();
    }

    public JSpinner getItemsPerLine() {
        return settingsPanel.getItemsPerLine();
    }

    public JMenuItem getByteMenuItem() {
        return blockBytesMenuBar.getByteItem();
    }

    public JMenuItem getShortMenuItem() {
        return blockBytesMenuBar.getShortItem();
    }

    public JMenuItem getIntMenuItem() {
        return blockBytesMenuBar.getIntItem();
    }

    public JMenuItem getLongMenuItem() {
        return blockBytesMenuBar.getLongItem();
    }

    public JMenuItem getFloatMenuItem() {
        return blockBytesMenuBar.getFloatItem();
    }

    public JMenuItem getDoubleMenuItem() {
        return blockBytesMenuBar.getDoubleItem();
    }


    //SETTERS

    // Метод для отображения инфо о количестве страниц и текущей страницы
    public void setPageInfo(int currentPage, int totalPages) {
        paginationPanel.getPageInfoLabel().setText(" / " + totalPages);
        paginationPanel.getPageNumberField().setText(String.valueOf(currentPage));
    }

    // Метод для включения/выключения опций "со знаком/без знака"
    public void setIntegerSignOptionsEnabled(boolean enabled) {
        blockBytesMenuBar.setIntegerSignOptionsEnabled(enabled);

    }
    //Получение значения выделенной ячейки
    public int getSelectedRow() {
        return dataTable.getSelectedRow();
    }

    //Получение значения выделенной колонки
    public int getSelectedColumn() {
        return dataTable.getSelectedColumn();
    }

    public LabelInfoPanel getLabelInfoPanel() {
        return labelInfoPanel;
    }



    //LISTENERS

    public void addOpenFileListener(ActionListener listener) {  //метод добавления слушаля к кнопке открытия файла(конкретная реализация определена в контроллере)
        fileSelectionPanel.addOpenFileListener(listener);
    }


    public void addNextPageButtonListener(ActionListener listener) {  //метод добавления слушаля к кнопке загрузки страницы
        paginationPanel.addNextPageButtonListener(listener);
    }

    public void addPrevPageButtonListener(ActionListener listener) {
        paginationPanel.addPrevPageButtonListener(listener);
    }

    public void addLoadPageButtonListener(ActionListener listener) {
        paginationPanel.addLoadPageButtonListener(listener);
    }

    public void addLinesPerPageSpinnerListener(ChangeListener listener) {
        settingsPanel.addLinesPerPageSpinnerListener(listener);
    }

    public void addItemsPerLineSpinnerListener(ChangeListener listener) {
        settingsPanel.addItemsPerLineSpinnerListener(listener);
    }

    public void addSignedItemListener(ActionListener listener) {
        blockBytesMenuBar.addSignedItemListener(listener);
    }

    public void addUnsignedItemListener(ActionListener listener) {
        blockBytesMenuBar.addUnsignedItemListener(listener);
    }

    public void addTableSelectionListener(ListSelectionListener listener) {
        dataTable.getSelectionModel().addListSelectionListener(listener);
    }


    //OTHER

    public void updateTableData() {
        ((HexTableModel) tableModel).fireTableDataChanged();
    }

    public void updateTableStructure() {
        ((HexTableModel) tableModel).fireTableStructureChanged();
    }


}
