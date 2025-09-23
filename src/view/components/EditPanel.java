package view.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class EditPanel extends JPanel {

    private JButton enableEditButton;
    private JLabel editStatusLabel;
    private boolean editMode = false;

    public EditPanel() {
        setLayout(new FlowLayout(FlowLayout.LEFT));
        setBorder(BorderFactory.createTitledBorder("Редактирование"));

        enableEditButton = new JButton("Включить редактирование");
        editStatusLabel = new JLabel("Режим просмотра");

        add(enableEditButton);
        add(editStatusLabel);

        enableEditButton.setToolTipText("Разрешить редактирование файла (только в режиме BYTE)");
    }

    // GETTERS
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
}


