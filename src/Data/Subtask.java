package Data;

public class Subtask extends Task {
    int epicID = 0;

    public Subtask(String taskName, String taskDetails) {   // Конструктор класса Data.Subtask
        super(taskName, taskDetails);
    }

    public int getEpicID() {    // Геттер для получения принадлежности подзадачи к определенному эпику
        return epicID;
    }

    public void setEpicID(int epicID) {     // Сеттер для установления принадлежности подзадачи к определенному эпику
        this.epicID = epicID;
    }

    public String toString() {
        return "Наименование: " + taskName +
                ", Описание: " + taskDetails +
                ", Статус: " + taskStatus +
                ", Эпик:" + getEpicID();
    }
}
