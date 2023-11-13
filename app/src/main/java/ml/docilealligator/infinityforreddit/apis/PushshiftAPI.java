package ml.docilealligator.infinityforreddit.apis;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface PushshiftAPI {

    @GET("reddit/submission/search/")
    Call<String> getRemovedPost(@Query("ids") String postId);

}
