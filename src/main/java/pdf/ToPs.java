package pdf;


import java.awt.geom.Point2D;
import java.util.Date;

import static java.lang.Math.*;

public class ToPs {

    static final double Scale = 0.96;
    static final double Unit = 14.0;
    static final double X_factor = Unit * 2;
    static final double Y_factor = X_factor * sqrt(3) / 2;
    static final double Half = Unit / 2;
    static final double Height = Half * sqrt(3);
    static final double Up = Unit / sqrt(3);
    static final double Down = Up / 2;
    static final double Match = 0.0;

    // The PostScript coordinate system has origo in the bottom-left corner

    private double oldX = 0.111111111;
    private double oldY = 0.111111111;
    private boolean colors;
    private Annotations annotations;

    public ToPs(boolean colors) {
        this.colors = colors;
        this.annotations = new Annotations(colors);
    }

    static double mm2pt(double mm) {
        return mm * 72.0 / 25.4;
    }

    String getCreationDate() {
        return (new Date()).toString();
    }

    public String getDocumentHeader(String title, String pageSize, String boundingBox, int numberOfPages) {
        annotations.setPageSize(pageSize);
        annotations.setNumberOfPages(numberOfPages);
        return "%!PS-Adobe-2.0\n" +
                "%%Creator: kladd\n" +
                "%%CreationDate: " + getCreationDate() + "\n" +
                "%%Title: " + title + "\n" +
                "%%Pages: " + numberOfPages + "\n" +
                "%%PageOrder: Ascend\n" +
                "%%BoundingBox: " + boundingBox + "\n" +
                "%%DocumentPaperSizes: " + pageSize + "\n" +
                "%%Orientation: Portrait\n" +
                "%%EndComments\n";
    }

    public String getDocumentTrailer() {
        return "%%EOF\n";
    }

/*
    A0 = 2384 x 3370
    A1 = 1684 x 2384
    A2 = 1191 x 1684
    A3 = 842 x 1191
    A4 = 595 x 842
*/

    public String getPageHeader(int pageNumber) {
        pageNumber++;
        String header = "%%Page: " + pageNumber + " " + pageNumber + "\n" +
                "%%BeginPageSetup\n" +
                "/pagelevel save def\n";
//                "90 rotate\n";
        header += "%%EndPageSetup\n";
        return header;
    }

    public String getPageTrailer() {
        return "pagelevel restore\n" + "showpage\n";
    }

    private static String toPsString(double d) {
        //DecimalFormat formatter = new DecimalFormat("#.##");
        //String s = "(" + formatter.format(d) + ")";
        return "(" + d + ")";
    }

    private String drawMarker(Point2D.Double point, Point2D.Double localOrigo, Point2D.Double globalOrigo) {
        double x = mm2pt(-point.getX() + globalOrigo.getX());
        double y = mm2pt(point.getY() + globalOrigo.getY());
        double newX = localOrigo.getX() - point.getX();
        double newY = -localOrigo.getY() + point.getY();
        String outputString = "";
        outputString += "newpath\n";
        outputString += (x - 8) + " " + (y) + " moveto\n";
        outputString += (x + 8) + " " + (y) + " lineto\n";
        outputString += "closepath stroke\n";
        outputString += "newpath\n";
        outputString += (x) + " " + (y - 8) + " moveto\n";
        outputString += (x) + " " + (y + 7) + " lineto\n";
        outputString += "closepath stroke\n";
        if (oldY != newY) {
            outputString += "newpath\n";
            outputString += (x + 9) + " " + (y - 8) + " moveto\n";
            outputString += toPsString(newY) + " show\n";
            outputString += "stroke\n";
            oldY = newY;
        }
        if (oldX != newX) {
            outputString += "newpath\n";
            outputString += (x - 8) + " " + (y + 8) + " moveto\n";
            outputString += toPsString(newX) + " show\n";
            outputString += "stroke\n";
            oldX = newX;
        }
        return outputString;
    }

    public String draw_hex(int x_index, int y_index) {
        double x_offset = 20;
        double y_offset = 40;
        boolean odd_line = y_index % 2 == 0;
        double x = x_index * X_factor;
        double y = y_index * Y_factor;
        x += x_offset + (odd_line ? 0.0 : 0.5 * X_factor);
        y += y_offset;
        String outputString = "gsave\n";
        outputString += "1 setlinewidth 1 setlinecap \n";
        outputString += "0 0 0 setrgbcolor\n";
        outputString += "newpath\n";
        outputString += scale_move(x, +Half, y, +Height, 1.0); // P
        outputString += scale_line(x, +Unit, y, +Match, 1.0); // Q
        outputString += scale_line(x, +Half, y, -Height, 1.0); // R
        outputString += scale_line(x, -Half, y, -Height, 1.0); // S
        outputString += scale_line(x, -Unit, y, +Match, 1.0); // T
        outputString += scale_line(x, -Half, y, +Height, 1.0); // U
        outputString += "closepath stroke\n";
        outputString += "grestore\n";
        return outputString;
    }

    public String draw_tri(int x_index, int y_index, boolean up_down) {
        double x_offset = 20;
        double y_offset = 40;
        boolean odd_line = y_index % 2 == 0;
        double x = x_index * X_factor;
        double y = y_index * Y_factor;
        x += x_offset + (odd_line ? 0.5 * X_factor : 0.0);
        y += up_down ? Down : -Up;
        y += y_offset;
        String outputString = "gsave\n";
        outputString += "1 setlinewidth 1 setlinecap \n";
        outputString += "0 0 0 setrgbcolor\n";
        outputString += "newpath\n";
        if (up_down) {
            outputString += scale_move(x, +Match, y, -Down, Scale); // A
            outputString += scale_line(x, +Half, y, +Up, Scale); // B
            outputString += scale_line(x, -Half, y, +Up, Scale); // C
        } else {
            outputString += scale_move(x, +Match, y, +Up, Scale); // A
            outputString += scale_line(x, +Half, y, -Down, Scale); // B
            outputString += scale_line(x, -Half, y, -Down, Scale); // C
        }
        outputString += "closepath stroke\n";
        outputString += "grestore\n";
        return outputString;
    }

    public String scale_move(double x, double x_offset, double y, double y_offset, double scale) {
        double angle = atan2(y_offset, x_offset);
        double vector = sqrt(x_offset * x_offset + y_offset * y_offset);
        vector *= Scale * scale;
        y_offset = sin(angle) * vector;
        x_offset = cos(angle) * vector;
        return mm2pt(x + x_offset) + " " + mm2pt(y + y_offset) + " moveto\n";
    }

    public String scale_line(double x, double x_offset, double y, double y_offset, double scale) {
        double angle = atan2(y_offset, x_offset);
        double vector = sqrt(x_offset * x_offset + y_offset * y_offset);
        vector *= Scale * scale;
        y_offset = sin(angle) * vector;
        x_offset = cos(angle) * vector;
        return mm2pt(x + x_offset) + " " + mm2pt(y + y_offset) + " lineto\n";
    }
}
