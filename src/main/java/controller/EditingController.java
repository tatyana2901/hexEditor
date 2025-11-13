package controller;

import model.DataType;
import service.EditingService;
import model.HexEditorModel;
import model.HexUtils;
import view.HexEditorView;
import view.components.EditingView;

import java.io.IOException;

public class EditingController {

    private HexEditorView view;
    private HexEditorModel editorModel;
    private EditingService editingService;
    private DisplayHelper helper;
    private EditingView editingView;

    public EditingController(HexEditorView view, HexEditorModel editorModel, DisplayHelper helper, EditingView editingView) {
        this.editingService = new EditingService(editorModel);
        this.view = view;
        this.editorModel = editorModel;
        this.helper = helper;
        this.editingView = editingView;
        editingView.addEnableEditListener(e -> activateEditMode());
        editingView.addChangeByteValueListener(e -> changeSingleByteValue());
        editingView.addInsertOverwriteListener(e -> insertBytes(false));
        editingView.addInsertShiftListener(e -> insertBytes(true));
        editingView.addDeleteWithShiftListener(e -> deleteSelectedBytes(true));
        editingView.addDeleteWithZeroListener(e -> deleteSelectedBytes(false));
    }

    private void activateEditMode() {
        boolean newEditMode = !editingView.isEditMode();


        if (newEditMode && editorModel.getType() != DataType.BYTE) {
            view.showErrorDialog("Редактирование доступно только в режиме BYTE", "Ошибка");
            return;
        }

        editingView.setEditMode(newEditMode);
        editingView.setTableContextMenuEnabled(newEditMode);
    }


    private void executeEditingOperation(EditingOperation editingOperation, String successMessage) {
        try {
            editingOperation.execute();
            helper.displayPage(editorModel.getCurrentPageNumber());
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


    private int[] getSelection() {
        int[] selectedRows = view.getSelectedRows();
        int[] selectedColumns = view.getSelectedColumns();

        if (selectedRows.length > 0 && selectedColumns.length > 0) {
            return editingService.getSelectedBytesRange(selectedRows, selectedColumns);
        }
        return null;

    }

    private boolean validateEditConditions() {
        if (!editingView.isEditMode()) {
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

    /**
     * Изменяет значение одного байта в текущей позиции выделения.
     * Запрашивает у пользователя новое HEX-значение и подтверждение,
     * затем выполняет изменение через сервис редактирования.
     *
     * @throws IllegalStateException если нет активного выделения или данные не загружены
     */
    private void changeSingleByteValue() {
        if (!validateEditConditions()) return;


        int[] selection = getSelection();
        if (selection == null) {
            view.showErrorDialog("Выберите байт для редактирования", "Ошибка");
            return;
        }
        Integer position = selection[0];

        byte[] newValueBytes = requestHexBytesFromUser(
                "Изменение байта",
                String.format("Введите новое значение байта (hex, 00-FF):\nПозиция: %d", position)
        );

        if (newValueBytes == null || newValueBytes.length != 1) {
            view.showErrorDialog("Введите ровно 1 байт (2 hex-символа)", "Ошибка");
            return;
        }


        int confirm = view.showConfirmDialog("Подтверждение изменения", String.format("Подтвердите изменение:\n\nПозиция: %d\nНовое значение: %02X",
                position, newValueBytes[0]));

        if (confirm != view.YES_OPTION) return;

        executeEditingOperation(
                () -> editingService.editSingleByte(position, newValueBytes[0]),
                String.format("Байт успешно изменен\nПозиция: %d\nНовое значение: %02X",
                        position, newValueBytes[0])
        );
    }

    /**
     * Вставляет байты в указанную позицию в одном из двух режимов.
     *
     * @param withShift true - вставка со сдвигом существующих данных,
     *                 false - замена существующих данных новыми байтами
     * @throws IllegalStateException если невозможно выполнить редактирование
     */
    private void insertBytes(boolean withShift) {
        if (!validateEditConditions()) return;


        int[] selection = getSelection();
        if (selection == null) {
            view.showErrorDialog("Выберите позицию для вставки", "Ошибка");
            return;
        }
        Integer position = selection[0];


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


        int confirm = view.showConfirmDialog("Подтверждение вставки", String.format("Подтвердите вставку:\n\n" +
                        "Позиция: %d\n" +
                        "Количество байт: %d\n" +
                        "Режим: %s\n" +
                        "Байты: %s\n\n" +
                        "Это действие нельзя отменить. Будет создана резервная копия файла.",
                position, bytesToInsert.length,
                withShift ? "со сдвигом" : "с заменой",
                HexUtils.bytesToHexString(bytesToInsert))
        );

        if (confirm != view.YES_OPTION) return;


        String operationName = withShift ? "вставлены со сдвигом" : "вставлены с заменой";
        executeEditingOperation(
                () -> {
                    if (withShift) {
                        editingService.insertBytesWithShift(position, bytesToInsert);
                    } else {
                        editingService.insertBytesWithOverwrite(position, bytesToInsert);
                    }
                },
                String.format("Байты успешно вставлены %s\nПозиция: %d\nКоличество: %d байт",
                        operationName, position, bytesToInsert.length)
        );
    }

    /**
     * Удаляет выделенные байты в указанном режиме.
     *
     * @param withShift true - удаление со сдвигом оставшихся данных,
     *                 false - обнуление значений выделенных байтов
     */
    private void deleteSelectedBytes(boolean withShift) {
        if (!validateEditConditions()) return;
        try {
            int[] selection = getSelection();
            if (selection == null) {
                view.showErrorDialog("Выберите байты для удаления", "Ошибка");
                return;
            }
            Integer position = selection[0];
            int length = selection[1];

            int confirm = view.showConfirmDialog("Подтверждение обнуления",
                    String.format("Обнулить выделенные байты?\n\n" +
                                    "Позиция: %d\n" +
                                    "Выделено: %d ячеек\n" +
                                    "Это действие нельзя отменить. Будет создана резервная копия файла.",
                            position, length));

            if (confirm != view.YES_OPTION) return;


            executeEditingOperation(
                    () -> {
                        if (withShift) {
                            editingService.removeBytesWithShift(position, length);
                        } else {
                            editingService.removeBytesWithZero(position, length);
                        }
                    },
                    String.format("Байты успешно удалены \nПозиция: %d, Количество: %d байт",
                            position, length)
            );

            view.clearSelection();

        } catch (IllegalStateException | IllegalArgumentException ex) {
            view.showErrorDialog("Ошибка: " + ex.getMessage(), "Ошибка");
        }

    }

    private byte[] requestHexBytesFromUser(String title, String message) {
        String bytesInput = view.showInputDialog(title, message);

        if (bytesInput == null || bytesInput.trim().isEmpty()) {
            return null;
        }

        return HexUtils.parseHexBytes(bytesInput);
    }
}


@FunctionalInterface
interface EditingOperation {
    void execute() throws IOException;
}