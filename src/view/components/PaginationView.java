package view.components;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.event.ActionListener;

public class PaginationView {
    private JPanel paginationPanel;
    private JTextField pageNumberField;
    private JLabel pageInfoLabel;
    private JButton loadPageButton;
    private JButton nextPageButton;
    private JButton prevPageButton;

    public PaginationView() {
        paginationPanel = new JPanel();
        pageNumberField = new JTextField(5);
        pageInfoLabel = new JLabel(" / 0");
        loadPageButton = new JButton("Загрузить страницу");
        prevPageButton = new JButton("Предыдущая страница");
        nextPageButton = new JButton("Следующая страница");

        paginationPanel.add(new JLabel("Страница:"));
        paginationPanel.add(pageNumberField);
        paginationPanel.add(pageInfoLabel);
        paginationPanel.add(loadPageButton);
        paginationPanel.add(prevPageButton);
        paginationPanel.add(nextPageButton);
    }


    public JTextField getPageNumberField() {
        return pageNumberField;
    }

    public JPanel getPaginationPanel() {
        return paginationPanel;
    }

    public void setPageInfo(int currentPage, int totalPages) {
        pageInfoLabel.setText(" / " + totalPages);
        pageNumberField.setText(String.valueOf(currentPage));
    }

    public int getPageNumberInput() {
        return Integer.parseInt(getPageNumberField().getText());
    }


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
