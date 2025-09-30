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
    private EditPanel editPanel;
    private TableContextMenu tableContextMenu;
    private JTable dataTable;

    //ЗАМЕНИТЬ НА ИНТЕРФЕЙСЫ????
    public HexEditorView(TableModel tableModel, FileSelectionPanel fileSelectionPanel,
                         PaginationPanel paginationPanel, LinesAndItemsSettingsPanel settingsPanel,
                         BlockBytesMenuBar blockBytesMenuBar, LabelInfoPanel labelInfoPanel, SearchPanel searchPanel, EditPanel editPanel, TableContextMenu tableContextMenu) {
        super("HexEditor");

        this.tableModel = tableModel;
        this.fileSelectionPanel = fileSelectionPanel;
        this.paginationPanel = paginationPanel;
        this.settingsPanel = settingsPanel;
        this.blockBytesMenuBar = blockBytesMenuBar;
        this.labelInfoPanel = labelInfoPanel;
        this.searchPanel = searchPanel;
        this.editPanel = editPanel;
        this.tableContextMenu = new TableContextMenu();
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
        panel.add(editPanel);

        setupTableContextMenu();
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

    public void addEnableEditListener(ActionListener listener) {
        editPanel.addEnableEditListener(listener);
    }

    public void addDeleteWithShiftListener(ActionListener listener) {
        tableContextMenu.addDeleteWithShiftListener(listener);
    }

    public void addDeleteWithZeroListener(ActionListener listener) {
        tableContextMenu.addDeleteWithZeroListener(listener);
    }

    public void addInsertOverwriteListener(ActionListener listener) {
        tableContextMenu.addInsertOverwriteListener(listener);
    }

    public void addInsertShiftListener(ActionListener listener) {
        tableContextMenu.addInsertShiftListener(listener);
    }
    public void addChangeByteValueListener(ActionListener listener) {
        tableContextMenu.addChangeByteValueListener(listener);
    }
    public void setTableContextMenuEnabled(boolean enabled) {
        tableContextMenu.setMenuEnabled(enabled);
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

    public int[] getSelectedRows() {
        return dataTable.getSelectedRows();
    }

    public int[] getSelectedColumns() {
        return dataTable.getSelectedColumns();
    }

    public void clearSelection() {
        dataTable.clearSelection();
    }

    public void selectTableCell(int row, int column) {
        // Очищаем предыдущее выделение
        clearSelection();

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


    public boolean isEditMode() {
        return editPanel.isEditMode();
    }

    public void setEditMode(boolean enabled) {
        editPanel.setEditMode(enabled);
    }

    private void setupTableContextMenu() {
        dataTable.setComponentPopupMenu(tableContextMenu.getContextMenu());

        // Слушатель для показа меню только в режиме редактирования
        dataTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                showContextMenuIfAllowed(e);
            }

            @Override
            public void mouseReleased(java.awt.event.MouseEvent e) {
                showContextMenuIfAllowed(e);
            }

            private void showContextMenuIfAllowed(java.awt.event.MouseEvent e) {
                if (e.isPopupTrigger() && isEditMode() &&
                        dataTable.getSelectedRow() >= 0 && dataTable.getSelectedColumn() > 0) {
                    tableContextMenu.getContextMenu().show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });
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


    // Выпадающие окна

    public void showErrorDialog(String message, String title) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.ERROR_MESSAGE);
    }

    public void showInfoDialog(String message, String title) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.INFORMATION_MESSAGE);
    }

    public int showConfirmDeleteWithZeroDialog(int startPosition, int count, int selectedCount) {
        return
                JOptionPane.showConfirmDialog(
                        this,
                        String.format("Обнулить выделенные байты?\n\n" +
                                        "Позиция: %d\n" +
                                        "Количество: %d байт\n" +
                                        "Выделено: %d ячеек\n\n" +
                                        "Это действие нельзя отменить. Будет создана резервная копия файла.",
                                startPosition, count, selectedCount),
                        "Подтверждение обнуления",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                );

    }


    //ПЕРЕРИСОВКА ТАБЛИЦЫ

    public void updateTableData() {
        ((HexTableModel) tableModel).fireTableDataChanged();
    }

    public void updateTableStructure() {
        ((HexTableModel) tableModel).fireTableStructureChanged();
    }


}

