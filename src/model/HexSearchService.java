package model;

import model.cache.PageCache;

import java.io.IOException;
import java.util.*;

import static model.cache.PageCache.*;

public class HexSearchService {

    private HexEditorModel editorModel;
    private List<Integer> searchResults;
    private int currentSearchPage;
    private int currentSearchResultIndex;
    private String pattern;
    private String mask;

    public HexSearchService(HexEditorModel editorModel) {
        this.editorModel = editorModel;
        this.searchResults = new ArrayList<>();
        this.currentSearchPage = -1;
        this.currentSearchResultIndex = -1;
    }

    public int getResultsCount() {
        return searchResults.size();
    }

    public int getCurrentSearchPage() {
        return currentSearchPage;
    }

    public int getCurrentPosition() {

        return searchResults.get(currentSearchResultIndex);
    }

    public int getCurrentResultIndex() {
        return currentSearchResultIndex;
    }

    public void increaseCurrentSearchResultIndex() {
        if (currentSearchResultIndex < searchResults.size() - 1)
            currentSearchResultIndex++;
    }
    public void decreaseCurrentSearchIndex() {

        if (currentSearchResultIndex > 0)
            currentSearchResultIndex--;
    }

    public void setMask(String mask) {
        this.mask = mask;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }




    public void searchBytes(int pageNumberToStart) throws IOException {

        byte[] pattern = HexUtils.parseHexBytes(this.pattern);
        byte[] mask = this.mask.isEmpty() ? null : HexUtils.parseHexBytes(this.mask);
        findBytes(pattern, mask,pageNumberToStart);

    }

    private void findBytes(byte[] searchPattern, byte[] mask, int pageNumberToStart) throws IOException {
        if (editorModel.getFile() == null) {
            throw new IllegalStateException("Файл не открыт");
        }
        long totalPagesAmount = editorModel.getTotalPages();
        for (int p = pageNumberToStart; p <= totalPagesAmount; p++) {
            int searchResultsCount = getResultsCount();

            if (!isPageInCache(p)) {
                addCachePage(new PageCache(editorModel.readPageData(p), p));
            }
            byte[] pageBytes = getCachedPageByNumber(p);
            long pageSize = pageBytes.length;

            for (int i = 0; i <= pageSize - searchPattern.length; i++) { //i - номер байта на странице

                boolean match = true;
                int copyOfI = i;
                for (int j = 0; j < searchPattern.length; j++) {
                    if (mask != null) {
                        if ((pageBytes[i++] & mask[j]) != (searchPattern[j] & mask[j])) {
                            match = false;
                            i = copyOfI;
                            break;
                        }
                    } else {
                        if (pageBytes[i++] != searchPattern[j]) {
                            match = false;
                            i = copyOfI;
                            break;
                        }
                    }
                }
                if (match) {
                    searchResults.add(copyOfI);
                    currentSearchPage = p;
                }
            }
            if (searchResultsCount < getResultsCount()) {
                currentSearchResultIndex++;
                break;
            }

        }

    }

    public int[] getTableCoordinatesForPosition(int position) {
        int row = position / editorModel.getItemsPerLine();
        int col = (position % editorModel.getItemsPerLine()) + 1;
        return new int[]{row, col};

    }


    public void clearSearchResults() {
        searchResults.clear();
        currentSearchResultIndex = -1;
        currentSearchPage = -1;
        pattern = null;
        mask = null;
    }
    class SearchResult {
        private int pageNumber;
        private int pageOffset;

        public SearchResult(int pageNumber, int pageOffset) {
            this.pageNumber = pageNumber;
            this.pageOffset = pageOffset;
        }
    }


}
