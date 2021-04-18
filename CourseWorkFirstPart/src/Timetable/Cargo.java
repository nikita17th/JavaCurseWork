package Timetable;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonAutoDetect
public class Cargo {
    private TypeOfCargo typeOfCargo;
    private int amountOfCargo;

    public Cargo() {
    }

    public Cargo(Cargo cargo) {
        typeOfCargo = cargo.typeOfCargo;
        amountOfCargo = cargo.amountOfCargo;
    }

    public Cargo(TypeOfCargo typeOfCargo, int amountOfCargo) {
        if (amountOfCargo <= 0) {
            throw new IllegalArgumentException("Amount of cargo must be more zero!");
        }
        this.typeOfCargo = typeOfCargo;
        this.amountOfCargo = amountOfCargo;
    }

    public TypeOfCargo getTypeOfCargo() {
        return typeOfCargo;
    }

    public void setTypeOfCargo(TypeOfCargo typeOfCargo) {
        this.typeOfCargo = typeOfCargo;
    }

    public Integer getAmountOfCargo() {
        return amountOfCargo;
    }

    public void reduceAmountOfCargo(int countCargo) {
        amountOfCargo -= countCargo;
        if (amountOfCargo < 0) {
            amountOfCargo = 0;
        }
    }

    public void setAmountOfCargo(Integer amountOfCargo) {
        if (amountOfCargo <= 0) {
            throw new IllegalArgumentException("Amount of cargo must be more zero!");
        }
        this.amountOfCargo = amountOfCargo;
    }

    @Override
    public String toString() {
        return "Cargo{" +
                "typeOfCargo=" + typeOfCargo +
                ", amountOfCargo=" + amountOfCargo +
                '}';
    }
}
