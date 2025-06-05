package com.example.guessingnumber_fp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.guessingnumber_fp.R;
import com.example.guessingnumber_fp.activities.MainActivity;
import androidx.core.app.ActivityOptionsCompat;
import android.media.MediaPlayer;
import com.example.guessingnumber_fp.activities.SoundManager;

public class SelectDifficultyFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_select_difficulty, container, false);
        ImageButton btnBack = view.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            if (getActivity() != null) {
                SoundManager.playSound(getActivity(), R.raw.cat_back_btn);
            }
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).showMainMenuFragment();
            }
        });

        View cardEasy = view.findViewById(R.id.cardEasy);
        View cardMedium = view.findViewById(R.id.cardMedium);
        View cardHard = view.findViewById(R.id.cardHard);
        View cardImpossible = view.findViewById(R.id.cardImpossible);

        // Sequential slide-in-left animation for cards
        View[] cards = {cardEasy, cardMedium, cardHard, cardImpossible};
        for (int i = 0; i < cards.length; i++) {
            View card = cards[i];
            card.setAlpha(0f);
            int delay = i * 120; // 120ms delay between cards
            card.postDelayed(() -> {
                card.setAlpha(1f);
                android.view.animation.Animation slideIn = android.view.animation.AnimationUtils.loadAnimation(getContext(), R.anim.slide_in_left);
                card.startAnimation(slideIn);
            }, delay);
        }

        cardEasy.setOnClickListener(v -> {
            SoundManager.playSound(getActivity(), R.raw.cat_buttons);
            startGame("easy", 10);
        });
        cardMedium.setOnClickListener(v -> {
            SoundManager.playSound(getActivity(), R.raw.cat_buttons);
            startGame("medium", 30);
        });
        cardHard.setOnClickListener(v -> {
            SoundManager.playSound(getActivity(), R.raw.cat_buttons);
            startGame("hard", 50);
        });
        cardImpossible.setOnClickListener(v -> {
            SoundManager.playSound(getActivity(), R.raw.cat_buttons);
            startGame("impossible", 100);
        });

        return view;
    }

    private void startGame(String difficulty, int max) {
        if (getActivity() == null) return;
        android.content.Intent intent = new android.content.Intent(getActivity(), com.example.guessingnumber_fp.activities.PlayActivity.class);
        intent.putExtra("difficulty", difficulty);
        intent.putExtra("max", max);
        ActivityOptionsCompat options = ActivityOptionsCompat.makeCustomAnimation(getActivity(), android.R.anim.fade_in, android.R.anim.fade_out);
        startActivity(intent, options.toBundle());
    }
} 