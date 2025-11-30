package com.example.event;

import com.google.common.eventbus.Subscribe;
import net.xiaoyu233.fml.reload.event.ItemRegistryEvent;

// register fish events
public class FishEventListen {
    @Subscribe
    public void onItemRegister(ItemRegistryEvent event) {
    }
    //please read net.xiaoyu233.fml.reload.event for events
}
