package controller;

import model.HexEditorModel;
import view.HexEditorView;


public class HexEditorController {


    public HexEditorController(HexEditorView view, HexEditorModel editorModel) {
        DisplayHelper helper = new DisplayHelper(editorModel, view);

        new EditingController(view, editorModel, helper, view.getEditingView());
        new SearchController(view.getSearchView(), editorModel, helper, view);
        new FileOpenController(view.getFileOpenView(), editorModel, helper, view);
        new PaginationController(view.getPaginationView(), editorModel, helper, view);
        new LinesAndItemsSettingsController(view.getSettingsView(), helper, editorModel, view);
        new SelectionController(view.getLabelInfoView(), view, editorModel);
        new BlockBytesController(view, editorModel, helper, view.getBlockBytesView());
        new FileSavingController(editorModel, view.getFileSaveView());
    }


}
