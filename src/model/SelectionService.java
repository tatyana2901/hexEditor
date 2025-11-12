package model;

public class SelectionService {

    HexEditorModel editorModel;

    public SelectionService(HexEditorModel editorModel) {
        this.editorModel = editorModel;
    }

    public Object getValueAtTableCoordinates(int row, int column) {
        if (row < 0 || column <= 0) throw new IllegalArgumentException("Индекс не может быть отрицательным числом.");

        int index = row * editorModel.getItemsPerLine() + (column - 1);
        if (index >= 0 && index < editorModel.getData().length) {
            return editorModel.getData()[index];
        }
        return null;
    }
}
