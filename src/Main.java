import controller.HexEditorController;
import model.HexEditingService;
import model.HexEditorModel;
import model.HexTableModel;
import view.HexEditorView;
import view.components.*;

public class Main {


    public static void main(String[] args) {
        HexEditorModel editorModel = new HexEditorModel();

        //Создание UI - элементов во view
        HexTableModel tableModel = new HexTableModel(editorModel);


        BlockBytesMenuBar blockBytesMenuBar = new BlockBytesMenuBar();
        TableContextMenu tableContextMenu = new TableContextMenu();
        HexEditingService editingService = new HexEditingService(editorModel);


        HexEditorView view = new HexEditorView(tableModel,
                blockBytesMenuBar, tableContextMenu);

        new HexEditorController(view, editorModel, editingService);


    }
}