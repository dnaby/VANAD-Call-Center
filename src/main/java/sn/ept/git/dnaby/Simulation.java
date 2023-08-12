package sn.ept.git.dnaby;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Simulation {
    private List<String> fileList = new ArrayList<>();
    private String datasetCSVFile;

    public Simulation(String folderPath, int month_number) {
        File folder = new File(folderPath);
        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    fileList.add(folderPath + "/" + file.getName());
                }
            }
        }
        datasetCSVFile = "./dataset/" + month_number + ".csv";
        createDatasetCSVFile();
    }

    public void createDatasetCSVFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(datasetCSVFile))) {
            // Set column names
            String columnNames = "Type,Is_Served,Arrival_Time,Service_Time,Number_Of_Server";
            for (int i = 0; i <= 26; i++) {
                columnNames += ",Wait_List_Length__" + i;
            }
            columnNames += ",LES,Avg_LES,AvgC_LES,weighted_AvgC_LES,Waiting_Time";
            writer.write(columnNames);
            writer.newLine();
        } catch (IOException e) {
            System.out.println("An error occurred while creating the CSV file: " + e.getMessage());
        }
    }

    public void createDataset() throws IOException {
        for (String day:fileList) {
            OneDaySimulation oneDaySimulation = new OneDaySimulation(datasetCSVFile);
            oneDaySimulation.createCustomers(day);
        }
    }

    public static void main(String[] args) throws IOException {
        for (int i = 1; i <= 12; i++) {
            String daysPath = "/Users/mac/Downloads/Modélisation Stochastique/year/" + i;
            Simulation sim = new Simulation(daysPath, i);
            sim.createDataset();
        }
    }
}
