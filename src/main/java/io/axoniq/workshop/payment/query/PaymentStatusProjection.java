package io.axoniq.workshop.payment.query;

import org.axonframework.eventhandling.EventHandler;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Component;
import io.axoniq.workshop.shared.PaymentConfirmedEvent;
import io.axoniq.workshop.shared.PaymentPreparedEvent;

import static io.axoniq.workshop.payment.query.PaymentStatus.Status.APPROVED;

@Component
public class PaymentStatusProjection {

    private final PaymentStatusRepository paymentStatusRepository;

    public PaymentStatusProjection(PaymentStatusRepository paymentStatusRepository) {
        this.paymentStatusRepository = paymentStatusRepository;
    }

    @EventHandler
    public void handle(PaymentPreparedEvent event) {
        paymentStatusRepository.save(new PaymentStatus(event.getPaymentId(), event.getUserId()));
    }

    @EventHandler
    public void handle(PaymentConfirmedEvent event) {
        paymentStatusRepository.findById(event.getPaymentId()).ifPresent(s -> s.setStatus(APPROVED));
    }

    // ...

    @QueryHandler(queryName = "getPayments")
    public Iterable<PaymentStatus> findAll() {
        return paymentStatusRepository.findAll();
    }
}
