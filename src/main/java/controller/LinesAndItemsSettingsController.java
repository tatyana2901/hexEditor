package controller;

import model.HexEditorModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import view.HexEditorView;
import view.components.LinesAndItemsSettingsView;

import java.io.IOException;

public class LinesAndItemsSettingsController {
    private static final Logger logger = LoggerFactory.getLogger(LinesAndItemsSettingsController.class);
    private final LinesAndItemsSettingsView settingsView;
    private final DisplayHelper helper;
    private final HexEditorView view;
    private final HexEditorModel editorModel;


    public LinesAndItemsSettingsController(LinesAndItemsSettingsView settingsView, DisplayHelper helper, HexEditorModel editorModel, HexEditorView view) {
        this.settingsView = settingsView;
        this.helper = helper;
        this.view = view;
        this.editorModel = editorModel;


        settingsView.addItemsPerLineListener(e -> changeItemsPerLine());
        settingsView.addLinesPerPageListener(e -> changeLinesPerPage());
    }

    private void changeItemsPerLine() {
        try {
            int itemsPerLine = settingsView.getItemsPerLineInput();
            editorModel.setItemsPerLine(itemsPerLine);
            helper.displayPage(1);
            view.updateTableStructure();
        } catch (ClassCastException e) {
            view.showErrorDialog("Введите целое число в качестве количества элементов в строке.", "Ошибка");
            logger.error("fail  method changeItemsPerLine", e);
        } catch (IllegalArgumentException | IllegalStateException | IOException e) {
            view.showErrorDialog(e.getMessage(), "Ошибка");
            logger.error("fail  method changeItemsPerLine", e);
        }

    }


    private void changeLinesPerPage() {
        try {
            int linesPerPage = settingsView.getLinesPerPageInput();
            editorModel.setLinesPerPage(linesPerPage);
            helper.displayPage(1);

        } catch (ClassCastException e) {
            view.showErrorDialog("Введите целое число в качестве количества строк.", "Ошибка");
            logger.error("fail  method changeLinesPerPage",e);
        } catch (IllegalArgumentException | IllegalStateException | IOException ex) {
            view.showErrorDialog(ex.getMessage(), "Ошибка");
            logger.error("fail  method changeLinesPerPage",ex);
        }
    }


}
