package model.cache;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class PageCache {

    private byte[] pageData;
    private int pageNumber;
    private boolean isDirty;
    private static final Map<Integer, PageCache> cache = new HashMap<>();

    public PageCache(byte[] pageData, int pageNumber) {
        if (pageNumber > 0 && pageData != null) {
            this.pageData = pageData;
            this.pageNumber = pageNumber;
        }

    }


    public byte[] getPageData() {
        return pageData;
    }

    public static boolean isPageInCache(int pageNumber) {
        return cache.containsKey(pageNumber);
    }

    public static void addCachePage(PageCache page) {
        if (page != null) {
            cache.put(page.pageNumber, page);
        }
    }

    public static boolean isDirtyPageExist() {
        return cache.values().stream().anyMatch(x -> x.isDirty);
    }

    public static byte[] getCachedPageByNumber(int pageNumber) {
        if (pageNumber <= 0) {
            throw new IllegalArgumentException("Номер страницы должен быть положительным.");
        }
        return cache.get(pageNumber).getPageData();
    }
    /**
     * Вычисляет смещение для указанной страницы с учетом измененных страниц в кэше.
     *
     * @param pageNumber номер страницы
     * @param unChangedPageItemsCount количество элементов на неизмененной странице
     * @return смещение в байтах для указанной страницы
     * @throws IllegalArgumentException если номер страницы не положительный
     */
    public static int getPageOffset(int pageNumber, int unChangedPageItemsCount) {

        if (pageNumber <= 0) {
            throw new IllegalArgumentException("Номер страницы должен быть положительным.");
        }
        if (!isDirtyPageExist()) {
            return unChangedPageItemsCount * (pageNumber - 1);
        }
        int changedPagesCount = 0;
        int changedPagesItemsSum = 0;
        for (Map.Entry<Integer, PageCache> entry : cache.entrySet()) {
            if (entry.getKey() < pageNumber && entry.getValue().isDirty) {
                changedPagesItemsSum = changedPagesItemsSum + entry.getValue().getPageData().length;
                changedPagesCount++;
            }
        }
        int unchangedPagesCount = pageNumber - 1 - changedPagesCount;
        return unchangedPagesCount * unChangedPageItemsCount + changedPagesItemsSum;
    }

    private static int getIndexOnCurrentPage(int pageNumber, int index, int unChangedPageItemsCount) {
        if (pageNumber > 1) {
            return index - getPageOffset(pageNumber, unChangedPageItemsCount);
        }
        return index;
    }
    /**
     * Устанавливает значение байта по указанному индексу на странице.
     *
     * @param pageNumber номер страницы
     * @param index индекс байта на странице
     * @param value новое значение байта
     * @param unChangedPageItemsCount количество элементов на неизмененной странице
     */
    public static void setByteByIndex(int pageNumber, int index, byte value, int unChangedPageItemsCount) {

        if (pageNumber > 0 && index >= 0 && unChangedPageItemsCount > 0) {
            byte[] cacheBytes = getCachedPageByNumber(pageNumber);
            int indexOnPage = getIndexOnCurrentPage(pageNumber, index, unChangedPageItemsCount);
            cacheBytes[indexOnPage] = value;
        }
    }
    /**
     * Устанавливает массив байтов на странице начиная с указанного индекса.
     *
     * @param pageNumber номер страницы
     * @param index начальный индекс для вставки
     * @param values массив байтов для вставки
     * @param unChangedPageItemsCount количество элементов на неизмененной странице
     * @throws IllegalArgumentException если вставка выходит за границы страницы
     */
    public static void setBytesArrayByIndex(int pageNumber, int index, byte[] values, int unChangedPageItemsCount) {

        if (pageNumber > 0 && index >= 0 && unChangedPageItemsCount > 0 && values != null) {
            byte[] originalPage = getCachedPageByNumber(pageNumber);
            int indexOnPage = getIndexOnCurrentPage(pageNumber, index, unChangedPageItemsCount);

            int endArrayIndex = indexOnPage + values.length;
            if (endArrayIndex > originalPage.length) {
                throw new IllegalArgumentException("Вставка выходит за границы страницы.");
            }
            byte[] newPage = Arrays.copyOf(originalPage, originalPage.length);
            System.arraycopy(values, 0, newPage, indexOnPage, values.length);
            cache.put(pageNumber, new PageCache(newPage, pageNumber));
        }

    }
    /**
     * Вставляет массив байтов на страницу со сдвигом существующих данных.
     *
     * @param pageNumber номер страницы
     * @param index индекс для вставки
     * @param values массив байтов для вставки
     * @param unChangedPageItemsCount количество элементов на неизмененной странице
     */
    public static void insertBytesArrayWithShift(int pageNumber, int index, byte[] values, int unChangedPageItemsCount) {

        if (pageNumber > 0 && index >= 0 && unChangedPageItemsCount > 0 && values != null) {
            byte[] originalPage = getCachedPageByNumber(pageNumber);
            int cachedPageSize = originalPage.length;
            byte[] newPage = new byte[cachedPageSize + values.length];
            int indexOnPage = getIndexOnCurrentPage(pageNumber, index, unChangedPageItemsCount);

            System.arraycopy(originalPage, 0, newPage, 0, indexOnPage);
            System.arraycopy(values, 0, newPage, indexOnPage, values.length);
            System.arraycopy(originalPage, indexOnPage, newPage,
                    indexOnPage + values.length, originalPage.length - indexOnPage);

            PageCache pageCache = new PageCache(newPage, pageNumber);
            pageCache.isDirty = true;
            cache.put(pageNumber, pageCache);
        }
    }

    /**
     * Удаляет байты со страницы со сдвигом оставшихся данных.
     *
     * @param pageNumber номер страницы
     * @param index начальный индекс удаления
     * @param length количество удаляемых байтов
     * @param unChangedPageItemsCount количество элементов на неизмененной странице
     */
    public static void deleteBytesWithShift(int pageNumber, int index, int length, int unChangedPageItemsCount) {

        if (pageNumber > 0 && index >= 0 && length > 0 && unChangedPageItemsCount > 0) {

            byte[] originalPage = getCachedPageByNumber(pageNumber);
            int cachedPageSize = originalPage.length;
            byte[] newPage = new byte[cachedPageSize - length];
            int indexOnPage = getIndexOnCurrentPage(pageNumber, index, unChangedPageItemsCount);


            System.arraycopy(originalPage, 0, newPage, 0, indexOnPage);
            System.arraycopy(originalPage, indexOnPage + length, newPage,
                    indexOnPage, originalPage.length - indexOnPage - length);

            PageCache pageCache = new PageCache(newPage, pageNumber);
            pageCache.isDirty = true;
            cache.put(pageNumber, pageCache);

        }

    }

    public static void clearCache() {
        cache.clear();
    }


}
