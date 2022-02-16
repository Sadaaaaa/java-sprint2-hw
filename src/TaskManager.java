import Data.Epic;
import Data.Subtask;
import Data.Task;
import java.util.ArrayList;
import java.util.List;

public interface TaskManager {

    int generateNewId();

    //1. Получение списка всех задач.
    ArrayList<Task> allTasksList();

    //2. Удаление всех задач.
    void deleteAllTasks();

    //3. Получение по идентификатору.
    Task getTaskById(int ID);

    Subtask getSubtaskById(int ID);

    Epic getEpicById(int ID);

    //4. Создание задач.
    void createTask(Task task);

    void createSubtask(Subtask subtask);

    void createEpic(Epic epic);

    //5. Обновление.
    void updateTask(Task newTask);

    void updateSubtask(Subtask newSubtask);

    void updateEpic(Epic newEpic);

    //6. Удаление по идентификатору.
    void deleteTaskByID(int ID);

    //7. Получение списка всех подзадач определённого эпика.
    void printSubtasks(int ID);

    //8. Автоматическое обновление статуса эпика
    void setStatus(int epicID);

    //9. История просмотра задач
    List<Task> history();
}

