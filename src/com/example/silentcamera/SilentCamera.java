package com.example.silentcamera;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap.CompressFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

public class SilentCamera extends Activity implements OnClickListener{

    private static final int GETTRIM = 1;
    
    private static final int FP = LinearLayout.LayoutParams.FILL_PARENT;
    private static final int WC = LinearLayout.LayoutParams.WRAP_CONTENT;
	public static boolean btn_flag = true;
    
	CameraView cameraView;	
	Button shutterBtn,trimingBtn;
	
	/*
	 * Life cycle
	 */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        cameraView = new CameraView(this);
        setContentView(cameraView );
        
        LinearLayout base = new LinearLayout(this);
        base.setGravity(Gravity.BOTTOM | Gravity.RIGHT);

        shutterBtn = new Button(this);
        shutterBtn.setText("撮影する");
        shutterBtn.setOnClickListener(this);
        base.addView(shutterBtn,params(WC,WC));
        
        trimingBtn = new Button(this);
        trimingBtn.setText("トリミング");
        trimingBtn.setOnClickListener(this);
        base.addView(trimingBtn,params(WC,WC));
        
        addContentView(base, params(FP,FP));
    }
        
    /**
     * Override methods
     */
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == GETTRIM){
            if(resultCode == RESULT_OK){
                toast("トリミングに成功しました。");
                String path = "/sdcard/SilentCamera/" + String.valueOf(new Date().getTime()) + ".jpg";
            	try {
        			FileOutputStream fos = new FileOutputStream(path);
        			BitmapHolder._holdedBitmap.compress(CompressFormat.JPEG, 100, fos);
        			// 保存処理終了
        			fos.close();
        		} catch (Exception e) {
        			Log.e("Error", "" + e.toString());
        		}
                BitmapHolder._holdedBitmap = null;
                new File(BitmapHolder._pathBitmap).delete();
                BitmapHolder._pathBitmap = null;                
            }else{
               toast("画像の取得に失敗しました。");
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
	@Override
	public void onClick(View v) {
		if(!btn_flag) return;
		if( v == shutterBtn){
			btn_flag = false;
			cameraView.takePhoto();
		}
		else if( v == trimingBtn){
	        if(BitmapHolder._holdedBitmap == null) {
	        	toast("何か撮影してください。");
	        	return;
	        }
			Intent intent = new Intent(getApplicationContext(),TrimingActivity.class);
	        startActivityForResult(intent,GETTRIM);			
		}
	}
	private void toast(String mes){
		Toast.makeText(this, mes, Toast.LENGTH_LONG).show();
	}
	private LinearLayout.LayoutParams params(int width,int height){
		return new LinearLayout.LayoutParams(width, height);
	}
}
