package nodopezzz.android.wishlist.Fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import nodopezzz.android.wishlist.Utils.GeneralSingleton;
import nodopezzz.android.wishlist.R;

public class EpisodeDialogFragment extends DialogFragment {

    private static final String ARG_TITLE = "ARG_TITLE";
    private static final String ARG_URL_IMAGE = "ARG_URL_IMAGE";
    private static final String ARG_OVERVIEW = "ARG_OVERVIEW";

    public static EpisodeDialogFragment newInstance(String title, String urlImage, String overview){
        EpisodeDialogFragment fragment = new EpisodeDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_OVERVIEW, overview);
        args.putString(ARG_TITLE, title);
        args.putString(ARG_URL_IMAGE, urlImage);
        fragment.setArguments(args);
        return fragment;
    }

    private TextView mTitleView;
    private ImageView mImageView;
    private TextView mOverviewView;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_episode, null);

        mTitleView = v.findViewById(R.id.episode_title);
        mImageView = v.findViewById(R.id.episode_image);
        mOverviewView = v.findViewById(R.id.episode_overview);

        Bundle args = getArguments();
        String title = args.getString(ARG_TITLE);
        String imageUrl = args.getString(ARG_URL_IMAGE);
        String overview = args.getString(ARG_OVERVIEW);

        mTitleView.setText(title);
        mOverviewView.setText(overview);
        mImageView.setImageResource(R.drawable.placeholder_image_not_found);
        if(GeneralSingleton.getInstance().getIconCache().getBitmapFromMemory(imageUrl) != null){
            mImageView.setImageBitmap(GeneralSingleton.getInstance().getIconCache().getBitmapFromMemory(imageUrl));
        }

        return new AlertDialog.Builder(getActivity()).setView(v).create();
    }
}
