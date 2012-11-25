package com.example.silentcamera;

import java.io.FileOutputStream;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

public class CameraView  extends SurfaceView implements SurfaceHolder.Callback,Camera.PreviewCallback,Camera.AutoFocusCallback {
    private SurfaceHolder holder;
    protected Camera myCamera;
    
	/**
     * Constructor
     */
    CameraView(Context context) {
        super(context);
        holder = getHolder();
        holder.addCallback(this);
        holder.setType(SurfaceHolder.SURFACE_TYPE_NORMAL);
    }
 
    /**
     * Original methods
     */
    public void takePhoto(){
        Toast.makeText(getContext(), "保存中", Toast.LENGTH_SHORT).show();
    	myCamera.autoFocus(this);
    }
    private void savePhotoBitmap(){
    	try {
    		FileOutputStream fos = new FileOutputStream((BitmapHolder._pathBitmap = "/sdcard/SilentCamera/" + String.valueOf(new Date().getTime()) + ".jpg"));
			BitmapHolder._holdedBitmap.compress(CompressFormat.JPEG, 100, fos);
			// 保存処理終了
			fos.close();
		} catch (Exception e) {
			Log.e("Error", "" + e.toString());
		}
    }
    private final void decodeYUV420SP(int[] rgb, byte[] yuv420sp, int width, int height) {
    	final int frameSize = width * height;
    	for (int j = 0, yp = 0; j < height; j++) {
    		int uvp = frameSize + (j >> 1) * width, u = 0, v = 0;
    		for (int i = 0; i < width; i++, yp++) {
    				int y = (0xff & ((int) yuv420sp[yp])) - 16;
    				if (y < 0) y = 0;
    				if ((i & 1) == 0) {
    					try{
	    					v = (0xff & yuv420sp[uvp++]) - 128;
	    					u = (0xff & yuv420sp[uvp++]) - 128;
    					}catch(Exception e){
    						//Log.d("uvp",e.getMessage());
    					}
    				}
    				int y1192 = 1192 * y;
    				int r = (y1192 + 1634 * v);
    				int g = (y1192 - 833 * v - 400 * u);
    				int b = (y1192 + 2066 * u);
    				if (r < 0) r = 0; else if (r > 262143) r = 262143;
    				if (g < 0) g = 0; else if (g > 262143) g = 262143;
    				if (b < 0) b = 0; else if (b > 262143) b = 262143;
    				rgb[yp] = 0xff000000 | ((r << 6) & 0xff0000) | ((g >> 2) & 0xff00) | ((b >> 10) & 0xff);
    		}
    	}
    }   
    
    /**
     * Override methods
     */
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
		myCamera = Camera.open();
    	try { 
    		myCamera.setPreviewDisplay(holder);
        } 
        catch (Exception e) { e.printStackTrace(); }
    }
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Camera.Parameters params = myCamera.getParameters();
        
        List<Integer> bestFormat = params.getSupportedPreviewFormats();
        params.setPreviewFormat(bestFormat.get(bestFormat.size()-1));
        
        List<Size> bestSize = params.getSupportedPreviewSizes();
        params.setPreviewSize(bestSize.get(bestSize.size()-1).width, bestSize.get(bestSize.size()-1).height);
        
        myCamera.setParameters(params);
        myCamera.startPreview();
    }
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        myCamera.stopPreview();
        myCamera.setPreviewCallback(null); 
        myCamera.release();
    }
    @Override
	public void onPreviewFrame(byte[] data, Camera camera) {
		final int width = getWidth(); // プレビューの幅
		final int height = getHeight(); // プレビューの高さ
		int[] rgb = new int[(width * height)]; // ARGB8888の画素の配列
		
		BitmapHolder._holdedBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888); //ARGB8888で空のビットマップ作成
		decodeYUV420SP(rgb, data, width, height); // 変換
		BitmapHolder._holdedBitmap.setPixels(rgb, 0, width, 0, 0, width, height); // 変換した画素からビットマップにセット
		
		savePhotoBitmap();
		
        Toast.makeText(getContext(), "保存完了", Toast.LENGTH_SHORT).show();
		myCamera.setPreviewCallback(null);
		new Thread() {
            @Override
            public void run() {
                try {
					Thread.sleep(1000);
					SilentCamera.btn_flag = true;
				} catch (InterruptedException e) {
					e.printStackTrace();
				} 
            }
        }.start();
	}
	@Override
	public void onAutoFocus(boolean success, Camera camera) {
		myCamera.setPreviewCallback(this);
	}
}
