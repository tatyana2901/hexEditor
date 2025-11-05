package model;

import java.io.IOException;
import java.util.*;
import java.util.function.Predicate;

public class HexSearchService {

    private HexEditorModel editorModel;
    private Map<Integer, Integer> searchResults; //список позиций найденных байтов в файле (страница кэша - индекс найденного байта на странице)
    private int currentSearchPage; // страница текущего резальтат поиска просматриваемого байта в данный момент для отображения в результатах поиска: 1,2,3,4,5


    public HexSearchService(HexEditorModel editorModel) {
        this.editorModel = editorModel;
        this.searchResults = new HashMap<>();
        this.currentSearchPage = -1; // номер просматриваемого найденного элемента. Например, 1 из 5 или 2 из 5
    }

    public int getResultsCount() {
        return searchResults.size();
    }

    public int getCurrentSearchPage() {
        return currentSearchPage;
    }

    public int getCurrentPosition() {
        return searchResults.get(currentSearchPage);
    }

    public long getCurrentResultIndex() {
        return searchResults.keySet().stream().filter(x -> x <= currentSearchPage).count();
    }

/*    public void increaseCurrentSearchIndex() {

        if (currentSearchIndex != searchResults.size() - 1)
            currentSearchIndex++;
    }

    public void decreaseCurrentSearchIndex() {

        if (currentSearchIndex > 0)
            currentSearchIndex--;
    }*/


    public Object getValueAtTableCoordinates(int row, int column) {
        if (row < 0 || column <= 0) throw new IllegalArgumentException("Индекс не может быть отрицательным числом.");

        int index = row * editorModel.getItemsPerLine() + (column - 1);
        if (index >= 0 && index < editorModel.getData().length) {
            return editorModel.getData()[index];
        }
        return null;
       /* System.err.println("Выход за пределы значений индексов таблицы.");
        throw new IllegalArgumentException("Индекс не может быть отрицательным числом.");*/
    }


    public void searchBytes(String hexPattern, String hexMask) throws IOException {
        byte[] pattern = HexUtils.parseHexBytes(hexPattern);

        byte[] mask = hexMask.isEmpty() ? null : HexUtils.parseHexBytes(hexMask);

        int[] result = editorModel.findBytes(pattern, mask); //индекс байта на странице кэша
        if (result != null) {
            searchResults.put(result[0], result[1]);
            currentSearchPage = result[0];
        }


    }
/*

    //ИСПРАВИТЬ НА КЭШ!!!
    public int getPageForPosition(int position) {
      */
/*  int itemsPerPage = editorModel.getItemsPerUnchangedPage();
        return (position / itemsPerPage) + 1;*//*


        searchResults.get()

    }
*/


    public int[] getTableCoordinatesForPosition(int position) {
        //  int itemsPerPage = editorModel.getItemsPerUnchangedPage();
        // int localPosition = position % itemsPerPage;
        /*int row = localPosition / editorModel.getItemsPerLine();
        int col = (localPosition % editorModel.getItemsPerLine()) + 1;*/
        int row = position / editorModel.getItemsPerLine();
        int col = (position % editorModel.getItemsPerLine()) + 1;
        return new int[]{row, col};

    }


}
