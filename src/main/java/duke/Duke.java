package duke;

import duke.exception.IllegalDeadlineException;
import duke.exception.IllegalEventException;
import duke.exception.IllegalTaskException;
import duke.exception.IllegalTodoException;
import duke.task.Deadline;
import duke.task.Event;
import duke.task.Task;
import duke.task.Todo;

import java.util.Scanner;
import java.util.ArrayList;

public class Duke {

    /**
     * Returns boolean value of true if input String is an integer,
     * else returns boolean value of false
     *
     * @param word String input to check if it is an integer
     * @return true if input String is an integer, otherwise false
     */
    public static boolean isNumeric(String word) {
        int valueToConvert;
        try {
            valueToConvert = Integer.parseInt(word);
            return true;
        } catch (NumberFormatException e) {
            System.out.println("Incorrect input, please enter an integer.");
        }
        return false;
    }

    public static void main(String[] args) {
        greetingMessage();

        String line;
        Scanner in = new Scanner(System.in);
        line = in.nextLine();
        ArrayList<Task> tasks = new ArrayList<>();

        while (!line.equals("bye")) { // Exits the program if input is "bye"
            String[] words = line.split(" ");
            if (line.equals("list")) {
                // List out all the tasks added
                list(tasks);
            } else if (words[0].equals("unmark") && (words.length == 2) && (isNumeric(words[1]))) {
                // Mark a task as not done
                unmarkTask(tasks, words);
            } else if (words[0].equals("mark") && (words.length == 2) && (isNumeric(words[1]))) {
                // Mark a task as done
                markTask(tasks, words);
            } else if (words[0].equals("delete") && (words.length == 2) && (isNumeric(words[1]))) {
                // Delete a task
                deleteTask(tasks, words);
            } else {
                // Adding a task to the list
                try {
                    addTask(line, tasks);
                    Task.incrementCount();
                } catch (IllegalTaskException e) {
                    // Ignoring empty command inputs
                }
            }
            line = in.nextLine();
        }

        // Exiting the program
        exitMessage();
    }

    private static void addTask(String line, ArrayList<Task> tasks) throws IllegalTaskException {
        if (line.contains("/by")) {
            // Adding a Deadline
            try {
                addDeadline(line, tasks);
            } catch (IllegalDeadlineException e) {
                deadlineErrorMessage();
                throw new IllegalTaskException();
            }
        } else if (line.contains("/from") || line.contains("/to")) {
            // Adding an Event
            try {
                addEvent(line, tasks);
            } catch (IllegalEventException e) {
                eventErrorMessage();
                throw new IllegalTaskException();
            } catch (IndexOutOfBoundsException e) {
                eventErrorMessage();
                throw new IllegalTaskException();
            }
        } else if (!line.isBlank()){
            // Adding a _Todo_
            try {
                addTodo(line, tasks);
            } catch (IllegalTodoException e) {
                todoErrorMessage();
                throw new IllegalTaskException();
            }
        } else {
            throw new IllegalTaskException();
        }
    }

    private static void addTodo(String line, ArrayList<Task> tasks) throws IllegalTodoException {
        if (line.isBlank()) {
            throw new IllegalTodoException();
        } else {
            Todo currTodo = new Todo(line);
            tasks.add(currTodo);
            addedTaskMessage(currTodo);
        }
    }

    private static void addEvent(String line, ArrayList<Task> tasks) throws IllegalEventException {
        String description = line.substring(0, line.indexOf("/from")).trim();
        String start = line.substring(line.indexOf("/from") + 5, line.indexOf("/to")).trim();
        String end = line.substring(line.indexOf("/to") + 3).trim();
        if (description.isBlank() || start.isBlank() || end.isBlank()) {
            throw new IllegalEventException();
        } else {
            Event currEvent = new Event(description, start, end);
            tasks.add(currEvent);
            addedTaskMessage(currEvent);
        }
    }

    private static void addDeadline(String line, ArrayList<Task> tasks) throws IllegalDeadlineException {
        String description = line.substring(0, line.indexOf("/by")).trim();
        String deadline = line.substring(line.indexOf("/by") + 3).trim();
        //System.out.println(description.isBlank());
        if (description.isBlank() || deadline.isBlank()) {
            throw new IllegalDeadlineException();
        } else {
            Deadline currDeadline = new Deadline(description, deadline);
            tasks.add(currDeadline);
            addedTaskMessage(currDeadline);
        }
    }

