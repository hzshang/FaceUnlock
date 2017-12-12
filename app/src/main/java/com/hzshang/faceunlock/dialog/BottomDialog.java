package com.hzshang.faceunlock.dialog;

import android.app.Dialog;
import android.content.Context;
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


//dialog class
public abstract class BottomDialog extends Dialog implements View.OnClickListener {

    private Context context;

    protected BottomDialog(Context context) {
        super(context, R.style.MyDialog);
        this.context = context;
        initView();
    }

    private void initView() {
        View view = View.inflate(context, R.layout.delete_user_dialog, null);
        WindowManager m = getWindow().getWindowManager();
        Display d = m.getDefaultDisplay();

        //点击空白区域可以取消dialog
        this.setCanceledOnTouchOutside(true);
        //点击back键可以取消dialog
        this.setCancelable(true);
        Window window = this.getWindow();
        //让Dialog显示在屏幕的底部
        window.setGravity(Gravity.BOTTOM);
        //设置窗口出现和窗口隐藏的动画
        window.setWindowAnimations(R.style.ios_bottom_dialog_anim);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
        setContentView(view);

        TextView cancel = (TextView) findViewById(R.id.bottom_dialog_cancel);
        cancel.setOnClickListener(this);
        TextView delete = (TextView) findViewById(R.id.bottom_dialog_delete);
        delete.setOnClickListener(this);
    }


}