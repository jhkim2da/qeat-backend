package com.qeat.controller;

import com.qeat.repository.BoothTableRepository;
import com.qeat.repository.MenuRepository;
import com.qeat.service.OrderService;
import com.qeat.domain.BoothTable;
import com.qeat.dto.menu.MenuResponse;
import com.qeat.dto.order.PublicOrderCreateRequest;
import com.qeat.dto.table.PublicTableMenuResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class QrController {

    private final BoothTableRepository boothTableRepository;
    private final MenuRepository menuRepository;
    private final OrderService orderService;

    public QrController(BoothTableRepository boothTableRepository,
                        MenuRepository menuRepository,
                        OrderService orderService) {
        this.boothTableRepository = boothTableRepository;
        this.menuRepository = menuRepository;
        this.orderService = orderService;
    }

    @GetMapping("/qr/{tableToken}")
    public String menuPage(@PathVariable String tableToken, Model model) {
        BoothTable table = boothTableRepository.findByTableTokenAndActiveTrue(tableToken)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 테이블 QR입니다."));

        List<MenuResponse> menus = menuRepository.findByBooth_Id(table.getBooth().getId())
                .stream()
                .map(MenuResponse::from)
                .toList();

        model.addAttribute("tableToken", tableToken);
        model.addAttribute("booth", table.getBooth());
        model.addAttribute("table", table);
        model.addAttribute("menus", menus);

        return "qr-menu";
    }

    @ResponseBody
    @GetMapping("/api/public/tables/{tableToken}")
    public PublicTableMenuResponse getTableMenu(@PathVariable String tableToken) {
        BoothTable table = boothTableRepository.findByTableTokenAndActiveTrue(tableToken)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 테이블 QR입니다."));

        List<MenuResponse> menus = menuRepository.findByBooth_Id(table.getBooth().getId())
                .stream()
                .map(MenuResponse::from)
                .toList();

        return PublicTableMenuResponse.from(table, menus);
    }

    @PostMapping("/api/public/tables/{tableToken}/orders")
    public ResponseEntity<Long> createOrder(@PathVariable String tableToken,
                                            @Valid @RequestBody PublicOrderCreateRequest request) {
        Long orderId = orderService.createOrderByTableToken(tableToken, request);
        return ResponseEntity.ok(orderId);
    }
}
