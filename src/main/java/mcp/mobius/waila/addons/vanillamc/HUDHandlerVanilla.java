package mcp.mobius.waila.addons.vanillamc;

import api.block.blocks.CropsBlock;
import api.block.blocks.DailyGrowthCropsBlock;
import btw.block.BTWBlocks;
import btw.block.blocks.PlanterBlockBase;
import btw.block.blocks.SaplingBlock;
import btw.block.blocks.SugarCaneBlockBase;
import btw.block.blocks.WheatCropTopBlock;
import btw.block.blocks.legacy.LegacySaplingBlock;
import btw.item.BTWItems;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.SpecialChars;
import mcp.mobius.waila.api.impl.ModuleRegistrar;
import mcp.mobius.waila.cbcore.LangUtil;
import net.minecraft.src.*;

import java.util.List;
import java.util.Locale;

public class HUDHandlerVanilla implements IWailaDataProvider {

    static Block mobSpawner = Block.mobSpawner;
    static Block crops = Block.crops;
    static Block melonStem = Block.melonStem;
    static Block pumpkinStem = Block.pumpkinStem;
    static Block carrot = Block.carrot;
    static Block potato = Block.potato;
    static Block lever = Block.lever;
    static Block repeaterIdle = Block.redstoneRepeaterIdle;
    static Block repeaterActv = Block.redstoneRepeaterActive;
    static Block comparatorIdl = Block.redstoneComparatorIdle;
    static Block comparatorAct = Block.redstoneComparatorActive;
    static Block redstone = Block.redstoneWire;
    static Block jukebox = Block.jukebox;
    static Block cocoa = Block.cocoaPlant;
    static Block netherwart = Block.netherStalk;
    static Block silverfish = Block.silverfish;
    static Block leave = Block.leaves;
    static Block log = Block.wood;
    static Block quartz = Block.blockNetherQuartz;
    static Block anvil = Block.anvil;
    static Block sapling = Block.sapling;
    static Block skull = Block.skull;
    static Block reed = Block.reed;

    private static final int TWO_BY_TWO_NONE = 0;
    private static final int TWO_BY_TWO_PARTIAL = 1;
    private static final int TWO_BY_TWO_READY = 2;

