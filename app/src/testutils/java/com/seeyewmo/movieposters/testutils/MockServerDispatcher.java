package com.seeyewmo.movieposters.testutils;

import java.util.HashMap;
import java.util.Map;

import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.RecordedRequest;

public class MockServerDispatcher {
    /**
     * Return ok response from mock server
     */
    public static class RequestDispatcher extends Dispatcher {
        String result;
        Map<String, MockResponse> mapResponses = new HashMap<>();

        public RequestDispatcher() {
        }

        public void addResponse(String path, int code, String body) {
            mapResponses.put(path, new MockResponse().setResponseCode(code).setBody(body));
        }

        @Override
        public MockResponse dispatch(RecordedRequest request) {
            String path = request.getPath();
            if (mapResponses.containsKey(path)) {
                return mapResponses.get(path);
            }

//                if(request.getPath().equals("api/data")){
//                    return new MockResponse().setResponseCode(200).setBody("{data:FakeData}");
//                }else if(request.getPath().equals("api/codes")){
//                    return new MockResponse().setResponseCode(200).setBody("{codes:FakeCode}");
//                }else if(request.getPath().equals("api/number"))
//                    return new MockResponse().setResponseCode(200).setBody("number:FakeNumber");

            return new MockResponse().setResponseCode(200).setBody(result);
//                return new MockResponse().setResponseCode(404);
        }
    }

    /**
     * Return error response from mock server
     */
    public static class ErrorDispatcher extends Dispatcher {

        @Override
        public MockResponse dispatch(RecordedRequest request) {

            return new MockResponse().setResponseCode(400);

        }
    }
}
