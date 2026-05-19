package com.partfinder.aggregator.domain.policy;

import com.partfinder.aggregator.domain.exception.OrderRuleViolationException;
import com.partfinder.aggregator.domain.model.Money;
import com.partfinder.aggregator.domain.model.Workshop;

/**
 * Implementa la Regla R2 del caso 7:
 *   "No se puede procesar un pedido si el taller tiene un cupo de credito
 *    excedido en el microservicio financiero."
 *
 * El "MS financiero" lo materializamos como proyeccion local: la entidad
 * Workshop trae creditLimit y creditUsed. Si el nuevo pedido haria que
 * creditUsed supere creditLimit, R2 lo bloquea.
 */
public class OrderCreditPolicy implements OrderRulePolicy {

    public static final String CODE = "R2_CREDIT_EXCEEDED";

    @Override
    public void check(Workshop workshop, Money orderAmount) {
        if (workshop.hasExceededCredit()) {
            throw new OrderRuleViolationException(CODE,
                    "Taller " + workshop.id() + " ya tiene su cupo de credito excedido " +
                            "(usado=" + workshop.creditUsed() + " / limite=" + workshop.creditLimit() + "). R2 bloquea.");
        }
        Money wouldBeUsed = workshop.creditUsed().plus(orderAmount);
        if (wouldBeUsed.greaterThan(workshop.creditLimit())) {
            throw new OrderRuleViolationException(CODE,
                    "Pedido por " + orderAmount + " excederia el cupo del taller " + workshop.id() +
                            " (usado=" + workshop.creditUsed() + ", limite=" + workshop.creditLimit() + "). R2 bloquea.");
        }
    }

    @Override
    public String ruleCode() { return CODE; }
}
