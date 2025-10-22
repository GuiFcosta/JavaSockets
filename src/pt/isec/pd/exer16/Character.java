package pt.isec.pd.exer16;

import java.net.URL;
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
        return "index=" + index + "\n" +
                "fullName=" + fullName + "\n" +
                "birthDate=" + birthDate + "\n" +
                "hogwartsHouse=" + hogwartsHouse + "\n" +
                "interpretedBy=" + interpretedBy + "\n" +
                "image=" + image + "\n" +
                "children=" + String.join(", ", children);
    }
}
