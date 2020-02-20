package nodopezzz.android.wishlist.Fragments;

import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import nodopezzz.android.wishlist.Adapters.EpisodeListAdapter;
import nodopezzz.android.wishlist.Models.Episode;
import nodopezzz.android.wishlist.R;
import nodopezzz.android.wishlist.APIs.TMDBAPI;

public class EpisodeListDialogFragment extends DialogFragment {

    public static final String ARG_SEASON_ID = "ARG_SEASON_ID";
    public static final String ARG_NUMBER_OF_SEASON = "ARG_SEASON_OF_NUMBER";
    public static final String ARG_SEASON_TITLE = "ARG_TITLE";

    public static EpisodeListDialogFragment newInstance(String title, String seasonId, String numberOfEpisode){
        EpisodeListDialogFragment fragment = new EpisodeListDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_SEASON_ID, seasonId);
        bundle.putString(ARG_NUMBER_OF_SEASON, numberOfEpisode);
        bundle.putString(ARG_SEASON_TITLE, title);
        fragment.setArguments(bundle);
        return fragment;
    }

    private String mTitle;
    private String mId;
    private String mSeasonNumber;

    private TextView mTitleView;
    private ProgressBar mProgressBar;
    private RecyclerView mEpisodeList;
    private TextView mNothingView;

    private List<Episode> mEpisodes;
    private EpisodeListAdapter mEpisodeAdapter;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_fragment_episode_list, null);

        Bundle args = getArguments();
        mTitle = args.getString(ARG_SEASON_TITLE);
        mId = args.getString(ARG_SEASON_ID);
        mSeasonNumber = args.getString(ARG_NUMBER_OF_SEASON);

        mTitleView = v.findViewById(R.id.episode_list_title);
        mProgressBar = v.findViewById(R.id.episode_list_progressbar);
        mEpisodeList = v.findViewById(R.id.episode_list);
        mNothingView = v.findViewById(R.id.episode_list_nothing);

        mTitleView.setText(mTitle);

        new GetEpisodes().execute();

        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .create();
    }

    private class GetEpisodes extends AsyncTask<Void, Void, List<Episode>>{

        @Override
        protected List<Episode> doInBackground(Void... voids) {
            return TMDBAPI.getEpisodes(mId, mSeasonNumber);
        }

        @Override
        protected void onPostExecute(List<Episode> episodes) {
            mEpisodes = episodes;
            updateUI();
        }
    }

    private void updateUI(){

        LinearLayoutManager linearLayoutManager =
                new LinearLayoutManager(getActivity());
        mEpisodeAdapter = new EpisodeListAdapter(getActivity(), this, mEpisodes);
        mEpisodeList.setLayoutManager(linearLayoutManager);
        mEpisodeList.setAdapter(mEpisodeAdapter);

        mProgressBar.setVisibility(View.GONE);
        if(mEpisodes == null || mEpisodes.size() == 0){
            mNothingView.setVisibility(View.VISIBLE);
        } else{
            mEpisodeList.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(mEpisodeAdapter != null){
            mEpisodeAdapter.clear();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mEpisodeAdapter != null){
            mEpisodeAdapter.quit();
        }
    }
}
