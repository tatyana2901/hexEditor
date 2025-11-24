package view.components;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;

public class EditingView {
    private final JPanel enableEditingPanel;
    private final JButton enableEditButton;
    private final JLabel editStatusLabel;
    private boolean editMode = false;

    private final JPopupMenu contextMenu;
    private final JMenuItem deleteWithShiftItem;
    private final JMenuItem deleteWithZeroItem;
    private final JMenuItem insertOverwriteItem;
    private final JMenuItem insertShiftItem;
    private final JMenuItem changeByteValue;


    public EditingView() {
        enableEditingPanel = new JPanel();
        enableEditingPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        enableEditingPanel.setBorder(BorderFactory.createTitledBorder("Редактирование"));

        enableEditButton = new JButton("Включить редактирование");
        editStatusLabel = new JLabel("Режим просмотра");

        enableEditingPanel.add(enableEditButton);
        enableEditingPanel.add(editStatusLabel);

        enableEditButton.setToolTipText("Разрешить редактирование файла (только в режиме BYTE)");


        contextMenu = new JPopupMenu();

        deleteWithShiftItem = new JMenuItem("Удалить со сдвигом");
        deleteWithZeroItem = new JMenuItem("Удалить (обнулить)");
        insertOverwriteItem = new JMenuItem("Вставить (с заменой)");
        insertShiftItem = new JMenuItem("Вставить (со сдвигом)");
        changeByteValue = new JMenuItem("Изменить значение байта");

        contextMenu.add(deleteWithShiftItem);
        contextMenu.add(deleteWithZeroItem);
        contextMenu.addSeparator();
        contextMenu.add(insertOverwriteItem);
        contextMenu.add(insertShiftItem);
        contextMenu.add(changeByteValue);
    }


    public JPanel getEnableEditingPanel() {
        return enableEditingPanel;
    }

    public boolean isEditMode() {
        return editMode;
    }

    public void setEditMode(boolean enabled) {
        this.editMode = enabled;

        if (enabled) {
            editStatusLabel.setText("Режим редактирования");
            enableEditButton.setText("Отключить редактирование");
        } else {
            editStatusLabel.setText("Режим просмотра");
            enableEditButton.setText("Включить редактирование");
        }
    }


    public void addEnableEditListener(ActionListener listener) {
        enableEditButton.addActionListener(listener);
    }

    public JPopupMenu getContextMenu() {
        return contextMenu;
    }


    public void addDeleteWithShiftListener(ActionListener listener) {
        deleteWithShiftItem.addActionListener(listener);
    }

    public void addDeleteWithZeroListener(ActionListener listener) {
        deleteWithZeroItem.addActionListener(listener);
    }

    public void addInsertOverwriteListener(ActionListener listener) {
        insertOverwriteItem.addActionListener(listener);
    }

    public void addInsertShiftListener(ActionListener listener) {
        insertShiftItem.addActionListener(listener);
    }

    public void addChangeByteValueListener(ActionListener listener) {
        changeByteValue.addActionListener(listener);
    }

    public void setTableContextMenuEnabled(boolean enabled) {
        deleteWithShiftItem.setEnabled(enabled);
        deleteWithZeroItem.setEnabled(enabled);
        insertOverwriteItem.setEnabled(enabled);
        insertShiftItem.setEnabled(enabled);
        changeByteValue.setEnabled(enabled);
    }
}


