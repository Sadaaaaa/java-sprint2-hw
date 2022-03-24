import Data.Epic;
import Data.StatusList;
import Data.Subtask;
import Data.Task;

import java.io.*;
import java.util.*;

public class FileBackedTasksManager extends InMemoryTaskManager {
    private String fileName;

    // Новый менеджер получает файл для автосохранения в своём конструкторе и сохраняет его в поле
    public FileBackedTasksManager(String fileName) {
        this.fileName = fileName;
    }

    public static void main(String[] args) throws IOException {
        // Выгружаем задачи и историю из файла
        FileBackedTasksManager manager = loadFromFile(new File("C:\\Users\\User\\Desktop\\backup", "backup.csv"));

        // Создаем задачи
        Task firstTask = new Task("Задача-5", "Описание задачи-5");
        manager.createTask(firstTask);
        Task secondTask = new Task("Задача-6", "Описание задачи-6");
        manager.createTask(secondTask);

        // Создаем эпик с сабтасками
        Epic firstEpic = new Epic("Эпик-1", "Описание эпик-1");
        manager.createEpic(firstEpic);
        Subtask firstSubtask = new Subtask("Подзадача-1", "Подзадача-1 относится к эпику-1");
        firstSubtask.setEpicID(firstEpic.getItemID());
        manager.createSubtask(firstSubtask);
        Subtask secondSubtask = new Subtask("Подзадача-2", "Подзадача-2 относится к эпику-1");
        secondSubtask.setEpicID(firstEpic.getItemID());
        manager.createSubtask(secondSubtask);
        Subtask thirdSubtask = new Subtask("Подзадача-3", "Подзадача-3 относится к эпику-1");
        thirdSubtask.setEpicID(firstEpic.getItemID());
        manager.createSubtask(thirdSubtask);

        // Создаем пустой эпик
        Epic secondEpic = new Epic("Эпик-2", "Описание эпик-2");
        manager.createEpic(secondEpic);

        // Запрашиваем созданные задачи несколько раз в разном порядке
        manager.getTaskById(firstTask.getItemID());
        manager.getEpicById(secondEpic.getItemID());
        manager.getSubtaskById(firstSubtask.getItemID());
        manager.getEpicById(firstEpic.getItemID());
    }

    File file = new File("C:\\Users\\User\\Desktop\\backup", "backup.csv");

    // Метод сохранения задач в файл
    static FileBackedTasksManager loadFromFile(File file) throws IOException {
        FileBackedTasksManager fileBackedTasksManager = new FileBackedTasksManager(file.getName());

        ArrayList<String> lineArray = new ArrayList<>();
        FileReader fileReader = new FileReader(file);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        String line = bufferedReader.readLine();
        while (line != null) {
            lineArray.add(line);
            line = bufferedReader.readLine();
        }

        // восстановили из истории значение поля taskID
        for (int i = 0; i < lineArray.size(); i++) {
            if (lineArray.get(i).isBlank()) {
                String[] s = lineArray.get(i - 1).split(",");
                fileBackedTasksManager.setTaskID(Integer.parseInt(s[0].replaceAll("[^0-9]", "")) + 1);
            }
        }
        // восстанавливаем задачи в хешмапы
        for (int i = 0; i < lineArray.size(); i++) {
            if (!lineArray.get(i).isBlank()) {
                Task t = fileBackedTasksManager.fromString(lineArray.get(i));
                if (t.getClass() == Task.class) {
                    fileBackedTasksManager.getHashMapTasks().put(t.getItemID(), t);
                } else if (t.getClass() == Subtask.class) {
                    fileBackedTasksManager.getHashMapSubtasks().put(t.getItemID(), (Subtask) t);
                } else {
                    fileBackedTasksManager.getHashMapEpics().put(t.getItemID(), (Epic) t);
                }
            } else {
                // сливаем три хешмапы (task, subtask, epic) в одну
                HashMap<Integer, Task> tempHashmap = new HashMap<>();
                tempHashmap.putAll(fileBackedTasksManager.getHashMapTasks());
                tempHashmap.putAll(fileBackedTasksManager.getHashMapSubtasks());
                tempHashmap.putAll(fileBackedTasksManager.getHashMapEpics());

                // возвращаем историю просмотров из файла в historyManager
                for (Integer x : fromStringHistory(lineArray.get(i + 1))) {
                    if (tempHashmap.get(x).getClass() == Task.class) {
                        fileBackedTasksManager.getTaskById(x);
                    } else if (tempHashmap.get(x).getClass() == Subtask.class) {
                        fileBackedTasksManager.getSubtaskById(x);
                    } else {
                        fileBackedTasksManager.getEpicById(x);
                    }
                }
                break;
            }
        }
        return fileBackedTasksManager;
    }

