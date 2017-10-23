package com.example.ahao9.chatclient.activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.example.ahao9.chatclient.adpaters.PersonListAdapter;
import com.example.ahao9.chatclient.components.ChatHistory;
import com.example.ahao9.chatclient.components.ChatMessage;
import com.example.ahao9.chatclient.components.ClientSocket;
import com.example.ahao9.chatclient.components.Person;
import com.example.ahao9.chatclient.components.R;
import com.example.ahao9.chatclient.components.UpdatedUserList;
import com.example.ahao9.chatclient.components.Utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Fragment2 --- Chat Person List
 */
public class PersonList extends Fragment implements AbsListView.OnScrollListener,View.OnClickListener {

    private static final String TAG = "cuowu";

    private static TextView textView;
    private static PersonListAdapter personListAdapter;

    private static List<Person> list;
    private ListView listView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.person_list,null);

        list = new ArrayList<>();
        textView = (TextView) view.findViewById(R.id.myself_name);
        textView.setText(SetNameActivity.getUsername());
        textView.setOnClickListener(this);
        listView = (ListView) view.findViewById(R.id.list);

        personListAdapter = new PersonListAdapter(getContext(),list);
        listView.setAdapter(personListAdapter);
        listView.setOnScrollListener(this);

        //fragment 1 as a subscriber to receive message
        EventBus.getDefault().register(this);

        //after this fragment's initialization finished,we start asynTask to receive user list from server
        MainActivity.getReceive_text().execute();

        return view;
    }

    /**
     * clear all existed users in the list, regain a new userlist
     */
    public void updateUI(final UpdatedUserList uplist){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                list.clear();
                personListAdapter.notifyDataSetChanged();

                String line = uplist.getUpdatedList();
                    Utils.showShortToast(getActivity(),line);
                    String[] userInfo = line.split(";");
                    for (int i = 0; i <userInfo.length ; i++) {
                        String u_name = userInfo[i].split(",")[0];
                        String u_ip = userInfo[i].split(",")[1];
                        if (! u_name.equals(SetNameActivity.getUsername())){
                            list.add(new Person(u_name,u_ip));
                            personListAdapter.notifyDataSetChanged();
                        }
                    }
            }
        });
    }

    @Subscribe
    public void onEvent(UpdatedUserList updatedUserList) {
        updateUI(updatedUserList);
    }

    /**
     * when you press yourself's icon on the Contact List, a popupMenu will pop out with your concrete intenet info
     * @param view
     */
    @Override
    public void onClick(View view) {
        PopupMenu popupMenu = new PopupMenu(getActivity(),textView);
        popupMenu.getMenuInflater().inflate(R.menu.popwindow,popupMenu.getMenu());

        popupMenu.getMenu().findItem(R.id.uname).setTitle("Username : " + SetNameActivity.getUsername());
        popupMenu.getMenu().findItem(R.id.ip).setTitle("Host Address : " + ClientSocket.getHostIPAddress());
        popupMenu.getMenu().findItem(R.id.port).setTitle("Host Port Number : " + ClientSocket.getHostPortNum());

        popupMenu.show();
    }

    public static TextView getTextView() {
        return textView;
    }
    public static List<Person> getList() {
        return list;
    }

    @Override
    public void onScrollStateChanged(AbsListView absListView, int i) {

    }

    @Override
    public void onScroll(AbsListView absListView, int i, int i1, int i2) {

    }
}
