package view.components;

import javax.swing.*;
import java.awt.*;

public class LabelInfoPanel extends JPanel {

    private JLabel decimalSignedLabel;
    private JLabel decimalUnsignedLabel;

    public LabelInfoPanel() {
        setLayout(new GridLayout(2, 2)); // Два ряда, два столбца

        add(new JLabel("Decimal (Signed):", SwingConstants.RIGHT));
        decimalSignedLabel = new JLabel("0");
        add(decimalSignedLabel);

        add(new JLabel("Decimal (Unsigned):", SwingConstants.RIGHT));
        decimalUnsignedLabel = new JLabel("0");
        add(decimalUnsignedLabel);

        setBorder(BorderFactory.createTitledBorder("Byte Information"));
    }

    public void setByteValue(byte byteValue) {
        decimalSignedLabel.setText(String.valueOf((int) byteValue)); // Signed //ИСПОЛЬЗОВАТЬ ГОТОВЫЕ ФУНКЦИИ tounsigned
        decimalUnsignedLabel.setText(String.valueOf((byteValue & 0xFF))); // Unsigned
    }

    public void clear() {
        decimalSignedLabel.setText(" ");
        decimalUnsignedLabel.setText(" ");
    }



}
