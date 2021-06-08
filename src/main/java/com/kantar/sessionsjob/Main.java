package com.kantar.sessionsjob;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static java.util.stream.Collectors.groupingBy;

public class Main {

    public static void main(String[] args) {

        if (args.length < 2) {
            System.err.println("Missing arguments: <input-statements-file> <output-sessions-file>");
            System.exit(1);
        }

        // buffer for reading input file
        BufferedReader psvInput = null;
        // buffer for creation and writing to output file
        BufferedWriter psvOutput = null;
        String inputFileName = args[0];
        String outputFileName = args[1];
        String line = "";
        // Array which contains sessions information
        ArrayList<Session> sessions = new ArrayList<>();
        // Map which contains split sessions per home
        Map<Integer, List<Session>> splitSessions;

        // try to open file with input data
        try {
            psvInput = new BufferedReader(new FileReader(inputFileName));
            psvOutput = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(outputFileName), StandardCharsets.UTF_8));
            int i = 0;
            // parse lines which contains data into list of Session class instances
            while ((line = psvInput.readLine()) != null) {
                String[] row = line.split("\\|");
                // store header line for output file
                if (i == 0)  {
                    psvOutput.write(line + "|EndTime|Duration");
                    psvOutput.newLine();
                }
                if (i > 0) {
                    sessions.add(new Session(row));
                }
                i++;
            }

            // sort collected list first by home number, then by time
            sessions.sort((Comparator<? super Session>) (o1, o2) -> {
                Integer firstHomeNr  = o1.getHomeNr();
                Integer secondHomeNr = o2.getHomeNr();
                int comp = firstHomeNr.compareTo(secondHomeNr);
                if (comp != 0) {
                    return comp;
                }

                Date firstStart = o1.getStartTime();
                Date secondStart = o2.getStartTime();
                return firstStart.compareTo(secondStart);
            });

            // convert ArrayList to basic array to be able to split it
            Session[] sortedSessions = sessions.toArray(new Session[0]);
            // split collected lines by home number using map
            splitSessions = Arrays.stream(sortedSessions).collect(groupingBy(Session::getHomeNr));

            // iterate through new created lists and extend Session instances with calculated end time and duration
            for (Map.Entry<Integer, List<Session>> entry : splitSessions.entrySet()) {
                List<Session> sessionsList = entry.getValue();
                // if list size is more then one calculate end time and duration based on next record
                for (int idx = 1; idx < sessionsList.size(); idx++) {
                    // calculate and set end time
                    sessionsList.get(idx-1).calculateEndTime(sessionsList.get(idx));
                    // duration calculation
                    sessionsList.get(idx-1).calculateDuration();
                    // write line to output file
                    psvOutput.write(sessionsList.get(idx-1).toString());
                    psvOutput.newLine();
                }

                if (sessionsList.size() != 0) {
                    // if list size is equal to 1 or last record reached set time to 23:59:59 and calculate duration
                    sessionsList.get(sessionsList.size() - 1).setEndTimeToMax();
                    sessionsList.get(sessionsList.size() - 1).calculateDuration();
                    // write line to output file
                    psvOutput.write(sessionsList.get(sessionsList.size() - 1).toString());
                    psvOutput.newLine();
                }
            }
            psvOutput.close();
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            try {
                assert psvInput != null;
                psvInput.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
