package model;

public class HexUtils {

    /**
     * Парсит строку с HEX-данными в массив байтов.
     *
     * @param input строка с HEX-данными (с разделителями или без)
     * @return массив байтов или null при ошибке парсинга
     */
    public static byte[] parseHexBytes(String input) {
        if (input == null || input.trim().isEmpty()) {
            return null;
        }

        try {
            String cleanInput = input.replaceAll("[^0-9A-Fa-f]", "");

            if (cleanInput.isEmpty() || cleanInput.length() % 2 != 0) {
                return null;
            }

            byte[] result = new byte[cleanInput.length() / 2];
            for (int i = 0; i < result.length; i++) {
                String byteStr = cleanInput.substring(i * 2, i * 2 + 2);
                result[i] = (byte) Integer.parseInt(byteStr, 16);
            }
            return result;
        } catch (Exception e) {
            return null;
        }
    }

    public static String bytesToHexString(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            if (i > 0) sb.append(" ");
            sb.append(String.format("%02X", bytes[i]));
        }
        return sb.toString();
    }

}
