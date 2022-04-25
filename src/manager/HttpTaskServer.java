package manager;

import Data.*;
import com.google.gson.*;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class HttpTaskServer {
    private static final int PORT = 8080;
    HttpServer httpServer;
    TaskManager manager;
    Gson gson = new GsonBuilder()
//            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
            .create();
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    public HttpTaskServer(TaskManager manager) throws IOException {
        this.manager = manager;
        this.httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);
        httpServer.createContext("/tasks/", (exchange) -> {
            int responseCode = 0; // поле кода ответа на запрос
            String response = ""; // поле ответа на запрос

            URI uri = exchange.getRequestURI(); // получили URI
            String path = uri.getPath(); // получили запрошенный path

            String[] pathArray = path.split("/");

            int taskID = splitQuery(uri);
            String method = exchange.getRequestMethod();
            switch (method) {
                case "GET":
                    if (pathArray.length > 2) {
                        // endpoint для allTasksList()
                        if ((pathArray[2].equals("task") || pathArray[2].equals("subtask") ||
                                pathArray[2].equals("epic")) && taskID == 0) {
                            response = gson.toJson(manager.allTasksList());
                            responseCode = 200;
                        }

                        // endpoint для getTaskById(taskID)
                        if (pathArray[2].equals("task") && taskID != 0) {
                            if (manager.getHashMapTasks().containsKey(taskID)) {
                                response = manager.getTaskById(taskID).toString();
                                responseCode = 200;
                            } else {
                                response = "Такой задачи не существует!";
                                responseCode = 204;
                            }
                        }

                        // endpoint для getSubtaskById(taskID)
                        if (pathArray[2].equals("subtask") && (pathArray.length < 4) && taskID != 0) {
                            if (manager.getHashMapSubtasks().containsKey(taskID)) {
                                response = manager.getSubtaskById(taskID).toString();
                                responseCode = 200;
                            } else {
                                responseCode = 204;
                            }
                        }

                        // endpoint для getEpicById(taskID)
                        if (pathArray[2].equals("epic") && taskID != 0) {
                            if (manager.getHashMapEpics().containsKey(taskID)) {
                                response = manager.getEpicById(taskID).toString();
                                responseCode = 200;
                            } else {
                                responseCode = 204;
                            }
                        }

                        // endpoint для getEpicSubTasks(id)
                        if (pathArray[2].equals("subtask") && (pathArray.length > 3) && taskID != 0) {
                            if (manager.getHashMapEpics().containsKey(taskID)) {
                                response = manager.getEpicById(taskID).getSubtaskIDList().toString();
                                responseCode = 200;
                            } else {
                                responseCode = 204;
                            }
                        }

                        // endpoint для getHistory()
                        if (pathArray[2].equals("history")) {
                            if (!manager.getHistoryManager().getHistory().isEmpty()) {
                                response = manager.getHistoryManager().getHistory().toString();
                                responseCode = 200;
                            } else {
                                responseCode = 204;
                            }
                        }
                    } else {
                        // endpoint для getPrioritizedTasks()
                        if (pathArray[1].equals("tasks")) {
                            if (!manager.getPrioritizedTasks().isEmpty()) {
                                response = manager.getPrioritizedTasks().toString();
                                responseCode = 200;
                            } else {
                                responseCode = 204;
                            }
                        }
                    }

                    break;
                case "POST":
                    InputStream inputStream = exchange.getRequestBody();
                    String body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);

                    JsonElement jsonElement = JsonParser.parseString(body);
                    JsonObject jsonObject = null;
                    if (!jsonElement.isJsonObject()) {
                        System.out.println("Переденные данные не соответствуют формату JSON");
                    } else {
                        jsonObject = jsonElement.getAsJsonObject();
                    }

                    String taskType = pathArray[2];
                    switch (taskType) {
                        case "task":
                            responseCode = 201;
                            manager.createTask(gson.fromJson(jsonObject, Task.class));
                            break;
                        case "epic":
                            responseCode = 201;
                            manager.createEpic(gson.fromJson(jsonObject, Epic.class));
                            break;
                        case "subtask":
                            responseCode = 201;
                            manager.createSubtask(gson.fromJson(jsonObject, Subtask.class));
                            break;
                        default:
                            responseCode = 400;
                            break;
                    }
                    break;
                case "DELETE":
                    // endpoint для deleteAllTasks()
                    if ((pathArray[2].equals("task") || pathArray[2].equals("subtask") ||
                            pathArray[2].equals("epic")) && taskID == 0) {
                        manager.deleteAllTasks();
                        response = "Все задачи удалены!";
                        responseCode = 200;
                    }

                    // endpoint для deleteTask(taskID)
                    if (pathArray[2].equals("task") && taskID != 0) {
                        if(manager.getHashMapTasks().containsKey(taskID)) {
                            manager.deleteTask(taskID);
                            responseCode = 200;
                        } else {
                            response = "Удаление неуспешно. Такой задачи не существует!";
                            responseCode = 204;
                        }
                    }

                    // endpoint для deleteSubtask(taskID)
                    if (pathArray[2].equals("subtask") && taskID != 0) {
                        if(manager.getHashMapSubtasks().containsKey(taskID)) {
                            manager.deleteSubtask(taskID);
                            responseCode = 200;
                        } else {
                            response = "Удаление неуспешно. Такой задачи не существует!";
                            responseCode = 204;
                        }
                    }

                    // endpoint для deleteEpic(taskID)
                    if (pathArray[2].equals("epic") && taskID != 0) {
                        if(manager.getHashMapEpics().containsKey(taskID)) {
                            manager.deleteEpic(taskID);
                            responseCode = 200;
                        } else {
                            response = "Удаление неуспешно. Такого эпика не существует!";
                            responseCode = 204;
                        }
                    }
                    break;
                default:
                    responseCode = 400;
                    break;
            }

            exchange.sendResponseHeaders(responseCode, 0);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        });
    }

        public void start() {
            System.out.println("Запускаем сервер на порту " + PORT);
            System.out.println("Открой в браузере http://localhost:" + PORT + "/");
            httpServer.start();
        }

        public void stop() {
            httpServer.stop(0);
        }



        public static int splitQuery (URI uri){
            String query = uri.getQuery();
            if (query == null) {
                return 0;
            } else {
                int indexSymb = query.indexOf("=") + 1;
                return Integer.parseInt(query.substring(indexSymb));
            }
        }

//        static class LocalDateAdapter extends TypeAdapter<LocalDate> {
//            @Override
//            public void write(JsonWriter jsonWriter, LocalDate localDate) throws IOException {
//                jsonWriter.value(localDate.format(DateTimeFormatter.ISO_DATE_TIME));
//            }
//
//            @Override
//            public LocalDate read(JsonReader jsonReader) throws IOException {
//                return LocalDate.parse(jsonReader.nextString(),DateTimeFormatter.ISO_DATE_TIME);
//            }
//        }
    }
