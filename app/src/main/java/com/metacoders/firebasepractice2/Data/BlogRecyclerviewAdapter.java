package com.metacoders.firebasepractice2.Data;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.metacoders.firebasepractice2.Model.Blog;
import com.metacoders.firebasepractice2.R;
import com.squareup.picasso.Picasso;

import java.util.Date;
import java.util.List;

/*First Extend RecyclerView.Adapter<BlogRecyclerviewAdapter.ViewHolder> then follow Alt + Enter*/

public class BlogRecyclerviewAdapter extends RecyclerView.Adapter<BlogRecyclerviewAdapter.ViewHolder> {

    private Context context;
    private List<Blog> blogList;

    public BlogRecyclerviewAdapter(Context context, List<Blog> blogList) {
        this.context = context;
        this.blogList = blogList;
    }

    @NonNull
    @Override
    public BlogRecyclerviewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.post_row, parent, false);

        return new ViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull BlogRecyclerviewAdapter.ViewHolder holder, int position) {

        Blog blog = blogList.get(position);
        String imageUrl = null;

        holder.title.setText(blog.getTitle());
        holder.desc.setText(blog.getDesc());

        java.text.DateFormat dateFormat = java.text.DateFormat.getDateInstance();
        String formattedDate = dateFormat.format(new Date(Long.valueOf(blog.getTimeStamp())).getTime());

        holder.timeStamp.setText(formattedDate);

        imageUrl = blog.getImage();

//        TODO: Use picasso library for loading image
        Picasso.get()
                .load(imageUrl)
                .into(holder.image);

    }

    @Override
    public int getItemCount() {
        return blogList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public TextView desc;
        public TextView timeStamp;
        public ImageView image;
        String userID;

        public ViewHolder(@NonNull View itemView, Context ctx) {
            super(itemView);

            context = ctx;

            title = (TextView) itemView.findViewById(R.id.post_list_title);
            desc = (TextView) itemView.findViewById(R.id.post_list_desc);
            timeStamp = (TextView) itemView.findViewById(R.id.post_list_time);
            image = (ImageView) itemView.findViewById(R.id.post_list_image);
            userID = null;

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    We can goto next activity

                }
            });
        }
    }
}
