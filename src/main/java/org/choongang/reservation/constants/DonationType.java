package org.choongang.reservation.constants;

import org.choongang.commons.Utils;

import java.util.Arrays;
import java.util.List;

public enum DonationType {
    TYPE_ALL(Utils.getMessage("DonationType.TYPE_ALL", "commons")),
    TYPE_A(Utils.getMessage("DonationType.TYPE_A", "commons")),
    TYPE_B(Utils.getMessage("DonationType.TYPE_B", "commons"));

    private final String title;

    DonationType(String title) {
        this.title = title;
    }

    public static List<String[]> getList() {

        return Arrays.asList(
                new String[] { TYPE_ALL.name(), TYPE_ALL.title},
                new String[] { TYPE_A.name(), TYPE_A.title},
                new String[] { TYPE_B.name(), TYPE_B.title}
        );
    }
}
