package com.paiementdemat.mobilepay;

import java.util.Comparator;

public class DateSorter implements Comparator<History> {
    @Override
    public int compare(History o1, History o2){
        return o2.getCreatedAt().compareToIgnoreCase(o1.getCreatedAt());
    }
}
