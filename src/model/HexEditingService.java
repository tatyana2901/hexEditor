package model;

import model.cache.PageCache;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class HexEditingService {

    private HexEditorModel editorModel;


    private File editedFile = null;
    private File tempFile = null;
    private boolean isFileACopy = false;

    public HexEditingService(HexEditorModel editorModel) {
        this.editorModel = editorModel;
    }

    //обнулить выделенные данные
    public void removeBytesWithZero(int startPosition, int length) throws IOException {
        byte[] zeros = new byte[length]; //создали пустой с нулями массив байт
        PageCache.setBytesArrayByIndex(editorModel.getCurrentPageNumber(), startPosition, zeros);
    }

    //удалить со сдвигом выделенные данные
    public void removeBytesWithShift(int startPosition, int length) throws IOException {
        editFile(startPosition, length, raf -> {/*ничего не делаем*/}, (raf, position) -> raf.seek(position));
    }

    // Вставить байты со сдвигом (расширяет файл)
    public void insertBytesWithShift(int startPosition, byte[] bytesToInsert) throws IOException {
        if (bytesToInsert == null || bytesToInsert.length == 0) {
            throw new IllegalArgumentException("Массив байтов для вставки не может быть пустым");
        }

        editFile(startPosition, 0,
                raf -> raf.write(bytesToInsert),
                (raf, position) -> {
                    // Ничего не делаем - не пропускаем байты в исходном файле
                }
        );
    }

    public void insertBytesWithOverwrite(int startPosition, byte[] bytesToInsert) throws IOException {
        if (bytesToInsert == null || bytesToInsert.length == 0) {
            throw new IllegalArgumentException("Массив байтов для вставки не может быть пустым");
        }
        PageCache.setBytesArrayByIndex(editorModel.getCurrentPageNumber(), startPosition, bytesToInsert);
    }

    public void editSingleByte(int position, byte newValue) {
        if (editorModel.getType() != DataType.BYTE) {
            throw new IllegalStateException("Редактирование разрешено только для типа BYTE");
        }
        if (editorModel.getFile() == null) {
            throw new IllegalStateException("Файл не открыт");
        }
        if (position < 0 || position >= editorModel.getFileSize()) {
            throw new IllegalArgumentException("Некорректная позиция");
        }
        PageCache.setByteByIndex(editorModel.getCurrentPageNumber(), position, newValue);
    }

    //рассмотреть вариант чтения блоком сразу из файла при вставке со смещением и удалении со смещением
    private void editFile(int startPosition, int length, BytesWriter writer, SeekPositioner positioner) throws IOException {


        if (editorModel.getType() != DataType.BYTE) {
            throw new IllegalStateException("Редактирование разрешено только для типа BYTE");
        }
        if (editorModel.getFile() == null) {
            throw new IllegalStateException("Файл не открыт");
        }
        if (startPosition < 0 || length < 0 || startPosition + length > editorModel.getFileSize()) {
            throw new IllegalArgumentException("Некорректная позиция или длина");
        }


        File originalFile = editorModel.getFile();
        if (editedFile == null) {

            this.editedFile = new File(originalFile.getParent(), "edited_" + originalFile.getName());
            //если в папке уже есть файл с именем editedFile , то удаляем его из папки
            if (editedFile.exists()) {
                if (!editedFile.delete()) {
                    throw new IllegalStateException("Ошибка: файл " + editedFile.getName() + " уже существует. Удалите или перенесите его в другую папку.");
                }
            }
        }

        this.tempFile = new File(originalFile.getParent(), "temp_" + originalFile.getName());

        try (RandomAccessFile sourceRaf = new RandomAccessFile(originalFile, "r");
             RandomAccessFile targetRaf = new RandomAccessFile(tempFile, "rw")) {

            // 1. Копируем данные ДО изменяемого блока
            if (startPosition > 0) {
                byte[] beforeBuffer = new byte[startPosition];
                sourceRaf.seek(0);
                sourceRaf.readFully(beforeBuffer);
                targetRaf.write(beforeBuffer);
            }

            // 2. Функция вставки новых значений в редактируемую область, которая будет определена в конкретном методе
            writer.write(targetRaf);

            // 3. Пропускаем редактируемый блок в исходном файле ПРИ ВСТАВКЕ ЭТОЙ ОПЕРАЦИИ ВЫПОЛНЯТЬСЯ НЕ БУДЕТ - пропускать ничего не надо будет!!!!!


            if (length > 0) {
                positioner.setPosition(sourceRaf, startPosition + length);
            }

            // 4. Копируем данные ПОСЛЕ обнуляемого блока
            long bytesAfter = originalFile.length() - (startPosition + length);
            if (bytesAfter > 0) {
                byte[] afterBuffer = new byte[(int) bytesAfter];
                sourceRaf.readFully(afterBuffer);
                targetRaf.write(afterBuffer);
            }
        }

        if (isFileACopy) {
            if (!originalFile.delete()) {
                System.out.println(("Не получилось удалить временный файл " + originalFile.getName()));
            }
        }

        if (!tempFile.renameTo(editedFile)) {
            System.out.println("Не удалось переместить временный файл в редактируемый файл.");
        }

        editorModel.initializeModel(editedFile);
        this.isFileACopy = true;
    }


}

@FunctionalInterface
interface BytesWriter {
    void write(RandomAccessFile raf) throws IOException;
}

@FunctionalInterface
interface SeekPositioner {
    void setPosition(RandomAccessFile raf, long position) throws IOException;
}

