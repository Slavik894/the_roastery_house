package com.example.theroasteryhouse.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.theroasteryhouse.database.DatabaseHelper;
import com.example.theroasteryhouse.databinding.FragmentAdminFinancesBinding;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AdminFinancesFragment extends Fragment {
    private FragmentAdminFinancesBinding binding;
    private DatabaseHelper db;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAdminFinancesBinding.inflate(inflater, container, false);
        db = new DatabaseHelper(requireContext());

        loadStatistics();

        return binding.getRoot();
    }

    private void loadStatistics() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        String startToday = sdf.format(calendar.getTime());

        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        String endToday = sdf.format(calendar.getTime());

        int countTodaySpecial = db.getOrdersCount(startToday, endToday, true);
        int countTodayStandard = db.getOrdersCount(startToday, endToday, false);
        int countTodayTotal = countTodaySpecial + countTodayStandard;
        double revenueToday = db.getTotalRevenue(startToday, endToday);

        binding.orderNumber.setText(String.valueOf(countTodayTotal));
        binding.specialOrderAmount.setText(String.valueOf(countTodaySpecial));
        binding.standardOrderAmount.setText(String.valueOf(countTodayStandard));
        binding.todayIncomeAmount.setText(String.format("%.2f zł", revenueToday));

        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        String startMonth = sdf.format(calendar.getTime());

        calendar.add(Calendar.MONTH, 1);
        calendar.add(Calendar.SECOND, -1);
        String endMonth = sdf.format(calendar.getTime());

        int countMonthSpecial = db.getOrdersCount(startMonth, endMonth, true);
        int countMonthStandard = db.getOrdersCount(startMonth, endMonth, false);
        int countMonthTotal = countMonthSpecial + countMonthStandard;
        double revenueMonth = db.getTotalRevenue(startMonth, endMonth);

        binding.monthlyOrdersAmount.setText(String.valueOf(countMonthTotal));
        binding.monthlySpecialOrdersAmount.setText(String.valueOf(countMonthSpecial));
        binding.monthlyStandardOrdersAmount.setText(String.valueOf(countMonthStandard));
        binding.monthlyIncomeAmount.setText(String.format("%.2f zł", revenueMonth));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}