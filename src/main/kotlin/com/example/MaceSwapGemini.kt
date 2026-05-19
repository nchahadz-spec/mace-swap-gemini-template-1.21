package com.example.maceswap

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.event.player.AttackEntityCallback
import net.minecraft.client.MinecraftClient
import net.minecraft.item.AxeItem
import net.minecraft.item.SwordItem
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.item.Items

class MaceSwapMod : ClientModInitializer {
    override fun onInitializeClient() {
        AttackEntityCallback.EVENT.register { player, world, hand, entity, hitResult ->
            if (world.isClient && hand == Hand.MAIN_HAND) {
                val client = MinecraftClient.getInstance()
                val playerClient = client.player ?: return@register ActionResult.PASS

                if (playerClient.getAttackCooldownProgress(0.0f) < 1.0f) {
                    return@register ActionResult.PASS
                }

                val currentSlot = playerClient.inventory.selectedSlot
                val heldItem = playerClient.inventory.getStack(currentSlot)

                if (heldItem.item is SwordItem || heldItem.item is AxeItem) {
                    val maceSlot = (0..8).firstOrNull { 
                        playerClient.inventory.getStack(it).isOf(Items.MACE) 
                    } ?: -1

                    if (maceSlot != -1) {
                        playerClient.inventory.selectedSlot = maceSlot
                        client.interactionManager?.attackEntity(playerClient, entity)
                        playerClient.inventory.selectedSlot = currentSlot
                        return@register ActionResult.SUCCESS
                    }
                }
            }
            ActionResult.PASS
        }
    }
}