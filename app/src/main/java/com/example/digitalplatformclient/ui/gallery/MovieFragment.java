package com.example.digitalplatformclient.ui.gallery;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.digitalplatformclient.R;
import com.example.digitalplatformclient.api.AJAX;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import ru.alexbykov.nopaginate.callback.OnLoadMoreListener;
import ru.alexbykov.nopaginate.paginate.NoPaginate;

/**
 * A fragment representing a list of Items.
 */
public class MovieFragment extends Fragment {
    private MovieFragment viewsingle;
    private OkHttpClient client = new OkHttpClient();
    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 3;
    private int page = 1;
    private MovieRecyclerViewAdapter adapter;
    private boolean loadmore = true;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MovieFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static MovieFragment newInstance(int columnCount) {
        MovieFragment fragment = new MovieFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_movie_list, container, false);
        viewsingle = this;

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            adapter = new MovieRecyclerViewAdapter(new JSONArray());
            recyclerView.setAdapter(adapter);

            NoPaginate noPaginate = NoPaginate.with(recyclerView)
                    .setOnLoadMoreListener(new OnLoadMoreListener() {
                        @Override
                        public void onLoadMore() {
                            loadMore((RecyclerView) view);
                        }
                    })
                    .build();
            loadMore((RecyclerView) view);
        }
        return view;
    }

    private void loadMore(RecyclerView view) {
        if (loadmore) {
            loadmore = false;
            AJAX.get("movies/all/" + page, "", new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    // Something went wrong
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        String responseStr = response.body().string();
                        try {
                            JSONObject jsonObject = new JSONObject(responseStr);

                            viewsingle.requireActivity().runOnUiThread(() -> {
                                try {
                                    // JSONObject itemsList = jsonObject.getJSONArray("itemsList").getJSONObject(0);
                                    // recyclerView.setAdapter(new MovieRecyclerViewAdapter(jsonObject.getJSONArray("itemsList")));
                                    JSONArray itemsList = jsonObject.getJSONArray("itemsList");
                                    int length = itemsList.length();
                                    for (int i = 0; i < length; i++) {
                                        adapter.getValues().put(itemsList.get(i));
                                    }
                                    adapter.notifyDataSetChanged();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        page++;

                    } else {
                        System.out.println("Test");
                        // AJAX not successful
                    }
                    loadmore = true;
                }
            });
        }
    }
}