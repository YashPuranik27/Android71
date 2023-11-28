package com.example.android71;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

// For reference: https://developer.android.com/reference/androidx/recyclerview/widget/RecyclerView.Adapter
public class AlbumController extends RecyclerView.Adapter<AlbumController.AlbumViewHolder> {
	private List<Album> list;
	private ItemActionListener listener;

	@NonNull
	@Override
	public AlbumViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_album, parent, false);
		return new AlbumViewHolder(itemView);
	}

	@Override
	public void onBindViewHolder(@NonNull AlbumViewHolder holder, int position) {
		Album album = list.get(position);
		holder.titleView.setText(album.getName());
		holder.bindListeners(position, listener, holder.itemView.getContext());
	}

	@Override
	public int getItemCount() {
		return list == null ? 0 : list.size();
	}

	void setItemActionListener(ItemActionListener listener) {
		this.listener = listener;
	}

	void setList(List<Album> list) {
		this.list = list;
		notifyDataSetChanged();
	}

	interface ItemActionListener {
		void openAlbum(int position);
		void deleteAlbum(int position, AlbumViewHolder holder, Context context);
		void renameAlbum(int position, AlbumViewHolder holder, Context context);
		void showActionsOverlay(AlbumViewHolder holder, Context context);
		void hideActionsOverlay(AlbumViewHolder holder, Context context);
	}

	static class AlbumViewHolder extends RecyclerView.ViewHolder {
		CardView cardView;
		ConstraintLayout mainLayout;
		LinearLayout overlayLayout;
		TextView titleView;
		ImageButton overflowButton, deleteButton, renameButton, cancelButton;

		public AlbumViewHolder(@NonNull View itemView) {
			super(itemView);
			cardView = itemView.findViewById(R.id.album_card_view);
			mainLayout = itemView.findViewById(R.id.album_card_layout_main);
			overlayLayout = itemView.findViewById(R.id.album_card_layout_overlay);
			titleView = itemView.findViewById(R.id.album_card_text_title);
			overflowButton = itemView.findViewById(R.id.album_card_button_overflow);
			deleteButton = itemView.findViewById(R.id.album_card_button_delete);
			renameButton = itemView.findViewById(R.id.album_card_button_rename);
			cancelButton = itemView.findViewById(R.id.album_card_button_cancel);
		}

		void bindListeners(int position, ItemActionListener listener, Context context) {
			itemView.setOnClickListener(v -> listener.openAlbum(position));
			deleteButton.setOnClickListener(v -> listener.deleteAlbum(position, this, context));
			renameButton.setOnClickListener(v -> listener.renameAlbum(position, this, context));
			overflowButton.setOnClickListener(v -> listener.showActionsOverlay(this, context));
			cancelButton.setOnClickListener(v -> listener.hideActionsOverlay(this, context));
		}
	}
}
