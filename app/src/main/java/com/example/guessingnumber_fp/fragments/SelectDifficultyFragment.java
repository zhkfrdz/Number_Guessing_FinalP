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

public class SelectDifficultyFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_select_difficulty, container, false);
        ImageButton btnBack = view.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).showMainMenuFragment();
            }
        });

        View cardEasy = view.findViewById(R.id.cardEasy);
        View cardMedium = view.findViewById(R.id.cardMedium);
        View cardHard = view.findViewById(R.id.cardHard);
        View cardImpossible = view.findViewById(R.id.cardImpossible);

        cardEasy.setOnClickListener(v -> startGame("easy", 10));
        cardMedium.setOnClickListener(v -> startGame("medium", 30));
        cardHard.setOnClickListener(v -> startGame("hard", 50));
        cardImpossible.setOnClickListener(v -> startGame("impossible", 100));

        return view;
    }

    private void startGame(String difficulty, int max) {
        if (getActivity() == null) return;
        android.content.Intent intent = new android.content.Intent(getActivity(), com.example.guessingnumber_fp.activities.PlayActivity.class);
        intent.putExtra("difficulty", difficulty);
        intent.putExtra("max", max);
        startActivity(intent);
    }
} 