package com.jedralski.MovieRecommendationSystems.factory;

import com.jedralski.MovieRecommendationSystems.connection.HttpConnection;

public class ConnectionFactory {

    public HttpConnection build(String url) {
        return new HttpConnection(url);
    }
}
