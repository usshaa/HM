package com.hotelmanagement.service;

import com.hotelmanagement.dto.InvoiceDTO;
import com.hotelmanagement.entity.*;
import com.hotelmanagement.exception.BadRequestException;
import com.hotelmanagement.exception.ResourceNotFoundException;
import com.hotelmanagement.repository.InvoiceRepository;
import com.hotelmanagement.repository.ReservationRepository;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
@RequiredArgsConstructor
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final ReservationRepository reservationRepository;
    private final RoomExtraService roomExtraService;

    @Transactional
    public InvoiceDTO generateInvoice(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found"));
        if (invoiceRepository.findByReservationId(reservationId).isPresent()) {
            throw new BadRequestException("Invoice already exists for this reservation");
        }

        Invoice invoice = new Invoice();
        invoice.setReservation(reservation);

        long nights = (reservation.getCheckOutDate() - reservation.getCheckInDate()) / (24 * 60 * 60 * 1000);
        if (nights < 1) nights = 1;
        double roomCharges = reservation.getRoomType().getBasePrice() * nights;
        invoice.setRoomCharges(roomCharges);

        double extraCharges = roomExtraService.getTotalExtrasForReservation(reservationId);
        invoice.setExtraCharges(extraCharges);

        double subtotal = roomCharges + extraCharges;
        double taxRate = subtotal < 5000 ? 0.12 : 0.18;
        invoice.setTaxAmount(subtotal * taxRate);
        invoice.setDiscountAmount(0.0);
        double totalAmount = subtotal + invoice.getTaxAmount() - invoice.getDiscountAmount();
        invoice.setTotalAmount(totalAmount);
        invoice.setPaidAmount(0.0);
        invoice.setBalanceDue(totalAmount);
        invoice.setPaymentStatus(PaymentStatus.UNPAID);
        invoice.setPaymentMode(PaymentMode.CASH);

        Invoice saved = invoiceRepository.save(invoice);
        log.info("Invoice generated: {} for reservation {}", saved.getId(), reservationId);
        return convertToDTO(saved);
    }

    @Transactional
    public InvoiceDTO recordPayment(Long invoiceId, Double amount, PaymentMode mode) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found"));
        if (amount <= 0) throw new BadRequestException("Payment amount must be greater than 0");

        double newPaid = invoice.getPaidAmount() + amount;
        if (newPaid > invoice.getTotalAmount()) throw new BadRequestException("Payment exceeds invoice total");

        invoice.setPaidAmount(newPaid);
        invoice.setBalanceDue(invoice.getTotalAmount() - newPaid);
        if (mode != null) invoice.setPaymentMode(mode);
        invoice.setPaymentStatus(newPaid >= invoice.getTotalAmount() ? PaymentStatus.PAID : PaymentStatus.PARTIAL);

        Invoice updated = invoiceRepository.save(invoice);
        log.info("Payment recorded: {} for invoice {}", amount, invoiceId);
        return convertToDTO(updated);
    }

    @Transactional
    public InvoiceDTO processRefund(Long invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found"));
        invoice.setPaymentStatus(PaymentStatus.REFUNDED);
        invoice.setBalanceDue(0.0);
        Invoice updated = invoiceRepository.save(invoice);
        log.info("Refund processed for invoice {}", invoiceId);
        return convertToDTO(updated);
    }

    public InvoiceDTO getInvoiceById(Long id) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found"));
        return convertToDTO(invoice);
    }

    public InvoiceDTO getInvoiceByReservation(Long reservationId) {
        Invoice invoice = invoiceRepository.findByReservationId(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found for reservation"));
        return convertToDTO(invoice);
    }

    public Page<InvoiceDTO> getAllInvoices(Pageable pageable) {
        return invoiceRepository.findAll(pageable).map(this::convertToDTO);
    }

    public byte[] generatePdf(Long invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found"));
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, baos);
            document.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20);
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
            Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 11);

            document.add(new Paragraph("HOTEL MANAGEMENT SYSTEM", titleFont));
            document.add(new Paragraph("INVOICE #" + invoice.getId(), headerFont));
            document.add(new Paragraph(" "));

            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm");
            String generated = Instant.ofEpochMilli(invoice.getGeneratedAt()).atZone(ZoneId.systemDefault()).format(fmt);
            document.add(new Paragraph("Date: " + generated, normalFont));
            document.add(new Paragraph("Guest: " + invoice.getReservation().getGuest().getName(), normalFont));
            document.add(new Paragraph("Room: " + (invoice.getReservation().getRoom() != null ? invoice.getReservation().getRoom().getRoomNumber() : "N/A"), normalFont));
            document.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100);
            table.addCell(new PdfPCell(new Phrase("Description", headerFont)));
            table.addCell(new PdfPCell(new Phrase("Amount (AED)", headerFont)));
            table.addCell(new PdfPCell(new Phrase("Room Charges", normalFont)));
            table.addCell(new PdfPCell(new Phrase(String.format("%.2f", invoice.getRoomCharges()), normalFont)));
            table.addCell(new PdfPCell(new Phrase("Extra Charges", normalFont)));
            table.addCell(new PdfPCell(new Phrase(String.format("%.2f", invoice.getExtraCharges()), normalFont)));
            table.addCell(new PdfPCell(new Phrase("Tax (GST)", normalFont)));
            table.addCell(new PdfPCell(new Phrase(String.format("%.2f", invoice.getTaxAmount()), normalFont)));
            table.addCell(new PdfPCell(new Phrase("Discount", normalFont)));
            table.addCell(new PdfPCell(new Phrase(String.format("%.2f", invoice.getDiscountAmount()), normalFont)));
            PdfPCell totalLabel = new PdfPCell(new Phrase("TOTAL", headerFont));
            PdfPCell totalValue = new PdfPCell(new Phrase(String.format("%.2f", invoice.getTotalAmount()), headerFont));
            table.addCell(totalLabel);
            table.addCell(totalValue);
            table.addCell(new PdfPCell(new Phrase("Paid", normalFont)));
            table.addCell(new PdfPCell(new Phrase(String.format("%.2f", invoice.getPaidAmount()), normalFont)));
            table.addCell(new PdfPCell(new Phrase("Balance Due", headerFont)));
            table.addCell(new PdfPCell(new Phrase(String.format("%.2f", invoice.getBalanceDue()), headerFont)));
            document.add(table);

            document.add(new Paragraph(" "));
            document.add(new Paragraph("Payment Status: " + invoice.getPaymentStatus(), normalFont));
            document.add(new Paragraph("Payment Mode: " + invoice.getPaymentMode(), normalFont));

            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            log.error("Error generating PDF for invoice {}", invoiceId, e);
            throw new BadRequestException("Failed to generate PDF");
        }
    }

    private InvoiceDTO convertToDTO(Invoice invoice) {
        return new InvoiceDTO(
                invoice.getId(),
                invoice.getReservation().getId(),
                invoice.getRoomCharges(),
                invoice.getExtraCharges(),
                invoice.getDiscountAmount(),
                invoice.getTaxAmount(),
                invoice.getTotalAmount(),
                invoice.getPaidAmount(),
                invoice.getBalanceDue(),
                invoice.getPaymentMode(),
                invoice.getPaymentStatus(),
                invoice.getNotes(),
                invoice.getGeneratedAt()
        );
    }
}
