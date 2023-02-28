package com.mybus.transformx.players;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

public class PlayerItemProcessor implements ItemProcessor<Player, OutPlayer> {

    private static final Logger LOGGER = LoggerFactory.getLogger(PlayerItemProcessor.class);

    @Override
    public OutPlayer process(final Player player) throws Exception {
        return new OutPlayer(player);
    }

}
