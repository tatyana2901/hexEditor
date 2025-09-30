package controller;

import model.*;
import view.HexEditorView;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.function.BiConsumer;


public class HexEditorController {
    private HexEditorView view;
    private HexEditorModel editorModel;
    private HexSearchService searchService;
    private HexEditingService editingService;

    public HexEditorController(HexEditorView view, HexEditorModel editorModel, HexSearchService searchService, HexEditingService editingService) {
        this.view = view;
        this.editorModel = editorModel;
        this.searchService = searchService;
        this.editingService = editingService;

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

        view.addEnableEditListener(e -> activateEditMode());
        view.addChangeByteValueListener(e -> changeSingleByteValue());
        view.addInsertOverwriteListener(e -> insertBytes(false));
        view.addInsertShiftListener(e -> insertBytes(true));


        view.addDeleteWithShiftListener(e -> deleteSelectedBytes((startPosition, length) -> {
            try {
                editingService.removeBytesWithShift(startPosition, length);
            } catch (IOException ex) {
                view.showErrorDialog("Ошибка ввода-вывода: " + ex.getMessage(), "Ошибка");
                ex.printStackTrace();
            }
        }));
        view.addDeleteWithZeroListener(e -> deleteSelectedBytes((startPosition, length) -> {
            try {
                editingService.removeBytesWithZero(startPosition, length);
            } catch (IOException ex) {
                view.showErrorDialog("Ошибка ввода-вывода: " + ex.getMessage(), "Ошибка");
                ex.printStackTrace();
            }
        }));
    }


    private void activateEditMode() {
        boolean newEditMode = !view.isEditMode();

        // Проверяем, что тип данных BYTE
        if (newEditMode && editorModel.getType() != DataType.BYTE) {
            view.showErrorDialog("Редактирование доступно только в режиме BYTE", "Ошибка");
            return;
        }

        view.setEditMode(newEditMode);
        view.setTableContextMenuEnabled(newEditMode);
    }

    private boolean validateEditConditions() {
        if (!view.isEditMode()) {
            view.showErrorDialog("Включите режим редактирования", "Ошибка");
            return false;
        }

        if (editorModel.getType() != DataType.BYTE) {
            view.showErrorDialog("Редактирование доступно только в режиме BYTE", "Ошибка");
            return false;
        }

        if (editorModel.getFile() == null) {
            view.showErrorDialog("Файл не открыт", "Ошибка");
            return false;
        }

        return true;
    }

    private Integer getSelectedPosition() {
        int[] selectedRows = view.getSelectedRows();
        int[] selectedColumns = view.getSelectedColumns();

        if (selectedRows.length > 0 && selectedColumns.length > 0) {
            int[] selection = editorModel.getSelectedBytesRange(selectedRows, selectedColumns);
            return selection[0];
        }
        return null;
    }


    private byte[] requestHexBytesFromUser(String title, String message) {
        String bytesInput = JOptionPane.showInputDialog(
                view,
                message,
                title,
                JOptionPane.QUESTION_MESSAGE
        );

        if (bytesInput == null || bytesInput.trim().isEmpty()) {
            return null;
        }

        return HexUtils.parseHexBytes(bytesInput);
    }

    private void executeEditingOperation(Runnable editingOperation, String successMessage) {
        try {
            editingOperation.run();
            displayPage(editorModel.getCurrentPageNumber());
            view.showInfoDialog(successMessage, "Успех");
        } catch (IllegalStateException | IllegalArgumentException ex) {
            view.showErrorDialog("Ошибка: " + ex.getMessage(), "Ошибка");
        } catch (IOException ex) {
            view.showErrorDialog("Ошибка ввода-вывода: " + ex.getMessage(), "Ошибка");
            ex.printStackTrace();
        } catch (Exception ex) {
            view.showErrorDialog("Неизвестная ошибка: " + ex.getMessage(), "Ошибка");
            ex.printStackTrace();
        }
    }

