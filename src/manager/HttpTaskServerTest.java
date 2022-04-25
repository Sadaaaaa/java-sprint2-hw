package manager;

import Data.Epic;
import Data.Subtask;
import Data.Task;
import com.google.gson.Gson;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

class HttpTaskServerTest {
    Gson gson = new Gson();
    HttpClient client = HttpClient.newHttpClient();
    KVServer kvServer;
    HttpTaskServer httpTaskServer;

    @BeforeEach
    void serverStart() throws IOException {
        kvServer = new KVServer();
        kvServer.start();
        httpTaskServer = new HttpTaskServer(Managers.getDefault("http://localhost:8078"));
        httpTaskServer.start();
    }

    @AfterEach
    void serverStop() {
        kvServer.stop();
        httpTaskServer.stop();
    }

    //проверка POST - GET - DELETE эндпоинтов для Task
    @Test
    void postGetDelTask() throws IOException, InterruptedException {
        Task task = new Task("taskName", "taskDetails");
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(gson.toJson(task));

        HttpRequest requestPOST = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/task/"))
                .POST(body)
                .build();
        HttpResponse<String> responsePOST = client.send(requestPOST, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, responsePOST.statusCode());

        HttpRequest requestGET = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/task/?id=1"))
                .GET()
                .build();
        HttpResponse<String> responseGET = client.send(requestGET, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, responseGET.statusCode());
        assertEquals("Наименование: taskName, Описание: taskDetails, Статус: NEW", responseGET.body());

        HttpRequest requestDEL = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/task/?id=1"))
                .DELETE()
                .build();
        HttpResponse<String> responseDEL = client.send(requestDEL, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, responseDEL.statusCode());
        assertTrue(responseDEL.body().isEmpty());
    }

    //проверка POST - GET - DELETE эндпоинтов для Epic
    @Test
    void postGetDelEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("epicName", "epicDetails");
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(gson.toJson(epic));

        HttpRequest requestPOST = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/epic/"))
                .POST(body)
                .build();
        HttpResponse<String> responsePOST = client.send(requestPOST, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, responsePOST.statusCode());

        HttpRequest requestGET = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/epic/?id=1"))
                .GET()
                .build();
        HttpResponse<String> responseGET = client.send(requestGET, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, responseGET.statusCode());
        assertEquals("Наименование: epicName, Описание: epicDetails, Статус: NEW, Кол-во подзадач:0", responseGET.body());

