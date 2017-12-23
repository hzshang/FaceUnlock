package com.hzshang.faceunlock.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.hzshang.faceunlock.R;
import com.hzshang.faceunlock.lib.Storage;

/**
 * Created by hzshang on 2017/12/1.
 */
//输入密保时会打开输入法，不安全，弃用
public abstract class CheckPinProtect extends Dialog implements View.OnClickListener {
    private Context context;
    private EditText answer;
    private TextView question;
    private String pin_answer;
    protected CheckPinProtect(Context context) {
        super(context);
        this.context=context;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        View view = View.inflate(context, R.layout.check_pin_protection_dialog,null);
        WindowManager m = getWindow().getWindowManager();
        Display d = m.getDefaultDisplay();
        WindowManager.LayoutParams p = getWindow().getAttributes();
        p.width = d.getWidth() - 100;
        getWindow().setAttributes(p);
        Button confirm = (Button) view.findViewById(R.id.confirm_pin_protected_check);
        Button cancle = (Button) view.findViewById(R.id.cancle_pin_protected_check);
        answer=(EditText) view.findViewById(R.id.check_pin_protected_answer);
        question=(TextView)view.findViewById(R.id.check_pin_protected_question);
        String[] array= Storage.getPinProtect(context);
        pin_answer=array[1];
        question.setText(array[0]);
        confirm.setOnClickListener(this);
        cancle.setOnClickListener(this);
        setContentView(view);
        setCanceledOnTouchOutside(false);
        hideNavigation();
    }
    protected boolean isAnswerIsCorrect(){
        return answer.getText().toString().equals(pin_answer);
    }
    private void hideNavigation(){
        int uiOptions =  View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY|View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        getWindow().getDecorView().setSystemUiVisibility(uiOptions);
    }


}
