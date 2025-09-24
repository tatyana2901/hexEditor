package model;

import model.cache.PageCache;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.*;

public class HexEditorModel {

    //String dataType - формат данных - short,byte,int,float ...
    List<Object> data;//- список объектов для передачи в таблицу jtable/ при передаче или после передачи объект должны конветироваться специальными методами в нужные типы данных

    private int itemsPerLine = 16; // количество элементов в одной строке по умолчанию 16
    private int linesPerPage = 16; // количество строк на странице, может быть изменено пользователем. по умолчанию 10
    private int currentPageNumber = 1; // номер текущей страницы
    private long fileSize;
    private long totalPages;
    private File file;
    private boolean isFileACopy = false;
    private boolean signed = true; // по умолчанию отображение со знаком
    private DataType type = DataType.BYTE; //по умолчанию тип отображения  - байт;


    public HexEditorModel() {
        this.data = new ArrayList<>();
    }

    public List<Object> getData() {
        return data;
    }

    public void setData(List<Object> data) {
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

    public int getLinesPerPage() {
        return linesPerPage;
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
    }

    public int getCurrentPageNumber() {
        return currentPageNumber;
    }


    public long getTotalPages() {
        return totalPages;
    }

    public int getItemsPerPage() {
        return linesPerPage * itemsPerLine;
    }//количество item??? на странице. Для отображения по 1 бату items = кол-во байт

    private void calculateTotalPages() {

        totalPages = (int) Math.ceil((double) fileSize / (itemsPerLine * linesPerPage * type.getBlockSize()));
    }


    private List<Object> readPageData(int pageNumber) throws IOException {
        /*если модификатор доступа поменяется на public, то нужно будет добавить проверки
        if (file == null) {
        throw new IllegalStateException("Файл не открыт. Сначала выберите файл.");
    }if (pageNumber < 1 || pageNumber > totalPages) {
        throw new IllegalArgumentException("Некорректный номер страницы.");
    }*/
        List<Byte> bytesPageData = new ArrayList<>();
        long startPosition = (pageNumber - 1) * (long) itemsPerLine * linesPerPage * type.getBlockSize();

        try (RandomAccessFile raf = new RandomAccessFile(file, "r")) {

            raf.seek(startPosition); //устанавливаем курсор на начальной позиции нужной страницы

            int bytesToRead = Math.min(getItemsPerPage() * type.getBlockSize(), (int) (fileSize - startPosition));
            for (int i = 0; i < bytesToRead; i++) {
                bytesPageData.add(raf.readByte());
            }
        }

        List<Object> objectPageData = BlockDataConverter.convertToTypedObjectList(bytesPageData, type); //конвертируем список байтов в нужный тип числа


        PageCache.addCachePage(new PageCache(objectPageData, pageNumber)); //кладем прочитанную страницу в кэш
        // System.out.println(bytesPageData.size());
        // System.out.println(objectPageData); //ТЕСТ
        return objectPageData;
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
        data.clear(); //очистка данных текущей страницы
        this.file = file;
        this.fileSize = file.length();
        calculateTotalPages();
    }


    public List<Integer> findBytes(byte[] searchPattern, byte[] mask) throws IOException {
        List<Integer> positions = new ArrayList<>();

        if (file == null) {
            throw new IllegalStateException("Файл не открыт");
        }

        try (RandomAccessFile raf = new RandomAccessFile(file, "r")) {
            byte[] buffer = new byte[searchPattern.length];
            long fileSize = raf.length();

            for (long i = 0; i <= fileSize - searchPattern.length; i++) { //i - номер байта в файле
                raf.seek(i);
                raf.readFully(buffer);

                boolean match = true;
                for (int j = 0; j < searchPattern.length; j++) {
                    if (mask != null) {
                        // Применяем маску: учитываем только биты, где mask[j] != 0
                        if ((buffer[j] & mask[j]) != (searchPattern[j] & mask[j])) {
                            match = false;
                            break;
                        }
                    } else {
                        // Без маски - точное совпадение
                        if (buffer[j] != searchPattern[j]) {
                            match = false;
                            break;
                        }
                    }
                }

                if (match) {
                    positions.add((int) i); //i - индекс байта, с которого начинется совпадение начала совпадения
                }
            }
        }

        return positions;
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
        int itemsPerLine = getItemsPerLine();
        int itemsPerPage = getItemsPerPage();
        int pageOffset = (currentPageNumber - 1) * itemsPerPage;

        // Начальная позиция в файле
        int startPos = minRow * itemsPerLine + (minCol - 1) + pageOffset;

        // Конечная позиция в файле
        int endPos = maxRow * itemsPerLine + (maxCol - 1) + pageOffset;

        // Длина выделенного блока
        int length = (endPos - startPos) + 1;

        // Проверяем, что позиции в пределах файла
        if (startPos >= fileSize) {
            throw new IllegalArgumentException("ыход позиции за границу файла.");
        }

        // Корректируем длину, если выделение выходит за пределы файла
        if (startPos + length > fileSize) {
            length = (int) (fileSize - startPos);
        }

        return new int[]{startPos, length};
    }



    //РАЗБИТЬ МЕТОД!!!
    public void zeroOutBytes(int startPosition, int length) throws IOException {

        if (type != DataType.BYTE) {
            throw new IllegalStateException("Редактирование разрешено только для типа BYTE");
        }
        if (file == null) {
            throw new IllegalStateException("Файл не открыт");
        }
        if (startPosition < 0 || length <= 0 || startPosition + length > fileSize) {
            throw new IllegalArgumentException("Некорректная позиция или длина");
        }

        // Создаем новый файл для изменений
        File editedFile = new File(file.getParent(), "edited_" + file.getName());

        try (RandomAccessFile sourceRaf = new RandomAccessFile(file, "r");
             RandomAccessFile targetRaf = new RandomAccessFile(editedFile, "rw")) {

            // 1. Копируем данные ДО обнуляемого блока
            if (startPosition > 0) {
                byte[] beforeBuffer = new byte[startPosition];
                sourceRaf.seek(0);
                sourceRaf.readFully(beforeBuffer);
                targetRaf.write(beforeBuffer);
            }

            // 2. Записываем нули вместо удаляемого блока
            byte[] zeros = new byte[length];
            targetRaf.write(zeros);

            // 3. Пропускаем обнуляемый блок в исходном файле
            sourceRaf.seek(startPosition + length);

            // 4. Копируем данные ПОСЛЕ обнуляемого блока
            long bytesAfter = fileSize - (startPosition + length);
            if (bytesAfter > 0) {
                byte[] afterBuffer = new byte[(int) bytesAfter];
                sourceRaf.readFully(afterBuffer);
                targetRaf.write(afterBuffer);
            }
        }

        if (isFileACopy) {

            this.file.delete();
        }

        this.file = editedFile;
        this.isFileACopy = true;
        // Обновляем размер файла в модели
        this.fileSize = file.length();
    }



}
