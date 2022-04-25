package manager;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        new KVServer().start();
        new HttpTaskServer(Managers.getDefault("http://localhost:8078")).start();
    }
}