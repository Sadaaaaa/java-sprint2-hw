package manager;
import Data.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import static org.junit.jupiter.api.Assertions.*;

class FileBackedTasksManagerTest extends TaskManagerTest<FileBackedTasksManager> {

    @BeforeEach
    void objCreate() {
        File file = new File("./src", "backup.csv");
        manager = new FileBackedTasksManager(file.getName());
    }

    @AfterEach
    public void objToNull() {
        manager = null;
    }

    @Test
    public void loadFromFileTest() throws IOException {
        File file = new File("./src", "backup.csv");

        // загружаем непустой файл
        Task task = new Task("Task", "Task descr");
        manager.createTask(task);
        manager.getTaskById(1);
        manager.save();

        manager = FileBackedTasksManager.loadFromFile(file);

        assertEquals(task.toString(), manager.allTasksList().get(0).toString());
        assertEquals(task.toString(), manager.getHistoryManager().getHistory().get(0).toString());

        // загружаем пустой файл (сначала очищаем его)
        PrintWriter writer = new PrintWriter(file);
        writer.print("");
        writer.close();

        manager = FileBackedTasksManager.loadFromFile(file);
        assertEquals(0, manager.allTasksList().size());
        assertEquals(0, manager.getHistoryManager().getHistory().size());
    }

    @Test
    public void saveTest() throws IOException {
        File file = new File("./src", "backup.csv");

        // сохраняем непустой файл
        Task task = new Task("Task", "Task descr");
        manager.createTask(task);

        manager.getTaskById(task.getItemID());
        System.out.println(manager.getHistoryManager().getHistory());

        manager.save();
        assertEquals(task.toString(), FileBackedTasksManager.loadFromFile(file).allTasksList().get(0).toString());
        assertEquals(task.toString(), FileBackedTasksManager.loadFromFile(file).getHistoryManager().getHistory().get(0).toString());


        // сохраняем пустой файл (сначала очищаем его)
        PrintWriter writer = new PrintWriter(file);
        writer.print("");
        writer.close();

        FileBackedTasksManager fileBackedTasksManager2 = new FileBackedTasksManager(file.getName());
        fileBackedTasksManager2.save();
        assertTrue(FileBackedTasksManager.loadFromFile(file).allTasksList().isEmpty());
        assertTrue(FileBackedTasksManager.loadFromFile(file).getHistoryManager().getHistory().isEmpty());
    }


    @Test
    public void toStringTest() {
        Task task = new Task("Task", "Task descr");
        manager.createTask(task);

        assertEquals("1,TASK,Task,NEW,Task descr,null,null", manager.toString(task).trim());

    }

    @Test
    public void fromStringTest() {
        Task task = new Task("Task", "Task descr");
        manager.createTask(task);
        manager.getTaskById(1);

        String s = FileBackedTasksManager.toStringHistory(manager.getHistoryManager());

        assertEquals("1", s.trim());
    }

    @Test
    public void toStringHistoryTest() {
        Task task = new Task("Task", "Task descr");
        manager.createTask(task);

        Task task2 = manager.fromString("1,TASK,Task,NEW,Task descr,null,null");

        assertEquals(task.toString(), task2.toString());
    }

    @Test
    public void fromStringHistoryTest() {
        String testString = "1,2,3";
        assertNotNull(FileBackedTasksManager.fromStringHistory(testString));

        String[] testArray = testString.split(",");
        assertEquals(Integer.parseInt(testArray[0]), FileBackedTasksManager.fromStringHistory(testString).get(0));
        assertEquals(Integer.parseInt(testArray[1]), FileBackedTasksManager.fromStringHistory(testString).get(1));
        assertEquals(Integer.parseInt(testArray[2]), FileBackedTasksManager.fromStringHistory(testString).get(2));


    }

}