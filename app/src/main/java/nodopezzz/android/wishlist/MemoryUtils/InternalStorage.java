package nodopezzz.android.wishlist.MemoryUtils;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import nodopezzz.android.wishlist.Network.UrlDownloader;
import nodopezzz.android.wishlist.R;

public class InternalStorage {

    private static class ContainerForLoading{
        private String name;
        private String imageUrl;
        private String source;

        private ContainerForLoading(String name, String imageUrl, String source) {
            this.name = name;
            this.imageUrl = imageUrl;
            this.source = source;
        }

        public String getName() {
            return name;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public String getSource() {
            return source;
        }
    }

    private Context mContext;

    public InternalStorage(Context context){
        mContext = context;
    }

    public void saveToInternalStorage(String name, String imageUrl, String source) throws OutOfMemoryError{
        Bitmap bitmapImage = ((BitmapDrawable)(mContext.getResources().getDrawable(R.drawable.placeholder_image_not_found))).getBitmap();
        saveToInternalStorage(name, bitmapImage, source);
        new LoadImage().execute(new ContainerForLoading(name, imageUrl, source));
    }

    public void saveToInternalStorage(String name, Bitmap bitmapImage, String source){
        ContextWrapper cw = new ContextWrapper(mContext);
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        File mypath = new File(directory,"IMG_" + source + "_" + name + ".jpg");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public Bitmap loadImage(String name, String source) throws OutOfMemoryError{
        ContextWrapper cw = new ContextWrapper(mContext);
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        File f = new File(directory, "IMG_" + source + "_" + name + ".jpg");
        try {
            return BitmapFactory.decodeStream(new FileInputStream(f));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean deleteImage(String name, String source){
        ContextWrapper cw = new ContextWrapper(mContext);
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        File f = new File(directory,"IMG_" + source + "_" + name + ".jpg");
        return f.delete();
    }

    private class LoadImage extends AsyncTask<ContainerForLoading,Void, Void>{

        @Override
        protected Void doInBackground(ContainerForLoading... containerForLoadings) {

            try {
                byte[] bytes = new byte[0];
                bytes = UrlDownloader.getResponseByte(containerForLoadings[0].getImageUrl());
                final Bitmap bitmap;
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);

                int width = (int) DimensionsCalculator.calculateDipToPx(mContext, 120f);
                int height = (int) DimensionsCalculator.calculateDipToPx(mContext, 180f);

                options.inSampleSize = ImageSizeCalculator.calculateInSampleSize(options, width, height);
                options.inJustDecodeBounds = false;
                bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
                Log.i("ImageSizeCalculator", "size (mb): " + bitmap.getByteCount() / 1024f / 1024f);
                saveToInternalStorage(
                        containerForLoadings[0].getName(),
                        bitmap,
                        containerForLoadings[0].getSource());
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
    }

}
