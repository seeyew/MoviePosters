package com.seeyewmo.movieposters.api;

import com.seeyewmo.movieposters.MainActivity;
import com.seeyewmo.movieposters.MoviePosterApplication;
import com.seeyewmo.movieposters.di.AppComponent;
import com.seeyewmo.movieposters.di.NetModule;
import com.seeyewmo.movieposters.dto.MoviePoster;
import com.seeyewmo.movieposters.utils.StringHelpers;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;

import javax.inject.Inject;
import javax.inject.Singleton;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import dagger.Component;
import okhttp3.Cache;
import okhttp3.Dispatcher;
import okhttp3.OkHttpClient;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.mock.BehaviorDelegate;
import retrofit2.mock.MockRetrofit;
import retrofit2.mock.NetworkBehavior;

import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class NetworkDataSourceTest {

    @Rule
    public TestRule rule = new InstantTaskExecutorRule();

    private MockWebServer mockWebServer;
    @Inject
    private Retrofit retrofit;
    private NetworkDataSource datasource;
    private final NetworkBehavior behavior = NetworkBehavior.create(new Random(2847));

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mockWebServer = new MockWebServer();
        mockWebServer.start();
        TestModule netModule = new TestModule(mockWebServer.url("").toString());
        retrofit = netModule.provideRetrofit(netModule.provideOkHttpClient(null));
        datasource = new NetworkDataSource(retrofit);
    }

    @After
    public void shutdown() throws Exception {
        mockWebServer.shutdown();
    }

    @Test
    public void testTooManyResults() throws Exception {
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("too_many_results.json");
        mockWebServer.enqueue(new MockResponse().setBody(StringHelpers.inputStreamToString(inputStream)));

        CountDownLatch latch = new CountDownLatch(1);
        datasource.searchWithCallback("1", new NetworkCallback<SearchResponse>() {
            @Override
            public void onResponse(SearchResponse data) {
                Assert.assertEquals("1", data.getTerm());
                Assert.assertFalse(data.isSuccess());
                Assert.assertNull(data.getData());
                latch.countDown();
            }
        });

        latch.await();
    }

    @Test
    public void testMovieNotFound() throws Exception {
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("movies_not_found.json");
        mockWebServer.enqueue(new MockResponse().setBody(StringHelpers.inputStreamToString(inputStream)));

        CountDownLatch latch = new CountDownLatch(1);
        datasource.searchWithCallback("1", new NetworkCallback<SearchResponse>() {
            @Override
            public void onResponse(SearchResponse data) {
                Assert.assertEquals("1", data.getTerm());
                Assert.assertFalse(data.isSuccess());
                Assert.assertNull(data.getData());
                latch.countDown();
            }
        });

        latch.await();
    }

    @Test
    public void testNetworkFailure() throws Exception {
       // TODO

    }

    @Test
    public void testGoodResults() throws Exception {
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("results_success.json");
        mockWebServer.enqueue(new MockResponse().setBody(StringHelpers.inputStreamToString(inputStream)));

        CountDownLatch latch = new CountDownLatch(1);
        datasource.searchWithCallback("1", data -> {
                Assert.assertEquals("1", data.getTerm());
                Assert.assertTrue(data.isSuccess());
                Assert.assertNotNull(data.getData());
                Assert.assertEquals(10, data.getData().getMoviePosters().length);
                latch.countDown();
        });

        latch.await();

    }

    private static class TestModule extends NetModule {

        TestModule(String baseUrl) {
            super(baseUrl, "");
        }

        @Override
        protected Retrofit provideRetrofit(OkHttpClient okHttpClient) {
            return super.provideRetrofit(okHttpClient);
        }

        @Override
        protected OkHttpClient provideOkHttpClient(Cache cache) {
            return super.provideOkHttpClient(cache);
        }
    }
}
