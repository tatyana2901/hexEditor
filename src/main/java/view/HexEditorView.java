package view;

import model.HexTableModel;
import view.components.*;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableModel;
import java.awt.Dimension;

public class HexEditorView extends JFrame {
    private TableModel tableModel;
    private FileOpenView fileOpenView;
    private PaginationView paginationView;
    private LinesAndItemsSettingsView settingsView;
    private BlockBytesView blockBytesView;
    private LabelInfoView labelInfoView;
    private SearchView searchView;
    private EditingView editingView;
    private JTable dataTable;
    private FileSaveView fileSaveView;


    public static final int YES_OPTION = JOptionPane.YES_OPTION;


    public HexEditorView(TableModel tableModel) {
        super("HexEditor");

        this.tableModel = tableModel;
        this.fileOpenView = new FileOpenView();
        this.paginationView = new PaginationView();
        this.settingsView = new LinesAndItemsSettingsView();
        this.blockBytesView = new BlockBytesView();
        this.labelInfoView = new LabelInfoView();
        this.searchView = new SearchView();
        this.editingView = new EditingView();
        this.dataTable = new JTable(tableModel);
        dataTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        this.fileSaveView = new FileSaveView();
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(Box.createVerticalGlue());
        panel.add(Box.createRigidArea(new Dimension(10, 10)));
        getContentPane().add(panel);
        setPreferredSize(new Dimension(260, 220));


        JScrollPane scrollPane = new JScrollPane(dataTable);

        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setPreferredSize(new Dimension(800, 400));

        scrollPane.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        panel.add(fileOpenView.getFileSelectionPanel());
        panel.add(scrollPane);
        panel.add(paginationView.getPaginationPanel());
        panel.add(settingsView.getLinesAndItemsSettingsPanel());
        setJMenuBar(blockBytesView.getMenuBar());
        panel.add(labelInfoView.getLabelPanel());
        panel.add(searchView.getSearchPanel());
        panel.add(editingView.getEnableEditingPanel());
        panel.add(fileSaveView.getFileSavingPanel());
        setupTableContextMenu();
        setupTableSelection();

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }

    public FileSaveView getFileSaveView() {
        return fileSaveView;
    }

    public BlockBytesView getBlockBytesView() {
        return blockBytesView;
    }

    public EditingView getEditingView() {
        return editingView;
    }

    public LabelInfoView getLabelInfoView() {
        return labelInfoView;
    }

    public LinesAndItemsSettingsView getSettingsView() {
        return settingsView;
    }

    public SearchView getSearchView() {
        return searchView;
    }

    public FileOpenView getFileOpenView() {
        return fileOpenView;
    }

    public PaginationView getPaginationView() {
        return paginationView;
    }

    public void addByteSelectionListener(ListSelectionListener listener) {
        dataTable.getSelectionModel().addListSelectionListener(listener);
    }


    public int getSelectedRow() {
        return dataTable.getSelectedRow();
    }


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

        clearSelection();

        dataTable.setRowSelectionInterval(row, row);
        dataTable.setColumnSelectionInterval(column, column);

        scrollToVisible(row, column);
    }


    public void scrollToVisible(int row, int column) {
        dataTable.scrollRectToVisible(dataTable.getCellRect(row, column, true));
    }

    public void setupTableSelection() {
        dataTable.setCellSelectionEnabled(true);
        dataTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    }


    private void setupTableContextMenu() {
        dataTable.setComponentPopupMenu(editingView.getContextMenu());

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
                if (e.isPopupTrigger() && editingView.isEditMode() &&
                        dataTable.getSelectedRow() >= 0 && dataTable.getSelectedColumn() > 0) {
                    editingView.getContextMenu().show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });
    }


    public void showErrorDialog(String message, String title) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.ERROR_MESSAGE);
    }

    public void showInfoDialog(String message, String title) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.INFORMATION_MESSAGE);
    }

    public int showConfirmDialog(String title, String text) {
        return JOptionPane.showConfirmDialog(
                this,
                text,
                title,
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );


    }

    public String showInputDialog(String title, String message) {

        return JOptionPane.showInputDialog(
                this,
                message,
                title,
                JOptionPane.QUESTION_MESSAGE
        );
    }

    public void updateTableData() {
        ((HexTableModel) tableModel).fireTableDataChanged();
    }

    public void updateTableStructure() {
        ((HexTableModel) tableModel).fireTableStructureChanged();
    }


}

