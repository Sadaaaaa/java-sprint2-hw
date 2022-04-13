package Data;

import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Epic extends Task {
    private List<Integer> subtaskIDList = new ArrayList<>();
    private String type = "EPIC";
    private LocalDate endTime;

    public Epic(String taskName, String taskDetails) {
        super(taskName, taskDetails);
        setDuration(Duration.ZERO);
    }

    @Override
    public String getType() {
        return type;
    }

    public List<Integer> getSubtaskIDList() {
        return subtaskIDList;
    }

    public void setSubtaskIDList(int subtaskID) {
        subtaskIDList.add(subtaskID);
    }

    public void setSubtaskIDList(List<Integer> subtaskIDList) {
        this.subtaskIDList = subtaskIDList;
    }

    public void setEpicStatus(StatusList status) {
        super.setTaskStatus(status);
    }

    public LocalDate getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDate endTime) {
        this.endTime = endTime;
    }

    public String toString() {
        return "Наименование: " + getTaskName() +
                ", Описание: " + getTaskDetails() +
                ", Статус: " + getTaskStatus() +
                ", Кол-во подзадач:" + getSubtaskIDList().size();
    }
}
