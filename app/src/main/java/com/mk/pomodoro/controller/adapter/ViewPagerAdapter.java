package com.mk.pomodoro.controller.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.mk.pomodoro.ui.HomeFragment;
import com.mk.pomodoro.ui.SettingsFragment;

public class ViewPagerAdapter extends FragmentStateAdapter {
    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            return new HomeFragment();
        } else if (position == 1) {
            return new SettingsFragment();
        } else {
            return new Fragment(); // Devuelve un fragmento vacío
        }
    }

    @Override
    public int getItemCount() {
        return 2; // Número de fragmentos que quieres mostrar en tu ViewPager
    }
}

