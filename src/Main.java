import controller.HexEditorController;
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



        HexEditorView view = new HexEditorView(tableModel, blockBytesMenuBar);

        new HexEditorController(view, editorModel);


    }
}