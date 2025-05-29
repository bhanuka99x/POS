package com.posapp.receipt;

import com.itextpdf.text.pdf.draw.VerticalPositionMark;
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

    public static void generateReceipt(List<ReceiptItem> items, double subTotal, double tax, double discount, double total, String paymentOption, int paymentId) {

        Document document = new Document(new Rectangle(200f, 842f), 10, 10, 10, 10); // A4 height is just a placeholder
        document.setMargins(10, 10, 10, 10);



        try {
            String filename = "receipt_" + System.currentTimeMillis() + ".pdf";
            File receiptFile = new File(System.getProperty("user.home"), filename);
            PdfWriter.getInstance(document, new FileOutputStream(receiptFile));
            document.open();

            // Header
            Paragraph header = new Paragraph("Luxe Spoons", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10));
            header.setAlignment(Element.ALIGN_CENTER);
            document.add(header);

            Paragraph contact = new Paragraph("No.30 Main Street\nKegalle\nPhone: 035-561-8956\n\n",
                    FontFactory.getFont(FontFactory.HELVETICA, 7));
            contact.setAlignment(Element.ALIGN_CENTER);
            document.add(contact);

            document.add(new Paragraph("Receipt No: #" + paymentId, FontFactory.getFont(FontFactory.HELVETICA, 7)));
            document.add(new Paragraph("Date: " + LocalDate.now(), FontFactory.getFont(FontFactory.HELVETICA, 7)));
            document.add(new Paragraph("Time: " + LocalTime.now().withNano(0), FontFactory.getFont(FontFactory.HELVETICA, 7)));
            document.add(new Paragraph("Payment: " + paymentOption, FontFactory.getFont(FontFactory.HELVETICA, 7)));
            document.add(new Paragraph("-----------------------------------------------------------------------------", FontFactory.getFont(FontFactory.HELVETICA, 7)));

            // Items Table: Name | Qty | Price
            PdfPTable itemTable = new PdfPTable(3);
            itemTable.setWidths(new float[]{2.5f, 1f, 1f}); // relative column widths
            itemTable.setWidthPercentage(100);

            Font tableHeaderFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 7);
            Font tableFont = FontFactory.getFont(FontFactory.HELVETICA, 7);

            // Table Headers
            itemTable.addCell(createCell("Item", tableHeaderFont, Element.ALIGN_LEFT, false));
            itemTable.addCell(createCell("Qty", tableHeaderFont, Element.ALIGN_CENTER, false));
            itemTable.addCell(createCell("Price", tableHeaderFont, Element.ALIGN_RIGHT, false));

            // Items
            for (ReceiptItem item : items) {
                itemTable.addCell(createCell(item.getProductName(), tableFont, Element.ALIGN_LEFT, false));
                itemTable.addCell(createCell(String.valueOf(item.getQuantity()), tableFont, Element.ALIGN_CENTER, false));
                itemTable.addCell(createCell(String.format("$%.2f", item.getPrice()), tableFont, Element.ALIGN_RIGHT, false));
            }

            document.add(itemTable);
            document.add(new Paragraph("-----------------------------------------------------------------------------", FontFactory.getFont(FontFactory.HELVETICA, 7)));

            // Summary rows
            document.add(createAlignedParagraph("Subtotal:", String.format("$%.2f", subTotal)));
            document.add(createAlignedParagraph("Tax (15%):", String.format("$%.2f", tax)));
            document.add(createAlignedParagraph("Discount:", String.format("$%.2f", discount)));
            document.add(createAlignedParagraphBold("Total:", String.format("$%.2f", total)));

            // Footer
            Paragraph thanks = new Paragraph("\nThank you for dining with us!", FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 7));
            thanks.setAlignment(Element.ALIGN_CENTER);
            document.add(thanks);

            document.close();

            // Auto-open the PDF
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(receiptFile);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Helper for item cells
    private static PdfPCell createCell(String text, Font font, int alignment, boolean border) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setHorizontalAlignment(alignment);
        cell.setBorder(border ? Rectangle.BOX : Rectangle.NO_BORDER);
        cell.setPadding(2);
        return cell;
    }

    // Pad text helper
    private static String padRight(String text, int length) {
        if (text.length() >= length) {
            return text.substring(0, length - 1) + " ";
        }
        return String.format("%-" + length + "s", text);
    }

    // Summary row aligned (left + right)
    private static Paragraph createAlignedParagraph(String leftText, String rightText) {
        Chunk left = new Chunk(leftText, FontFactory.getFont(FontFactory.HELVETICA, 7));
        Chunk right = new Chunk(rightText, FontFactory.getFont(FontFactory.HELVETICA, 7));
        Paragraph p = new Paragraph();
        p.add(left);
        p.add(new Chunk(new VerticalPositionMark()));
        p.add(right);
        return p;
    }

    // Total row (bold and slightly larger)
    private static Paragraph createAlignedParagraphBold(String leftText, String rightText) {
        Chunk left = new Chunk(leftText, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9));
        Chunk right = new Chunk(rightText, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9));
        Paragraph p = new Paragraph();
        p.add(left);
        p.add(new Chunk(new VerticalPositionMark()));
        p.add(right);
        return p;
    }
}
