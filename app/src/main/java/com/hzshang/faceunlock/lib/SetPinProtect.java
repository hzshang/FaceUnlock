package com.hzshang.faceunlock.lib;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import com.hzshang.faceunlock.R;

/**
 * Created by hzshang on 2017/11/30.
 */
//init pin protect View
public abstract class SetPinProtect extends Dialog implements View.OnClickListener{
    private Context context;
    protected EditText answer;
    protected Spinner questions;
    protected SetPinProtect(Context context){
        super(context);
        this.context=context;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        View view = View.inflate(context,R.layout.setpinprotection,null);
        WindowManager m = getWindow().getWindowManager();
        Display d = m.getDefaultDisplay();
        WindowManager.LayoutParams p = getWindow().getAttributes();
        p.width = d.getWidth() - 100;
        getWindow().setAttributes(p);
        Button confirm = (Button) view.findViewById(R.id.set_pin_protection_confirm);
        Button cancle = (Button) view.findViewById(R.id.set_pin_protection_cancle);
        answer=(EditText) view.findViewById(R.id.answer);
        questions=(Spinner)view.findViewById(R.id.question);
        confirm.setOnClickListener(this);
        cancle.setOnClickListener(this);
        setContentView(view);
    }
}
