package model;

import java.util.ArrayList;
import java.util.List;

public final class ComplexTask extends Task {

    private List<Task> taskList;

    public ComplexTask(int idTask) {
        super(idTask);
        taskList = new ArrayList<>();
    }

    public void addTask(Task task) {
        taskList.add(task);
    }

    public void deleteTask(Task task) {
        taskList.remove(task);
    }

    public List<Task> getTaskList() {
        return taskList;
    }

    @Override
    public int estimateDuration() {
        int total = 0;

        for (Task task : taskList) {
            total =total + task.estimateDuration();
        }

        return total;
    }
}
