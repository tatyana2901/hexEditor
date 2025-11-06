package model;

import model.cache.PageCache;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class HexEditorModel {

    byte[] data;
    private int itemsPerLine = 16;
    private int linesPerPage = 16;
    private int currentPageNumber = 1;
    private long fileSize;
    private long totalPages;
    private File file;

    private boolean signed = true; // по умолчанию отображение со знаком
    private DataType type = DataType.BYTE; //по умолчанию тип отображения  - байт;


    public HexEditorModel() {
        this.data = new byte[0];
    }


    public byte[] getData() {
        return data;
    }

    public int getItemsPerLine() {
        return itemsPerLine;
    }

    public void setItemsPerLine(int itemsPerLine) {
        this.itemsPerLine = itemsPerLine;
        PageCache.clearCache();
        calculateTotalPages();
    }

    public boolean isSigned() {
        return signed;
    }

    public void setSigned(boolean signed) {
        this.signed = signed;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setLinesPerPage(int linesPerPage) {
        this.linesPerPage = linesPerPage;
        PageCache.clearCache();
        calculateTotalPages();
    }

    public DataType getType() {
        return type;
    }

    public void setType(DataType type) {
        this.type = type;
        PageCache.clearCache();
        calculateTotalPages();
    }

    public void setCurrentPageNumber(int currentPageNumber) {
        this.currentPageNumber = currentPageNumber;
    }

    public File getFile() {
        return file;
    }

    public int getCurrentPageNumber() {
        return currentPageNumber;
    }


    public long getTotalPages() {
        return totalPages;
    }

    public int getItemsPerUnchangedPage() {
        return linesPerPage * itemsPerLine;
    }


    private void calculateTotalPages() {

        totalPages = (int) Math.ceil((double) fileSize / (itemsPerLine * linesPerPage * type.getBlockSize()));

        if (currentPageNumber > totalPages) {
            currentPageNumber = (int) totalPages;
        }
    }


    public byte[] readPageData(int pageNumber) throws IOException {

        long startPosition = (pageNumber - 1) * (long) itemsPerLine * linesPerPage * type.getBlockSize();
        try (RandomAccessFile raf = new RandomAccessFile(file, "r")) {
            raf.seek(startPosition);
            int bytesToRead = Math.min(getItemsPerUnchangedPage() * type.getBlockSize(), (int) (fileSize - startPosition));
            byte[] bytesPageData = new byte[bytesToRead];
            raf.readFully(bytesPageData);
            PageCache.addCachePage(new PageCache(bytesPageData, pageNumber)); //кладем прочитанную страницу в кэш
            return bytesPageData;
        }
    }

    public void loadPageData(int pageNumber) throws IOException {

        if (pageNumber < 1 || pageNumber > totalPages) {
            throw new IllegalArgumentException("Задан некорректный номер страницы!");
        }
        if (file == null) {
            throw new IllegalStateException("Файл не открыт. Сначала выберите файл.");
        }
        if (PageCache.isPageInCache(pageNumber)) {
            data = PageCache.getCachedPageByNumber(pageNumber); // берем страницу из кэша
        } else {
            data = readPageData(pageNumber); // читаем страницу из файла
        }

    }


    public void initializeModel(File file) {

        if (file == null) {
            throw new IllegalArgumentException("Файл не может быть null.");
        }
        if (!file.exists()) {
            throw new IllegalArgumentException("Файла с таким названием не существует.");
        }
        PageCache.clearCache();
        this.file = file;
        this.fileSize = file.length();
        calculateTotalPages();
    }


}
