package model;

import model.cache.PageCache;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.stream.Collectors;

import static model.cache.PageCache.*;

public class HexEditorModel {

    byte[] data;
    private int itemsPerLine = 16; // количество элементов в одной строке по умолчанию 16
    private int linesPerPage = 16; // количество строк на странице, может быть изменено пользователем. по умолчанию 10
    private int currentPageNumber = 1; // номер текущей страницы
    private long fileSize;
    private long totalPages;
    private File file;

    private boolean signed = true; // по умолчанию отображение со знаком
    private DataType type = DataType.BYTE; //по умолчанию тип отображения  - байт;

    //   private static Map<Integer, Integer> pageRowCountsChanged = new HashMap<>(); // номер страницы - количество строк на странице


    public HexEditorModel() {
        this.data = new byte[0];
    }


    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public int getItemsPerLine() {
        return itemsPerLine;
    }

    public void setItemsPerLine(int itemsPerLine) {
        this.itemsPerLine = itemsPerLine;
        PageCache.clearCache(); //очищаем кэш после изменения количества столбцов на странице
        calculateTotalPages();
    }

    public boolean isSigned() {
        return signed;
    }

    public void setSigned(boolean signed) {
        this.signed = signed;
    }


    public void setLinesPerPage(int linesPerPage) {
        this.linesPerPage = linesPerPage;
        PageCache.clearCache(); //очищаем кэш после изменения количесва строк на странице
        calculateTotalPages();
    }

    public DataType getType() {
        return type;
    }

    public void setType(DataType type) {
        this.type = type;
        PageCache.clearCache(); //очищаем кэш после изменения настроек отображения блоками байт
        calculateTotalPages(); //пересчитываем количество страниц
    }


    public void setCurrentPageNumber(int currentPageNumber) {
        this.currentPageNumber = currentPageNumber;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
        this.fileSize = file.length();
    }


    public void setTotalPages(long totalPages) {
        this.totalPages = totalPages;
    }

    public int getCurrentPageNumber() {
        return currentPageNumber;
    }


    public long getTotalPages() {
        return totalPages;
    }

    public int getItemsPerUnchangedPage() {
        return linesPerPage * itemsPerLine;
    }//количество item??? на странице. Для отображения по 1 бату items = кол-во байт*/


    private void calculateTotalPages() {

        totalPages = (int) Math.ceil((double) fileSize / (itemsPerLine * linesPerPage * type.getBlockSize()));

        if (currentPageNumber > totalPages) {
            currentPageNumber = (int) totalPages;
        }
    }


    private byte[] readPageData(int pageNumber) throws IOException {


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
        PageCache.clearCache(); //очистка кэша
        this.file = file;
        this.fileSize = file.length();
        calculateTotalPages();
        // data = null;
        //  data = new byte[getItemsPerPage()];
    }

    // ОН ДОЛЖЕН БЫТЬ ДЕЙСТВИТЕЛЬНО ЗЕСЬ? В ЭТОМ КОМПОНЕНТЕ??? - Да поому что раотает с файлом
    public int[] findBytes(byte[] searchPattern, byte[] mask) throws IOException {
        if (file == null) {
            throw new IllegalStateException("Файл не открыт");
        }
        long totalPagesAmount = getTotalPages();
        //поиск первого результата по страницам
        for (int p = 1; p <= totalPagesAmount; p++) {

            if (!isPageInCache(p)) {
                addCachePage(new PageCache(readPageData(p), p));
            }
            byte[] pageBytes = getCachedPageByNumber(p);
            long pageSize = pageBytes.length;
            //добавить условие на длину страницы если она меньше длины поискового паттерна после редактирования напимер
            for (int i = 0; i <= pageSize - searchPattern.length; i++) { //i - номер байта на странице

                boolean match = true;
                int copyOfI = i;
                for (int j = 0; j < searchPattern.length; j++) {
                    if (mask != null) {
                        // Применяем маску: учитываем только биты, где mask[j] != 0
                        if ((pageBytes[i++] & mask[j]) != (searchPattern[j] & mask[j])) {
                            match = false;
                            i = copyOfI; //возвращаем i после выхода из цикла
                            break;
                        }
                    } else {
                        System.out.println("i" + " " + i);
                        // Без маски - точное совпадение
                        if (pageBytes[i++] != searchPattern[j]) {
                            match = false;
                            i = copyOfI; //возвращаем i после выхода из цикла
                            break;
                        }
                    }
                }
                if (match) {
                    System.out.println(p + " " + i);
                    return new int[]{p, copyOfI}; //индекс байта (byte [] из кэша )на странице, с которого начинется совпадение после первого сопадения поиск прекращается. продолжение поиска только после нажатия кнпки следующий
                }
            }
        }
        return null;
    }


    //ОН ТОЧНО ДОЛЖЕН БЫТЬ ЗДЕСЬ??
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
        int itemsPerLine = getItemsPerLine();
        //  int itemsPerPage = getItemsPerPage();
        //  int pageOffset = (currentPageNumber - 1) * itemsPerPage;
        int pageOffset = getPageOffset(currentPageNumber, getItemsPerUnchangedPage());
        // Начальная позиция в файле
        int startPos = minRow * itemsPerLine + (minCol - 1) + pageOffset;
        // Конечная позиция в файле
        int endPos = maxRow * itemsPerLine + (maxCol - 1) + pageOffset;

        // Длина выделенного блока
        int length = (endPos - startPos) + 1;

        // Проверяем, что позиции в пределах файла
        if (startPos > fileSize) {
            throw new IllegalArgumentException("Выход позиции за границу файла.");
        }

        // Корректируем длину, если выделение выходит за пределы файла
        if (startPos + length > fileSize) {
            length = (int) (fileSize - startPos);
        }

        return new int[]{startPos, length};
    }


}
