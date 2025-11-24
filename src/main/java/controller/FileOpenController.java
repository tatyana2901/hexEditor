package controller;

import model.HexEditorModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import view.HexEditorView;
import view.components.FileOpenView;

import java.io.File;
import java.io.IOException;

public class FileOpenController {
    private static final Logger logger = LoggerFactory.getLogger(FileOpenController.class);
    private final FileOpenView fileOpenView;
    private final HexEditorModel editorModel;
    private final DisplayHelper helper;
    private final HexEditorView editorView;

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
                logger.error("fail method openFile", ex);

            }
        }
    }
}
