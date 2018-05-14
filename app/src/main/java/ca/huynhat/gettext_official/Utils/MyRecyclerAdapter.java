package ca.huynhat.gettext_official.Utils;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import ca.huynhat.gettext_official.Activities.ItemDetailsActivity;
import ca.huynhat.gettext_official.Model.Book;
import ca.huynhat.gettext_official.Model.Post;
import ca.huynhat.gettext_official.R;

/**
 * Created by huynhat on 2018-03-14.
 */

public class MyRecyclerAdapter extends RecyclerView.Adapter<MyRecyclerAdapter.AppViewHolder>
                implements Filterable{

    private Context context;
    private ArrayList<Post> postList;
    private ArrayList<Book> bookList;
    private ArrayList<Post> filterPostList;


    private NumberFormat currency = NumberFormat.getCurrencyInstance();


    public MyRecyclerAdapter(Context context, ArrayList<Post> postList, ArrayList<Book> bookList) {
        this.context = context;
        this.postList = postList;
        this.bookList = bookList;
        this.filterPostList = postList;
    }

    public MyRecyclerAdapter(){

    }


    @Override
    public AppViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.book_item_layout_three, parent, false);

        return new AppViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(AppViewHolder holder, final int position) {
        final Post post = filterPostList.get(position);
        //final Book book = bookList.get(position);
        holder.price.setText(currency.format(post.getPrice()));
        holder.isbn_num.setText("ISBN: "+post.getIsbn_number());
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
                //Toast.makeText(context, "Price is : "+post.getPrice(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context, ItemDetailsActivity.class);
                intent.putExtra("post_id",post.getPost_id());
                context.startActivity(intent);
            }
        });

        if(post.getDate_time_stamp()!=null){
            long numOfDays = dayDiffs(post.getDate_time_stamp());
            String displayDays = numOfDays == 0? "Today" : String.valueOf(numOfDays);
            holder.days_in_store.setText(String.valueOf(displayDays));
        }

    }

    private long dayDiffs(String posted_day){
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date current_date = new Date();
        Date posted_date=null;

        try {
            posted_date = dateFormat.parse(posted_day);

        } catch (Exception e) {
            e.printStackTrace();
        }

        /** in milliseconds */
        long diff = current_date.getTime() - posted_date.getTime();

        /** remove the milliseconds part */
        diff = diff / 1000;

        return  diff / (24 * 60 * 60);

    }


    @Override
    public int getItemCount() {
        return filterPostList.size();
    }

    public class AppViewHolder extends RecyclerView.ViewHolder {

        public TextView name, price, isbn_num,days_in_store;
        public ImageView thumbnail;
        public CardView item_cardview;


        public AppViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.my_book_title);
            isbn_num = (TextView) view.findViewById(R.id.my_isbnTextView);
            price = (TextView) view.findViewById(R.id.my_price_view_text);
            thumbnail =(ImageView)view.findViewById(R.id.mybookImageView);
            item_cardview = (CardView) view.findViewById(R.id.my_card_view);
            days_in_store =(TextView)view.findViewById(R.id.days_in_market);

        }
    }


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
                        if(post.getIsbn_number().contains(constraint)){
                            filterList.add(post);
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
}
