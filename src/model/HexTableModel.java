package model;

import model.cache.PageCache;

import javax.swing.table.AbstractTableModel;

import static model.cache.PageCache.getPageOffset;


public class HexTableModel extends AbstractTableModel {

    private HexEditorModel hexEditorModel;


    public HexTableModel(HexEditorModel hexEditorModel) {
        this.hexEditorModel = hexEditorModel;
    }


    @Override
    public int getRowCount() {

        //   return hexEditorModel.getLinesPerPage(); //ЗАМЕНИЛА ОПРЕДЕЛЕНИЕ!!!
        return (int) Math.ceil((double) hexEditorModel.getData().length / hexEditorModel.getType().getBlockSize() / hexEditorModel.getItemsPerLine());
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
                //  return String.format("%08X", (rowIndex * hexEditorModel.getItemsPerLine() + hexEditorModel.getItemsPerPage() * (hexEditorModel.getCurrentPageNumber() - 1))); // Форматируем адрес в шестнадцатеричном виде с учетом постраничного отображения
                return String.format("%08X", (rowIndex * hexEditorModel.getItemsPerLine() + getPageOffset(hexEditorModel.getCurrentPageNumber(), hexEditorModel.getItemsPerUnchangedPage()))); // Форматируем адрес в шестнадцатеричном виде с учетом постраничного отображения
            } else if (index >= 0 && index < hexEditorModel.getData().length) {
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
            //   byte value = hexEditorModel.getData().get(index);

            byte[] bytesToConvert = BlockDataConverter.getBlockByPositionInList(hexEditorModel.getData(), type.getBlockSize(), index);
            switch (type) {

                case BYTE:
                    byte[] bytesPage = hexEditorModel.getData();

                    return String.format("%02X", bytesPage[index]); // Всегда без знака (hex)
                case SHORT:
                    short shortValue = BlockDataConverter.getShortsFromBytes(bytesToConvert);
                    return hexEditorModel.isSigned() ? String.format("%d", shortValue) : Short.toUnsignedInt(shortValue) + "";
                case INTEGER:
                    int intValue = BlockDataConverter.getIntsFromBytes(bytesToConvert);
                    return hexEditorModel.isSigned() ? String.format("%d", intValue) : Integer.toUnsignedString(intValue);
                case LONG:
                    long longValue = BlockDataConverter.getLongsFromBytes(bytesToConvert);
                    return hexEditorModel.isSigned() ? String.format("%d", longValue) : Long.toUnsignedString(longValue);
                case FLOAT:
                    float floatValue = BlockDataConverter.getFloatsFromBytes(bytesToConvert);
                    return String.format("%10.3e", floatValue);
                case DOUBLE:
                    double doubleValue = BlockDataConverter.getDoublesFromBytes(bytesToConvert);
                    return String.format("%10.3e", doubleValue);
                default:
                    return "";
            }
        } catch (Exception ex) {
            System.err.println("Ошибка форматирования значения по индексу " + index + ": " + ex.getMessage());
            return "0";
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



