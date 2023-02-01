package io.axoniq.workshop;

import io.axoniq.workshop.rental.command.Bike;
import io.axoniq.workshop.shared.*;
import org.axonframework.test.aggregate.AggregateTestFixture;
import org.axonframework.test.aggregate.FixtureConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

class BikeTests {

	private static final String bikeId = UUID.randomUUID().toString();
	private static final String renter = "Marc";

	private FixtureConfiguration<Bike> fixture;

	@BeforeEach
	void setup() {
		fixture = new AggregateTestFixture<>(Bike.class);
	}

	@Test
	void shouldRegisterBike() {
		fixture.givenNoPriorActivity()
				.when(new RegisterBikeCommand(bikeId))
				.expectEvents(new BikeRegisteredEvent(bikeId));
	}

	@Test
	void shouldRequestBike() {
		fixture.given(new BikeRegisteredEvent(bikeId))
				.when(new RequestBikeCommand(bikeId, renter))
				.expectEvents(new BikeRequestedEvent(bikeId, renter));
	}

	@Test
	void shouldNotRequestRequestedBike() {
		fixture.given(new BikeRegisteredEvent(bikeId), new BikeRequestedEvent(bikeId, renter))
				.when(new RequestBikeCommand(bikeId, renter))
				.expectException(IllegalStateException.class);
	}

	@Test
	void shouldApproveBike() {
		fixture.given(new BikeRegisteredEvent(bikeId), new BikeRequestedEvent(bikeId, renter))
				.when(new ApproveBikeRequestCommand(bikeId))
				.expectEvents(new BikeInUseEvent(bikeId, renter));
	}

	@Test
	void shouldNotRequestUnavailableBike() {
		fixture.given(
						new BikeRegisteredEvent(bikeId),
						new BikeRequestedEvent(bikeId, renter),
						new BikeInUseEvent(bikeId, renter)
				)
				.when(new RequestBikeCommand(bikeId, renter))
				.expectException(IllegalStateException.class);
	}

	@Test
	void shouldReturnBike() {
		fixture.given(
						new BikeRegisteredEvent(bikeId),
						new BikeRequestedEvent(bikeId, renter),
						new BikeInUseEvent(bikeId, renter)
				)
				.when(new ReturnBikeCommand(bikeId))
				.expectEvents(new BikeReturnedEvent(bikeId));
	}

	@Test
	void shouldNotReturnAvailableBike() {
		fixture.given(
						new BikeRegisteredEvent(bikeId),
						new BikeRequestedEvent(bikeId, renter),
						new BikeInUseEvent(bikeId, renter),
						new BikeReturnedEvent(bikeId)
				)
				.when(new ReturnBikeCommand(bikeId))
				.expectException(IllegalStateException.class);
	}
}
