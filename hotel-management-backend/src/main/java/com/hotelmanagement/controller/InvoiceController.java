package com.hotelmanagement.controller;

import com.hotelmanagement.dto.ApiResponse;
import com.hotelmanagement.dto.InvoiceDTO;
import com.hotelmanagement.entity.PaymentMode;
import com.hotelmanagement.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/invoices")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:3000"})
public class InvoiceController {

    private final InvoiceService invoiceService;

    @PostMapping("/generate/{reservationId}")
    @PreAuthorize("hasRole('RECEPTIONIST')")
    public ResponseEntity<ApiResponse<InvoiceDTO>> generateInvoice(@PathVariable Long reservationId) {
        InvoiceDTO invoice = invoiceService.generateInvoice(reservationId);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(true, "Invoice generated", invoice));
    }

    @PostMapping("/{id}/pay")
    @PreAuthorize("hasRole('RECEPTIONIST')")
    public ResponseEntity<ApiResponse<InvoiceDTO>> recordPayment(
            @PathVariable Long id,
            @RequestParam Double amount,
            @RequestParam(required = false) PaymentMode mode) {
        InvoiceDTO updated = invoiceService.recordPayment(id, amount, mode);
        return ResponseEntity.ok(new ApiResponse<>(true, "Payment recorded", updated));
    }

    @PostMapping("/{id}/refund")
    @PreAuthorize("hasRole('ADMIN') or hasRole('RECEPTIONIST')")
    public ResponseEntity<ApiResponse<InvoiceDTO>> processRefund(@PathVariable Long id) {
        InvoiceDTO refunded = invoiceService.processRefund(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Refund processed", refunded));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('RECEPTIONIST') or hasRole('GUEST') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<InvoiceDTO>> getInvoice(@PathVariable Long id) {
        InvoiceDTO invoice = invoiceService.getInvoiceById(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Invoice retrieved", invoice));
    }

    @GetMapping("/reservation/{reservationId}")
    @PreAuthorize("hasRole('RECEPTIONIST') or hasRole('GUEST')")
    public ResponseEntity<ApiResponse<InvoiceDTO>> getInvoiceByReservation(@PathVariable Long reservationId) {
        InvoiceDTO invoice = invoiceService.getInvoiceByReservation(reservationId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Invoice retrieved", invoice));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('RECEPTIONIST')")
    public ResponseEntity<ApiResponse<Page<InvoiceDTO>>> getAllInvoices(Pageable pageable) {
        Page<InvoiceDTO> invoices = invoiceService.getAllInvoices(pageable);
        return ResponseEntity.ok(new ApiResponse<>(true, "Invoices retrieved", invoices));
    }

    @GetMapping("/{id}/export/pdf")
    @PreAuthorize("hasRole('RECEPTIONIST') or hasRole('GUEST') or hasRole('ADMIN')")
    public ResponseEntity<byte[]> exportPdf(@PathVariable Long id) {
        byte[] pdf = invoiceService.generatePdf(id);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "invoice_" + id + ".pdf");
        return new ResponseEntity<>(pdf, headers, HttpStatus.OK);
    }
}
