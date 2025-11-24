package controller;

import model.HexEditorModel;
import service.SelectionService;
import view.HexEditorView;
import view.components.LabelInfoView;

public class SelectionController {
    private final SelectionService selectionService;
    private final LabelInfoView labelInfoView;
    private final HexEditorView view;

    public SelectionController(LabelInfoView labelInfoView, HexEditorView view, HexEditorModel editorModel) {
        this.selectionService = new SelectionService(editorModel);
        this.labelInfoView = labelInfoView;
        this.view = view;

        setupTableSelectionListener();
    }

    private void setupTableSelectionListener() {
        view.addByteSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                onByteSelectionChanged();
            }
        });
    }


    private void onByteSelectionChanged() {
        int selectedRow = view.getSelectedRow();
        int selectedColumn = view.getSelectedColumn();
        if (selectedRow >= 0 && selectedColumn > 0) {
            Object value = selectionService.getValueAtTableCoordinates(selectedRow, selectedColumn);
            if (value instanceof Byte) {
                byte byteValue = (Byte) value;
                labelInfoView.setByteLabelText(byteValue);
            } else {
                labelInfoView.clearByteLabelText();
            }
        } else {
            labelInfoView.clearByteLabelText();
        }
    }


}
