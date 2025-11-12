package service;

import model.HexEditorModel;
import model.cache.PageCache;

import java.util.Arrays;

import static model.cache.PageCache.getPageOffset;

public class EditingService {

    private HexEditorModel editorModel;

    public EditingService(HexEditorModel editorModel) {
        this.editorModel = editorModel;
    }

    public void removeBytesWithZero(int startPosition, int length) {
        byte[] zeros = new byte[length];
        PageCache.setBytesArrayByIndex(editorModel.getCurrentPageNumber(), startPosition, zeros, editorModel.getItemsPerUnchangedPage());
    }

    public void removeBytesWithShift(int startPosition, int length) {
        PageCache.deleteBytesWithShift(editorModel.getCurrentPageNumber(), startPosition, length, editorModel.getItemsPerUnchangedPage());
    }

    public void insertBytesWithShift(int startPosition, byte[] bytesToInsert) {
        if (bytesToInsert == null || bytesToInsert.length == 0) {
            throw new IllegalArgumentException("Массив байтов для вставки не может быть пустым");
        }
        PageCache.insertBytesArrayWithShift(editorModel.getCurrentPageNumber(), startPosition, bytesToInsert, editorModel.getItemsPerUnchangedPage());
    }

    public void insertBytesWithOverwrite(int startPosition, byte[] bytesToInsert) {
        if (bytesToInsert == null || bytesToInsert.length == 0) {
            throw new IllegalArgumentException("Массив байтов для вставки не может быть пустым");
        }
        PageCache.setBytesArrayByIndex(editorModel.getCurrentPageNumber(), startPosition, bytesToInsert, editorModel.getItemsPerUnchangedPage());
    }

    public void editSingleByte(int position, byte newValue) {
        PageCache.setByteByIndex(editorModel.getCurrentPageNumber(), position, newValue, editorModel.getItemsPerUnchangedPage());
    }

    public int[] getSelectedBytesRange(int[] selectedRows, int[] selectedColumns) {
        if (selectedRows == null || selectedColumns == null ||
                selectedRows.length == 0 || selectedColumns.length == 0) {
            throw new IllegalArgumentException("Выделите байты для обнуления!");
        }

        int[] sortedRows = selectedRows.clone();
        int[] sortedColumns = selectedColumns.clone();
        Arrays.sort(sortedRows);
        Arrays.sort(sortedColumns);

        int minRow = sortedRows[0];
        int maxRow = sortedRows[sortedRows.length - 1];
        int minCol = sortedColumns[0];
        int maxCol = sortedColumns[sortedColumns.length - 1];


        if (minCol == 0) {
            minCol = 1;
            if (minCol > maxCol) {
                throw new IllegalArgumentException("Выделите байты для обнуления!");
            }
        }

        int itemsPerLine = editorModel.getItemsPerLine();
        int pageOffset = getPageOffset(editorModel.getCurrentPageNumber(), editorModel.getItemsPerUnchangedPage());
        int startPos = minRow * itemsPerLine + (minCol - 1) + pageOffset;
        int endPos = maxRow * itemsPerLine + (maxCol - 1) + pageOffset;
        int length = (endPos - startPos) + 1;


        if (startPos > editorModel.getFileSize()) {
            throw new IllegalArgumentException("Выход позиции за границу файла.");
        }
        if (startPos + length > editorModel.getFileSize()) {
            length = (int) (editorModel.getFileSize() - startPos);
        }

        return new int[]{startPos, length};
    }


}
