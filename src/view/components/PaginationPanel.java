package view.components;

import javax.swing.*;
import java.awt.event.ActionListener;

public class PaginationPanel extends JPanel {
    private JTextField pageNumberField;
    private JLabel pageInfoLabel;
    private JButton loadPageButton;
    private JButton nextPageButton;
    private JButton prevPageButton;

    public PaginationPanel() {

        pageNumberField = new JTextField(5);
        pageInfoLabel = new JLabel(" / 0");
        loadPageButton = new JButton("Загрузить страницу");
        prevPageButton = new JButton("Предыдущая страница");
        nextPageButton = new JButton("Следующая страница");

        add(new JLabel("Страница:"));
        add(pageNumberField);
        add(pageInfoLabel);
        add(loadPageButton);
        add(prevPageButton);
        add(nextPageButton);
    }

    //GETTERS
    public JTextField getPageNumberField() {
        return pageNumberField;
    }

    public JLabel getPageInfoLabel() {
        return pageInfoLabel;
    }

    //LISTENERS
    public void addNextPageButtonListener(ActionListener listener) {  //метод добавления слушаля к кнопке загрузки страницы(конкретная реализация определена в контроллере)
        nextPageButton.addActionListener(listener);
    }

    public void addPrevPageButtonListener(ActionListener listener) {
        prevPageButton.addActionListener(listener);
    }

    public void addLoadPageButtonListener(ActionListener listener) {
        loadPageButton.addActionListener(listener);
    }

}
