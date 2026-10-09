/*
 * goPaint is designed to simplify painting inside of Minecraft.
 * Copyright (C) Arcaniax-Development
 * Copyright (C) Arcaniax team and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package net.onelitefeather.bettergopaint.injection;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;
import net.onelitefeather.bettergopaint.listeners.ConnectListener;
import net.onelitefeather.bettergopaint.listeners.InteractListener;
import net.onelitefeather.bettergopaint.listeners.InventoryListener;
import org.bukkit.event.Listener;

/**
 * Collects every listener into a {@code Set<Listener>} that the plugin registers on enable.
 */
public final class ListenerModule extends AbstractModule {

    @Override
    protected void configure() {
        Multibinder<Listener> listeners = Multibinder.newSetBinder(binder(), Listener.class);
        listeners.addBinding().to(ConnectListener.class);
        listeners.addBinding().to(InteractListener.class);
        listeners.addBinding().to(InventoryListener.class);
    }
}
