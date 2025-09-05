import controller.HexEditorController;
import model.HexEditorModel;
import model.HexTableModel;
import view.HexEditorView;
import view.components.BlockBytesMenuBar;
import view.components.FileSelectionPanel;
import view.components.LinesAndItemsSettingsPanel;
import view.components.PaginationPanel;

public class Main {


    public static void main(String[] args) {
        HexEditorModel editorModel = new HexEditorModel();

        //Создание UI - элементов во view
        HexTableModel tableModel = new HexTableModel(editorModel);
        FileSelectionPanel fileSelectionPanel = new FileSelectionPanel();
        PaginationPanel paginationPanel = new PaginationPanel();
        LinesAndItemsSettingsPanel settingsPanel = new LinesAndItemsSettingsPanel();
        BlockBytesMenuBar blockBytesMenuBar = new BlockBytesMenuBar();

        HexEditorView view = new HexEditorView(tableModel, fileSelectionPanel, paginationPanel, settingsPanel, blockBytesMenuBar);

        new HexEditorController(view, editorModel);


    }
}