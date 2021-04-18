package JsonFile;

import Port.Ship;
import Timetable.*;
import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.InputMismatchException;
import java.util.Scanner;

import static Port.Port.*;

public class ExecuteTask extends Application {
    public static final String ERROR_FORMAT_INT = "You should input integer number, re-enter the record again";

    public static void main(String[] args) throws IOException {
        launch(args);

        TimeTable timeTable = GenerateParameters.getTimeTable();
        PerformanceCranes performanceCranes = GenerateParameters.getPerformanceCranes();

        addNodes(timeTable);

        Path pathToTimeTable = Paths.get("timeTable.json");
        Path pathToPerformanceCranes = Paths.get("performanceCranes.json");

        ObjectWriter objectWriterTimeTable = new ObjectMapper().writer().withDefaultPrettyPrinter();
        Writer jsonFileTimeTable = new FileWriter(String.valueOf(pathToTimeTable));
        objectWriterTimeTable.writeValue(jsonFileTimeTable, timeTable);

        ObjectWriter objectWriterPerformanceCranes = new ObjectMapper().writer().withDefaultPrettyPrinter();
        Writer jsonFilePerformanceCranes = new FileWriter(String.valueOf(pathToPerformanceCranes));
        objectWriterPerformanceCranes.writeValue(jsonFilePerformanceCranes, performanceCranes);

        Port.Port.work();

        Path pathToReport = Paths.get("report.json");

        JsonFactory jsonFactory = new JsonFactory();
        JsonGenerator jsonGenerator = jsonFactory.createGenerator(new File(String.valueOf(pathToReport)), JsonEncoding.UTF8);


        int allCountOfWaitUnloadingShips = 0;
        int allCountOfShipsUnloaded = 0;
        int allSumOfTimeUnloadingShips = 0;
        double allSumOfTimeStop = 0.0;
        int allMaxOfTimeStop = 0;
        for (TypeOfCargo typeOfCargo : TypeOfCargo.values()) {
            for (Ship ship : unloadedShips.get(typeOfCargo)) {
                jsonGenerator.writeStartObject();
                jsonGenerator.writeStringField("Name", ship.getNameShip());
                jsonGenerator.writeStringField("Time arrive" , ship.getDataAndTimeOfArrival().toString());
                jsonGenerator.writeStringField("Time waiting", new DayAndTime(0, 0,
                        ship.getStartTimeUnloading().receiveTimeInMinute() -
                                ship.getDataAndTimeOfArrival().receiveTimeInMinute()).toString());

                jsonGenerator.writeStringField("Time start unloading", ship.getStartTimeUnloading().toString());
                jsonGenerator.writeStringField("Time unloaded",  new DayAndTime(0, 0,
                        ship.getFinishTimeUnloading().receiveTimeInMinute() -
                                ship.getStartTimeUnloading().receiveTimeInMinute()).toString());
                jsonGenerator.writeEndObject();
                jsonGenerator.writeRaw('\n');
            }
            allSumOfTimeUnloadingShips += sumOfTimeUnloadingShips.get(typeOfCargo);
            sumOfFine += mapOfFine.get(typeOfCargo) - (countOfCranes.get(typeOfCargo) - 1) * COAST_ONE_CRANE;
            allSumOfTimeStop += sumOfTimeStop.get(typeOfCargo);
            allCountOfShipsUnloaded += countOfShipsUnloaded.get(typeOfCargo);
            allCountOfWaitUnloadingShips += countOfWaitUnloadingShips.get(typeOfCargo);
            allMaxOfTimeStop = Integer.max(maxOfTimeStop.get(typeOfCargo), allMaxOfTimeStop);
        }

        jsonGenerator.writeRaw('\n');
        jsonGenerator.writeStartObject();
        jsonGenerator.writeNumberField("Count of ships unloaded", allCountOfShipsUnloaded);
        jsonGenerator.writeRaw('\n');
        jsonGenerator.writeNumberField("Average length queue to unloading", allCountOfWaitUnloadingShips /
                DayAndTime.MINUTE_IN_THIRTY_DAYS);
        jsonGenerator.writeRaw('\n');
        jsonGenerator.writeStringField("Average time wait to unloading", new DayAndTime(0, 0,
                Math.round(allSumOfTimeUnloadingShips / allCountOfShipsUnloaded)).toString());
        jsonGenerator.writeRaw('\n');
        jsonGenerator.writeNumberField("Max time delay", allMaxOfTimeStop);
        jsonGenerator.writeRaw('\n');
        jsonGenerator.writeNumberField("Average time delay: ", allSumOfTimeStop / allCountOfShipsUnloaded);
        jsonGenerator.writeRaw('\n');
        jsonGenerator.writeNumberField("Sum of fine", sumOfFine);
        jsonGenerator.writeRaw('\n');
        jsonGenerator.writeNumberField("Count of loose cranes", countOfCranes.get(TypeOfCargo.LOOSE));
        jsonGenerator.writeRaw('\n');
        jsonGenerator.writeNumberField("Count of liquid cranes", countOfCranes.get(TypeOfCargo.LIQUID));
        jsonGenerator.writeRaw('\n');
        jsonGenerator.writeNumberField("Count of container cranes", countOfCranes.get(TypeOfCargo.CONTAINER));
        jsonGenerator.writeEndObject();
        jsonGenerator.close();
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


    @Override
    public void start(Stage stage) throws Exception {
        URL url = new File("src/JsonFile/GraphicsInterface.fxml").
                toURI().toURL();
        Parent root = FXMLLoader.load(url);

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
        // stage.setResizable(false);
    }
}
