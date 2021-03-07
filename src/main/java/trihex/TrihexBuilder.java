package trihex;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import pdf.ToPdf;
import pdf.ToPs;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.io.File;

public class TrihexBuilder {
    String docTitle = "trihex";
    String pageSize = "a4";
    Element designElement;

    public TrihexBuilder() throws Throwable {
    }

    static double mm2pt(double mm) {
        double points = mm * 72.0 / 25.4;
        return points;
    }

    public static String mm2pti(double mm) {
        Double mmpoints = mm2pt(mm);
        Integer immipoints = mmpoints.intValue();
        return immipoints.toString();
    }

    private String getBoundingBox() {
        Rectangle2D bounds = new Rectangle(1000, 2000, 0, 0);
        return mm2pti(-bounds.getMinX()) + " " +
                mm2pti(bounds.getMinY()) + " " +
                mm2pti(-bounds.getMaxX()) + " " +
                mm2pti(bounds.getMaxY());
    }

    public String convertToPs(String title) throws Throwable {
        String boundingBox = getBoundingBox();
        ToPs toPs = new ToPs(true);
        int pageNumber = 0;
        String postScript = toPs.getDocumentHeader(title, pageSize, boundingBox, 1);
        postScript += toPs.getPageHeader(pageNumber);
        for (int x = 0; x < 6; x++) {
            for (int y = 0; y < 10; y++) {
                postScript += toPs.draw_hex(x, y);
            }
        }
        for (int x = 0; x < 6; x++) {
            for (int y = 0; y < 10; y++) {
                postScript += toPs.draw_tri(x, y, true);
                postScript += toPs.draw_tri(x, y, false);
            }
        }
        postScript += toPs.getPageTrailer();
        postScript += toPs.getDocumentTrailer();
        return postScript;
    }

    public void convertToPdf(String temp, File outputFile) throws Throwable {
        (new ToPdf()).convert(temp, outputFile, pageSize);
    }
}
