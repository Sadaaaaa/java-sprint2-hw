package manager;
import Data.Task;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import static org.junit.jupiter.api.Assertions.*;

class FileBackedTasksManagerTest extends TaskManagerTest {


    @Test
    public void loadFromFileTest() throws IOException {
        File file = new File("./src", "backup.csv");
        FileBackedTasksManager fileBackedTasksManager = new FileBackedTasksManager(file.getName());

        // загружаем непустой файл
        Task task = new Task("Task", "Task descr");
        fileBackedTasksManager.createTask(task);
        fileBackedTasksManager.getTaskById(1);
        fileBackedTasksManager.save();

        fileBackedTasksManager = FileBackedTasksManager.loadFromFile(file);

        assertEquals(task.toString(), fileBackedTasksManager.allTasksList().get(0).toString());
        assertEquals(task.toString(), fileBackedTasksManager.getHistoryManager().getHistory().get(0).toString());

        // загружаем пустой файл (сначала очищаем его)
        PrintWriter writer = new PrintWriter(file);
        writer.print("");
        writer.close();

        fileBackedTasksManager = FileBackedTasksManager.loadFromFile(file);
        assertEquals(0, fileBackedTasksManager.allTasksList().size());
        assertEquals(0, fileBackedTasksManager.getHistoryManager().getHistory().size());
    }

    @Test
    public void saveTest() throws IOException {
        File file = new File("./src", "backup.csv");
        FileBackedTasksManager fileBackedTasksManager = new FileBackedTasksManager(file.getName());

        // сохраняем непустой файл
        Task task = new Task("Task", "Task descr");
        fileBackedTasksManager.createTask(task);

        fileBackedTasksManager.getTaskById(task.getItemID());
        System.out.println(fileBackedTasksManager.getHistoryManager().getHistory());

        fileBackedTasksManager.save();
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
        File file = new File("./src", "backup.csv");
        FileBackedTasksManager fileBackedTasksManager = new FileBackedTasksManager(file.getName());
        Task task = new Task("Task", "Task descr");
        fileBackedTasksManager.createTask(task);

        assertEquals("1,TASK,Task,NEW,Task descr,null,null", fileBackedTasksManager.toString(task).trim());

    }

    @Test
    public void fromStringTest() {
        File file = new File("./src", "backup.csv");
        FileBackedTasksManager fileBackedTasksManager = new FileBackedTasksManager(file.getName());
        Task task = new Task("Task", "Task descr");
        fileBackedTasksManager.createTask(task);
        fileBackedTasksManager.getTaskById(1);

        String s = FileBackedTasksManager.toStringHistory(fileBackedTasksManager.getHistoryManager());

        assertEquals("1", s.trim());
    }

    @Test
    public void toStringHistoryTest() {
        File file = new File("./src", "backup.csv");
        FileBackedTasksManager fileBackedTasksManager = new FileBackedTasksManager(file.getName());
        Task task = new Task("Task", "Task descr");
        fileBackedTasksManager.createTask(task);

        Task task2 = fileBackedTasksManager.fromString("1,TASK,Task,NEW,Task descr,null,null");

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