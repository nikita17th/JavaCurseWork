package Port;

import Timetable.Cargo;
import Timetable.DayAndTime;
import Timetable.TypeOfCargo;

import java.util.Objects;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static Port.Port.*;

public class Crane implements Runnable {
    private final Integer performance;
    private final TypeOfCargo typeOfCargo;
    private static final ConcurrentMap<TypeOfCargo, Lock> lockers = new ConcurrentHashMap<>();
    static {
        for (TypeOfCargo typeOfCargo : TypeOfCargo.values()) {
            lockers.put(typeOfCargo,  new ReentrantLock());
        }
    }
    public Crane(Integer performance, TypeOfCargo typeOfCargo) {
        this.performance = performance;
        this.typeOfCargo = typeOfCargo;
    }

    public Integer getPerformance() {
        return performance;
    }

    public TypeOfCargo getTypeOfCargo() {
        return typeOfCargo;
    }

    @Override
    public void run() {
        CyclicBarrier cyclicBarrier = Port.cyclicBarrier;
        int timeStop = 0;
        Ship unloadingShip = null;
        try {
            while (getTime() < DayAndTime.MINUTE_IN_THIRTY_DAYS) {
                lockers.get(typeOfCargo).lock();
                countOfWaitUnloadingShips.put(typeOfCargo, countOfWaitUnloadingShips.get(typeOfCargo) +
                        queuesForUnloading.get(typeOfCargo).size());
                if (!queuesShips.get(typeOfCargo).isEmpty()) {
                    if (Objects.requireNonNull(queuesShips.get(typeOfCargo).peek()).getDataAndTimeOfArrival().receiveTimeInMinute() <=
                            getTime()) {
                        queuesForUnloading.get(typeOfCargo).add(queuesShips.get(typeOfCargo).poll());
                    }
                }

                if (unloadingShip == null) {
                    if (!queuesForUnloading.get(typeOfCargo).isEmpty()) {
                        unloadingShip = queuesForUnloading.get(typeOfCargo).poll();
                        nowInUnloading.get(typeOfCargo).add(unloadingShip);
                        assert unloadingShip != null;
                        unloadingShip.addCountCranesUnloading();
                        Cargo cargo = unloadingShip.getCargo();
                        timeStop = DayAndTime.generateRandomMinute(0, 1440);
                        maxOfTimeStop.put(typeOfCargo, Integer.max(maxOfTimeStop.get(typeOfCargo), timeStop));
                        sumOfTimeStop.put(typeOfCargo, sumOfTimeStop.get(typeOfCargo) + timeStop);
                        cargo.setAmountOfCargo(cargo.getAmountOfCargo() + timeStop * performance);
                        countOfFreeCranes.replace(typeOfCargo, countOfFreeCranes.get(typeOfCargo) - 1);
                    }
                }

                if (unloadingShip == null) {
                    if (!nowInUnloading.get(typeOfCargo).isEmpty()) {
                        for (Ship ship : nowInUnloading.get(typeOfCargo)) {
                            if (ship.getCountCranesUnloading() < 2 && countOfFreeCranes.get(typeOfCargo) > 0) {
                                unloadingShip = ship;
                                unloadingShip.addCountCranesUnloading();
                                countOfFreeCranes.replace(typeOfCargo, countOfFreeCranes.get(typeOfCargo) - 1);
                                break;
                            }
                        }
                    }
                }

                if (unloadingShip == null) {
                    lockers.get(typeOfCargo).unlock();
                    cyclicBarrier.await();
                    continue;
                } else {
                    if (!unloadingShip.isUnloading()) {
                        unloadingShip.setUnloading(true);
                        DayAndTime startTimeUnloading = new DayAndTime();
                        startTimeUnloading.setMinute(getTime());
                        unloadingShip.setStartTimeUnloading(startTimeUnloading);
                    }

                    if (unloadingShip.isUnloading()) {
                        unloadingShip.getCargo().reduceAmountOfCargo(performance);
                    }

                    if (unloadingShip.getCargo().getAmountOfCargo() == 0) {
                        if (unloadingShip.getCountCranesUnloading() > 0) {
                            unloadingShip.setCountCranesUnloading(unloadingShip.getCountCranesUnloading() - 1);
                            countOfFreeCranes.replace(typeOfCargo, countOfFreeCranes.get(typeOfCargo) + 1);
                        }

                        if (unloadingShip.getCountCranesUnloading() == 0) {
                            DayAndTime finishTimeUnloading = new DayAndTime();
                            finishTimeUnloading.setMinute(getTime());
                            unloadingShip.setFinishTimeUnloading(finishTimeUnloading);
                            unloadedShips.get(typeOfCargo).add(unloadingShip);
                            unloadingShip.setUnloading(false);
                            //System.out.println(unloadedShips.get(typeOfCargo).size() + typeOfCargo.toString());
                            nowInUnloading.get(typeOfCargo).remove(unloadingShip);
                            //                          System.out.println(queuesShips.get(typeOfCargo).size() + typeOfCargo.toString());
                        }
                        unloadingShip = null;
                    }

                }
                lockers.get(typeOfCargo).unlock();
                cyclicBarrier.await();
            }
            lockers.get(typeOfCargo).lock();
            if (unloadingShip != null) {
                int timeFine = (getTime() - (unloadingShip.getDataAndTimeOfArrival().receiveTimeInMinute() +
                        unloadingShip.getParkingTime().receiveTimeInMinute()));
                if (timeFine > 0) {
                    timeFine = timeFine / DayAndTime.MAX_MINUTE +
                            timeFine % DayAndTime.MAX_MINUTE != 0 ? 1 : 0;
                    int fine = timeFine * 100;
                    mapOfFine.replace(typeOfCargo, mapOfFine.get(typeOfCargo) + fine);
                }
            }
            lockers.get(typeOfCargo).unlock();
        } catch (InterruptedException | BrokenBarrierException e) {
            e.printStackTrace();
        }
    }
}
