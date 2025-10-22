package pt.isec.pd.exer16;

import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import com.google.gson.*;

import javax.json.*;
import javax.json.JsonObject;

public class HarryPotterExer {
    public static void main(String[] args) throws IOException, URISyntaxException {
        if (args.length != 1) {
            System.out.println("Usage: java HarryPotterExer <directory_path_to_save_images>");
            return;
        }
        File dir = new File(args[0]);
        String uri = "https://potterapi-fedeperin.vercel.app/en/characters?index=";
        String[] id = {"0", "1", "2", "3", "4", "5"};
        getHarryPotterCharacter(dir, uri, id);
    }

    public static void getHarryPotterCharacter(File dir, String uri, String[] id) throws IOException, URISyntaxException {
        HttpURLConnection connection;
        Character character;
        URL url;

        for (String i : id) {
            url = new URI(uri + i).toURL();
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/json");

            int responseCode = connection.getResponseCode();
            System.out.println("Response Code: " + responseCode);

            try (InputStream in = connection.getInputStream();
                 JsonReader jr = Json.createReader(in)) {

                JsonObject jo = jr.readObject();

                String imageUrl = jo.getString("image").trim();
                try (InputStream imageIn = new URI(imageUrl).toURL().openStream()) {
                    String file;
                    try {
                        file = Paths.get(new URI(imageUrl).getPath()).getFileName().toString();
                    } catch (Exception e) {
                        file = "character_" + i + ".jpg";
                    }
                    Path out = dir.toPath().resolve(file);
                    Files.copy(imageIn, out, StandardCopyOption.REPLACE_EXISTING);
                    System.out.println("Image saved to: " + out);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }

                Gson gson = new GsonBuilder().create();
                character = gson.fromJson(jo.toString(), Character.class);
                System.out.println(character);
            } finally {
                connection.disconnect();
            }
        }
    }
}