    private static void list(ArrayList<Task> tasks) {
        int taskCount = Task.getTaskCount();
        borderLine();
        System.out.println("\t Here are the tasks in your list:");
        for (int i = 0; i < taskCount; i++) {
            System.out.println("\t " + (i + 1) + "." + tasks.get(i));
        }
        borderLine();
    }

    private static void markTask(ArrayList<Task> tasks, String[] words) {
        int taskNumber = Integer.parseInt(words[1]);
        int taskCount = Task.getTaskCount();
        if (taskNumber > taskCount) {
            // Input task number exceeds the number of tasks in the list
            exceedTaskNumberMessage(taskNumber);
        } else {
            tasks.get(taskNumber - 1).markAsDone();
            // Printing out marked as done message
            borderLine();
            System.out.println("\t Understood. I've marked this task as done:");
            System.out.println("\t " + tasks.get(taskNumber - 1));
            borderLine();
        }
    }

    private static void unmarkTask(ArrayList<Task> tasks, String[] words) {
        int taskNumber = Integer.parseInt(words[1]);
        int taskCount = Task.getTaskCount();
        if (taskNumber > taskCount) {
            // Input task number exceeds the number of tasks in the list
            exceedTaskNumberMessage(taskNumber);
        } else {
            tasks.get(taskNumber - 1).markAsNotDone();
            // Printing out marked as not done message
            borderLine();
            System.out.println("\t Understood. I've marked this task as not done yet:");
            System.out.println("\t " + tasks.get(taskNumber - 1));
            borderLine();
        }
    }

    private static void deleteTask(ArrayList<Task> tasks, String[] words) {
        int taskNumber = Integer.parseInt(words[1]);
        int taskCount = Task.getTaskCount();
        if (taskNumber > taskCount) {
            // Input task number exceeds the number of tasks in the list
            exceedTaskNumberMessage(taskNumber);
        } else {
            Task taskToDelete = tasks.get(taskNumber - 1);
            tasks.remove(taskNumber - 1);
            Task.decrementCount();
            deleteTaskMessage(taskToDelete);
        }
    }

    private static void borderLine() {
        System.out.println("\t____________________________________________________________");
    }

    private static void exitMessage() {
        borderLine();
        System.out.println("\t Bye. Hope to see you again soon!");
        borderLine();
    }

    private static void addedTaskMessage(Task currentTask) {
        borderLine();
        System.out.println("\t Alright, I have added this task: \n\t\t" + currentTask);
        System.out.println("\t You now have " + (Task.getTaskCount() + 1) + " tasks in your list.");
        borderLine();
    }

    private static void deleteTaskMessage(Task taskToDelete) {
        borderLine();
        System.out.println("\t Understood. I have removed this task:");
        System.out.println("\t\t" + taskToDelete);
        System.out.println("\t You now have " + Task.getTaskCount() + " tasks in your list.");
        borderLine();
    }

    private static void exceedTaskNumberMessage(int taskNumber) {
        borderLine();
        System.out.println("\t Task " + taskNumber + " does not exist.");
        borderLine();
    }

    private static void todoErrorMessage() {
        borderLine();
        System.out.println("\t Error. Please enter a valid description.");
        borderLine();
    }

    private static void eventErrorMessage() {
        borderLine();
        System.out.println("\t Error. Please enter a valid description, start time and end time");
        borderLine();
    }

    private static void deadlineErrorMessage() {
        borderLine();
        System.out.println("\t Error. Please enter a valid description and deadline.");
        borderLine();
    }

    private static void greetingMessage() {
        borderLine();
        System.out.println("\t Hello! I'm Vivy.");
        System.out.println("\t Here are some commands you can give me:");
        System.out.println("\t - list: I'll list out all the tasks you have recorded.");
        System.out.println("\t - mark <task_number>: I'll mark that task as done.");
        System.out.println("\t - unmark <task_number>: I'll mark that task as undone.");
        System.out.println("\t - delete <task_number>: I'll delete that task from your list.");
        System.out.println("\t - bye: I will shut down my program.");
        System.out.println("\t - Anything else will be recorded as a task. \n");
        System.out.println("\t Format for tasks:");
        System.out.println("\t   Deadlines: <description> /by <deadline>");
        System.out.println("\t              (eg. Eat bread /by Thursday)");
        System.out.println("\t      Events: <description> /from <start date/time> /to <end date/time>");
        System.out.println("\t              (eg. Meeting /from March 3 8pm /to 9pm)");
        System.out.println("\t        Todo: <description>");
        System.out.println("\t              (eg. Water the plants)");
        System.out.println("\t What can I do for you?");
        borderLine();
    }
}