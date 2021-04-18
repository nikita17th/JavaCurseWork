package Timetable;



import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class GenerateParameters {
    public static final int MIN_COUNT_OF_NODES = 30;
    public static final int MAX_COUNT_OF_NODES = 90;

    public static final int MIN_LENGTH_NAME_SHIP = 8;
    public static final int MAX_LENGTH_NAME_SHIP = 20;

    public static final int PERFORMANCE_LOOSE = 10;
    public static final int PERFORMANCE_LIQUID = 15;
    public static final int PERFORMANCE_CONTAINER = 1;

    public static final int MIN_LOOSE_SHIP = 40000;
    public static final int MAX_LOOSE_SHIP = 60000;
    public static final int MIN_LIQUID_SHIP = 50000;
    public static final int MAX_LIQUID_SHIP = 70000;
    public static final int MIN_CONTAINER_SHIP = 2000;
    public static final int MAX_CONTAINER_SHIP = 6000;

    public static Map<NameParameters, Integer> generationParameters = new HashMap<>();

    private static int maxLooseShip;
    private static int minCountOfNodes;
    private static int maxCountOfNodes;
    private static int performanceLoose;
    private static int maxContainerShip;
    private static int minLengthNameShip;
    private static int maxLengthNameShip;
    private static int maxLiquidShip;
    private static int minLiquidShip;
    private static int minContainerShip;
    private static int minLooseShip;
    private static int performanceContainer;
    private static int performanceLiquid;

    private static void setRangesForGenerate() {
        minCountOfNodes = generationParameters.get(NameParameters.MIN_COUNT_OF_NODES);
        maxCountOfNodes = generationParameters.get(NameParameters.MAX_COUNT_OF_NODES);
        minLengthNameShip = generationParameters.get(NameParameters.MIN_LENGTH_NAME_SHIP);
        maxLengthNameShip = generationParameters.get(NameParameters.MAX_LENGTH_NAME_SHIP);

        performanceLoose = generationParameters.get(NameParameters.PERFORMANCE_LOOSE);
        performanceLiquid = generationParameters.get(NameParameters.PERFORMANCE_LIQUID);
        performanceContainer = generationParameters.get(NameParameters.PERFORMANCE_CONTAINER);

        minLooseShip = generationParameters.get(NameParameters.MIN_LOOSE_SHIP);
        maxLooseShip = generationParameters.get(NameParameters.MAX_LOOSE_SHIP);
        minLiquidShip = generationParameters.get(NameParameters.MIN_LIQUID_SHIP);
        maxLiquidShip = generationParameters.get(NameParameters.MAX_LIQUID_SHIP);
        minContainerShip = generationParameters.get(NameParameters.MIN_CONTAINER_SHIP);
        maxContainerShip = generationParameters.get(NameParameters.MAX_CONTAINER_SHIP);
    }

    public static PerformanceCranes getPerformanceCranes() {
        return new PerformanceCranes(performanceLoose, performanceLiquid, performanceContainer);
    }

    public static TimeTable getTimeTable() {
        setRangesForGenerate();
        System.out.println(generationParameters);
        int countOfNodes = generateRandomNumber(minCountOfNodes, maxCountOfNodes);
        TimeTable timeTable = new TimeTable();
        for (int i = 0; i < countOfNodes; i++) {
            int cargoType = generateRandomNumber(1, 3);
            Cargo cargo = null;
            int countOfMinutesParking = 0;
            switch (cargoType) {
                case 1:
                    cargo = new Cargo(TypeOfCargo.LOOSE, generateRandomNumber(minLooseShip, maxLooseShip));
                    countOfMinutesParking = cargo.getAmountOfCargo() / performanceLoose;
                    break;
                case 2:
                    cargo = new Cargo(TypeOfCargo.LIQUID, generateRandomNumber(minLiquidShip, maxLiquidShip));
                    countOfMinutesParking = cargo.getAmountOfCargo() / performanceLiquid;
                    break;
                case 3:
                    cargo = new Cargo(TypeOfCargo.CONTAINER, generateRandomNumber(minContainerShip, maxContainerShip));
                    countOfMinutesParking = cargo.getAmountOfCargo() / performanceContainer;
                    break;
                    default:
                        break;
            }
            NodeTimeTable nodeTimeTable = new NodeTimeTable(DayAndTime.generateRandomDateAndTime(0, 30),
                    new RandomString(generateRandomNumber(minLengthNameShip, maxLengthNameShip),
                            ThreadLocalRandom.current()).nextString(),
                    cargo,
                    new DayAndTime(countOfMinutesParking)
                    );
            timeTable.getTimeTable().add(nodeTimeTable);
        }
        printTimeTable(timeTable);
        return timeTable;
    }

    private static int generateRandomNumber(int lowerRange, int upperRange){
        return (int) (Math.floor(Math.random() * (upperRange - lowerRange + 1)) + lowerRange);
    }

    private static void printTimeTable(TimeTable timeTable) {

        StringBuilder nameShip = new StringBuilder("Name ship");
        String[] headers = {"Arrival Time", transformFieldTimeTable(nameShip, maxLengthNameShip),
                "Type of cargo", "Amount of cargo", "Parking time"};
        int lengthHeaders = Arrays.toString(headers).length();
        String headline = "TIME TABLE (count of nodes = " + timeTable.getTimeTable().size() + ")";
        int countSpaces = lengthHeaders - headline.length();
        System.out.println(" ".repeat(countSpaces / 2) + headline + " ".repeat(countSpaces / 2));
        System.out.println("=".repeat(lengthHeaders + headers.length + 1));
        for (String header : headers) {
            System.out.print("| " + header + " ");
        }
        System.out.println("|");
        System.out.println("|" + "=".repeat(Math.max(0, lengthHeaders + headers.length - 1)) + "|");
        for (NodeTimeTable node : timeTable.getTimeTable()) {
            StringBuilder field = new StringBuilder(node.getDataAndTimeOfArrival().toString());
            System.out.print("| " + transformFieldTimeTable(field, headers[0].length()));
            field = new StringBuilder(node.getNameShip());
            System.out.print("| " +transformFieldTimeTable(field, headers[1].length()));
            field = new StringBuilder(node.getCargo().getTypeOfCargo().toString());
            System.out.print("| " +transformFieldTimeTable(field, headers[2].length()));
            field = new StringBuilder(node.getCargo().getAmountOfCargo().toString());
            System.out.print("| " +transformFieldTimeTable(field, headers[3].length()));
            field = new StringBuilder(node.getParkingTime().toString());
            System.out.println("| " +transformFieldTimeTable(field, headers[4].length()) + "|");
        }
        System.out.println("=".repeat(lengthHeaders + headers.length + 1));
    }

    private static String transformFieldTimeTable(StringBuilder field, int length) {
        if (length > field.length()) {
            int countSpaces = length - field.length() + 1;
            field.append(" ".repeat(Math.max(0, countSpaces)));
        }
        return field.toString();
    }

}