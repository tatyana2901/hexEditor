package controller;

import model.HexEditorModel;
import view.HexEditorView;


import java.io.IOException;

public class DisplayHelper {
    private HexEditorView view;
    private HexEditorModel model;

    public DisplayHelper(HexEditorModel model, HexEditorView view) {
        this.model = model;
        this.view = view;
    }


    public void displayPage(int pageNumber) throws IOException {
        model.loadPageData(pageNumber);
        model.setCurrentPageNumber(pageNumber);
        view.getPaginationView().setPageInfo(model.getCurrentPageNumber(), (int) model.getTotalPages());
        view.updateTableData();
    }


}
