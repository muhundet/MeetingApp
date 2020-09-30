package retrofit;

import com.example.meetingapp.models.ZoomParticipants;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface RetrofitInterface {
    @GET("/api/hours")
    Call<List<ZoomParticipants>> getZoomParticipants();

}
