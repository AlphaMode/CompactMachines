package org.dave.compactmachines3.block;

import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.ProbeMode;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.ItemHandlerHelper;
import org.dave.compactmachines3.compat.ITopInfoProvider;
import org.dave.compactmachines3.init.Blockss;
import org.dave.compactmachines3.init.Itemss;
import org.dave.compactmachines3.misc.CubeTools;
import org.dave.compactmachines3.tile.TileEntityMachine;
import org.dave.compactmachines3.tile.TileEntityTunnel;
import org.dave.compactmachines3.utility.DimensionBlockPos;
import org.dave.compactmachines3.world.WorldSavedDataMachines;
import org.dave.compactmachines3.world.tools.DimensionTools;
import org.dave.compactmachines3.world.tools.StructureTools;

import java.util.HashMap;

public class BlockTunnel extends BlockProtected implements ITileEntityProvider, ITopInfoProvider {
    public static final PropertyDirection MACHINE_SIDE = PropertyDirection.create("machineside");

    public BlockTunnel(Material material) {
        super(material);
        this.setLightOpacity(1);
        this.setLightLevel(1.0f);

        this.setDefaultState(blockState.getBaseState().withProperty(MACHINE_SIDE, EnumFacing.DOWN));
    }

    @Override
    public boolean isBlockProtected(IBlockState state, IBlockAccess world, BlockPos pos) {
        return true;
    }

    @SideOnly(Side.CLIENT)
    public void initModel() {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(getRegistryName(), "inventory"));
    }

    @Override
    public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
        return CubeTools.shouldSideBeRendered(blockAccess, pos, side);
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        if(player.isSneaking()) {
            return false;
        }

        if(world.isRemote || !(player instanceof EntityPlayerMP)) {
            return true;
        }

        EnumFacing connectedSide = state.getValue(MACHINE_SIDE);
        EnumFacing nextDirection = StructureTools.getNextDirection(connectedSide);

        int coords = StructureTools.getCoordsForPos(pos);
        HashMap sideMapping = WorldSavedDataMachines.INSTANCE.tunnels.get(coords);
        while(sideMapping != null) {
            if (nextDirection == null) {
                if(world.getTileEntity(pos) != null) {
                    world.removeTileEntity(pos);
                }

                IBlockState blockState = Blockss.wall.getDefaultState();
                world.setBlockState(pos, blockState);

                ItemHandlerHelper.giveItemToPlayer(player, new ItemStack(Itemss.tunnelTool));
                WorldSavedDataMachines.INSTANCE.removeTunnel(pos);
                break;
            }

            if(sideMapping.get(nextDirection) == null) {
                if(world.getTileEntity(pos) != null) {
                    world.removeTileEntity(pos);
                }

                world.setBlockState(pos, state.withProperty(MACHINE_SIDE, nextDirection));
                WorldSavedDataMachines.INSTANCE.removeTunnel(pos, connectedSide);
                WorldSavedDataMachines.INSTANCE.addTunnel(pos, nextDirection);
                break;
            }

            nextDirection = StructureTools.getNextDirection(nextDirection);
        }

        notifyOverworldNeighbor(pos);
        return true;
    }

    @Override
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block blockIn, BlockPos whatblock) {
        super.neighborChanged(state, world, pos, blockIn, whatblock);

        if(world.isRemote) {
            return;
        }

        if(!(world.getTileEntity(pos) instanceof TileEntityTunnel)) {
            return;
        }

        notifyOverworldNeighbor(pos);
    }

    public void notifyOverworldNeighbor(BlockPos pos) {
        DimensionBlockPos dimpos = WorldSavedDataMachines.INSTANCE.machinePositions.get(StructureTools.getCoordsForPos(pos));
        if(dimpos == null) {
            return;
        }

        WorldServer realWorld = DimensionTools.getWorldServerForDimension(dimpos.getDimension());
        if(realWorld == null || !(realWorld.getTileEntity(dimpos.getBlockPos()) instanceof TileEntityMachine)) {
            return;
        }

        realWorld.notifyNeighborsOfStateChange(dimpos.getBlockPos(), Blockss.machine, false);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(MACHINE_SIDE, EnumFacing.getFront(meta));
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(MACHINE_SIDE).getIndex();
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, MACHINE_SIDE);
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityTunnel();
    }

    @Override
    public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, EntityPlayer player, World world, IBlockState blockState, IProbeHitData data) {
        TileEntity te = world.getTileEntity(data.getPos());
        if(te instanceof TileEntityTunnel) {
            TileEntityTunnel tnt = (TileEntityTunnel) te;

            String translate = "enumfacing." + blockState.getValue(BlockTunnel.MACHINE_SIDE).getName();
            probeInfo.horizontal()
                    .item(new ItemStack(Items.COMPASS))
                    .text(TextFormatting.YELLOW + "{*" + translate + "*}" + TextFormatting.RESET);
        }
    }
}
