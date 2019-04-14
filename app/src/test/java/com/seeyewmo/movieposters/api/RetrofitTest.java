package com.seeyewmo.movieposters.api;

import com.seeyewmo.movieposters.dto.MoviePoster;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.mock.BehaviorDelegate;
import retrofit2.mock.Calls;
import retrofit2.mock.MockRetrofit;
import retrofit2.mock.NetworkBehavior;

public class RetrofitTest {

    private MockRetrofit mockRetrofit;
    private DelegateWebService service;
    private BehaviorDelegate<MovieWebService> delegate;
    private final NetworkBehavior behavior = NetworkBehavior.create(new Random(2847));
    private final IOException mockFailure = new IOException("Timeout!");

    @Before
    public void setUp() throws Exception {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://example.com")
                .build();
        mockRetrofit = new MockRetrofit.Builder(retrofit)
                .networkBehavior(behavior)
                .build();
        delegate =  mockRetrofit.create(MovieWebService.class);
        service = new DelegateWebService(delegate);
    }

    @After
    public void shutdown() throws Exception {
//        mockWebServer.shutdown();
    }

//    https://stackoverflow.com/questions/35748656/android-unit-test-with-retrofit2-and-mockito-or-robolectric
    @Test
    public void testTooManyResults() throws IOException {
        String expectedString = "Too many results.";
        final SearchResult searchResult = new SearchResult(10,false);
        searchResult.setError(expectedString);
        service.setSearchResult(searchResult);

        Call<SearchResult> call = service.searchPoster("s");

        Response<SearchResult> result = call.execute();
        Assert.assertTrue(result.body() != null);
        SearchResult returnedResult = result.body();
        Assert.assertEquals(expectedString, returnedResult.getError());
        Assert.assertFalse(returnedResult.isSuccessful());
    }



    @Test
    public void testGoodResults() throws IOException {
        final SearchResult searchResult = new SearchResult(10,true);
        MoviePoster[] results = new MoviePoster[] {new MoviePoster(), new MoviePoster()};
        searchResult.setMoviePosters(results);
        service.setSearchResult(searchResult);

        Call<SearchResult> call = service.searchPoster("hello");

        Response<SearchResult> result = call.execute();
        Assert.assertTrue(result.body() != null);
        SearchResult returnResult = result.body();
        Assert.assertNull(returnResult.getError());
        Assert.assertTrue(returnResult.isSuccessful());

        Assert.assertEquals(10, searchResult.getTotalResults());
        Assert.assertEquals(results.length, searchResult.getMoviePosters().length);

    }

    private static class DelegateWebService implements MovieWebService {
        BehaviorDelegate<MovieWebService> delegate;
        SearchResult searchResult;
        DelegateWebService(BehaviorDelegate<MovieWebService> delegate) {
            this.delegate = delegate;
        }

        @Override
        public Call<SearchResult> searchPoster(String term) {
            Call<SearchResult> response = Calls.response(searchResult);
            return delegate.returning(response).searchPoster(term);
        }

        void setSearchResult(SearchResult searchResult) {
            this.searchResult = searchResult;
        }
    }
}
