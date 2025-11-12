package controller;

import model.HexEditorModel;
import view.HexEditorView;


public class HexEditorController {
    private HexEditorView view;
    private HexEditorModel editorModel;
    private DisplayHelper helper;


    public HexEditorController(HexEditorView view, HexEditorModel editorModel) {
        this.view = view;
        this.editorModel = editorModel;
        this.helper = new DisplayHelper(editorModel, view);

        new EditingController(view, editorModel, helper, view.getEditingView());
        new SearchController(view.getSearchView(), editorModel, helper, view);
        new FileOpenController(view.getFileOpenView(), editorModel, helper, view);
        new PaginationController(view.getPaginationView(), editorModel, helper, view);
        new LinesAndItemsSettingsController(view.getSettingsView(), helper, editorModel, view);
        new SelectionController(view.getLabelInfoView(), view, editorModel);
        new BlockBytesController(view, editorModel, helper, view.getBlockBytesView());

    }


}
