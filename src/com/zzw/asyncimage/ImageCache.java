package com.zzw.asyncimage;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.util.LruCache;
import android.text.TextUtils;
import android.util.Log;


public class ImageCache {

    private static ImageCache imageCacheInstance;
    
    private static LruCache<String, Bitmap> lruCache;
    
    public static ImageCache getInstance(Context context) {
		synchronized (ImageCache.class) {
			if(imageCacheInstance == null) {
				imageCacheInstance = new ImageCache(context);
			}
		} 
		
		return imageCacheInstance;
	}
    
    private ImageCache(Context context) {
    	BitmapFactory.Options options = new BitmapFactory.Options();
		options.inPreferredConfig = Bitmap.Config.RGB_565;
		options.inPurgeable = true;
		options.inInputShareable = true;
    	
    	if (lruCache == null) {
			lruCache = getBitmapCache(context);
		}
	}
    
	public synchronized LruCache<String, Bitmap> getBitmapCache(Context context) {
		if (lruCache == null)
			buildCache(context);
		return lruCache;
	}
	
	private void buildCache(Context context) {
        int memClass = ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass();

        int cacheSize = Math.min(Math.max(4, memClass / 15), 8);

        lruCache = new LruCache<String, Bitmap>(1024 * 1024 * cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {

                return bitmap.getRowBytes() * bitmap.getHeight();//bitmap.getByteCount();
            }
        };
    }
	

    public void addBitmapToCache(String url, Bitmap bitmap) {
		putToCache(url, bitmap);
	}
    
    private void putToCache(String url, Bitmap bitmap) {
    	if (url == null || bitmap == null) {
			return;
		}
    	synchronized (lruCache) {
			lruCache.put(url, bitmap);
		}
    }
    
    private Bitmap getFromCache(String url) {
    	if (!TextUtils.isEmpty(url)) {
	    	synchronized (lruCache) {
				return lruCache.get(url);
			}
    	}
		return null;
    }
    
	public Bitmap getBitmapFromCache(String url) {
		
		return getFromCache(url);
    }
	
    /**
     * Clears the image cache used internally to improve performance. Note that for memory
     * efficiency reasons, the cache will automatically be cleared after a certain inactivity delay.
     */
	private void clearCache() {
		lruCache.evictAll();
	}
    
    public void purgeNow() {
    	clearCache();
    }
}
