package com.qa.framework.library.httpclient;

import org.apache.http.client.CookieStore;

/**
 * The type Cookie cache.
 */
public class CookieCache {
    private static ThreadLocal<CookieStore> cookieStoreThreadLocal = new ThreadLocal<CookieStore>();

    /**
     * Set.
     *
     * @param cookie the cookie
     */
    public static void set(CookieStore cookie) {
        cookieStoreThreadLocal.set(cookie);
    }

    /**
     * Get cookie store.
     *
     * @return the cookie store
     */
    public static CookieStore get() {
        return cookieStoreThreadLocal.get();
    }

    /**
     * Clear.
     */
    public static void clear() {
        set(null);
    }

}
