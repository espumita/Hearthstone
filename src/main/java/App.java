import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONObject;
import spark.ModelAndView;
import spark.Request;
import spark.template.jade.JadeTemplateEngine;
import com.google.common.io.ByteStreams;
import com.google.common.io.Closeables;
import com.google.common.net.MediaType;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

import static spark.Spark.*;

public class App {
    public static void main(String[] args) {
        port(1337);
        threadPool(8);

        get("/", (req, res) -> new ModelAndView(content(),"App.jade"), new JadeTemplateEngine());

        post("/search", (req, res) -> new ModelAndView(queryContent(req), "searchResult.jade"), new JadeTemplateEngine());

        get("/favicon.ico", (req, res) -> {
            try {
                InputStream inputStream = null;
                OutputStream outputStream = null;
                try {
                    inputStream = new BufferedInputStream(new FileInputStream("src/main/resources/favicon.ico"));
                    outputStream = new BufferedOutputStream(res.raw().getOutputStream());
                    res.raw().setContentType(MediaType.ICO.toString());
                    res.status(200);
                    ByteStreams.copy(inputStream, outputStream);
                    outputStream.flush();
                    return "";
                } finally {
                    Closeables.close(inputStream, true);
                }
            } catch (FileNotFoundException ex) {
                res.status(404);
                return ex.getMessage();
            } catch (IOException ex) {
                res.status(500);
                return ex.getMessage();
            }
        });
    }

    private static Map<String, String> queryContent(Request req) {
        Map<String, String> map = new HashMap<>();
        map.put("title", "Hearthstone App");
        try {
            String search = req.queryParams("search");
            HttpResponse<JsonNode> response = Unirest.get("https://omgvamp-hearthstone-v1.p.mashape.com/cards/search/"+search)
                    .header("X-Mashape-Key", "fmzbntRqkFmshCSZBJk2203AN8Ybp1KGrcijsnaHeDRpsXG9if")
                    .asJson();
            System.out.println(response.getBody());
            map.put("img", jsonMap(response,"img"));
            map.put("race", jsonMap(response,"race"));
            map.put("health", jsonMap(response,"health"));
            map.put("type", jsonMap(response,"type"));
            map.put("locale", jsonMap(response,"locale"));
            map.put("cardSet", jsonMap(response,"cardSet"));
            map.put("attack", jsonMap(response,"attack"));
            map.put("cardId", jsonMap(response,"cardId"));
            map.put("faction", jsonMap(response,"faction"));
            map.put("name", jsonMap(response,"name"));
            map.put("imgGold", jsonMap(response,"imgGold"));
            map.put("text", jsonMap(response,"text"));
            map.put("rarity", jsonMap(response,"rarity"));
            map.put("cost", jsonMap(response,"cost"));
        } catch (UnirestException e) {
            e.printStackTrace();
        }
        return map;
    }

    private static String jsonMap(HttpResponse<JsonNode> response, String key) {
        String valueMapped = "";
        try {
            valueMapped = ((JSONObject) response.getBody().getArray().get(0)).get(key).toString();
        }catch (Exception e){
            valueMapped = "none";
        }
        return valueMapped;
    }

    private static Map<String, String> content() {
        Map<String, String> map = new HashMap<>();
        map.put("title", "Hearthstone App");
        return map;
    }
}