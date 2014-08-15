package com.zzw.asyncimage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;


public class ImageLoader {
	private static String TAG = ImageLoader.class.getSimpleName();
	private File SDcache;
	private ImageCache memoryCache;
	private Context context;
	
	public ImageLoader(Context context) {
		this.context = context;
		memoryCache = ImageCache.getInstance(context);
	}

	public void download(String url, ImageView imageView, int imageType) {
		if (!isAllowImage()) {
			return;
		}

		Bitmap bitmap = memoryCache.getBitmapFromCache(url);
		if (bitmap == null) {
			asyncloadImage(url, imageView, imageType);
		} else {
			imageView.setImageBitmap(bitmap);
		}
	}
	
	public boolean isAllowImage() {
		return true;
	}
	
	public void asyncloadImage(String path, ImageView imageView, int imageType) {  
        AsyncImageTask task = new AsyncImageTask(imageView);  
        task.execute(path);  
    }  
  
    private final class AsyncImageTask extends AsyncTask<String, Integer, Bitmap> {
  
        private ImageView imageView;  
  
        public AsyncImageTask(ImageView imageView) {  
            this.imageView = imageView;  
        }  
  
        /** 
         * 将在onPreExecute 方法执行后马上执行，该方法运行在后台线程中。 
         * 这里将主要负责执行那些很耗时的后台计算工作。可以调用  
         * publishProgress方法来更新实时的任务进度。该方法是抽象方法， 
         * 子类必须实现。  
         */ 
        @Override  
        protected Bitmap doInBackground(String... params) {  
            try {  
                return getImageURI(params[0]);  
            } catch (Exception e) {  
                e.printStackTrace();  
            }  
            return null;  
        }
  
        // 这个放在ui线程中执行  
        @Override  
        protected void onPostExecute(Bitmap bitmap) {  
            super.onPostExecute(bitmap);   
            // 完成图片的绑定  
            if (imageView != null && bitmap != null) {  
            	imageView.setImageBitmap(bitmap);
            }  
        }  
    }
    
    /**
     * 从网络上获取图片，如果图片在本地存在的话就直接拿，如果不存在再去服务器上下载图片 
     * 拿到的图片存入内存缓存和硬盘缓存
     * @param path 图片的地址
     * @return
     * @throws Exception
     */
	public Bitmap getImageURI(String path) throws Exception {
		
		Bitmap bitemap = null;
		String name = MD5.getMD5(path) +".bin";
		
		// 创建缓存目录，系统一运行就创建缓存目录
		SDcache = new File(Environment.getExternalStorageDirectory(), "cache");
		if (!SDcache.exists()) {
			SDcache.mkdirs();
		}
		
		File file = new File(SDcache, name);
		// 如果图片存在本地缓存目录，则不去服务器下载
		if (file.exists()&&file.length()>0) {
			
			FileInputStream is = new FileInputStream(file);
			bitemap = BitmapFactory.decodeStream(is);
			memoryCache.addBitmapToCache(path, bitemap);
			is.close();
			return bitemap;
		} else {
			// 从网络上获取图片
			URL url = new URL(path);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(5000);
			conn.setRequestMethod("GET");
			conn.setDoInput(true);
			if (conn.getResponseCode() == 200) {

				InputStream is = conn.getInputStream();
				FileOutputStream fos = new FileOutputStream(file);
				byte[] buffer = new byte[4*1024];
				int len = 0;
				while ((len = is.read(buffer)) != -1) {
					fos.write(buffer, 0, len);
				}
				
				bitemap = BitmapFactory.decodeStream(is);
				memoryCache.addBitmapToCache(path, bitemap);
				is.close();
				fos.close();
				// 返回一个URI对象
				return bitemap;
			}
		}
		return null;
	}
}
