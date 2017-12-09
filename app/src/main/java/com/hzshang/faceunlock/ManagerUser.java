package com.hzshang.faceunlock;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.hzshang.faceunlock.lib.DeleteFace;
import com.hzshang.faceunlock.lib.Storage;
import com.hzshang.faceunlock.service.ManagerService;

import java.util.List;
import java.util.Map;

public class ManagerUser extends AppCompatActivity{
    private LinearLayout linearLayout;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_user);
        linearLayout = (LinearLayout) findViewById(R.id.menu_add_user);
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(ManagerUser.this, AddUser.class);
                startActivity(intent);
            }
        });
        listView = (ListView) findViewById(R.id.mgr_user);
    }

    @Override
    protected void onResume() {
        showUsers();
        super.onResume();

    }

    //wait to better
    private void showUsers() {
        List<Map<String, Object>> users=Storage.getUsers(this);
        if(users!=null){
            SimpleAdapter simpleAdapter = new SimpleAdapter(this, users, R.layout.user, new String[]{"name", "faceUrl","faceId"}, new int[]{R.id.name, R.id.face,R.id.faceId});
            listView.setAdapter(simpleAdapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){

                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    ViewGroup group=(ViewGroup)view;
                    TextView textView=(TextView)group.getChildAt(3);
                    showDeleteDialog(textView.getText().toString());
                }
            });
        }
    }
    private void showDeleteDialog(String faceId){
        BottomDialog dialog=new BottomDialog(this,faceId);
        dialog.show();
    }

    private void deleteUser(final String faceId){
        String groupId=Storage.getGroupId(this);
        final ProgressDialog progressDialog = new ProgressDialog(this);
        new DeleteFace(this,new DeleteFace.interFace<Boolean,String>(){

            @Override
            public void processFinish(Boolean out) {
                progressDialog.dismiss();
                if(out){
                    Storage.deleteUserInLocal(ManagerUser.this,faceId);
                    com.hzshang.faceunlock.common.Dialog.showDialog(ManagerUser.this.getString(R.string.delete_user_done),ManagerUser.this);
                }else{
                    com.hzshang.faceunlock.common.Dialog.showDialog(ManagerUser.this.getString(R.string.delete_user_error),ManagerUser.this);
                }
                //update UI
                showUsers();
            }

            @Override
            public void processPre() {
                progressDialog.show();
            }

            @Override
            public void processRunning(String progress) {
                progressDialog.setMessage(progress);
            }
        }).execute(faceId,groupId);
    }

    //dialog class
    class BottomDialog extends Dialog {

        TextView delete;
        TextView cancel;
        String faceId;
        Context context;
        public BottomDialog(Context context,String faceId) {
            super(context, R.style.MyDialog);
            this.context=context;
            this.faceId=faceId;
            initView();
        }

        private void initView() {
            View view = View.inflate(context,R.layout.delete_user_dialog,null);
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
            lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setAttributes(lp);
            setContentView(view);
            cancel=(TextView)findViewById(R.id.dialog_cancel);
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });
            delete=(TextView)findViewById(R.id.dialog_delete);
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    deleteUser(faceId);
                    dismiss();
                }
            });
        }
    }
}

