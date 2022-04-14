package manager;

import Data.Epic;
import Data.StatusList;
import Data.Subtask;
import Data.Task;
import org.junit.jupiter.api.Test;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest<T extends TaskManager> {
    T manager;

    @Test
    public void generateNewIdTest() {
        assertEquals(manager.generateNewId(), 1);
    }

    // Метод 1: Получение списка всех задач. Тест 1: стандартное поведение
    @Test
    public void allTasksListTest1() throws IOException {
        Task task = new Task("Task", "Task description");

        manager.createTask(task);
        assertEquals("Наименование: Task, Описание: Task description, Статус: NEW", manager.allTasksList().get(0).toString());
    }

    // Метод 1: Получение списка всех задач. Тест 2: пустой список задач
    @Test
    public void allTasksListTest2() {
        Map<Integer, Task> hashmapTask = new HashMap<>();

        List<Task> alltasksList = new ArrayList<>(hashmapTask.values());
        assertArrayEquals(alltasksList.toArray(), manager.allTasksList().toArray());
    }

    // Метод 1: Получение списка всех задач. Тест 3: неверный идентификатор задачи
    @Test
    public void allTasksListTest3() throws IOException {
        Task task = new Task("Task", "Task description");
        task.setItemID(15);
        manager.createTask(task);

        assertEquals(task.getItemID(), manager.allTasksList().get(0).getItemID());
    }

    // Метод 2: Удаление всех задач. Стандартное поведение
    @Test
    public void deleteAllTasksTest1() throws IOException {
        manager.createTask(new Task("Task", "Task description"));
        manager.createTask(new Subtask("Task", "Task description"));
        manager.createTask(new Epic("Task", "Task description"));
        manager.deleteAllTasks();
        Map<Integer, Task> emptyMap = new HashMap<>();

        assertEquals(emptyMap.isEmpty(), (manager.getHashMapTasks().isEmpty() && manager.getHashMapEpics().isEmpty()
                && manager.getHashMapSubtasks().isEmpty()));
    }

    // Метод 2: Удаление всех задач. Пустой перечень задач
    @Test
    public void deleteAllTasksTest2() {
        manager.deleteAllTasks();
        Map<Integer, Task> emptyMap = new HashMap<>();

        assertEquals(emptyMap.isEmpty(), (manager.getHashMapTasks().isEmpty() && manager.getHashMapEpics().isEmpty()
                && manager.getHashMapSubtasks().isEmpty()));
    }


    // Метод 3: Получение по идентификатору.
    @Test
    public void getTaskByIDTestNormal() throws IOException {
        Task task = new Task("Task", "Task description");
        manager.createTask(task);

        assertEquals(task, manager.getTaskById(task.getItemID()));
    }

    @Test
    public void getTaskByIDTestNotExist() throws IOException {
        Task task = new Task("Task", "Task description");
        manager.createTask(task);

        final NullPointerException exception = assertThrows(
                NullPointerException.class,
                () -> manager.getTaskById(5));

        assertNull(exception.getMessage());
    }

    @Test
    public void getEpicByIDTestNormal() {
        Epic epic = new Epic("Epic", "Epic description");
        manager.createEpic(epic);

        assertEquals(epic, manager.getEpicById(epic.getItemID()));
    }

    @Test
    public void getEpicByIDTestNotExist() {
        Epic epic = new Epic("Epic", "Epic description");
        manager.createEpic(epic);

        final NullPointerException exception = assertThrows(
                NullPointerException.class,
                () -> manager.getEpicById(5));

        assertNull(exception.getMessage());
    }

    @Test
    public void getSubtaskByIDTestNormal() {
        Epic epic = new Epic("Epic", "Epic description");
        manager.createEpic(epic);
        Subtask subtask = new Subtask("Subtask", "Subtask description");
        subtask.setEpicID(1);
        manager.createSubtask(subtask);

        assertEquals(subtask, manager.getSubtaskById(subtask.getItemID()));
    }

    @Test
    public void getSubtaskByIDTestNotExist() {
        Epic epic = new Epic("Epic", "Epic description");
        manager.createEpic(epic);
        Subtask subtask = new Subtask("Subtask", "Subtask description");
        subtask.setEpicID(1);
        manager.createSubtask(subtask);

        final NullPointerException exception = assertThrows(
                NullPointerException.class,
                () -> manager.getSubtaskById(5));

        assertNull(exception.getMessage());
    }

    // Метод 4: Создание задач.
    @Test
    void addNewTaskTest() throws IOException {
        Task task = new Task("Test addNewTask", "Test addNewTask description");
        manager.createTask(task);

        final Task savedTask = manager.getTaskById(task.getItemID());

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        final List<Task> tasks = manager.allTasksList();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");
    }


    @Test
    void addNewEpicTest() {
        Epic epic = new Epic("Epic addNewEpic", "Epic addNewEpic description");
        manager.createEpic(epic);

        final Epic savedEpic = manager.getEpicById(epic.getItemID());

        assertNotNull(savedEpic, "Задача не найдена.");
        assertEquals(epic, savedEpic, "Задачи не совпадают.");

        final List<Task> epics = manager.allTasksList();

        assertNotNull(epics, "Задачи не возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество задач.");
        assertEquals(epic, epics.get(0), "Задачи не совпадают.");

        // для эпика проверили расчёт статуса
        Subtask subtask = new Subtask("Test addNewTask", "Test addNewTask description");
        subtask.setEpicID(epic.getItemID());
        subtask.setTaskStatus(StatusList.IN_PROGRESS);
        manager.createSubtask(subtask);

        assertEquals(manager.getEpicById(epic.getItemID()).getTaskStatus(),
                manager.getSubtaskById(subtask.getItemID()).getTaskStatus(), "Статусы не совпадают");

    }


    @Test
    void addNewSubtaskTest() {
        Epic epic = new Epic("Epic addNewEpic", "Epic addNewEpic description");
        manager.createEpic(epic);
        Subtask subtask = new Subtask("Subtask addNewSubtask", "Subtask addNewSubtask description");
        subtask.setEpicID(1);
        manager.createSubtask(subtask);

        // Для подзадач нужно дополнительно проверить наличие эпика
        assertEquals(1, manager.getHashMapSubtasks().get(2).getEpicID());

        final Subtask savedSubtask = manager.getSubtaskById(subtask.getItemID());

        assertNotNull(savedSubtask, "Задача не найдена.");
        assertEquals(subtask, savedSubtask, "Задачи не совпадают.");

        final List<Task> subtasks = manager.allTasksList();

        assertNotNull(subtasks, "Задачи не возвращаются.");
        assertEquals(2, subtasks.size(), "Неверное количество задач.");
        assertEquals(subtask, subtasks.get(0), "Задачи не совпадают.");
    }

    // Метод 5: Обновление.
    @Test
    public void updateTaskTest() throws IOException {
        Task task = new Task("Test addNewTask", "Test addNewTask description");
        manager.createTask(task);

        Task newTask = new Task("Test anotherTask", "Test anotherTask description");
        manager.createTask(task);
        newTask.setItemID(1);

        manager.updateTask(newTask);
        assertEquals("Test anotherTask", manager.getTaskById(1).getTaskName());
    }

    @Test
    public void updateEpicTest() {
        Epic epic = new Epic("Test addNewEpic", "Test addNewEpic description");
        manager.createEpic(epic);

        Epic newEpic = new Epic("Test anotherEpic", "Test anotherEpic description");
        manager.createEpic(epic);
        newEpic.setItemID(1);

        manager.updateEpic(newEpic);
        assertEquals("Test anotherEpic", manager.getEpicById(1).getTaskName());
    }

    @Test
    public void updateSubtaskTest() {
        Epic epic = new Epic("Test addNewEpic", "Test addNewEpic description");
        manager.createEpic(epic);

        Subtask subtask = new Subtask("Test addNewTask", "Test addNewTask description");
        subtask.setEpicID(1);
        manager.createSubtask(subtask);

        Subtask newSubtask = new Subtask("Test anotherSubtask", "Test anotherSubtask description");
        newSubtask.setEpicID(1);
        manager.createSubtask(newSubtask);
        newSubtask.setItemID(2);

        manager.updateSubtask(newSubtask);
        assertEquals("Test anotherSubtask", manager.getSubtaskById(2).getTaskName());
    }

    // Метод 6: Удаление по идентификатору.
    @Test
    public void deleteTaskTest() throws IOException {
        Task task = new Task("Test addNewTask", "Test addNewTask description");
        manager.createTask(task);

        assertFalse(manager.getHashMapTasks().isEmpty());
        manager.deleteTask(1);
        assertTrue(manager.getHashMapTasks().isEmpty());
    }

    @Test
    public void deleteEpicTest() {
        Epic epic = new Epic("Test addNewEpic", "Test addNewEpic description");
        manager.createEpic(epic);

        assertFalse(manager.getHashMapEpics().isEmpty());
        manager.deleteEpic(1);
        assertTrue(manager.getHashMapEpics().isEmpty());
    }

    @Test
    public void deleteSubtaskTest() {
        Epic epic = new Epic("Test addNewEpic", "Test addNewEpic description");
        manager.createEpic(epic);
        Subtask subtask = new Subtask("Test addNewSubtask", "Test addNewSubtask description");
        subtask.setEpicID(1);
        manager.createSubtask(subtask);

        assertFalse(manager.getHashMapSubtasks().isEmpty());
        manager.deleteSubtask(2);
        assertTrue(manager.getHashMapSubtasks().isEmpty());
    }

    // Метод 7: Получение списка всех подзадач определённого эпика.
    @Test
    public void printSubtasksTest() {
        Epic epic = new Epic("Test addNewEpic", "Test addNewEpic description");
        manager.createEpic(epic);
        Subtask subtask = new Subtask("Test addNewSubtask", "Test addNewSubtask description");
        subtask.setEpicID(1);
        manager.createSubtask(subtask);

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        manager.printSubtasks(1);
        assertEquals("2. Наименование: Test addNewSubtask, Описание: Test addNewSubtask description, Статус: NEW, Эпик:1", outContent.toString().trim());
    }

    // Метод 9. История просмотра задач
    @Test
    void historyTest() throws IOException {
        Epic epic = new Epic("Test addNewEpic", "Test addNewEpic description");
        manager.createEpic(epic);
        Subtask subtask = new Subtask("Test addNewSubtask", "Test addNewSubtask description");
        subtask.setEpicID(1);
        manager.createSubtask(subtask);
        Task task = new Task("Task", "Task description");
        manager.createTask(task);

        manager.getEpicById(1);
        manager.getSubtaskById(2);
        manager.getTaskById(3);

        final List<Task> history = manager.getHistoryManager().getHistory();
        assertNotNull(history, "История не пустая.");
        assertEquals(3, history.size(), "История не пустая.");
    }

    // Метод 8. Автоматическое обновление статуса эпика
    @Test
    void setStatusTest() {
        Epic epic = new Epic("Test addNewEpic", "Test addNewEpic description");
        manager.createEpic(epic);
        Subtask subtask = new Subtask("Test addNewSubtask", "Test addNewSubtask description");
        subtask.setTaskStatus(StatusList.IN_PROGRESS);
        subtask.setEpicID(1);
        manager.createSubtask(subtask);
        manager.setStatus(1);

        assertEquals(StatusList.IN_PROGRESS, manager.getEpicById(1).getTaskStatus());
    }
}











