package io.axoniq.workshop.payment.command;

import io.axoniq.workshop.shared.ConfirmPaymentCommand;
import io.axoniq.workshop.shared.PaymentConfirmedEvent;
import io.axoniq.workshop.shared.PaymentPreparedEvent;
import io.axoniq.workshop.shared.PreparePaymentCommand;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.spring.stereotype.Aggregate;

import static org.axonframework.modelling.command.AggregateLifecycle.apply;

@Aggregate
public class Payment {

    @AggregateIdentifier
    private String paymentId;

    private String userId;

    private boolean completed;

    public Payment() {
    }

    @CommandHandler
    public Payment(PreparePaymentCommand cmd) {
        apply(new PaymentPreparedEvent(cmd.getPaymentId(), cmd.getUserId()));
    }

    @CommandHandler
    public void handle(ConfirmPaymentCommand cmd) {
        apply(new PaymentConfirmedEvent(cmd.getPaymentId()));
    }

    @EventSourcingHandler
    public void handle(PaymentPreparedEvent evt) {
        paymentId = evt.getPaymentId();
        userId = evt.getUserId();
    }

    @EventSourcingHandler
    public void handle(PaymentConfirmedEvent evt) {
        completed = true;
    }
}