    private void changeSingleByteValue() {
        if (!validateEditConditions()) return;

        // Получаем позицию
        Integer position = getSelectedPosition();
        if (position == null) {
            view.showErrorDialog("Выберите байт для редактирования", "Ошибка");
            return;
        }

        // Запрашиваем новое значение
        byte[] newValueBytes = requestHexBytesFromUser(
                "Изменение байта",
                String.format("Введите новое значение байта (hex, 00-FF):\nПозиция: %d", position)
        );

        if (newValueBytes == null || newValueBytes.length != 1) {
            view.showErrorDialog("Введите ровно 1 байт (2 hex-символа)", "Ошибка");
            return;
        }

        // Подтверждение
        int confirm = JOptionPane.showConfirmDialog(
                view,
                String.format("Подтвердите изменение:\n\nПозиция: %d\nНовое значение: %02X",
                        position, newValueBytes[0]),
                "Подтверждение изменения",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirm != JOptionPane.YES_OPTION) return;

        // Выполняем операцию
        executeEditingOperation(
                () -> {
                    try {
                        editingService.editSingleByte(position, newValueBytes[0]);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                },
                String.format("Байт успешно изменен\nПозиция: %d\nНовое значение: %02X",
                        position, newValueBytes[0])
        );
    }

    private void insertBytes(boolean withShift) {
        if (!validateEditConditions()) return;

        // Получаем позицию
        Integer position = getSelectedPosition();
        if (position == null) {
            return;
        }

        // Запрашиваем байты для вставки
        byte[] bytesToInsert = requestHexBytesFromUser(
                "Вставка байтов",
                "Введите байты для вставки (hex, через пробел или без разделителей):\n" +
                        "Пример: FF A1 3C или FFA13C\n\n" +
                        "Позиция: " + position + "\n" +
                        "Режим: " + (withShift ? "со сдвигом" : "с заменой")
        );

        if (bytesToInsert == null || bytesToInsert.length == 0) {
            view.showErrorDialog("Не удалось распознать байты", "Ошибка");
            return;
        }

        // Подтверждение
        int confirm = JOptionPane.showConfirmDialog(
                view,
                String.format("Подтвердите вставку:\n\n" +
                                "Позиция: %d\n" +
                                "Количество байт: %d\n" +
                                "Режим: %s\n" +
                                "Байты: %s\n\n" +
                                "Это действие нельзя отменить. Будет создана резервная копия файла.",
                        position, bytesToInsert.length,
                        withShift ? "со сдвигом" : "с заменой",
                        HexUtils.bytesToHexString(bytesToInsert)),
                "Подтверждение вставки",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirm != JOptionPane.YES_OPTION) return;

        // Создаем final копии для использования в лямбде
        final int finalPosition = position;
        final byte[] finalBytesToInsert = bytesToInsert;
        final boolean finalWithShift = withShift;

        // Выполняем операцию
        String operationName = withShift ? "вставлены со сдвигом" : "вставлены с заменой";
        executeEditingOperation(
                () -> {
                    if (finalWithShift) {
                        try {
                            editingService.insertBytesWithShift(finalPosition, finalBytesToInsert);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        try {
                            editingService.insertBytesWithOverwrite(finalPosition, finalBytesToInsert);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                },
                String.format("Байты успешно %s\nПозиция: %d\nКоличество: %d байт",
                        operationName, finalPosition, finalBytesToInsert.length)
        );
    }


    private void deleteSelectedBytes(BiConsumer<Integer, Integer> editingFunction) {
        try {
            // 1. Проверяем условия
            if (!view.isEditMode()) {
                view.showErrorDialog("Включите режим редактирования", "Ошибка");
                return;
            }

            if (editorModel.getType() != DataType.BYTE) {
                view.showErrorDialog("Редактирование доступно только в режиме BYTE", "Ошибка");
                return;
            }

            if (editorModel.getFile() == null) {
                view.showErrorDialog("Файл не открыт", "Ошибка");
                return;
            }

            // 2. Получаем выделение из view
            int[] selectedRows = view.getSelectedRows();
            int[] selectedColumns = view.getSelectedColumns();

            // 3. Получаем диапазон из модели
            int[] selection = editorModel.getSelectedBytesRange(
                    selectedRows, selectedColumns);

            int startPosition = selection[0];
            int length = selection[1];

            // 4. Подтверждение действия
            int confirm = view.showConfirmDeleteWithZeroDialog(startPosition, length, selectedRows.length * selectedColumns.length);
            if (confirm != JOptionPane.YES_OPTION) {
                return;
            }

            // 5. Выполняем обнуление
            editingFunction.accept(startPosition, length);

            // 6. Обновляем отображение

            displayPage(editorModel.getCurrentPageNumber());

            // 7. Снимаем выделение
            view.clearSelection();

            // 8. Уведомляем пользователя
            view.showInfoDialog(
                    String.format("Байты успешно удалены\nПозиция: %d, Количество: %d байт",
                            startPosition, length),
                    "Успех"
            );

        } catch (IllegalStateException | IllegalArgumentException ex) {
            view.showErrorDialog("Ошибка: " + ex.getMessage(), "Ошибка");
        } catch (IOException ex) {
            view.showErrorDialog("Ошибка ввода-вывода: " + ex.getMessage(), "Ошибка");
            ex.printStackTrace();
        } catch (Exception ex) {
            view.showErrorDialog("Неизвестная ошибка: " + ex.getMessage(), "Ошибка");
            ex.printStackTrace();
        }

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

