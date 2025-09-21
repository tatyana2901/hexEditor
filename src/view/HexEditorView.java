package view;

import model.HexTableModel;
import view.components.*;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.File;

public class HexEditorView extends JFrame {
    private TableModel tableModel;
    private FileSelectionPanel fileSelectionPanel;
    private PaginationPanel paginationPanel;
    private LinesAndItemsSettingsPanel settingsPanel;
    private BlockBytesMenuBar blockBytesMenuBar;
    private LabelInfoPanel labelInfoPanel;
    private SearchPanel searchPanel;
    private JTable dataTable;

    //ЗАМЕНИТЬ НА ИНТЕРФЕЙСЫ????
    public HexEditorView(TableModel tableModel, FileSelectionPanel fileSelectionPanel,
                         PaginationPanel paginationPanel, LinesAndItemsSettingsPanel settingsPanel,
                         BlockBytesMenuBar blockBytesMenuBar, LabelInfoPanel labelInfoPanel, SearchPanel searchPanel) {
        super("HexEditor");

        this.tableModel = tableModel;
        this.fileSelectionPanel = fileSelectionPanel;
        this.paginationPanel = paginationPanel;
        this.settingsPanel = settingsPanel;
        this.blockBytesMenuBar = blockBytesMenuBar;
        this.labelInfoPanel = labelInfoPanel;
        this.searchPanel = searchPanel;
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
        panel.add(searchPanel);

        setupTableSelection();

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

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

    public void addByteSelectionListener(ListSelectionListener listener) {
        dataTable.getSelectionModel().addListSelectionListener(listener);
    }

    public void addSearchListener(ActionListener listener) {
        searchPanel.addSearchButtonListener(listener);
    }

    public void addNextSearchResultListener(ActionListener listener) {
        searchPanel.addNextListener(listener);
    }

    public void addPrevSearchResultListener(ActionListener listener) {
        searchPanel.addPrevListener(listener);
    }


    public void setupDataTypeListeners(ActionListener byteListener,
                                       ActionListener shortListener,
                                       ActionListener intListener,
                                       ActionListener longListener,
                                       ActionListener floatListener,
                                       ActionListener doubleListener) {
        blockBytesMenuBar.getByteItem().addActionListener(byteListener);
        blockBytesMenuBar.getShortItem().addActionListener(shortListener);
        blockBytesMenuBar.getIntItem().addActionListener(intListener);
        blockBytesMenuBar.getLongItem().addActionListener(longListener);
        blockBytesMenuBar.getFloatItem().addActionListener(floatListener);
        blockBytesMenuBar.getDoubleItem().addActionListener(doubleListener);
    }

    //OTHER

    // Метод для отображения инфо о количестве страниц и текущей страницы
    public void setPageInfo(int currentPage, int totalPages) {
        paginationPanel.getPageInfoLabel().setText(" / " + totalPages);
        paginationPanel.getPageNumberField().setText(String.valueOf(currentPage));
    }

    public File showOpenFileDialog() {
        JFileChooser fileChooser = fileSelectionPanel.getFileChooser();
        int ret = fileChooser.showDialog(this, "Открыть файл");
        return (ret == JFileChooser.APPROVE_OPTION) ? fileChooser.getSelectedFile() : null;
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


    public void selectTableCell(int row, int column) {
        // Очищаем предыдущее выделение
        dataTable.clearSelection();

        // Устанавливаем выделение конкретной ячейки
        dataTable.setRowSelectionInterval(row, row);
        dataTable.setColumnSelectionInterval(column, column);

        // Убеждаемся, что ячейка видима
        scrollToVisible(row, column);
    }


    public void scrollToVisible(int row, int column) {
        dataTable.scrollRectToVisible(dataTable.getCellRect(row, column, true));
    }


    public void setSearchStatus(String statusText) {
        searchPanel.setStatus(statusText);
    }

    private void setupTableSelection() {
        dataTable.setCellSelectionEnabled(true);
        dataTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    }

    public void setByteLabelText(byte value) {
        labelInfoPanel.setByteValue(value);
    }


    public void clearByteLabelText() {
        labelInfoPanel.clear();
    }


    public void setFileInfo(String info) {
        fileSelectionPanel.getLabel().setText(info);
    }

    public int getPageNumberInput() {
        return Integer.parseInt(paginationPanel.getPageNumberField().getText());
    }

    public int getLinesPerPageInput() {
        return (int) settingsPanel.getLinesPerPage().getValue();
    }

    public int getItemsPerLineInput() {
        return (int) settingsPanel.getItemsPerLine().getValue();
    }


    public String getSearchPattern() {
        return searchPanel.getSearchField().getText();
    }

    public String getSearchMask() {
        return searchPanel.getMaskField().getText();
    }

    // МЕТОДЫ БРАБОТКИ ОШИБОК

    public void showErrorDialog(String message, String title) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.ERROR_MESSAGE);
    }

    public void showInfoDialog(String message, String title) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.INFORMATION_MESSAGE);
    }


    //ПЕРЕРИСОВКА ТАБЛИЦЫ

    public void updateTableData() {
        ((HexTableModel) tableModel).fireTableDataChanged();
    }

    public void updateTableStructure() {
        ((HexTableModel) tableModel).fireTableStructureChanged();
    }


}

