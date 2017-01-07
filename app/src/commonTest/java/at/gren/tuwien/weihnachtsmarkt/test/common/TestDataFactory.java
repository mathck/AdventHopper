package at.gren.tuwien.weihnachtsmarkt.test.common;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import at.gren.tuwien.weihnachtsmarkt.data.model.Properties;
import at.gren.tuwien.weihnachtsmarkt.data.model.Weihnachtsmarkt;

/**
 * Factory class that makes instances of data models with random field values.
 * The aim of this class is to help setting up test fixtures.
 */
public class TestDataFactory {

    public static String randomUuid() {
        return UUID.randomUUID().toString();
    }

    public static Weihnachtsmarkt makeRibot(String uniqueSuffix) {
        return Weihnachtsmarkt.create("type", randomUuid(), null, "geometry_name", makeProperties(uniqueSuffix));
    }

    public static List<Weihnachtsmarkt> makeListMärkte(int number) {
        List<Weihnachtsmarkt> märkte = new ArrayList<>();
        for (int i = 0; i < number; i++) {
            märkte.add(makeRibot(String.valueOf(i)));
        }
        return märkte;
    }

    public static Properties makeProperties(String uniqueSuffix) {
        return Properties.builder()
                .setBEZEICHNUNG(uniqueSuffix)
                .setADRESSE("Fancy Adresse Straße 5")
                .setDATUM("21.11.1991")
                .setOBJECTID("pikatchu")
                .setOEFFNUNGSZEIT("Mo - Fr 10:00 - 15:00")
                .setSILVESTERMARKT(0)
                .setWEBLINK1("http://www.google.at/")
                .build();
    }
}