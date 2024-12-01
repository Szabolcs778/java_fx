package org.example.java_fx_szoftverleltar;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class GombController {

    @FXML
    private Label label1;

    @FXML
    private Label label2;

    private ScheduledExecutorService executorService;

    @FXML
    public void startTask() {
        if (executorService != null && !executorService.isShutdown()) {
            return; // Ne indítsuk újra, ha már fut
        }

        executorService = Executors.newScheduledThreadPool(2);

        // Frissítjük az első Label-t 1 másodpercenként
        executorService.scheduleAtFixedRate(() -> Platform.runLater(() -> label1.setText("1 másodperc: " + System.currentTimeMillis())),
                0, 1, TimeUnit.SECONDS);

        // Frissítjük a második Label-t 2 másodpercenként
        executorService.scheduleAtFixedRate(() -> Platform.runLater(() -> label2.setText("2 másodperc: " + System.currentTimeMillis())),
                0, 2, TimeUnit.SECONDS);
    }

    @FXML
    public void stopTask() {
        if (executorService != null) {
            executorService.shutdown();
        }
    }
}
