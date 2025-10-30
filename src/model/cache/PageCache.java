package model.cache;

import java.util.*;

public class PageCache {

    private byte[] pageData;
    private int pageNumber; //номер страницы

    private static Map<Integer, PageCache> cache = new HashMap<>(); //все страницы файла в кэше

    public PageCache(byte[] pageData, int pageNumber) {
        if (pageNumber > 0 && pageData != null) {
            this.pageData = pageData;
            this.pageNumber = pageNumber;
        }

    }

    public int getPageNumber() {
        return pageNumber;
    }

    public byte[] getPageData() {
        return pageData;
    }

    public static Map<Integer, PageCache> getCache() {
        return cache;
    }

    public static boolean isPageInCache(int pageNumber) {
        return cache.containsKey(pageNumber);
    }

    public static void addCachePage(PageCache page) {
        if (page != null) {
            cache.put(page.pageNumber, page);
        }
    }

    private static int getCurrentPageIndex(int pageNumber, int index) {
        if (pageNumber > 1) {
            return index - (pageNumber - 1) * getCachedPageByNumber(pageNumber - 1).length;
        }
        return index;
    }

    public static void setByteByIndex(int pageNumber, int index, byte value) {
        byte[] cacheBytes = getCachedPageByNumber(pageNumber);
        int indexOnPage = index;
        if (index >= cacheBytes.length) {
            indexOnPage = getCurrentPageIndex(pageNumber, index);
        }
     //   System.out.println(cacheBytes.length);
        cacheBytes[indexOnPage] = value;
        //  isDirty = true; // Помечаем страницу как измененную
    }

    public static void setBytesArrayByIndex(int pageNumber, int index, byte[] values) {
        byte[] cacheBytes = getCachedPageByNumber(pageNumber);
        int indexOnPage = index;
        if (index >= cacheBytes.length) {
            indexOnPage = getCurrentPageIndex(pageNumber, index);
        }
        int endArrayIndex = indexOnPage + values.length;
        if (endArrayIndex > cacheBytes.length) {
            throw new IllegalArgumentException("Вставка выходит за границы страницы.");
        }
        byte[] newValuesArray = Arrays.copyOf(cacheBytes, cacheBytes.length);
        System.arraycopy(values, 0, newValuesArray, indexOnPage, values.length);
        cache.put(pageNumber, new PageCache(newValuesArray, pageNumber));
        //  isDirty = true; // Помечаем страницу как измененную
    }


    public static byte[] getCachedPageByNumber(int pageNumber) {
        return cache.get(pageNumber).getPageData();
    }

    public static void clearCache() {
        cache.clear();
    }


}
