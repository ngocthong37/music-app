package com.quocluan.kdmaylanh.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.quocluan.kdmaylanh.entity.*;
import com.quocluan.kdmaylanh.model.InvoiceModel;
import com.quocluan.kdmaylanh.model.MonthlyRevenue;
import com.quocluan.kdmaylanh.repository.InvoiceRepository;
import com.quocluan.kdmaylanh.repository.OrderRepository;
import com.quocluan.kdmaylanh.repository.OrderStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class InvoiceService {
    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderStatusRepository orderStatusRepository;

    public ResponseEntity<Object> payOrder(String json) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode jsonNode = objectMapper.readTree(json);
            Integer orderId = jsonNode.get("orderId").asInt();
            Date transactionDate = new Date();

            BigDecimal amount = jsonNode.get("amount").decimalValue();
            String note = jsonNode.get("note").asText();
            Invoice invoice = new Invoice();
            invoice.setNote(note);
            invoice.setTransactionDate(transactionDate);
            invoice.setAmount(amount);
            Optional<Orders> orderOptional = orderRepository.findById(orderId);
            Optional<OrderStatus> orderStatusOptional = orderStatusRepository.findOrderStatusByOrderId(orderId);
            if (orderOptional.isPresent()) {
                invoice.setOrders(orderOptional.get());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject("ERROR", "order not found with ID: " + orderId, null));
            }
            if (orderStatusOptional.isPresent()) {
                OrderStatus orderStatus = orderStatusOptional.get();
                List<String> validStatus = Arrays.asList("CANCELED", "COMPLETED", "REJECTED", "DELIVERED", "PAID", "PENDING");
                if (validStatus.contains(orderStatus.getStatus().getStatusID())) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject("ERROR", "Cannot pay this order", ""));
                }
                Status status1 = new Status();
                status1.setStatusID("PAID");
                orderStatus.setNote("Order paid successfully");
                orderStatus.setStatus(status1);
                orderStatusRepository.save(orderStatus);
            }
            else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject("ERROR", "order status not found with ID: " + orderId, null));
            }
            Invoice savedInvoice = invoiceRepository.save(invoice);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("OK", "paid order successfully", savedInvoice.getInvoiceID()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject("ERROR", "An error occurred", e.getMessage()));
        }
    }

    public ResponseEntity<ResponseObject> findAllInvoice() {
        List<Invoice> invoiceList = invoiceRepository.findAll();
        if (invoiceList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("Not found", "Not found", ""));
        }
        List<InvoiceModel> invoiceModelList = new ArrayList<>();
        for (Invoice invoice : invoiceList) {
            InvoiceModel invoiceModel = convertToModel(invoice);
            invoiceModelList.add(invoiceModel);
        }
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("OK", "Successfully", invoiceModelList));
    }

    public ResponseEntity<ResponseObject> findRevenueByDate(Integer day, Integer month, Integer year) {
        BigDecimal totalRevenue = BigDecimal.ZERO;
        totalRevenue =  invoiceRepository.findRevenueByDate(day, month, year);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("OK", "Successfully", totalRevenue));
    }

    public ResponseEntity<ResponseObject> findRevenueBetweenDates(@DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date startDate,
                                                          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date endDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(endDate);
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        endDate = calendar.getTime();
        List<Object[]> results = invoiceRepository.findMonthlyRevenueBetweenDates(startDate, endDate);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("OK", "Successfully", results.stream()
                .map(result -> new MonthlyRevenue((Integer) result[0], (Integer) result[1], (BigDecimal) result[2]))
                .collect(Collectors.toList())));
    }
    private InvoiceModel convertToModel(Invoice invoice) {
        InvoiceModel invoiceModel = new InvoiceModel();
        invoiceModel.setInvoiceId(invoice.getInvoiceID());
        invoiceModel.setAmount(invoice.getAmount());
        invoiceModel.setTransactionDate(invoice.getTransactionDate());
        invoiceModel.setOrderId(invoice.getOrders().getOrderID());
        invoiceModel.setNote(invoice.getNote());
        return invoiceModel;
    }



}
