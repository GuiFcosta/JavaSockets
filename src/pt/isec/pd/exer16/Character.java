package pt.isec.pd.exer16;

import java.awt.*;
import java.net.URI;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

public record Character (
        int index,
        String fullName,
        Date birthDate,
        String hogwartsHouse,
        String interpretedBy,
        URL image,
        String[] children) {

    @Override
    public String toString() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String birthDateStr = (birthDate == null) ? "null" : sdf.format(birthDate);

        return "index=" + index + "\n" +
                "fullName=" + fullName + "\n" +
                "birthDate=" + birthDateStr + "\n" +
                "hogwartsHouse=" + hogwartsHouse + "\n" +
                "interpretedBy=" + interpretedBy + "\n" +
                "image=" + image + "\n" +
                "children=" + String.join(", ", children);
    }
}
