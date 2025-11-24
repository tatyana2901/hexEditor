package view.components;

import javax.swing.ButtonGroup;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import java.awt.event.ActionListener;

public class BlockBytesView {
    private final JMenuBar menuBar;
    private final JMenuItem byteItem;
    private final JMenuItem shortItem;
    private final JMenuItem intItem;
    private final JMenuItem longItem;
    private final JMenuItem floatItem;
    private final JMenuItem doubleItem;
    private final JRadioButtonMenuItem signedItem;
    private final JRadioButtonMenuItem unsignedItem;


    public BlockBytesView() {
        menuBar = new JMenuBar();
        JMenu viewMenu = new JMenu("Вид");
        JMenu dataTypeMenu = new JMenu("Тип данных");

        byteItem = new JMenuItem("Byte");
        shortItem = new JMenuItem("Short");
        intItem = new JMenuItem("Integer");
        longItem = new JMenuItem("Long");
        floatItem = new JMenuItem("Float");
        doubleItem = new JMenuItem("Double");

        dataTypeMenu.add(byteItem);
        dataTypeMenu.add(shortItem);
        dataTypeMenu.add(intItem);
        dataTypeMenu.add(longItem);
        dataTypeMenu.add(floatItem);
        dataTypeMenu.add(doubleItem);

        JMenu integerSignMenu = new JMenu("Целое число: знак");
        signedItem = new JRadioButtonMenuItem("Со знаком");
        unsignedItem = new JRadioButtonMenuItem("Без знака");
        ButtonGroup integerSignGroup = new ButtonGroup();
        integerSignGroup.add(signedItem);
        integerSignGroup.add(unsignedItem);

        integerSignMenu.add(signedItem);
        integerSignMenu.add(unsignedItem);

        viewMenu.add(dataTypeMenu);
        viewMenu.add(integerSignMenu);

        menuBar.add(viewMenu);

        byteItem.setSelected(true);
        signedItem.setSelected(true);
        setIntegerSignOptionsEnabled(false);

    }


    public void setIntegerSignOptionsEnabled(boolean enabled) {
        signedItem.setEnabled(enabled);
        unsignedItem.setEnabled(enabled);
    }

    public JMenuBar getMenuBar() {
        return menuBar;
    }

    public JMenuItem getByteItem() {
        return byteItem;
    }

    public JMenuItem getShortItem() {
        return shortItem;
    }

    public JMenuItem getIntItem() {
        return intItem;
    }

    public JMenuItem getLongItem() {
        return longItem;
    }

    public JMenuItem getFloatItem() {
        return floatItem;
    }

    public JMenuItem getDoubleItem() {
        return doubleItem;
    }

    public void addSignedItemListener(ActionListener listener) {
        signedItem.addActionListener(listener);
    }

    public void addUnsignedItemListener(ActionListener listener) {
        unsignedItem.addActionListener(listener);
    }

    public void setupDataTypeListeners(ActionListener byteListener,
                                       ActionListener shortListener,
                                       ActionListener intListener,
                                       ActionListener longListener,
                                       ActionListener floatListener,
                                       ActionListener doubleListener) {
        getByteItem().addActionListener(byteListener);
        getShortItem().addActionListener(shortListener);
        getIntItem().addActionListener(intListener);
        getLongItem().addActionListener(longListener);
        getFloatItem().addActionListener(floatListener);
        getDoubleItem().addActionListener(doubleListener);
    }

}
