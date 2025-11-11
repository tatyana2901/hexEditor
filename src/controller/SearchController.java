package controller;

import model.DataType;
import model.HexEditorModel;
import model.HexSearchService;
import view.HexEditorView;
import view.components.SearchView;

import java.io.IOException;

public class SearchController {

    private SearchView searchView;
    private HexEditorModel editorModel;
    private DisplayHelper helper;
    private HexEditorView view;
    private HexSearchService searchService;

    public SearchController(SearchView searchView, HexEditorModel editorModel, DisplayHelper helper, HexEditorView view) {
        this.searchView = searchView;
        this.editorModel = editorModel;
        this.helper = helper;
        this.view = view;
        this.searchService = new HexSearchService(editorModel);
        setupSearchListeners();

    }

    private void setupSearchListeners() {
        searchView.addSearchListener(e -> performSearch());
        searchView.addNextSearchListener(e -> getNextSearchItemResult());
        searchView.addPrevSearchListener(e -> getPrevSearchItemResult());
    }

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

        } catch (Exception ex) {
            view.showErrorDialog("Ошибка поиска: " + ex.getMessage(), "Ошибка");
        }
    }

    private void highlightSearchResult() {
        try {
            int position = searchService.getCurrentPosition(); //смещение найденного байта
            int targetPage = searchService.getCurrentSearchPage(); //берет из списка значение по индексу (соответствует номерц текущей позиции)
            helper.displayPage(targetPage);
            if (position >= 0) {
                int[] startCoords = searchService.getTableCoordinatesForPosition(position);
                view.selectTableCell(startCoords[0], startCoords[1]);
            }
        } catch (IOException e) {
            view.showErrorDialog("Ошибка перехода: " + e.getMessage(), "Ошибка");

        }
    }

    private void getNextSearchItemResult() {
        try {

            if (searchService.getCurrentResultIndex() + 1 == searchService.getResultsCount()) {
                //запустить новый поиск
                if (searchService.getCurrentSearchPage() < editorModel.getTotalPages()) {
                    searchService.searchBytes(searchService.getCurrentSearchPage() + 1);
                }

            } else {

                searchService.increaseCurrentSearchResultIndex();

            }
            displaySearchResultOnPage();

        } catch (Exception e) {
            view.showErrorDialog("Ошибка перехода к следующему результату поиска: " + e.getMessage(), "Ошибка");
        }


    }

    private void getPrevSearchItemResult() {

        try {
            searchService.decreaseCurrentSearchIndex();
            displaySearchResultOnPage();
        } catch (Exception e) {
            view.showErrorDialog("Ошибка перехода к предыдущему результату поиска:  " + e.getMessage(), "Ошибка");
        }
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
