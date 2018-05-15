package com.example.georgecristian.proiectidp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.FriendViewHolder> {

    private List<String> friendsStrings;

    public FriendsAdapter(List<String> friendsStrings) {
        this.friendsStrings = friendsStrings;
    }

    public void setFriendsStrings(List<String> friendsStrings) {
        this.friendsStrings = friendsStrings;
    }

    @Override
    public FriendViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.friends_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        FriendViewHolder viewHolder = new FriendViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(FriendViewHolder holder, int position) {
        holder.bind(friendsStrings.get(position));
    }

    @Override
    public int getItemCount() {
        return friendsStrings.size();
    }

    class FriendViewHolder extends RecyclerView.ViewHolder {
        TextView friendView;

        public FriendViewHolder(View itemView) {
            super(itemView);

            friendView = (TextView) itemView.findViewById(R.id.tv_item_friend);
        }

        protected void bind(String text) {
            friendView.setText(text);
        }

    }

}
