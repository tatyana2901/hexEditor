package view.components;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.GridLayout;

public class LabelInfoView {
    private final JPanel labelPanel;
    private final JLabel decimalSignedLabel;
    private final JLabel decimalUnsignedLabel;

    public LabelInfoView() {

        labelPanel = new JPanel();
        labelPanel.setLayout(new GridLayout(2, 2));

        labelPanel.add(new JLabel("Decimal (Signed):", SwingConstants.RIGHT));
        decimalSignedLabel = new JLabel("0");
        labelPanel.add(decimalSignedLabel);

        labelPanel.add(new JLabel("Decimal (Unsigned):", SwingConstants.RIGHT));
        decimalUnsignedLabel = new JLabel("0");
        labelPanel.add(decimalUnsignedLabel);

        labelPanel.setBorder(BorderFactory.createTitledBorder("Byte Information"));
    }

    public JPanel getLabelPanel() {
        return labelPanel;
    }

    public void setByteLabelText(byte byteValue) {
        decimalSignedLabel.setText(String.valueOf((int) byteValue));
        decimalUnsignedLabel.setText(String.valueOf((Byte.toUnsignedInt(byteValue))));

    }

    public void clearByteLabelText() {
        decimalSignedLabel.setText(" ");
        decimalUnsignedLabel.setText(" ");
    }


}
