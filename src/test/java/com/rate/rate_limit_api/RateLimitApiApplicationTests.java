package com.rate.rate_limit_api;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

@SpringBootTest
class RateLimitApiApplicationTests {


    @Test
    void contextLoads() throws IOException {

        for(int i=0; i< 200; i++){
        HttpUriRequest request = new HttpGet( "http://localhost:8080/user/2");

        // When
        HttpResponse httpResponse = HttpClientBuilder.create().build().execute( request );

            System.out.println(httpResponse.getStatusLine().getStatusCode()+" .................. ");
        }

//        // Then
//        assertThat(
//                httpResponse.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_NOT_FOUND));
    }

}
