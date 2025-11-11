package controller;

import model.HexEditorModel;
import view.HexEditorView;

import java.io.IOException;

public class DisplayHelper {
    private HexEditorView view;
    private HexEditorModel model;

    public DisplayHelper(HexEditorView view, HexEditorModel model) {
        this.view = view;
        this.model = model;
    }


    public void displayPage(int pageNumber) throws IOException {

        model.loadPageData(pageNumber); //загрузить данные страницы
        model.setCurrentPageNumber(pageNumber); // поменять номер текущей страницы
        view.setPageInfo(model.getCurrentPageNumber(), (int) model.getTotalPages()); //обновить информацию о текущей странице
        view.updateTableData();
    }




}
