package com.levor.liferpgtasks.factories;

import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.http.OkHttp3Requestor;
import com.dropbox.core.v2.DbxClientV2;

/**
 * Singleton instance of {@link DbxClientV2} and friends
 */
public class DropboxClientFactory {

    private static DbxClientV2 sDbxClient;

    private static String accessCode = null;

    public static void init(String accessToken) {
        if (sDbxClient == null || !accessToken.equals(accessCode)) {
            DbxRequestConfig requestConfig = DbxRequestConfig.newBuilder("LifeRPG Tasks/1")
                    .withHttpRequestor(OkHttp3Requestor.INSTANCE)
                    .build();

            sDbxClient = new DbxClientV2(requestConfig, accessToken);

        }
    }

    public static DbxClientV2 getClient() {
        if (sDbxClient == null) {
            throw new IllegalStateException("Client not initialized.");
        }
        return sDbxClient;
    }
}