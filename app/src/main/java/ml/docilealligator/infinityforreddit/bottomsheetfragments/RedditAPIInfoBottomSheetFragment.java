package ml.docilealligator.infinityforreddit.bottomsheetfragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import ml.docilealligator.infinityforreddit.R;
import ml.docilealligator.infinityforreddit.activities.MainActivity;
import ml.docilealligator.infinityforreddit.customviews.LandscapeExpandedRoundedBottomSheetDialogFragment;
import ml.docilealligator.infinityforreddit.databinding.FragmentRedditApiInfoBottomSheetBinding;
import ml.docilealligator.infinityforreddit.utils.Utils;

public class RedditAPIInfoBottomSheetFragment extends LandscapeExpandedRoundedBottomSheetDialogFragment {

    private MainActivity mainActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        FragmentRedditApiInfoBottomSheetBinding binding = FragmentRedditApiInfoBottomSheetBinding.inflate(inflater, container, false);

        if (mainActivity != null && mainActivity.typeface != null) {
            Utils.setFontToAllTextViews(binding.getRoot(), mainActivity.typeface);
        }

        binding.getRoot().setNestedScrollingEnabled(true);

        String message = "This is Omega for Reddit, a fork of Infinity for Reddit that fixes some of its problems including support for gifs in comments. This apk uses a free API key, so you won't really be able to browse Reddit with it.";
        binding.messageTextViewRedditApiInfoBottomSheetFragment.setText(message);

        binding.doNotShowThisAgainTextView.setOnClickListener(view -> {
            binding.doNotShowThisAgainCheckBox.toggle();
        });

        binding.continueButtonRedditApiInfoBottomSheetFragment.setOnClickListener(view -> {
            if (binding.doNotShowThisAgainCheckBox.isChecked()) {
                mainActivity.doNotShowRedditAPIInfoAgain();
            }
            dismiss();
        });

        return binding.getRoot();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity) context;
    }
}