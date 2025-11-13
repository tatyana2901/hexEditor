package controller;

import model.HexEditorModel;
import view.HexEditorView;
import view.components.FileOpenView;

import java.io.File;
import java.io.IOException;

public class FileOpenController {
    private FileOpenView fileOpenView;
    private HexEditorModel editorModel;
    private DisplayHelper helper;
    private HexEditorView editorView;

    public FileOpenController(FileOpenView fileOpenView, HexEditorModel editorModel, DisplayHelper helper, HexEditorView editorView) {
        this.fileOpenView = fileOpenView;
        this.editorModel = editorModel;
        this.helper = helper;
        this.editorView = editorView;
        fileOpenView.addOpenFileListener(e -> openFile());
    }

    private void openFile() {

        File file = fileOpenView.showOpenFileDialog();
        if (file != null) {
            try {
                editorModel.initializeModel(file);
                helper.displayPage(1);
                fileOpenView.setFileInfo("Выбран файл: " + file.getName());

            } catch (IllegalArgumentException | IllegalStateException | IOException ex) {
               editorView.showErrorDialog("Ошибка при чтении файла: " + ex.getMessage(), "Ошибка");
            }
        }
    }
}
