package com.hemaapp.wcpc_driver;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;

import xtom.frame.util.XtomImageUtil;

public class ImageUtil extends XtomImageUtil {
	
	/**
	 * 获取圆角图片
	 * 
	 * @param bitmap
	 *            原图片
	 * @param roundPx
	 *            圆角度 值越大越圆，自己掌握
	 * @param width
	 *            想要的宽度
	 * @params height
	 *            想要的高度
	 * @return
	 */
	
	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int width, int height, int roundPx) {
		
		width = bitmap.getWidth();
        height = bitmap.getHeight();
        int left = 0, top = 0, right = width, bottom = height;
        roundPx = height/2;
        if (width > height) {
            left = (width - height)/2;
            top = 0;
            right = left + height;
            bottom = height;
        } else if (height > width) {
            left = 0;
            top = (height - width)/2;
            right = width;
            bottom = top + width;
            roundPx = width/2;
        }

        Bitmap output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        int color = 0xff424242;
        Paint paint = new Paint();
        Rect rect = new Rect(left, top, right, bottom);
        RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
	}

}
