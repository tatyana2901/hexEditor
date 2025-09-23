package model;

import javax.swing.table.AbstractTableModel;


//SWING WOKER?? ЧТО ЭТО?? посмотреть

public class HexTableModel extends AbstractTableModel {

    private HexEditorModel hexEditorModel;


    public HexTableModel(HexEditorModel hexEditorModel) {
        this.hexEditorModel = hexEditorModel;
    }


    @Override
    public int getRowCount() {
        return (int) Math.ceil((double) hexEditorModel.getData().size() / hexEditorModel.getItemsPerLine());
    }

    @Override
    public int getColumnCount() {
        // Количество столбцов в таблице (количество байтов в строке + столбец адреса)
        return hexEditorModel.getItemsPerLine() + 1;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        // Получение значения ячейки таблицы без учета строки-заголовка
        try {
            int index = rowIndex * hexEditorModel.getItemsPerLine() + columnIndex - 1;

            if (columnIndex == 0) {
                // Первый столбец - адрес
                return String.format("%08X", (rowIndex * hexEditorModel.getItemsPerLine() + hexEditorModel.getItemsPerPage() * (hexEditorModel.getCurrentPageNumber() - 1))); // Форматируем адрес в шестнадцатеричном виде с учетом постраничного отображения
            } else if (index >= 0 && index < hexEditorModel.getData().size()) {
                // Данные из файла
                return getFormattedRow(hexEditorModel.getType(), index);
            } else {
                return ""; // Пустая ячейка, если нет данных
            }
        } catch (Exception ex) {  // Логируем ошибку для разработчика
            System.err.println("Ошибка в ячейке [" + rowIndex + ", " + columnIndex + "]: " + ex.getMessage());
            ex.printStackTrace();
            // Возвращаем пустое значение для пользователя, чтобы не ломать GUI
            return "ERR";
        }
    }


    private String getFormattedRow(DataType type, int index) {
        try {
            Object value = hexEditorModel.getData().get(index);

            switch (type) {

                case BYTE:
                    return String.format("%02X", (Byte) value); // Всегда без знака (hex)
                case SHORT:
                    return hexEditorModel.isSigned() ? String.format("%d", (Short) value) : Short.toUnsignedInt((Short) value) + "";
                case INTEGER:
                    return hexEditorModel.isSigned() ? String.format("%d", (Integer) value) : Integer.toUnsignedString((Integer) value);
                case LONG:
                    return hexEditorModel.isSigned() ? String.format("%d", (Long) value) : Long.toUnsignedString((Long) value);
                case FLOAT:
                    return String.format("%10.3e", (Float) value);
                case DOUBLE:
                    return String.format("%10.3e", (Double) value);
                default:
                    return value.toString();
            }
        } catch (Exception ex) {
            System.err.println("Ошибка форматирования значения по индексу " + index + ": " + ex.getMessage());
            return "ERR";
        }
    }


    @Override
    public String getColumnName(int column) {
        try {
            if (column == 0) {
                return "Address"; // Название столбца адреса
            } else {
                return String.format("%02X", column - 1); // Названия столбцов данных (00, 01, 02...)
            }
        } catch (Exception ex) {
            System.err.println("Ошибка в формировании названия столца № " + column + ": " + ex.getMessage());
            return "ERR";
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false; // Ячейки не редактируемые
    }


    public void fireTableDataChanged() {
        super.fireTableDataChanged();
    }

}



