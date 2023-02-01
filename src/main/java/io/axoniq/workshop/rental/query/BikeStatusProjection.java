package io.axoniq.workshop.rental.query;

import io.axoniq.workshop.shared.BikeInUseEvent;
import io.axoniq.workshop.shared.BikeRegisteredEvent;
import io.axoniq.workshop.shared.BikeReturnedEvent;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BikeStatusProjection {
    private final BikeStatusRepository bikeStatusRepository;

    public BikeStatusProjection(BikeStatusRepository bikeStatusRepository) {
        this.bikeStatusRepository = bikeStatusRepository;
    }


    @EventHandler
    public void on(BikeRegisteredEvent event) {
        var bikeStatus = new BikeStatus(event.getBikeId());
        bikeStatusRepository.save(bikeStatus);
    }

    @EventHandler
    public void on(BikeInUseEvent event) {
        bikeStatusRepository.findById(event.getBikeId())
                .map(bs -> {
                    bs.requestedBy(event.getRenter());
                    bs.rented();
                    return bs;
                });
    }

    @EventHandler
    public void on(BikeReturnedEvent event) {
        bikeStatusRepository.findById(event.getBikeId())
                .map(bs -> {
                    bs.returned();
                    return bs;
                });

    }

    @QueryHandler(queryName = "findAll")
    public List<BikeStatus> findAll() {
        return bikeStatusRepository.findAll();
    }

    @QueryHandler(queryName = "findOne")
    public BikeStatus findOne(String bikeId) {
        return bikeStatusRepository.findById(bikeId).orElse(null);
    }
}
