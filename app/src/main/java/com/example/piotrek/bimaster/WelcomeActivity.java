package com.example.piotrek.bimaster;

import android.animation.ArgbEvaluator;
import android.annotation.TargetApi;
import android.graphics.Color;
import android.media.Image;
import android.os.Build;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.piotrek.bimaster.utils.Utils;

public class WelcomeActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    Button SkipButton, FinishButton, NextButton;
    ImageView first_i,middle_i,last_i;
    ImageView[] indicators;
    int lastValue = 0;
    CoordinatorLayout coordinator;
    int page = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_welcome);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        //Controls assignment
        NextButton = (Button) findViewById(R.id.intro_btn_next);
        SkipButton = (Button) findViewById(R.id.intro_btn_skip);
        FinishButton = (Button) findViewById(R.id.intro_btn_finish);
        first_i = (ImageView) findViewById(R.id.intro_indicator_0);
        middle_i = (ImageView) findViewById(R.id.intro_indicator_1);
        last_i = (ImageView) findViewById(R.id.intro_indicator_2);
        coordinator = (CoordinatorLayout) findViewById(R.id.main_content);
        indicators = new ImageView[]{first_i, middle_i, last_i};

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setCurrentItem(page);
        updateIndicators(page);

        final int color1 = ContextCompat.getColor(this, R.color.colorPrimaryDark);
        final int color2 = ContextCompat.getColor(this, R.color.colorAccent);
        final int color3 = ContextCompat.getColor(this, R.color.colorRed);
        final int[] colorList = new int[]{color1, color2, color3};
        final ArgbEvaluator ev = new ArgbEvaluator();

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                int colorUp = (Integer) ev.evaluate(positionOffset, colorList[position], colorList[position == 2 ? position : position+1]);
                mViewPager.setBackgroundColor(colorUp);
                changeStatusBar(colorUp);
            }

            @Override
            public void onPageSelected(int position) {
                page = position;
                updateIndicators(page);

                if (position == 0)
                {
                    changeStatusBar(color1);
                    mViewPager.setBackgroundColor(color1);
                    NextButton.setVisibility(View.VISIBLE);
                    FinishButton.setVisibility(View.GONE);
                }
                else if (position == 1)
                {
                    changeStatusBar(color2);
                    mViewPager.setBackgroundColor(color2);
                    NextButton.setVisibility(View.VISIBLE);
                    FinishButton.setVisibility(View.GONE);
                }
                else
                {
                    changeStatusBar(color3);
                    mViewPager.setBackgroundColor(color3);
                    NextButton.setVisibility(View.GONE);
                    FinishButton.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        NextButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                page += 1;
                mViewPager.setCurrentItem(page,true);
            }
        });

        SkipButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                finish();
                Utils.saveSP(WelcomeActivity.this, "firstTime", "false");
            }
        });

        FinishButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                finish();
                Utils.saveSP(WelcomeActivity.this, "firstTime", "false");
            }
        });

    }

    @TargetApi(21)
    public void changeStatusBar(int color)
    {
        Window window = getWindow();

        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(color);
    }

    public void updateIndicators(int position)
    {
        for (int i=0; i<indicators.length; i++)
        {
            indicators[i].setBackgroundResource(
                    i == position ? R.drawable.indicator_selected : R.drawable.indicator_unselected
            );
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_welcome, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        ImageView fragImage;
        int [] backgrounds = new int[] {R.drawable.screen_one, R.drawable.screen_two, R.drawable.screen_three};
        String[] titles = new String[] {"Witaj w BIMaster", "Dziel się uwagami", "Reaguj na zmiany"};
        String [] descriptions = new String[]{"Mobilnym narzędziu wspierającym analizę danych w Twojej organizacji ", "Wysyłając wiadomości z wynikami do wybranych współpracowników", "Dzięki powiadomieniom o nowych raportach i personalizowanym alertom"};
        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_welcome, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            TextView textDesc = (TextView)rootView.findViewById(R.id.description_label);
            textView.setText(titles[getArguments().getInt(ARG_SECTION_NUMBER)-1]);
            textDesc.setText(descriptions[getArguments().getInt(ARG_SECTION_NUMBER)-1]);
            fragImage = (ImageView) rootView.findViewById(R.id.section_img);
            fragImage.setBackgroundResource(backgrounds[getArguments().getInt(ARG_SECTION_NUMBER)-1]);
            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "SECTION 1";
                case 1:
                    return "SECTION 2";
                case 2:
                    return "SECTION 3";
            }
            return null;
        }
    }
}
