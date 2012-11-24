package com.example.silentcamera;

import java.util.ArrayList;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.FrameLayout;
import android.widget.Button;
import android.widget.ImageView;

public class TrimingActivity extends Activity {

	Bitmap _bmOriginal;
	
	@Override
    public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.triming);
		System.gc();
		_bmOriginal = BitmapHolder._holdedBitmap.copy(Bitmap.Config.ARGB_8888, true);
		BitmapHolder._holdedBitmap = null;
    }
       
       
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		final TrimView _tview = new TrimView(this);
		((LinearLayout) findViewById(R.id.imgcontainer)).addView(_tview);
		int _width = ((FrameLayout)findViewById(R.id.fl1)).getWidth();
		int _height = ((FrameLayout)findViewById(R.id.fl1)).getHeight();
		
        float _scaleW = (float) _width / (float) _bmOriginal.getWidth();
        float _scaleH = (float) _height / (float) _bmOriginal.getHeight();
        final float _scale = Math.min(_scaleW, _scaleH);
        Matrix matrix = new Matrix();
        matrix.postScale(_scale, _scale);
           
        Bitmap _bm = Bitmap.createBitmap(_bmOriginal, 0, 0, _bmOriginal.getWidth(),_bmOriginal.getHeight(), matrix, true);
           
           
        ((ImageView)findViewById(R.id.imageView1)).setImageBitmap(_bm);
           
        ((Button)findViewById(R.id.button1)).setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		ArrayList<Integer> _al = _tview.getTrimData();
        		
        		int _ix = (int)(_al.get(0)/_scale);
        		int _iy = (int)(_al.get(1)/_scale);
        		int _iwidth = (int)(_al.get(2)/_scale);
        		int _iheight = (int)(_al.get(3)/_scale);
        		
        		_ix = (_ix>0) ? _ix : 0;
        		_iy = (_iy>0) ? _iy : 0;
        		_iwidth = (_iwidth + _ix < _bmOriginal.getWidth()) ? _iwidth : _bmOriginal.getWidth() - _ix;
        		_iheight = (_iheight + _iy < _bmOriginal.getHeight()) ? _iheight : _bmOriginal.getHeight() - _iy;
        		BitmapHolder._holdedBitmap = Bitmap.createBitmap(_bmOriginal, _ix, _iy, _iwidth, _iheight, null, true);
        		setResult(RESULT_OK);
        		finish();
        	}	
        });

        ((Button)findViewById(R.id.button2)).setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		_tview.reset();
        	}
        });
        
        ((Button)findViewById(R.id.button3)).setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		finish();
        	}
        });
                          
        super.onWindowFocusChanged(hasFocus);
        _tview.sizeSet((int)(_bmOriginal.getWidth()*_scale),(int)(_bmOriginal.getHeight()*_scale));
	}	
}