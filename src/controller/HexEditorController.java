package controller;

import model.*;
import view.HexEditorView;
import view.components.SearchView;

import java.io.File;
import java.io.IOException;
import java.util.function.BiConsumer;


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

        setupDataTypeListeners();

        view.addSignedItemListener(e -> setSigned(true));
        view.addUnsignedItemListener(e -> setSigned(false));
    }

    private void setupDataTypeListeners() {
        view.setupDataTypeListeners(
                e -> setDataType(DataType.BYTE),
                e -> setDataType(DataType.SHORT),
                e -> setDataType(DataType.INTEGER),
                e -> setDataType(DataType.LONG),
                e -> setDataType(DataType.FLOAT),
                e -> setDataType(DataType.DOUBLE)
        );
    }

    private void setDataType(DataType dataType) {
        try {
            editorModel.setType(dataType);
            boolean isIntegerType = (dataType == DataType.SHORT || dataType == DataType.INTEGER || dataType == DataType.LONG);
            view.setIntegerSignOptionsEnabled(isIntegerType);
            helper.displayPage(1);

        } catch (IllegalArgumentException | IllegalStateException | IOException ex) {
            view.showErrorDialog(ex.getMessage(), "Ошибка");
        }
    }

    private void setSigned(boolean signed) {
        try {
            editorModel.setSigned(signed);
            view.updateTableData();
        } catch (Exception ex) {
            view.showErrorDialog(ex.getMessage(), "Ошибка");
        }
    }


}

@FunctionalInterface
interface EditingOperation {
    void execute() throws IOException;
}