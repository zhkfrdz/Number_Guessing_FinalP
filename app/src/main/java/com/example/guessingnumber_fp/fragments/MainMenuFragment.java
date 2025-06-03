package com.example.guessingnumber_fp.fragments;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.guessingnumber_fp.R;
import com.google.android.material.button.MaterialButton;
import com.example.guessingnumber_fp.activities.MainActivity;
import com.example.guessingnumber_fp.activities.HighscoresActivity;
import com.example.guessingnumber_fp.activities.StatsActivity;
import com.example.guessingnumber_fp.activities.SettingsActivity;
import com.example.guessingnumber_fp.activities.HelpActivity;

public class MainMenuFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_menu, container, false);
        MaterialButton btnPlay = view.findViewById(R.id.btnPlay);
        MaterialButton btnHighscores = view.findViewById(R.id.btnHighscores);
        MaterialButton btnStats = view.findViewById(R.id.btnStats);
        MaterialButton btnSettings = view.findViewById(R.id.btnSettings);
        MaterialButton btnHelp = view.findViewById(R.id.btnHelp);
        MaterialButton btnQuit = view.findViewById(R.id.btnQuit);

        btnPlay.setOnClickListener(v -> {
            MediaPlayer player = MediaPlayer.create(getActivity(), R.raw.cat_buttons);
            if (player != null) {
                player.setOnCompletionListener(MediaPlayer::release);
                player.start();
            }
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).showSelectDifficultyFragment();
            }
        });
        btnHighscores.setOnClickListener(v -> {
            MediaPlayer player = MediaPlayer.create(getActivity(), R.raw.cat_buttons);
            if (player != null) {
                player.setOnCompletionListener(MediaPlayer::release);
                player.start();
            }
            startActivity(new Intent(getActivity(), HighscoresActivity.class));
        });
        btnStats.setOnClickListener(v -> {
            MediaPlayer player = MediaPlayer.create(getActivity(), R.raw.cat_buttons);
            if (player != null) {
                player.setOnCompletionListener(MediaPlayer::release);
                player.start();
            }
            startActivity(new Intent(getActivity(), StatsActivity.class));
        });
        btnSettings.setOnClickListener(v -> {
            MediaPlayer player = MediaPlayer.create(getActivity(), R.raw.cat_buttons);
            if (player != null) {
                player.setOnCompletionListener(MediaPlayer::release);
                player.start();
            }
            startActivity(new Intent(getActivity(), SettingsActivity.class));
        });
        btnHelp.setOnClickListener(v -> {
            MediaPlayer player = MediaPlayer.create(getActivity(), R.raw.cat_buttons);
            if (player != null) {
                player.setOnCompletionListener(MediaPlayer::release);
                player.start();
            }
            startActivity(new Intent(getActivity(), HelpActivity.class));
        });
        btnQuit.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).showQuitDialog();
            }
        });
        return view;
    }
} 