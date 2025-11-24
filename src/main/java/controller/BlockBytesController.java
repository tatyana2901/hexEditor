package controller;

import model.DataType;
import model.HexEditorModel;
import view.HexEditorView;
import view.components.BlockBytesView;

import java.io.IOException;

public class BlockBytesController {
    private final HexEditorView view;
    private final HexEditorModel editorModel;
    private final DisplayHelper helper;
    private final BlockBytesView blockBytesView;


    public BlockBytesController(HexEditorView view, HexEditorModel editorModel, DisplayHelper helper, BlockBytesView blockBytesView) {
        this.view = view;
        this.editorModel = editorModel;
        this.helper = helper;
        this.blockBytesView = blockBytesView;

        setupDataTypeListeners();

        blockBytesView.addSignedItemListener(e -> setSigned(true));
        blockBytesView.addUnsignedItemListener(e -> setSigned(false));
    }

    private void setupDataTypeListeners() {
        blockBytesView.setupDataTypeListeners(
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
            blockBytesView.setIntegerSignOptionsEnabled(isIntegerType);
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
