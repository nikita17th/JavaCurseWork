package Port;

import Timetable.*;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.*;

import static Timetable.TypeOfCargo.*;

public class Port {
    private static TimeTable timeTable;
    private static TreeSet<Ship> ships;
    private static final Map<TypeOfCargo, Boolean> foundOptimal = new HashMap<>();
    static {
        for (TypeOfCargo typeOfCargo: TypeOfCargo.values()) {
            foundOptimal.put(typeOfCargo, false);
        }
    }
    public static Map<TypeOfCargo, Double> sumOfTimeStop = new HashMap<>();
    public static Map<TypeOfCargo, Integer> maxOfTimeStop = new HashMap<>();
    public static Map<TypeOfCargo, Double> sumOfTimeUnloadingShips = new HashMap<>();
    public static Map<TypeOfCargo, Double> countOfWaitUnloadingShips = new HashMap<>();
    public static Map<TypeOfCargo, Integer> countOfShipsUnloaded = new HashMap<>();
    public static final int COAST_ONE_CRANE = 30000;

    public static Integer sumOfFine = 0;

    public static ConcurrentMap<TypeOfCargo, CopyOnWriteArrayList<Ship>> nowInUnloading = new ConcurrentHashMap<>();
    public static ConcurrentMap<TypeOfCargo, CopyOnWriteArrayList<Ship>> unloadedShips = new ConcurrentHashMap<>();
    public static ConcurrentMap<TypeOfCargo, ConcurrentLinkedQueue<Ship>> queuesShips = new ConcurrentHashMap<>();
    public static ConcurrentMap<TypeOfCargo, ConcurrentLinkedQueue<Ship>> queuesForUnloading = new ConcurrentHashMap<>();
    public static ConcurrentMap<TypeOfCargo, Integer> countOfFreeCranes = new ConcurrentHashMap<>();
    public static ConcurrentMap<TypeOfCargo, Integer> mapOfFine = new ConcurrentHashMap<>();
    public static Map<TypeOfCargo, List<Thread>> mapOfCranes = new HashMap<>();
    public static Map<TypeOfCargo, Integer> countOfCranes = new HashMap<>();
    public static Map<TypeOfCargo, Integer> mapOfPerformanceCrane = new HashMap<>();
    public static volatile Timer timer;
    public static CyclicBarrier cyclicBarrier;

    public static void work() throws IOException {
        acceptParametersFromJson();
        executeIntroducingAnError();
        findOptimal();
    }

    private static void initStatistic() {
        sumOfFine = 0;
        for (TypeOfCargo typeOfCargo : values()) {
            if (!foundOptimal.get(typeOfCargo)) {
                sumOfTimeUnloadingShips.put(typeOfCargo, 0.0);
                countOfWaitUnloadingShips.put(typeOfCargo, 0.0);
                maxOfTimeStop.put(typeOfCargo, 0);
                sumOfTimeStop.put(typeOfCargo, 0.0);
                countOfShipsUnloaded.put(typeOfCargo, 0);
                mapOfFine.put(typeOfCargo, 0);
            }
        }
    }


    private static void findOptimal() {
        initCountOfCranes();
        initStatistic();
        executeQueuing();
        executeModeling();
        countFine();
        printStatistic();

        ConcurrentMap<TypeOfCargo, Integer> oldMapOfFine = new ConcurrentHashMap<>();

        for (TypeOfCargo typeOfCargo : values()) {
            oldMapOfFine.put(typeOfCargo, mapOfFine.get(typeOfCargo));
        }

        while (!foundOptimal.get(LOOSE) || !foundOptimal.get(LIQUID) || !foundOptimal.get(CONTAINER)) {
            for (TypeOfCargo typeOfCargo : TypeOfCargo.values()) {
                if (!foundOptimal.get(typeOfCargo)) {
                    countOfFreeCranes.put(typeOfCargo, countOfFreeCranes.get(typeOfCargo) + 1);
                } else {
                    countOfFreeCranes.put(typeOfCargo, 0);
                }
            }

            initStatistic();
            executeQueuing();
            executeModeling();
            countFine();

            for (TypeOfCargo typeOfCargo : TypeOfCargo.values()) {
                if (!foundOptimal.get(typeOfCargo)) {
                    if (mapOfFine.get(typeOfCargo) + COAST_ONE_CRANE * (countOfFreeCranes.get(typeOfCargo) - 1) >
                            oldMapOfFine.get(typeOfCargo)){
                        mapOfFine.put(typeOfCargo, oldMapOfFine.get(typeOfCargo));
                        countOfCranes.put(typeOfCargo, countOfFreeCranes.get(typeOfCargo) - 1);
                        foundOptimal.put(typeOfCargo, true);
                    } else{
                        oldMapOfFine.put(typeOfCargo, mapOfFine.get(typeOfCargo) + COAST_ONE_CRANE *
                                (countOfFreeCranes.get(typeOfCargo) - 1));
                    }
                }
            }
        }
        for (TypeOfCargo typeOfCargo : TypeOfCargo.values()) {
            for (Ship ship : unloadedShips.get(typeOfCargo)) {

                System.out.println("Name: " + ship.getNameShip());
                System.out.println("Time arrive: " + ship.getDataAndTimeOfArrival().toString());
                System.out.println("Time waiting: " + new DayAndTime(0, 0,
                        ship.getStartTimeUnloading().receiveTimeInMinute() -
                                ship.getDataAndTimeOfArrival().receiveTimeInMinute()).toString());

                System.out.println("Time start unloading: " + ship.getStartTimeUnloading().toString());
                System.out.println("Time unloaded: " +  new DayAndTime(0, 0,
                        ship.getFinishTimeUnloading().receiveTimeInMinute() -
                                ship.getStartTimeUnloading().receiveTimeInMinute()).toString());
                System.out.println();
            }
        }
        printStatistic();
    }

