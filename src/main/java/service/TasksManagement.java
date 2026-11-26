package service;

import model.*;

import java.io.*;
import java.util.*;

public class TasksManagement {

    private Map<Employee, List<Task>> map;

    public TasksManagement() {
        map = new HashMap<>();
    }

    public void addEmployee(Employee employee) {
        map.putIfAbsent(employee, new ArrayList<>());
    }

    public List<Task> getAllTasks(){
        List<Task> result = new ArrayList<>();
        for (List<Task> list : map.values()) {
            result.addAll(list);
        }
        return result;
    }

    public void removeTask(Task task) {
        for (Employee emp : map.keySet()) {

            map.get(emp).removeIf(t -> t.getIdTask() == task.getIdTask());
        }
    }


    public boolean employeeIdExists(int id){

        for(Employee e : map.keySet()){
            if(e.getIdEmployee() == id){
                return true;
            }
        }

        return false;
    }

    public boolean taskIdExists(int id) {
        for (List<Task> tasks : map.values()) {
            for (Task t : tasks) {
                if (isIdInTask(t, id)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isIdInTask(Task task, int targetId) {
        if (task.getIdTask() == targetId) {
            return true;
        }

        if (task instanceof ComplexTask) {
            for (Task subTask : ((ComplexTask) task).getTaskList()) {
                if (isIdInTask(subTask, targetId)) {
                    return true;
                }
            }
        }
        return false;
    }
    public void nestTask(int parentId, int childId) {
        Task parent = findTaskById(parentId);
        Task child = findTaskById(childId);
        if (parent instanceof ComplexTask cp && child != null && parent != child) {
            removeTask(child);
            cp.addTask(child);
        }
    }

    public Task findTaskById(int id) {
        for (List<Task> tasks : map.values()) {
            for (Task t : tasks) {
                Task found = searchTaskRecursive(t, id);
                if (found != null) return found;
            }
        }
        return null;
    }

    private Task searchTaskRecursive(Task t, int id) {
        if (t.getIdTask() == id) return t;
        if (t instanceof ComplexTask cp) {
            for (Task sub : cp.getTaskList()) {
                Task found = searchTaskRecursive(sub, id);
                if (found != null) return found;
            }
        }
        return null;
    }

    public Map<Employee, List<Task>> getEmployeeTasks() {
        return map;
    }

    public void assignTaskToEmployee(Employee employee, Task task) {
        removeTask(task);
        map.get(employee).add(task);
    }

    public int calculateEmployeeWorkDuration(Employee employee) {

        int totalDuration = 0;

        List<Task> tasks = map.get(employee);

        if (tasks != null) {

            for (Task task : tasks) {

                if (task.getStatusTask().equals("Completed")) {
                    totalDuration += task.estimateDuration();
                }
            }
        }

        return totalDuration;
    }

    public void modifyTaskStatus(Task task, String status) {
        if (status.equals("Completed") || status.equals("Uncompleted")) {
            task.setStatusTask(status);
        }
    }


    public void saveData(String fileName) {

        try (ObjectOutputStream out =
                     new ObjectOutputStream(new FileOutputStream(fileName))) {

            out.writeObject(map);

            System.out.println("Data saved successfully.");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @SuppressWarnings("unchecked")
    public void loadData(String fileName) {

        try (ObjectInputStream in =
                     new ObjectInputStream(new FileInputStream(fileName))) {

            map = (Map<Employee, List<Task>>) in.readObject();
            for (Employee e : map.keySet()) {
                if (e.getIdEmployee() == 0) {
                    break;
                }
            }
            System.out.println("Data loaded successfully.");

        } catch (IOException | ClassNotFoundException e) {
            System.out.println("No previous data found.");
        }
    }
}
