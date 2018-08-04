package com.rana.sahaj.awstest;

import android.content.Context;

import com.amazonaws.mobileconnectors.appsync.AWSAppSyncClient;
import com.amazonaws.mobileconnectors.appsync.sigv4.BasicAPIKeyAuthProvider;

public class ClientFactory {

    private static volatile AWSAppSyncClient client;

    public static AWSAppSyncClient getInstance(Context context) {
        if (client == null) {
            client = AWSAppSyncClient.builder()
                    .context(context)
                    .apiKey(new BasicAPIKeyAuthProvider(Constants.APPSYNC_API_KEY)) // API Key based authorization
                    .region(Constants.APPSYNC_REGION)
                    .serverUrl(Constants.APPSYNC_API_URL)
                    .build();
        }
        return client;
    }

}
