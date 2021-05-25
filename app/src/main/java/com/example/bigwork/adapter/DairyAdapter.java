package com.example.bigwork.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.bigwork.MainActivity;
import com.example.bigwork.R;
import com.example.bigwork.ReadActivity;
import com.example.bigwork.beans.Diary;
import com.example.bigwork.database.DairySave;
import com.example.bigwork.inter.onMoveAndSwipedListener;


import java.io.Serializable;
import java.util.List;


public class DairyAdapter extends RecyclerView.Adapter<DairyAdapter.ViewHolder> implements onMoveAndSwipedListener {
    private Context mContext;
    private List<Diary> diaryList;
    public DairyAdapter(List<Diary> diaryList){
        this.diaryList = diaryList;
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        return false;
    }

    @Override
    public void onItemDismiss(final int position) {
        AlertDialog dialog = new AlertDialog.Builder(mContext)
                .setTitle("提示")
                .setMessage("确定删除"+diaryList.get(position).getDate()+"的记录吗？")
                .setNegativeButton("取消",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                             notifyDataSetChanged();
                            }
                        })
                .setOnKeyListener(new DialogInterface.OnKeyListener() {
                    @Override
                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                        if (keyCode == KeyEvent.KEYCODE_BACK
                                && event.getRepeatCount() == 0) {
                            notifyDataSetChanged();
                        }
                        return false;
                    }
                })
                .setPositiveButton("确定",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int whichButton) {
                                DairySave dairySave =new DairySave(mContext,"Dairy.db",null,2);
                                SQLiteDatabase db=dairySave.getWritableDatabase();
                                db.delete("Dairy","date=?",new String[]{diaryList.get(position).getDate()});
                                diaryList.remove(position);
                                notifyItemRemoved(position);
                            }
                        }).show();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        CardView cardView;
        TextView dateView;
        TextView weatherView;
        TextView addressView;
        ImageView weather_icon_view;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView=(ImageView) itemView.findViewById(R.id.dairy_img);
            dateView=(TextView)itemView.findViewById(R.id.date);
            weatherView=(TextView)itemView.findViewById(R.id.weather);
            addressView=(TextView)itemView.findViewById(R.id.address);
            weather_icon_view=(ImageView)itemView.findViewById(R.id.weather_icon);
            cardView=(CardView)itemView.findViewById(R.id.materialCardView);
        }
    }
    @NonNull
    @Override
    public DairyAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(mContext==null){
            mContext=parent.getContext();
        }
        View view= LayoutInflater.from(mContext).inflate(R.layout.dairy_item,parent,false);
        final DairyAdapter.ViewHolder holder=new ViewHolder(view);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position=holder.getAdapterPosition();
                Intent intent=new Intent(mContext, ReadActivity.class);
                intent.putExtra("dairy", diaryList.get(position).getText());
                intent.putExtra("img_list",(Serializable) diaryList.get(position).getImg());
                mContext.startActivity(intent);
            }
        });


        return holder;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBindViewHolder(@NonNull DairyAdapter.ViewHolder holder, int position) {
        Diary diary = diaryList.get(position);
        if(diary.getImg()==null){
            Glide.with(mContext).load(diary.getSkyconSrc(diary)).into(holder.weather_icon_view);
            Glide.with(mContext).load(R.drawable.default_error).apply(
                    RequestOptions.centerCropTransform()
                            .placeholder(R.drawable.default_error)
                            .error(R.drawable.default_error)
                            .fallback(R.drawable.default_error))
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .thumbnail(0.1f)
                    .into(holder.imageView);
            holder.dateView.setText(diary.getDate());
            holder.addressView.setText(diary.getAddress());
            holder.weatherView.setText(diary.getWeather());
        }
        else {
            Glide.with(mContext).load(diary.getSkyconSrc(diary)).into(holder.weather_icon_view);
            Glide.with(mContext).load(diary.getImg().get(0)).apply(
                    RequestOptions.centerCropTransform()
                            .error(R.drawable.default_error)
                            .fallback(R.drawable.default_error))
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .thumbnail(0.1f)
                    .into(holder.imageView);
            holder.dateView.setText(diary.getDate());
            holder.addressView.setText(diary.getAddress());
            holder.weatherView.setText(diary.getWeather());
        }

    }

    @Override
    public int getItemCount() {
        return diaryList.size();
    }
}
