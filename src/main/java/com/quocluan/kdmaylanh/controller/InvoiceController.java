package com.quocluan.kdmaylanh.controller;

import com.quocluan.kdmaylanh.entity.ResponseObject;
import com.quocluan.kdmaylanh.service.InvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping(path = "/api/v1/")
public class InvoiceController {

    @Autowired
    private InvoiceService invoiceService;

    @PostMapping("invoice/pay")
    public ResponseEntity<Object> payOrder(@RequestBody String json) {
        return invoiceService.payOrder(json);
    }


    @GetMapping("employee/invoice/findAll")
    public ResponseEntity<ResponseObject> findAll() {
        return invoiceService.findAllInvoice();
    }

    @GetMapping("employee/invoice/findRevenueByDate/{day}/{month}/{year}")
    public ResponseEntity<ResponseObject> findRevenueByDate(@PathVariable Integer day,
                                                            @PathVariable Integer month, @PathVariable Integer year) {
        return invoiceService.findRevenueByDate(day, month, year);
    }

    @GetMapping("employee/revenue/between")
    public ResponseEntity<ResponseObject> findRevenueBetweenDates(@RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date startDate,
                                                          @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date endDate) {
        return invoiceService.findRevenueBetweenDates(startDate, endDate);
    }

}
