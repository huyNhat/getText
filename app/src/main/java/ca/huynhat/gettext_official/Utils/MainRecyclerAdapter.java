package ca.huynhat.gettext_official.Utils;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.text.NumberFormat;
import java.util.ArrayList;

import ca.huynhat.gettext_official.Activities.ItemDetailsActivity;
import ca.huynhat.gettext_official.Model.Book;
import ca.huynhat.gettext_official.Model.Post;
import ca.huynhat.gettext_official.R;

/**
 * Created by huynhat on 2018-03-14.
 */

public class MainRecyclerAdapter extends RecyclerView.Adapter<MainRecyclerAdapter.AppViewHolder>
                implements Filterable{
    private static final String TAG = MainRecyclerAdapter.class.getSimpleName();

    private Context context;
    private ArrayList<Post> postList;
    private ArrayList<Post> filterPostList;
    private ArrayList<Book> bookList;

    private NumberFormat currency = NumberFormat.getCurrencyInstance();


    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String charString = constraint.toString();
                if(charString.isEmpty()){
                    filterPostList = postList;
                }else {
                    ArrayList<Post> filterList = new ArrayList<>();
                    for(Post post : postList){
                        if(post.getBook_title()!=null && post.getIsbn_number()!=null){
                            if(post.getIsbn_number().contains(constraint) || post.getBook_title().toLowerCase().contains(constraint)){
                                filterList.add(post);
                            }
                        }else {
                            Log.d(TAG,"error at filtering post");
                        }

                    }
                    filterPostList=filterList;
            }

                FilterResults filterResults = new FilterResults();
                filterResults.values = filterPostList;

                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filterPostList = (ArrayList<Post>) results.values;
                notifyDataSetChanged();
            }
        };
    }


    public MainRecyclerAdapter(Context context, ArrayList<Post> postList, ArrayList<Book> bookList) {
        this.context = context;
        this.postList = postList;
        this.bookList = bookList;
        this.filterPostList = postList;
    }

    public MainRecyclerAdapter(){

    }


    @Override
    public AppViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.book_item_layout, parent, false);
        return new AppViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(AppViewHolder holder, final int position) {
        final Post post = filterPostList.get(position);
        //final Book book = bookList.get(position);
        holder.price.setText(currency.format(post.getPrice()));
        holder.current_condition.setText(post.getCurrent_condition());
        for(Book book :bookList){
            if(book.getBook_isbn().equals(post.getIsbn_number())){
                holder.name.setText(book.getBook_title());
                Glide.with(context)
                        .load(book.getImage_url())
                        .into(holder.thumbnail);
            }
        }

        holder.item_cardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Toast.makeText(context, "Item clicked: "+post.getPost_id(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context, ItemDetailsActivity.class);
                intent.putExtra("post_id",post.getPost_id());
                context.startActivity(intent);
            }
        });



    }

    @Override
    public int getItemCount() {
        return filterPostList.size();
    }

    public class AppViewHolder extends RecyclerView.ViewHolder {

        public TextView name, price, current_condition;
        public ImageView thumbnail;
        public CardView item_cardview;

        public AppViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.title);
            current_condition = (TextView) view.findViewById(R.id.current_condition);
            price = (TextView) view.findViewById(R.id.price);
            thumbnail =(ImageView)view.findViewById(R.id.thumbnail);
            item_cardview = (CardView) view.findViewById(R.id.card_view);

        }


    }
}
