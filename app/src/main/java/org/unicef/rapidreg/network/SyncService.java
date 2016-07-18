package org.unicef.rapidreg.network;


import android.content.Context;

import com.google.gson.JsonElement;

import org.unicef.rapidreg.PrimeroConfiguration;

import java.util.List;

import retrofit2.Response;
import rx.Observable;


public class SyncService extends BaseRetrofitService {

    private SyncServiceInterface serviceInterface;

    @Override
    String getBaseUrl() {
        return PrimeroConfiguration.getApiBaseUrl();
    }

    public SyncService(Context context) throws Exception {
        createRetrofit(context);
        serviceInterface = getRetrofit().create(SyncServiceInterface.class);
    }


    public Observable<Response<List<JsonElement>>> getAllCasesRx(
            String cookie,
            String locale,
            Boolean isMobile) {
        return serviceInterface.getAllCases(cookie, locale);
    }

    public Observable<Response<JsonElement>> postCase(
            String cookie,
            Boolean isMobile,
            Object requestBody) {
        return serviceInterface.postCase(cookie, requestBody);
    }

    public Observable<Response<JsonElement>> putCase(
            String cookie,
            Boolean isMobile,
            Object requestBody) {
        return serviceInterface.putCase(cookie, requestBody);
    }
}


