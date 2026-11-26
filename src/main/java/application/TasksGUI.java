package application;

import model.*;
import service.TasksManagement;
import util.Utility;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class TasksGUI extends JFrame {

    private TasksManagement manager;

    private JTextField empIdField;
    private JTextField empNameField;

    private JTable employeeTable;
    private JTable taskTable;

    private DefaultTableModel employeeModel;
    private DefaultTableModel taskModel;

    private JTextArea statsArea;

    public TasksGUI() {

        manager = new TasksManagement();
        manager.loadData("tasks.dat");
        setTitle("Tasks Management System");
        setSize(900,600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {

                manager.saveData("tasks.dat");

                System.exit(0);
            }
        });

        JTabbedPane tabs = new JTabbedPane();

        tabs.add("Employees", createEmployeeTab());
        tabs.add("Tasks", createTaskTab());
        tabs.add("Statistics", createStatisticsTab());

        add(tabs);
        showTasks();
        //refreshEmployees();
    }

    // EMPLOYEE TAB

    private JPanel createEmployeeTab() {

        JPanel panel = new JPanel(new BorderLayout());

        JPanel topPanel = new JPanel();

        empIdField = new JTextField(5);
        empNameField = new JTextField(10);

        JButton addButton = new JButton("Add Employee");
        JButton refreshButton = new JButton("Refresh");

        topPanel.add(new JLabel("ID"));
        topPanel.add(empIdField);
        topPanel.add(new JLabel("Name"));
        topPanel.add(empNameField);
        topPanel.add(addButton);
        topPanel.add(refreshButton);

        panel.add(topPanel, BorderLayout.NORTH);

        employeeModel = new DefaultTableModel(new String[]{"ID","Name"},0);
        employeeTable = new JTable(employeeModel);

        panel.add(new JScrollPane(employeeTable), BorderLayout.CENTER);

        addButton.addActionListener(e -> addEmployee());
        refreshButton.addActionListener(e -> refreshEmployees());

        return panel;
    }

    // Task TAB

    private JPanel createTaskTab() {

        JPanel panel = new JPanel(new BorderLayout());

        JPanel topPanel = new JPanel();

        JButton addButton = new JButton("Add simple Task");
        JButton complexTaskButton = new JButton("Create Complex Task");
        JButton assignButton = new JButton("Assign Task");
        JButton modifyStatusButton = new JButton("Modify Status");
        JButton nestButton = new JButton("Nest Task");

        topPanel.add(addButton);
        topPanel.add(complexTaskButton);
        topPanel.add(assignButton);
        topPanel.add(modifyStatusButton);
        topPanel.add(nestButton);

        panel.add(topPanel, BorderLayout.NORTH);

        taskModel = new DefaultTableModel(new String[]{"Employee","Task ID","Duration","Status","Sub Tasks"},0);

        taskTable = new JTable(taskModel);

        panel.add(new JScrollPane(taskTable), BorderLayout.CENTER);

        addButton.addActionListener(e -> addTask());
        assignButton.addActionListener(e -> assignTask());
        complexTaskButton.addActionListener(e -> createComplexTask());
        modifyStatusButton.addActionListener(e -> modifyStatus());
        nestButton.addActionListener(e -> nestTaskAction());

        return panel;
    }

    private void nestTaskAction() {
        try {
            String pIdStr = JOptionPane.showInputDialog(this, "Parent Complex Task ID:");
            String cIdStr = JOptionPane.showInputDialog(this, "Task ID to move inside it:");

            if (pIdStr != null && cIdStr != null) {
                int pId = Integer.parseInt(pIdStr);
                int cId = Integer.parseInt(cIdStr);

                manager.nestTask(pId, cId);
                showTasks(); // Refresh the table
                JOptionPane.showMessageDialog(this, "Task nested successfully!");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter valid numeric IDs.");
        }
    }

    // STATISTICS TAB

    private JPanel createStatisticsTab() {
        JPanel panel = new JPanel(new BorderLayout());

        JPanel topPanel = new JPanel();

        JButton over40Button = new JButton("Employees > 40h");
        JButton statsButton = new JButton("Task Status Stats");

        topPanel.add(over40Button);
        topPanel.add(statsButton);

        panel.add(topPanel, BorderLayout.NORTH);

        statsArea = new JTextArea();
        panel.add(new JScrollPane(statsArea), BorderLayout.CENTER);

        over40Button.addActionListener(e -> showEmployeesOver40());
        statsButton.addActionListener(e -> showTaskStatistics());

        return panel;
    }


    private void addEmployee() {

        int id = Integer.parseInt(empIdField.getText());
        String name = empNameField.getText();

        if(manager.employeeIdExists(id)){
            JOptionPane.showMessageDialog(this,
                    "Employee ID already exists!");
            return;
        }

        Employee employee = new Employee(id,name);

        manager.addEmployee(employee);

        refreshEmployees();
    }

    private void refreshEmployees() {

        employeeModel.setRowCount(0);

        Map<Employee, List<Task>> map = manager.getEmployeeTasks();

        for(Employee e : map.keySet()) {

            if(e.getIdEmployee()==0)
                continue;
            employeeModel.addRow(new Object[]{
                    e.getIdEmployee(),
                    e.getName()
            });
        }
    }

    private void createComplexTask() {

        // Input field for ID
        JTextField idField = new JTextField();

        // Employee dropdown
        JComboBox<Employee> employeeCombo = new JComboBox<>();

        for (Employee e : manager.getEmployeeTasks().keySet()) {
            employeeCombo.addItem(e);
        }

        // Multi-select task list (from unassigned tasks)
        DefaultListModel<Task> listModel = new DefaultListModel<>();

        for (Task t : manager.getAllTasks()) {
            listModel.addElement(t);
        }

        JList<Task> taskList = new JList<>(listModel);
        taskList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        JScrollPane scrollPane = new JScrollPane(taskList);
        scrollPane.setPreferredSize(new Dimension(200,120));

        // Panel layout (better than GridLayout stretching)
        JPanel panel = new JPanel(new BorderLayout(10,10));

        JPanel topPanel = new JPanel(new GridLayout(2,2,5,5));

        topPanel.add(new JLabel("Complex Task ID:"));
        topPanel.add(idField);

        topPanel.add(new JLabel("Assign to:"));
        topPanel.add(employeeCombo);

        panel.add(topPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(new JLabel("Select Subtasks:"), BorderLayout.NORTH);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        panel.add(centerPanel, BorderLayout.CENTER);

        int option = JOptionPane.showConfirmDialog(
                this,
                panel,
                "Create Complex Task",
                JOptionPane.OK_CANCEL_OPTION
        );

        if (option == JOptionPane.OK_OPTION) {

            try {

                int id = Integer.parseInt(idField.getText());

                // ✅ Check duplicate ID
                if (manager.taskIdExists(id)) {
                    JOptionPane.showMessageDialog(this,
                            "Task ID already exists!");
                    return;
                }

                List<Task> selectedTasks = taskList.getSelectedValuesList();

                if (selectedTasks.isEmpty()) {
                    JOptionPane.showMessageDialog(this,
                            "Select at least one subtask!");
                    return;
                }


                Employee selectedEmployee =
                        (Employee) employeeCombo.getSelectedItem();
                ComplexTask complexTask = new ComplexTask(id);

                for (Task t : selectedTasks) {
                    complexTask.addTask(t);
                    manager.removeTask(t);
                }

                manager.assignTaskToEmployee(selectedEmployee, complexTask);

                JOptionPane.showMessageDialog(this,
                        "Complex Task created successfully");

                showTasks(); // refresh table

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                        "Invalid ID!");
            }
        }
    }

    private void modifyStatus(){
        int selectedRow = taskTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Select a task from the table first");
            return;
        }

        int taskId = (int) taskModel.getValueAt(selectedRow, 1);

        Task selectedTask = null;

        for (Task t : manager.getAllTasks()) {
            if (t.getIdTask() == taskId) {
                selectedTask = t;
                break;
            }
        }

        if (selectedTask == null) {
            JOptionPane.showMessageDialog(this,
                    "Task not found");
            return;
        }

        JComboBox<String> statusCombo = new JComboBox<>();

        statusCombo.addItem("Completed");
        statusCombo.addItem("Uncompleted");

        int option = JOptionPane.showConfirmDialog(
                this,
                statusCombo,
                "Select status",
                JOptionPane.OK_CANCEL_OPTION
        );

        if (option == JOptionPane.OK_OPTION) {

            String selectedStatus = statusCombo.getSelectedItem().toString();

            manager.modifyTaskStatus(selectedTask, selectedStatus);

            JOptionPane.showMessageDialog(this,
                    "Status modified successfully");

            showTasks();
        }
    }

    private void addTask() {

        // Input fields
        JTextField idField = new JTextField();
        JTextField startField = new JTextField();
        JTextField endField = new JTextField();

        // Employee dropdown
        JComboBox<Employee> employeeCombo = new JComboBox<>();

        for (Employee e : manager.getEmployeeTasks().keySet()) {
            employeeCombo.addItem(e);
        }

        // Panel layout
        JPanel panel = new JPanel(new GridLayout(4,2,5,5));

        panel.add(new JLabel("Task ID:"));
        panel.add(idField);

        panel.add(new JLabel("Start Time:"));
        panel.add(startField);

        panel.add(new JLabel("End Time:"));
        panel.add(endField);

        panel.add(new JLabel("Assign to:"));
        panel.add(employeeCombo);

        int option = JOptionPane.showConfirmDialog(
                this,
                panel,
                "Add Task",
                JOptionPane.OK_CANCEL_OPTION
        );

        if (option == JOptionPane.OK_OPTION) {

            try {

                int id = Integer.parseInt(idField.getText());
                int start = Integer.parseInt(startField.getText());
                int end = Integer.parseInt(endField.getText());

                // ✅ Check duplicate ID
                if (manager.taskIdExists(id)) {
                    JOptionPane.showMessageDialog(this,
                            "Task ID already exists!");
                    return;
                }

                Task task = new SimpleTask(id, start, end);

                Employee selectedEmployee =
                        (Employee) employeeCombo.getSelectedItem();

                // Assign immediately
                manager.assignTaskToEmployee(selectedEmployee, task);

                JOptionPane.showMessageDialog(this,
                        "Task added successfully");

                showTasks(); // refresh table

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                        "Invalid input! Please enter numbers.");
            }
        }
    }

    private void assignTask() {

        int selectedRow = taskTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Select a task from the table first");
            return;
        }

        int taskId = (int) taskModel.getValueAt(selectedRow, 1);

        Task selectedTask = null;

        for (Task t : manager.getAllTasks()) {
            if (t.getIdTask() == taskId) {
                selectedTask = t;
                break;
            }
        }

        if (selectedTask == null) {
            JOptionPane.showMessageDialog(this,
                    "Task not found");
            return;
        }

        JComboBox<Employee> employeeCombo = new JComboBox<>();

        for (Employee e : manager.getEmployeeTasks().keySet()) {
            employeeCombo.addItem(e);
        }

        int option = JOptionPane.showConfirmDialog(
                this,
                employeeCombo,
                "Select Employee",
                JOptionPane.OK_CANCEL_OPTION
        );

        if (option == JOptionPane.OK_OPTION) {

            Employee selectedEmployee =
                    (Employee) employeeCombo.getSelectedItem();

            manager.assignTaskToEmployee(selectedEmployee, selectedTask);

            JOptionPane.showMessageDialog(this,
                    "Task assigned successfully");

            showTasks();
        }
    }

    private void showTasks() {

        taskModel.setRowCount(0);

        Map<Employee, List<Task>> data = manager.getEmployeeTasks();

        for (Employee employee : data.keySet()) {

            List<Task> tasks = data.get(employee);

            if (tasks != null) {

                for (Task task : tasks) {

                    var subTaskList = "";
                    if (task instanceof ComplexTask) {
                        for (Task t : ((ComplexTask) task).getTaskList()) {
                            subTaskList += t.getIdTask();

                            // Check if the inner task is ALSO complex
                            if (t instanceof ComplexTask) {
                                subTaskList += "[";
                                for (Task subT : ((ComplexTask) t).getTaskList()) {
                                    subTaskList += subT.getIdTask() + ",";
                                }
                                subTaskList += "]";
                            }
                            subTaskList += " / ";
                        }
                    }

                    taskModel.addRow(new Object[]{
                            employee.getName(),
                            task.getIdTask(),
                            task.estimateDuration(),
                            task.getStatusTask(),
                            subTaskList
                    });
                }
            }
        }
    }

    private void showEmployeesOver40(){

        statsArea.setText("");

        Map<Employee, List<Task>> data = manager.getEmployeeTasks();

        var result = Utility.getEmployeesOver40(data);
        for (Map.Entry<Employee, Integer> entry : result) {
            statsArea.setText(entry.getKey().getName() +
                    " - " + entry.getValue() + " hours");
        }
    }

    private void showTaskStatistics(){

        statsArea.setText("");

        Map<String, Map<String, Integer>> stats =
                Utility.computeTaskStatusStatistics(manager.getEmployeeTasks());

        for(String name : stats.keySet()){

            Map<String,Integer> values = stats.get(name);

            statsArea.append(name + " -> ");

            statsArea.append("Completed: "
                    + values.get("Completed") + ", ");

            statsArea.append("Uncompleted: "
                    + values.get("Uncompleted") + "\n");
        }
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            new TasksGUI().setVisible(true);
        });
    }

    private String getSubtaskString(Task task) {
        if (task instanceof SimpleTask) {
            return "";
        }

        StringBuilder result = new StringBuilder();
        for (Task t : ((ComplexTask) task).getTaskList()) {
            result.append(t.getIdTask());
            if (t instanceof ComplexTask) {
                result.append("[").append(getSubtaskString(t)).append("]");
            }
            result.append(", ");
        }

        if (result.length() > 0) {
            result.setLength(result.length() - 2);
        }
        return result.toString();
    }



}
