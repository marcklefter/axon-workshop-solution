package io.axoniq.workshop.shared

import org.axonframework.modelling.command.TargetAggregateIdentifier

// ...
// Bounded Context: Rental.
data class RegisterBikeCommand(@TargetAggregateIdentifier val bikeId: String)
data class RequestBikeCommand(@TargetAggregateIdentifier val bikeId: String, val renter: String)
data class ApproveBikeRequestCommand(@TargetAggregateIdentifier val bikeId: String)
data class RejectBikeRequestCommand(@TargetAggregateIdentifier val bikeId: String)
data class ReturnBikeCommand(@TargetAggregateIdentifier val bikeId: String)

data class BikeRegisteredEvent(val bikeId: String)
data class BikeRequestedEvent(val bikeId: String, val renter: String)
data class BikeInUseEvent(val bikeId: String, val renter: String)
data class BikeRequestRejectedEvent(val bikeId: String)
data class BikeReturnedEvent(val bikeId: String)

// ...
// Bounded Context: Payment.
data class PreparePaymentCommand(@TargetAggregateIdentifier val paymentId: String, val userId: String)
data class ConfirmPaymentCommand(@TargetAggregateIdentifier val paymentId: String)

data class PaymentPreparedEvent(val paymentId: String, val userId: String)
data class PaymentConfirmedEvent(val paymentId: String)