    private static void printStatistic() {
        int allCountOfWaitUnloadingShips = 0;
        int allCountOfShipsUnloaded = 0;
        int allSumOfTimeUnloadingShips = 0;
        double allSumOfTimeStop = 0.0;
        int allMaxOfTimeStop = 0;
        for (TypeOfCargo typeOfCargo : values()) {
            for (Ship ship : unloadedShips.get(typeOfCargo)) {
                if (!foundOptimal.get(typeOfCargo)) {
                    sumOfTimeUnloadingShips.put(typeOfCargo, sumOfTimeUnloadingShips.get(typeOfCargo) +
                            (ship.getStartTimeUnloading().receiveTimeInMinute() -
                            ship.getDataAndTimeOfArrival().receiveTimeInMinute()));
                }
            }

            allSumOfTimeUnloadingShips += sumOfTimeUnloadingShips.get(typeOfCargo);
            countOfShipsUnloaded.put(typeOfCargo, countOfShipsUnloaded.get(typeOfCargo) +
                    unloadedShips.get(typeOfCargo).size());


            sumOfFine += mapOfFine.get(typeOfCargo) - (countOfCranes.get(typeOfCargo) - 1) * COAST_ONE_CRANE;
            allSumOfTimeStop += sumOfTimeStop.get(typeOfCargo);
            allCountOfShipsUnloaded += countOfShipsUnloaded.get(typeOfCargo);
            allCountOfWaitUnloadingShips += countOfWaitUnloadingShips.get(typeOfCargo);
            allMaxOfTimeStop = Integer.max(maxOfTimeStop.get(typeOfCargo), allMaxOfTimeStop);
        }


        System.out.println("Count of ships unloaded: " + allCountOfShipsUnloaded);
        System.out.println("Average length queue to unloading: " + allCountOfWaitUnloadingShips /
                DayAndTime.MINUTE_IN_THIRTY_DAYS);
        System.out.println("Average time wait to unloading: " + new DayAndTime(0, 0,
                Math.round(allSumOfTimeUnloadingShips / allCountOfShipsUnloaded)));
        System.out.println("Max time delay: " + allMaxOfTimeStop);
        System.out.println("Average time delay: " +  allSumOfTimeStop / allCountOfShipsUnloaded);
        System.out.println("Sum of fine: " + sumOfFine);

        System.out.println("Count of loose cranes: " + countOfCranes.get(LOOSE));
        System.out.println("Count of liquid cranes: " + countOfCranes.get(LIQUID));
        System.out.println("Count of container cranes: " + countOfCranes.get(CONTAINER));

        System.out.println();
    }

    private static void countFine() {
        for (TypeOfCargo typeOfCargo : values()) {
            for (Ship ship : unloadedShips.get(typeOfCargo)) {
                int timeFine = (ship.getFinishTimeUnloading().receiveTimeInMinute() -
                        (ship.getDataAndTimeOfArrival().receiveTimeInMinute() +
                                ship.getParkingTime().receiveTimeInMinute()));
                if (timeFine > 0) {
                    timeFine = timeFine / DayAndTime.MAX_MINUTE + (timeFine % 60 == 0 ? 0 : 1);
                    int fine = timeFine * 100;
                    mapOfFine.put(typeOfCargo, mapOfFine.get(typeOfCargo) + fine);
                }
            }

            for (Ship ship : queuesForUnloading.get(typeOfCargo)) {
                int timeFine = (DayAndTime.MINUTE_IN_THIRTY_DAYS -
                        (ship.getDataAndTimeOfArrival().receiveTimeInMinute() +
                                ship.getParkingTime().receiveTimeInMinute()));
                if (timeFine > 0) {
                    timeFine = timeFine / DayAndTime.MAX_MINUTE + (timeFine % 60 == 0 ? 0 : 1);
                    int fine = timeFine * 100;
                    mapOfFine.put(typeOfCargo, mapOfFine.get(typeOfCargo) + fine);
                }
            }
        }
    }

