import spark.ModelAndView;
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

        Map<String, String> map = new HashMap<>();
        map.put("title", "Hearthstone App");
        get("/", (req, res) -> new ModelAndView(map,"App.jade"), new JadeTemplateEngine());


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
}