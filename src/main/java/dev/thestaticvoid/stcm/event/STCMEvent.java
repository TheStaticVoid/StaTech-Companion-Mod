package dev.thestaticvoid.stcm.event;

import net.neoforged.bus.api.IEventBus;

public class STCMEvent {
    public static void init(IEventBus eventBus) {
        eventBus.addListener(UseItemOnBlockHandler::onPlayerUseItem);
    }
}
