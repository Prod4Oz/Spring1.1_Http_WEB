package ru.netology;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Server {

    ConcurrentHashMap<String, ConcurrentHashMap<String, Handler>> handlers = new ConcurrentHashMap<>();
    ServerSocket serverSocket;

    public void listen(int port) throws IOException {

        serverSocket = new ServerSocket(port);
        ExecutorService es = Executors.newFixedThreadPool(64);
        System.out.print("Server Starting\n");

        while (true) {
            try {
                System.out.print("accepting client\n");
                Socket socket = serverSocket.accept();
                es.submit(() -> requestProcess(socket));
            } catch (Exception e) {
                handle(e);
            }
        }
    }

    private void requestProcess(Socket socket) {
        try (
                final var in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                final var out = new BufferedOutputStream(socket.getOutputStream());
        ) {
            final var requestLine = in.readLine();

            if (requestLine == null)
                return;


            final var parts = requestLine.split(" ");

            if (parts.length != 3) {
                badRequest(out);
                return;
            }

            final var request = new Request(parts[0], parts[1]);

            System.out.println("path : " + request.getPath());
            System.out.println("query params : " + request.getQueryParams());
            System.out.println("query param with name 'value' : " + request.getQueryParam("value"));

            if (!handlers.containsKey(request.getMethod())) {
                notFound(out);
            }
            var methodHandlers = handlers.get(request.getMethod());

            if (!methodHandlers.containsKey(request.getPath())) {
                notFound(out);
            }
            var handler = methodHandlers.get(request.getPath());
            if (handler == null) {
                notFound(out);
                return;
            }
            handler.handle(request, out);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void badRequest(BufferedOutputStream out) throws IOException {
        out.write((
                "HTTP/1.1 400 Bad Request\r\n" +
                        "Content-Length: 0\r\n" +
                        "Connection: close\r\n" +
                        "\r\n"
        ).getBytes());
        out.flush();
    }

    private void notFound(BufferedOutputStream out) throws IOException {
        out.write((
                "HTTP/1.1 404 Not Found\r\n" +
                        "Content-Length: 0\r\n" +
                        "Connection: close\r\n" +
                        "\r\n"
        ).getBytes());
        out.flush();
    }

    private void handle(Exception e) {
        if (!(e instanceof SocketException)) {
            e.printStackTrace();
        }
    }

    public void addHandler(String method, String path, Handler handler) {
        handlers.putIfAbsent(method, new ConcurrentHashMap<>());
        handlers.get(method).put(path, handler);
    }
}