package view.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class SearchPanel extends JPanel {
    private JTextField searchField;
    private JTextField maskField;
    private JButton searchButton;
    private JButton nextButton;
    private JButton prevButton;
    private JLabel statusLabel;

    public SearchPanel() {
        setLayout(new FlowLayout(FlowLayout.LEFT));
        setBorder(BorderFactory.createTitledBorder("Поиск байт"));

        add(new JLabel("Байты:"));
        searchField = new JTextField(15);
        add(searchField);

        add(new JLabel("Маска:"));
        maskField = new JTextField(10);
        add(maskField);

        searchButton = new JButton("Найти");
        add(searchButton);

        nextButton = new JButton("След.");
        add(nextButton);

        prevButton = new JButton("Пред.");
        add(prevButton);

        statusLabel = new JLabel(" ");
        add(statusLabel);

        // Добавляем подсказки
        searchField.setToolTipText("Введите байты в hex: 'A1 B2 C3' или 'A1B2C3'");
        maskField.setToolTipText("Маска в hex: 'FF 00 FF' - где FF учитывать, 00 - игнорировать");

    }

    //GETTERS

    public JTextField getSearchField() {
        return searchField;
    }

    public JTextField getMaskField() {
        return maskField;
    }

    // OTHERS

    public void setStatus(String status) {
        statusLabel.setText(status);
    }

    // LISTENERS
    public void addSearchButtonListener(ActionListener listener) {
        searchButton.addActionListener(listener);
    }

    public void addNextListener(ActionListener listener) {
        nextButton.addActionListener(listener);
    }

    public void addPrevListener(ActionListener listener) {
        prevButton.addActionListener(listener);
    }

}
