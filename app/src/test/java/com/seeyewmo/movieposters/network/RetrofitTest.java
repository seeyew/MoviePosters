package com.seeyewmo.movieposters.network;

import com.seeyewmo.movieposters.dto.MoviePoster;
import com.seeyewmo.movieposters.network.api.MovieWebService;
import com.seeyewmo.movieposters.network.api.ApiSearchResponse;

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

    @Before
    public void setUp() throws Exception {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://example.com")
                .build();
        NetworkBehavior behavior = NetworkBehavior.create(new Random(2847));
        MockRetrofit mockRetrofit = new MockRetrofit.Builder(retrofit)
                .networkBehavior(behavior)
                .build();
        delegate =  mockRetrofit.create(MovieWebService.class);
        service = new DelegateWebService(delegate);
    }


    @Test
    public void testTooManyResults() throws IOException {
        String expectedString = "Too many results.";
        final ApiSearchResponse apiSearchResponse = new ApiSearchResponse(10,false);
        apiSearchResponse.setError(expectedString);
        service.setApiSearchResponse(apiSearchResponse);

        Call<ApiSearchResponse> call = service.searchPoster("s");
        Response<ApiSearchResponse> result = call.execute();

        Assert.assertTrue(result.body() != null);
        ApiSearchResponse returnedResult = result.body();
        Assert.assertEquals(expectedString, returnedResult.getError());
        Assert.assertFalse(returnedResult.isSuccessful());
    }

    @Test
    public void testGoodResults() throws IOException {
        final ApiSearchResponse apiSearchResponse = new ApiSearchResponse(10,true);
        MoviePoster[] results = new MoviePoster[] {new MoviePoster(), new MoviePoster()};
        apiSearchResponse.setMoviePosters(results);
        service.setApiSearchResponse(apiSearchResponse);

        Call<ApiSearchResponse> call = service.searchPoster("hello");
        Response<ApiSearchResponse> result = call.execute();

        Assert.assertTrue(result.body() != null);
        ApiSearchResponse returnResult = result.body();
        Assert.assertNull(returnResult.getError());
        Assert.assertTrue(returnResult.isSuccessful());

        Assert.assertEquals(10, apiSearchResponse.getTotalResults());
        Assert.assertEquals(results.length, apiSearchResponse.getMoviePosters().length);

    }

    // A delegate web service that allows Retrofit to call and return the results we want
    private static class DelegateWebService implements MovieWebService {
        BehaviorDelegate<MovieWebService> delegate;
        ApiSearchResponse apiSearchResponse;

        DelegateWebService(BehaviorDelegate<MovieWebService> delegate) {
            this.delegate = delegate;
        }

        @Override
        public Call<ApiSearchResponse> searchPoster(String term) {
            Call<ApiSearchResponse> response = Calls.response(apiSearchResponse);
            return delegate.returning(response).searchPoster(term);
        }

        void setApiSearchResponse(ApiSearchResponse apiSearchResponse) {
            this.apiSearchResponse = apiSearchResponse;
        }
    }
}
