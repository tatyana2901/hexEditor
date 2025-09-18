package controller;

import model.DataType;
import model.HexEditorModel;
import model.HexSearchService;
import view.HexEditorView;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
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
        view.addLoadPageButtonListener(e -> loadPage());
        view.addItemsPerLineSpinnerListener(e -> changeItemsPerLine());
        view.addLinesPerPageSpinnerListener(e -> changeLinesPerPage());


        setupDataTypeListeners();

        //Слушатели для переключателей опций знаковости
        view.addSignedItemListener(e -> setSigned(true));
        view.addUnsignedItemListener(e -> setSigned(false));

        setupTableSelectionListener();
        setupSearchListeners();
    }


    private void openFile() {
        JFileChooser fileChooser = view.getFileChooser();//получаем файл из view

        int ret = fileChooser.showDialog(null, "Открыть файл");
        if (ret == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();

            try {
                editorModel.initializeModel(file);
                editorModel.displayPage(1);
                view.setPageInfo(1, (int) editorModel.getTotalPages());
                view.getLabel().setText("Выбран файл: " + file.getName());
                view.updateTableData(); // Уведомляем таблицу об изменении данных
            } catch (IllegalArgumentException | IllegalStateException | IOException ex) {
                JOptionPane.showMessageDialog(view, "Ошибка при чтении файла: " + ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        }


    }

    //обавить обработку исключений выбрачываемых displaypage итд
    private void nextPage() {
        try {
            if (editorModel.getCurrentPageNumber() < editorModel.getTotalPages()) {
                editorModel.displayPage(editorModel.getCurrentPageNumber() + 1);
                view.setPageInfo(editorModel.getCurrentPageNumber(), (int) editorModel.getTotalPages());
                view.updateTableData();
            }
        } catch (IllegalArgumentException | IllegalStateException | IOException ex) {
            JOptionPane.showMessageDialog(view, ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }


    private void prevPage() {
        try {
            if (editorModel.getCurrentPageNumber() > 1) {
                editorModel.displayPage(editorModel.getCurrentPageNumber() - 1);
                view.setPageInfo(editorModel.getCurrentPageNumber(), (int) editorModel.getTotalPages());
                view.updateTableData();
            }
        } catch (IllegalArgumentException | IllegalStateException | IOException ex) {
            JOptionPane.showMessageDialog(view, ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }


    private void loadPage() {
        try {
            int inputPageNumber = Integer.parseInt(view.getPageNumber().getText());
            if (inputPageNumber > editorModel.getTotalPages()) {
                JOptionPane.showMessageDialog(view, "Введите номер страницы от 1 до " + editorModel.getTotalPages(), "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (inputPageNumber > 0 || inputPageNumber <= editorModel.getTotalPages()) {
                editorModel.displayPage(inputPageNumber);
                view.setPageInfo(editorModel.getCurrentPageNumber(), (int) editorModel.getTotalPages());
                view.updateTableData();
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(view, "Неправильный формат номера страницы.", "Ошибка", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException | IllegalStateException | IOException ex) { //так можно делать?
            JOptionPane.showMessageDialog(view, ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
        }


    }


    private void changeItemsPerLine() {
        try {
            int itemsPerLine = (int) view.getItemsPerLine().getValue();
            editorModel.setItemsPerLine(itemsPerLine);
            editorModel.displayPage(1);
            view.setPageInfo(editorModel.getCurrentPageNumber(), (int) editorModel.getTotalPages());
            view.updateTableStructure();
        } catch (ClassCastException e) {
            JOptionPane.showMessageDialog(view, "Введите целое число в качестве количества элементов в строке.", "Ошибка", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException | IllegalStateException | IOException ex) {
            JOptionPane.showMessageDialog(view, ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
        }

    }


    private void changeLinesPerPage() {
        try {
            int linesPerPage = (int) view.getLinesPerPage().getValue();
            editorModel.setLinesPerPage(linesPerPage);
            editorModel.displayPage(1);
            view.setPageInfo(editorModel.getCurrentPageNumber(), (int) editorModel.getTotalPages());
            view.updateTableData();
        } catch (ClassCastException e) {
            JOptionPane.showMessageDialog(view, "Введите целое число в качестве количества строк.", "Ошибка", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException | IllegalStateException | IOException ex) {
            JOptionPane.showMessageDialog(view, ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void setupDataTypeListeners() {

        Map<JMenuItem, DataType> typeMap = new HashMap<JMenuItem, DataType>() {{

            put(view.getByteMenuItem(), DataType.BYTE);
            put(view.getShortMenuItem(), DataType.SHORT);
            put(view.getIntMenuItem(), DataType.INTEGER);
            put(view.getLongMenuItem(), DataType.LONG);
            put(view.getDoubleMenuItem(), DataType.DOUBLE);
            put(view.getFloatMenuItem(), DataType.FLOAT);

        }};


        ActionListener typeListener = e -> {
            DataType type = typeMap.get((JMenuItem) e.getSource());
            if (type != null) {
                setDataType(type);
            }
        };

        typeMap.keySet().forEach(item -> item.addActionListener(typeListener));
    }

    private void setDataType(DataType dataType) {
        try {
            editorModel.setType(dataType);
            // Включаем/выключаем опции "со знаком/без знака" в зависимости от типа данных
            boolean isIntegerType = (dataType == DataType.SHORT || dataType == DataType.INTEGER || dataType == DataType.LONG);
            view.setIntegerSignOptionsEnabled(isIntegerType);
            editorModel.displayPage(1);
            view.setPageInfo(editorModel.getCurrentPageNumber(), (int) editorModel.getTotalPages());
            view.updateTableData();
        } catch (IllegalArgumentException | IllegalStateException | IOException ex) {
            JOptionPane.showMessageDialog(view, ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void setSigned(boolean signed) {
        try {
            editorModel.setSigned(signed);
            view.updateTableData();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(view, ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
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
                    view.getLabelInfoPanel().setByteValue(byteValue); //КОНТРОЛЛЕР НЕ ДОЛЖЕН ЗНАТЬ О labelInfoPanel - заменить
                } else {
                    view.getLabelInfoPanel().clear();
                }

            } catch (Exception ex) {
                view.getLabelInfoPanel().clear();
                JOptionPane.showMessageDialog(view, "Ошибка: " + ex.getMessage(),
                        "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            view.getLabelInfoPanel().clear();
        }
    }

    private void setupSearchListeners() {
        view.addSearchListener(e -> performSearch());
        //еще два слушателя


    }

    private void performSearch() {
        try {
            String pattern = view.getSearchPattern().getText();
            String mask = view.getSearchMask().getText();

            if (pattern.isEmpty()) {
                return;
            }

            Map<Integer,Integer> results = searchService.searchBytes(pattern, mask);

            if (results.isEmpty()) {
                JOptionPane.showMessageDialog(view, "Ничего не найдено");
            } else {
                highlightSearchResult(searchService.getCurrentPosition());
                updateSearchStatus();
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(view, "Ошибка поиска: " + ex.getMessage());
        }
    }

    private void highlightSearchResult(int position) {

        try {
            int targetPage = searchService.getPageForPosition(position);
            editorModel.displayPage(targetPage);
            view.updateTableData();

            int startPos = searchService.getCurrentSearchIndex();
            int length = searchService.getCurrentSearchResultLength(); //как определить длину текущего найденного индекса

            if (startPos >= 0 && length > 0) {
                highlightByteRange(startPos, length);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(view, "Ошибка перехода: " + e.getMessage());
        }
    }

    private void highlightByteRange(int startPosition, int length) {
     //   view.clearSelection();

        for (int i = 0; i < length; i++) {
            int currentPos = startPosition + i;
            int[] coords = searchService.getTableCoordinatesForPosition(currentPos);
            view.addToSelection(coords[0], coords[1]);

            if (i == 0) {
                view.scrollToVisible(coords[0], coords[1]);
            }
        }
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

