package nodopezzz.android.wishlist;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DataParser {

    public static String formDate(String date){
        if(date == null) return "";
        SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date parseDate = ft.parse(date);
            return String.format("%te %<tB %<tY", parseDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String formYear(String date) {
        if (date == null) return "";
        SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date parseDate = ft.parse(date);
            return String.format("%tY", parseDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

}
