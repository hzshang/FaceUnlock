package com.hzshang.faceunlock.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.hzshang.faceunlock.R;

/**
 * Created by hzshang on 2017/12/12.
 */

public abstract class OverLayDialog extends Dialog implements View.OnClickListener{
    private Context context;
    public OverLayDialog(@NonNull Context context) {
        super(context);
        this.context=context;
        initView();
    }
    private void initView(){
        View view = View.inflate(context, R.layout.ask_permission_dialog, null);
        WindowManager m = getWindow().getWindowManager();
        Display d = m.getDefaultDisplay();
        WindowManager.LayoutParams p = getWindow().getAttributes();
        p.width = d.getWidth() - 100;
        getWindow().setAttributes(p);
        setContentView(view);
        TextView cancel = (TextView) findViewById(R.id.ask_overlay_cancel);
        cancel.setOnClickListener(this);
        TextView delete = (TextView) findViewById(R.id.ask_overlay_confirm);
        delete.setOnClickListener(this);
    }
}
