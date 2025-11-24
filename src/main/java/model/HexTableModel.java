package model;

import javax.swing.table.AbstractTableModel;

import static model.cache.PageCache.getPageOffset;


public class HexTableModel extends AbstractTableModel {

    private final HexEditorModel hexEditorModel;


    public HexTableModel(HexEditorModel hexEditorModel) {
        this.hexEditorModel = hexEditorModel;
    }


    @Override
    public int getRowCount() {
        return (int) Math.ceil((double) hexEditorModel.getData().length / hexEditorModel.getType().getBlockSize() / hexEditorModel.getItemsPerLine());
    }

    @Override
    public int getColumnCount() {
        return hexEditorModel.getItemsPerLine() + 1;
    }

    /**
     * Возвращает значение для ячейки таблицы по указанным индексам.
     *
     * @param rowIndex    индекс строки
     * @param columnIndex индекс столбца
     * @return отформатированное значение ячейки или пустую строку
     */
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {

        int index = rowIndex * hexEditorModel.getItemsPerLine() + columnIndex - 1;

        if (columnIndex == 0) {
            return String.format("%08X", (rowIndex * hexEditorModel.getItemsPerLine() + getPageOffset(hexEditorModel.getCurrentPageNumber(), hexEditorModel.getItemsPerUnchangedPage()))); // Форматируем адрес в шестнадцатеричном виде с учетом постраничного отображения
        } else if (index >= 0 && index < hexEditorModel.getData().length / hexEditorModel.getType().getBlockSize()) {
            return getFormattedRow(hexEditorModel.getType(), index);
        } else {
            return "";
        }

    }

    /**
     * Форматирует значение ячейки в соответствии с типом данных.
     *
     * @param type  тип данных для форматирования
     * @param index индекс элемента в данных
     * @return отформатированное строковое представление значения
     */
    private String getFormattedRow(DataType type, int index) {

        byte[] bytesToConvert = BlockDataConverter.getBlockByPositionInList(hexEditorModel.getData(), type.getBlockSize(), index);
        switch (type) {

            case BYTE:
                byte[] bytesPage = hexEditorModel.getData();
                return String.format("%02X", bytesPage[index]);
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

    }


    @Override
    public String getColumnName(int column) {

        if (column == 0) {
            return "Address";
        } else {
            return String.format("%02X", column - 1);
        }

    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;


    }

}



