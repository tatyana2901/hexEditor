import controller.HexEditorController;
import model.HexEditorModel;
import model.HexSearchService;
import model.HexTableModel;
import view.HexEditorView;
import view.components.*;

public class Main {


    public static void main(String[] args) {
        HexEditorModel editorModel = new HexEditorModel();

        //Создание UI - элементов во view
        HexTableModel tableModel = new HexTableModel(editorModel);
        FileSelectionPanel fileSelectionPanel = new FileSelectionPanel();
        PaginationPanel paginationPanel = new PaginationPanel();
        LinesAndItemsSettingsPanel settingsPanel = new LinesAndItemsSettingsPanel();
        BlockBytesMenuBar blockBytesMenuBar = new BlockBytesMenuBar();
        LabelInfoPanel labelInfoPanel = new LabelInfoPanel();
        SearchPanel searchPanel = new SearchPanel();
        HexSearchService searchService = new HexSearchService(editorModel);
        EditPanel editPanel = new EditPanel();
        TableContextMenu tableContextMenu = new TableContextMenu();

        HexEditorView view = new HexEditorView(tableModel, fileSelectionPanel, paginationPanel, settingsPanel,
                blockBytesMenuBar, labelInfoPanel, searchPanel, editPanel, tableContextMenu);

        new HexEditorController(view, editorModel, searchService);


    }
}