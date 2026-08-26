package com.qeat.controller;

import com.qeat.domain.BoothTable;
import com.qeat.dto.order.PublicOrderCreateRequest;
import com.qeat.dto.table.PublicTableMenuResponse;
import com.qeat.service.OrderService;
import com.qeat.service.PublicTableService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class QrController {

    private final PublicTableService publicTableService;
    private final OrderService orderService;

    public QrController(PublicTableService publicTableService, OrderService orderService) {
        this.publicTableService = publicTableService;
        this.orderService = orderService;
    }

    @GetMapping("/qr/{tableToken}")
    public String menuPage(@PathVariable String tableToken, Model model) {
        BoothTable table = publicTableService.findActiveTable(tableToken);
        model.addAttribute("tableToken", tableToken);
        model.addAttribute("booth", table.getBooth());
        model.addAttribute("table", table);
        model.addAttribute("menus", publicTableService.getPublicMenus(table.getBooth().getId()));
        return "qr-menu";
    }

    @ResponseBody
    @GetMapping("/api/public/tables/{tableToken}")
    public PublicTableMenuResponse getTableMenu(@PathVariable String tableToken) {
        return publicTableService.getTableMenu(tableToken);
    }

    @ResponseBody
    @PostMapping("/api/public/tables/{tableToken}/orders")
    public ResponseEntity<Long> createOrder(
            @PathVariable String tableToken,
            @Valid @RequestBody PublicOrderCreateRequest request
    ) {
        return ResponseEntity.ok(orderService.createOrderByTableToken(tableToken, request));
    }
}
