package controller;

import model.DataType;
import model.HexEditorModel;
import model.HexSearchService;
import view.HexEditorView;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class HexEditorController {
    private HexEditorView view;
    private HexEditorModel editorModel;
    private HexSearchService searchService;


    public HexEditorController(HexEditorView view, HexEditorModel editorModel, HexSearchService searchService) {
        this.view = view;
        this.editorModel = editorModel;
        this.searchService = searchService;

        view.addOpenFileListener(e -> openFile());
        view.addNextPageButtonListener(e -> nextPage());
        view.addPrevPageButtonListener(e -> prevPage());
        view.addLoadPageButtonListener(e -> goToInputNumberPage());
        view.addItemsPerLineSpinnerListener(e -> changeItemsPerLine());
        view.addLinesPerPageSpinnerListener(e -> changeLinesPerPage());


        setupDataTypeListeners();

        //Слушатели для переключателей опций знаковости
        view.addSignedItemListener(e -> setSigned(true));
        view.addUnsignedItemListener(e -> setSigned(false));

        setupTableSelectionListener();
        setupSearchListeners();
    }


    private void displayPage(int pageNumber) throws IOException {

        editorModel.loadPageData(pageNumber); //загрузить данные страницы
        editorModel.setCurrentPageNumber(pageNumber); // поменять номер текущей страницы
        view.setPageInfo(editorModel.getCurrentPageNumber(), (int) editorModel.getTotalPages()); //обновить информацию о текущей странице
        view.updateTableData();
    }


    private void openFile() {

        File file = view.showOpenFileDialog();
        if (file != null) {
            try {
                editorModel.initializeModel(file);
                displayPage(1);
                view.setFileInfo("Выбран файл: " + file.getName());

            } catch (IllegalArgumentException | IllegalStateException | IOException ex) {
                view.showErrorDialog("Ошибка при чтении файла: " + ex.getMessage(), "Ошибка");
            }
        }
    }

    private void nextPage() {
        try {
            if (editorModel.getCurrentPageNumber() < editorModel.getTotalPages()) {
                displayPage(editorModel.getCurrentPageNumber() + 1);
            }
        } catch (IllegalArgumentException | IllegalStateException | IOException ex) {
            view.showErrorDialog(ex.getMessage(), "Ошибка");
        }
    }


    private void prevPage() {
        try {
            if (editorModel.getCurrentPageNumber() > 1) {
                displayPage(editorModel.getCurrentPageNumber() - 1);
            }
        } catch (IllegalArgumentException | IllegalStateException | IOException ex) {
            view.showErrorDialog(ex.getMessage(), "Ошибка");
        }
    }


    private void goToInputNumberPage() {
        try {
            int inputPageNumber = view.getPageNumberInput();
            if (inputPageNumber > editorModel.getTotalPages()) {
                view.showErrorDialog("Введите номер страницы от 1 до " + editorModel.getTotalPages(), "Ошибка");
                return;
            }
            if (inputPageNumber > 0 || inputPageNumber <= editorModel.getTotalPages()) {
                displayPage(inputPageNumber);
            }
        } catch (NumberFormatException ex) {

            view.showErrorDialog("Неправильный формат номера страницы.", "Ошибка");

        } catch (IllegalArgumentException | IllegalStateException | IOException ex) { //так можно делать?
            view.showErrorDialog(ex.getMessage(), "Ошибка");
        }

    }


    private void changeItemsPerLine() {
        try {
            int itemsPerLine = view.getItemsPerLineInput();
            editorModel.setItemsPerLine(itemsPerLine);
            displayPage(1);
            view.updateTableStructure();
        } catch (ClassCastException e) {
            view.showErrorDialog("Введите целое число в качестве количества элементов в строке.", "Ошибка");
        } catch (IllegalArgumentException | IllegalStateException | IOException ex) {
            view.showErrorDialog(ex.getMessage(), "Ошибка");
        }

    }


    private void changeLinesPerPage() {
        try {
            int linesPerPage = view.getLinesPerPageInput();
            editorModel.setLinesPerPage(linesPerPage);
            displayPage(1);

        } catch (ClassCastException e) {
            view.showErrorDialog("Введите целое число в качестве количества строк.", "Ошибка");
        } catch (IllegalArgumentException | IllegalStateException | IOException ex) {
            view.showErrorDialog(ex.getMessage(), "Ошибка");
        }
    }

    private void setupDataTypeListeners() {
        view.setupDataTypeListeners(
                e -> setDataType(DataType.BYTE),
                e -> setDataType(DataType.SHORT),
                e -> setDataType(DataType.INTEGER),
                e -> setDataType(DataType.LONG),
                e -> setDataType(DataType.FLOAT),
                e -> setDataType(DataType.DOUBLE)
        );
    }

    private void setDataType(DataType dataType) {
        try {
            editorModel.setType(dataType);
            // Включаем/выключаем опции "со знаком/без знака" в зависимости от типа данных
            boolean isIntegerType = (dataType == DataType.SHORT || dataType == DataType.INTEGER || dataType == DataType.LONG);
            view.setIntegerSignOptionsEnabled(isIntegerType);
            displayPage(1);

        } catch (IllegalArgumentException | IllegalStateException | IOException ex) {
            view.showErrorDialog(ex.getMessage(), "Ошибка");
        }
    }

    private void setSigned(boolean signed) {
        try {
            editorModel.setSigned(signed);
            view.updateTableData();
        } catch (Exception ex) {
            view.showErrorDialog(ex.getMessage(), "Ошибка");
        }
    }


    private void setupTableSelectionListener() {
        view.addByteSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                onByteSelectionChanged();
            }
        });
    }

    private void onByteSelectionChanged() {
        int selectedRow = view.getSelectedRow();
        int selectedColumn = view.getSelectedColumn();

        if (selectedRow >= 0 && selectedColumn > 0) {
            try {
                Object value = searchService.getValueAtTableCoordinates(selectedRow, selectedColumn);

                if (value instanceof Byte) {
                    byte byteValue = (Byte) value;
                    view.setByteLabelText(byteValue);
                } else {
                    view.clearByteLabelText();
                }

            } catch (Exception ex) {
                view.clearByteLabelText();
                view.showErrorDialog(ex.getMessage(), "Ошибка");
            }
        } else {
            view.clearByteLabelText();
        }
    }

    private void setupSearchListeners() {
        view.addSearchListener(e -> performSearch());
        view.addNextSearchResultListener(e -> getNextSearchItemResult());
        view.addPrevSearchResultListener(e -> getPrevSearchItemResult());
    }

    private void performSearch() {
        try {

            if (editorModel.getType() != DataType.BYTE) {

                view.showErrorDialog("Поиск байт доступен только в режиме отображения BYTE", "Ошибка");
                return;
            }
            String pattern = view.getSearchPattern();
            String mask = view.getSearchMask();

            if (pattern.isEmpty()) {
                return;
            }

            searchService.searchBytes(pattern, mask);
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
            int position = searchService.getCurrentPosition();
            int targetPage = searchService.getPageForPosition(position);
            displayPage(targetPage);
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
            searchService.increaseCurrentSearchIndex();
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
            view.setSearchStatus(
                    String.format("Найдено: %d, Текущее: %d",
                            searchService.getResultsCount(), searchService.getCurrentSearchIndex() + 1)
            );
        }
    }
}

