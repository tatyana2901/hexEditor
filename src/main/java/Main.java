import controller.HexEditorController;
import model.HexEditorModel;
import model.HexTableModel;
import view.HexEditorView;

public class Main {


    public static void main(String[] args) {
        HexEditorModel editorModel = new HexEditorModel();
        HexTableModel tableModel = new HexTableModel(editorModel);
        HexEditorView view = new HexEditorView(tableModel);
        new HexEditorController(view, editorModel);

    }
}