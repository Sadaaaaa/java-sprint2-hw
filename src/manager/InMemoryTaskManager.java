package manager;

import Data.Epic;
import Data.StatusList;
import Data.Subtask;
import Data.Task;
import history.HistoryManager;
import history.InMemoryHistoryManager;

import java.time.Duration;
import java.time.LocalDate;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    private int taskID = 1;

    private Map<Integer, Task> hashMapTasks = new HashMap<>();
    private Map<Integer, Subtask> hashMapSubtasks = new HashMap<>();
    private Map<Integer, Epic> hashMapEpics = new HashMap<>();
    private HistoryManager historyManager = new InMemoryHistoryManager();

    public void setTaskID(int taskID) {
        this.taskID = taskID;
    }
    @Override
    public Map<Integer, Task> getHashMapTasks() {
        return hashMapTasks;
    }
    @Override
    public Map<Integer, Subtask> getHashMapSubtasks() {
        return hashMapSubtasks;
    }
    @Override
    public Map<Integer, Epic> getHashMapEpics() {
        return hashMapEpics;
    }
    @Override
    public HistoryManager getHistoryManager() {
        return historyManager;
    }


    @Override
    public int generateNewId() {
        return taskID++;
    }

    @Override
    public LocalDate getEndTime(Task task) {
        LocalDate tmp = null;
        if (task.getStartTime() != null) {
            tmp = task.getStartTime().plusDays(task.getDuration().toDays());
        }
        return tmp;
    }

    //1. Получение списка всех задач.
    @Override
    public List<Task> allTasksList() {
        List<Task> allTasks = new ArrayList<>();
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
        boolean isCrossed = isCrossed(task);

        if (!isCrossed) {
            int newID = generateNewId(); // Узнаем ID нового таска
            System.out.println("Задаче присвоен ID: " + newID);
            task.setItemID(newID); // запись ID в поле объекта
            hashMapTasks.put(newID, task);
            System.out.println("Задача [" + newID + ". " + hashMapTasks.get(newID).toString() + "] создана.");
        } else {
            System.out.println("Задачи пересекаются. Выберите другой startTime");
        }

    }

    @Override
    public void createSubtask(Subtask subtask) {
        boolean isCrossed = isCrossed(subtask);

        if (!isCrossed) {
            int newID = generateNewId(); // Узнаем ID нового сабтаска
            System.out.println("Подзадаче присвоен ID: " + newID);
            subtask.setItemID(newID); // добавлено: запись ID в поле объекта
            int epicID = subtask.getEpicID();
            hashMapSubtasks.put(newID, subtask);
            System.out.println("Подзадача [" + newID + ". " + hashMapSubtasks.get(newID).toString() + "] создана.");
            hashMapEpics.get(epicID).setSubtaskIDList(newID);
            setStatus(epicID); // обновили статус эпика
            if (subtask.getStartTime() != null) {
                updateDuration(epicID);
            }
        } else {
            System.out.println("Задачи пересекаются. Выберите другой startTime");
        }
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
        boolean isCrossed = isCrossed(newTask);

        if (!isCrossed) {
            int ID = newTask.getItemID();
            hashMapTasks.put(ID, newTask);
            System.out.println("Задача [" + ID + ". " + hashMapTasks.get(ID).toString() + "] обновлена.");
        } else {
            System.out.println("Задачи пересекаются. Выберите другой startTime");
        }
    }

    @Override
    public void updateSubtask(Subtask newSubtask) {
        boolean isCrossed = isCrossed(newSubtask);

        if (!isCrossed) {
            int ID = newSubtask.getItemID(); // узнали ID подзадачи, которую необходимо обновить
            int epicID = hashMapSubtasks.get(ID).getEpicID(); // узнали ID эпика, которому принадлежит подзадача
            List<Integer> subtaskIDList = hashMapEpics.get(epicID).getSubtaskIDList();
            subtaskIDList.remove(Integer.valueOf(ID)); // метод удаляет подзадачу из списка, прикрепленного к эпику
            hashMapSubtasks.put(ID, newSubtask); // положили подзадачу в hashmap
            newSubtask.setEpicID(epicID); // установили соответствие подзадачи эпику
            hashMapEpics.get(epicID).setSubtaskIDList(ID); // добавили подзадачу в лист подзадач, принадлежащих эпику
            setStatus(epicID); // обновили статус эпика
            System.out.println("Подзадача [" + ID + ". " + hashMapSubtasks.get(ID).toString() + "] обновлена.");
        } else {
            System.out.println("Задачи пересекаются. Выберите другой startTime");
        }
    }

    @Override
    public void updateEpic(Epic newEpic) {
        int ID = newEpic.getItemID();
        List<Integer> subtaskIDList = hashMapEpics.get(ID).getSubtaskIDList(); // сохранили привязку подзадач и эпика
        newEpic.setSubtaskIDList(subtaskIDList); // положили в объект newEpic лист с подзадачами
        hashMapEpics.put(ID, newEpic); // положили эпик в hashmap
        setStatus(ID); // обновили статус эпика
        System.out.println("Эпик [" + ID + ". " + hashMapEpics.get(ID).toString() + "] обновлен.");
    }

    //6. Удаление по идентификатору.
    public void deleteTask(int ID) {
        System.out.println("Задача [" + ID + ". " + hashMapTasks.get(ID).toString() + "] удалена.");
        getHistoryManager().remove(ID); //удалили историю вызовов задачи
        hashMapTasks.remove(ID);
    }

    public void deleteEpic(int ID) {
        List<Integer> subtaskIDList = hashMapEpics.get(ID).getSubtaskIDList(); // получаем лист с подзадачами данного эпика
        System.out.println("Эпик [" + ID + ". " + hashMapEpics.get(ID).toString() + "] удален.");
        getHistoryManager().remove(ID); //удалили историю вызовов эпика
        for (Integer subtaskID : subtaskIDList) {
            getHistoryManager().remove(subtaskID); //удалили историю вызовов подзадач данного эпика
            hashMapSubtasks.remove(subtaskID); // удалили все подзадачи эпика
        }
        hashMapEpics.remove(ID); //удалили эпик
    }

    public void deleteSubtask(int ID) {
        System.out.println("Подзадача [" + ID + ". " + hashMapSubtasks.get(ID).toString() + "] удалена.");
        getHistoryManager().remove(ID); //удалили историю вызовов подзадачи
        int epicID = hashMapSubtasks.get(ID).getEpicID();
        List<Integer> subtaskIDList = hashMapEpics.get(epicID).getSubtaskIDList();
        subtaskIDList.remove(Integer.valueOf(ID)); // метод удаляет подзадачу из списка, прикрепленного к эпику
        hashMapSubtasks.remove(ID);
        setStatus(epicID); // добавлено: обновление статуса эпика
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
        List<StatusList> listOfStatus = new ArrayList<>();
        List<Integer> listSubtaskID = hashMapEpics.get(epicID).getSubtaskIDList();
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

    //10. Автообновление длительности эпика
    @Override
    public void updateDuration(int epicID) {
        TaskManager manager = new InMemoryTaskManager();
        LocalDate epicStartDate = null;
        LocalDate epicEndDate = null;
        List<Integer> listSubtaskID = hashMapEpics.get(epicID).getSubtaskIDList();

        if (listSubtaskID.size() != 0) {
            for (Integer ID : listSubtaskID) {
                LocalDate subtaskStart = hashMapSubtasks.get(ID).getStartTime();
                LocalDate subtaskEnd = manager.getEndTime(hashMapSubtasks.get(ID));

                if (epicStartDate == null) {
                    epicStartDate = subtaskStart;
                    epicEndDate = subtaskEnd;
                } else {
                    if (subtaskStart.isBefore(epicStartDate)) {
                        epicStartDate = subtaskStart;
                    }

                    if (subtaskEnd.isAfter(epicEndDate)) {
                        epicEndDate = subtaskEnd;
                    }
                }
            }
            hashMapEpics.get(epicID).setStartTime(epicStartDate);
            hashMapEpics.get(epicID).setEndTime(epicEndDate);
            hashMapEpics.get(epicID).setDuration(Duration.between(epicStartDate.atStartOfDay(), epicEndDate.atStartOfDay()));

        } else {
            hashMapEpics.get(epicID).setDuration(Duration.ZERO);
            hashMapEpics.get(epicID).setStartTime(null);
            hashMapEpics.get(epicID).setEndTime(null);
        }
    }

    //11. Сортировка задач
    @Override
    public List<Task> getPrioritizedTasks() {
        Comparator<Task> comparator = (o1, o2) -> {
            if (o1.getItemID() == o2.getItemID()) return 0;
            if (o1.getStartTime() == null) return 1;
            if (o2.getStartTime() == null) return -1;
            if (o1.getStartTime().compareTo(o2.getStartTime()) == 0) return 0;
            return o1.getStartTime().compareTo(o2.getStartTime());
        };

        Set<Task> taskSet = new TreeSet<>(comparator);
        taskSet.addAll(hashMapTasks.values());
        taskSet.addAll(hashMapSubtasks.values());


        //        for (Task x : sortedArr) {
//            System.out.println(x.getItemID() + " " + x.getStartTime());
//        }
        return new ArrayList<>(taskSet);


        //Сортировка пузырьком:
//        Task temp;
//        List<Task> sortedArr = allTasksList();
//        for (int i = 0; i < sortedArr.size(); i++) {
//            if (sortedArr.get(i).getStartTime() != null) {
//                for (int j = i + 1; j < sortedArr.size(); j++) {
//                    if (sortedArr.get(j).getStartTime() != null) {
//                        if (sortedArr.get(i).getStartTime().isAfter(sortedArr.get(j).getStartTime())) {
//                            temp = sortedArr.get(i);
//                            sortedArr.set(i, sortedArr.get(j));
//                            sortedArr.set(j, temp);
//                        } else if (sortedArr.get(i).getStartTime().isEqual(sortedArr.get(j).getStartTime())) {
//                            temp = sortedArr.get(i+1);
//                            sortedArr.set(i+1, sortedArr.get(j));
//                            sortedArr.set(j, temp);
//                        }
//                    }
//                }
//            }
//        }
//
//        for(Task x:sortedArr){
//            System.out.println(x.getItemID()+" "+x.getStartTime());
//        }
//
//        return sortedArr;
    }

    //12. Поиск пересечений
    public boolean isCrossed(Task task) {
        boolean isCrossed = false;
        List<Task> tempArr = getPrioritizedTasks();
        if (task.getStartTime() != null) {
            for (Task x : tempArr) {
                if (getEndTime(x).isAfter(task.getStartTime()) || getEndTime(x).isEqual(task.getStartTime())) {
                    isCrossed = true;
                } else {
                    isCrossed = false;
                }
            }
        }

        return isCrossed;
    }
}



