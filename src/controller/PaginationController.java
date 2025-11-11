package controller;

import model.HexEditorModel;
import view.HexEditorView;
import view.components.PaginationView;

import java.io.IOException;

public class PaginationController {
    private PaginationView paginationView;
    private HexEditorModel editorModel;
    private DisplayHelper helper;
    private HexEditorView editorView;

    public PaginationController(PaginationView paginationView, HexEditorModel editorModel, DisplayHelper helper, HexEditorView editorView) {
        this.paginationView = paginationView;
        this.editorModel = editorModel;
        this.helper = helper;
        this.editorView = editorView;

        paginationView.addNextPageButtonListener(e -> nextPage());
        paginationView.addPrevPageButtonListener(e -> prevPage());
        paginationView.addLoadPageButtonListener(e -> goToInputNumberPage());

    }

    private void nextPage() {
        try {
            if (editorModel.getCurrentPageNumber() < editorModel.getTotalPages()) {
                helper.displayPage(editorModel.getCurrentPageNumber() + 1);
            }
        } catch (IllegalArgumentException | IllegalStateException | IOException ex) {
            editorView.showErrorDialog(ex.getMessage(), "Ошибка");
        }
    }


    private void prevPage() {
        try {
            if (editorModel.getCurrentPageNumber() > 1) {
                helper.displayPage(editorModel.getCurrentPageNumber() - 1);
            }
        } catch (IllegalArgumentException | IllegalStateException | IOException ex) {
            editorView.showErrorDialog(ex.getMessage(), "Ошибка");
        }
    }


    private void goToInputNumberPage() {
        try {
            int inputPageNumber = paginationView.getPageNumberInput();
            if (inputPageNumber > editorModel.getTotalPages()) {
                editorView.showErrorDialog("Введите номер страницы от 1 до " + editorModel.getTotalPages(), "Ошибка");
                return;
            }
            if (inputPageNumber > 0 || inputPageNumber <= editorModel.getTotalPages()) {
                helper.displayPage(inputPageNumber);
            }
        } catch (NumberFormatException ex) {

            editorView.showErrorDialog("Неправильный формат номера страницы.", "Ошибка");

        } catch (IllegalArgumentException | IllegalStateException | IOException ex) { //так можно делать?
            editorView.showErrorDialog(ex.getMessage(), "Ошибка");
        }

    }
}
