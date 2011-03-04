package edu.gemini.aspen.giapi.status.mappers;

import edu.gemini.aspen.giapi.status.Mapper;
import edu.gemini.aspen.giapi.status.StatusItem;

/**
 * A Mapper implementation to extract Integer values out of
 * a Status Item.
 */
public class IntMapper implements Mapper<Integer,Integer> {
    @Override
    public Integer extract(StatusItem<Integer> item) {
        return item.getValue();
    }
}
