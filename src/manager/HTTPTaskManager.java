package manager;

import Data.Epic;
import Data.Subtask;
import Data.Task;
import com.google.gson.Gson;

import java.util.Map;

public class HTTPTaskManager extends FileBackedTasksManager {
    KVTaskClient kvTaskClient;
    Gson gson = new Gson();

    public HTTPTaskManager(String url) {
        super(url);
        kvTaskClient = new KVTaskClient(url);
        kvTaskClient.register();
    }

    @Override
    protected void save() {
        kvTaskClient.put("tasks", gson.toJson(getHashMapTasks()));
        kvTaskClient.put("subtasks", gson.toJson(getHashMapSubtasks()));
        kvTaskClient.put("epics", gson.toJson(getHashMapEpics()));
        kvTaskClient.put("history", gson.toJson(getHistoryManager().getHistory()));
    }

    void load(String key) {
        switch (key) {
            case "tasks":
                Map<Integer,Task> tasksMap = kvTaskClient.load(key);
                for(Map.Entry<Integer, Task> x : tasksMap.entrySet()) {
                    getHashMapTasks().put(x.getKey(), x.getValue());
                }
                break;
            case "subtasks":
                Map<Integer,Task> subtasksMap = kvTaskClient.load(key);
                for(Map.Entry<Integer, Task> x : subtasksMap.entrySet()) {
                    getHashMapSubtasks().put(x.getKey(), (Subtask) x.getValue());
                }
                break;
            case "epics":
                Map<Integer,Task> epicsMap = kvTaskClient.load(key);
                for(Map.Entry<Integer, Task> x : epicsMap.entrySet()) {
                    getHashMapEpics().put(x.getKey(), (Epic) x.getValue());
                }
                break;
            case "history":
                kvTaskClient.loadHistory(key);
                for (Task x : kvTaskClient.loadHistory(key)) {
                    getHistoryManager().getHistory().add(x);
                }
                break;
            default:
                System.out.println("Задан неверный ключ для восстановления");
                break;
        }
    }
}
