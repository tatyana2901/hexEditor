package controller;

import model.HexEditorModel;
import service.SelectionService;
import view.HexEditorView;
import view.components.LabelInfoView;

public class SelectionController {
    private SelectionService selectionService;
    private LabelInfoView labelInfoView;
    private HexEditorView view;

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
            try {
                Object value = selectionService.getValueAtTableCoordinates(selectedRow, selectedColumn);

                if (value instanceof Byte) {
                    byte byteValue = (Byte) value;
                    labelInfoView.setByteLabelText(byteValue);
                } else {
                    labelInfoView.clearByteLabelText();
                }

            } catch (Exception ex) {
                labelInfoView.clearByteLabelText();
                view.showErrorDialog(ex.getMessage(), "Ошибка");
            }
        } else {
            labelInfoView.clearByteLabelText();
        }
    }


}
