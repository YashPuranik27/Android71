package com.example.android71;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PhotoController extends RecyclerView.Adapter<PhotoController.PhotoViewHolder> {

    private Context context;
    private List<Photo> photoList;
    private PhotoActionListener listener;

    @NonNull
    @Override
    public PhotoController.PhotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View itemView = LayoutInflater.from(context).inflate(R.layout.card_view_thumbnail, parent, false);
        return new PhotoViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoViewHolder holder, int position) {
        Photo photo = photoList.get(position);
        holder.photoDetailView.setText(photo.getFileName());
        holder.imageView.setImageBitmap(photo.getPersistence().getBitmap());
        holder.layout.setOnClickListener(v -> listener.openPhoto(position));
        holder.cancelButton.setOnClickListener(v -> listener.hideActionsOverlay(holder, context));
        holder.overflowButton.setOnClickListener(v-> listener.showActionsOverlay(holder, context));
        holder.removeButton.setOnClickListener(v-> listener.removePhoto(position, holder, context));

    }

    void setActionListener(PhotoActionListener listener){
        this.listener = listener;
    }

    interface PhotoActionListener{
        void removePhoto(int position, PhotoViewHolder holder, Context context);
        void openPhoto(int position);
        void showActionsOverlay(PhotoViewHolder holder, Context context);
        void hideActionsOverlay(PhotoViewHolder holder, Context context);
    }

    @Override
    public int getItemCount() {
        return photoList.size();
    }

    void setPhotoList(List<Photo> photoList){ this.photoList = photoList; }

    static class PhotoViewHolder extends RecyclerView.ViewHolder{
        CardView cardView;
        TextView photoDetailView;
        ImageView imageView;
        ConstraintLayout layout;
        LinearLayout overlayLayout;
        ImageButton overflowButton, removeButton, cancelButton;

        public PhotoViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.photo_card_view);
            layout = itemView.findViewById(R.id.photo_card_layout);
            photoDetailView = itemView.findViewById(R.id.photo_card_text_details);
            imageView = itemView.findViewById(R.id.photo_card_image_view);
            overlayLayout = itemView.findViewById(R.id.photo_card_overlay);
            overflowButton = itemView.findViewById(R.id.photo_card_overflow_button);
            removeButton = itemView.findViewById(R.id.photo_card_button_remove);
            cancelButton = itemView.findViewById(R.id.photo_card_button_cancel);
        }
    }
}
