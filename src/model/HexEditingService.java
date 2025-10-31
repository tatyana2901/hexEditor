package model;

import model.cache.PageCache;

import java.io.IOException;

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


}
