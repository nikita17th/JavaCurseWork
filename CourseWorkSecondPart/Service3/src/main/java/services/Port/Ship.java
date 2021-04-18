package services.Port;

import services.Timetable.Cargo;
import services.Timetable.DayAndTime;


public class Ship implements Comparable<Ship>{
    private DayAndTime dataAndTimeOfArrival;
    private String nameShip;
    private Cargo cargo;
    private DayAndTime parkingTime;

    private int countCranesUnloading = 0;
    private boolean isUnloading = false;
    private DayAndTime startTimeUnloading = new DayAndTime();
    private DayAndTime finishTimeUnloading = new DayAndTime();

    public Ship() {
        dataAndTimeOfArrival = new DayAndTime();
        nameShip = "";
        cargo = new Cargo();
        parkingTime = new DayAndTime();
    }

    public Ship(DayAndTime dataAndTimeOfArrival, String nameShip, Cargo cargo, DayAndTime parkingTime) {
        this.dataAndTimeOfArrival = dataAndTimeOfArrival;
        this.nameShip = nameShip;
        this.cargo = cargo;
        this.parkingTime = parkingTime;
    }

    public Ship(Ship ship) {
        dataAndTimeOfArrival = new DayAndTime(ship.dataAndTimeOfArrival);
        nameShip = ship.nameShip;
        cargo = new Cargo(ship.cargo);
        parkingTime = new DayAndTime(ship.parkingTime);
    }

    public Cargo getCargo() {
        return cargo;
    }

    public void setCargo(Cargo cargo) {
        this.cargo = cargo;
    }

    public DayAndTime getDataAndTimeOfArrival() {
        return dataAndTimeOfArrival;
    }

    public void setDataAndTimeOfArrival(DayAndTime dataAndTimeOfArrival) {
        this.dataAndTimeOfArrival = dataAndTimeOfArrival;
    }

    public String getNameShip() {
        return nameShip;
    }

    public void setNameShip(String nameShip) {
        this.nameShip = nameShip;
    }

    public DayAndTime getParkingTime() {
        return parkingTime;
    }

    public void setParkingTime(DayAndTime parkingTime) {
        this.parkingTime = parkingTime;
    }

    @Override
    public int compareTo(Ship ship) {
        if (ship == null) {
            throw new IllegalArgumentException("Null parameter");
        }

        return dataAndTimeOfArrival.compareTo(ship.getDataAndTimeOfArrival());
    }

    public int getCountCranesUnloading() {
        return countCranesUnloading;
    }

    public void setCountCranesUnloading(int countCranesUnloading) {
        this.countCranesUnloading = countCranesUnloading;
    }

    public void addCountCranesUnloading() {
        countCranesUnloading++;
    }

    public boolean isUnloading() {
        return isUnloading;
    }

    public void setUnloading(boolean unloading) {
        isUnloading = unloading;
    }

    public DayAndTime getStartTimeUnloading() {
        return startTimeUnloading;
    }

    public void setStartTimeUnloading(DayAndTime startTimeUnloading) {
        this.startTimeUnloading = startTimeUnloading;
    }

    public DayAndTime getFinishTimeUnloading() {
        return finishTimeUnloading;
    }

    public void setFinishTimeUnloading(DayAndTime finishTimeUnloading) {
        this.finishTimeUnloading = finishTimeUnloading;
    }
}
