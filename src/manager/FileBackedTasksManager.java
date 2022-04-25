package manager;

import Data.*;
import history.HistoryManager;

import java.io.*;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class FileBackedTasksManager extends InMemoryTaskManager {
    private String fileName;

    // Новый менеджер получает файл для автосохранения в своём конструкторе и сохраняет его в поле
    public FileBackedTasksManager(String fileName) {
        this.fileName = fileName;
    }

    public static void main(String[] args) {
        // Выгружаем задачи и историю из файла
        FileBackedTasksManager manager = new FileBackedTasksManager("backup.csv");
        try {
            manager = loadFromFile(new File("./src", "backup.csv"));
        } catch (Exception e) {
            System.out.println("Нет данных для загрузки.");
        }

        // Создаем задачи
        Task firstTask = new Task("Задача-5", "Описание задачи-5");
        manager.createTask(firstTask);
        firstTask.setStartTime(LocalDate.of(2022,5,1));
        firstTask.setDuration(Duration.ofDays(5));
        Task secondTask = new Task("Задача-6", "Описание задачи-6");
        secondTask.setStartTime(LocalDate.of(2023,5,2));
        secondTask.setDuration(Duration.ofDays(10));
        manager.createTask(secondTask);

        // Создаем эпик с сабтасками
        Epic firstEpic = new Epic("Эпик-1", "Описание эпик-1");
        manager.createEpic(firstEpic);
        Subtask firstSubtask = new Subtask("Подзадача-1", "Подзадача-1 относится к эпику-1");
        firstSubtask.setStartTime(LocalDate.of(2024,5,3));
        firstSubtask.setDuration(Duration.ofDays(10));
        firstSubtask.setEpicID(firstEpic.getItemID());
        manager.createSubtask(firstSubtask);
        Subtask secondSubtask = new Subtask("Подзадача-2", "Подзадача-2 относится к эпику-1");
        secondSubtask.setStartTime(LocalDate.of(2025,5,4));
        secondSubtask.setDuration(Duration.ofDays(10));
        secondSubtask.setEpicID(firstEpic.getItemID());
        manager.createSubtask(secondSubtask);
        Subtask thirdSubtask = new Subtask("Подзадача-3", "Подзадача-3 относится к эпику-1");
        thirdSubtask.setStartTime(LocalDate.of(2026,5,5));
        thirdSubtask.setDuration(Duration.ofDays(10));
        thirdSubtask.setEpicID(firstEpic.getItemID());
        manager.createSubtask(thirdSubtask);

        // Создаем пустой эпик
        Epic secondEpic = new Epic("Эпик-2", "Описание эпик-2");
        manager.createEpic(secondEpic);

        // Запрашиваем созданные задачи несколько раз в разном порядке
        try {
            manager.getTaskById(firstTask.getItemID());
            manager.getEpicById(secondEpic.getItemID());
            manager.getSubtaskById(firstSubtask.getItemID());
            manager.getEpicById(firstEpic.getItemID());
        } catch (NullPointerException e) {
            System.out.println("Задачи не были добавлены. Измените startTime.");
        }

        manager.getPrioritizedTasks();
    }

    File file = new File("./src", "backup.csv");

    // Метод восстановления задач из файла
    static FileBackedTasksManager loadFromFile(File file) throws IOException {
        FileBackedTasksManager fileBackedTasksManager = new FileBackedTasksManager(file.getName());

        List<String> lineArray = new ArrayList<>();
        FileReader fileReader = new FileReader(file);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        String line = bufferedReader.readLine();
        while (line != null) {
            lineArray.add(line);
            line = bufferedReader.readLine();
        }

        // восстановили из истории значение поля taskID
        for (int i = 0; i < lineArray.size(); i++) {
            if (lineArray.get(i).isEmpty()) {
                String[] s = lineArray.get(i - 1).split(",");
                fileBackedTasksManager.setTaskID(Integer.parseInt(s[0].replaceAll("[^0-9]", "")) + 1);
            }
        }
        // восстанавливаем задачи в хешмапы
        for (int i = 0; i < lineArray.size(); i++) {
            if (!lineArray.get(i).isBlank()) {
                Task t = fileBackedTasksManager.fromString(lineArray.get(i));
                if (t.getType().equals(TaskType.TASK.toString())) {
                    fileBackedTasksManager.getHashMapTasks().put(t.getItemID(), t);
                } else if (t.getType().equals(TaskType.SUBTASK.toString())) {
                    fileBackedTasksManager.getHashMapSubtasks().put(t.getItemID(), (Subtask) t);
                } else {
                    fileBackedTasksManager.getHashMapEpics().put(t.getItemID(), (Epic) t);
                }



            } else {
                // блок кода, обновляющий продолжительность эпика на основании загруженных сабтасков
                //------------
                if (!fileBackedTasksManager.getHashMapEpics().isEmpty()) {
                    for (Integer x : fileBackedTasksManager.getHashMapEpics().keySet()) {
                        fileBackedTasksManager.updateDuration(x);
                    }
                }
                //------------

                // сливаем три хешмапы (task, subtask, epic) в одну
                HashMap<Integer, Task> tempHashmap = new HashMap<>();
                tempHashmap.putAll(fileBackedTasksManager.getHashMapTasks());
                tempHashmap.putAll(fileBackedTasksManager.getHashMapSubtasks());
                tempHashmap.putAll(fileBackedTasksManager.getHashMapEpics());

                // возвращаем историю просмотров из файла в historyManager
                for (Integer x : fromStringHistory(lineArray.get(i + 1))) {
                    if (tempHashmap.get(x).getType().equals(TaskType.TASK.toString())) {
                        fileBackedTasksManager.getTaskById(x);
                    } else if (tempHashmap.get(x).getType().equals(TaskType.SUBTASK.toString())) {
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
    protected void save() {
        // сортировка задач по itemID
        List<Task> allTasks = allTasksList();
        List<Task> allTasksSorted = allTasksList();
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
        if (getHistoryManager().getHistory().size() != 0) {
            try (FileWriter fileWriter = new FileWriter(file, true)) {
                fileWriter.write(toStringHistory(getHistoryManager()));
            } catch (IOException e) {
                throw new ManagerSaveException("Ошибка сохранения в файл");
            }
        }
    }

    // преобразование задачи в строку для записи в файл
    public String toString(Task task) {
        if (task.getClass() == Task.class) {
            return task.getItemID() + "," +
                    TaskType.TASK + "," +
                    task.getTaskName() + "," +
                    task.getTaskStatus() + "," +
                    task.getTaskDetails() + "," +
                    task.getStartTime() + "," +
                    task.getDuration() + "\n";
        } else if (task.getClass() == Subtask.class) {
            return task.getItemID() + "," +
                    TaskType.SUBTASK + "," +
                    task.getTaskName() + "," +
                    task.getTaskStatus() + "," +
                    task.getTaskDetails() + "," +
                    task.getStartTime() + "," +
                    task.getDuration() + "," +
                    ((Subtask) task).getEpicID() + "\n";
        } else {
            return task.getItemID() + "," +
                    TaskType.EPIC + "," +
                    task.getTaskName() + "," +
                    task.getTaskStatus() + "," +
                    task.getTaskDetails() + "," +
                    ((Epic) task).getSubtaskIDList() + ",/старт:" +
                    (task.getStartTime() == null ? "-" : task.getStartTime()) + ", завершение:" +
                    (((Epic) task).getEndTime() == null ? "-" : ((Epic) task).getEndTime()) + ", продолжительность:" +
                    (task.getDuration() == Duration.ZERO ? "0 сек" : task.getDuration()) + "/" + "\n";

        }
    }

    // преобразование строки в задачу
    public Task fromString(String value) {
        // делим строку с разделителем ","
        String[] split = value.split(",");

        if (split[1].equals(TaskType.TASK.toString())) {

            // создаем Task с названием и описанием
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            Task task = new Task(split[2], split[4]);
            if (!split[5].equals("null")) {
                LocalDate date = LocalDate.parse(split[5], formatter);
                task.setStartTime(date);
                task.setDuration(Duration.parse(split[6]));
            }

            // добавляем статус к Task
            if (split[3].equals(StatusList.NEW.toString())) {
                task.setTaskStatus(StatusList.NEW);
            } else if (split[3].equals(StatusList.IN_PROGRESS.toString())) {
                task.setTaskStatus(StatusList.IN_PROGRESS);
            } else {
                task.setTaskStatus(StatusList.DONE);
            }

            // добавляем itemID к Task
            int itemID = Integer.parseInt(split[0].replaceAll("[^0-9]", ""));
            task.setItemID(itemID);

            return task;

        } else if (split[1].equals(TaskType.SUBTASK.toString())) {

            // создаем Subtask с названием и описанием
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate date = LocalDate.parse(split[5], formatter);

            Subtask subtask = new Subtask(split[2], split[4]);
            subtask.setStartTime(date);
            subtask.setDuration(Duration.parse(split[6]));

            // добавляем статус к Subtask
            if (split[3].equals(StatusList.NEW.toString())) {
                subtask.setTaskStatus(StatusList.NEW);
            } else if (split[3].equals(StatusList.IN_PROGRESS.toString())) {
                subtask.setTaskStatus(StatusList.IN_PROGRESS);
            } else {
                subtask.setTaskStatus(StatusList.DONE);
            }

            // добавляем itemID к Subtask
            subtask.setItemID(Integer.parseInt(split[0].replaceAll("[^0-9]", "")));

            // добавляем Epic к Subtask
            subtask.setEpicID(Integer.parseInt(split[7].replaceAll("[^0-9]", "")));

            return subtask;
        } else {

            // создаем Epic с названием и описанием
            Epic epic = new Epic(split[2], split[4]);

            // добавляем статус к Epic
            if (split[3].equals(StatusList.NEW.toString())) {
                epic.setTaskStatus(StatusList.NEW);
            } else if (split[3].equals(StatusList.IN_PROGRESS.toString())) {
                epic.setTaskStatus(StatusList.IN_PROGRESS);
            } else {
                epic.setTaskStatus(StatusList.DONE);
            }

            // добавляем itemID к Epic
            epic.setItemID(Integer.parseInt(split[0].replaceAll("[^0-9]", "")));

            //добавляем все сабтаски, принадлежащие этому эпику
            String resultStr = value.substring(value.indexOf('[') + 1, value.indexOf(']'));
            String[] strArr = resultStr.split(",");
            if (!Objects.equals(strArr[0], "")) {
                for (String x : strArr) {
                    epic.setSubtaskIDList(Integer.parseInt(x.trim()));
                }
            }

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
        if (!(value == null)) {
            String[] split = value.split(",");
            for (String x : split) {
                historyList.add(Integer.parseInt(x));
            }
        }
        return historyList;

    }

    @Override
    public Map<Integer, Task> getHashMapTasks() {
        return super.getHashMapTasks();
    }

    @Override
    public Map<Integer, Subtask> getHashMapSubtasks() {
        return super.getHashMapSubtasks();
    }

    @Override
    public Map<Integer, Epic> getHashMapEpics() {
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
    public List<Task> allTasksList() {
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
        Task taskCall = super.getTaskById(ID);
        save();
        //return super.getTaskById(ID);
        return taskCall;
    }

    @Override
    public Subtask getSubtaskById(int ID) {
        Subtask subtaskCall = super.getSubtaskById(ID);
        save();
        return subtaskCall;
    }

    @Override
    public Epic getEpicById(int ID) {
        Epic epicCall = super.getEpicById(ID);
        save();
        //return super.getEpicById(ID);
        return epicCall;
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
    public void deleteTask(int ID) {
        super.deleteTask(ID);
    }

    @Override
    public void deleteEpic(int ID) {
        super.deleteEpic(ID);
    }

    @Override
    public void deleteSubtask(int ID) {
        super.deleteSubtask(ID);
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

    @Override
    public void updateDuration(int epicID) {
        super.updateDuration(epicID);
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return super.getPrioritizedTasks();
    }
}
