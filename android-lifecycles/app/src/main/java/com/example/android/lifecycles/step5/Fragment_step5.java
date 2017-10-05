/*
 * Copyright 2017, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.lifecycles.step5;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import com.example.android.codelabs.lifecycle.R;

/**
 * Shows a SeekBar that should be synced with a value in a ViewModel.
 */
public class Fragment_step5 extends Fragment {

  private SeekBar seekBar;

  private SeekBarViewModel seekBarViewModel;

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View root = inflater.inflate(R.layout.fragment_step5, container, false);
    seekBar = root.findViewById(R.id.seekBar);

    // TODO: get ViewModel
    seekBarViewModel = ViewModelProviders.of(getActivity()).get(SeekBarViewModel.class);
    subscribeSeekBar();

    return root;
  }

  private void subscribeSeekBar() {

    // Update the ViewModel when the SeekBar is changed.

    seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
      @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        // TODO: Set the ViewModel's value when the change comes from the user.
        if (fromUser) {
          seekBarViewModel.seekbarValue.setValue(progress);
          Log.i("seekBarViewModel", "progress is changing");
        }

      }

      @Override public void onStartTrackingTouch(SeekBar seekBar) {
      }

      @Override public void onStopTrackingTouch(SeekBar seekBar) {
      }
    });

    // TODO: Update the SeekBar when the ViewModel is changed.
    // seekBarViewModel.seekbarValue.observe(...
    seekBarViewModel.seekbarValue.observe(getActivity(), new Observer<Integer>() {
      @Override public void onChanged(@Nullable Integer integer) {
        seekBar.setProgress(integer);
      }
    });
  }
}
