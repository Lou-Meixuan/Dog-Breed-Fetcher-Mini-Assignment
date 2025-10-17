package dogapi;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.*;

/**
 * BreedFetcher implementation that relies on the dog.ceo API.
 * Note that all failures get reported as BreedNotFoundException
 * exceptions to align with the requirements of the BreedFetcher interface.
 */
public class DogApiBreedFetcher implements BreedFetcher {
    private final OkHttpClient client = new OkHttpClient();

    /**
     * Fetch the list of sub breeds for the given breed from the dog.ceo API.
     * @param breed the breed to fetch sub breeds for
     * @return list of sub breeds for the given breed
     */
    @Override
    public List<String> getSubBreeds(String breed) throws BreedNotFoundException{
        if (breed == null || breed.trim().isEmpty()) {
            throw new BreedNotFoundException("Breed must be non-empty.");
        }
        final Request request = new Request.Builder()
                .url(String.format("https://dog.ceo/api/breed/%s/list", breed))
                .build();
        try{
            final Response response = client.newCall(request).execute();
            final JSONObject responseBody = new JSONObject(response.body().string());

            JSONArray message = responseBody.getJSONArray("message");
            String status = responseBody.getString("status");
            if (status.equalsIgnoreCase("success")) {
                ArrayList<String> breedList = new ArrayList<>();
                int length = message.length();
                for (int i = 0; i < length; i++) {
                    breedList.add(message.getString(i));
                }
                return breedList;
            }
            else{
                throw new BreedNotFoundException(responseBody.getString("message"));
            }
        } catch (IOException | org.json.JSONException e) {
            throw new BreedNotFoundException("Could not fetch sub-breeds");
        }
    }
}