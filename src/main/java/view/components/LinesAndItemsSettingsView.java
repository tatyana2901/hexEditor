package view.components;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeListener;
import java.awt.FlowLayout;

public class LinesAndItemsSettingsView {
    private final JSpinner linesPerPageSpinner;
    private final JSpinner itemsPerLineSpinner;
    private final JPanel panel;

    public LinesAndItemsSettingsView() {
        panel = new JPanel();
        JLabel linesPerPageLabel = new JLabel("Количество строк на странице:");
        linesPerPageSpinner = new JSpinner(new SpinnerNumberModel(16, 1, 256, 1));
        JLabel itemsPerLineLabel = new JLabel("Элементов в строке:");
        itemsPerLineSpinner = new JSpinner(new SpinnerNumberModel(16, 1, 1024, 1));

        panel.add(linesPerPageLabel);
        panel.add(linesPerPageSpinner);
        panel.add(itemsPerLineLabel);
        panel.add(itemsPerLineSpinner);
        panel.setLayout(new FlowLayout());
    }


    public JPanel getLinesAndItemsSettingsPanel() {
        return panel;
    }

    public int getLinesPerPageInput() {
        return (int) linesPerPageSpinner.getValue();
    }

    public int getItemsPerLineInput() {
        return (int) itemsPerLineSpinner.getValue();
    }

    public void addLinesPerPageListener(ChangeListener listener) {
        linesPerPageSpinner.addChangeListener(listener);
    }

    public void addItemsPerLineListener(ChangeListener listener) {
        itemsPerLineSpinner.addChangeListener(listener);
    }

}
