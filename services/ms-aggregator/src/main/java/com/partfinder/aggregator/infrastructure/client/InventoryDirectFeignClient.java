package com.partfinder.aggregator.infrastructure.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "inventory", url = "${inventory-direct.base-url}")
public interface InventoryDirectFeignClient {

    @GetMapping("/inventory")
    List<InventoryItemDto> getInventory(@RequestParam("partId") String partId);
}
