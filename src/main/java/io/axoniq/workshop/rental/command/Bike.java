package io.axoniq.workshop.rental.command;

import io.axoniq.workshop.shared.*;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.spring.stereotype.Aggregate;

import static org.axonframework.modelling.command.AggregateLifecycle.apply;

@Aggregate
public class Bike {

    @AggregateIdentifier
    private String bikeId;

    private String renter;

    private boolean available;

    protected Bike() {}

    @CommandHandler
    public Bike(RegisterBikeCommand cmd) {
        apply(new BikeRegisteredEvent(cmd.getBikeId()));
    }

    @CommandHandler
    public void handle(RequestBikeCommand cmd) {
        if (renter != null) {
            throw new IllegalStateException("Bike has already been requested by another user");
        }

        if (!available) {
            throw new IllegalStateException("Bike already in use");
        }

        apply(new BikeRequestedEvent(cmd.getBikeId(), cmd.getRenter()));
    }

    @CommandHandler
    public void handle(ApproveBikeRequestCommand cmd) {
        apply(new BikeInUseEvent(bikeId, renter));
    }

    @CommandHandler
    public void handle(RejectBikeRequestCommand cmd) {
        apply(new BikeRequestRejectedEvent(bikeId));
    }

    @CommandHandler
    public void handle(ReturnBikeCommand cmd) {
        if (available) {
            throw new IllegalStateException("Bike already returned");
        }
        apply(new BikeReturnedEvent(cmd.getBikeId()));
    }

    @EventSourcingHandler
    public void handle(BikeRegisteredEvent evt) {
        bikeId = evt.getBikeId();
        available = true;
    }

    @EventSourcingHandler
    public void handle(BikeRequestedEvent evt) {
        renter = evt.getRenter();
    }

    @EventSourcingHandler
    public void handle(BikeInUseEvent evt) {
        available = false;
    }

    @EventSourcingHandler
    public void handle(BikeRequestRejectedEvent evt) {
        reset();
    }

    @EventSourcingHandler
    public void handle(BikeReturnedEvent evt) {
        reset();
    }

    private void reset() {
        renter = null;
        available = true;
    }
}

