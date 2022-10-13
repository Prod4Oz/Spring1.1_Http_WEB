package ru.netology;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;


public class Main {
  public static void main(String[] args) throws IOException {


    Server server = new Server();

    server.addHandler("GET", "/messages", (request, out) -> {
      var message = "Hello, my name is John. Request parameters: " + request.getQueryParams();
      try {
        out.write((
                "HTTP/1.1 200 OK\r\n" +
                        "Content-Type: " + "text/plane" + "\r\n" +
                        "Content-Length: " + message.length() + "\r\n" +
                        "Connection: close\r\n" +
                        "\r\n" +
                        message
        ).getBytes());
        out.flush();
      } catch (IOException e) {
        e.printStackTrace();
      }
    });
    server.addHandler("POST", "/messages", (request, out) -> {
      var message = "Hello, my name is John. Request parameters: " + request.getQueryParams();
      try {
        out.write((
                "HTTP/1.1 200 OK\r\n" +
                        "Content-Type: " + "text/plane" + "\r\n" +
                        "Content-Length: " + message.length() + "\r\n" +
                        "Connection: close\r\n" +
                        "\r\n" +
                        message
        ).getBytes());
        out.flush();
      } catch (IOException e) {
        e.printStackTrace();
      }
    });

    server.addHandler("GET", "/spring.png", Main::processPath);
    server.addHandler("GET", "/spring.svg", Main::processPath);
    server.addHandler("GET", "/resources.html", Main::processPath);

    server.listen(9999);
  }

  private static void processPath(Request request, BufferedOutputStream out) {
    try {
      final var filePath = Path.of(".", "public", request.getPath());
      final var mimeType = Files.probeContentType(filePath);

      final var length = Files.size(filePath);
      out.write((
              "HTTP/1.1 200 OK\r\n" +
                      "Content-Type: " + mimeType + "\r\n" +
                      "Content-Length: " + length + "\r\n" +
                      "Connection: close\r\n" +
                      "\r\n"
      ).getBytes());
      Files.copy(filePath, out);
      out.flush();
    } catch (IOException e) {
      throw new RuntimeException();
    }
  }
}
