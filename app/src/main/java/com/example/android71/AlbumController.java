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

public class AlbumController extends RecyclerView.Adapter<AlbumController.AlbumViewHolder> {
	private Context context;
	private ItemActionListener listener;
	private List<Album> list;

	@NonNull
	@Override
	public AlbumController.AlbumViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		return new AlbumViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_album, parent, false));
	}


	@Override
	public void onBindViewHolder(@NonNull AlbumController.AlbumViewHolder holder, int position) {
		Album album = list.get(position);
		holder.titleView.setText(album.getName());

		holder.mainLayout.setOnClickListener(v -> listener.openAlbum(position));
		holder.deleteButton.setOnClickListener(v -> listener.deleteAlbum(position, holder, context));
		holder.renameButton.setOnClickListener(v -> listener.renameAlbum(position, holder, context));
		holder.overflowButton.setOnClickListener(v -> listener.showActionsOverlay(holder, context));
		holder.cancelButton.setOnClickListener(v -> listener.hideActionsOverlay(holder, context));
	}


	@Override
	public int getItemCount() {
		return list.size();
	}

	void setItemActionListener(ItemActionListener listener) {
		this.listener = listener;
	}

	void setList(List<Album> list) {
		this.list = list;
	}


	interface ItemActionListener {
		void openAlbum(int position);

		void deleteAlbum(int position, AlbumViewHolder holder, Context context);

		void renameAlbum(int position, AlbumController.AlbumViewHolder holder, Context context);

		void showActionsOverlay(AlbumViewHolder holder, Context context);

		void hideActionsOverlay(AlbumViewHolder holder, Context context);
	}


	static class AlbumViewHolder extends RecyclerView.ViewHolder {
		CardView cardView;
		TextView titleView;
		ImageButton overflowButton, deleteButton, renameButton, cancelButton;
		ConstraintLayout mainLayout;
		LinearLayout overlayLayout;

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
	}

}