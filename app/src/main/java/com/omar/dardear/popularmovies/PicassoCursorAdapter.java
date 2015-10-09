package com.omar.dardear.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;

import com.omar.dardear.popularmovies.data.MoviesContract;
import com.squareup.picasso.Picasso;

/**
 * Created by Omar on 10/1/2015.
 */
public class PicassoCursorAdapter extends CursorAdapter {

    public PicassoCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    static class ViewHolder {
        ImageView posterView;
        ImageView star_IN;
        ImageView star_Out;


        public ViewHolder(View pview) {
            posterView = (ImageView) pview.findViewById(R.id.PicassoImageView);
            star_IN=(ImageView) pview.findViewById(R.id.star_in);
            star_Out=(ImageView) pview.findViewById(R.id.star_out);
        }
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        View view = LayoutInflater.from(context).inflate(R.layout.grid_view_item, viewGroup, false);

        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ViewHolder viewHolder = (ViewHolder) view.getTag();

        String Poster_Attr = cursor.getString(cursor.getColumnIndexOrThrow(MoviesContract.MoviesEntry.COLUMN_POSTER_ATTR));

        int sort_index=cursor.getInt(cursor.getColumnIndexOrThrow(MoviesContract.MoviesEntry.COLUMN_SORT_INDEX));
        int favourite=cursor.getInt(cursor.getColumnIndexOrThrow(MoviesContract.MoviesEntry.COLUMN_FAVOURITE));

        if (favourite==1 && sort_index==0)
        {
            viewHolder.star_Out.setVisibility(View.VISIBLE);
        }
        else if (favourite==1)
        {
            viewHolder.star_IN.setVisibility(View.VISIBLE);
        }
        else
        {
            viewHolder.star_IN.setVisibility(View.INVISIBLE);
            viewHolder.star_Out.setVisibility(View.INVISIBLE);
        }


        Picasso.with(context)
                .load("http://image.tmdb.org/t/p/w185/" + Poster_Attr)
                .placeholder(R.drawable.loading342)
                .into(viewHolder.posterView);


    }
}
