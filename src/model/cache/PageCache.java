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

    public static byte[] getCachedPageByNumber(int pageNumber) {
        return cache.get(pageNumber).getPageData();
    }

    public static void clearCache() {
        cache.clear();
    }


}
