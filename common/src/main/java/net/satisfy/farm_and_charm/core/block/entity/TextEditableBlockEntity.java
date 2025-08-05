package net.satisfy.farm_and_charm.core.block.entity;

import net.minecraft.network.chat.Component;

public interface TextEditableBlockEntity {
    void setText(int line, Component text);
    int getTextLineCount();
}