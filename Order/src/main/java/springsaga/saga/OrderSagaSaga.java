package springsaga.saga;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import springsaga.config.kafka.KafkaProcessor;
import springsaga.domain.*;
import springsaga.external.*;

@Service
public class OrderSagaSaga {

    @Autowired
    DeliveryService deliveryService;

    @Autowired
    StorageService storageService;

    @Autowired
    OrderService orderService;

    @Autowired
    FactoryService factoryService;

    @Autowired
    OverseasDeliveryService overseasDeliveryService;

    @StreamListener(
        value = KafkaProcessor.INPUT,
        condition = "headers['type']=='OrderPlaced'"
    )
    public void wheneverOrderPlaced_OrderSaga(
        @Payload OrderPlaced orderPlaced,
        @Header(KafkaHeaders.ACKNOWLEDGMENT) Acknowledgment acknowledgment,
        @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) byte[] messageKey
    ) {
        OrderPlaced event = orderPlaced;
        System.out.println(
            "\n\n##### listener OrderSaga : " + orderPlaced + "\n\n"
        );

        try {
            Delivery delivery = new Delivery();
            /* Logic */
            delivery.setOrderId(event.getId());

            deliveryService.startDelivery(delivery);
        } catch (Exception e) {
            OrderCancelCommand orderCancelCommand = new OrderCancelCommand();
            /* Logic */
            orderCancelCommand.setId(event.getId());

            orderService.orderCancel(event.getId(), orderCancelCommand);
        }

        // Manual Offset Commit //
        acknowledgment.acknowledge();
    }

    @StreamListener(
        value = KafkaProcessor.INPUT,
        condition = "headers['type']=='DeliveryStarted'"
    )
    public void wheneverDeliveryStarted_OrderSaga(
        @Payload DeliveryStarted deliveryStarted,
        @Header(KafkaHeaders.ACKNOWLEDGMENT) Acknowledgment acknowledgment,
        @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) byte[] messageKey
    ) {
        DeliveryStarted event = deliveryStarted;
        System.out.println(
            "\n\n##### listener OrderSaga : " + deliveryStarted + "\n\n"
        );

        try {
            Storage storage = new Storage();
            /* Logic */
            storage.set(event.getOrderId());

            storageService.increaseStock(storage);
        } catch (Exception e) {
            CancelDeliveryCommand cancelDeliveryCommand = new CancelDeliveryCommand();
            /* Logic */
            cancelDeliveryCommand.setOrderId(event.getOrderId());

            deliveryService.cancelDelivery(
                event.getOrderId(),
                cancelDeliveryCommand
            );
        }

        // Manual Offset Commit //
        acknowledgment.acknowledge();
    }

    @StreamListener(
        value = KafkaProcessor.INPUT,
        condition = "headers['type']=='StockIncreased'"
    )
    public void wheneverStockIncreased_OrderSaga(
        @Payload StockIncreased stockIncreased,
        @Header(KafkaHeaders.ACKNOWLEDGMENT) Acknowledgment acknowledgment,
        @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) byte[] messageKey
    ) {
        StockIncreased event = stockIncreased;
        System.out.println(
            "\n\n##### listener OrderSaga : " + stockIncreased + "\n\n"
        );

        UpdateStatusCommand updateStatusCommand = new UpdateStatusCommand();
        /* Logic */
        updateStatusCommand.setId(event.get());

        orderService.updateStatus(event.get(), updateStatusCommand);

        // Manual Offset Commit //
        acknowledgment.acknowledge();
    }

    @StreamListener(
        value = KafkaProcessor.INPUT,
        condition = "headers['type']=='OrderCompleted'"
    )
    public void wheneverOrderCompleted_OrderSaga(
        @Payload OrderCompleted orderCompleted,
        @Header(KafkaHeaders.ACKNOWLEDGMENT) Acknowledgment acknowledgment,
        @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) byte[] messageKey
    ) {
        OrderCompleted event = orderCompleted;
        System.out.println(
            "\n\n##### listener OrderSaga : " + orderCompleted + "\n\n"
        );

        // Manual Offset Commit //
        acknowledgment.acknowledge();
    }

    @StreamListener(
        value = KafkaProcessor.INPUT,
        condition = "headers['type']=='ProductManufactured'"
    )
    public void wheneverProductManufactured_OrderSaga(
        @Payload ProductManufactured productManufactured,
        @Header(KafkaHeaders.ACKNOWLEDGMENT) Acknowledgment acknowledgment,
        @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) byte[] messageKey
    ) {
        ProductManufactured event = productManufactured;
        System.out.println(
            "\n\n##### listener OrderSaga : " + productManufactured + "\n\n"
        );

        // Manual Offset Commit //
        acknowledgment.acknowledge();
    }

    @StreamListener(
        value = KafkaProcessor.INPUT,
        condition = "headers['type']=='OverseasDeliveryStarted'"
    )
    public void wheneverOverseasDeliveryStarted_OrderSaga(
        @Payload OverseasDeliveryStarted overseasDeliveryStarted,
        @Header(KafkaHeaders.ACKNOWLEDGMENT) Acknowledgment acknowledgment,
        @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) byte[] messageKey
    ) {
        OverseasDeliveryStarted event = overseasDeliveryStarted;
        System.out.println(
            "\n\n##### listener OrderSaga : " + overseasDeliveryStarted + "\n\n"
        );

        /* Logic */

        // Manual Offset Commit //
        acknowledgment.acknowledge();
    }
}
