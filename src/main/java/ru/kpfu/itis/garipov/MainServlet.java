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
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
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
        StringBuilder content = new StringBuilder();
        for (Response respons : responses) {
            content.append("Обновлено в ").append(respons.updated_at).append("\n");
            content.append("Маршрут: ").append(respons.getData().Marsh).append("\n");
            content.append("Широта: ").append(respons.getData().Latitude).append("\n");
            content.append("Долгота: ").append(respons.getData().Longitude).append("\n");
            content.append("Скорость автобуса: ").append(respons.getData().Speed).append("\n");
            content.append("Азимут: ").append(respons.getData().Azimuth).append("\n");
            content.append("_____________" + "\n");
        }
        System.out.println(content);
        return content.toString();
    }
}