package com.example.digitalplatformclient.ui.gallery;

import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.digitalplatformclient.R;
import com.example.digitalplatformclient.api.AJAX;
import com.example.digitalplatformclient.ui.player.PlayerActivity;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class MovieRecyclerViewAdapter extends RecyclerView.Adapter<MovieRecyclerViewAdapter.ViewHolder> {

    private final JSONArray mValues;

    public MovieRecyclerViewAdapter(JSONArray items) {
        mValues = items;
    }

    public JSONArray getValues() {
        return mValues;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_movie, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        try {
            holder.mItem = mValues.getJSONObject(position);
            holder.visualname.setText(holder.mItem.getString("visualname"));
            //  holder.mContentView.setText(mValues.getJSONObject(position).getString("visualname"));
            holder.mView.setOnClickListener((event) -> {
                try {
                    Intent intent = new Intent(holder.mView.getContext(), PlayerActivity.class);
                    intent.putExtra("url", AJAX.URL_API + "movie/" + holder.mItem.getString("_id"));
                    holder.mView.getContext().startActivity(intent);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            });
            String path = AJAX.URL + holder.mItem.getJSONArray("files").getString(0);
            Picasso.get().load(path).into(holder.portada);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return mValues.length();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        //  public final TextView mIdView;
        public final TextView visualname;
        public JSONObject mItem;
        public final ImageView portada;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            //   mIdView = (TextView) view.findViewById(R.id.item_number);
            visualname = (TextView) view.findViewById(R.id.visualname);
            portada = (ImageView) view.findViewById(R.id.portada);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + "'";
        }
    }
}