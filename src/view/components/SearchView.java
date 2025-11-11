package view.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class SearchView {
    private JPanel searchPanel = new JPanel();
    private JTextField searchField;
    private JTextField maskField;
    private JButton searchButton;
    private JButton nextButton;
    private JButton prevButton;
    private JLabel statusLabel;

    public SearchView() {
        searchPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBorder(BorderFactory.createTitledBorder("Поиск байт"));

        searchPanel.add(new JLabel("Байты:"));
        searchField = new JTextField(15);
        searchPanel.add(searchField);

        searchPanel.add(new JLabel("Маска:"));
        maskField = new JTextField(10);
        searchPanel.add(maskField);

        searchButton = new JButton("Найти");
        searchPanel.add(searchButton);

        nextButton = new JButton("След.");
        searchPanel.add(nextButton);

        prevButton = new JButton("Пред.");
        searchPanel.add(prevButton);

        statusLabel = new JLabel(" ");
        searchPanel.add(statusLabel);

        // Добавляем подсказки
        searchField.setToolTipText("Введите байты в hex: 'A1 B2 C3' или 'A1B2C3'");
        maskField.setToolTipText("Маска в hex: 'FF 00 FF' - где FF учитывать, 00 - игнорировать");

    }

    //GETTERS

    public JPanel getSearchPanel() {
        return searchPanel;
    }

    public JTextField getSearchField() {
        return searchField;
    }

    public JTextField getMaskField() {
        return maskField;
    }

    // OTHERS

    public void setSearchStatus(String status) {
        statusLabel.setText(status);
    }
    public String getSearchPattern() {
        return getSearchField().getText();
    }

    public String getSearchMask() {
        return getMaskField().getText();
    }


    // LISTENERS
    public void addSearchListener(ActionListener listener) {
        searchButton.addActionListener(listener);
    }

    public void addNextSearchListener(ActionListener listener) {
        nextButton.addActionListener(listener);
    }

    public void addPrevSearchListener(ActionListener listener) {
        prevButton.addActionListener(listener);
    }

}
