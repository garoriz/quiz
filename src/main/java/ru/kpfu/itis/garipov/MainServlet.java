package ru.kpfu.itis.garipov;

import com.google.gson.Gson;
import ru.kpfu.itis.garipov.model.Response;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@WebServlet(urlPatterns = "/busTimetable")
public class MainServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/plain");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().write(getInfo());

    }

    private String getInfo() {
        StringBuilder content = new StringBuilder();
        try {
            URL getUrl = new URL("http://data.kzn.ru:8082/api/v0/dynamic_datasets/bus.json");
            HttpURLConnection connection = (HttpURLConnection) getUrl.openConnection();
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestMethod("GET");
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream())
            )) {
                String input;
                while ((input = reader.readLine()) != null) {
                    content.append(input);
                }
            }
            connection.disconnect();
        } catch (IOException e) {
            return "Ошибка! Сервер недоступен";
        }
        //return content.toString();
        return readableInfo(content.toString());
    }

    private String readableInfo(String json) {
        Gson gson = new Gson();
        json = json.substring(1, json.length() - 1);
        String[] jsons = json.split("},");
        List<Response> responses = new ArrayList<>();
        for (int i = 0; i < jsons.length - 1; i++) {
            jsons[i] = jsons[i] + "}";
            responses.add(gson.fromJson(jsons[i], Response.class));
        }
        responses.add(gson.fromJson(jsons[jsons.length - 1], Response.class));
        String content = "";
        for (int i = 0; i < responses.size(); i++) {
            content += "Обновлено в " + responses.get(i).updated_at + "<br/>";
            content += "Маршрут: " + responses.get(i).getData().Marsh + "<br/>";
            content += "Широта: " + responses.get(i).getData().Latitude + "<br/>";
            content += "Долгота: " + responses.get(i).getData().Longitude + "<br/>";
            content += "Скорость автобуса: " + responses.get(i).getData().Speed + "<br/>";
            content += "Азимут: " + responses.get(i).getData().Azimuth + "<br/>";
            content += "_____________<br/>";
        }
        System.out.println(content);
        return content;
    }
}