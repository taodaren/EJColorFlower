package cn.eejing.colorflower.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wx.wheelview.adapter.BaseWheelAdapter;

import cn.eejing.colorflower.R;

public class MyWheelAdapter extends BaseWheelAdapter<String> {
    private Context mContext;

    public MyWheelAdapter(Context context) {
        mContext = context;
    }

    @Override
    protected View bindView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_date_select, parent,false);
            viewHolder.textView = convertView.findViewById(R.id.item_name);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.textView.setText(mList.get(position));
        return convertView;
    }

    static class ViewHolder {
        TextView textView;
    }

}
