package com.seeyewmo.movieposters;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.widget.AutoCompleteTextView;

import com.jakewharton.espresso.OkHttp3IdlingResource;
import com.seeyewmo.movieposters.database.MoviePosterDAO;
import com.seeyewmo.movieposters.database.MoviePosterDB;
import com.seeyewmo.movieposters.di.AppComponent;
import com.seeyewmo.movieposters.di.AppModule;
import com.seeyewmo.movieposters.di.DaggerAppComponent;
import com.seeyewmo.movieposters.di.NetModule;
import com.seeyewmo.movieposters.di.RoomModule;
import com.seeyewmo.movieposters.testutils.MockServerDispatcher;
import com.seeyewmo.movieposters.testutils.StringHelpers;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.Request;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.io.InputStream;

import javax.inject.Singleton;

import androidx.annotation.Nullable;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.IdlingResource;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;
import dagger.Component;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import retrofit2.Retrofit;

import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class MainActivityTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(
            MainActivity.class, false, false);

    private String stringToBetyped;
    private MockWebServer mockWebServer;
    private IdlingResource resource;
    private MockServerDispatcher.RequestDispatcher dispatcher;

    @Before
    public void setupEnvironment() throws Exception {
        // Specify a valid string.
        stringToBetyped = "Espresso";
        mockWebServer = new MockWebServer();
        mockWebServer.start(8080);
        AppComponent component =
                DaggerAppComponent.builder()
                        .appModule(new AppModule(ApplicationProvider.getApplicationContext()))
                        .netModule(new ExpressoTestModule(mockWebServer.url("")
                                .toString()))
                        .roomModule(new ExpressoRoomModule()).build();

        getApp().setComponent(component);
        component.moviePosterDAO().deleteOldMoviePosters("something");
        resource = OkHttp3IdlingResource.create("OkHttp", component.httpclient());
        IdlingRegistry.getInstance().register(resource);
        dispatcher = new MockServerDispatcher.RequestDispatcher();
        mockWebServer.setDispatcher(dispatcher);
    }

    @After
    public void shutdown() throws Exception{
        IdlingRegistry.getInstance().unregister(resource);
        mockWebServer.shutdown();
    }

    @Test
    public void testNoResults() throws IOException {
        launchNewTaskActivity();
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("movies_not_found.json");
        String notFound = StringHelpers.inputStreamToString(inputStream);//"{\"Response\":\"False\",\"Error\":\"Movie not found!\"}";
        dispatcher.addResponse("/?s=something&apikey=", 200, notFound);


        onView(withId(R.id.action_search)).perform(click());
        onView(isAssignableFrom(AutoCompleteTextView.class)).perform(typeText("something\n"));

        onView(withId(R.id.status)).check(matches(withText(getResourceString(R.string.error, "Movie not found!"))));
        onView (withId(R.id.recycler_view)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
    }

    @Test
    public void testTooManyResults() throws IOException {
        launchNewTaskActivity();
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("too_many_results.json");
        String notFound = StringHelpers.inputStreamToString(inputStream);//"{\"Response\":\"False\",\"Error\":\"Movie not found!\"}";
        dispatcher.addResponse("/?s=something&apikey=", 200, notFound);


        onView(withId(R.id.action_search)).perform(click());
        onView(isAssignableFrom(AutoCompleteTextView.class)).perform(typeText("something\n"));

        onView(withId(R.id.status)).check(matches(withText(getResourceString(R.string.error, "Too many results."))));
        onView (withId(R.id.recycler_view)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
    }

    @Test
    public void testWithResults() throws IOException {
        launchNewTaskActivity();
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("results_success.json");
        String notFound = StringHelpers.inputStreamToString(inputStream);//"{\"Response\":\"False\",\"Error\":\"Movie not found!\"}";
        dispatcher.addResponse("/?s=something&apikey=", 200, notFound);


        onView(withId(R.id.action_search)).perform(click());
        onView(isAssignableFrom(AutoCompleteTextView.class)).perform(typeText("something\n"));

        onView(withId(R.id.recycler_view)).check(matches(isDisplayed()));
        onView (withId(R.id.status)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
    }

    private void launchNewTaskActivity() {
        /*ApplicationProvider.getApplicationContext(),*/
        Intent intent = new Intent(InstrumentationRegistry.getInstrumentation().getTargetContext(),
                MainActivity.class);

        mActivityRule.launchActivity(intent);
    }


    private String getResourceString(int id, Object... formatArgs) {
        Context targetContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        return targetContext.getResources().getString(id, formatArgs);
    }

    private MoviePosterApplication getApp() {
        return (MoviePosterApplication) InstrumentationRegistry.getInstrumentation()
                .getTargetContext().getApplicationContext();
    }

    public static class ExpressoTestModule extends NetModule {

        ExpressoTestModule(String baseUrl) {
            super(baseUrl, "");
        }

        @Override
        protected OkHttpClient provideOkHttpClient(Cache cache) {
            return super.provideOkHttpClient(null);
        }

    }

    public static class ExpressoRoomModule extends RoomModule {

        public ExpressoRoomModule() {
            super();
        }

        @Override
        protected MoviePosterDB provideMoviePosterDB(Application application) {
            //Returning in memory database instead!
            return Room.inMemoryDatabaseBuilder(androidx.test.InstrumentationRegistry.getContext(),
                    MoviePosterDB.class)
                    // allowing main thread queries, just for testing
                    .allowMainThreadQueries()
                    .build();
        }

        @Override
        protected MoviePosterDAO provideMoviePosterDAO(MoviePosterDB db) {
            return super.provideMoviePosterDAO(db);
        }
    }

//    An alternative is to create a new TestApplication that takes this component
//    Modify the test runner to run using our TestApplication (or create a new AndroidManifest.xml).
//    This allows us to inject the dependency into this file
//    @Singleton
//    @Component(modules={AppModule.class, ExpressoTestModule.class})
//    public interface EspressoTestComponent {
//        void inject(MainActivity activity);
//        //void inject(MainActivityTest activity);
//        // void inject(MyService service);
//    }
}
