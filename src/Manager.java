import Data.Epic;
import Data.Subtask;
import Data.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Manager {
    private int taskID = 1;

    public int generateNewId() {
        return taskID++;
    }

    HashMap<Integer, Task> hashMapTasks = new HashMap<>();
    HashMap<Integer, Subtask> hashMapSubtasks = new HashMap<>();
    HashMap<Integer, Epic> hashMapEpics = new HashMap<>();

    //1. Получение списка всех задач.
    public ArrayList<Object> allTasksList() {
        ArrayList<Object> allTasks = new ArrayList<>();
        allTasks.addAll(hashMapTasks.entrySet());
        allTasks.addAll(hashMapSubtasks.entrySet());
        allTasks.addAll(hashMapEpics.entrySet());
        return allTasks;
    }

    //2. Удаление всех задач.
    public void deleteAllTasks() {
        hashMapTasks.clear();
        hashMapSubtasks.clear();
        hashMapEpics.clear();
        System.out.println("Все задачи удалены.");
    }

    //3. Получение по идентификатору.
    public Task getTaskById(int ID) {
        return hashMapTasks.get(ID);
    }

    public Subtask getSubtaskById(int ID) {
        return hashMapSubtasks.get(ID);
    }

    public Epic getEpicById(int ID) {
        return hashMapEpics.get(ID);
    }


    //4. Создание задач.
    public void createTask(Task task) {
        int newID = generateNewId(); // Узнаем ID нового таска
        System.out.println("Задаче присвоен ID: " + newID);
        task.setItemID(newID); // запись ID в поле объекта
        hashMapTasks.put(newID, task);
        System.out.println("Задача [" + newID + ". " + hashMapTasks.get(newID).toString() + "] создана.");
    }

    public void createSubtask(Subtask subtask) {
        int newID = generateNewId(); // Узнаем ID нового сабтаска
        System.out.println("Подзадаче присвоен ID: " + newID);
        subtask.setItemID(newID); // добавлено: запись ID в поле объекта
        int epicID = subtask.getEpicID();
        hashMapSubtasks.put(newID, subtask);
        System.out.println("Подзадача [" + newID + ". " + hashMapSubtasks.get(newID).toString() + "] создана.");
        hashMapEpics.get(epicID).setSubtaskIDList(newID);
        setStatus(epicID); // обновили статус эпика
    }

    public void createEpic(Epic epic) {
        int newID = generateNewId();           // Узнаем ID нового таска
        System.out.println("Эпику присвоен ID: " + newID);
        epic.setItemID(newID); // запись ID в поле объекта
        hashMapEpics.put(newID, epic);
        System.out.println("Эпик [" + newID + ". " + hashMapEpics.get(newID).toString() + "] создан.");
    }


    //5. Обновление.
    public void updateTask(Task newTask) {
        int ID = newTask.getItemID();
        hashMapTasks.put(ID, newTask);
        System.out.println("Задача [" + ID + ". " + hashMapTasks.get(ID).toString() + "] обновлена.");
    }

    public void updateSubtask(Subtask newSubtask) {
        int ID = newSubtask.getItemID(); // узнали ID подзадачи, которую необходимо обновить
        int epicID = hashMapSubtasks.get(ID).getEpicID(); // узнали ID эпика, которому принадлежит подзадача
        ArrayList<Integer> subtaskIDList = hashMapEpics.get(epicID).getSubtaskIDList();
        subtaskIDList.remove(Integer.valueOf(ID)); // метод удаляет подзадачу из списка, прикрепленного к эпику
        hashMapSubtasks.put(ID, newSubtask); // положили подзадачу в hashmap
        newSubtask.setEpicID(epicID); // установили соответствие подзадачи эпику
        hashMapEpics.get(epicID).setSubtaskIDList(ID); // добавили подзадачу в лист подзадач, принадлежащих эпику
        setStatus(epicID); // обновили статус эпика
        System.out.println("Подзадача [" + ID + ". " + hashMapSubtasks.get(ID).toString() + "] обновлена.");
    }

    public void updateEpic(Epic newEpic) {
        int ID = newEpic.getItemID();
        ArrayList<Integer> subtaskIDList = hashMapEpics.get(ID).getSubtaskIDList(); // сохранили привязку подзадач и эпика
        newEpic.setSubtaskIDList(subtaskIDList); // положили в объект newEpic лист с подзадачами
        hashMapEpics.put(ID, newEpic); // положили эпик в hashmap
        setStatus(ID); // обновили статус эпика
        System.out.println("Эпик [" + ID + ". " + hashMapEpics.get(ID).toString() + "] обновлен.");
    }

    //6. Удаление по идентификатору.
    public void deleteTaskByID(int ID) {
        if (hashMapTasks.containsKey(ID)) {
            System.out.println("Задача [" + ID + ". " + hashMapTasks.get(ID).toString() + "] удалена.");
            hashMapTasks.remove(ID);
        } else if (hashMapSubtasks.containsKey(ID)) {
            System.out.println("Подзадача [" + ID + ". " + hashMapSubtasks.get(ID).toString() + "] удалена.");
            int epicID = hashMapSubtasks.get(ID).getEpicID();
            ArrayList<Integer> subtaskIDList = hashMapEpics.get(epicID).getSubtaskIDList();
            subtaskIDList.remove(Integer.valueOf(ID)); // метод удаляет подзадачу из списка, прикрепленного к эпику
            hashMapSubtasks.remove(ID);
            setStatus(epicID); // добавлено: обновление статуса эпика
        } else if (hashMapEpics.containsKey(ID)) {
            ArrayList<Integer> subtaskIDList = hashMapEpics.get(ID).getSubtaskIDList(); // получаем лист с подзадачами данного эпика
            System.out.println("Эпик [" + ID + ". " + hashMapEpics.get(ID).toString() + "] удален.");
            for (Integer subtaskID : subtaskIDList) {
                hashMapSubtasks.remove(subtaskID); // удалили все подзадачи эпика
            }
            hashMapEpics.remove(ID); //удалили эпик
        } else {
            System.out.println("Такой задачи не существует.");
        }
    }

    //7. Получение списка всех подзадач определённого эпика.
    public void printSubtasks(int ID) {
        for (Map.Entry<Integer, Subtask> entry : hashMapSubtasks.entrySet()) {
            Integer key = entry.getKey();
            Subtask value = entry.getValue();
            if (value.getEpicID() == ID) {
                System.out.println(key + ". " + value);
            } else {
                System.out.println("У данного эпика нет подзадач либо эпик не существует");
            }
        }
    }

    //8. Автоматическое обновление статуса эпика
    public void setStatus(int epicID) {
        ArrayList<String> listOfStatus = new ArrayList<>();
        ArrayList<Integer> listSubtaskID = hashMapEpics.get(epicID).getSubtaskIDList();
        if (listSubtaskID.size() == 0) {
            hashMapEpics.get(epicID).setEpicStatus("NEW");
        } else {
            for (Integer ID : listSubtaskID) {
                listOfStatus.add(hashMapSubtasks.get(ID).getTaskStatus());
                if (listOfStatus.contains("NEW") && !listOfStatus.contains("IN_PROGRESS") && !listOfStatus.contains("DONE")) {
                    hashMapEpics.get(epicID).setEpicStatus("NEW");
                } else if (listOfStatus.contains("DONE") && !listOfStatus.contains("IN_PROGRESS") && !listOfStatus.contains("NEW")) {
                    hashMapEpics.get(epicID).setEpicStatus("DONE");
                } else {
                    hashMapEpics.get(epicID).setEpicStatus("IN_PROGRESS");
                }
            }
        }
    }
}
