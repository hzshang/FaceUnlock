package com.hzshang.faceunlock.lib;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.hzshang.faceunlock.R;

/**
 * Created by hzshang on 2017/12/1.
 */

public abstract class checkPinProtect extends Dialog implements View.OnClickListener {
    private Context context;
    protected EditText answer;
    protected TextView question;
    public checkPinProtect(Context context) {
        super(context);
        this.context=context;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        View view = View.inflate(context, R.layout.set_pin_protection_dialog,null);
        WindowManager m = getWindow().getWindowManager();
        Display d = m.getDefaultDisplay();
        WindowManager.LayoutParams p = getWindow().getAttributes();
        p.width = d.getWidth() - 100;
        getWindow().setAttributes(p);
        Button confirm = (Button) view.findViewById(R.id.confirm_pin_protected_check);
        Button cancle = (Button) view.findViewById(R.id.cancle_pin_protected_check);
        answer=(EditText) view.findViewById(R.id.input_pin_protected_answer);
        confirm.setOnClickListener(this);
        cancle.setOnClickListener(this);
        setContentView(view);
    }
}
