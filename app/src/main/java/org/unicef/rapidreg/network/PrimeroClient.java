package org.unicef.rapidreg.network;

import org.unicef.rapidreg.model.LoginRequestBody;
import org.unicef.rapidreg.model.LoginResponse;
import org.unicef.rapidreg.model.forms.CaseForm;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface PrimeroClient {

    @POST("/api/login")
    public Call<LoginResponse> login(@Body LoginRequestBody body);

    @GET("/api/form_sections")
    public Call<CaseForm> getForm(
            @Header("Cookie") String cookie,
            @Query("locale") String locale,
            @Query("mobile") Boolean isMobile,
            @Query("parent_form") String parentForm);
}
