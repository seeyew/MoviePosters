package com.seeyewmo.movieposters;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;
import retrofit2.Retrofit;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class MainActivityTest {

    /**
     * {@link IntentsTestRule} is an {@link ActivityTestRule} which inits and releases Espresso
     * Intents before and after each test run.
     */
    @Rule
    public IntentsTestRule<MainActivity> mShowResultTestRule =
            new IntentsTestRule<>(MainActivity.class);

    private String stringToBetyped;



    @Before
    public void registerIdlingResource() {
        // Specify a valid string.
        stringToBetyped = "Espresso";
    }

    @Test
    public void no_results() {

    }
}
