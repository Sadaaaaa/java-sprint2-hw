import Data.Epic;
import Data.Subtask;
import Data.Task;

public class Main {
    public static void main(String[] args) {
        InMemoryTaskManager manager = new InMemoryTaskManager();

        // Сценарий тестирования
        // создайте две задачи, эпик с тремя подзадачами и эпик без подзадач
        System.out.println("Шаг 1 Создаем задачи");
        Task firstTask = new Task("Купить хлеба", "Сходить в магазин около дома и купить хлеба");
        manager.createTask(firstTask);
        Task secondTask = new Task("Убраться дома", "Сделать сухую и влажную уборку во всех комнатах");
        manager.createTask(secondTask);

        Epic firstEpic = new Epic("Выполнить задание", "Выполнить ДЗ по программированию");
        manager.createEpic(firstEpic);
        Subtask firstSubtask = new Subtask("Прочитать ТЗ", "Внимательно прочитать и разобрать ТЗ к заданию");
        firstSubtask.setEpicID(firstEpic.getItemID());
        manager.createSubtask(firstSubtask);
        Subtask secondSubtask = new Subtask("Разработать архитектуру", "Придумать архитектуру будущего приложения");
        secondSubtask.setEpicID(firstEpic.getItemID());
        manager.createSubtask(secondSubtask);
        Subtask thirdSubtask = new Subtask("Реализовать ТЗ", "Написать приложение согласно ТЗ");
        thirdSubtask.setEpicID(firstEpic.getItemID());
        manager.createSubtask(thirdSubtask);

        Epic secondEpic = new Epic("Приготовить ужин", "Взять все ингредиенты и приготовить пищу");
        manager.createEpic(secondEpic);

        System.out.println(System.lineSeparator());

        //запросите созданные задачи несколько раз в разном порядке
        //после каждого запроса выведите историю и убедитесь, что в ней нет повторов;
        manager.getTaskById(firstTask.getItemID());
        manager.getEpicById(secondEpic.getItemID());
        manager.getSubtaskById(firstSubtask.getItemID());
        manager.getEpicById(firstEpic.getItemID());

        System.out.println("Шаг 2.1 Первый вызов истории:");
        for (Task history : manager.history()) {
            System.out.println(history);
        }

        manager.getSubtaskById(thirdSubtask.getItemID());
        manager.getTaskById(secondTask.getItemID());
        manager.getSubtaskById(secondSubtask.getItemID());
        manager.getTaskById(firstTask.getItemID());

        System.out.println(System.lineSeparator());
        System.out.println("Шаг 2.2 Второй вызов истории:");
        for (Task history : manager.history()) {
            System.out.println(history);
        }

        manager.getEpicById(firstEpic.getItemID());
        manager.getSubtaskById(thirdSubtask.getItemID());
        manager.getEpicById(secondEpic.getItemID());

        System.out.println(System.lineSeparator());
        System.out.println("Шаг 2.3 Третий вызов истории:");
        for (Task history : manager.history()) {
            System.out.println(history);
        }

        System.out.println(System.lineSeparator());
        //удалите задачу, которая есть в истории, и проверьте, что при печати она не будет выводиться
        System.out.println("Шаг 3 Удаление задачи:");
        int ID = firstTask.getItemID();
        manager.deleteTaskByID(ID);

        for (Task history : manager.history()) {
            System.out.println(history);
        }

        System.out.println(System.lineSeparator());
        //удалите эпик с тремя подзадачами и убедитесь, что из истории удалился как сам эпик, так и все его подзадачи.
        System.out.println("Шаг 4 Удаление эпика:");
        manager.deleteTaskByID(firstEpic.getItemID());
        for (Task history : manager.history()) {
            System.out.println(history);
        }
    }
}
