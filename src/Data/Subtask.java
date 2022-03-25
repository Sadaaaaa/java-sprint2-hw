package Data;

public class Subtask extends Task {
    private int epicID = 0;
    private String type = "SUBTASK";

    public Subtask(String taskName, String taskDetails) {   // Конструктор класса Data.Subtask
        super(taskName, taskDetails);
    }

    @Override
    public String getType() {
        return type;
    }

    public int getEpicID() {    // Геттер для получения принадлежности подзадачи к определенному эпику
        return epicID;
    }

    public void setEpicID(int epicID) {     // Сеттер для установления принадлежности подзадачи к определенному эпику
        this.epicID = epicID;
    }

    public String toString() {
        return "Наименование: " + getTaskName() +
                ", Описание: " + getTaskDetails() +
                ", Статус: " + getTaskStatus() +
                ", Эпик:" + getEpicID();
    }
}
