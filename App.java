
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class App {

    public static void main(String[] args) throws IOException {

        HttpServer server = HttpServer.create(
            new InetSocketAddress("127.0.0.1", 8080), 0
        );

        server.createContext("/", App::handle);

        server.start();

        System.out.println("サーバーを起動しました");
        System.out.println("http://localhost:8080/");
    }

    private static void handle(HttpExchange ex)
            throws IOException {

        String path = ex.getRequestURI().getPath();
        String method = ex.getRequestMethod();

        // CSSファイルを返す
        if (path.equals("/style.css") && method.equals("GET")) {

            String css = Files.readString(Path.of("style.css"));

            send(ex, css, "text/css");
            return;
        }

        if (!path.equals("/") ||
                (!method.equals("GET") && !method.equals("POST"))) {
            ex.sendResponseHeaders(404, -1);
            ex.close();
            return;
        }

        Map<String, String> form = new HashMap<>();

        // フォームの入力を取得
        if (method.equals("POST")) {

            String body = new String(
                ex.getRequestBody().readAllBytes(),
                StandardCharsets.UTF_8
            );

            for (String pair : body.split("&")) {

                String[] data = pair.split("=", 2);

                if (data.length == 2) {

                    String key = URLDecoder.decode(
                        data[0], StandardCharsets.UTF_8
                    );

                    String value = URLDecoder.decode(
                        data[1], StandardCharsets.UTF_8
                    );

                    form.put(key, value);
                }
            }
        }

        String residence = form.getOrDefault(
            "residence", "new"
        );

        String work = form.getOrDefault(
            "work", "remote"
        );

        String car = form.getOrDefault(
            "car", "yes"
        );

        boolean winter = form.containsKey("winter");

        // 許可する選択肢に限定する
        if (!residence.equals("return")) residence = "new";
        if (!work.equals("local")) work = "remote";
        if (!car.equals("no")) car = "yes";

        // HTMLファイルを読み込む
        String html = Files.readString(Path.of("index.html"));

        html = html.replace(
            "{{RESIDENCE_RETURN}}",
            residence.equals("return") ? "checked" : ""
        );

        html = html.replace(
            "{{RESIDENCE_NEW}}",
            residence.equals("new") ? "checked" : ""
        );

        html = html.replace(
            "{{WORK_REMOTE}}",
            work.equals("remote") ? "checked" : ""
        );

        html = html.replace(
            "{{WORK_LOCAL}}",
            work.equals("local") ? "checked" : ""
        );

        html = html.replace(
            "{{CAR_YES}}",
            car.equals("yes") ? "checked" : ""
        );

        html = html.replace(
            "{{CAR_NO}}",
            car.equals("no") ? "checked" : ""
        );

        html = html.replace(
            "{{WINTER_CHECKED}}",
            winter ? "checked" : ""
        );

        String result = "";

        // ボタンが押された場合
        if (method.equals("POST")) {

            Content.Conditions conditions =
                new Content.Conditions(
                    residence, work, car, winter
                );

            Content.Preview preview =
                Content.build(conditions);

            StringBuilder sb = new StringBuilder();

            sb.append("<section class='results'>");
            sb.append("<h2>あなたの暮らしプレビュー</h2>");

            sb.append("<div class='result-card'>");
            sb.append("<h3>暮らしのイメージ</h3>");
            sb.append("<ul>");

            for (String text : preview.life()) {
                sb.append("<li>")
                  .append(escape(text))
                  .append("</li>");
            }

            sb.append("</ul></div>");

            sb.append("<div class='result-card'>");
            sb.append("<h3>現地で確認したいこと</h3>");
            sb.append("<ul>");

            for (String text : preview.checks()) {
                sb.append("<li>")
                  .append(escape(text))
                  .append("</li>");
            }

            sb.append("</ul></div>");

            sb.append("<div class='official'>");
            sb.append("<h3>関連情報</h3>");
            sb.append(
                "<a href='https://kaneyama-iju.jp/' " +
                "target='_blank' rel='noopener noreferrer'>" +
                "金山町の移住情報サイト</a>"
            );
            sb.append("</div>");

            sb.append(
                "<p class='disclaimer'>" +
                "※生活例は想定です。制度や住宅の条件は" +
                "公式情報で確認してください。</p>"
            );

            sb.append("</section>");

            result = sb.toString();
        }

        html = html.replace("{{RESULT}}", result);

        send(ex, html, "text/html");
    }

    private static String escape(String text) {
        return text.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;")
                   .replace("'", "&#39;");
    }

    private static void send(
            HttpExchange ex,
            String text,
            String type
    ) throws IOException {

        byte[] data = text.getBytes(StandardCharsets.UTF_8);

        ex.getResponseHeaders().set(
            "Content-Type", type + "; charset=UTF-8"
        );

        ex.sendResponseHeaders(200, data.length);

        try (var os = ex.getResponseBody()) {
            os.write(data);
        }
    }
}
