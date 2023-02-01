package io.axoniq.workshop.payment.api;

import io.axoniq.workshop.payment.query.PaymentStatus;
import io.axoniq.workshop.shared.ConfirmPaymentCommand;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/payment")
public class PaymentController {

    private final QueryGateway queryGateway;
    private final CommandGateway commandGateway;

    public PaymentController(QueryGateway queryGateway, CommandGateway commandGateway) {
        this.queryGateway = queryGateway;
        this.commandGateway = commandGateway;
    }

    // ...

    @PostMapping("/{paymentId}/confirm")
    public CompletableFuture<Void> confirmPayment(@PathVariable("paymentId")String paymentId) {
        return commandGateway.send(new ConfirmPaymentCommand(paymentId));
    }

    @GetMapping("/")
    public CompletableFuture<List<PaymentStatus>> getPayments() {
        return queryGateway.query("getPayments", null, ResponseTypes.multipleInstancesOf(PaymentStatus.class));
    }
}
