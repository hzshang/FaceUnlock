package com.hzshang.faceunlock.UI;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.hzshang.faceunlock.R;
import com.hzshang.faceunlock.dialog.BottomDialog;
import com.hzshang.faceunlock.dialog.DialogMessage;
import com.hzshang.faceunlock.lib.DeleteFace;
import com.hzshang.faceunlock.lib.Storage;

import java.util.List;
import java.util.Map;

public class ManagerUser extends AppCompatActivity {
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_user);
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.menu_add_user);
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


    private void showUsers() {
        List<Map<String, Object>> users = Storage.getUsers(this);
        if (users != null) {
            SimpleAdapter simpleAdapter = new SimpleAdapter(this, users, R.layout.user, new String[]{"name", "faceUrl", "faceId"}, new int[]{R.id.name, R.id.face, R.id.faceId});
            listView.setAdapter(simpleAdapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    ViewGroup group = (ViewGroup) view;
                    TextView textView = (TextView) group.getChildAt(3);
                    showDeleteDialog(textView.getText().toString());
                }
            });
        }
    }

    private void showDeleteDialog(final String faceId) {
        BottomDialog dialog = new BottomDialog(this) {
            @Override
            public void onClick(View view) {
                if(view.getId()==R.id.bottom_dialog_delete){
                    deleteUser(faceId);
                }
                dismiss();
            }
        };
        dialog.show();
    }

    private void deleteUser(final String faceId) {
        String groupId = Storage.getGroupId(this);
        final ProgressDialog progressDialog = new ProgressDialog(this);
        new DeleteFace(this, new DeleteFace.interFace<Boolean, String>() {

            @Override
            public void processFinish(Boolean out) {
                progressDialog.dismiss();
                if (out) {
                    Storage.deleteUserInLocal(ManagerUser.this, faceId);
                    DialogMessage.showDialog(ManagerUser.this.getString(R.string.delete_user_done), ManagerUser.this);
                } else {
                    DialogMessage.showDialog(ManagerUser.this.getString(R.string.delete_user_error), ManagerUser.this);
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
        }).execute(faceId, groupId);
    }

}

