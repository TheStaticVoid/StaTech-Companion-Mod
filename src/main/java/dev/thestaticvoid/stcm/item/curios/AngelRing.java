package dev.thestaticvoid.stcm.item.curios;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import dev.thestaticvoid.stcm.STCM;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.neoforged.neoforge.common.NeoForgeMod;
import top.theillusivec4.curios.api.SlotContext;

public class AngelRing extends STCMCurioItem {

    public static final ResourceLocation FLIGHT_MODIFIER_ID = ResourceLocation.fromNamespaceAndPath(STCM.ID, "creative_flight");

    public AngelRing(Properties properties) {
		super(properties);
	}

  @Override
  public Multimap<Holder<Attribute>, AttributeModifier> getAttributeModifiers(SlotContext slotContext, ResourceLocation id, net.minecraft.world.item.ItemStack stack) {
    Multimap<Holder<Attribute>, AttributeModifier> attributes = HashMultimap.create();

    // Add creative flight modifier (+1.0 via ADD_VALUE)
    attributes.put(
        NeoForgeMod.CREATIVE_FLIGHT,
        new AttributeModifier(
            FLIGHT_MODIFIER_ID,
            1.0,
            AttributeModifier.Operation.ADD_VALUE
        )
    );
      return attributes;
  }
}
