package manager;

import Data.Epic;
import Data.StatusList;
import Data.Subtask;
import Data.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.junit.jupiter.api.function.Executable;

import java.io.PrintStream;
import java.time.Duration;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest<T extends TaskManager> {
    private T TaskManager;


    @Test
    public void generateNewIdTest() {
        TaskManager manager = new InMemoryTaskManager();
        Assertions.assertEquals(manager.generateNewId(), 1);
    }

    // Функция 1: Получение списка всех задач. Тест 1: стандартное поведение
    @Test
    public void allTasksListTest1() throws IOException {
        TaskManager manager = new InMemoryTaskManager();
        Task task = new Task("Task", "Task description");

        manager.createTask(task);
        assertEquals("Наименование: Task, Описание: Task description, Статус: NEW", manager.allTasksList().get(0).toString());
    }

    // Функция 1: Получение списка всех задач. Тест 2: пустой список задач
    @Test
    public void allTasksListTest2() {
        TaskManager manager = new InMemoryTaskManager();
        Map<Integer, Task> hashmapTask = new HashMap<>();
        List<Task> alltasksList = new ArrayList<>();

        alltasksList.addAll(hashmapTask.values());
        alltasksList.toArray();

        Assertions.assertArrayEquals(alltasksList.toArray(), manager.allTasksList().toArray());
    }

    // Функция 1: Получение списка всех задач. Тест 3: неверный идентификатор задачи
    @Test
    public void allTasksListTest3() throws IOException {
        TaskManager manager = new InMemoryTaskManager();
        Task task = new Task("Task", "Task description");
        task.setItemID(15);
        manager.createTask(task);

        System.out.println(manager.allTasksList().get(0).getItemID());

        Assertions.assertEquals(task.getItemID(), manager.allTasksList().get(0).getItemID());
    }

    // Функция 2: Удаление всех задач. Стандартное поведение
    @Test
    public void deleteAllTasksTest1() {
        InMemoryTaskManager manager = new InMemoryTaskManager();
        manager.createTask(new Task("Task", "Task description"));
        manager.createTask(new Subtask("Task", "Task description"));
        manager.createTask(new Epic("Task", "Task description"));
        manager.deleteAllTasks();
        Map<Integer, Task> emptyMap = new HashMap<>();

        Assertions.assertEquals(emptyMap.isEmpty(), (manager.getHashMapTasks().isEmpty() && manager.getHashMapEpics().isEmpty()
                && manager.getHashMapSubtasks().isEmpty()));
    }

    // Функция 2: Удаление всех задач. Пустой перечень задач
    @Test
    public void deleteAllTasksTest2() {
        InMemoryTaskManager manager = new InMemoryTaskManager();
        manager.deleteAllTasks();
        Map<Integer, Task> emptyMap = new HashMap<>();

        Assertions.assertEquals(emptyMap.isEmpty(), (manager.getHashMapTasks().isEmpty() && manager.getHashMapEpics().isEmpty()
                && manager.getHashMapSubtasks().isEmpty()));
    }


    // Функция 3: Получение по идентификатору.
    @Test
    public void getTaskByIDTestNormal() {
        InMemoryTaskManager manager = new InMemoryTaskManager();
        Task task = new Task("Task", "Task description");
        manager.createTask(task);

        Assertions.assertEquals(task, manager.getTaskById(task.getItemID()));
    }

    @Test
    public void getTaskByIDTestNotExist() {
        InMemoryTaskManager manager = new InMemoryTaskManager();
        Task task = new Task("Task", "Task description");
        manager.createTask(task);

        final NullPointerException exception = assertThrows(
                NullPointerException.class,
                () -> manager.getTaskById(5));

        assertEquals(null, exception.getMessage());
    }

    @Test
    public void getEpicByIDTestNormal() {
        InMemoryTaskManager manager = new InMemoryTaskManager();
        Epic epic = new Epic("Epic", "Epic description");
        manager.createEpic(epic);

        Assertions.assertEquals(epic, manager.getEpicById(epic.getItemID()));
    }

    @Test
    public void getEpicByIDTestNotExist() {
        InMemoryTaskManager manager = new InMemoryTaskManager();
        Epic epic = new Epic("Epic", "Epic description");
        manager.createEpic(epic);

        final NullPointerException exception = assertThrows(
                NullPointerException.class,
                () -> manager.getEpicById(5));

        assertEquals(null, exception.getMessage());
    }

    @Test
    public void getSubtaskByIDTestNormal() {
        InMemoryTaskManager manager = new InMemoryTaskManager();
        Epic epic = new Epic("Epic", "Epic description");
        manager.createEpic(epic);
        Subtask subtask = new Subtask("Subtask", "Subtask description");
        subtask.setEpicID(1);
        manager.createSubtask(subtask);

        Assertions.assertEquals(subtask, manager.getSubtaskById(subtask.getItemID()));
    }

    @Test
    public void getSubtaskByIDTestNotExist() {
        InMemoryTaskManager manager = new InMemoryTaskManager();
        Epic epic = new Epic("Epic", "Epic description");
        manager.createEpic(epic);
        Subtask subtask = new Subtask("Subtask", "Subtask description");
        subtask.setEpicID(1);
        manager.createSubtask(subtask);

        final NullPointerException exception = assertThrows(
                NullPointerException.class,
                () -> manager.getSubtaskById(5));

        assertEquals(null, exception.getMessage());
    }

    // Функция 4: Создание задач.
    @Test
    void addNewTaskTest() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();
        Task task = new Task("Test addNewTask", "Test addNewTask description");
        taskManager.createTask(task);

        final Task savedTask = taskManager.getTaskById(task.getItemID());

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        final List<Task> tasks = taskManager.allTasksList();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");
    }


    @Test
    void addNewEpicTest() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();
        Epic epic = new Epic("Epic addNewEpic", "Epic addNewEpic description");
        taskManager.createEpic(epic);

        final Epic savedEpic = taskManager.getEpicById(epic.getItemID());

        assertNotNull(savedEpic, "Задача не найдена.");
        assertEquals(epic, savedEpic, "Задачи не совпадают.");

        final List<Task> epics = taskManager.allTasksList();

        assertNotNull(epics, "Задачи не возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество задач.");
        assertEquals(epic, epics.get(0), "Задачи не совпадают.");

        // для эпика проверили расчёт статуса
        Subtask subtask = new Subtask("Test addNewTask", "Test addNewTask description");
        subtask.setEpicID(epic.getItemID());
        subtask.setTaskStatus(StatusList.IN_PROGRESS);
        taskManager.createSubtask(subtask);

        assertEquals(taskManager.getEpicById(epic.getItemID()).getTaskStatus(),
                taskManager.getSubtaskById(subtask.getItemID()).getTaskStatus(), "Статусы не совпадают");

    }


    @Test
    void addNewSubtaskTest() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();
        Epic epic = new Epic("Epic addNewEpic", "Epic addNewEpic description");
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask("Subtask addNewSubtask", "Subtask addNewSubtask description");
        subtask.setEpicID(1);
        taskManager.createSubtask(subtask);

        // Для подзадач нужно дополнительно проверить наличие эпика
        assertEquals(1, taskManager.getHashMapSubtasks().get(2).getEpicID());

        final Subtask savedSubtask = taskManager.getSubtaskById(subtask.getItemID());

        assertNotNull(savedSubtask, "Задача не найдена.");
        assertEquals(subtask, savedSubtask, "Задачи не совпадают.");

        final List<Task> subtasks = taskManager.allTasksList();

        assertNotNull(subtasks, "Задачи не возвращаются.");
        assertEquals(2, subtasks.size(), "Неверное количество задач.");
        assertEquals(subtask, subtasks.get(0), "Задачи не совпадают.");
    }

    // Функция 5: Обновление.
    @Test
    public void updateTaskTest() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();
        Task task = new Task("Test addNewTask", "Test addNewTask description");
        taskManager.createTask(task);

        Task newTask = new Task("Test anotherTask", "Test anotherTask description");
        taskManager.createTask(task);
        newTask.setItemID(1);

        taskManager.updateTask(newTask);
        assertEquals("Test anotherTask", taskManager.getTaskById(1).getTaskName());
    }

    @Test
    public void updateEpicTest() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();
        Epic epic = new Epic("Test addNewEpic", "Test addNewEpic description");
        taskManager.createEpic(epic);

        Epic newEpic = new Epic("Test anotherEpic", "Test anotherEpic description");
        taskManager.createEpic(epic);
        newEpic.setItemID(1);

        taskManager.updateEpic(newEpic);
        assertEquals("Test anotherEpic", taskManager.getEpicById(1).getTaskName());
    }

    @Test
    public void updateSubtaskTest() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();
        Epic epic = new Epic("Test addNewEpic", "Test addNewEpic description");
        taskManager.createEpic(epic);

        Subtask subtask = new Subtask("Test addNewTask", "Test addNewTask description");
        subtask.setEpicID(1);
        taskManager.createSubtask(subtask);

        Subtask newSubtask = new Subtask("Test anotherSubtask", "Test anotherSubtask description");
        newSubtask.setEpicID(1);
        taskManager.createSubtask(newSubtask);
        newSubtask.setItemID(2);

        taskManager.updateSubtask(newSubtask);
        assertEquals("Test anotherSubtask", taskManager.getSubtaskById(2).getTaskName());
    }

    // Функция 6: Удаление по идентификатору.
    @Test
    public void deleteTaskTest() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();
        Task task = new Task("Test addNewTask", "Test addNewTask description");
        taskManager.createTask(task);

        assertFalse(taskManager.getHashMapTasks().isEmpty());
        taskManager.deleteTask(1);
        assertTrue(taskManager.getHashMapTasks().isEmpty());
    }

    @Test
    public void deleteEpicTest() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();
        Epic epic = new Epic("Test addNewEpic", "Test addNewEpic description");
        taskManager.createEpic(epic);

        assertFalse(taskManager.getHashMapEpics().isEmpty());
        taskManager.deleteEpic(1);
        assertTrue(taskManager.getHashMapEpics().isEmpty());
    }

    @Test
    public void deleteSubtaskTest() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();
        Epic epic = new Epic("Test addNewEpic", "Test addNewEpic description");
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask("Test addNewSubtask", "Test addNewSubtask description");
        subtask.setEpicID(1);
        taskManager.createSubtask(subtask);

        assertFalse(taskManager.getHashMapSubtasks().isEmpty());
        taskManager.deleteSubtask(2);
        assertTrue(taskManager.getHashMapSubtasks().isEmpty());
    }

    // Функция 7: Получение списка всех подзадач определённого эпика.
    @Test
    public void printSubtasksTest() {

        InMemoryTaskManager taskManager = new InMemoryTaskManager();
        Epic epic = new Epic("Test addNewEpic", "Test addNewEpic description");
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask("Test addNewSubtask", "Test addNewSubtask description");
        subtask.setEpicID(1);
        taskManager.createSubtask(subtask);

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        taskManager.printSubtasks(1);
        assertEquals("2. Наименование: Test addNewSubtask, Описание: Test addNewSubtask description, Статус: NEW, Эпик:1", outContent.toString().trim());

    }

    //9. История просмотра задач
    @Test
    void historyTest() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();
        Epic epic = new Epic("Test addNewEpic", "Test addNewEpic description");
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask("Test addNewSubtask", "Test addNewSubtask description");
        subtask.setEpicID(1);
        taskManager.createSubtask(subtask);
        Task task = new Task("Task", "Task description");
        taskManager.createTask(task);

        taskManager.getEpicById(1);
        taskManager.getSubtaskById(2);
        taskManager.getTaskById(3);


        final List<Task> history = taskManager.getHistoryManager().getHistory();
        assertNotNull(history, "История не пустая.");
        assertEquals(3, history.size(), "История не пустая.");
    }

    //8. Автоматическое обновление статуса эпика
    @Test
    void setStatusTest() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();
        Epic epic = new Epic("Test addNewEpic", "Test addNewEpic description");
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask("Test addNewSubtask", "Test addNewSubtask description");
        subtask.setTaskStatus(StatusList.IN_PROGRESS);
        subtask.setEpicID(1);
        taskManager.createSubtask(subtask);

        taskManager.setStatus(1);


        assertEquals(StatusList.IN_PROGRESS, taskManager.getEpicById(1).getTaskStatus());

    }

}











