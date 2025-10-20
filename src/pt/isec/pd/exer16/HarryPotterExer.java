package pt.isec.pd.exer16;

import java.io.*;
import java.net.*;
import com.google.gson.*;
import javax.json.*;
import javax.json.JsonObject;

public class HarryPotterExer {
    public static void main(String[] args) throws IOException, URISyntaxException {
        String id = args.length > 0 ? args[0] : "3";
        getHarryPotterCharacter(id);
    }

    public static void getHarryPotterCharacter(String id) throws IOException, URISyntaxException {
        String uri = "https://potterapi-fedeperin.vercel.app/en/characters?index=" + id;

        URL url = new URI(uri).toURL();

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", "application/json");

        InputStream in = connection.getInputStream();

        JsonReader jr = Json.createReader(in);
        JsonObject jo = jr.readObject();

        jr.close();
        connection.disconnect();

        Gson gson = new GsonBuilder().create();
        Character cha = gson.fromJson(jo.toString(), Character.class);
        System.out.println(cha);

    }
}
