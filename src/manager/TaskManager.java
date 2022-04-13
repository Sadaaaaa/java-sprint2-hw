package manager;

import Data.Epic;
import Data.Subtask;
import Data.Task;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public interface TaskManager {

    int generateNewId();

    LocalDate getEndTime(Task task);

    //1. Получение списка всех задач.
    List<Task> allTasksList();

    //2. Удаление всех задач.
    void deleteAllTasks();

    //3. Получение по идентификатору.
    Task getTaskById(int ID);

    Subtask getSubtaskById(int ID);

    Epic getEpicById(int ID);

    //4. Создание задач.
    void createTask(Task task) throws IOException;

    void createSubtask(Subtask subtask);

    void createEpic(Epic epic);

    //5. Обновление.
    void updateTask(Task newTask);

    void updateSubtask(Subtask newSubtask);

    void updateEpic(Epic newEpic);

    //6. Удаление по идентификатору.
    void deleteTask(int ID);

    void deleteEpic(int ID);

    void deleteSubtask(int ID);

    //7. Получение списка всех подзадач определённого эпика.
    void printSubtasks(int ID);

    //8. Автоматическое обновление статуса эпика
    void setStatus(int epicID);

    //9. История просмотра задач
    List<Task> history();

    //10. Автообновление длительности эпика
    void updateDuration(int epicID);

    //11. Сортировка задач
    List<Task> getPrioritizedTasks();

    //12. Поиск пересечений
    boolean isCrossed(Task task);
}

