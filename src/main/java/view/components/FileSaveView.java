package view.components;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.awt.BorderLayout;
import java.awt.event.ActionListener;

public class FileSaveView {

    private final JPanel fileSavingPanel;
    private final JButton saveFileButton;
    private final JTextArea saveResultTextArea;

    public FileSaveView() {
        this.fileSavingPanel = new JPanel();
        this.saveFileButton = new JButton("Сохранить изменения в файл");
        this.saveResultTextArea = new JTextArea(3, 30);
        saveResultTextArea.setEditable(false);
        saveResultTextArea.setLineWrap(true);
        saveResultTextArea.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(saveResultTextArea);

        fileSavingPanel.add(saveFileButton, BorderLayout.NORTH);
        fileSavingPanel.add(scrollPane, BorderLayout.CENTER);
    }

    public JPanel getFileSavingPanel() {
        return fileSavingPanel;
    }

    public void addSaveFileButtonListener(ActionListener listener) {
        saveFileButton.addActionListener(listener);
    }

    public void appendSaveResult(String text) {
        saveResultTextArea.append(text + "\n");
        saveResultTextArea.setCaretPosition(saveResultTextArea.getDocument().getLength());
    }

    public void clearSaveResult() {
        saveResultTextArea.setText("");
    }
}

