package view.components;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import java.awt.*;

public class LinesAndItemsSettingsView {
    private JSpinner linesPerPageSpinner;
    private JSpinner itemsPerLineSpinner;
    private JPanel panel;

    public LinesAndItemsSettingsView() {
        panel = new JPanel();
        JLabel linesPerPageLabel = new JLabel("Количество строк на странице:");
        linesPerPageSpinner = new JSpinner(new SpinnerNumberModel(16, 1, 256, 1)); // Default: 16, Min: 1, Max: 256, Step:1
        JLabel itemsPerLineLabel = new JLabel("Элементов в строке:");
        itemsPerLineSpinner = new JSpinner(new SpinnerNumberModel(16, 1, 1024, 1)); // Default: 16, Min: 1, Max: 1024, Step: 1

        panel.add(linesPerPageLabel);
        panel.add(linesPerPageSpinner);
        panel.add(itemsPerLineLabel);
        panel.add(itemsPerLineSpinner);
        panel.setLayout(new FlowLayout());
    }

    //GETTERS


    public JPanel getLinesAndItemsSettingsPanel() {
        return panel;
    }

    public int getLinesPerPageInput() {
        return (int) linesPerPageSpinner.getValue();
    }

    public int getItemsPerLineInput() {
        return (int) itemsPerLineSpinner.getValue();
    }

    //LISTENERS
    public void addLinesPerPageListener(ChangeListener listener) {
        linesPerPageSpinner.addChangeListener(listener);
    }

    public void addItemsPerLineListener(ChangeListener listener) {
        itemsPerLineSpinner.addChangeListener(listener);
    }

}
