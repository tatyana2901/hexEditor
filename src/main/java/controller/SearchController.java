package controller;

import model.DataType;
import model.HexEditorModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import service.SearchService;
import view.HexEditorView;
import view.components.SearchView;

import java.io.IOException;

public class SearchController {
    private static final Logger logger = LoggerFactory.getLogger(SearchController.class);
    private final SearchView searchView;
    private final HexEditorModel editorModel;
    private final DisplayHelper helper;
    private final HexEditorView view;
    private final SearchService searchService;

    public SearchController(SearchView searchView, HexEditorModel editorModel, DisplayHelper helper, HexEditorView view) {
        this.searchView = searchView;
        this.editorModel = editorModel;
        this.helper = helper;
        this.view = view;
        this.searchService = new SearchService(editorModel);
        setupSearchListeners();

    }

    private void setupSearchListeners() {
        searchView.addSearchListener(e -> performSearch());
        searchView.addNextSearchListener(e -> getNextSearchItemResult());
        searchView.addPrevSearchListener(e -> getPrevSearchItemResult());
    }

    /**
     * Выполняет поиск байтов по заданному шаблону и маске.
     *
     * @throws IllegalStateException если поиск выполняется не в режиме BYTE
     */
    private void performSearch() {
        try {
            if (editorModel.getType() != DataType.BYTE) {
                view.showErrorDialog("Поиск байт доступен только в режиме отображения BYTE", "Ошибка");
                return;
            }
            searchService.clearSearchResults();
            String pattern = searchView.getSearchPattern();
            String mask = searchView.getSearchMask();
            if (pattern.isEmpty()) {
                return;
            }
            searchService.setMask(mask);
            searchService.setPattern(pattern);
            searchService.searchBytes(1);
            if (searchService.getResultsCount() == 0) {
                view.showInfoDialog("Ничего не найдено", "NoResult");
            } else {
                highlightSearchResult();
                updateSearchStatus();
            }
        } catch (IOException ex) {
            view.showErrorDialog("Ошибка поиска: " + ex.getMessage(), "Ошибка");
            logger.error("fail method performSearch",ex);
        }
    }

    private void highlightSearchResult() {
        try {
            int position = searchService.getCurrentPosition();
            int targetPage = searchService.getCurrentSearchPage();
            helper.displayPage(targetPage);
            if (position >= 0) {
                int[] startCoords = searchService.getTableCoordinatesForPosition(position);
                view.selectTableCell(startCoords[0], startCoords[1]);
            }
        } catch (IOException e) {
            view.showErrorDialog("Ошибка перехода: " + e.getMessage(), "Ошибка");
            logger.error("fail method highlightSearchResult",e);

        }
    }

    private void getNextSearchItemResult() {
        try {
            if (searchService.getCurrentResultIndex() + 1 == searchService.getResultsCount()) {
                if (searchService.getCurrentSearchPage() < editorModel.getTotalPages()) {
                    searchService.searchBytes(searchService.getCurrentSearchPage() + 1);
                }
            } else {
                searchService.increaseCurrentSearchResultIndex();
            }
            displaySearchResultOnPage();

        } catch (IOException e) {
            view.showErrorDialog("Ошибка перехода к следующему результату поиска: " + e.getMessage(), "Ошибка");
            logger.error("fail method getNextSearchItemResult",e);
        }
    }

    private void getPrevSearchItemResult() {
            searchService.decreaseCurrentSearchIndex();
            displaySearchResultOnPage();
    }

    public void displaySearchResultOnPage() {
        highlightSearchResult();
        updateSearchStatus();
    }


    private void updateSearchStatus() {
        if (searchService.getResultsCount() > 0) {
            searchView.setSearchStatus(
                    String.format("Найдено: %d, Текущее: %d",
                            searchService.getResultsCount(), searchService.getCurrentResultIndex() + 1)
            );
        }
    }
}
