package sn.ept.git.dnaby;

import umontreal.ssj.simevents.Event;
import umontreal.ssj.simevents.Sim;

import java.io.*;
import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

public class OneDaySimulation {
    private int[] waiting_list_length = new int[27];
    private double[] LES = new double[27];
    private LinkedList<Double>[] avg_LES = new LinkedList[27];
    private LinkedList<Double>[][] avgC_LES = new LinkedList[27][100];
    private double[][] weighted_AvgC_LES = new double[27][100];
    private int number_of_server;
    private double smoothing_factor;
    private ArrayList<Customer> served_customer = new ArrayList<>();
    private ArrayList<Customer> abandoned_customer = new ArrayList<>();
    private String datasetCSVFilePath;

    public OneDaySimulation(String filepath) {
        datasetCSVFilePath = filepath;
        number_of_server = 0;
        smoothing_factor = 0.2;
        for (int i = 0; i < 27; i++) {
            waiting_list_length[i] = 0;
            LES[i] = -1.0;
            avg_LES[i] = new LinkedList<>();
            for (int j = 0; j < 100; j++) {
                avgC_LES[i][j] = new LinkedList<>();
                weighted_AvgC_LES[i][j] = -1.0;
            }
        }
        Sim.init();
        new EndOfSim().schedule(getDuration(" 20:00:00", " 08:00:00") * 1000);
    }

    public void createCustomers(String filepath) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(filepath));
        br.readLine();

        for (String line = br.readLine(); line != null; line = br.readLine()) {
            Customer customer = new Customer();
            String[] columns = line.split(",");

            customer.setType(Integer.parseInt(columns[8]));
            customer.setArrival_time(getTime(columns[1]));
            customer.setIs_served(isNotNull(columns[4]));

            if (isNotNull(columns[4])) {
                customer.setReal_waiting_time(getDuration(columns[4], columns[1]));
                customer.setService_time(getDuration(columns[7], columns[4]));
            } else {
                customer.setReal_waiting_time(getDuration(columns[7], columns[1]));
                customer.setService_time(-1.0);
            }

            if (getDuration(columns[1], " 08:00:00") >= 0) {
                new Arrival(customer).schedule(getDuration(columns[1], " 08:00:00") * 1000);
            }
        }

        br.close();
        Sim.start();
    }

    public LocalTime getTime(String dateTimeString) {
        return LocalTime.parse(dateTimeString.split(" ")[1].replace("\"", ""));
    }

    public double getDuration(String dateTimeString1, String dateTimeString2) {
        return Duration.between(getTime(dateTimeString2), getTime(dateTimeString1)).getSeconds();
    }

    public boolean isNotNull(String answered) {
        return !answered.equals("\"NULL\"");
    }

    class Arrival extends Event {
        private Customer customer;

        public Arrival(Customer customer) {
             this.customer = customer;
        }

        @Override
        public void actions() {
            waiting_list_length[customer.getType()] += 1;
            new Departure(customer).schedule(customer.getReal_waiting_time() * 1000);
        }
    }

    class Departure extends Event {
        private Customer customer;

        public Departure(Customer customer) {
            this.customer = customer;
        }

        @Override
        public void actions() {
            waiting_list_length[customer.getType()] -= 1;
            customer.setWaiting_list_length(waiting_list_length);
            // Set LES for Customer and MAJ of LES value
            customer.setLES(LES[customer.getType()]);
            // Set avg_LES for Customer and MAJ of avg_LES value
            customer.setAvg_LES(getMean(avg_LES[customer.getType()]));
            // Set avgC_LES for Customer and MAJ of avgC_LES value
            customer.setAvgC_LES(getMean(avgC_LES[customer.getType()][waiting_list_length[customer.getType()]]));
            //
            if (customer.getIs_served()) {
                LES[customer.getType()] = customer.getReal_waiting_time();
                updatePredictor(avg_LES[customer.getType()], customer.getReal_waiting_time());
                updatePredictor(avgC_LES[customer.getType()][waiting_list_length[customer.getType()]], customer.getReal_waiting_time());
            }
            // Set weighted_AvgC_LES for Customer and MAJ of weighted_AvgC_LES value
            if (weighted_AvgC_LES[customer.getType()][waiting_list_length[customer.getType()]] == -1) {
                customer.setWeighted_AvgC_LES(-1);
                if (customer.getIs_served()) {
                    weighted_AvgC_LES[customer.getType()][waiting_list_length[customer.getType()]] = customer.getReal_waiting_time();
                }
            } else {
                customer.setWeighted_AvgC_LES(
                        weighted_AvgC_LES[customer.getType()][waiting_list_length[customer.getType()]]
                );
                if (customer.getIs_served()) {
                    weighted_AvgC_LES[customer.getType()][waiting_list_length[customer.getType()]] =
                            smoothing_factor * customer.getLES() +
                                    (1 - smoothing_factor) *
                                            weighted_AvgC_LES[customer.getType()][waiting_list_length[customer.getType()]];
                }
            }
            if (customer.getIs_served()) {
                number_of_server += 1;
                customer.setNumber_of_server(number_of_server);
                new EndOfService(customer).schedule(customer.getService_time() * 1000);
            } else {
                customer.setNumber_of_server(number_of_server);
                abandoned_customer.add(customer);
                writeLine(customer);
            }
        }
    }

    class EndOfService extends Event {
        private Customer customer;

        public EndOfService(Customer customer) {
            this.customer = customer;
        }

        @Override
        public void actions() {
            number_of_server -= 1;
            served_customer.add(customer);
            writeLine(customer);
        }
    }

    class EndOfSim extends Event {

        public void actions() {
            Sim.stop();
        }
    }

    public double getMean(LinkedList<Double> list) {
        return list.stream()
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(-1);
    }

    public void updatePredictor(LinkedList<Double> predictorList, double value) {
        if (predictorList.size() == 10) {
            predictorList.removeFirst();
            predictorList.addLast(value);
        } else {
            predictorList.addLast(value);
        }
    }

    public void writeLine(Customer customer) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(datasetCSVFilePath, true))) {
            writer.write(customer.toString());
            writer.newLine();
        } catch (IOException e) {
            System.out.println("An error occurred while appending data to the CSV file: " + e.getMessage());
        }
    }
}
