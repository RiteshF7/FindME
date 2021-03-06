package com.frinder.frinder.adapters;

import android.content.Context;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.frinder.frinder.R;
import com.frinder.frinder.model.Interest;

import java.util.List;

/**
 * Created by mallikaviswas on 10/21/17.
 */

public class InterestsAdapter extends RecyclerView.Adapter<InterestsAdapter.ViewHolder> {
    private final static String TAG = "InterestsAdapter";
    private Context mContext;
    private List<Interest> mInterests;
    private OnItemClickListener listener;

    public InterestsAdapter(Context context, List<Interest> interests) {
        mContext = context;
        mInterests = interests;
    }

    private Context getContext() {
        return mContext;
    }

    public interface OnItemClickListener {
        void onItemClick(View itemView, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    // Usually involves inflating a layout from XML and returning the holder
    @Override
    public InterestsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View interestView = inflater.inflate(R.layout.item_interest, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(interestView);
        return viewHolder;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(InterestsAdapter.ViewHolder viewHolder, int position) {
        // Get the data model based on position
        Interest interest = mInterests.get(position);

        // Set item views based on your views and data model
        viewHolder.tvInterestLabel.setText(interest.getLabel());
        viewHolder.ivInterestPic.setImageResource(interest.getPic());
        viewHolder.ivInterestIcon.setImageResource(interest.getIcon());

        int color = 0;
        if (interest.isSelected()) {
            color = ContextCompat.getColor(mContext, R.color.yellow_a700);
        }
        else {
            color = ContextCompat.getColor(mContext, R.color.black);
        }
        viewHolder.flInterestContainer.setBackgroundColor(color);
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return mInterests.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public ImageView ivInterestPic;
        public ImageView ivInterestIcon;
        public TextView tvInterestLabel;
        public FrameLayout flInterestContainer;

        public ViewHolder(final View itemView) {
            super(itemView);

            flInterestContainer = (FrameLayout) itemView.findViewById(R.id.flInterestContainer);
            ivInterestPic = (ImageView) itemView.findViewById(R.id.ivInterestPic);
            ivInterestIcon = (ImageView) itemView.findViewById(R.id.ivInterestIcon);
            tvInterestLabel = (TextView) itemView.findViewById(R.id.tvInterestLabel);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(itemView, position);
                        }
                    }
                }
            });
        }
    }
}
