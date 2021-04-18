package services.JsonFile;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import services.Timetable.*;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.InputMismatchException;
import java.util.Scanner;


@SpringBootApplication
@RestController
public class ExecuteTask {
    public static final String ERROR_FORMAT_INT = "You should input integer number, re-enter the record again";

    @GetMapping("/getGenerateTimeTable")
    public static String generateTimeTable(String args) throws IOException {
        RestTemplateBuilder builder = new RestTemplateBuilder();
        RestTemplate restTemplate = builder.build();
        String stringTimeTable = restTemplate.getForObject("http://localhost:8080/generateTimeTable", String.class);

        ObjectMapper objectMapper = new ObjectMapper();
        TimeTable timeTable = objectMapper.readValue(stringTimeTable, TimeTable.class);

        addNodes(timeTable);

        Path pathToTimeTable = null;
        if (args == null || args.isEmpty()) {
            pathToTimeTable = Paths.get("timeTable.json");
        } else {
            pathToTimeTable = Paths.get(args);
        }

        ObjectWriter objectWriterTimeTable = new ObjectMapper().writer().withDefaultPrettyPrinter();
        Writer jsonFileTimeTable = new FileWriter(String.valueOf(pathToTimeTable));
        objectWriterTimeTable.writeValue(jsonFileTimeTable, timeTable);

        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = null;
        try {
            json = ow.writeValueAsString(timeTable);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return json;
    }


    @GetMapping("/getPerformance")
    public static String getPerformance() throws IOException {
        RestTemplateBuilder builder = new RestTemplateBuilder();
        RestTemplate restTemplate = builder.build();
        String stringPerformance = restTemplate.getForObject("http://localhost:8080/generatePerformance", String.class);
        Path pathToPerformanceCranes = Paths.get("performanceCranes.json");
        ObjectMapper objectMapper = new ObjectMapper();

        PerformanceCranes performanceCranes = objectMapper.readValue(stringPerformance, PerformanceCranes.class);
        ObjectWriter objectWriterPerformanceCranes = new ObjectMapper().writer().withDefaultPrettyPrinter();
        Writer jsonFilePerformanceCranes = new FileWriter(String.valueOf(pathToPerformanceCranes));
        objectWriterPerformanceCranes.writeValue(jsonFilePerformanceCranes, performanceCranes);

        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = null;
        try {
            json = ow.writeValueAsString(performanceCranes);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return json;
    }

    @GetMapping("/getTimeTableByName")
    public static String getTimeTableByName(String args) throws IOException {
        RestTemplateBuilder builder = new RestTemplateBuilder();
        RestTemplate restTemplate = builder.build();

        File file = new File(args);
        if (!file.exists()) {
            return "Not have this file!";
        }
        Path pathToTimeTable = Paths.get(args);

        ObjectMapper objectMapper = new ObjectMapper();
        TimeTable timeTable = objectMapper.readValue(new File(String.valueOf(pathToTimeTable)), TimeTable.class);

        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = null;
        try {
            json = ow.writeValueAsString(timeTable);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return json;
    }


    @PostMapping("/postThirdService")
    public static void postThirdService(@RequestBody String report) throws IOException {
        Path pathToReport = Paths.get("report.json");

        ObjectWriter objectWriterTimeTable = new ObjectMapper().writer().withDefaultPrettyPrinter();
        Writer jsonFileTimeTable = new FileWriter(String.valueOf(pathToReport));
        objectWriterTimeTable.writeValue(jsonFileTimeTable, report);
    }

    public static void addNodes(TimeTable timeTable) {
        System.out.println("If you want add nodes in time table, write yes");

        Scanner in = new Scanner(System.in);
        if (!in.hasNext() || !in.next().toLowerCase().trim().equals("yes")) {
            return;
        }

        System.out.println("""
                Input node in format:
                Data and time of arrival <int int int> (day hour minute),
                name ship <string>,
                type of cargo <string> (LOOSE or LIQUID or CONTAINERS),
                amount of cargo <int>,
                parking time <int int int> (day hour minute)""");
        while (true) {
            try {
                System.out.println("If you want stop adding, input exit");
                String inputString = in.next().toLowerCase().trim();
                if (inputString.equals("exit")) {
                    return;
                }
                DayAndTime arriveTime = new DayAndTime(Integer.parseInt(inputString), in.nextInt(), in.nextInt());
                String nameShip = in.next().trim();
                String typeOfCargo = in.next().toUpperCase().trim();
                Cargo cargo;
                switch (typeOfCargo) {
                    case "LOOSE" -> cargo = new Cargo(TypeOfCargo.LOOSE, in.nextInt());
                    case "LIQUID" -> cargo = new Cargo(TypeOfCargo.LIQUID, in.nextInt());
                    case "CONTAINER" -> cargo = new Cargo(TypeOfCargo.CONTAINER, in.nextInt());
                    default -> {
                        System.out.println("Type of cargo does not match possible " +
                                "your input:" + typeOfCargo);
                        in.nextLine();
                        continue;
                    }
                }
                DayAndTime parkingTome = new DayAndTime(in.nextInt(), in.nextInt(), in.nextInt());
                NodeTimeTable node = new NodeTimeTable(arriveTime, nameShip, cargo, parkingTome);
                timeTable.getTimeTable().add(node);
                System.out.println("Input node success!");

            } catch (NumberFormatException | InputMismatchException numberFormatException) {
                System.out.println(ERROR_FORMAT_INT);
                in.nextLine();
            } catch (IllegalArgumentException illegalArgumentException) {
                System.out.println(illegalArgumentException.getMessage());
            }
        }
    }


}
