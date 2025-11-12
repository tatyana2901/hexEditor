package controller;

import model.HexEditorModel;
import view.HexEditorView;
import view.components.LinesAndItemsSettingsView;

import java.io.IOException;

public class LinesAndItemsSettingsController {

    private LinesAndItemsSettingsView settingsView;
    private DisplayHelper helper;
    private HexEditorView view;
    private HexEditorModel editorModel;


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
        } catch (IllegalArgumentException | IllegalStateException | IOException ex) {
            view.showErrorDialog(ex.getMessage(), "Ошибка");
        }

    }


    private void changeLinesPerPage() {
        try {
            int linesPerPage = settingsView.getLinesPerPageInput();
            editorModel.setLinesPerPage(linesPerPage);
            helper.displayPage(1);

        } catch (ClassCastException e) {
            view.showErrorDialog("Введите целое число в качестве количества строк.", "Ошибка");
        } catch (IllegalArgumentException | IllegalStateException | IOException ex) {
            view.showErrorDialog(ex.getMessage(), "Ошибка");
        }
    }


}
