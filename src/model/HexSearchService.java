package model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HexSearchService {

    private HexEditorModel editorModel;
    private List<Integer> searchResults; //список позиций найденных байтов в файле
    private int currentSearchIndex; // индекс просматриваемого байта в данный момент

    public HexSearchService(HexEditorModel editorModel) {
        this.editorModel = editorModel;
        this.searchResults = new ArrayList<>();
        this.currentSearchIndex = -1;
    }

    public int getResultsCount() {
        return searchResults.size();
    }

    public int getCurrentSearchIndex() {
        return currentSearchIndex;
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

    private byte[] parseHexPattern(String pattern) {
        String cleanPattern = pattern.replaceAll("[^0-9A-Fa-f]", "");

        if (cleanPattern.isEmpty()) {
            throw new IllegalArgumentException("Пустой шаблон поиска");
        }
        if (cleanPattern.length() % 2 != 0) {
            throw new IllegalArgumentException("Некорректная длина hex-строки");
        }

        byte[] result = new byte[cleanPattern.length() / 2];

        for (int i = 0; i < result.length; i++) {
            String byteStr = cleanPattern.substring(i * 2, i * 2 + 2);
            result[i] = (byte) Integer.parseInt(byteStr, 16);
        }

        return result;
    }


    public List<Integer> searchBytes(String hexPattern, String hexMask) throws IOException {
        byte[] pattern = parseHexPattern(hexPattern);

        byte[] mask = hexMask.isEmpty() ? null : parseHexPattern(hexMask);

        searchResults = editorModel.findBytes(pattern, mask);
        currentSearchIndex = searchResults.isEmpty() ? -1 : 0;

        return new ArrayList<>(searchResults); // возвращаем копию
    }

    public int getCurrentPosition() {
        return currentSearchIndex >= 0 ? searchResults.get(currentSearchIndex) : -1;
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
