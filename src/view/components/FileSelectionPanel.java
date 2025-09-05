package view.components;

import javax.swing.*;
import java.awt.event.ActionListener;

public class FileSelectionPanel extends JPanel {

    private JButton openFileButton;
    private JFileChooser fileChooser;
    private JLabel label;

    public FileSelectionPanel() {
        label = new JLabel("Файл не выбран");
        label.setAlignmentX(CENTER_ALIGNMENT);
        add(label);
        openFileButton = new JButton("Выбрать файл");
        openFileButton.setAlignmentX(CENTER_ALIGNMENT);
        add(openFileButton);


        fileChooser = new JFileChooser();

    }

    public JFileChooser getFileChooser() {
        return fileChooser;
    }

    public JLabel getLabel() {
        return label;
    }

    public void addOpenFileListener(ActionListener listener) {  //метод добавления слушаля к кнопке открытия файла(конкретная реализация определена в контроллере)
        openFileButton.addActionListener(listener);
    }
}
