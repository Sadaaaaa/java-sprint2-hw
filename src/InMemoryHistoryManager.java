import Data.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private ArrayList<Task> historyList = new ArrayList<>();

    @Override
    public void add(Task task) {
        historyList.add(task);
        if (historyList.size() > 10) {
            historyList.remove(0);
        }
    }

    @Override
    public List<Task> getHistory() {
        return historyList;
    }
}
