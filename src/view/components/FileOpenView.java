package view.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.File;

public class FileOpenView {
    private JPanel fileSelectionPanel;
    private JFileChooser fileChooser;
    private JLabel label;
    private JButton openFileButton;

    public FileOpenView() {
        fileSelectionPanel = new JPanel();
        label = new JLabel("Файл не выбран");
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        fileSelectionPanel.add(label);
        openFileButton = new JButton("Выбрать файл");
        openFileButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        fileSelectionPanel.add(openFileButton);
        fileChooser = new JFileChooser();
    }

    public void setFileInfo(String info) {
        label.setText(info);
    }


    public File showOpenFileDialog() {
        int ret = fileChooser.showDialog(fileSelectionPanel, "Открыть файл");
        return (ret == JFileChooser.APPROVE_OPTION) ? fileChooser.getSelectedFile() : null;
    }

    public JPanel getFileSelectionPanel() {
        return fileSelectionPanel;
    }

    public void addOpenFileListener(ActionListener listener) {  //метод добавления слушаля к кнопке открытия файла(конкретная реализация определена в контроллере)
        openFileButton.addActionListener(listener);
    }
}
