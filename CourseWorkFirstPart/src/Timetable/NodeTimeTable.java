package Timetable;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonAutoDetect
public class NodeTimeTable implements Comparable<NodeTimeTable>{
   private DayAndTime dataAndTimeOfArrival;
   private String nameShip;
   private Cargo cargo;
   private DayAndTime parkingTime;

   public NodeTimeTable() {
   }

   public NodeTimeTable(DayAndTime dataAndTimeOfArrival, String nameShip, Cargo cargo, DayAndTime parkingTime) {
      this.dataAndTimeOfArrival = dataAndTimeOfArrival;
      this.nameShip = nameShip;
      this.cargo = cargo;
      this.parkingTime = parkingTime;
   }

   public DayAndTime getDataAndTimeOfArrival() {
      return dataAndTimeOfArrival;
   }

   public String getNameShip() {
      return nameShip;
   }

   public Cargo getCargo() {
      return cargo;
   }

   public DayAndTime getParkingTime() {
      return parkingTime;
   }

   public void setDataAndTimeOfArrival(DayAndTime dataAndTimeOfArrival) {
      this.dataAndTimeOfArrival = dataAndTimeOfArrival;
   }

   public void setNameShip(String nameShip) {
      this.nameShip = nameShip;
   }

   public void setCargo(Cargo cargo) {
      this.cargo = cargo;
   }

   public void setParkingTime(DayAndTime parkingTime) {
      this.parkingTime = parkingTime;
   }

   @Override
   public int compareTo(NodeTimeTable nodeTimeTable) {
      if (nodeTimeTable == null) {
         throw new IllegalArgumentException("Null parameter");
      }

      return dataAndTimeOfArrival.compareTo(nodeTimeTable.getDataAndTimeOfArrival());
   }

   @Override
   public String toString() {
      return "NodeTimeTable{" +
              "dataAndTimeOfArrival=" + dataAndTimeOfArrival +
              ", nameShip='" + nameShip + '\'' +
              ", cargo=" + cargo +
              ", parkingTime=" + parkingTime +
              '}';
   }
}
