/*
 * WorldEdit, a Minecraft world manipulation toolkit
 * Copyright (C) sk89q <http://www.sk89q.com>
 * Copyright (C) WorldEdit team and contributors
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

package com.sk89q.worldedit.util.formatting.component;

import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.util.SideEffect;
import com.sk89q.worldedit.util.SideEffectSet;
import com.sk89q.worldedit.util.concurrency.LazyReference;
import com.sk89q.worldedit.util.formatting.WorldEditText;
import com.sk89q.worldedit.util.formatting.text.Component;
import com.sk89q.worldedit.util.formatting.text.TranslatableComponent;
import com.sk89q.worldedit.util.formatting.text.format.NamedTextColor;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import static com.sk89q.worldedit.util.formatting.text.Component.space;
import static com.sk89q.worldedit.util.formatting.text.Component.translatable;
import static com.sk89q.worldedit.util.formatting.text.event.ClickEvent.runCommand;

public class SideEffectBox extends PaginationBox {

    private static final LazyReference<List<SideEffect>> SIDE_EFFECTS = LazyReference.from(() ->
        WorldEdit.getInstance().getPlatformManager().getSupportedSideEffects()
            .stream()
            .sorted(Comparator.comparing(effect ->
                WorldEditText.reduceToText(
                    translatable(effect.getDisplayName()),
                    Locale.US
                )
            ))
            .collect(Collectors.toList())
    );

    private final SideEffectSet sideEffectSet;

    private static List<SideEffect> getSideEffects() {
        return SIDE_EFFECTS.getValue();
    }

    public SideEffectBox(SideEffectSet sideEffectSet) {
        super("Side Effects");

        this.sideEffectSet = sideEffectSet;
    }

    private static final SideEffect.State[] SHOWN_VALUES = {SideEffect.State.OFF, SideEffect.State.ON};

    @Override
    public Component getComponent(int number) {
        SideEffect effect = getSideEffects().get(number);
        SideEffect.State state = this.sideEffectSet.getState(effect);

        TranslatableComponent.Builder builder = translatable()
            .key(effect.getDisplayName())
            .color(NamedTextColor.YELLOW)
            .hoverEvent(translatable(effect.getDescription()));
        for (SideEffect.State uiState : SHOWN_VALUES) {
            builder = builder.append(space());
            builder = builder.append(translatable(uiState.getDisplayName(), uiState == state ? NamedTextColor.WHITE : NamedTextColor.GRAY)
                .clickEvent(runCommand("//perf -h " + effect.name().toLowerCase(Locale.US) + " " + uiState.name().toLowerCase(Locale.US)))
                .hoverEvent(uiState == state
                    ? translatable("worldedit.sideeffect.box.current")
                    : translatable("worldedit.sideeffect.box.change-to", translatable(uiState.getDisplayName()))
                )
            );
        }

        return builder.build();
    }

    @Override
    public int getComponentsSize() {
        return getSideEffects().size();
    }
}
