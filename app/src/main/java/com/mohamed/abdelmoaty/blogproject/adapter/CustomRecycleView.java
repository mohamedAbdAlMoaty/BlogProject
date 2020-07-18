package com.mohamed.abdelmoaty.blogproject.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mohamed.abdelmoaty.blogproject.R;
import com.mohamed.abdelmoaty.blogproject.activites.SingleActivity;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by HP on 5/1/2018.
 */




public class CustomRecycleView extends RecyclerView.Adapter<CustomRecycleView.ViewHolder> {

    private ArrayList<String> usernames;
    private ArrayList<String> titlee;
    private ArrayList<String> descc;
    private   ArrayList<String> imgg;
   private   ArrayList<String> profileimgg;
    private ArrayList<String> timee;
     private   ArrayList<String> blogIds;
    private Context context;


    public CustomRecycleView(Context context,ArrayList<String> usernames,ArrayList<String> titlee, ArrayList<String> descc, ArrayList<String> imgg, ArrayList<String> blogIds,ArrayList<String> timee,ArrayList<String> profileimgg) {
       this.usernames=usernames;
        this.titlee = titlee;
        this.descc=descc;
        this.imgg = imgg;
        this.blogIds=blogIds;
       this.profileimgg=profileimgg;
        this.timee=timee;
        this.context=context;

    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View row = LayoutInflater.from(parent.getContext()).inflate(R.layout.custmrecyleview,parent,false);
        return new ViewHolder(row);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.name.setText(usernames.get(position));
        holder.title.setText(titlee.get(position));
        holder.desc.setText(descc.get(position));
        holder.time.setText(timee.get(position));
        Glide.with(context).load(imgg.get(position)).into(holder.imgvar);
       Glide.with(context).load(profileimgg.get(position)).into(holder.prof);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context , SingleActivity.class);
                i.putExtra("blogid",blogIds.get(position));
                i.putExtra("title",titlee.get(position));
                i.putExtra("desc",descc.get(position));
                i.putExtra("image",imgg.get(position));
               context.startActivity(i);
            }
        });


    }

    @Override
    public int getItemCount() {
        return titlee.size();
    }


    public static class ViewHolder extends  RecyclerView.ViewHolder{
        public View mView;
        public TextView name;
        public TextView title;
        public TextView desc;
        public  TextView time;
        public ImageView imgvar;
        public CircleImageView prof;

        public ViewHolder(View itemView) {
            super(itemView);
            mView=itemView;
            name=(TextView) itemView.findViewById(R.id.username);
            title = (TextView) itemView.findViewById(R.id.post_title);
            desc = (TextView) itemView.findViewById(R.id.post_desc);
            time=(TextView) itemView.findViewById(R.id.time);
            imgvar=(ImageView)itemView.findViewById(R.id.source_img);
            prof=(CircleImageView)itemView.findViewById(R.id.prof);

        }
    }

}

