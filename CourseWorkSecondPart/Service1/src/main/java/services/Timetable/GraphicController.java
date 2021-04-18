package services.Timetable;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

public class GraphicController {

    @FXML
    private TextField maxLooseShip;
    @FXML
    private TextField minCountOfNodes;
    @FXML
    private TextField maxCountOfNodes;
    @FXML
    private TextField performanceLoose;
    @FXML
    private TextField maxContainerShip;
    @FXML
    private Button dataSend;
    @FXML
    private TextField minLengthNameShip;
    @FXML
    private TextField maxLengthNameShip;
    @FXML
    private TextField maxLiquidShip;
    @FXML
    private TextField minLiquidShip;
    @FXML
    private TextField minContainerShip;
    @FXML
    private TextField minLooseShip;
    @FXML
    private TextField performanceContainer;
    @FXML
    private TextField performanceLiquid;


    public void executeDataSend(ActionEvent actionEvent) {
        executeSetParameters(minCountOfNodes, NameParameters.MIN_COUNT_OF_NODES, GenerateParameters.MIN_COUNT_OF_NODES);
        executeSetParameters(maxCountOfNodes, NameParameters.MAX_COUNT_OF_NODES, GenerateParameters.MAX_COUNT_OF_NODES);

        System.out.println(minCountOfNodes);
        System.out.println(maxCountOfNodes);
        executeSetParameters(minLengthNameShip, NameParameters.MIN_LENGTH_NAME_SHIP,
                GenerateParameters.MIN_LENGTH_NAME_SHIP);
        executeSetParameters(maxLengthNameShip, NameParameters.MAX_LENGTH_NAME_SHIP,
                GenerateParameters.MAX_LENGTH_NAME_SHIP);

        executeSetParameters(performanceLoose, NameParameters.PERFORMANCE_LOOSE, GenerateParameters.PERFORMANCE_LOOSE);
        executeSetParameters(performanceLiquid, NameParameters.PERFORMANCE_LIQUID, GenerateParameters.PERFORMANCE_LIQUID);
        executeSetParameters(performanceContainer, NameParameters.PERFORMANCE_CONTAINER,
                GenerateParameters.PERFORMANCE_CONTAINER);

        executeSetParameters(minLooseShip, NameParameters.MIN_LOOSE_SHIP, GenerateParameters.MIN_LOOSE_SHIP);
        executeSetParameters(maxLooseShip, NameParameters.MAX_LOOSE_SHIP, GenerateParameters.MAX_LOOSE_SHIP);
        executeSetParameters(minLiquidShip, NameParameters.MIN_LIQUID_SHIP, GenerateParameters.MIN_LIQUID_SHIP);
        executeSetParameters(maxLiquidShip, NameParameters.MAX_LIQUID_SHIP, GenerateParameters.MAX_LIQUID_SHIP);
        executeSetParameters(minContainerShip, NameParameters.MIN_CONTAINER_SHIP, GenerateParameters.MIN_CONTAINER_SHIP);
        executeSetParameters(maxContainerShip, NameParameters.MAX_CONTAINER_SHIP, GenerateParameters.MAX_CONTAINER_SHIP);

        Stage stage = (Stage) dataSend.getScene().getWindow();
        stage.close();
    }

    private void executeSetParameters(TextField textField, NameParameters nameParameter, int defaultParameter) {
        try {
            int valueInText = Integer.parseInt(textField.getText().trim());
            GenerateParameters.generationParameters.put(nameParameter, valueInText);
        }
        catch (Exception ignored) {
            GenerateParameters.generationParameters.put(nameParameter, defaultParameter);
        }
    }

    private void filterField(TextField textField) {
        textField
                .textProperty()
                .addListener((observable, oldValue, newValue) -> {
                    if (!newValue.matches("\\d*")) {
                        textField.setText(newValue.replaceAll("[^\\d]", ""));
                    }
                });
    }

    public void pressKeyLoose(KeyEvent keyEvent) {
        filterField(performanceLoose);
    }

    public void typedKeyLoose(KeyEvent keyEvent) {
        filterField(performanceLoose);
    }

    public void pressedKeyMinCountsOfNodes(KeyEvent keyEvent) {
        filterField(minCountOfNodes);
    }

    public void pressedKeyMaxCountsOfNodes(KeyEvent keyEvent) {
        filterField(maxCountOfNodes);
    }

    public void pressedKeyMinLengthNameShip(KeyEvent keyEvent) {
        filterField(minLengthNameShip);
    }

    public void pressedKeyMaxLengthNameShip(KeyEvent keyEvent) {
        filterField(maxLengthNameShip);
    }

    public void typedKeyMinCountsOfNodes(KeyEvent keyEvent) {
        filterField(minCountOfNodes);
    }

    public void typedKeyMaxCountsOfNodes(KeyEvent keyEvent) {
        filterField(maxCountOfNodes);
    }

    public void typedKeyMinLengthNameShip(KeyEvent keyEvent) {
        filterField(minLengthNameShip);
    }

    public void typedKeyMaxLengthNameShip(KeyEvent keyEvent) {
        filterField(maxLengthNameShip);
    }

    public void typedKeyPerformanceLiquid(KeyEvent keyEvent) {
        filterField(performanceLiquid);
    }

    public void typedKeyPerformanceContainer(KeyEvent keyEvent) {
        filterField(performanceContainer);
    }

    public void pressedKeyPerformanceLiquid(KeyEvent keyEvent) {
        filterField(performanceLiquid);
    }

    public void pressedKeyPerformanceContainer(KeyEvent keyEvent) {
        filterField(performanceContainer);
    }

    public void typedKeyMinLooseShip(KeyEvent keyEvent) {
        filterField(minLooseShip);
    }

    public void typedKeyMinContainerShip(KeyEvent keyEvent) {
        filterField(minContainerShip);
    }

    public void pressedKeyMinLiquidShip(KeyEvent keyEvent) {
        filterField(minLiquidShip);
    }

    public void typedKeyMinLiquidShip(KeyEvent keyEvent) {
        filterField(minLiquidShip);
    }

    public void pressedKeyMinContainerShip(KeyEvent keyEvent) {
        filterField(minContainerShip);
    }

    public void typedKeyMaxLooseShip(KeyEvent keyEvent) {
        filterField(maxLooseShip);
    }

    public void pressedKeyMaxLooseShip(KeyEvent keyEvent) {
        filterField(maxLooseShip);
    }

    public void pressedKeyMaxLiquidShip(KeyEvent keyEvent) {
        filterField(maxLiquidShip);
    }

    public void typedKeyMaxLiquidShip(KeyEvent keyEvent) {
        filterField(maxLiquidShip);
    }

    public void pressedKeyMinLooseShip(KeyEvent keyEvent) {
        filterField(minLooseShip);
    }

    public void pressedKeyMaxContainerShip(KeyEvent keyEvent) {
        filterField(maxContainerShip);
    }

    public void typedKeyMaxContainerShip(KeyEvent keyEvent) {
        filterField(maxContainerShip);
    }
}