    @Override
    public ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config) {
        Block block = accessor.getBlock();

        if (block == silverfish && config.getConfig("vanilla.silverfish")) {
            int metadata = accessor.getMetadata();
            return switch (metadata) {
                case 0 -> new ItemStack(Block.stone);
                case 1 -> new ItemStack(Block.cobblestone);
                case 2 -> new ItemStack(Block.brick);
                default -> null;
            };
        }

        if (block == redstone) return new ItemStack(Item.redstone);
        if (block instanceof BlockRedstoneOre) return new ItemStack(Block.oreRedstone);
        if (block == crops || block == BTWBlocks.wheatCrop || block == BTWBlocks.wheatCropTop) return new ItemStack(Item.wheat);
        if (block == carrot) return new ItemStack(Item.carrot);
        if (block == potato) return new ItemStack(Item.potato);
        if (block == BTWBlocks.carrotCrop || block == BTWBlocks.floweringCarrotCrop) return new ItemStack(BTWItems.carrot.itemID, 1, 0);
        if (block == BTWBlocks.hempCrop || block == BTWBlocks.hempRoots) return new ItemStack(BTWItems.hempSeeds.itemID, 1, 0);
        if (block == reed || block == BTWBlocks.sugarCane || block == BTWBlocks.sugarCaneRoots) return new ItemStack(Item.reed.itemID, 1, 0);
        if ((block == leave) && (accessor.getMetadata() > 3)) return new ItemStack(block, 1, accessor.getMetadata() - 4);
        if (block == log) return new ItemStack(block, 1, accessor.getMetadata() % 4);
        if ((block == quartz) && (accessor.getMetadata() > 2)) return new ItemStack(block, 1, 2);
        if (block == anvil) return new ItemStack(block, 1, block.damageDropped(accessor.getMetadata()));
        if (block instanceof LegacySaplingBlock) return new ItemStack(block, 1, accessor.getMetadata() & 3);
        if (block instanceof SaplingBlock) return new ItemStack(block, 1, 0);
        if (block == sapling) return new ItemStack(block, 1, block.damageDropped(accessor.getMetadata() & 3));
        return null;
    }

    @Override
    public List<String> getWailaHead(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        Block block = accessor.getBlock();

        if (block == mobSpawner && accessor.getTileEntity() instanceof TileEntityMobSpawner && config.getConfig("vanilla.spawntype")) {
            String name = currenttip.get(0);
            String mobname = ((TileEntityMobSpawner) accessor.getTileEntity()).getSpawnerLogic().getEntityNameToSpawn();
            currenttip.set(0, String.format("%s (%s)", name, mobname));
        }

        if (block == redstone) {
            String name = currenttip.get(0).replaceFirst(String.format(" %s", accessor.getMetadata()), "");
            currenttip.set(0, name);
        }

        if (block == melonStem) currenttip.set(0, SpecialChars.WHITE + LangUtil.translateG("hud.item.melonstem"));
        if (block == pumpkinStem) currenttip.set(0, SpecialChars.WHITE + LangUtil.translateG("hud.item.pumpkinstem"));
        return currenttip;
    }

    @Override
    public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        Block block = accessor.getBlock();

        if (config.getConfig("general.showcrop") && appendCropInfo(currenttip, accessor, config)) {
            return currenttip;
        }

        if (config.getConfig("vanilla.leverstate") && block == lever) {
            String redstoneOn = (accessor.getMetadata() & 8) == 0 ? LangUtil.translateG("hud.msg.off") : LangUtil.translateG("hud.msg.on");
            currenttip.add(String.format("%s : %s", LangUtil.translateG("hud.msg.state"), redstoneOn));
            return currenttip;
        }

        if (config.getConfig("vanilla.repeater") && (block == repeaterIdle || block == repeaterActv)) {
            int tick = (accessor.getMetadata() >> 2) + 1;
            currenttip.add(String.format("%s : %s %s", LangUtil.translateG("hud.msg.delay"), tick, tick == 1 ? "tick" : "ticks"));
            return currenttip;
        }

        if (config.getConfig("vanilla.comparator") && (block == comparatorIdl || block == comparatorAct)) {
            String mode = ((accessor.getMetadata() >> 2) & 1) == 0 ? LangUtil.translateG("hud.msg.comparator") : LangUtil.translateG("hud.msg.substractor");
            currenttip.add("Mode : " + mode);
            return currenttip;
        }

        if (config.getConfig("vanilla.redstone") && block == redstone) {
            currenttip.add(String.format("%s : %s", LangUtil.translateG("hud.msg.power"), accessor.getMetadata()));
            return currenttip;
        }
        return currenttip;
    }

    private boolean appendCropInfo(List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        Block block = accessor.getBlock();
        World world = accessor.getWorld();
        MovingObjectPosition pos = accessor.getPosition();
        int x = pos.blockX;
        int y = pos.blockY;
        int z = pos.blockZ;
        int metadata = accessor.getMetadata();
        boolean showDetails = config.getConfig("general.showcropdetails");

        if (appendSaplingInfo(currenttip, world, block, metadata, x, y, z, showDetails)) return true;
        if (appendSugarCaneInfo(currenttip, world, block, x, y, z, showDetails)) return true;
        if (appendApiCropInfo(currenttip, world, block, metadata, x, y, z, showDetails)) return true;
        if (appendLegacyCropInfo(currenttip, world, block, metadata, x, y, z, showDetails)) return true;
        if (appendStemInfo(currenttip, world, block, metadata, x, y, z, showDetails)) return true;
        if (appendCocoaInfo(currenttip, world, block, metadata, x, y, z, showDetails)) return true;
        return appendNetherWartInfo(currenttip, world, block, metadata, x, y, z, showDetails);
    }

    private void addGrowthLine(List<String> currenttip, int growth, int maxGrowth, boolean mature) {
        if (mature) {
            currenttip.add(String.format("%s : %s", LangUtil.translateG("hud.msg.growth"), LangUtil.translateG("hud.msg.mature")));
        } else {
            float growthPct = maxGrowth > 0 ? (growth / (float) maxGrowth) * 100.0F : 0.0F;
            currenttip.add(String.format(Locale.ROOT, "%s : %.0f %%", LangUtil.translateG("hud.msg.growth"), growthPct));
        }
    }

    private void addGrowthRateLine(List<String> currenttip, float chancePerRandomTick) {
        float chance = Math.max(0.0F, Math.min(1.0F, chancePerRandomTick));
        currenttip.add(String.format(Locale.ROOT, "%s: %.2f%%", LangUtil.translateG("hud.msg.crop.growth_rate"), chance * 100.0F));
    }

    private String getStageText(int stage, int maxStage) {
        return LangUtil.translateG("hud.msg.stage", stage, maxStage);
    }

    private String formatLightIssue(int light, int req) {
        return String.format(Locale.ROOT, "%s (%d/%d)", LangUtil.translateG("hud.msg.crop.no_light"), light, req);
    }

    private boolean isInEnd(World world) {
        return world.provider.dimensionId == 1;
    }

    private boolean hasLightBlockAbove(World world, int x, int y, int z) {
        return world.getBlockId(x, y + 1, z) == BTWBlocks.lightBlockOn.blockID
                || world.getBlockId(x, y + 2, z) == BTWBlocks.lightBlockOn.blockID;
    }

    private boolean isInBrightLight(World world, int x, int y, int z) {
        return world.getBlockLightValue(x, y, z) >= 15 || hasLightBlockAbove(world, x, y, z);
    }

    private float getGrowthMultiplier(Block blockBelow, World world, int x, int y, int z, Block plantBlock) {
        if (blockBelow == null) return 0.0F;
        return blockBelow.getPlantGrowthOnMultiplier(world, x, y, z, plantBlock);
    }

    private boolean isSugarCaneLike(Block block) {
        return block == reed || block instanceof SugarCaneBlockBase;
    }

    private boolean isHempTop(int metadata) {
        return (metadata & 8) != 0;
    }

    private boolean appendCocoaInfo(List<String> currenttip, World world, Block block, int metadata, int x, int y, int z, boolean showDetails) {
        if (block != cocoa) return false;

        int growth = Math.min((metadata >> 2) & 3, 2);
        boolean mature = growth >= 2;
        currenttip.add(getStageText(growth + 1, 3));
        addGrowthLine(currenttip, growth, 2, mature);

        if (showDetails) {
            boolean inEnd = isInEnd(world);
            boolean attached = block.canBlockStay(world, x, y, z);
            if (inEnd) currenttip.add(SpecialChars.RED + LangUtil.translateG("hud.msg.crop.in_end"));
            if (!attached) currenttip.add(SpecialChars.YELLOW + LangUtil.translateG("hud.msg.crop.no_soil"));

            if (!mature) {
                if (!inEnd && attached) addGrowthRateLine(currenttip, 0.05F);
                else currenttip.add(SpecialChars.YELLOW + LangUtil.translateG("hud.msg.crop.stopped"));
            }
        }
        return true;
    }

    private boolean appendNetherWartInfo(List<String> currenttip, World world, Block block, int metadata, int x, int y, int z, boolean showDetails) {
        if (block != netherwart) return false;

        int growth = Math.min(metadata, 3);
        boolean mature = growth >= 3;
        currenttip.add(getStageText(growth + 1, 4));
        addGrowthLine(currenttip, growth, 3, mature);

        if (showDetails) {
            boolean inNether = world.provider.dimensionId == -1;
            boolean validSoil = block.canBlockStay(world, x, y, z);
            if (!inNether) currenttip.add(SpecialChars.RED + LangUtil.translateG("hud.msg.crop.only_nether"));
            if (!validSoil) currenttip.add(SpecialChars.YELLOW + LangUtil.translateG("hud.msg.crop.no_soil"));

            if (!mature) {
                if (inNether && validSoil) addGrowthRateLine(currenttip, 0.1F);
                else currenttip.add(SpecialChars.YELLOW + LangUtil.translateG("hud.msg.crop.stopped"));
            }
        }
        return true;
    }

    private boolean appendSugarCaneInfo(List<String> currenttip, World world, Block block, int x, int y, int z, boolean showDetails) {
        if (!isSugarCaneLike(block)) return false;

        int bottomY = y;
        while (isSugarCaneLike(Block.blocksList[world.getBlockId(x, bottomY - 1, z)])) bottomY--;
        int topY = y;
        while (isSugarCaneLike(Block.blocksList[world.getBlockId(x, topY + 1, z)])) topY++;

        int height = topY - bottomY + 1;
        Block topBlock = Block.blocksList[world.getBlockId(x, topY, z)];
        int topMeta = world.getBlockMetadata(x, topY, z) & 15;

        currenttip.add(String.format("%s: %d/3", LangUtil.translateG("hud.msg.reed.height"), height));
        if (height < 3) currenttip.add(String.format("%s: %s", LangUtil.translateG("hud.msg.reed.next_growth"), getStageText(topMeta + 1, 16)));
        else currenttip.add(SpecialChars.GRAY + LangUtil.translateG("hud.msg.reed.max_height"));

        if (showDetails && height < 3) {
            boolean inEnd = isInEnd(world);
            Block support = Block.blocksList[world.getBlockId(x, bottomY - 1, z)];
            boolean waterNearby = support != null && support.isConsideredNeighbouringWaterForReedGrowthOn(world, x, bottomY - 1, z);
            boolean hasSpace = world.isAirBlock(x, topY + 1, z);

            if (inEnd) currenttip.add(SpecialChars.RED + LangUtil.translateG("hud.msg.crop.in_end"));
            if (!waterNearby) currenttip.add(SpecialChars.YELLOW + LangUtil.translateG("hud.msg.crop.no_water"));
            if (!hasSpace) currenttip.add(SpecialChars.YELLOW + LangUtil.translateG("hud.msg.crop.no_space"));

            if (!inEnd && waterNearby && hasSpace) {
                addGrowthRateLine(currenttip, topBlock instanceof BlockReed ? 0.5F : 1.0F);
            } else {
                currenttip.add(SpecialChars.YELLOW + LangUtil.translateG("hud.msg.crop.stopped"));
            }
        }
        return true;
    }

    private boolean appendApiCropInfo(List<String> currenttip, World world, Block block, int metadata, int x, int y, int z, boolean showDetails) {
        if (!(block instanceof CropsBlock cropBlock)) return false;
        if (block instanceof SaplingBlock) return false;

        int maxGrowth = (block instanceof WheatCropTopBlock) ? 3 : 7;
        int growth = cropBlock.getGrowthLevel(metadata);
        boolean mature = growth >= maxGrowth;
        addGrowthLine(currenttip, growth, maxGrowth, mature);

        if (showDetails) {
            Block blockBelow = Block.blocksList[world.getBlockId(x, y - 1, z)];
            boolean inEnd = isInEnd(world);
            boolean weedsBlocked = block.getWeedsGrowthLevel(world, x, y, z) > 0;
            boolean hasGrownToday = (metadata & 8) != 0;
            boolean needsNaturalLight = block == BTWBlocks.wheatCrop || block == BTWBlocks.wheatCropTop;
            int requiredLight = needsNaturalLight ? 11 : 9;
            int currentLight = needsNaturalLight ? world.getBlockNaturalLightValue(x, y, z) : world.getBlockLightValue(x, y, z);
            if (block == BTWBlocks.hempRoots && mature) {
                requiredLight = 15;
                currentLight = world.getBlockLightValue(x, y, z);
            }
            boolean lightOk = currentLight >= requiredLight || hasLightBlockAbove(world, x, y, z);
            boolean hydrated = blockBelow != null && blockBelow.isBlockHydratedForPlantGrowthOn(world, x, y - 1, z);

            if (block == BTWBlocks.hempRoots && mature) {
                boolean hasTop = world.getBlockId(x, y + 1, z) == BTWBlocks.hempCrop.blockID;
                boolean brightLight = isInBrightLight(world, x, y, z);
                boolean hasSpace = world.isAirBlock(x, y + 1, z);
                if (inEnd) {
                    currenttip.add(SpecialChars.RED + LangUtil.translateG("hud.msg.crop.in_end"));
                    return true;
                }
                if (!hasTop) {
                    if (!hydrated) currenttip.add(SpecialChars.YELLOW + LangUtil.translateG("hud.msg.crop.no_water"));
                    if (!brightLight) currenttip.add(SpecialChars.YELLOW + formatLightIssue(currentLight, 15));
                    if (!hasSpace) currenttip.add(SpecialChars.YELLOW + LangUtil.translateG("hud.msg.crop.no_space"));
                    if (weedsBlocked) currenttip.add(SpecialChars.YELLOW + LangUtil.translateG("hud.msg.crop.weeds"));
                    if (hydrated && brightLight && hasSpace && !weedsBlocked) {
                        float chance = (cropBlock.getBaseGrowthChance(world, x, y, z) / 4.0F)
                                * getGrowthMultiplier(blockBelow, world, x, y - 1, z, block);
                        addGrowthRateLine(currenttip, chance);
                    } else {
                        currenttip.add(SpecialChars.YELLOW + LangUtil.translateG("hud.msg.crop.stopped"));
                    }
                }
                return true;
            }

            if (inEnd) currenttip.add(SpecialChars.RED + LangUtil.translateG("hud.msg.crop.in_end"));
            if (!hydrated) currenttip.add(SpecialChars.YELLOW + LangUtil.translateG("hud.msg.crop.no_water"));
            if (!lightOk) currenttip.add(SpecialChars.YELLOW + formatLightIssue(currentLight, requiredLight));
            if (weedsBlocked) currenttip.add(SpecialChars.YELLOW + LangUtil.translateG("hud.msg.crop.weeds"));
            if (!mature && hasGrownToday) currenttip.add(SpecialChars.YELLOW + LangUtil.translateG("hud.msg.crop.daily_wait"));

            boolean canGrow = !mature && !inEnd && !weedsBlocked && hydrated && lightOk && !hasGrownToday;
            if (canGrow) {
                float chance = cropBlock.getBaseGrowthChance(world, x, y, z);
                if (block instanceof DailyGrowthCropsBlock) {
                    if (blockBelow != null && blockBelow.getIsFertilizedForPlantGrowth(world, x, y - 1, z)) chance *= 2.0F;
                } else {
                    chance *= getGrowthMultiplier(blockBelow, world, x, y - 1, z, block);
                }
                addGrowthRateLine(currenttip, chance);
            } else if (!mature) {
                currenttip.add(SpecialChars.YELLOW + LangUtil.translateG("hud.msg.crop.stopped"));
            }
        }
        return true;
    }

    private boolean appendLegacyCropInfo(List<String> currenttip, World world, Block block, int metadata, int x, int y, int z, boolean showDetails) {
        if (!(block instanceof BlockCrops cropBlock)) return false;

        if (block == BTWBlocks.hempCrop && isHempTop(metadata)) {
            currenttip.add(getStageText(3, 3));
            currenttip.add(String.format("%s : %s", LangUtil.translateG("hud.msg.growth"), LangUtil.translateG("hud.msg.mature")));
            return true;
        }

        int growth = metadata & 7;
        boolean mature = growth >= 7;
        if (block == BTWBlocks.hempCrop) currenttip.add(getStageText(growth >= 7 ? 2 : 1, 3));
        addGrowthLine(currenttip, growth, 7, mature);

        if (showDetails) {
            Block blockBelow = Block.blocksList[world.getBlockId(x, y - 1, z)];
            boolean inEnd = isInEnd(world);
            boolean weedsBlocked = block.getWeedsGrowthLevel(world, x, y, z) > 0;
            boolean hydrated = blockBelow != null && blockBelow.isBlockHydratedForPlantGrowthOn(world, x, y - 1, z);
            boolean brightCrop = block == BTWBlocks.hempCrop;
            int requiredLight = brightCrop ? 15 : 9;
            int currentLight = world.getBlockLightValue(x, y + (brightCrop ? 0 : 1), z);
            boolean lightOk = brightCrop ? isInBrightLight(world, x, y, z) : currentLight >= requiredLight;

            if (inEnd) currenttip.add(SpecialChars.RED + LangUtil.translateG("hud.msg.crop.in_end"));
            if (!hydrated) currenttip.add(SpecialChars.YELLOW + LangUtil.translateG("hud.msg.crop.no_water"));
            if (!lightOk) currenttip.add(SpecialChars.YELLOW + formatLightIssue(currentLight, requiredLight));
            if (weedsBlocked) currenttip.add(SpecialChars.YELLOW + LangUtil.translateG("hud.msg.crop.weeds"));

            if (block == BTWBlocks.hempCrop && mature && !isHempTop(metadata)) {
                boolean hasSpace = world.isAirBlock(x, y + 1, z);
                if (!hasSpace) currenttip.add(SpecialChars.YELLOW + LangUtil.translateG("hud.msg.crop.no_space"));
                if (!inEnd && hydrated && lightOk && !weedsBlocked && hasSpace) {
                    float chance = cropBlock.getBaseGrowthChance(world, x, y, z)
                            * getGrowthMultiplier(blockBelow, world, x, y - 1, z, block)
                            / 4.0F;
                    addGrowthRateLine(currenttip, chance);
                } else if (!inEnd) {
                    currenttip.add(SpecialChars.YELLOW + LangUtil.translateG("hud.msg.crop.stopped"));
                }
                return true;
            }

            boolean canGrow = !mature && !inEnd && !weedsBlocked && hydrated && lightOk;
            if (canGrow) {
                float chance = cropBlock.getBaseGrowthChance(world, x, y, z)
                        * getGrowthMultiplier(blockBelow, world, x, y - 1, z, block);
                addGrowthRateLine(currenttip, chance);
            } else if (!mature) {
                currenttip.add(SpecialChars.YELLOW + LangUtil.translateG("hud.msg.crop.stopped"));
            }
        }
        return true;
    }

    private boolean appendStemInfo(List<String> currenttip, World world, Block block, int metadata, int x, int y, int z, boolean showDetails) {
        if (block != melonStem && block != pumpkinStem) return false;

        int growth = Math.min(metadata, 15);
        int visibleGrowth = Math.min(growth, 14);
        boolean mature = growth >= 14;
        currenttip.add(getStageText(visibleGrowth + 1, 15));
        addGrowthLine(currenttip, visibleGrowth, 14, mature);

        if (showDetails) {
            Block blockBelow = Block.blocksList[world.getBlockId(x, y - 1, z)];
            boolean inEnd = isInEnd(world);
            boolean weedsBlocked = block.getWeedsGrowthLevel(world, x, y, z) > 0;
            boolean hydrated = blockBelow != null && blockBelow.isBlockHydratedForPlantGrowthOn(world, x, y - 1, z);
            int light = world.getBlockLightValue(x, y + 1, z);
            boolean lightOk = light >= 9;
            boolean hasFruit = hasAttachedFruit(world, x, y, z, block == melonStem ? Block.melon : Block.pumpkin);
            boolean hasSpace = hasStemFruitSpace(world, x, y, z);

            if (inEnd) currenttip.add(SpecialChars.RED + LangUtil.translateG("hud.msg.crop.in_end"));
            if (!hydrated) currenttip.add(SpecialChars.YELLOW + LangUtil.translateG("hud.msg.crop.no_water"));
            if (!lightOk) currenttip.add(SpecialChars.YELLOW + formatLightIssue(light, 9));
            if (weedsBlocked) currenttip.add(SpecialChars.YELLOW + LangUtil.translateG("hud.msg.crop.weeds"));
            if (mature && !hasFruit && !hasSpace) currenttip.add(SpecialChars.YELLOW + LangUtil.translateG("hud.msg.crop.no_space"));

            if (!mature || !hasFruit) {
                if (!inEnd && hydrated && lightOk && !weedsBlocked && (!mature || hasSpace)) {
                    float chance = 0.2F * getGrowthMultiplier(blockBelow, world, x, y - 1, z, block);
                    addGrowthRateLine(currenttip, chance);
                } else {
                    currenttip.add(SpecialChars.YELLOW + LangUtil.translateG("hud.msg.crop.stopped"));
                }
            }
        }
        return true;
    }

    private boolean appendSaplingInfo(List<String> currenttip, World world, Block block, int metadata, int x, int y, int z, boolean showDetails) {
        if (block instanceof SaplingBlock saplingBlock) {
            int growth = saplingBlock.getGrowthLevel(metadata);
            int stage = Math.min(3, growth / 3 + 1);
            boolean mature = growth >= 7;
            currenttip.add(getStageText(stage, 3));

            if (showDetails) {
                Block blockBelow = Block.blocksList[world.getBlockId(x, y - 1, z)];
                boolean inEnd = isInEnd(world);
                int light = world.getBlockLightValue(x, y, z);
                boolean lightOk = light >= 9 || hasLightBlockAbove(world, x, y, z);
                boolean canGrowOn = blockBelow != null && blockBelow.canSaplingsGrowOnBlock(world, x, y - 1, z);
                boolean onVase = blockBelow == BTWBlocks.vase;
                boolean hasGrownToday = (metadata & 8) != 0;

                if (inEnd) currenttip.add(SpecialChars.RED + LangUtil.translateG("hud.msg.crop.in_end"));
                if (!canGrowOn) currenttip.add(SpecialChars.YELLOW + LangUtil.translateG("hud.msg.crop.no_soil"));
                if (onVase) currenttip.add(SpecialChars.YELLOW + LangUtil.translateG("hud.msg.sapling.on_vase"));
                if (!lightOk) currenttip.add(SpecialChars.YELLOW + formatLightIssue(light, 9));
                if (!mature && hasGrownToday) currenttip.add(SpecialChars.YELLOW + LangUtil.translateG("hud.msg.crop.daily_wait"));

                if (block == BTWBlocks.jungleSapling) {
                    int status = getNewJungle2x2Status(world, x, y, z);
                    if (status == TWO_BY_TWO_NONE) currenttip.add(SpecialChars.YELLOW + LangUtil.translateG("hud.msg.sapling.jungle_needs_2x2"));
                    else if (status == TWO_BY_TWO_PARTIAL) currenttip.add(SpecialChars.YELLOW + LangUtil.translateG("hud.msg.sapling.jungle_2x2_wait"));
                    else currenttip.add(SpecialChars.GREEN + LangUtil.translateG("hud.msg.sapling.jungle_2x2_ready"));
                }

                if (!inEnd && canGrowOn && !onVase && lightOk && (!hasGrownToday || mature)) {
                    float chance = 0.4F;
                    if (blockBelow != null && blockBelow.getIsFertilizedForPlantGrowth(world, x, y - 1, z)) chance *= 2.0F;
                    addGrowthRateLine(currenttip, chance);
                } else if (!mature) {
                    currenttip.add(SpecialChars.YELLOW + LangUtil.translateG("hud.msg.crop.stopped"));
                }
            }
            return true;
        }

        if (block instanceof LegacySaplingBlock legacySapling) {
            int type = metadata & 3;
            int growthStage = (metadata & (~3)) >> 2;
            boolean mature = growthStage >= 3;
            currenttip.add(getStageText(growthStage + 1, 4));

            if (showDetails) {
                Block blockBelow = Block.blocksList[world.getBlockId(x, y - 1, z)];
                boolean inEnd = isInEnd(world);
                int light = world.getBlockLightValue(x, y + 1, z);
                boolean lightOk = light >= 9;
                boolean canGrowOn = blockBelow != null && blockBelow.canSaplingsGrowOnBlock(world, x, y - 1, z);
                boolean needsFertilizedPlanter = mature && blockBelow instanceof PlanterBlockBase
                        && !blockBelow.getIsFertilizedForPlantGrowth(world, x, y - 1, z);

                if (inEnd) currenttip.add(SpecialChars.RED + LangUtil.translateG("hud.msg.crop.in_end"));
                if (!canGrowOn) currenttip.add(SpecialChars.YELLOW + LangUtil.translateG("hud.msg.crop.no_soil"));
                if (!lightOk) currenttip.add(SpecialChars.YELLOW + formatLightIssue(light, 9));
                if (needsFertilizedPlanter) currenttip.add(SpecialChars.YELLOW + LangUtil.translateG("hud.msg.sapling.needs_fertilized_planter"));

                if (type == 3) {
                    int status = getLegacyJungle2x2Status(world, x, y, z, legacySapling);
                    if (status == TWO_BY_TWO_NONE) currenttip.add(SpecialChars.YELLOW + LangUtil.translateG("hud.msg.sapling.jungle_needs_2x2"));
                    else if (status == TWO_BY_TWO_PARTIAL) currenttip.add(SpecialChars.YELLOW + LangUtil.translateG("hud.msg.sapling.jungle_2x2_wait"));
                    else currenttip.add(SpecialChars.GREEN + LangUtil.translateG("hud.msg.sapling.jungle_2x2_ready"));
                }

                if (!inEnd && canGrowOn && lightOk && !needsFertilizedPlanter) {
                    float chance = 1.0F / 32.0F;
                    if (blockBelow != null && blockBelow.getIsFertilizedForPlantGrowth(world, x, y - 1, z)) {
                        chance *= blockBelow.getPlantGrowthOnMultiplier(world, x, y - 1, z, block);
                    }
                    if (mature) chance *= 2.0F;
                    addGrowthRateLine(currenttip, chance);
                } else if (!mature) {
                    currenttip.add(SpecialChars.YELLOW + LangUtil.translateG("hud.msg.crop.stopped"));
                }
            }
            return true;
        }

        if (block instanceof BlockSapling vanillaSapling) {
            int subtype = metadata & 3;
            boolean marked = (metadata & 8) != 0;
            currenttip.add(getStageText(marked ? 2 : 1, 3));

            if (showDetails) {
                int light = world.getBlockLightValue(x, y + 1, z);
                if (light < 9) currenttip.add(SpecialChars.YELLOW + formatLightIssue(light, 9));
                if (subtype == 3 && !hasVanillaJungle2x2(world, vanillaSapling, x, y, z)) {
                    currenttip.add(SpecialChars.YELLOW + LangUtil.translateG("hud.msg.sapling.jungle_needs_2x2"));
                }

                if (!isInEnd(world) && light >= 9) addGrowthRateLine(currenttip, 1.0F / 7.0F);
                else currenttip.add(SpecialChars.YELLOW + LangUtil.translateG("hud.msg.crop.stopped"));
            }
            return true;
        }

        return false;
    }

    private boolean hasAttachedFruit(World world, int x, int y, int z, Block fruitBlock) {
        return world.getBlockId(x - 1, y, z) == fruitBlock.blockID
                || world.getBlockId(x + 1, y, z) == fruitBlock.blockID
                || world.getBlockId(x, y, z - 1) == fruitBlock.blockID
                || world.getBlockId(x, y, z + 1) == fruitBlock.blockID;
    }

    private boolean hasStemFruitSpace(World world, int x, int y, int z) {
        return canStemGrowFruitAt(world, x - 1, y, z)
                || canStemGrowFruitAt(world, x + 1, y, z)
                || canStemGrowFruitAt(world, x, y, z - 1)
                || canStemGrowFruitAt(world, x, y, z + 1);
    }

    private boolean canStemGrowFruitAt(World world, int x, int y, int z) {
        int blockId = world.getBlockId(x, y, z);
        Block targetBlock = Block.blocksList[blockId];
        boolean replaceable = world.isAirBlock(x, y, z)
                || (targetBlock != null && (targetBlock.blockMaterial.isReplaceable()
                || (targetBlock.blockMaterial == Material.plants && blockId != Block.cocoaPlant.blockID)));
        if (!replaceable) return false;
        if (world.doesBlockHaveSolidTopSurface(x, y - 1, z)) return true;
        Block blockBelow = Block.blocksList[world.getBlockId(x, y - 1, z)];
        return blockBelow != null && blockBelow.canDomesticatedCropsGrowOnBlock(world, x, y - 1, z);
    }

    private int getNewJungle2x2Status(World world, int x, int y, int z) {
        boolean foundAny = false;
        for (int xOffset = -1; xOffset <= 0; xOffset++) {
            for (int zOffset = -1; zOffset <= 0; zOffset++) {
                int startX = x + xOffset;
                int startZ = z + zOffset;
                if (isNewJungleSaplingAt(world, startX, y, startZ)
                        && isNewJungleSaplingAt(world, startX + 1, y, startZ)
                        && isNewJungleSaplingAt(world, startX, y, startZ + 1)
                        && isNewJungleSaplingAt(world, startX + 1, y, startZ + 1)) {
                    foundAny = true;
                    if (isNewSaplingMature(world, startX, y, startZ)
                            && isNewSaplingMature(world, startX + 1, y, startZ)
                            && isNewSaplingMature(world, startX, y, startZ + 1)
                            && isNewSaplingMature(world, startX + 1, y, startZ + 1)) {
                        return TWO_BY_TWO_READY;
                    }
                }
            }
        }
        return foundAny ? TWO_BY_TWO_PARTIAL : TWO_BY_TWO_NONE;
    }

    private int getLegacyJungle2x2Status(World world, int x, int y, int z, LegacySaplingBlock sapling) {
        boolean foundAny = false;
        for (int xOffset = -1; xOffset <= 0; xOffset++) {
            for (int zOffset = -1; zOffset <= 0; zOffset++) {
                int startX = x + xOffset;
                int startZ = z + zOffset;
                if (sapling.isSameSapling(world, startX, y, startZ, 3)
                        && sapling.isSameSapling(world, startX + 1, y, startZ, 3)
                        && sapling.isSameSapling(world, startX, y, startZ + 1, 3)
                        && sapling.isSameSapling(world, startX + 1, y, startZ + 1, 3)) {
                    foundAny = true;
                    if (sapling.getSaplingGrowthStage(world, startX, y, startZ) == 3
                            && sapling.getSaplingGrowthStage(world, startX + 1, y, startZ) == 3
                            && sapling.getSaplingGrowthStage(world, startX, y, startZ + 1) == 3
                            && sapling.getSaplingGrowthStage(world, startX + 1, y, startZ + 1) == 3) {
                        return TWO_BY_TWO_READY;
                    }
                }
            }
        }
        return foundAny ? TWO_BY_TWO_PARTIAL : TWO_BY_TWO_NONE;
    }

    private boolean hasVanillaJungle2x2(World world, BlockSapling sapling, int x, int y, int z) {
        return (sapling.isSameSapling(world, x + 1, y, z, 3)
                        && sapling.isSameSapling(world, x, y, z + 1, 3)
                        && sapling.isSameSapling(world, x + 1, y, z + 1, 3))
                || (sapling.isSameSapling(world, x - 1, y, z, 3)
                        && sapling.isSameSapling(world, x, y, z + 1, 3)
                        && sapling.isSameSapling(world, x - 1, y, z + 1, 3))
                || (sapling.isSameSapling(world, x + 1, y, z, 3)
                        && sapling.isSameSapling(world, x, y, z - 1, 3)
                        && sapling.isSameSapling(world, x + 1, y, z - 1, 3))
                || (sapling.isSameSapling(world, x - 1, y, z, 3)
                        && sapling.isSameSapling(world, x, y, z - 1, 3)
                        && sapling.isSameSapling(world, x - 1, y, z - 1, 3));
    }

    private boolean isNewJungleSaplingAt(World world, int x, int y, int z) {
        return world.getBlockId(x, y, z) == BTWBlocks.jungleSapling.blockID;
    }

    private boolean isNewSaplingMature(World world, int x, int y, int z) {
        Block block = Block.blocksList[world.getBlockId(x, y, z)];
        if (!(block instanceof SaplingBlock sapling)) return false;
        return sapling.getGrowthLevel(world.getBlockMetadata(x, y, z)) >= 7;
    }

    @Override
    public List<String> getWailaTail(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        return currenttip;
    }

    @Override
    public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world, int x, int y, int z) {
        if (te != null) te.writeToNBT(tag);
        return tag;
    }

    public static void register() {
        ModuleRegistrar.instance().addConfig("VanillaMC", "vanilla.spawntype");
        ModuleRegistrar.instance().addConfig("VanillaMC", "vanilla.leverstate");
        ModuleRegistrar.instance().addConfig("VanillaMC", "vanilla.repeater");
        ModuleRegistrar.instance().addConfig("VanillaMC", "vanilla.comparator");
        ModuleRegistrar.instance().addConfig("VanillaMC", "vanilla.redstone");
        ModuleRegistrar.instance().addConfig("VanillaMC", "vanilla.silverfish");
        ModuleRegistrar.instance().addConfigRemote("VanillaMC", "vanilla.jukebox");
        ModuleRegistrar.instance().addConfigRemote("VanillaMC", "vanilla.show_invisible_players");

        IWailaDataProvider provider = new HUDHandlerVanilla();

        ModuleRegistrar.instance().registerStackProvider(provider, silverfish.getClass());
        ModuleRegistrar.instance().registerStackProvider(provider, redstone.getClass());
        ModuleRegistrar.instance().registerStackProvider(provider, BlockRedstoneOre.class);
        ModuleRegistrar.instance().registerStackProvider(provider, BlockCrops.class);
        ModuleRegistrar.instance().registerStackProvider(provider, CropsBlock.class);
        ModuleRegistrar.instance().registerStackProvider(provider, leave.getClass());
        ModuleRegistrar.instance().registerStackProvider(provider, log.getClass());
        ModuleRegistrar.instance().registerStackProvider(provider, quartz.getClass());
        ModuleRegistrar.instance().registerStackProvider(provider, anvil.getClass());
        ModuleRegistrar.instance().registerStackProvider(provider, BlockSapling.class);
        ModuleRegistrar.instance().registerStackProvider(provider, BlockWoodSlab.class);
        ModuleRegistrar.instance().registerStackProvider(provider, BlockReed.class);
        ModuleRegistrar.instance().registerStackProvider(provider, SugarCaneBlockBase.class);

        ModuleRegistrar.instance().registerHeadProvider(provider, mobSpawner.getClass());
        ModuleRegistrar.instance().registerHeadProvider(provider, melonStem.getClass());
        ModuleRegistrar.instance().registerHeadProvider(provider, pumpkinStem.getClass());

        ModuleRegistrar.instance().registerBodyProvider(provider, BlockCrops.class);
        ModuleRegistrar.instance().registerBodyProvider(provider, CropsBlock.class);
        ModuleRegistrar.instance().registerBodyProvider(provider, BlockStem.class);
        ModuleRegistrar.instance().registerBodyProvider(provider, BlockSapling.class);
        ModuleRegistrar.instance().registerBodyProvider(provider, BlockReed.class);
        ModuleRegistrar.instance().registerBodyProvider(provider, SugarCaneBlockBase.class);
        ModuleRegistrar.instance().registerBodyProvider(provider, BlockCocoa.class);
        ModuleRegistrar.instance().registerBodyProvider(provider, BlockNetherStalk.class);
        ModuleRegistrar.instance().registerBodyProvider(provider, lever.getClass());
        ModuleRegistrar.instance().registerBodyProvider(provider, repeaterIdle.getClass());
        ModuleRegistrar.instance().registerBodyProvider(provider, repeaterActv.getClass());
        ModuleRegistrar.instance().registerBodyProvider(provider, comparatorIdl.getClass());
        ModuleRegistrar.instance().registerBodyProvider(provider, comparatorAct.getClass());
        ModuleRegistrar.instance().registerHeadProvider(provider, redstone.getClass());
        ModuleRegistrar.instance().registerBodyProvider(provider, redstone.getClass());
        ModuleRegistrar.instance().registerBodyProvider(provider, jukebox.getClass());

        ModuleRegistrar.instance().registerNBTProvider(provider, mobSpawner.getClass());
        ModuleRegistrar.instance().registerNBTProvider(provider, BlockCrops.class);
        ModuleRegistrar.instance().registerNBTProvider(provider, CropsBlock.class);
        ModuleRegistrar.instance().registerNBTProvider(provider, BlockStem.class);
        ModuleRegistrar.instance().registerNBTProvider(provider, BlockSapling.class);
        ModuleRegistrar.instance().registerNBTProvider(provider, BlockReed.class);
        ModuleRegistrar.instance().registerNBTProvider(provider, SugarCaneBlockBase.class);
        ModuleRegistrar.instance().registerNBTProvider(provider, carrot.getClass());
        ModuleRegistrar.instance().registerNBTProvider(provider, potato.getClass());
        ModuleRegistrar.instance().registerNBTProvider(provider, lever.getClass());
        ModuleRegistrar.instance().registerNBTProvider(provider, repeaterIdle.getClass());
        ModuleRegistrar.instance().registerNBTProvider(provider, repeaterActv.getClass());
        ModuleRegistrar.instance().registerNBTProvider(provider, comparatorIdl.getClass());
        ModuleRegistrar.instance().registerNBTProvider(provider, comparatorAct.getClass());
        ModuleRegistrar.instance().registerNBTProvider(provider, redstone.getClass());
        ModuleRegistrar.instance().registerNBTProvider(provider, jukebox.getClass());
        ModuleRegistrar.instance().registerNBTProvider(provider, BlockCocoa.class);
        ModuleRegistrar.instance().registerNBTProvider(provider, BlockNetherStalk.class);
        ModuleRegistrar.instance().registerNBTProvider(provider, silverfish.getClass());
    }
}
