package com.algaecare.view;

import javafx.animation.PauseTransition;
import javafx.util.Duration;
import java.util.logging.Logger;

public class Transition extends View {
    private static final Logger LOGGER = Logger.getLogger(Transition.class.getName());
    private final Duration displayDuration;
    private final Runnable onComplete;

    public Transition(String imagePath,
            Duration displayDuration, Runnable onComplete) {
        super(imagePath);
        this.displayDuration = displayDuration;
        this.onComplete = onComplete;
        startTransition();
    }

    private void startTransition() {
        PauseTransition pause = new PauseTransition(displayDuration);
        pause.setOnFinished(e -> {
            LOGGER.info("Transition complete after " + displayDuration.toSeconds() + " seconds");
            if (onComplete != null) {
                onComplete.run();
            }
        });
        pause.play();
    }
}