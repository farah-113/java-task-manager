package util;

import model.Employee;
import model.Task;

import java.util.*;

public class Utility {

    public static List<Map.Entry<Employee, Integer>> getEmployeesOver40(Map<Employee, List<Task>> employeeTasks) {

        List<Map.Entry<Employee, Integer>> result = new ArrayList<>();

        for (Map.Entry<Employee, List<Task>> entry : employeeTasks.entrySet()) {

            Employee employee = entry.getKey();
            List<Task> tasks = entry.getValue();
            int totalDuration = 0;

            for (Task t : tasks) {
                if (t.getStatusTask().equals("Completed")) {
                    totalDuration += t.estimateDuration();
                }
            }

            if (totalDuration > 40) {
                result.add(new AbstractMap.SimpleEntry<>(employee, totalDuration));
            }
        }

        // Sorting
        result.sort(Comparator.comparingInt(Map.Entry::getValue));

        return result;
    }


    public static Map<String, Map<String, Integer>> computeTaskStatusStatistics(
            Map<Employee, List<Task>> employeeTasks) {

        Map<String, Map<String, Integer>> result = new HashMap<>();

        for (Map.Entry<Employee, List<Task>> entry : employeeTasks.entrySet()) {

            Employee employee = entry.getKey();
            List<Task> tasks = entry.getValue();

            int completed = 0;
            int uncompleted = 0;

            for (Task t : tasks) {

                if (t.getStatusTask().equals("Completed")) {
                    completed++;
                } else {
                    uncompleted++;
                }
            }

            Map<String, Integer> statusMap = new HashMap<>();

            statusMap.put("Completed", completed);
            statusMap.put("Uncompleted", uncompleted);

            result.put(employee.getName(), statusMap);
        }

        return result;
    }
}
