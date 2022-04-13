package history;

import Data.Task;
import manager.InMemoryTaskManager;
import manager.TaskManager;
import org.junit.jupiter.api.Test;

import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    @Test
    void add() {
        HistoryManager historyManager = new InMemoryHistoryManager();
        Task task = new Task("Test addNewTask", "Test addNewTask description");
        historyManager.add(task);

        assertEquals(task, historyManager.getHistory().get(0));
    }

    @Test
    void remove() {
        HistoryManager historyManager = new InMemoryHistoryManager();
        Task task = new Task("Test addNewTask", "Test addNewTask description");

        historyManager.add(task);
        historyManager.add(task);
        assertEquals(1, historyManager.getHistory().size());

        historyManager.remove(task.getItemID());
        assertEquals(0, historyManager.getHistory().size());

        task.setItemID(1);
        Task task2 = new Task("Test2 addNewTask", "Test2 addNewTask description");
        task2.setItemID(2);
        Task task3 = new Task("Test3 addNewTask", "Test3 addNewTask description");
        task3.setItemID(3);

        // удаление из начала
        historyManager.add(task);
        historyManager.add(task2);
        historyManager.add(task3);
        assertEquals(3, historyManager.getHistory().size());

        assertEquals(task.toString(), historyManager.getHistory().get(0).toString());
        historyManager.remove(1);
        assertEquals(task2.toString(), historyManager.getHistory().get(0).toString());

        // удаление из середины
        historyManager.getHistory().clear();
        historyManager.add(task);
        historyManager.add(task2);
        historyManager.add(task3);
        assertEquals(3, historyManager.getHistory().size());

        assertEquals(task2.toString(), historyManager.getHistory().get(1).toString());
        historyManager.remove(2);
        assertEquals(task3.toString(), historyManager.getHistory().get(1).toString());

        // удаление последнего айтема
        historyManager.getHistory().clear();
        historyManager.add(task);
        historyManager.add(task2);
        historyManager.add(task3);
        assertEquals(3, historyManager.getHistory().size());

        assertEquals(task3.toString(), historyManager.getHistory().get(2).toString());
        historyManager.remove(3);
        assertEquals(2, historyManager.getHistory().size());
        assertEquals(task.toString(), historyManager.getHistory().get(0).toString());
        assertEquals(task2.toString(), historyManager.getHistory().get(1).toString());
    }

    @Test
    void getHistoryTest() {
        HistoryManager historyManager = new InMemoryHistoryManager();
        Task task = new Task("Test addNewTask", "Test addNewTask description");

        assertEquals(0, historyManager.getHistory().size());

        historyManager.add(task);
        assertEquals(task, historyManager.getHistory().get(0));
    }
}