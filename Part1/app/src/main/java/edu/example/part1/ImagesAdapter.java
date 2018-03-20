package edu.example.part1;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.List;

import edu.example.part1.pojo.Population;

/**
 * Created by Joy on 3/20/18.
 * An adapter to display a collection of images
 * in a RecyclerView.
 * Note: A RecyclerView is like a ListView except it has better performance
 */

public class ImagesAdapter extends RecyclerView.Adapter<ImagesAdapter.ViewHolder> {

    private OnItemClickListener listener;
    private List<Population.WorldPopulation> worldPopulationList;
    private Context mContext;

    /*
    A constructor to initialize the RecyclerView with the list that will be used to populate the View
     */
    public ImagesAdapter(Context context, List<Population.WorldPopulation> worldPopulations) {
        mContext = context;
        worldPopulationList = worldPopulations;
    }

    /*
    A convenience method to return the context
     */
    private Context getContext() {
        return mContext;
    }

    /*
    Initializes every row in RecyclerView with an ImageView
     */
    @Override
    public ImagesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View imageView = layoutInflater.inflate(R.layout.image_view, parent, false);
        return new ViewHolder(imageView);
    }

    /*
    Populates every row of the RecyclerView
     */
    @Override
    public void onBindViewHolder(ImagesAdapter.ViewHolder viewHolder, int position) {
        Population.WorldPopulation worldPopulation = worldPopulationList.get(position);
        ImageView imageView = viewHolder.imageView;
        Picasso.get().load(worldPopulation.flag).into(imageView);
    }

    @Override
    public int getItemCount() {
        return worldPopulationList.size();
    }

    /*
    This method is used to set a listener for clicking on an item in RecyclerView
     */
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    /*
    An interface that declares a method that defines the action that will be performed when
    user clicks on an item in the RecyclerView
     */
    public interface OnItemClickListener {
        void onItemClick(View itemView, int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;

        public ViewHolder(final View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.imageView2);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        listener.onItemClick(itemView, getAdapterPosition());
                    } else {
                        Toast.makeText(getContext(), "You cannot click here", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}
