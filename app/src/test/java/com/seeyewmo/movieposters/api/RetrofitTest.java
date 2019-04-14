package com.seeyewmo.movieposters.api;

import com.seeyewmo.movieposters.MoviePosterApplication;
import com.seeyewmo.movieposters.api.MovieWebService;
import com.seeyewmo.movieposters.api.SearchResult;
import com.seeyewmo.movieposters.utils.StringHelpers;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import okhttp3.Cache;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

public class RetrofitTest {
    private MockWebServer mockWebServer;
    private Retrofit retrofit;
    private MovieWebService service;

    @Mock
    Cache cache;

    @Before
    public void setUp() throws Exception {
//        MockitoAnnotations.initMocks(this);
        mockWebServer = new MockWebServer();
        retrofit = MoviePosterApplication.buildRetrofit(MoviePosterApplication.getOkhttpClient(cache),
                mockWebServer.url("").toString());
        service = retrofit.create(MovieWebService.class);
    }

    @After
    public void shutdown() throws Exception {
        mockWebServer.shutdown();
    }

//    https://stackoverflow.com/questions/35748656/android-unit-test-with-retrofit2-and-mockito-or-robolectric
    @Test
    public void testTooManyResults() throws IOException {
        String expectedString = "Too many results.";

        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("too_many_results.json");
        mockWebServer.enqueue(new MockResponse().setBody(StringHelpers.inputStreamToString(inputStream)));

        Call<SearchResult> call = service.searchPoster("s");

        Response<SearchResult> result = call.execute();
        Assert.assertTrue(result.body() != null);
        SearchResult searchResult = result.body();
        Assert.assertEquals(expectedString, searchResult.getError());
        Assert.assertFalse(searchResult.isSuccessful());
    }

    @Test
    public void testGoodResults() throws IOException {
        String expectedString = "Too many results.";

        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("results_success.json");
        mockWebServer.enqueue(new MockResponse().setBody(StringHelpers.inputStreamToString(inputStream)));

        Call<SearchResult> call = service.searchPoster("hello");

        Response<SearchResult> result = call.execute();
        Assert.assertTrue(result.body() != null);
        SearchResult searchResult = result.body();
        Assert.assertNull(searchResult.getError());
        Assert.assertTrue(searchResult.isSuccessful());

        Assert.assertEquals(544, searchResult.getTotalResults());
        Assert.assertEquals(10, searchResult.getMoviePosters().length);

    }
}
