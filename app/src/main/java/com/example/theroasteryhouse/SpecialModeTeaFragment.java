package com.example.theroasteryhouse;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.example.theroasteryhouse.databinding.SpModeTeaFragmentBinding;

public class SpecialModeTeaFragment extends Fragment {

    private SpModeTeaFragmentBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = SpModeTeaFragmentBinding.inflate(inflater, container, false);

        // Тут пізніше додамо логіку для слайдерів і списків (Load data from DB)
        // Наприклад: setupRecyclerView(binding.spModeTeaRecyclerLeaves, "tea_leaves");

        return binding.getRoot();
    }
}