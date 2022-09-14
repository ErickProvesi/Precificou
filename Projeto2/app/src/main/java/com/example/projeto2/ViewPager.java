package com.example.projeto2;

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
    private ViewPager2 vpgViewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pager);

        vpgViewPager = findViewById(R.id.vpgViewPager);
        pagerAdapter = new ViewPager.ScreenSliderPageAdapter(this);
        vpgViewPager.setAdapter(pagerAdapter);
    }

    private class ScreenSliderPageAdapter extends FragmentStateAdapter {

        public ScreenSliderPageAdapter(ViewPager viewPager) {
            super(viewPager);

        }

        @NonNull
        @NotNull
        @Override
        public Fragment createFragment(int position) {
            int op = 0;
            switch(position){

                case 0:
                    op = 0;
                    break;
                case 1:
                    op = 1;
                    break;
                case 2:
                    op = 2;
                    break;
                default:
                    return null;
            }
            if(op == 0){
                return new FragmentoPerfil();

            }else if (op == 1){
                return new FragmentoProduto();

            }else {
                return new FragmentoMeusIngredientes();
            }
        }

        @Override
        public int getItemCount() {
            return Num_Pages;
        }
    }
}