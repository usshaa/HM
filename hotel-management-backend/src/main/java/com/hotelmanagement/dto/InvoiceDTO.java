package com.hotelmanagement.dto;

import com.hotelmanagement.entity.PaymentMode;
import com.hotelmanagement.entity.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceDTO {
    private Long id;
    private Long reservationId;
    private Double roomCharges;
    private Double extraCharges;
    private Double discountAmount;
    private Double taxAmount;
    private Double totalAmount;
    private Double paidAmount;
    private Double balanceDue;
    private PaymentMode paymentMode;
    private PaymentStatus paymentStatus;
    private String notes;
    private Long generatedAt;
}
