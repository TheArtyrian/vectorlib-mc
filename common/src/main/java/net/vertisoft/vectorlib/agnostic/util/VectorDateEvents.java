package net.vertisoft.vectorlib.agnostic.util;

import java.time.LocalDate;
import java.time.Month;

public class VectorDateEvents
{
    // All event types
    private static boolean IS_XENS_BDAY;
    private static boolean IS_YUR_BDAY;
    private static boolean IS_HECCO_BDAY;
    private static boolean IS_YIRMIRI_BDAY;
    private static boolean IS_THE_WORST_DAY_EVER;

    public static void getDates()
    {
        LocalDate localDate = LocalDate.now();
        int day = localDate.getDayOfMonth();
        Month month = localDate.getMonth();

        IS_XENS_BDAY = (day == 22 && month == Month.SEPTEMBER);
        IS_YUR_BDAY = (day == 15 && month == Month.NOVEMBER);
        IS_HECCO_BDAY = (day == 1 && month == Month.OCTOBER);
        IS_YIRMIRI_BDAY = (day == 15 && month == Month.MARCH);
        IS_THE_WORST_DAY_EVER = (day == 30 && month == Month.AUGUST);
    }

    public static boolean isXensBday() { return IS_XENS_BDAY; }
    public static boolean isYurBday() { return IS_YUR_BDAY; }
    public static boolean isHeccoBday() { return IS_HECCO_BDAY; }
    public static boolean isYirmiriBday() { return IS_YIRMIRI_BDAY; }
    public static boolean isBadDay() { return IS_THE_WORST_DAY_EVER; }
}
