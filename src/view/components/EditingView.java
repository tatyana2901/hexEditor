package view.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class EditingView {
    private JPanel enableEditingPanel;
    private JButton enableEditButton;
    private JLabel editStatusLabel;
    private boolean editMode = false;

    private JPopupMenu contextMenu;
    private JMenuItem deleteWithShiftItem;
    private JMenuItem deleteWithZeroItem;
    private JMenuItem insertOverwriteItem;
    private JMenuItem insertShiftItem;
    private JMenuItem changeByteValue;


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
        contextMenu.addSeparator(); // Добавляем разделитель для лучшей организации
        contextMenu.add(insertOverwriteItem);
        contextMenu.add(insertShiftItem);
        contextMenu.add(changeByteValue);
    }

    // GETTERS


    public JPanel getEnableEditingPanel() {
        return enableEditingPanel;
    }

    public JButton getEnableEditButton() {
        return enableEditButton;
    }

    public boolean isEditMode() {
        return editMode;
    }

    // METHODS TO UPDATE UI
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

    public void setEditModeEnabled(boolean enabled) {
        enableEditButton.setEnabled(enabled);
        if (!enabled) {
            setEditMode(false);
        }
    }


    // LISTENERS
    public void addEnableEditListener(ActionListener listener) {
        enableEditButton.addActionListener(listener);
    }

    public JPopupMenu getContextMenu() {
        return contextMenu;
    }

    // LISTENERS
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


