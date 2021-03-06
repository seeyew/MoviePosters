package com.seeyewmo.movieposters.network;

import com.seeyewmo.movieposters.di.modules.NetModule;
import com.seeyewmo.movieposters.testutils.HandlerThreadExecutor;
import com.seeyewmo.movieposters.testutils.MockServerDispatcher;
import com.seeyewmo.movieposters.testutils.StringHelpers;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.android.util.concurrent.RoboExecutorService;

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;
import java.util.concurrent.Executor;

import javax.inject.Inject;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.mock.NetworkBehavior;

@RunWith(RobolectricTestRunner.class)
public class NetworkDataSourceTest {

    @Rule
    public TestRule rule = new InstantTaskExecutorRule();

    @Inject
    private Retrofit retrofit;

    private MockWebServer mockWebServer;
    private NetworkDataSource datasource;
    private final NetworkBehavior behavior = NetworkBehavior.create(new Random(2847));


    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mockWebServer = new MockWebServer();
        mockWebServer.start();
        TestModule netModule = new TestModule(mockWebServer.url("").toString());
        retrofit = netModule.provideRetrofit(netModule.provideOkHttpClient(null));
        //TODO: We can use Dagger in the future here, but this is not necessary at the moment
        datasource = new NetworkDataSource(retrofit);
    }

    @After
    public void shutdown() throws Exception {
        mockWebServer.shutdown();
    }

    @Test
    public void testTooManyResults() throws Exception {
        mockServerHelper("too_many_results.json");

        datasource.searchWithCallback("1", new NetworkCallback<SearchResult>() {
            @Override
            public void onResponse(SearchResult data) {
                Assert.assertEquals("1", data.getTerm());
                Assert.assertFalse(data.isSuccess());
                Assert.assertNull(data.getData());
            }
        });
        Robolectric.flushBackgroundThreadScheduler();
    }

    @Test
    public void testMovieNotFound() throws Exception {
        mockServerHelper("movies_not_found.json");

        datasource.searchWithCallback("1", new NetworkCallback<SearchResult>() {
            @Override
            public void onResponse(SearchResult data) {
                Assert.assertEquals("1", data.getTerm());
                Assert.assertFalse(data.isSuccess());
                Assert.assertNull(data.getData());
            }
        });
        Robolectric.flushBackgroundThreadScheduler();
    }

    @Test
    public void testClientError() throws Exception {
        mockWebServer.setDispatcher(new MockServerDispatcher.ErrorDispatcher());
        datasource.searchWithCallback("1", new NetworkCallback<SearchResult>() {
            @Override
            public void onResponse(SearchResult data) {
                Assert.assertFalse(data.isSuccess());
                Assert.assertNull(data.getData());
                Assert.assertEquals("Client Error", data.getError());
            }
        });
        Robolectric.flushBackgroundThreadScheduler();
    }

    @Test
    public void testNetworkFailure() throws Exception {
        //Shut down the server to simulate connection error!
        mockWebServer.shutdown();

        datasource.searchWithCallback("1", new NetworkCallback<SearchResult>() {
            @Override
            public void onResponse(SearchResult data) {
                Assert.assertFalse(data.isSuccess());
                Assert.assertNull(data.getData());
            }
        });
        Robolectric.flushBackgroundThreadScheduler();
    }

    @Test
    public void testGoodResults() throws Exception {
        mockServerHelper("results_success.json");

        datasource.searchWithCallback("1", data -> {
                Assert.assertEquals("1", data.getTerm());
                Assert.assertTrue(data.isSuccess());
                Assert.assertNotNull(data.getData());
                Assert.assertEquals(10, data.getData().getMoviePosters().length);
        });
        Robolectric.flushBackgroundThreadScheduler();
    }

    private void mockServerHelper(String resultFilePath) throws IOException {
        InputStream inputStream = this.getClass().getClassLoader()
                .getResourceAsStream(resultFilePath);
        mockWebServer.enqueue(new MockResponse().setBody(
                StringHelpers.inputStreamToString(inputStream)));
    }

    private static class TestModule extends NetModule {

        TestModule(String baseUrl) {
            super(baseUrl, "");
        }

        @Override
        protected Retrofit provideRetrofit(OkHttpClient okHttpClient) {
            Retrofit retrofit =  new Retrofit.Builder()
                    .baseUrl(mBaseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .callbackExecutor(new RoboExecutorService())
                    .client(okHttpClient) // OkHttp auto retires on connections issues anyway.
                    .build();
            return retrofit;
        }

        @Override
        protected OkHttpClient provideOkHttpClient(Cache cache) {
            return super.provideOkHttpClient(cache);
        }

        @Override
        protected Executor providesExecutor() {
            return new HandlerThreadExecutor(null);
        }
    }
}
