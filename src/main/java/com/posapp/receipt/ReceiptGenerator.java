package com.posapp.receipt;

import com.posapp.controllers.OperatorDashboardController.ReceiptItem;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class ReceiptGenerator {

    public static void generateReceipt(List<ReceiptItem> items, double subTotal, double tax, double discount, double total, String paymentOption) {
        Document document = new Document();

        try {
            String filename = "receipt_" + System.currentTimeMillis() + ".pdf";
            File receiptFile = new File(System.getProperty("user.home"), filename);
            PdfWriter.getInstance(document, new FileOutputStream(receiptFile));
            document.open();

            // Shop details
            Paragraph header = new Paragraph("My Restaurant POS\n", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18));
            header.setAlignment(Element.ALIGN_CENTER);
            document.add(header);

            Paragraph contact = new Paragraph("123 Main Street, City\nPhone: 123-456-7890\n\n", FontFactory.getFont(FontFactory.HELVETICA, 12));
            contact.setAlignment(Element.ALIGN_CENTER);
            document.add(contact);

            document.add(new Paragraph("Date: " + LocalDate.now()));
            document.add(new Paragraph("Time: " + LocalTime.now()));
            document.add(new Paragraph("Payment Method: " + paymentOption));
            document.add(new Paragraph("------------------------------------------------------------\n"));

            // Table of items
            PdfPTable table = new PdfPTable(3);
            table.setWidthPercentage(100);
            table.setSpacingBefore(10f);
            table.setSpacingAfter(10f);

            table.addCell("Item");
            table.addCell("Qty");
            table.addCell("Price");

            for (ReceiptItem item : items) {
                table.addCell(item.getProductName());
                table.addCell(String.valueOf(item.getQuantity()));
                table.addCell(String.format("$%.2f", item.getPrice()));
            }

            document.add(table);

            // Summary
            document.add(new Paragraph(String.format("Subtotal: $%.2f", subTotal)));
            document.add(new Paragraph(String.format("Tax (15%%): $%.2f", tax)));
            document.add(new Paragraph(String.format("Discount: $%.2f", discount)));
            document.add(new Paragraph(String.format("Total: $%.2f", total)));

            document.add(new Paragraph("\nThank you for dining with us!", FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 12)));

            document.close();

            // Auto-open the PDF
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(receiptFile);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
