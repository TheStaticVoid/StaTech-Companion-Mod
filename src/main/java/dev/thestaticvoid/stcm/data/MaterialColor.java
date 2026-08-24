package dev.thestaticvoid.stcm.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record MaterialColor (String name, String color){
    public static final Codec<MaterialColor> CODEC = RecordCodecBuilder.create(
    instance -> instance.group(
            Codec.STRING.fieldOf("name").forGetter(MaterialColor::name),
            Codec.STRING.fieldOf("color").forGetter(MaterialColor::color))
            .apply(instance, MaterialColor::new));
}