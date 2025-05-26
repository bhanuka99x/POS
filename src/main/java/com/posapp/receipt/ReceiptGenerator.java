package com.posapp.receipt;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.posapp.controllers.OperatorDashboardController.ReceiptItem;

import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class ReceiptGenerator {

    public static void generateReceipt(List<ReceiptItem> items, double subTotal, double tax, double discount, double total, String paymentOption, int paymentId) {

        Document document = new Document();

        try {
            String filename = "receipt_" + System.currentTimeMillis() + ".pdf";
            File receiptFile = new File(System.getProperty("user.home"), filename);
            PdfWriter.getInstance(document, new FileOutputStream(receiptFile));
            document.open();

            // Title
            Paragraph title = new Paragraph("SUPERMARKET", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18));
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            // Contact Info
            Paragraph info = new Paragraph(
                    "Lorem ipsum 258\nCity Index - 02025\nTel.: +456-468-987-02\n",
                    FontFactory.getFont(FontFactory.HELVETICA, 11)
            );
            info.setAlignment(Element.ALIGN_CENTER);
            document.add(info);

            // Separator
            document.add(new Paragraph("------------------------------------------------------------"));

            // Cashier & Manager Info
            Paragraph staffInfo = new Paragraph("Cashier: #3\nManager: Eric Steer\n", FontFactory.getFont(FontFactory.HELVETICA, 11));
            document.add(staffInfo);

            document.add(new Paragraph("------------------------------------------------------------"));

            // Table Header
            PdfPTable table = new PdfPTable(3);
            table.setWidthPercentage(100);
            table.setSpacingBefore(10f);
            table.setSpacingAfter(10f);
            table.setWidths(new float[]{3, 1, 2});

            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
            table.addCell(new PdfPCell(new Phrase("Name", headerFont)));
            table.addCell(new PdfPCell(new Phrase("Qty", headerFont)));
            table.addCell(new PdfPCell(new Phrase("Price", headerFont)));

            // Items
            Font itemFont = FontFactory.getFont(FontFactory.HELVETICA, 11);
            for (ReceiptItem item : items) {
                table.addCell(new Phrase(item.getProductName(), itemFont));
                table.addCell(new Phrase(String.valueOf(item.getQuantity()), itemFont));
                table.addCell(new Phrase(String.format("$%.2f", item.getPrice()), itemFont));
            }

            document.add(table);
            document.add(new Paragraph("------------------------------------------------------------"));

            // Totals
            Font totalFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
            document.add(new Paragraph(String.format("Sub Total            $%.2f", subTotal), totalFont));
            document.add(new Paragraph(String.format("CASH                 $%.2f", total), totalFont));
            document.add(new Paragraph(String.format("CHANGE               $%.2f", total - subTotal), totalFont));

            document.add(new Paragraph("------------------------------------------------------------"));

            // Barcode (simulated with text)
            Paragraph barcode = new Paragraph("| || ||||| || ||| || || ||| |", FontFactory.getFont(FontFactory.COURIER_BOLD, 16));
            barcode.setAlignment(Element.ALIGN_CENTER);
            document.add(barcode);

            // Thank You
            Paragraph thanks = new Paragraph("THANK YOU!\nGlad to see you again!", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12));
            thanks.setAlignment(Element.ALIGN_CENTER);
            document.add(thanks);

            document.close();

            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(receiptFile);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
