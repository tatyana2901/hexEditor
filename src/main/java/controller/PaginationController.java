package controller;

import model.HexEditorModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import view.HexEditorView;
import view.components.PaginationView;

import java.io.IOException;

public class PaginationController {

    private static final Logger logger = LoggerFactory.getLogger(PaginationController.class);
    private final PaginationView paginationView;
    private final HexEditorModel editorModel;
    private final DisplayHelper helper;
    private final HexEditorView editorView;

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
            logger.error("gail method nextPage", ex);
        }
    }


    private void prevPage() {
        try {
            if (editorModel.getCurrentPageNumber() > 1) {
                helper.displayPage(editorModel.getCurrentPageNumber() - 1);
            }
        } catch (IllegalArgumentException | IllegalStateException | IOException ex) {
            editorView.showErrorDialog(ex.getMessage(), "Ошибка");
            logger.error("gail method prevPage", ex);
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
            logger.error("fail method goToInputNumberPage", ex);

        } catch (IllegalArgumentException | IllegalStateException | IOException ex) { //так можно делать?
            editorView.showErrorDialog(ex.getMessage(), "Ошибка");
            logger.error("fail method goToInputNumberPage", ex);
        }

    }
}
