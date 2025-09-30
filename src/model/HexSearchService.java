package model;

import java.io.IOException;
import java.util.*;

public class HexSearchService {

    private HexEditorModel editorModel;
    private List<Integer> searchResults; //список позиций найденных байтов в файле (индексов байта)
    private int currentSearchIndex; // индекс просматриваемого байта в данный момент для отображения в результатах поиска: 1,2,3,4,5


    public HexSearchService(HexEditorModel editorModel) {
        this.editorModel = editorModel;
        this.searchResults = new ArrayList<>();
        this.currentSearchIndex = -1; // номер просматриваемого найденного элемента. Например, 1 из 5 или 2 из 5
    }

    public int getResultsCount() {
        return searchResults.size();
    }

    public int getCurrentSearchIndex() {
        return currentSearchIndex;
    }

    public int getCurrentPosition() {
        return searchResults.get(currentSearchIndex);
    }

    public void increaseCurrentSearchIndex() {

        if (currentSearchIndex != searchResults.size() - 1)
            currentSearchIndex++;
    }

    public void decreaseCurrentSearchIndex() {

        if (currentSearchIndex > 0)
            currentSearchIndex--;
    }


    public Object getValueAtTableCoordinates(int row, int column) {
        if (row < 0 || column <= 0) throw new IllegalArgumentException("Индекс не может быть отрицательным числом.");

        int index = row * editorModel.getItemsPerLine() + (column - 1);
        if (index >= 0 && index < editorModel.getData().size()) {
            return editorModel.getData().get(index);
        }
        System.err.println("Выход за пределы значений индексов таблицы.");
        throw new IllegalArgumentException("Индекс не может быть отрицательным числом.");
    }


    public void searchBytes(String hexPattern, String hexMask) throws IOException {
        byte[] pattern = HexUtils.parseHexBytes(hexPattern);

        byte[] mask = hexMask.isEmpty() ? null : HexUtils.parseHexBytes(hexMask);

        searchResults = editorModel.findBytes(pattern, mask);
        currentSearchIndex = searchResults.isEmpty() ? -1 : 0;

    }


    public int getPageForPosition(int position) {
        int itemsPerPage = editorModel.getItemsPerPage();
        return (position / itemsPerPage) + 1;
    }


    public int[] getTableCoordinatesForPosition(int position) {
        int itemsPerPage = editorModel.getItemsPerPage();
        int localPosition = position % itemsPerPage;
        int row = localPosition / editorModel.getItemsPerLine();
        int col = (localPosition % editorModel.getItemsPerLine()) + 1;
        return new int[]{row, col};
    }


}
