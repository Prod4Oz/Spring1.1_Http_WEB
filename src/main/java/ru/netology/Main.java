package ru.netology;

import java.io.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
  public static void main(String[] args) throws IOException {

    final var server = new Server();

    server.listen(9999);
  }
}