        HttpRequest requestDEL = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/epic/?id=1"))
                .DELETE()
                .build();
        HttpResponse<String> responseDEL = client.send(requestDEL, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, responseDEL.statusCode());
        assertTrue(responseDEL.body().isEmpty());
    }

    //проверка POST - GET - DELETE эндпоинтов для Subtask
    @Test
    void postGetDelSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("epicName", "epicDetails");
        HttpRequest.BodyPublisher bodyEpic = HttpRequest.BodyPublishers.ofString(gson.toJson(epic));

        HttpRequest postEpic = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/epic/"))
                .POST(bodyEpic)
                .build();
        HttpResponse<String> responseEpic = client.send(postEpic, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, responseEpic.statusCode());

        Subtask subtask = new Subtask("subtaskName", "subtaskDetails");
        subtask.setEpicID(1);
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(gson.toJson(subtask));
        System.out.println(body);
        HttpRequest requestPOST = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/subtask/"))
                .POST(body)
                .build();
        HttpResponse<String> responsePOST = client.send(requestPOST, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, responsePOST.statusCode());

        HttpRequest requestGET = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/subtask/?id=2"))
                .version(HttpClient.Version.HTTP_1_1)
                .GET()
                .build();
        HttpResponse<String> responseGET = client.send(requestGET, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, responseGET.statusCode());
        assertEquals("Наименование: subtaskName, Описание: subtaskDetails, Статус: NEW, Эпик:1", responseGET.body());

        HttpRequest requestDEL = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/subtask/?id=2"))
                .DELETE()
                .build();
        HttpResponse<String> responseDEL = client.send(requestDEL, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, responseDEL.statusCode());
        assertTrue(responseDEL.body().isEmpty());
    }

    @Test
    void getDeleteAllTasksANDSubtaskList() throws IOException, InterruptedException {
        Epic epic = new Epic("epicName", "epicDetails");
        HttpRequest.BodyPublisher bodyEpic = HttpRequest.BodyPublishers.ofString(gson.toJson(epic));
        HttpRequest postEpic = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/epic/"))
                .POST(bodyEpic)
                .build();
        client.send(postEpic, HttpResponse.BodyHandlers.ofString());

        Subtask subtask = new Subtask("subtaskName", "subtaskDetails");
        subtask.setEpicID(1);
        HttpRequest.BodyPublisher bodySubtask = HttpRequest.BodyPublishers.ofString(gson.toJson(subtask));
        System.out.println(bodySubtask);
        HttpRequest requestPOST = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/subtask/"))
                .POST(bodySubtask)
                .build();
        client.send(requestPOST, HttpResponse.BodyHandlers.ofString());

        Task task = new Task("taskName", "taskDetails");
        HttpRequest.BodyPublisher bodyTask = HttpRequest.BodyPublishers.ofString(gson.toJson(task));
        HttpRequest requestTask = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/task/"))
                .POST(bodyTask)
                .build();
        client.send(requestTask, HttpResponse.BodyHandlers.ofString());

        HttpRequest requestAllTasks = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/task/"))
                .GET()
                .build();
        HttpResponse<String> responseAllTasks = client.send(requestAllTasks, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, responseAllTasks.statusCode());
        assertNotNull(responseAllTasks.body());

        HttpRequest requestSubtaskList = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/subtask/epic/?id=1"))
                .GET()
                .build();
        HttpResponse<String> responseSubtaskList = client.send(requestSubtaskList, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, responseSubtaskList.statusCode());
        assertEquals("[2]", responseSubtaskList.body());

        HttpRequest requestDeleteAllTasks = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/task/"))
                .DELETE()
                .build();
        HttpResponse<String> responseDeleteAllTasks = client.send(requestDeleteAllTasks, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, responseDeleteAllTasks.statusCode());
        assertEquals("Все задачи удалены!", responseDeleteAllTasks.body());
    }

    @Test
    void getHistoryANDPriorTasks() throws IOException, InterruptedException {
        //Создаем задачи
        //Эпик
        Epic epic = new Epic("epicName", "epicDetails");
        HttpRequest.BodyPublisher bodyEpic = HttpRequest.BodyPublishers.ofString(gson.toJson(epic));
        HttpRequest postEpic = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/epic/"))
                .POST(bodyEpic)
                .build();
        client.send(postEpic, HttpResponse.BodyHandlers.ofString());
        //Сабтаск
        Subtask subtask = new Subtask("subtaskName", "subtaskDetails");
        subtask.setEpicID(1);
        HttpRequest.BodyPublisher bodySubtask = HttpRequest.BodyPublishers.ofString(gson.toJson(subtask));
        System.out.println(bodySubtask);
        HttpRequest requestPOST = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/subtask/"))
                .POST(bodySubtask)
                .build();
        client.send(requestPOST, HttpResponse.BodyHandlers.ofString());
        //Таск
        Task task = new Task("taskName", "taskDetails");
        HttpRequest.BodyPublisher bodyTask = HttpRequest.BodyPublishers.ofString(gson.toJson(task));
        HttpRequest requestTask = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/task/"))
                .POST(bodyTask)
                .build();
        client.send(requestTask, HttpResponse.BodyHandlers.ofString());

        //вызываем задачи для истории
        //Эпик
        HttpRequest requestGETEpic = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/epic/?id=1"))
                .GET()
                .build();
        client.send(requestGETEpic, HttpResponse.BodyHandlers.ofString());
        //Сабтаск
        HttpRequest requestGETSubtask = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/subtask/?id=2"))
                .version(HttpClient.Version.HTTP_1_1)
                .GET()
                .build();
        client.send(requestGETSubtask, HttpResponse.BodyHandlers.ofString());
        //Таск
        HttpRequest requestGETTask = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/task/?id=3"))
                .GET()
                .build();
        client.send(requestGETTask, HttpResponse.BodyHandlers.ofString());

        //Вызываем историю
        HttpRequest requestGETHistory = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/history/"))
                .GET()
                .build();
        HttpResponse<String> responseHistory = client.send(requestGETHistory, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, responseHistory.statusCode());
        assertNotNull(responseHistory.body());

        //Вызываем приоритетный список
        HttpRequest requestGETPriorList = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/history/"))
                .GET()
                .build();
        HttpResponse<String> responsePriorList = client.send(requestGETPriorList, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, responsePriorList.statusCode());
        assertNotNull(responsePriorList.body());
    }
}