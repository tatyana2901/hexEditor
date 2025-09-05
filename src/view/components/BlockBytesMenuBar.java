package view.components;

import javax.swing.*;
import java.awt.event.ActionListener;

public class BlockBytesMenuBar extends JMenuBar {

    private JMenu viewMenu;
    private JMenu dataTypeMenu;
    private JMenuItem byteItem, shortItem, intItem, longItem, floatItem, doubleItem;
    private JRadioButtonMenuItem signedItem, unsignedItem;
    private ButtonGroup integerSignGroup;


    public BlockBytesMenuBar() {

        viewMenu = new JMenu("Вид");
        dataTypeMenu = new JMenu("Тип данных");

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

        // Создаем подменю для выбора "со знаком/без знака"
        JMenu integerSignMenu = new JMenu("Целое число: знак");
        signedItem = new JRadioButtonMenuItem("Со знаком");
        unsignedItem = new JRadioButtonMenuItem("Без знака");
        integerSignGroup = new ButtonGroup();
        integerSignGroup.add(signedItem);
        integerSignGroup.add(unsignedItem);

        integerSignMenu.add(signedItem);
        integerSignMenu.add(unsignedItem);

        viewMenu.add(dataTypeMenu);
        viewMenu.add(integerSignMenu); // Добавляем подменю в меню "Вид"

        add(viewMenu);

        byteItem.setSelected(true); // Устанавливаем Byte выбранным по умолчанию
        signedItem.setSelected(true); // Целые числа по умолчанию "со знаком"
        setIntegerSignOptionsEnabled(false); // Сначала отключаем опции "со знаком/без знака"

    }

    // Методы для включения/выключения опций "со знаком/без знака"
    public void setIntegerSignOptionsEnabled(boolean enabled) {
        signedItem.setEnabled(enabled);
        unsignedItem.setEnabled(enabled);
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

    //LISTENERS

    public void addSignedItemListener(ActionListener listener) {
        signedItem.addActionListener(listener);
    }

    public void addUnsignedItemListener(ActionListener listener) {
        unsignedItem.addActionListener(listener);
    }


}
