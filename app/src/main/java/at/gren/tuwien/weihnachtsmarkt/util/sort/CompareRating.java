package at.gren.tuwien.weihnachtsmarkt.util.sort;

import java.util.Comparator;

import at.gren.tuwien.weihnachtsmarkt.data.model.Weihnachtsmarkt;

public class CompareRating implements Comparator<Weihnachtsmarkt> {
    @Override
    public int compare(Weihnachtsmarkt markt1, Weihnachtsmarkt markt2) {
        return markt2.properties().AVERAGERATING().compareTo(markt1.properties().AVERAGERATING());
    }
}
