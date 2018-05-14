package ca.huynhat.gettext_official.Utils;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import ca.huynhat.gettext_official.Model.Book;
import ca.huynhat.gettext_official.Model.BookWanted;
import ca.huynhat.gettext_official.Model.Post;
import ca.huynhat.gettext_official.R;

/**
 * Created by huynhat on 2018-03-14.
 */

public class WishListRecyclerAdapter extends RecyclerView.Adapter<WishListRecyclerAdapter.AppViewHolder> {

    private Context context;
    private ArrayList<BookWanted> bookList;


    public WishListRecyclerAdapter(Context context, ArrayList<BookWanted> bookList) {
        this.context = context;
        this.bookList = bookList;
    }

    public WishListRecyclerAdapter(){

    }


    @Override
    public AppViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.book_item_layout_two, parent, false);

        return new AppViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(AppViewHolder holder, int position) {
        final BookWanted book = bookList.get(position);
        //final Book book = bookList.get(position);
        holder.title.setText(book.getBook_title());
        holder.isbn.setText(book.getBook_isbn());
        Glide.with(context)
                .load(book.getImage_url())
                .into(holder.thumbnail);
        if(bookList.get(position).getDate_wanted()!=null){
            long numOfDays = dayDiffs(bookList.get(position).getDate_wanted());
            String displayDays = numOfDays == 0? "Today" : numOfDays == 1 ? "1 day ago": String.valueOf(numOfDays)+" days ago" ;
            holder.wish_date.setText(displayDays);
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

    public void deteleItem(int postion){
        bookList.remove(postion);
        notifyItemRemoved(postion);
    }
    public void restoreItem(BookWanted item, int position) {
        bookList.add(position, item);
        // notify item added by position
        notifyItemInserted(position);
    }

    @Override
    public int getItemCount() {
        return bookList.size();
    }

    public class AppViewHolder extends RecyclerView.ViewHolder {

        public TextView title, isbn, wish_date;
        public ImageView thumbnail;
        public ConstraintLayout view_fore, view_back;

        public AppViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.bookTitleView);
            isbn = view.findViewById(R.id.isbnView);
            wish_date = view.findViewById(R.id.wishing_date);
            thumbnail = view.findViewById(R.id.bookImageView);
            view_fore = (ConstraintLayout) view.findViewById(R.id.view_foreground);
            view_back = (ConstraintLayout) view.findViewById(R.id.view_background);
        }
    }
}
