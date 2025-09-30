package view.components;

import javax.swing.*;
import java.awt.event.ActionListener;

public class TableContextMenu {
    private JPopupMenu contextMenu;
    private JMenuItem deleteWithShiftItem;
    private JMenuItem deleteWithZeroItem;
    private JMenuItem insertOverwriteItem;
    private JMenuItem insertShiftItem;
    private JMenuItem changeByteValue;

    public TableContextMenu() {
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

    public void setMenuEnabled(boolean enabled) {
        deleteWithShiftItem.setEnabled(enabled);
        deleteWithZeroItem.setEnabled(enabled);
        insertOverwriteItem.setEnabled(enabled);
        insertShiftItem.setEnabled(enabled);
        changeByteValue.setEnabled(enabled);
    }
}

