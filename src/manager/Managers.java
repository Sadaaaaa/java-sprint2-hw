package manager;

import history.HistoryManager;
import history.InMemoryHistoryManager;

public class Managers {

    static TaskManager getDefault(String url) {
        return new HTTPTaskManager(url);
    }

    static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