    private static void executeModeling() {
        timer = new Timer();
        int countAllCranes = 0;
        Map<TypeOfCargo, Integer> oldCountOfFreeCranes = new HashMap<>();
        for (TypeOfCargo typeOfCargo : values()) {
            countAllCranes += countOfFreeCranes.get(typeOfCargo);
            oldCountOfFreeCranes.put(typeOfCargo, countOfFreeCranes.get(typeOfCargo));
        }
        cyclicBarrier = new CyclicBarrier(countAllCranes, timer);
        for (TypeOfCargo typeOfCargo : values()) {

            if (!foundOptimal.get(typeOfCargo)) {
                nowInUnloading.put(typeOfCargo, new CopyOnWriteArrayList<>());
                unloadedShips.put(typeOfCargo, new CopyOnWriteArrayList<>());
                mapOfCranes.put(typeOfCargo, new LinkedList<>());
                queuesForUnloading.put(typeOfCargo, new ConcurrentLinkedQueue<>());

                for (int i = 0; i < countOfFreeCranes.get(typeOfCargo); i++) {
                    mapOfCranes.get(typeOfCargo).add(new Thread(new Crane(mapOfPerformanceCrane.get(typeOfCargo),
                            typeOfCargo)));
                }

                for (Thread crane : mapOfCranes.get(typeOfCargo)) {
                    crane.start();
                }
            }
        }

        for (TypeOfCargo typeOfCargo : values()) {
            for (Thread crane : mapOfCranes.get(typeOfCargo)) {
                if (!foundOptimal.get(typeOfCargo)) {
                    try {
                        crane.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    countOfFreeCranes.put(typeOfCargo, oldCountOfFreeCranes.get(typeOfCargo));
                }

            }
        }
    }

    private static void executeQueuing() {
        for (TypeOfCargo typeOfCargo : values()) {
            queuesShips.put(typeOfCargo, new ConcurrentLinkedQueue<>());
        }

        for (Ship ship : ships) {
            queuesShips.get(ship.getCargo().getTypeOfCargo()).add(new Ship(ship));
        }
    }

    public static void executeIntroducingAnError() {
        ships = new TreeSet<>();
        for (NodeTimeTable nodeTimeTable : timeTable.getTimeTable()) {
            int randomTimeAdd = DayAndTime.generateRandomMinute(-DayAndTime.MINUTE_IN_SEVEN_DAYS,
                    DayAndTime.MINUTE_IN_SEVEN_DAYS);
            Ship ship;
            try {
                ship = new Ship(nodeTimeTable.getDataAndTimeOfArrival().addTime(randomTimeAdd),
                        nodeTimeTable.getNameShip(),
                        nodeTimeTable.getCargo(),
                        nodeTimeTable.getParkingTime());
            } catch (IllegalArgumentException ignored) {
                continue;
            }
            ships.add(ship);
        }
    }

    public static void acceptParametersFromJson() throws IOException {
        Path pathToTimeTable = Paths.get("timeTable.json");
        Path pathToPerformanceCranes = Paths.get("performanceCranes.json");

        ObjectMapper objectMapper = new ObjectMapper();
        timeTable = objectMapper.readValue(new File(String.valueOf(pathToTimeTable)), TimeTable.class);

        PerformanceCranes performanceCranes = objectMapper.readValue(new File(String.valueOf(pathToPerformanceCranes)),
                PerformanceCranes.class);

        mapOfPerformanceCrane.put(LOOSE, performanceCranes.getPerformanceLoose());
        mapOfPerformanceCrane.put(LIQUID, performanceCranes.getPerformanceLiquid());
        mapOfPerformanceCrane.put(CONTAINER, performanceCranes.getPerformanceContainer());
    }

    public static void initCountOfCranes() {
        for (TypeOfCargo typeOfCargo : values()) {
            countOfCranes.put(typeOfCargo, 1);
            countOfFreeCranes.put(typeOfCargo, 1);
        }
    }

    public static int getTime() {
        return timer.getTime();
    }

}
