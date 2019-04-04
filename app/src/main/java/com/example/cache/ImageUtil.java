package com.example.cache;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.LruCache;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

public class ImageUtil {
    private static final String TAG = "ImageUtil";
    private static ImageUtil sInstance = new ImageUtil();
    //private static LruCache<String, Bitmap> mCache;
    private static DisplayMetrics dm = new DisplayMetrics();
    private static BigFileCache<String, Bitmap> mCache;
    //private static TwoQCache<String, Bitmap> mCache;
    private final int maxSize = (int) (Runtime.getRuntime().maxMemory()) / 8;//四舍五入,用总内存就没错
    private final int bigFileMaxSize = maxSize/7*5;
    private final int bigFileLowThreshold = 1024*1024;
    private final int bigFileHighThreshold = 3*1024*1024;
    private int missCount = 0;
    private int hitCount = 0;
    private int bitmapSize = 0;
    private long beginTime;
    private long endTime;
    private List<Long> mLoadTimes;

    private ImageUtil() {
        //这个是定义大小
        //int maxSize = 1024*1024*4;
        //注意这个地方及其的容易出错,因为我写成了freeMemory()/4+0.5f,导致大半天都集合都无法存入东西,因为两者不一样大了,而用int maxSize = 1024*1024*4;里面出现效果
        mLoadTimes = new LinkedList<Long>();
        Log.d(TAG, "内存缓存大小" + String.valueOf(maxSize));
//        mCache = new LruCache<String, Bitmap>(maxSize) {
//            @Override
//            protected int sizeOf(String key, Bitmap value) {
//                //这个是对应上面的byte,注意单位对应,如果这里想作为M那么上面就写4,这里就写/1024/1024
//                return value.getByteCount();
//            }
//        };
        mCache = new BigFileCache<String, Bitmap>(maxSize, bigFileMaxSize, bigFileLowThreshold, bigFileHighThreshold) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount();
            }
        };
