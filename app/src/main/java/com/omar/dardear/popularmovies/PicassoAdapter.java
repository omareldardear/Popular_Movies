package com.omar.dardear.popularmovies;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;


/**
 * Created by Omar on 9/4/2015.
 */
public class PicassoAdapter  extends ArrayAdapter {
    private Context context;
    private LayoutInflater inflater;

    private ArrayList<String> imageUrls;

    static class ViewHolder
    {
        ImageView posterView;
    }



    public PicassoAdapter(Context context,ArrayList<String> imageUrls) {
        super(context, R.layout.grid_view_item,R.id.GridView_Imgs,imageUrls);

        this.context = context;
        this.imageUrls = imageUrls;

        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount()
    {
        return imageUrls.size();
    }

    @Override
    public String  getItem(int position)
    {
        return imageUrls.get(position);
    }
    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


//        ImageView imageView;
//        if (null == convertView) {
//
//            imageView=new ImageView(context);
////            convertView = inflater.inflate(R.layout.grid_view_item, parent, false);
//            convertView=imageView;
//        }
//
//        else{
//            imageView=(ImageView) convertView;
//        }
//
//
//        Picasso
//                .with(context)
//                .load(imageUrls.get(position))
//                .placeholder(R.drawable.test)
//                .error(R.drawable.test)
//                .fit()
//                .tag(context)
//                .into(imageView);
//
//        return imageView;
//    }
        ViewHolder holder;
        if (convertView == null)
        {
            holder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.grid_view_item, parent, false);
            holder.posterView = (ImageView) convertView.findViewById(R.id.imageView);
            convertView.setTag(holder);
        } //end if
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }


        Picasso.with(context)
                .load(imageUrls.get(position))
                .placeholder(R.drawable.loading342)
                .into(holder.posterView);
        return convertView;
    }
}
