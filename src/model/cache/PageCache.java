package model.cache;

import java.util.*;

public class PageCache {

    private byte[] pageData;
    private int pageNumber; //номер страницы
    private boolean isDirty;
    private static Map<Integer, PageCache> cache = new HashMap<>(); //все страницы файла в кэше

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
            //   return index - (pageNumber - 1) * getCachedPageByNumber(pageNumber - 1).length; //здесь откидываем индексы всех предыдущих страниц
            return index - getPageOffset(pageNumber, unChangedPageItemsCount); //здесь откидываем индексы всех предыдущих страниц
        }
        return index;
    }

    public static void setByteByIndex(int pageNumber, int index, byte value, int unChangedPageItemsCount) {

        if (pageNumber > 0 && index >= 0 && unChangedPageItemsCount > 0) {
            byte[] cacheBytes = getCachedPageByNumber(pageNumber);
            int indexOnPage = getIndexOnCurrentPage(pageNumber, index, unChangedPageItemsCount);
            //   System.out.println(cacheBytes.length);
            cacheBytes[indexOnPage] = value;
        }
    }

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

    public static void insertBytesArrayWithShift(int pageNumber, int index, byte[] values, int unChangedPageItemsCount) {

        if (pageNumber > 0 && index >= 0 && unChangedPageItemsCount > 0 && values != null) {
            byte[] originalPage = getCachedPageByNumber(pageNumber);
            int cachedPageSize = originalPage.length;
            byte[] newPage = new byte[cachedPageSize + values.length];
            int indexOnPage = getIndexOnCurrentPage(pageNumber, index, unChangedPageItemsCount);

            // Копируем часть до позиции вставки
            System.arraycopy(originalPage, 0, newPage, 0, indexOnPage);

            // Копируем вставляемый массив
            System.arraycopy(values, 0, newPage, indexOnPage, values.length);

            // Копируем оставшуюся часть исходного массива
            System.arraycopy(originalPage, indexOnPage, newPage,
                    indexOnPage + values.length, originalPage.length - indexOnPage);

            PageCache pageCache = new PageCache(newPage, pageNumber);
            pageCache.isDirty = true;
            cache.put(pageNumber, pageCache);
        }
    }


    public static void deleteBytesWithShift(int pageNumber, int index, int length, int unChangedPageItemsCount) {

        if (pageNumber > 0 && index >= 0 && length > 0 && unChangedPageItemsCount > 0) {

            byte[] originalPage = getCachedPageByNumber(pageNumber);
            int cachedPageSize = originalPage.length;
            byte[] newPage = new byte[cachedPageSize - length];
            int indexOnPage = getIndexOnCurrentPage(pageNumber, index, unChangedPageItemsCount);

            // Копируем часть до позиции вставки
            System.arraycopy(originalPage, 0, newPage, 0, indexOnPage);

            // Копируем оставшуюся часть исходного массива
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
