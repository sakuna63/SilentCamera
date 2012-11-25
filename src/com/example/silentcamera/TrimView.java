package com.example.silentcamera;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.FloatMath;
import android.view.MotionEvent;
import android.view.View;

public class TrimView extends View {

    public float _x = 0, _y = 0;				// �^�b�`���ꂽ�ʒu�̍��W
    private int _w = 0,_h = 0;					// �摜�̃T�C�Y
    private int _X = 0,_Y = 0;					// �摜�̒[��x���W,y���W
    private int dif = 0;						// �摜���̗]���̑傫��
    
    
    private int sqX = 0,sqY = 0;				// �g���~���O�g��x,y���̒��S���W
    private int changeX_l=0,changeX_r=0;		// �����ʒu����̘g�p�̍��E�̕ω��l
    private int changeY_t=0,changeY_u=0;		// �����ʒu����̘g�p�̏㉺�̕ω��l
    
    private boolean isDraw = false;
    
    Paint paint1;
    Paint paint2;
    Paint paint3;
        
    public TrimView(Context context) {
        super(context);
        paint1 = new Paint();
        paint1.setColor(0xcc000000);
        paint1.setAntiAlias(true);
        
        paint2 = new Paint();
        paint2.setStyle(Style.STROKE);
        paint2.setStrokeWidth(2.0f);
        paint2.setColor(Color.LTGRAY);
        
        paint3 = new Paint();
        paint3.setAntiAlias(true);
        paint3.setColor(Color.LTGRAY);
    }
        
    // �摜�̃T�C�Y���擾
    public void sizeSet(int w, int h) {
        _w = w;
        _h = h;
    }
    
    public void reset(){
        changeX_l=0; changeX_r=0;		
        changeY_t=0; changeY_u=0;		
    }
    
    public ArrayList<Integer> getTrimData(){
        ArrayList<Integer> _arl = new ArrayList<Integer>();
        _arl.add(changeX_l);
        _arl.add(changeY_t);
        _arl.add(_w - changeX_r);
        _arl.add(_h - changeY_u);
        
        return _arl;
    }

    protected void onDraw(Canvas canvas) {
        
    	if(!isDraw){
    		isDraw = true;
    		dif = (getWidth() - _w)/2;
	        _X = dif;					// �[��x���W�͗]���̑傫���Ɠ����Ay���W��0
	        sqX = _w/2+ dif;
	        sqY = _h/2;
    	}
    	
        canvas.drawRect(dif + changeX_l, 0, _w + dif - changeX_r, changeY_t, paint1);		// ��̉e
        canvas.drawRect(0, 0, dif+changeX_l, _h, paint1);			// ���̉e
        canvas.drawRect(dif + changeX_l, _h-changeY_u, _w + dif - changeX_r, _h, paint1);	// ���̉e
        canvas.drawRect(_w+dif-changeX_r, 0, getWidth(), _h, paint1);	// �E�̉e
        
        canvas.drawRect(dif + changeX_l, changeY_t, _w+dif - changeX_r, _h - changeY_u, paint2);	// �g���~���O�̘g
        
        
        canvas.drawCircle(sqX+changeX_l/2-changeX_r/2, changeY_t, 12, paint3);			// ��̉~		:1
        canvas.drawCircle(dif+changeX_l, changeY_t, 12, paint3);						// ����̉~	:2
        canvas.drawCircle(dif+changeX_l, sqY+changeY_t/2-changeY_u/2, 12, paint3);		// ���̉~		:3
        canvas.drawCircle(dif+changeX_l, _h-changeY_u, 12, paint3);						// �����̉~	:4
        canvas.drawCircle(sqX+changeX_l/2-changeX_r/2, _h-changeY_u, 12, paint3);		// ���̉~		:5
        canvas.drawCircle(_w+dif-changeX_r, _h-changeY_u, 12, paint3);					// �E���̉~	:6
        canvas.drawCircle(_w+dif-changeX_r, sqY+changeY_t/2-changeY_u/2, 12, paint3);	// �E�̉~		:7
        canvas.drawCircle(_w+dif-changeX_r, changeY_t, 12, paint3);						// �E��̉~	:8
    }

    String TouchMode = "NONE";
    float _distance = 0f;
	private int circleNum = 0;
    
    public boolean onTouchEvent(MotionEvent e) {
        switch (e.getAction()) {
        case MotionEvent.ACTION_DOWN:
            _x = e.getX();	// �^�b�`���ꂽ���W���擾
            _y = e.getY();	// �^�b�`���ꂽ���W���擾
            if((circleNum = getNum(_x,_y)) != 0) TouchMode = "MOVE";
            break;
        case MotionEvent.ACTION_MOVE:
            _x = e.getX();	// �^�b�`���ꂽ���W���擾
            _y = e.getY();	// �^�b�`���ꂽ���W���擾

            if( _x < dif || _x > dif + _w || _y < 0 || _y > _h) break;
            
            if (TouchMode == "MOVE") {
            	switch (circleNum) {
				case 1:
					changeY_t = (int) e.getY();
					break;
				case 2:
					changeY_t = (int) e.getY();
					changeX_l = (int) e.getX() - dif;
					break;
				case 3:
					changeX_l = (int) e.getX() - dif;
					break;
				case 4:
					changeY_u = _h - (int) e.getY();
					changeX_l = (int) e.getX() - dif;
					break;
				case 5:
					changeY_u = _h - (int) e.getY();
					break;
				case 6:
					changeY_u = _h - (int) e.getY();
					changeX_r = dif + _w - (int) e.getX() ;
					break;
				case 7:
					changeX_r = dif + _w - (int) e.getX() ;
					break;
				case 8:
					changeY_t = (int) e.getY();
					changeX_r = dif + _w - (int) e.getX() ;
					break;
				}
            }
            break;
        case MotionEvent.ACTION_UP:
            TouchMode = "NONE";
            break;
        }
        invalidate();	
        return true;
    }
    
    private int getNum(float _x2, float _y2) {
    	int num = 0;
    	if( checkOver(_x2,sqX+changeX_l/2-changeX_r/2) ){
    		if( checkOver(_y2,changeY_t) ) num = 1;
    		else if( checkOver(_y2,_h-changeY_u) ) num = 5; 
    	}
    	else if( checkOver(_x2, dif+changeX_l) ) {
    		if( checkOver(_y2,changeY_t) ) num = 2;
    		else if( checkOver(_y2, sqY+changeY_t/2-changeY_u/2) ) num = 3;
    		else if( checkOver(_y2,_h-changeY_u) ) num = 4;     		
    	}
    	else if( checkOver(_x2, _w+dif-changeX_r) ){
    		if( checkOver(_y2,_h-changeY_u) ) num = 6;     	
    		else if( checkOver(_y2, sqY+changeY_t/2-changeY_u/2) ) num = 7;
    		else if( checkOver(_y2,changeY_t) ) num = 8;    		
    	}
		return num;
	}
    private boolean checkOver(float p,int cp){
    	boolean flag = false;
    	if( p < cp + 12 && p > cp - 12) flag = true;
    	return flag;
    }    
}
