package com.gmail.guitaekm.neoforge;

import net.neoforged.fml.common.Mod;

import com.gmail.guitaekm.ExampleMod;

@Mod(ExampleMod.MOD_ID)
public final class ExampleModNeoForge {
    public ExampleModNeoForge() {
        // Run our common setup.
        ExampleMod.init();
    }
}
