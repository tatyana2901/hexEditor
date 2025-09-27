package model;

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
        editFile(startPosition, length, zeros);
    }

    //удалить со сдвигом выделенные данные
    public void removeBytesWithShift(int startPosition, int length) throws IOException {
        editFile(startPosition, length, null);
    }

    //вставить байты со сдвигом
    public void insertBytesWithShift() {
    }

    //вставить байты с перезаписью
    public void insertBytesOnCurrent(int startPosition, int length, byte[] rangeToInsert) throws IOException {

        editFile(startPosition, length, rangeToInsert);

    }


    private void editFile(int startPosition, int length, byte[] rangeToInsert) throws IOException {


        if (editorModel.getType() != DataType.BYTE) {
            throw new IllegalStateException("Редактирование разрешено только для типа BYTE");
        }
        if (editorModel.getFile() == null) {
            throw new IllegalStateException("Файл не открыт");
        }
        if (startPosition < 0 || length <= 0 || startPosition + length > editorModel.getFileSize()) {
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

            // 1. Копируем данные ДО обнуляемого блока
            if (startPosition > 0) {
                byte[] beforeBuffer = new byte[startPosition];
                sourceRaf.seek(0);
                sourceRaf.readFully(beforeBuffer);
                targetRaf.write(beforeBuffer);
            }

            insertBytesRange(targetRaf, rangeToInsert);

            // 3. Пропускаем обнуляемый блок в исходном файле ПРИ ВСТАВКЕ ЭТОЙ ОПЕРАЦИИ ВЫПОЛНЯТЬСЯ НЕ БУДЕТ - пропускать ничего не надо будет!!!!!
            sourceRaf.seek(startPosition + length);

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

    private void insertBytesRange(RandomAccessFile targetRaf, byte[] rangeToInsert) throws IOException {
        if (rangeToInsert == null) {
            return;
        }
        targetRaf.write(rangeToInsert);
    }


}
