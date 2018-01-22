package net.cizirti.thesoundfxer.utils;

import java.util.Locale;

/**
 * Created by cezvedici on 22.01.2018.
 */

public class Utility {
    public static String convertMsToReadableTime(int duration) {
        int d = 0, s = 0;

        while (duration > 1000) {
            s += 1;

            if (s > 60) {
                d += 1;
                s = 0;
            }

            duration -= 1000;
        }

        return String.format(Locale.CANADA, "%02d:%02d", d, s);
    }
}
