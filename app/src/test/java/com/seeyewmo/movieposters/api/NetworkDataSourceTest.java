package com.seeyewmo.movieposters.api;

import com.seeyewmo.movieposters.di.NetModule;
import com.seeyewmo.movieposters.testutils.MockServerDispatcher;
import com.seeyewmo.movieposters.testutils.StringHelpers;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

import javax.inject.Inject;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import retrofit2.Retrofit;
import retrofit2.mock.NetworkBehavior;

import static org.mockito.Mockito.verify;

public class NetworkDataSourceTest {

    @Rule
    public TestRule rule = new InstantTaskExecutorRule();

    @Inject
    private Retrofit retrofit;

    private MockWebServer mockWebServer;
    private NetworkDataSource datasource;
    private final NetworkBehavior behavior = NetworkBehavior.create(new Random(2847));
    private MockServerDispatcher.ErrorDispatcher dispatcher;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mockWebServer = new MockWebServer();
        mockWebServer.start();
        TestModule netModule = new TestModule(mockWebServer.url("").toString());
        retrofit = netModule.provideRetrofit(netModule.provideOkHttpClient(null));
        //TODO: We can use Dagger in the future here, but this is not necessary at the moment
        datasource = new NetworkDataSource(retrofit);
        dispatcher = new MockServerDispatcher.ErrorDispatcher();
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
    public void testClientError() throws Exception {
        mockWebServer.setDispatcher(dispatcher);
        CountDownLatch latch = new CountDownLatch(1);
        datasource.searchWithCallback("1", new NetworkCallback<SearchResponse>() {
            @Override
            public void onResponse(SearchResponse data) {
                Assert.assertFalse(data.isSuccess());
                Assert.assertNull(data.getData());
                Assert.assertEquals("Client Error", data.getError());
                latch.countDown();
            }
        });

        latch.await();
    }

    @Test
    public void testNetworkFailure() throws Exception {
        //Shut down the server to simulate connection error!
        mockWebServer.shutdown();

        CountDownLatch latch = new CountDownLatch(1);
        datasource.searchWithCallback("1", new NetworkCallback<SearchResponse>() {
            @Override
            public void onResponse(SearchResponse data) {
                Assert.assertFalse(data.isSuccess());
                Assert.assertNull(data.getData());
                latch.countDown();
            }
        });

        latch.await();
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
