package com.changhong.pontest.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.changhong.pontest.R;

import java.util.List;

/**
 * RecyclerView适配器
 */
public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.TextHolder> {
    private Context mContext;
    private List<String> texts;
    private OnItemClickListener mClickListener;

    public MyRecyclerViewAdapter(Context context, List<String> textInfo) {
        mContext = context;
        texts = textInfo;
    }

    @Override
    public TextHolder onCreateViewHolder(ViewGroup parent, int viewType) {          //创建viewholder
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.item_files, parent, false);            //获取视图

        return new TextHolder(view, mClickListener);
    }

    @Override
    public void onBindViewHolder(TextHolder holder, int position) {                 //绑定数据
        if (texts.isEmpty()) {
            holder.bindText("");
        } else {
            String mtext = texts.get(position);
            holder.bindText(mtext);
        }
    }


    @Override
    public int getItemCount() {                             //获取项目数量
        if (!texts.isEmpty()) {
            return texts.size();
        } else {
            return 0;
        }
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mClickListener = listener;
    }

    public static class TextHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView mTextView;
        private OnItemClickListener mListener;// 声明自定义的接口

        public TextHolder(View itemView, OnItemClickListener listener) {
            super(itemView);
            mListener = listener;
            // 为ItemView添加点击事件
            itemView.setOnClickListener(this);
            mTextView = itemView.findViewById(R.id.file_names);
        }

        public void bindText(String mText) {
            mTextView.setText(mText);
        }

        @Override
        public void onClick(View v) {
            // getpostion()为Viewholder自带的一个方法，用来获取RecyclerView当前的位置，将此作为参数，传出去
            mListener.onItemClick(v, getPosition());
        }
    }
}
