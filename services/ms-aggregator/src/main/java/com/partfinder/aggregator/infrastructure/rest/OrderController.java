package com.partfinder.aggregator.infrastructure.rest;

import com.partfinder.aggregator.application.usecase.CreateOrderUseCase;
import com.partfinder.aggregator.application.usecase.GetOrderUseCase;
import com.partfinder.aggregator.application.usecase.ListSupplierOrdersUseCase;
import com.partfinder.aggregator.application.usecase.ListWorkshopOrdersUseCase;
import com.partfinder.aggregator.application.usecase.UpdateOrderStatusUseCase;
import com.partfinder.aggregator.domain.model.Money;
import com.partfinder.aggregator.domain.model.PartId;
import com.partfinder.aggregator.domain.model.Quantity;
import com.partfinder.aggregator.domain.model.SupplierId;
import com.partfinder.aggregator.domain.model.WorkshopId;
import com.partfinder.aggregator.infrastructure.rest.dto.CreateOrderRequest;
import com.partfinder.aggregator.infrastructure.rest.dto.OrderResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/orders")
@Tag(name = "Orders", description = "Pedidos de un taller a un proveedor. R2 (cupo de credito) se valida aqui.")
public class OrderController {

    private final CreateOrderUseCase createOrder;
    private final GetOrderUseCase getOrder;
    private final ListWorkshopOrdersUseCase listOrders;
    private final ListSupplierOrdersUseCase listSupplierOrders;
    private final UpdateOrderStatusUseCase updateStatus;

    public OrderController(CreateOrderUseCase createOrder, GetOrderUseCase getOrder,
                           ListWorkshopOrdersUseCase listOrders, ListSupplierOrdersUseCase listSupplierOrders,
                           UpdateOrderStatusUseCase updateStatus) {
        this.createOrder = createOrder; this.getOrder = getOrder;
        this.listOrders = listOrders; this.listSupplierOrders = listSupplierOrders;
        this.updateStatus = updateStatus;
    }

    @Operation(summary = "Crea un pedido. Devuelve 422 si R2 bloquea por cupo de credito excedido.")
    @PostMapping
    @Transactional
    public ResponseEntity<OrderResponse> create(@Valid @RequestBody CreateOrderRequest req) {
        var order = createOrder.execute(
                new WorkshopId(req.workshopId()),
                new PartId(req.partId()),
                new SupplierId(req.supplierId()),
                Quantity.of(req.quantity()),
                new Money(req.unitPrice(), req.currency())
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(OrderResponse.from(order));
    }

    @Operation(summary = "Consulta un pedido por id.")
    @GetMapping("/{id}")
    public OrderResponse getOne(@PathVariable("id") String id) {
        return OrderResponse.from(getOrder.execute(id));
    }

    @Operation(summary = "Lista pedidos por taller (workshopId) o por proveedor (supplierId).")
    @GetMapping
    public List<OrderResponse> list(@RequestParam(value = "workshopId", required = false) String workshopId,
                                    @RequestParam(value = "supplierId", required = false) String supplierId) {
        if (supplierId != null && !supplierId.isBlank()) {
            return listSupplierOrders.execute(new SupplierId(supplierId)).stream().map(OrderResponse::from).toList();
        }
        return listOrders.execute(new WorkshopId(workshopId)).stream().map(OrderResponse::from).toList();
    }

    @Operation(summary = "Marca un pedido como entregado (CREATED -> FULFILLED).")
    @PostMapping("/{id}/fulfill")
    @Transactional
    public OrderResponse fulfill(@PathVariable("id") String id) {
        return OrderResponse.from(updateStatus.fulfill(id));
    }

    @Operation(summary = "Cancela un pedido y devuelve el cupo (CREATED -> CANCELLED).")
    @PostMapping("/{id}/cancel")
    @Transactional
    public OrderResponse cancel(@PathVariable("id") String id) {
        return OrderResponse.from(updateStatus.cancel(id));
    }
}
