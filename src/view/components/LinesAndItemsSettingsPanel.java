package view.components;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import java.awt.*;

public class LinesAndItemsSettingsPanel extends JPanel {
    private JSpinner linesPerPageSpinner;
    private JSpinner itemsPerLineSpinner;

    public LinesAndItemsSettingsPanel() {

        JLabel linesPerPageLabel = new JLabel("Количество строк на странице:");
        linesPerPageSpinner = new JSpinner(new SpinnerNumberModel(16, 1, 256, 1)); // Default: 16, Min: 1, Max: 256, Step:1
        JLabel itemsPerLineLabel = new JLabel("Элементов в строке:");
        itemsPerLineSpinner = new JSpinner(new SpinnerNumberModel(16, 1, 1024, 1)); // Default: 16, Min: 1, Max: 1024, Step: 1

        add(linesPerPageLabel);
        add(linesPerPageSpinner);
        add(itemsPerLineLabel);
        add(itemsPerLineSpinner);
        setLayout(new FlowLayout());
    }

    //GETTERS
    public JSpinner getLinesPerPage() {
        return linesPerPageSpinner;
    }

    public JSpinner getItemsPerLine() {
        return itemsPerLineSpinner;
    }

    //LISTENERS
    public void addLinesPerPageSpinnerListener(ChangeListener listener) {
        linesPerPageSpinner.addChangeListener(listener);
    }

    public void addItemsPerLineSpinnerListener(ChangeListener listener) {
        itemsPerLineSpinner.addChangeListener(listener);
    }

}
