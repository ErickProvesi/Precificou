package com.erickprovesi.precificou;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;

import org.jetbrains.annotations.NotNull;

public class ViewPager extends FragmentActivity {

    private static final int Num_Pages=3;
    private FragmentStateAdapter pagerAdapter;
    public static ViewPager2 vpgViewPager;
    public static int count=1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pager);

        vpgViewPager = findViewById(R.id.vpgViewPager);
        pagerAdapter = new ViewPager.ScreenSliderPageAdapter(this);
        vpgViewPager.setAdapter(pagerAdapter);
        vpgViewPager.setCurrentItem(count, false);
    }

    private class ScreenSliderPageAdapter extends FragmentStateAdapter {

        public ScreenSliderPageAdapter(ViewPager viewPager) {
            super(viewPager);

        }

        @NonNull
        @NotNull
        @Override
        public Fragment createFragment(int position) {
            switch(position){

                case 0:
                    return new FragmentoPerfil();
                case 1:
                    return new FragmentoProduto();
                case 2:
                    return new FragmentoMeusIngredientes();
                default:
                    return null;
            }
        }

        @Override
        public int getItemCount() {
            return Num_Pages;
        }
    }
}