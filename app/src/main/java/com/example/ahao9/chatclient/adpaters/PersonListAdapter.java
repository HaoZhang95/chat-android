package com.example.ahao9.chatclient.adpaters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.ahao9.chatclient.activities.ChatList;
import com.example.ahao9.chatclient.components.Person;
import com.example.ahao9.chatclient.components.R;
import com.example.ahao9.chatclient.components.Utils;

import java.util.List;

/**
 * PersonListAdapter for Fragment2 ---> PersonList
 */
public class PersonListAdapter extends BaseAdapter {

    private LinearLayout layout;
    private Context context;
    private List<Person> list;

    public PersonListAdapter(Context context, List<Person> list) {
        this.context=context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * set newly user to the list
     */
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = LayoutInflater.from(context);
        layout = (LinearLayout) inflater.inflate(R.layout.person_item,null);
        TextView t_name = (TextView)layout.findViewById(R.id.client_name);
        TextView t_ip = (TextView)layout.findViewById(R.id.client_ip);

        t_name.setText(list.get(position).getName());
        t_ip.setText("Ip Address: " + list.get(position).getIp());

        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.showShortToast(context,"sorry, personal chat haven't been implemented.");
            }
        });

        return layout;
    }
}
