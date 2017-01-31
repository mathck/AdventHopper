package at.gren.tuwien.weihnachtsmarkt.util.sort;

import java.util.Comparator;

import at.gren.tuwien.weihnachtsmarkt.data.model.Weihnachtsmarkt;

public class CompareName implements Comparator<Weihnachtsmarkt> {
    @Override
    public int compare(Weihnachtsmarkt markt1, Weihnachtsmarkt markt2) {
        return markt1.properties().BEZEICHNUNG().compareTo(markt2.properties().BEZEICHNUNG());
    }
}
