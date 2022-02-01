import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Manager {
    int taskID = 0;

    Scanner scanner = new Scanner(System.in);

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
        taskID++;              // Узнаем ID нового таска
        System.out.println("Задаче присвоен ID: " + taskID);
        hashMapTasks.put(taskID, task);
        System.out.println("Задача [" + taskID + ". " + hashMapTasks.get(taskID).toString() + "] создана.");
    }

    public void createSubtask(Subtask subtask) {
        System.out.println("Введите ID эпика, к которому относится подзадача:");
        int epicID = scanner.nextInt();
        if (hashMapEpics.containsKey(epicID)) {
            taskID++;              // Узнаем ID нового таска
            System.out.println("Подзадаче присвоен ID: " + taskID);
            subtask.setEpicID(epicID);
            hashMapSubtasks.put(taskID, subtask);
            System.out.println("Подзадача [" + taskID + ". " + hashMapSubtasks.get(taskID).toString() + "] создана.");
            hashMapEpics.get(epicID).setSubtaskIDList(taskID);
        } else {
            System.out.println("Такого эпика не существует. Сперва создайте подходящий эпик.");
        }
    }

    public void createEpic(Epic epic) {
        taskID++;              // Узнаем ID нового таска
        System.out.println("Эпику присвоен ID: " + taskID);
        hashMapEpics.put(taskID, epic);
        System.out.println("Эпик [" + taskID + ". " + hashMapEpics.get(taskID).toString() + "] создан.");
    }


    //5. Обновление.
    public void updateTask(int taskID, Task task) {
        if (hashMapTasks.containsKey(taskID)) {
            hashMapTasks.remove(taskID);
            hashMapTasks.put(taskID, task);
            System.out.println("Задача [" + taskID + ". " + hashMapTasks.get(taskID).toString() + "] обновлена.");
        } else {
            System.out.println("Задача с таким ID не найдена");
        }
    }

    public void updateSubtask(int taskID, Subtask subtask) {
        if (hashMapSubtasks.containsKey(taskID)) {
            int epicID = hashMapSubtasks.get(taskID).getEpicID();
            ArrayList<Integer> subtaskIDList = hashMapEpics.get(epicID).getSubtaskIDList();
            subtaskIDList.remove(Integer.valueOf(taskID)); // метод удаляет подзадачу из списка, прикрепленного к эпику
            hashMapSubtasks.remove(taskID);
            hashMapSubtasks.put(taskID, subtask);
            subtask.setEpicID(epicID);
            hashMapEpics.get(epicID).setSubtaskIDList(taskID);
            System.out.println("Подзадача [" + taskID + ". " + hashMapSubtasks.get(taskID).toString() + "] обновлена.");
        } else {
            System.out.println("Подзадача с таким ID не найдена");
        }
    }

    public void updateEpic(int taskID, Epic epic) {
        if (hashMapEpics.containsKey(taskID)) {
            ArrayList<Integer> subtaskIDList = hashMapEpics.get(taskID).getSubtaskIDList(); // сохранили привязку сабтасков и эпика
            hashMapEpics.remove(taskID); // удалили старый эпик
            hashMapEpics.put(taskID, epic); // создали новый эпик

            for (Integer subtaskID : subtaskIDList) {
                hashMapEpics.get(taskID).setSubtaskIDList(subtaskID); // положили сабтаски в новый эпик
            }

            System.out.println("Эпик [" + taskID + ". " + hashMapEpics.get(taskID).toString() + "] обновлен.");
        } else {
            System.out.println("Эпик с таким ID не найден");
        }
    }

    //6. Удаление по идентификатору.
    public void deleteTaskByID(int userInput) {
        if (hashMapTasks.containsKey(userInput)) {
            System.out.println("Задача [" + userInput + ". " + hashMapTasks.get(userInput).toString() + "] удалена.");
            hashMapTasks.remove(userInput);
        } else if (hashMapSubtasks.containsKey(userInput)) {
            System.out.println("Подзадача [" + userInput + ". " + hashMapSubtasks.get(userInput).toString() + "] удалена.");
            int epicID = hashMapSubtasks.get(userInput).getEpicID();
            ArrayList<Integer> subtaskIDList = hashMapEpics.get(epicID).getSubtaskIDList();
            subtaskIDList.remove(Integer.valueOf(userInput)); // метод удаляет подзадачу из списка, прикрепленного к эпику
            hashMapSubtasks.remove(userInput);
        } else if (hashMapEpics.containsKey(userInput)) {
            ArrayList<Integer> subtaskIDList = hashMapEpics.get(userInput).getSubtaskIDList(); // получаем лист с подзадачами данного эпика
            System.out.println("Эпик [" + userInput + ". " + hashMapEpics.get(userInput).toString() + "] удален.");
            for (Integer subtaskID : subtaskIDList) {
                hashMapSubtasks.remove(subtaskID); // удалили все подзадачи эпика
            }
            hashMapEpics.remove(userInput); //удалили эпик
        } else {
            System.out.println("Такой задачи не существует.");
        }
    }


    //7. Получение списка всех подзадач определённого эпика.
    public void printSubtasks() {
        System.out.println("Введите ID эпика:");
        int userInput = scanner.nextInt();
        for (Map.Entry<Integer, Subtask> entry : hashMapSubtasks.entrySet()) {
            Integer key = entry.getKey();
            Subtask value = entry.getValue();
            if (value.getEpicID() == userInput) {
                System.out.println(key + ". " + value);
            } else {
                System.out.println("У данного эпика нет подзадач либо эпик не существует");
            }
        }
    }

    //8. Автоматическое обновление статуса эпика
    public void setStatus(int epicID) {
        ArrayList<String> listOfStatus = new ArrayList<>();
        ArrayList<Integer> x = hashMapEpics.get(epicID).getSubtaskIDList();
        if (x.size() == 0) {
            hashMapEpics.get(epicID).setEpicStatus("NEW");
        } else {
            for (Integer y : x) {
                listOfStatus.add(hashMapSubtasks.get(y).getTaskStatus());
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
    public void statusUpdate(int userInput) {
        if (userInput == 1) {
            System.out.println("Введите ID задачи:");
            userInput = scanner.nextInt();
            if (hashMapTasks.containsKey(userInput)) {
                Task currentTask = hashMapTasks.get(userInput);
                printStatus();
                userInput = scanner.nextInt();
                if (userInput == 1) {
                    currentTask.setTaskStatus("NEW");
                } else if (userInput == 2) {
                    currentTask.setTaskStatus("IN_PROGRESS");
                } else if (userInput == 3) {
                    currentTask.setTaskStatus("DONE");
                }
                System.out.println("Статус задачи выставлен как: " + currentTask.getTaskStatus());
            } else {
                System.out.println("Задачи с таким ID не найдено.");
            }
        } else if (userInput == 2) {
            System.out.println("Введите ID подзадачи:");
            userInput = scanner.nextInt();
            if (hashMapSubtasks.containsKey(userInput)) {
                Subtask currentSubtask = hashMapSubtasks.get(userInput);
                printStatus();
                userInput = scanner.nextInt();
                if (userInput == 1) {
                    currentSubtask.setTaskStatus("NEW");
                    int epicID = currentSubtask.getEpicID();
                    setStatus(epicID);
                } else if (userInput == 2) {
                    currentSubtask.setTaskStatus("IN_PROGRESS");
                    int epicID = currentSubtask.getEpicID();
                    setStatus(epicID);
                } else if (userInput == 3) {
                    currentSubtask.setTaskStatus("DONE");
                    int epicID = currentSubtask.getEpicID();
                    setStatus(epicID);
                }
                System.out.println("Статус подзадачи выставлен как: " + currentSubtask.getTaskStatus());
            } else {
                System.out.println("Подзадачи с таким ID не найдено.");
            }

        } else if (userInput == 3) {
            System.out.println("Извините, статус эпика нельзя менять вручную.");
        }
    }

    private static void printStatus() {
        System.out.println("Выберите статус:");
        System.out.println("1 - NEW");
        System.out.println("2 - IN_PROGRESS");
        System.out.println("3 - DONE");
    }
}
