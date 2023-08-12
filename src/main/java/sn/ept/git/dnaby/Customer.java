package sn.ept.git.dnaby;

import java.time.LocalTime;
import java.util.Arrays;

public class Customer {
    private int type;
    private int[] waiting_list_length = new int[27];
    private LocalTime arrival_time;
    private double service_time;
    private int number_of_server;
    private double LES;
    private double avg_LES;
    private double avgC_LES;
    private double weighted_AvgC_LES;
    private double real_waiting_time;
    private boolean is_served;

    public Customer() {}

    public int getType() {return type;}

    public void setType(int type) {this.type = type;}

    public void setArrival_time(LocalTime arrival_time) {this.arrival_time = arrival_time;}

    public void setNumber_of_server(int number_of_server) {this.number_of_server = number_of_server;}

    public double getLES() {return LES;}

    public void setLES(double LES) {this.LES = LES;}

    public void setAvg_LES(double avg_LES) {this.avg_LES = avg_LES;}

    public void setAvgC_LES(double avgC_LES) {this.avgC_LES = avgC_LES;}

    public void setWeighted_AvgC_LES(double weighted_AvgC_LES) {this.weighted_AvgC_LES = weighted_AvgC_LES;}

    public double getReal_waiting_time() {return real_waiting_time;}

    public void setReal_waiting_time(double real_waiting_time) {this.real_waiting_time = real_waiting_time;}

    public boolean getIs_served() {return is_served;}

    public void setIs_served(boolean is_served) {this.is_served = is_served;}

    public void setWaiting_list_length(int[] waiting_list_length) {
        if (waiting_list_length.length == this.waiting_list_length.length) {
            this.waiting_list_length = Arrays.copyOf(waiting_list_length, waiting_list_length.length);
        } else {
            System.out.println("Array length does not match. Update aborted.");
        }
    }

    public double getService_time() {return service_time;}

    public void setService_time(double service_time) {this.service_time = service_time;}

    @Override
    public String toString() {
        return type + "," + is_served + "," + arrival_time.toString() + "," + service_time + "," + number_of_server +
                "," + Arrays.toString(waiting_list_length)
                .replace("[", "")
                .replace("]", "")
                .replace(" ", "") + "," + LES + "," + avg_LES + "," + avgC_LES + ","
                + weighted_AvgC_LES + "," + real_waiting_time;
    }
}
