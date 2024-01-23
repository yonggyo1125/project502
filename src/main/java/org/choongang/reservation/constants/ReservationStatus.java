package org.choongang.reservation.constants;

import org.choongang.commons.Utils;

import java.util.Arrays;
import java.util.List;

public enum ReservationStatus {
    APPLY(Utils.getMessage("ReservationStatus.APPLY", "commons")),
    COMPLETE(Utils.getMessage("ReservationStatus.COMPLETE", "commons")),
    CANCEL(Utils.getMessage("ReservationStatus.CANCEL", "commons"));

    private final String title;

    ReservationStatus(String title) {
        this.title = title;
    }

    public static List<String[]> getList() {
        return Arrays.asList(
                new String[] { APPLY.name(), APPLY.title },
                new String[] { COMPLETE.name(), COMPLETE.title },
                new String[] { CANCEL.name(), CANCEL.title }
        );
    }
}
