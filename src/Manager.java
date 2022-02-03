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
    public void printAllTasks() {
        System.out.println("Задачи:");
        for (Map.Entry<Integer, Task> entry1 : hashMapTasks.entrySet()) {
            Integer key = entry1.getKey();
            Task value = entry1.getValue();
            System.out.println(key + ". " + value.toString());
        }

        System.out.println("Подзадачи:");
        for (Map.Entry<Integer, Subtask> entry2 : hashMapSubtasks.entrySet()) {
            Integer key = entry2.getKey();
            Subtask value = entry2.getValue();
            System.out.println(key + ". " + value.toString());
        }

        System.out.println("Эпики:");
        for (Map.Entry<Integer, Epic> entry3 : hashMapEpics.entrySet()) {
            Integer key = entry3.getKey();
            Epic value = entry3.getValue();
            System.out.println(key + ". " + value);
        }
    }

    //2. Удаление всех задач.
    public void deleteAllTasks() {
        hashMapTasks.clear();
        hashMapSubtasks.clear();
        hashMapEpics.clear();
        System.out.println("Все задачи удалены.");
    }

    //3. Получение по идентификатору.
    public void getTasksByID(int userInput) {
        if (hashMapTasks.containsKey(userInput)) {
            System.out.println(userInput + ". " + hashMapTasks.get(userInput).toString());
        } else if (hashMapSubtasks.containsKey(userInput)) {
            System.out.println(userInput + ". " + hashMapSubtasks.get(userInput).toString());
        } else if (hashMapEpics.containsKey(userInput)) {
            System.out.println(userInput + ". " + hashMapEpics.get(userInput).toString());
        } else {
            System.out.println("Такой задачи не существует.");
        }
    }

    //4. Создание задач.
    public void createTask(Task task) {
        int newID = generateNewId();              // Узнаем ID нового таска
        System.out.println("Задаче присвоен ID: " + newID);
        task.setItemID(newID); // добавлено: запись ID в поле объекта
        hashMapTasks.put(newID, task);
        System.out.println("Задача [" + newID + ". " + hashMapTasks.get(newID).toString() + "] создана.");
    }

    public void createSubtask(Subtask subtask, int epicID) {
        if (hashMapEpics.containsKey(epicID)) {
            int newID = generateNewId();           // Узнаем ID нового сабтаска
            System.out.println("Подзадаче присвоен ID: " + newID);
            subtask.setItemID(newID); // добавлено: запись ID в поле объекта
            subtask.setEpicID(epicID);
            hashMapSubtasks.put(newID, subtask);
            System.out.println("Подзадача [" + newID + ". " + hashMapSubtasks.get(newID).toString() + "] создана.");
            hashMapEpics.get(epicID).setSubtaskIDList(newID);
            setStatus(epicID); // добавлено: обновление статуса эпика
        } else {
            System.out.println("Такого эпика не существует. Сперва создайте подходящий эпик.");
        }
    }

    public void createEpic(Epic epic) {
        int newID = generateNewId();           // Узнаем ID нового таска
        System.out.println("Эпику присвоен ID: " + newID);
        epic.setItemID(newID); // добавлено: запись ID в поле объекта
        hashMapEpics.put(newID, epic);
        System.out.println("Эпик [" + newID + ". " + hashMapEpics.get(newID).toString() + "] создан.");
    }


    //5. Обновление.
    public void updateTask(Task oldTask, Task newTask) {
        int ID = oldTask.getItemID(); // добавлено: определяем ID таска, который нужно обновить
        if (hashMapTasks.containsKey(ID)) {
            hashMapTasks.put(ID, newTask);
            System.out.println("Задача [" + ID + ". " + hashMapTasks.get(ID).toString() + "] обновлена.");
        } else {
            System.out.println("Задача с таким ID не найдена");
        }
    }

    public void updateSubtask(Subtask oldSubtask, Subtask newSubtask) {
        int ID = oldSubtask.getItemID(); // добавлено: определяем ID сабтаска, который нужно обновить
        if (hashMapSubtasks.containsKey(ID)) {
            int epicID = hashMapSubtasks.get(ID).getEpicID();
            ArrayList<Integer> subtaskIDList = hashMapEpics.get(epicID).getSubtaskIDList();
            subtaskIDList.remove(Integer.valueOf(ID)); // метод удаляет подзадачу из списка, прикрепленного к эпику
            hashMapSubtasks.put(ID, newSubtask);
            newSubtask.setEpicID(epicID);
            hashMapEpics.get(epicID).setSubtaskIDList(ID);
            setStatus(epicID); // добавлено: обновление статуса эпика
            System.out.println("Подзадача [" + ID + ". " + hashMapSubtasks.get(ID).toString() + "] обновлена.");
        } else {
            System.out.println("Подзадача с таким ID не найдена");
        }
    }

    public void updateEpic(Epic oldEpic, Epic newEpic) {
        int ID = oldEpic.getItemID(); // добавлено: определяем ID эпика, который нужно обновить
        if (hashMapEpics.containsKey(ID)) {
            ArrayList<Integer> subtaskIDList = hashMapEpics.get(ID).getSubtaskIDList(); // сохранили привязку сабтасков и эпика
            hashMapEpics.put(ID, newEpic); // создали новый эпик

            for (Integer subtaskID : subtaskIDList) {
                hashMapEpics.get(ID).setSubtaskIDList(subtaskID); // положили сабтаски в новый эпик
            }
            setStatus(ID); // добавлено: обновление статуса эпика
            System.out.println("Эпик [" + ID + ". " + hashMapEpics.get(ID).toString() + "] обновлен.");
        } else {
            System.out.println("Эпик с таким ID не найден");
        }
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

    // 9. Обновление статусов
    public void statusUpdate(int ID, int status) {
        if (hashMapTasks.containsKey(ID)) {
            Task currentTask = hashMapTasks.get(ID);
            if (status == 1) {
                currentTask.setTaskStatus("NEW");
            } else if (status == 2) {
                currentTask.setTaskStatus("IN_PROGRESS");
            } else if (status == 3) {
                currentTask.setTaskStatus("DONE");
            }
            System.out.println("Статус задачи выставлен как: " + currentTask.getTaskStatus());
        } else if (hashMapSubtasks.containsKey(ID)) {
            Subtask currentSubtask = hashMapSubtasks.get(ID);
            if (status == 1) {
                currentSubtask.setTaskStatus("NEW");
                int epicID = currentSubtask.getEpicID();
                setStatus(epicID);
            } else if (status == 2) {
                currentSubtask.setTaskStatus("IN_PROGRESS");
                int epicID = currentSubtask.getEpicID();
                setStatus(epicID);
            } else if (status == 3) {
                currentSubtask.setTaskStatus("DONE");
                int epicID = currentSubtask.getEpicID();
                setStatus(epicID);
            }
            System.out.println("Статус подзадачи выставлен как: " + currentSubtask.getTaskStatus());
        } else {
            System.out.println("ID не найден.");
        }
    }
}
