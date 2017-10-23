package com.example.ahao9.chatclient.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ahao9.chatclient.components.ClientSocket;
import com.example.ahao9.chatclient.components.R;
import com.example.ahao9.chatclient.components.Utils;

import java.io.IOException;

/**
 * Fragment 3, a page that covered some general user info and settings
 */
public class SettingList extends android.support.v4.app.Fragment implements View.OnClickListener{

    private Button exitBtn;
    private TextView info_name;
    private TextView server_name;
    private TextView about;
    private TextView ip;
    private ImageView iv_sex;
    private PopupMenu popupMenu;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View layout = inflater.inflate(R.layout.setting_list,null);
        exitBtn = (Button) layout.findViewById(R.id.btn_exit);
        iv_sex = (ImageView) layout.findViewById(R.id.iv_sex);

        initView(layout);
        exitBtn.setOnClickListener(new exitClickListener());

        return layout;
    }

    class exitClickListener implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            System.exit(0);
        }
    }

    /**
     * set current name and ip info in this UI and setOnClickListener on each item
     * @param layout
     */
    private void initView(View layout) {
        info_name = (TextView) layout.findViewById(R.id.info_name);
        ip = (TextView) layout.findViewById(R.id.info_ip);
        ip.setText("Host IP : " + ClientSocket.getHostIPAddress() + "    Host Port : " + ClientSocket.getHostPortNum());
        info_name.setText(SetNameActivity.getUsername());

        server_name = (TextView) layout.findViewById(R.id.show_server_info);
        about = (TextView) layout.findViewById(R.id.about);
        server_name.setOnClickListener(this);
        about.setOnClickListener(this);
        layout.findViewById(R.id.setting_user).setOnClickListener(this);
        layout.findViewById(R.id.info_rename).setOnClickListener(this);
        layout.findViewById(R.id.reset_sex).setOnClickListener(this);
        layout.findViewById(R.id.rating_me).setOnClickListener(this);
    }

    /**
     * concrete action for each item you clicked on
     * @param view
     */
    int sex = 0;
    String str;
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.setting_user:
                break;
            case R.id.info_rename:// Rename
                showDialog();
                break;
            case R.id.reset_sex:// switch sex
                switch_sex();
                break;
            case R.id.show_server_info:  //show server info
                showPopMenu("Server Info : ","Server Address : " + ClientSocket.getIpAddress(),
                        "Server Port Number : " + ClientSocket.getPortNum());
                break;
            case R.id.rating_me:// rating me
                startActivity(new Intent().setClass(getActivity(),RatingActivity.class));
                break;
            case R.id.about:// about me
                showPopMenu("About : ","Made by Hao Zhang ","Metropolia UAS 2017 Autumn");
                break;
            default:
                break;
        }
    }
    
    private void showPopMenu(String title1,String title2,String title3) {
        popupMenu = new PopupMenu(getActivity(),about);
        popupMenu.getMenuInflater().inflate(R.menu.popwindow,popupMenu.getMenu());

        popupMenu.getMenu().findItem(R.id.uname).setTitle(title1);
        popupMenu.getMenu().findItem(R.id.ip).setTitle(title2);
        popupMenu.getMenu().findItem(R.id.port).setTitle(title3);

        popupMenu.setGravity(Gravity.CENTER);
        popupMenu.show();
    }

    private void switch_sex() {
        if (sex == 0 ){
            str = "Female";
        }else if (sex == 1){
            str = "Male";
        }
        new AlertDialog.Builder(getActivity())
                .setTitle("Do you want to switch your sex to " + str + "?")
                .setIcon(R.drawable.plus)
                .setPositiveButton("Verify", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (sex == 0){
                            iv_sex.setImageResource(R.drawable.ic_sex_female);
                            sex = 1;
                        }else if (sex == 1){
                            iv_sex.setImageResource(R.drawable.ic_sex_male);
                            sex = 0;
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        dialog.dismiss();
                    }
                }).show();
    }

    /**
     * Rename Dialog
     * create a dialog to rename the current name
     * send current name and updated name to server, then server send your new name to others clients
     */
    private void showDialog() {
        final EditText setName = new EditText(getActivity());
        try {
            new AlertDialog.Builder(getActivity())
                    .setTitle("Rename:")
                    .setIcon(R.drawable.plus)
                    .setView(setName)

                    .setPositiveButton("Verify", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String current_name = info_name.getText().toString();
                            String updated_name = setName.getText().toString();
                            if (updated_name.equals("")) {
                                Utils.showShortToast(getContext(),"Name can not be empty!");
                                showDialog();
                            }else {
                                Toast.makeText(getContext(), updated_name, Toast.LENGTH_SHORT).show();
                                SetNameActivity.setUsername(updated_name);
                                info_name.setText(SetNameActivity.getUsername());
                                PersonList.getTextView().setText(updated_name);

                                //transmit updated name to server to verify
                                try {
                                    ClientSocket.getDos().writeUTF("updated_name" + "," + current_name + "," + updated_name);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method stub
                            dialog.dismiss();
                        }
                    }).show();
        }catch (Exception e){
            Log.d("cuowu",e.getMessage());
        }
    }
}
