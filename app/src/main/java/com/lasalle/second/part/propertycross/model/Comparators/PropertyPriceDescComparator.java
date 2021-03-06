package com.lasalle.second.part.propertycross.model.Comparators;

import com.lasalle.second.part.propertycross.model.Property;

import java.util.Comparator;

/**
 * Created by Eduard on 25/01/2016.
 */
public class PropertyPriceDescComparator implements Comparator<Property> {
    @Override
    public int compare(Property lhs, Property rhs) {
        return Float.compare(rhs.getPrice(), lhs.getPrice());
    }
}
