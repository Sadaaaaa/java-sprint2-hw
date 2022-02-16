import Data.Epic;
import Data.StatusList;
import Data.Subtask;
import Data.Task;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        System.out.println("Пришло время практики!");
        Scanner scanner = new Scanner(System.in);
        InMemoryTaskManager manager = new InMemoryTaskManager();

        printMainMenu();
        try {
            int userInput = scanner.nextInt();
            while (userInput != 0) {
                if (userInput == 1) {
                    printTaskType();
                    userInput = scanner.nextInt();
                    if (userInput == 1) {
                        System.out.println("Введите наименование задачи:");
                        String taskName = scanner.next();
                        System.out.println("Введите описание задачи:");
                        String taskDescription = scanner.next();
                        Task task = new Task(taskName, taskDescription);
                        manager.createTask(task);
                    } else if (userInput == 2) {
                        System.out.println("Введите наименование подзадачи:");
                        String subTaskName = scanner.next();
                        System.out.println("Введите описание подзадачи:");
                        String subTaskDescription = scanner.next();
                        System.out.println("Введите ID эпика, к которому относится подзадача:");
                        int epicID = scanner.nextInt();
                        if (manager.getHashMapEpics().containsKey(epicID)) {
                            Subtask subtask = new Subtask(subTaskName, subTaskDescription);
                            subtask.setEpicID(epicID);
                            manager.createSubtask(subtask);
                        } else {
                            System.out.println("Такого эпика не существует. Сперва создайте подходящий эпик.");
                        }
                    } else if (userInput == 3) {
                        System.out.println("Введите наименование эпика:");
                        String epicName = scanner.next();
                        System.out.println("Введите описание эпика:");
                        String epicDescription = scanner.next();
                        Epic epic = new Epic(epicName, epicDescription);
                        manager.createEpic(epic);
                    }
                } else if (userInput == 2) {
                    for (Object list : manager.allTasksList()) {
                        System.out.println(list);
                    }
                } else if (userInput == 3) {
                    System.out.println("Введите ID задачи:");
                    userInput = scanner.nextInt();
                    manager.deleteTaskByID(userInput);
                } else if (userInput == 4) {
                    printTaskType();
                    userInput = scanner.nextInt();
                    if (userInput == 1) {
                        System.out.println("Введите ID задачи, которую хотите обновить:");
                        userInput = scanner.nextInt();
                        if (manager.getHashMapTasks().containsKey(userInput)) {
                            System.out.println("1 - обновить задачу");
                            System.out.println("2 - обновить статус задачи");
                            int input = scanner.nextInt();
                            if (input == 1) {
                                System.out.println("Введите новое наименование задачи:");
                                String taskName = scanner.next();
                                System.out.println("Введите новое описание задачи:");
                                String taskDescription = scanner.next();
                                Task newTask = new Task(taskName, taskDescription);
                                newTask.setItemID(userInput);
                                manager.updateTask(newTask);
                            } else if (input == 2) {
                                Task updTask = manager.getHashMapTasks().get(userInput);
                                printStatus();
                                int status = scanner.nextInt();
                                if (status == 1) {
                                    updTask.setTaskStatus(StatusList.NEW);
                                } else if (status == 2) {
                                    updTask.setTaskStatus(StatusList.IN_PROGRESS);
                                } else if (status == 3) {
                                    updTask.setTaskStatus(StatusList.DONE);
                                }
                                manager.updateTask(updTask);
                            }
                        } else {
                            System.out.println("Задачи с таким ID не найдено.");
                        }
                    } else if (userInput == 2) {
                        System.out.println("Введите ID подзадачи, которую хотите обновить:");
                        userInput = scanner.nextInt();
                        if (manager.getHashMapSubtasks().containsKey(userInput)) {
                            System.out.println("1 - обновить задачу");
                            System.out.println("2 - обновить статус подзадачи");
                            int input = scanner.nextInt();
                            if (input == 1) {
                                System.out.println("Введите новое наименование подзадачи:");
                                String taskName = scanner.next();
                                System.out.println("Введите новое описание подзадачи:");
                                String taskDescription = scanner.next();
                                Subtask newSubtask = new Subtask(taskName, taskDescription);
                                newSubtask.setItemID(userInput);
                                manager.updateSubtask(newSubtask);
                            } else if (input == 2) {
                                Subtask updSubtask = manager.getHashMapSubtasks().get(userInput);
                                printStatus();
                                int status = scanner.nextInt();
                                if (status == 1) {
                                    updSubtask.setTaskStatus(StatusList.NEW);
                                } else if (status == 2) {
                                    updSubtask.setTaskStatus(StatusList.IN_PROGRESS);
                                } else if (status == 3) {
                                    updSubtask.setTaskStatus(StatusList.DONE);
                                }
                                manager.updateSubtask(updSubtask);
                            }
                        } else {
                            System.out.println("Подзадачи с таким ID не найдено.");
                        }
                    } else if (userInput == 3) {
                        System.out.println("Введите ID эпика, который хотите обновить:");
                        userInput = scanner.nextInt();
                        if (manager.getHashMapEpics().containsKey(userInput)) {
                            System.out.println("Введите новое наименование эпика:");
                            String taskName = scanner.next();
                            System.out.println("Введите новое описание эпика:");
                            String taskDescription = scanner.next();
                            Epic newEpic = new Epic(taskName, taskDescription);
                            newEpic.setItemID(userInput);
                            manager.updateEpic(newEpic);
                        } else {
                            System.out.println("Эпика с таким ID не найдено.");
                        }
                    } else {
                        System.out.println("Неверный ввод.");
                    }
                } else if (userInput == 5) {
                    printTaskType();
                    userInput = scanner.nextInt();
                    if (userInput == 1) {
                        System.out.println("Введите ID задачи:");
                        userInput = scanner.nextInt();
                        System.out.println(userInput + ". " + manager.getTaskById(userInput).toString());
                    } else if (userInput == 2) {
                        System.out.println("Введите ID подзадачи:");
                        userInput = scanner.nextInt();
                        System.out.println(userInput + ". " + manager.getSubtaskById(userInput).toString());
                    } else if (userInput == 3) {
                        System.out.println("Введите ID эпика:");
                        userInput = scanner.nextInt();
                        System.out.println(userInput + ". " + manager.getEpicById(userInput).toString());
                    }
                } else if (userInput == 6) {
                    System.out.println("Введите ID эпика:");
                    userInput = scanner.nextInt();
                    manager.printSubtasks(userInput);
                } else if (userInput == 7) {
                    manager.deleteAllTasks();
                } else if (userInput == 8) {
                    for (Task history : manager.history()) {
                        System.out.println(history);
                    }
                }
                printMainMenu(); // печатаем меню ещё раз перед завершением предыдущего действия
                userInput = scanner.nextInt(); // повторное считывание данных от пользователя
            }
        } catch (Exception e) {
            System.out.println("Неверный ввод.");
        }
        System.out.println("Программа завершена");
    }

    private static void printMainMenu() {
        System.out.println("Выберите один из пунктов меню:");
        System.out.println("1 - Создать задачу");
        System.out.println("2 - Напечатать список задач");
        System.out.println("3 - Удалить задачу");
        System.out.println("4 - Обновить задачу");
        System.out.println("5 - Искать задачу по ID");
        System.out.println("6 - Искать подзадачи определенного эпика");
        System.out.println("7 - Удалить все задачи");
        System.out.println("8 - Показать историю просмотров");
        System.out.println("0 - Выйти из приложения");
    }

    private static void printTaskType() {
        System.out.println("Выберите тип:");
        System.out.println("1 - Задача");
        System.out.println("2 - Подзадача");
        System.out.println("3 - Эпик");
    }

    private static void printStatus() {
        System.out.println("Выберите статус:");
        System.out.println("1 - NEW");
        System.out.println("2 - IN_PROGRESS");
        System.out.println("3 - DONE");
    }
}