//        mCache = new TwoQCache<String, Bitmap>(maxSize) {
//            @Override
//            protected int sizeOf(String key, Bitmap value) {
//                return value.getByteCount();
//            }
//        };
    }

    public float getHitRate() {
        return (float)hitCount/(hitCount+missCount);
    }

    public int getBitmapSize() {
        return bitmapSize;
    }

    public Long getTotalTime() {
        Long sum = Long.valueOf(0);
        for (Long i :
                mLoadTimes) {
            sum += i;
        }
        return sum;
    }
    //1.上下文
    private static Context mContext;
    public static ImageUtil with(Context context) {
        mContext = context;
        ((Activity)mContext).getWindowManager().getDefaultDisplay().getMetrics(dm);
        return sInstance;
    }

    //2.路径
    public RequestCreator load(String path) {
        RequestCreator creator = new RequestCreator(path);
        return creator;
    }

    Handler mHandler = new Handler();

    public class RequestCreator {
        String mUrl = null;
        ImageView mIv;

        RequestCreator(String path) {
            mUrl = path;
        }

        //3.控件
        public void into(ImageView iv) {
            mIv = iv;
            load();
        }

        public void prefetch() {
            Bitmap bitmap = mCache.get(mUrl);
            if(bitmap == null) {
                File file = getFileName();
                try {
                    if (file.exists()) {
                        bitmap = BitmapFactory.decodeStream(new FileInputStream(file));
                        if (bitmap != null) {
                            Log.d(TAG, "从磁盘缓存中预取，图片大小：" + String.valueOf(bitmap.getByteCount()));
                            //立马且存到内存
                            saveCeche(bitmap);
                        }
                        else {
                            new Thread(new Runnable() {
                                Bitmap bitmap;
                                @Override
                                public void run() {
                                    try {
                                        HttpURLConnection conn = (HttpURLConnection) new URL(mUrl).openConnection();
                                        conn.setConnectTimeout(4 * 1000);
                                        conn.setReadTimeout(4 * 1000);

                                        InputStream is = conn.getInputStream();
                                        bitmap = BitmapFactory.decodeStream(is);
                                        if (bitmap == null) {
                                            //没有就显示错误图片
                                            showErrorPic();
                                            return;
                                        }
                                        Log.d(TAG, "从网络中预取，图片原始大小：" + String.valueOf(bitmap.getByteCount()));
                                        bitmap = compressScale(bitmap);
                                        //存到磁盘是file格式,所以内部要处理
                                        saveDisk(bitmap);
                                        //存到内存的是纯bitmap格式.
                                        saveCeche(bitmap);
                                        conn.disconnect();
                                        is.close();
                                    } catch (IOException e) {
                                        //异常显示错误图片
                                        showErrorPic();
                                        e.printStackTrace();
                                    }
                                }
                            }).start();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        private void load() {
            /*-从内存里读取图片    HashMap-*/
            beginTime = System.currentTimeMillis();
            if (loadFromCache()) {
                hitCount++;
                return;
            }
            missCount++;
            /*--从磁盘读取"文件"存入磁盘的是文件file-MD5统一名字,在内存中才是图片bitmap-用url当做名字就行,所以从磁盘读取需要转换成图片;--*/
            if (loadFromDisk()) return;

            /*--从网络获取,开启线程,然后子线程显示即可,再存磁盘,存内存--*/
            loadFromNet();
        }

        //从内存读取
        private boolean loadFromCache() {
            Bitmap bitmapCeche = mCache.get(mUrl);
            if (bitmapCeche != null) {
                //Log.d(TAG, "从内存缓存加载，图片大小：" + String.valueOf(bitmapCeche.getByteCount()));
                bitmapSize += bitmapCeche.getByteCount();
                mIv.setImageBitmap(bitmapCeche);
                endTime = System.currentTimeMillis();
                mLoadTimes.add(Long.valueOf(endTime - beginTime));
                return true;
            }
            return false;
        }

        //从磁盘获取
        private boolean loadFromDisk() {

            File file = getFileName();
            try {
                if (file.exists()) {
                    Bitmap bitmapDisk = BitmapFactory.decodeStream(new FileInputStream(file));
                    if (bitmapDisk != null) {
                        //Log.d(TAG, "从磁盘缓存加载，图片大小：" + String.valueOf(bitmapDisk.getByteCount()));
                        bitmapSize += bitmapDisk.getByteCount();
                        mIv.setImageBitmap(bitmapDisk);
                        endTime = System.currentTimeMillis();
                        mLoadTimes.add(Long.valueOf(endTime - beginTime));
                        //立马且存到内存
                        saveCeche(bitmapDisk);
                        return true;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }

        //从网络获取,并存入磁盘和内存
        private void loadFromNet() {
            new Thread(new Runnable() {
                Bitmap bitmap;
                @Override
                public void run() {
                    try {
                        HttpURLConnection conn = (HttpURLConnection) new URL(mUrl).openConnection();
                        conn.setConnectTimeout(4 * 1000);
                        conn.setReadTimeout(4 * 1000);

                        InputStream is = conn.getInputStream();
                        bitmap = BitmapFactory.decodeStream(is);
                        if (bitmap == null) {
                            //没有就显示错误图片
                            showErrorPic();
                            return;
                        }
                        //Log.d(TAG, "从网络中下载，图片原始大小：" + String.valueOf(bitmap.getByteCount()));
                        bitmap = compressScale(bitmap);
                        bitmapSize += bitmap.getByteCount();
                        //Log.d(TAG, "图片压缩后大小：" + String.valueOf(bitmap.getByteCount()));
                        //子线程->主线程显示
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                mIv.setImageBitmap(bitmap);
                            }
                        });
                        //存到磁盘是file格式,所以内部要处理
                        saveDisk(bitmap);
                        //存到内存的是纯bitmap格式.
                        saveCeche(bitmap);
                        conn.disconnect();
                        is.close();
                    } catch (IOException e) {
                        //异常显示错误图片
                        showErrorPic();
                        e.printStackTrace();
                    }
                }
            }).start();
        }

        private Bitmap compressScale(Bitmap image) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            // 判断如果图片大于1M,进行压缩避免在生成图片（BitmapFactory.decodeStream）时溢出
            if (baos.toByteArray().length / 1024 > 1024) {
                baos.reset();// 重置baos即清空baos
                image.compress(Bitmap.CompressFormat.JPEG, 80, baos);// 这里压缩50%，把压缩后的数据存放到baos中
            }
            ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
            BitmapFactory.Options newOpts = new BitmapFactory.Options();
            // 开始读入图片，此时把options.inJustDecodeBounds 设回true了
            newOpts.inJustDecodeBounds = true;
            Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
            newOpts.inJustDecodeBounds = false;
            newOpts.inSampleSize = calculateInSampleSize(newOpts);
            isBm = new ByteArrayInputStream(baos.toByteArray());
            bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
            return bitmap;
        }
        //磁盘里的file文件名字
        //@NonNull
        private File getFileName() {
            String packageName = mContext.getPackageName();
            File dir = new File("data/data/" + packageName + "/" + "myimage/");//自定义的
            if (!dir.exists()) {
                dir.mkdir();//没有就创建
            }
            String name = MD5Util.encode(mUrl);
            return new File(dir, name);
        }

        //图片存到磁盘
        private void saveDisk(Bitmap bitmap) {
            File fileName = getFileName();
            //bitmap->file,图片转成文件
            try {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(fileName));
                Log.d(TAG, "存储到磁盘");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        //图片存到内存的方法
        private void saveCeche(Bitmap bitmap) {
            mCache.put(mUrl, bitmap);
            if(bitmap.getByteCount() > bigFileLowThreshold && bitmap.getByteCount() < bigFileHighThreshold) {
//                Log.d(TAG, "hhh  大图片存储到内存，当前内存缓存已使用：" + bitmap.getByteCount());
//                Log.d(TAG, "hhh   " + mUrl);
            }
            else if (bitmap.getByteCount() <= bigFileLowThreshold) {
                //Log.d(TAG, "hhh   小图片存储到内存，当前内存缓存已使用：" + mCache.size());
            }
            else {
                //Log.d(TAG, "hhh   超大图片不缓存到内存");
            }
        }

        //显示错误图片
        private void showErrorPic() {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    Bitmap error = BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.ic_launcher);
                    Toast.makeText(mContext, "Error", Toast.LENGTH_SHORT).show();
                    mIv.setImageBitmap(error);
                }
            });
        }
    }

    public static int calculateInSampleSize(BitmapFactory.Options options) {
        // 源图片的高度和宽度
        final int imageHeight = options.outHeight;
        final int imageWidth = options.outWidth;
        int inSampleSize = 1;
        //if (imageHeight > MainActivity.sHeight || imageWidth > MainActivity.sWidth) {
            if (imageWidth > imageHeight) {
                inSampleSize = Math.round((float)imageWidth / dm.widthPixels);
            } else {
                inSampleSize = Math.round((float)imageHeight / dm.heightPixels);
            }
        //}
        return inSampleSize;
    }

}
