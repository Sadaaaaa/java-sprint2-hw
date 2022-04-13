package Data;
import manager.InMemoryTaskManager;
import manager.TaskManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Optional;

class EpicTest {

    @Test
    public void epicStatusShouldBeNew() {
        Epic epic = new Epic("Epic", "Epic description");
        Assertions.assertEquals(StatusList.NEW, epic.getTaskStatus());
    }

    @Test
    public void epicStatusShouldBeNewToo() {
        TaskManager manager = new InMemoryTaskManager();
        Epic epic = new Epic("Epic", "Epic description");
        manager.createEpic(epic);
        Subtask subtask = new Subtask("Subtask", "Description Subtask");
        subtask.setEpicID(epic.getItemID());
        Assertions.assertEquals(StatusList.NEW, epic.getTaskStatus());
    }

    @Test
    public void epicStatusShouldBeDone() {
        TaskManager manager = new InMemoryTaskManager();
        Epic epic = new Epic("Epic", "Description Epic");
        manager.createEpic(epic);
        Subtask subtask = new Subtask("Subtask", "Description Subtask");
//        subtask.setStartTime(LocalDate.of(2030,5,1));
//        subtask.setDuration(Duration.ofDays(5));
        subtask.setEpicID(epic.getItemID());
        manager.createSubtask(subtask);
        subtask.setTaskStatus(StatusList.DONE);
        manager.updateSubtask(subtask);
        Assertions.assertEquals(StatusList.DONE, epic.getTaskStatus());
    }

    @Test
    public void epicStatusShouldBeInProgress() {
        TaskManager manager = new InMemoryTaskManager();
        Epic epic = new Epic("Epic", "Description Epic");
        manager.createEpic(epic);

        Subtask subtask1 = new Subtask("Subtask1", "Description Subtask1");
        subtask1.setEpicID(epic.getItemID());
        manager.createSubtask(subtask1);

        Subtask subtask2 = new Subtask("Subtask2", "Description Subtask2");
        subtask2.setEpicID(epic.getItemID());
        manager.createSubtask(subtask2);
        subtask2.setTaskStatus(StatusList.DONE);
        manager.updateSubtask(subtask2);
        Assertions.assertEquals(StatusList.IN_PROGRESS, epic.getTaskStatus());
    }

    @Test
    public void epicStatusShouldBeInProgressToo() {
        TaskManager manager = new InMemoryTaskManager();
        Epic epic = new Epic("Epic", "Description Epic");
        manager.createEpic(epic);

        Subtask subtask1 = new Subtask("Subtask2", "Description Subtask2");
        subtask1.setEpicID(epic.getItemID());
        manager.createSubtask(subtask1);
        subtask1.setTaskStatus(StatusList.IN_PROGRESS);
        manager.updateSubtask(subtask1);
        Assertions.assertEquals(StatusList.IN_PROGRESS, epic.getTaskStatus());
    }
}