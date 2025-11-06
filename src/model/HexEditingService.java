package model;

import model.cache.PageCache;

import java.io.IOException;
import java.util.Arrays;

import static model.cache.PageCache.getPageOffset;

public class HexEditingService {

    private HexEditorModel editorModel;

    public HexEditingService(HexEditorModel editorModel) {
        this.editorModel = editorModel;
    }

    //обнулить выделенные данные
    public void removeBytesWithZero(int startPosition, int length) throws IOException {
        byte[] zeros = new byte[length]; //создали пустой с нулями массив байт
        PageCache.setBytesArrayByIndex(editorModel.getCurrentPageNumber(), startPosition, zeros, editorModel.getItemsPerUnchangedPage());
    }

    //удалить со сдвигом выделенные данные
    public void removeBytesWithShift(int startPosition, int length) throws IOException {
        PageCache.deleteBytesWithShift(editorModel.getCurrentPageNumber(), startPosition, length, editorModel.getItemsPerUnchangedPage());
    }

    // Вставить байты со сдвигом (расширяет файл)
    public void insertBytesWithShift(int startPosition, byte[] bytesToInsert) {
        if (bytesToInsert == null || bytesToInsert.length == 0) {
            throw new IllegalArgumentException("Массив байтов для вставки не может быть пустым");
        }
        PageCache.insertBytesArrayWithShift(editorModel.getCurrentPageNumber(), startPosition, bytesToInsert, editorModel.getItemsPerUnchangedPage());
    }

    public void insertBytesWithOverwrite(int startPosition, byte[] bytesToInsert)  {
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

        // Сортируем индексы для корректного определения диапазона
        int[] sortedRows = selectedRows.clone();
        int[] sortedColumns = selectedColumns.clone();
        Arrays.sort(sortedRows);
        Arrays.sort(sortedColumns);

        int minRow = sortedRows[0];
        int maxRow = sortedRows[sortedRows.length - 1];
        int minCol = sortedColumns[0];
        int maxCol = sortedColumns[sortedColumns.length - 1];

        // Проверяем, что выделены только столбцы с данными (не адрес)
        if (minCol == 0) {
            minCol = 1; // Первый столбец - адрес, его пропускаем
            if (minCol > maxCol) {
                throw new IllegalArgumentException("Выделите байты для обнуления!");
            }
        }

        // Преобразуем координаты таблицы в позиции в файле
        int itemsPerLine = editorModel.getItemsPerLine();
        //  int itemsPerPage = getItemsPerPage();
        //  int pageOffset = (currentPageNumber - 1) * itemsPerPage;
        int pageOffset = getPageOffset(editorModel.getCurrentPageNumber(), editorModel.getItemsPerUnchangedPage());
        // Начальная позиция в файле
        int startPos = minRow * itemsPerLine + (minCol - 1) + pageOffset;
        // Конечная позиция в файле
        int endPos = maxRow * itemsPerLine + (maxCol - 1) + pageOffset;

        // Длина выделенного блока
        int length = (endPos - startPos) + 1;

        // Проверяем, что позиции в пределах файла
        if (startPos > editorModel.getFileSize()) {
            throw new IllegalArgumentException("Выход позиции за границу файла.");
        }

        // Корректируем длину, если выделение выходит за пределы файла
        if (startPos + length > editorModel.getFileSize()) {
            length = (int) (editorModel.getFileSize() - startPos);
        }

        return new int[]{startPos, length};
    }


}
