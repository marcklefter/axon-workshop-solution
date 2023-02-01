package io.axoniq.workshop.rental.process;

import io.axoniq.workshop.shared.*;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.deadline.DeadlineManager;
import org.axonframework.deadline.annotation.DeadlineHandler;
import org.axonframework.modelling.saga.EndSaga;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.SagaLifecycle;
import org.axonframework.modelling.saga.StartSaga;
import org.axonframework.spring.stereotype.Saga;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Saga
public class PaymentProcess {

    @Autowired
    private transient CommandGateway commandGateway;

    @Autowired
    private transient DeadlineManager deadlineManager;

    private String bikeId;

    private String deadlineId;

    @StartSaga
    @SagaEventHandler(associationProperty = "bikeId")
    public void on(BikeRequestedEvent event) {
        bikeId = event.getBikeId();

        var paymentId = UUID.randomUUID().toString();

        // associate (future) events for confirming or rejecting payment with the generated
        // payment identifier.
        SagaLifecycle.associateWith("paymentId", paymentId);

        deadlineId = deadlineManager.schedule(Duration.of(30, ChronoUnit.SECONDS), "paymentDeadline");

        commandGateway.send(new PreparePaymentCommand(paymentId, event.getRenter()));
    }

    @EndSaga
    @SagaEventHandler(associationProperty = "paymentId")
    public void on(PaymentConfirmedEvent event) {
        deadlineManager.cancelSchedule(deadlineId, "paymentDeadline");
        commandGateway.send(new ApproveBikeRequestCommand(bikeId));
    }

    @DeadlineHandler(deadlineName = "paymentDeadline")
    public void handle() {
        commandGateway.send(new RejectBikeRequestCommand(bikeId));
        SagaLifecycle.end();
    }
}
