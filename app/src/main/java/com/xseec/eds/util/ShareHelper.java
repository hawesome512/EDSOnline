package com.xseec.eds.util;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.RecyclerView;
import android.util.LruCache;
import android.view.View;
import android.widget.LinearLayout;

import com.xseec.eds.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static android.support.v4.content.ContextCompat.getColor;

public class ShareHelper {

    //nj--截取scrollview界面 2018/11/23
    public static Bitmap getLinearLayoutBitmap(LinearLayout linearLayout){
        int height=0;
        Bitmap bitmap;
        for (int i=0;i<linearLayout.getChildCount();i++){
            height+=linearLayout.getChildAt( i ).getHeight();
        }
        bitmap=Bitmap.createBitmap( linearLayout.getWidth(),height,Bitmap.Config.ARGB_8888 );
        Canvas canvas=new Canvas( bitmap );
        linearLayout.draw( canvas );
        return bitmap;
    }

    public static Bitmap getRecycleViewBitmap(RecyclerView view){
        Bitmap bigBitmap;
        RecyclerView.Adapter adapter=view.getAdapter();
        if (adapter==null){
            return null;
        }
        int size=adapter.getItemCount();
        int height=0;
        Paint paint = new Paint();
        int iHeight = 0;
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 8;
        LruCache<String, Bitmap> bitmaCache = new LruCache<>(cacheSize);
        for (int i = 0; i < size; i++) {
            RecyclerView.ViewHolder holder = adapter.createViewHolder(view, adapter.getItemViewType(i));
            adapter.onBindViewHolder(holder, i);
            holder.itemView.measure(
                    View.MeasureSpec.makeMeasureSpec(view.getWidth(), View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            holder.itemView.layout(0, 0, holder.itemView.getMeasuredWidth(),
                    holder.itemView.getMeasuredHeight());
            holder.itemView.setDrawingCacheEnabled(true);
            holder.itemView.buildDrawingCache();
            Bitmap drawingCache = holder.itemView.getDrawingCache();
            if (drawingCache != null) {

                bitmaCache.put(String.valueOf(i), drawingCache);
            }
            height += holder.itemView.getMeasuredHeight();
        }

        bigBitmap = Bitmap.createBitmap(view.getMeasuredWidth(), height, Bitmap.Config.ARGB_8888);
        Canvas bigCanvas = new Canvas(bigBitmap);
        Drawable lBackground = view.getBackground();
        if (lBackground instanceof ColorDrawable) {
            ColorDrawable lColorDrawable = (ColorDrawable) lBackground;
            int lColor = lColorDrawable.getColor();
            bigCanvas.drawColor(lColor);
        }

        for (int i = 0; i < size; i++) {
            Bitmap bitmap = bitmaCache.get(String.valueOf(i));
            bigCanvas.drawBitmap(bitmap, 0f, iHeight, paint);
            iHeight += bitmap.getHeight();
            bitmap.recycle();
        }
        return bigBitmap;
    }

    //nj--将LinearLayout recyclerView布局整合成Bitmap 2018/11/2
    public static Bitmap getBitmap(LinearLayout linearLayout,RecyclerView recycler){
        Bitmap firstBitmap=getLinearLayoutBitmap( linearLayout );
        Bitmap secondBitmap=getRecycleViewBitmap( recycler );
        Bitmap bitmap;
        int weight=Math.max( firstBitmap.getWidth(),secondBitmap.getWidth());
        int height=firstBitmap.getHeight()+secondBitmap.getHeight();
        bitmap=Bitmap.createBitmap( weight,height,Bitmap.Config.ARGB_8888 );
        Canvas canvas=new Canvas( bitmap );
        Context context=EDSApplication.getContext();
        canvas.drawColor(getColor(context,R.color.colorWhite ) );
        canvas.drawBitmap( firstBitmap,0,0,null );
        canvas.drawBitmap( secondBitmap,0,firstBitmap.getHeight(),null );
        return bitmap;
    }

    //nj--设置报表信息截屏后保存的位置 2018/11/25
    private static File setFileName(){
        String dir=Environment.getExternalStorageDirectory().getAbsolutePath()+"/eds/reportInfo/IMAG/";
        File fileDir=new File( dir );
        String fileName=DateHelper.getNowForId();
        return new File( fileDir+fileName +".PNG");
    }

    //nj--将报表界面截屏保存、分享 2018/11/25
    public static void shareCapture(Context context,Bitmap bitmap){
        File file=setFileName();
        FileOutputStream out;
        try {
            out=new FileOutputStream( file );
            bitmap.compress( Bitmap.CompressFormat.PNG,90,out );
            out.flush();
            out.close();
            Uri uri;
            if (Build.VERSION.SDK_INT>Build.VERSION_CODES.N){
                uri= FileProvider.getUriForFile(context,
                        context.getPackageName()+".PhotoPickerProvider",file);
            }else {
                uri=Uri.fromFile( file );
            }
            Intent intent=new Intent( Intent.ACTION_SEND );
            intent.setType( "image/*" );
            intent.putExtra( Intent.EXTRA_STREAM,uri );
            context.startActivity( Intent.createChooser( intent,null ) );
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
