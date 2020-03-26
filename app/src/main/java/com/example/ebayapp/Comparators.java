package com.example.ebayapp;

import java.util.Comparator;

public class Comparators {

    static class AscSortPriceComparator implements Comparator<SimilarItems> {
        @Override
        public int compare(SimilarItems s1, SimilarItems s2) {
            return s1.getPrice().compareTo(s2.getPrice());
        }
    }

    static class AscSortTitleComparator implements Comparator<SimilarItems> {
        @Override
        public int compare(SimilarItems s1, SimilarItems s2) {
            return s1.getTitle().toLowerCase().compareTo(s2.getTitle().toLowerCase());
        }
    }

    static class AscSortDaysLeftComparator implements Comparator<SimilarItems> {
        @Override
        public int compare(SimilarItems s1, SimilarItems s2) {
            Integer daysLeft1 = Integer.parseInt(s1.getDaysLeft());
            Integer daysLeft2 = Integer.parseInt(s2.getDaysLeft());
            return daysLeft1.compareTo(daysLeft2);
        }
    }

    static class AscSortShippingComparator implements Comparator<SimilarItems> {
        @Override
        public int compare(SimilarItems s1, SimilarItems s2) {
            Integer shipping1 = Integer.parseInt(s1.getShipping());
            Integer shipping2 = Integer.parseInt(s2.getShipping());
            return shipping1.compareTo(shipping2);
        }
    }
}
