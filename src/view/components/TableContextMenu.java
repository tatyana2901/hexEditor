package view.components;

import javax.swing.*;
import java.awt.event.ActionListener;

public class TableContextMenu {
    private JPopupMenu contextMenu;
    private JMenuItem deleteWithShiftItem;
    private JMenuItem deleteWithZeroItem;

    public TableContextMenu() {
        contextMenu = new JPopupMenu();

        deleteWithShiftItem = new JMenuItem("Удалить со сдвигом");
        deleteWithZeroItem = new JMenuItem("Удалить (обнулить)");

        contextMenu.add(deleteWithShiftItem);
        contextMenu.add(deleteWithZeroItem);
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

    public void setMenuEnabled(boolean enabled) {
        deleteWithShiftItem.setEnabled(enabled);
        deleteWithZeroItem.setEnabled(enabled);
    }
}