    // Метод сохранения задач в файл
    void save() {
        // сортировка задач по itemID
        ArrayList<Task> allTasks = allTasksList();
        ArrayList<Task> allTasksSorted = allTasksList();
        for (Task x : allTasks) {
            allTasksSorted.set(x.getItemID() - 1, x);
        }

        // Запись задач в файл
        try (FileWriter fileWriter = new FileWriter(file)) {
            for (Task x : allTasksSorted) {
                fileWriter.write(toString(x));
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка сохранения в файл");
        }

        // Запись истории в файл
        try (FileWriter fileWriter = new FileWriter(file, true)) {
            fileWriter.write(toStringHistory(getHistoryManager()));
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка сохранения в файл");
        }
    }

    // преобразование задачи в строку для записи в файл
    public String toString(Task task) {
        if (task.getClass() == Task.class) {
            return task.getItemID() + "," +
                    TaskType.TASK + "," +
                    task.getTaskName() + "," +
                    task.getTaskStatus() + "," +
                    task.getTaskDetails() + "\n";
        } else if (task.getClass() == Subtask.class) {
            return task.getItemID() + "," +
                    TaskType.SUBTASK + "," +
                    task.getTaskName() + "," +
                    task.getTaskStatus() + "," +
                    task.getTaskDetails() + "," +
                    ((Subtask) task).getEpicID() + "\n";
        } else {
            return task.getItemID() + "," +
                    TaskType.EPIC + "," +
                    task.getTaskName() + "," +
                    task.getTaskStatus() + "," +
                    task.getTaskDetails() + "," +
                    ((Epic) task).getSubtaskIDList() + "\n";
        }
    }

    // преобразование строки в задачу
    public Task fromString(String value) {
        // делим строку с разделителем ","
        String[] split = value.split(",");

        if (split[1].equals("TASK")) {

            // создаем Task с названием и описанием
            Task task = new Task(split[2], split[4]);

            // добавляем статус к Task
            if (split[3].equals("NEW")) {
                task.setTaskStatus(StatusList.NEW);
            } else if (split[3].equals("IN_PROGRESS")) {
                task.setTaskStatus(StatusList.IN_PROGRESS);
            } else {
                task.setTaskStatus(StatusList.DONE);
            }

            // добавляем itemID к Task
            int itemID = Integer.parseInt(split[0].replaceAll("[^0-9]", ""));
            task.setItemID(itemID);

            return task;

        } else if (split[1].equals("SUBTASK")) {

            // создаем Subtask с названием и описанием
            Subtask subtask = new Subtask(split[2], split[4]);

            // добавляем статус к Subtask
            if (split[3].equals("NEW")) {
                subtask.setTaskStatus(StatusList.NEW);
            } else if (split[3].equals("IN_PROGRESS")) {
                subtask.setTaskStatus(StatusList.IN_PROGRESS);
            } else {
                subtask.setTaskStatus(StatusList.DONE);
            }

            // добавляем itemID к Subtask
            subtask.setItemID(Integer.parseInt(split[0].replaceAll("[^0-9]", "")));

            // добавляем Epic к Subtask
            subtask.setEpicID(Integer.parseInt(split[5].replaceAll("[^0-9]", "")));

            return subtask;
        } else {

            // создаем Epic с названием и описанием
            Epic epic = new Epic(split[2], split[4]);

            // добавляем статус к Epic
            if (split[3].equals("NEW")) {
                epic.setTaskStatus(StatusList.NEW);
            } else if (split[3].equals("IN_PROGRESS")) {
                epic.setTaskStatus(StatusList.IN_PROGRESS);
            } else {
                epic.setTaskStatus(StatusList.DONE);
            }

            // добавляем itemID к Epic
            epic.setItemID(Integer.parseInt(split[0].replaceAll("[^0-9]", "")));
            return epic;
        }
    }

    // преобразование истории в строку
    public static String toStringHistory(HistoryManager manager) {
        StringBuilder s = new StringBuilder();
        for (Task x : manager.getHistory()) {
            s.append(x.getItemID()).append(",");
        }
        return "\n" + ((s == null || s.length() == 0) ? null : (s.substring(0, s.length() - 1)));
    }

    // преобразование строки с историей в список ID задач, которые были просмотрены
    static List<Integer> fromStringHistory(String value) {
        List<Integer> historyList = new ArrayList<>();
        String[] split = value.split(",");
        for (String x : split) {
            historyList.add(Integer.parseInt(x));
        }
        return historyList;
    }

    @Override
    public HashMap<Integer, Task> getHashMapTasks() {
        return super.getHashMapTasks();
    }

    @Override
    public HashMap<Integer, Subtask> getHashMapSubtasks() {
        return super.getHashMapSubtasks();
    }

    @Override
    public HashMap<Integer, Epic> getHashMapEpics() {
        return super.getHashMapEpics();
    }

    @Override
    public HistoryManager getHistoryManager() {
        return super.getHistoryManager();
    }

    @Override
    public int generateNewId() {
        return super.generateNewId();
    }

    //1. Получение списка всех задач.
    @Override
    public ArrayList<Task> allTasksList() {
        return super.allTasksList();
    }

    //2. Удаление всех задач.
    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
    }

    //3. Получение задач по идентификатору.
    @Override
    public Task getTaskById(int ID) {
        save();
        return super.getTaskById(ID);
    }

    @Override
    public Subtask getSubtaskById(int ID) {
        save();
        return super.getSubtaskById(ID);
    }

    @Override
    public Epic getEpicById(int ID) {
        save();
        return super.getEpicById(ID);
    }

    //4. Создание задач.
    @Override
    public void createTask(Task task) {
        super.createTask(task);
        save();
    }

    @Override
    public void createSubtask(Subtask subtask) {
        super.createSubtask(subtask);
        save();
    }

    @Override
    public void createEpic(Epic epic) {
        super.createEpic(epic);
        save();
    }

    //5. Обновление задач.
    @Override
    public void updateTask(Task newTask) {
        super.updateTask(newTask);
    }

    @Override
    public void updateSubtask(Subtask newSubtask) {
        super.updateSubtask(newSubtask);
    }

    @Override
    public void updateEpic(Epic newEpic) {
        super.updateEpic(newEpic);
    }

    //6. Удаление задач по идентификатору.
    @Override
    public void deleteTaskByID(int ID) {
        super.deleteTaskByID(ID);
    }

    //7. Получение списка всех подзадач определённого эпика.
    @Override
    public void printSubtasks(int ID) {
        super.printSubtasks(ID);
    }

    //8. Автоматическое обновление статуса эпика
    @Override
    public void setStatus(int epicID) {
        super.setStatus(epicID);
    }

    //9. История просмотра задач
    @Override
    public List<Task> history() {
        return super.history();
    }
}