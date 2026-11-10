package net.wiselywisp.thebeneath;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.event.village.VillagerTradesEvent;
import net.wiselywisp.thebeneath.block.ModBlocks;
import net.wiselywisp.thebeneath.effect.ModEffects;
import net.wiselywisp.thebeneath.entity.ModEntities;
import net.wiselywisp.thebeneath.entity.ModSpawnPlacements;
import net.wiselywisp.thebeneath.entity.VoidWandererNaturalSpawner;
import net.wiselywisp.thebeneath.entity.VoidRiftSpawner;
import net.wiselywisp.thebeneath.fluid.ModFluids;
import net.wiselywisp.thebeneath.fluid.VoidExposureHandler;
import net.wiselywisp.thebeneath.enchantment.EmptyAttunementServer;
import net.wiselywisp.thebeneath.enchantment.EmptyAttunementNetwork;
import net.wiselywisp.thebeneath.block.entity.ModBlockEntities;
import net.wiselywisp.thebeneath.item.ModItemGroup;
import net.wiselywisp.thebeneath.item.ModItems;
import net.wiselywisp.thebeneath.sound.ModSounds;
import net.wiselywisp.thebeneath.world.ModFeatures;
import net.wiselywisp.thebeneath.world.dimension.ModDimensions;
import net.wiselywisp.thebeneath.world.gen.MyStructureProcessors;
import net.wiselywisp.thebeneath.world.gen.ModStructureTypes;
import net.wiselywisp.thebeneath.world.gen.ModChunkGenerators;
import net.wiselywisp.thebeneath.world.gen.ModBiomeSources;
import net.wiselywisp.thebeneath.world.gen.CeilingExitOpeningHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Optional;

@Mod(TheBeneath.MOD_ID)
public final class TheBeneath {
    public static final String MOD_ID = "thebeneath";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public TheBeneath(IEventBus modEventBus, ModContainer modContainer) {
        ModBlocks.BLOCKS.register(modEventBus);
        ModFluids.FLUID_TYPES.register(modEventBus);
        ModFluids.FLUIDS.register(modEventBus);
        ModEffects.EFFECTS.register(modEventBus);
        ModEntities.ENTITY_TYPES.register(modEventBus);
        modEventBus.addListener(ModSpawnPlacements::register);
        ModItems.ITEMS.register(modEventBus);
        ModBlockEntities.BLOCK_ENTITIES.register(modEventBus);
        ModSounds.SOUNDS.register(modEventBus);
        ModItemGroup.TABS.register(modEventBus);
        ModFeatures.FEATURES.register(modEventBus);
        MyStructureProcessors.PROCESSORS.register(modEventBus);
        ModStructureTypes.TYPES.register(modEventBus);
        ModChunkGenerators.TYPES.register(modEventBus);
        ModBiomeSources.TYPES.register(modEventBus);

        NeoForge.EVENT_BUS.addListener(this::onServerTick);
        NeoForge.EVENT_BUS.addListener(VoidExposureHandler::onRespawn);
        NeoForge.EVENT_BUS.addListener(EmptyAttunementServer::onPlayerTick);
        modEventBus.addListener(EmptyAttunementNetwork::register);
        NeoForge.EVENT_BUS.addListener(CeilingExitOpeningHandler::onChunkLoad);
        NeoForge.EVENT_BUS.addListener(this::addVillagerTrades);
        LOGGER.info("Loading The Beneath for NeoForge 1.21.1");
    }

    private void onServerTick(ServerTickEvent.Pre event) {
        MinecraftServer server = event.getServer();
        CeilingExitOpeningHandler.processQueuedChunks(server);
        VoidWandererNaturalSpawner.tick(server);
        VoidRiftSpawner.tick(server);
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            VoidExposureHandler.tick(player);
            ServerLevel current = player.serverLevel();
            if (current.dimension() == Level.OVERWORLD
                    && player.getY() < current.getMinBuildHeight()
                    && player.hasEffect(MobEffects.SLOW_FALLING)) {
                ServerLevel beneath = server.getLevel(ModDimensions.THE_BENEATH_LEVEL_KEY);
                if (beneath == null) {
                    player.sendSystemMessage(Component.literal("The Beneath dimension could not be found!"));
                    continue;
                }
                beneath.getChunkAt(BlockPos.containing(player.getX(), 64, player.getZ()));
                player.teleportTo(beneath, player.getX(), 64.0, player.getZ(),
                        Collections.emptySet(), player.getYRot(), player.getXRot());
                player.resetFallDistance();
            } else if (current.dimension() == ModDimensions.THE_BENEATH_LEVEL_KEY
                    && player.getY() > current.getMaxBuildHeight()) {
                ServerLevel overworld = server.overworld();
                int x = (int)Math.floor(player.getX());
                int z = (int)Math.floor(player.getZ());
                overworld.getChunkAt(new BlockPos(x, 64, z));
                int y = overworld.getHeight(Heightmap.Types.MOTION_BLOCKING, x, z);
                player.teleportTo(overworld, player.getX(), y + 1.0, player.getZ(),
                        Collections.emptySet(), player.getYRot(), player.getXRot());
                player.resetFallDistance();
            }
        }
    }

    private void addVillagerTrades(VillagerTradesEvent event) {
        if (event.getType() != VillagerProfession.CARTOGRAPHER) return;
        event.getTrades().get(2).add((trader, random) -> new MerchantOffer(
                new ItemCost(Items.EMERALD, 12),
                Optional.of(new ItemCost(Items.COMPASS, 1)),
                new ItemStack(ModItems.EXPLORER_MAP.get()),
                1, 2, 0.04F
        ));
    }
}
