package com.example.vpelenskyi.qrssh;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.vpelenskyi.qrssh.host.Host;

import java.util.ArrayList;

/**
 * Created by v.pelenskyi on 03.02.2016.
 */
public class BaseAdapterHost extends BaseAdapter {

    private Context ctx;
    private LayoutInflater lInflater;
    private ArrayList<Host> hosts;
    int imageWinConnect = R.drawable.windows_connect;
    int imageWinNoConnect = R.drawable.windows;
    int imageUbnConnect = R.drawable.ubuntu_connect;
    int imageUbnNoConnect = R.drawable.ubuntu;
    int image;

    BaseAdapterHost(Context context, ArrayList<Host> hosts) {
        this.ctx = context;
        this.hosts = hosts;
        this.lInflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return hosts.size();
    }

    @Override
    public Object getItem(int position) {
        return hosts.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = lInflater.inflate(R.layout.item, parent, false);
        }

        Host host = getHost(position);

        ((TextView) view.findViewById(R.id.itemText)).setText(host.getAlias());
        ((ImageView) view.findViewById(R.id.itemImeg)).setImageResource(getImageOS(host));
        return view;
    }

    private int getImageOS(Host host) {
        switch (host.getOs()) {
            case Host.OS_WINDOWS:
                if (host.getHostConnect()) {
                    image = imageWinConnect;
                } else {
                    image = imageWinNoConnect;
                }
                break;
            case Host.OS_UBUNTU:
                if (host.getHostConnect()) {
                    image = imageUbnConnect;
                } else {
                    image = imageUbnNoConnect;
                }
                break;
            default:
                image = imageWinNoConnect;
                break;
        }
        return image;
    }

    private Host getHost(int position) {
        return ((Host) getItem(position));


    }


}
