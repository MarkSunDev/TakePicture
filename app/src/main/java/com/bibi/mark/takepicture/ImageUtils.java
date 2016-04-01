package com.bibi.mark.takepicture;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.io.File;

/**
 * 利用Picasso库来获取网络图片
 * 如果不想用Picasso显示图片,可以删除build.gradle中和代码中所有关于Picasso的代码
 * Created by mark on 16/3/30.
 */
public class ImageUtils {
    private Context mContext;
    private static ImageUtils mImageUtils;

    public static enum ImageType{
        IMAGE_TYPE_RECT, IMAGE_TYPE_CRICLE
    }

    public static ImageUtils getInstance(Context context){
        if(mImageUtils == null){
            mImageUtils = new ImageUtils(context);
        }
        return mImageUtils;
    }

    public ImageUtils(Context context) {
        this.mContext = context;
    }

    /**
     * 显示网络图片
     * @param imageView
     * @param url
     * @param width 不清楚可以填0
     * @param height
     */
    public void setImageUrl(ImageView imageView,String url, int width, int height){
        if(mContext == null) return;
        if (url != null && !url.equals("null") && url.length() > 0) {
            if(width == 0 || height == 0){
                Picasso.with(mContext).load(url).placeholder(R.drawable.loading).into(imageView);
            }else{
                Picasso.with(mContext).load(url).placeholder(R.drawable.loading).resize(width, height).centerCrop().into(imageView);
            }
        }
    }

    /**
     * 设置头像
     * @param imageView
     * @param url
     * @param type ImageType.IMAGE_TYPE_RECT 方形 ImageType.IMAGE_TYPE_CRICLE 圆形
     */
    public void setHeadViewUrl(ImageView imageView, String url, ImageType type){
        if(mContext == null) return;
        Transformation transformation;
        if(type == ImageType.IMAGE_TYPE_RECT){
            transformation = new RoundedTransformation(5, 0);
        }else{
            transformation = new CircleTransformation();
        }
        if (url != null && !url.equals("null") && url.length() > 0) {
            Picasso.with(mContext).load(url).fit().placeholder(R.drawable.loading).transform(transformation).into(imageView);
        }else{
            Picasso.with(mContext).load(R.drawable.loading).fit().transform(transformation).into(imageView);
        }
    }

    /**
     * 设置本地图片
     * @param imageView
     * @param filePath
     * @param type
     */
    public void setHeadViewFile(ImageView imageView,String filePath, ImageType type){
        if(mContext == null) return;
        Transformation transformation;
        if(type == ImageType.IMAGE_TYPE_RECT){
            transformation = new RoundedTransformation(5, 0);
        }else{
            transformation = new CircleTransformation();
        }
        Picasso.with(mContext).load(new File(filePath)).fit().transform(transformation).into(imageView);
    }

    class RoundedTransformation implements Transformation {
        private final int radius;
        private final int margin;  // dp

        // radius is corner radii in dp
        // margin is the board in dp
        public RoundedTransformation(final int radius, final int margin) {
            this.radius = radius;
            this.margin = margin;
        }

        @Override
        public Bitmap transform(final Bitmap source) {
            final Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setShader(new BitmapShader(source, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));
            Bitmap.Config config = source.getConfig() != null ? source.getConfig() : Bitmap.Config.ARGB_8888;
            Bitmap output = Bitmap.createBitmap(source.getWidth(), source.getHeight(), config);
            Canvas canvas = new Canvas(output);
            canvas.drawRoundRect(new RectF(margin, margin, source.getWidth() - margin, source.getHeight() - margin), radius, radius, paint);

            if (source != output) {
                source.recycle();
            }
            return output;
        }

        @Override
        public String key() {
            return "rounded";
        }
    }

    public class CircleTransformation implements Transformation {

        @Override
        public Bitmap transform(final Bitmap source) {
            final Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setShader(new BitmapShader(source, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));

            Bitmap output = Bitmap.createBitmap(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(output);
            canvas.drawCircle(source.getWidth() / 2, source.getHeight() / 2, source.getHeight() / 2, paint);

            if (source != output) {
                source.recycle();
            }
            return output;
        }

        @Override
        public String key() {
            return "circle";
        }
    }
}
