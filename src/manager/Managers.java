package manager;

import history.HistoryManager;
import history.InMemoryHistoryManager;

public class Managers {

    static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
