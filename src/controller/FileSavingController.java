package controller;

import model.HexEditorModel;
import model.cache.PageCache;
import view.components.FileSaveView;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;


public class FileSavingController {
    private HexEditorModel editorModel;
    private FileSaveView fileSaveView;

    public FileSavingController(HexEditorModel editorModel, FileSaveView fileSaveView) {
        this.editorModel = editorModel;
        this.fileSaveView = fileSaveView;
        fileSaveView.addSaveFileButtonListener(e -> saveChangesToTxtFile());
    }

    public void saveChangesToTxtFile() {
        fileSaveView.clearSaveResult();
        long lastPageNumber = editorModel.getTotalPages();
        int bytesOnPageQuantity = editorModel.getItemsPerUnchangedPage();
        long fileSize = editorModel.getFileSize();

        File originalFile = editorModel.getFile();
        File destinationFile = new File("edited_" + originalFile.getName());

        try (RandomAccessFile raf = new RandomAccessFile(originalFile, "r");
             FileOutputStream fos = new FileOutputStream(destinationFile);
             BufferedOutputStream bos = new BufferedOutputStream(fos)) {


            for (int i = 1; i <= lastPageNumber; i++) {
                if (PageCache.isPageInCache(i)) {
                    byte[] pageData = PageCache.getCachedPageByNumber(i);
                    bos.write(pageData);
                } else {

                    long currentOffset = (long) (i - 1) * bytesOnPageQuantity;

                    byte[] pageData;

                    if (i == lastPageNumber) {
                        pageData = new byte[(int) (fileSize - currentOffset)];
                    } else {
                        pageData = new byte[bytesOnPageQuantity];
                    }
                    raf.seek(currentOffset);
                    int bytesRead = raf.read(pageData);
                    if (bytesRead > 0) {
                        bos.write(pageData, 0, bytesRead);
                    }
                }
            }

            fileSaveView.appendSaveResult("Файл сохранен в: " + destinationFile.getAbsolutePath());
        } catch (FileNotFoundException e) {
            fileSaveView.appendSaveResult("Ошибка: Файл не найден - " + e.getMessage());
            System.out.println("Файл не найден");
        } catch (IOException e) {
            fileSaveView.appendSaveResult("Ошибка ввода-вывода: " + e.getMessage());
            e.printStackTrace();
        }


    }


}
