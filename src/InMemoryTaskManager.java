import Data.Epic;
import Data.StatusList;
import Data.Subtask;
import Data.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryTaskManager implements TaskManager {
    private int taskID = 1;

    private HashMap<Integer, Task> hashMapTasks = new HashMap<>();
    private HashMap<Integer, Subtask> hashMapSubtasks = new HashMap<>();
    private HashMap<Integer, Epic> hashMapEpics = new HashMap<>();
    private HistoryManager historyManager = new InMemoryHistoryManager();

    public HistoryManager getHistoryManager() {
        return historyManager;
    }

    @Override
    public int generateNewId() {
        return taskID++;
    }

    //1. Получение списка всех задач.
    @Override
    public ArrayList<Task> allTasksList() {
        ArrayList<Task> allTasks = new ArrayList<>();
        allTasks.addAll(hashMapTasks.values());
        allTasks.addAll(hashMapSubtasks.values());
        allTasks.addAll(hashMapEpics.values());
        return allTasks;
    }

    //2. Удаление всех задач.
    @Override
    public void deleteAllTasks() {
        hashMapTasks.clear();
        hashMapSubtasks.clear();
        hashMapEpics.clear();
        System.out.println("Все задачи удалены.");
    }

    //3. Получение по идентификатору.
    @Override
    public Task getTaskById(int ID) {
        historyManager.add(hashMapTasks.get(ID)); // new feature: добавили вызов задачи в историю
        return hashMapTasks.get(ID);

    }

    @Override
    public Subtask getSubtaskById(int ID) {
        historyManager.add(hashMapSubtasks.get(ID)); // new feature: добавили вызов подзадачи в историю
        return hashMapSubtasks.get(ID);
    }

    @Override
    public Epic getEpicById(int ID) {
        historyManager.add(hashMapEpics.get(ID)); // new feature: добавили вызов эпика в историю
        return hashMapEpics.get(ID);
    }


    //4. Создание задач.
    @Override
    public void createTask(Task task) {
        int newID = generateNewId(); // Узнаем ID нового таска
        System.out.println("Задаче присвоен ID: " + newID);
        task.setItemID(newID); // запись ID в поле объекта
        hashMapTasks.put(newID, task);
        System.out.println("Задача [" + newID + ". " + hashMapTasks.get(newID).toString() + "] создана.");
    }

    @Override
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

    @Override
    public void createEpic(Epic epic) {
        int newID = generateNewId();           // Узнаем ID нового таска
        System.out.println("Эпику присвоен ID: " + newID);
        epic.setItemID(newID); // запись ID в поле объекта
        hashMapEpics.put(newID, epic);
        System.out.println("Эпик [" + newID + ". " + hashMapEpics.get(newID).toString() + "] создан.");
    }


    //5. Обновление.
    @Override
    public void updateTask(Task newTask) {
        int ID = newTask.getItemID();
        hashMapTasks.put(ID, newTask);
        System.out.println("Задача [" + ID + ". " + hashMapTasks.get(ID).toString() + "] обновлена.");
    }

    @Override
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

    @Override
    public void updateEpic(Epic newEpic) {
        int ID = newEpic.getItemID();
        ArrayList<Integer> subtaskIDList = hashMapEpics.get(ID).getSubtaskIDList(); // сохранили привязку подзадач и эпика
        newEpic.setSubtaskIDList(subtaskIDList); // положили в объект newEpic лист с подзадачами
        hashMapEpics.put(ID, newEpic); // положили эпик в hashmap
        setStatus(ID); // обновили статус эпика
        System.out.println("Эпик [" + ID + ". " + hashMapEpics.get(ID).toString() + "] обновлен.");
    }

    //6. Удаление по идентификатору.
    @Override
    public void deleteTaskByID(int ID) {
        if (hashMapTasks.containsKey(ID)) {
            System.out.println("Задача [" + ID + ". " + hashMapTasks.get(ID).toString() + "] удалена.");
            getHistoryManager().remove(ID); //удалили историю вызовов задачи
            hashMapTasks.remove(ID);
        } else if (hashMapSubtasks.containsKey(ID)) {
            System.out.println("Подзадача [" + ID + ". " + hashMapSubtasks.get(ID).toString() + "] удалена.");
            getHistoryManager().remove(ID); //удалили историю вызовов подзадачи
            int epicID = hashMapSubtasks.get(ID).getEpicID();
            ArrayList<Integer> subtaskIDList = hashMapEpics.get(epicID).getSubtaskIDList();
            subtaskIDList.remove(Integer.valueOf(ID)); // метод удаляет подзадачу из списка, прикрепленного к эпику
            hashMapSubtasks.remove(ID);
            setStatus(epicID); // добавлено: обновление статуса эпика
        } else if (hashMapEpics.containsKey(ID)) {
            ArrayList<Integer> subtaskIDList = hashMapEpics.get(ID).getSubtaskIDList(); // получаем лист с подзадачами данного эпика
            System.out.println("Эпик [" + ID + ". " + hashMapEpics.get(ID).toString() + "] удален.");
            getHistoryManager().remove(ID); //удалили историю вызовов эпика
            for (Integer subtaskID : subtaskIDList) {
                getHistoryManager().remove(subtaskID); //удалили историю вызовов подзадач данного эпика
                hashMapSubtasks.remove(subtaskID); // удалили все подзадачи эпика
            }
            hashMapEpics.remove(ID); //удалили эпик
        } else {
            System.out.println("Такой задачи не существует.");
        }
    }

    //7. Получение списка всех подзадач определённого эпика.
    @Override
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
    @Override
    public void setStatus(int epicID) {
        ArrayList<StatusList> listOfStatus = new ArrayList<>();
        ArrayList<Integer> listSubtaskID = hashMapEpics.get(epicID).getSubtaskIDList();
        if (listSubtaskID.size() == 0) {
            hashMapEpics.get(epicID).setEpicStatus(StatusList.NEW);
        } else {
            for (Integer ID : listSubtaskID) {
                listOfStatus.add(hashMapSubtasks.get(ID).getTaskStatus());
                if (listOfStatus.contains(StatusList.NEW) && !listOfStatus.contains(StatusList.IN_PROGRESS) && !listOfStatus.contains(StatusList.DONE)) {
                    hashMapEpics.get(epicID).setEpicStatus(StatusList.NEW);
                } else if (listOfStatus.contains(StatusList.DONE) && !listOfStatus.contains(StatusList.IN_PROGRESS) && !listOfStatus.contains(StatusList.NEW)) {
                    hashMapEpics.get(epicID).setEpicStatus(StatusList.DONE);
                } else {
                    hashMapEpics.get(epicID).setEpicStatus(StatusList.IN_PROGRESS);
                }
            }
        }
    }

    //9. История просмотра задач
    @Override
    public List<Task> history() {
        return historyManager.getHistory();
    }

}
