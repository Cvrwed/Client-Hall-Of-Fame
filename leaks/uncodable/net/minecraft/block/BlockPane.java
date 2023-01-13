package net.minecraft.block;

import java.util.List;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockPane extends Block {
   public static final PropertyBool NORTH = PropertyBool.create("north");
   public static final PropertyBool EAST = PropertyBool.create("east");
   public static final PropertyBool SOUTH = PropertyBool.create("south");
   public static final PropertyBool WEST = PropertyBool.create("west");
   private final boolean canDrop;

   protected BlockPane(Material materialIn, boolean canDrop) {
      super(materialIn);
      this.setDefaultState(
         this.blockState
            .getBaseState()
            .withProperty(NORTH, Boolean.FALSE)
            .withProperty(EAST, Boolean.FALSE)
            .withProperty(SOUTH, Boolean.FALSE)
            .withProperty(WEST, Boolean.FALSE)
      );
      this.canDrop = canDrop;
      this.setCreativeTab(CreativeTabs.tabDecorations);
   }

   @Override
   public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
      return state.withProperty(NORTH, this.canPaneConnectToBlock(worldIn.getBlockState(pos.north()).getBlock()))
         .withProperty(SOUTH, this.canPaneConnectToBlock(worldIn.getBlockState(pos.south()).getBlock()))
         .withProperty(WEST, this.canPaneConnectToBlock(worldIn.getBlockState(pos.west()).getBlock()))
         .withProperty(EAST, this.canPaneConnectToBlock(worldIn.getBlockState(pos.east()).getBlock()));
   }

   @Override
   public Item getItemDropped(IBlockState state, Random rand, int fortune) {
      return !this.canDrop ? null : super.getItemDropped(state, rand, fortune);
   }

   @Override
   public boolean isOpaqueCube() {
      return false;
   }

   @Override
   public boolean isFullCube() {
      return false;
   }

   @Override
   public boolean shouldSideBeRendered(IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
      return worldIn.getBlockState(pos).getBlock() != this && super.shouldSideBeRendered(worldIn, pos, side);
   }

   @Override
   public void addCollisionBoxesToList(World worldIn, BlockPos pos, IBlockState state, AxisAlignedBB mask, List<AxisAlignedBB> list, Entity collidingEntity) {
      boolean flag = this.canPaneConnectToBlock(worldIn.getBlockState(pos.north()).getBlock());
      boolean flag1 = this.canPaneConnectToBlock(worldIn.getBlockState(pos.south()).getBlock());
      boolean flag2 = this.canPaneConnectToBlock(worldIn.getBlockState(pos.west()).getBlock());
      boolean flag3 = this.canPaneConnectToBlock(worldIn.getBlockState(pos.east()).getBlock());
      if ((!flag2 || !flag3) && (flag2 || flag3 || flag || flag1)) {
         if (flag2) {
            this.setBlockBounds(0.0F, 0.0F, 0.4375F, 0.5F, 1.0F, 0.5625F);
            super.addCollisionBoxesToList(worldIn, pos, state, mask, list, collidingEntity);
         } else if (flag3) {
            this.setBlockBounds(0.5F, 0.0F, 0.4375F, 1.0F, 1.0F, 0.5625F);
            super.addCollisionBoxesToList(worldIn, pos, state, mask, list, collidingEntity);
         }
      } else {
         this.setBlockBounds(0.0F, 0.0F, 0.4375F, 1.0F, 1.0F, 0.5625F);
         super.addCollisionBoxesToList(worldIn, pos, state, mask, list, collidingEntity);
      }

      if ((!flag || !flag1) && (flag2 || flag3 || flag || flag1)) {
         if (flag) {
            this.setBlockBounds(0.4375F, 0.0F, 0.0F, 0.5625F, 1.0F, 0.5F);
            super.addCollisionBoxesToList(worldIn, pos, state, mask, list, collidingEntity);
         } else if (flag1) {
            this.setBlockBounds(0.4375F, 0.0F, 0.5F, 0.5625F, 1.0F, 1.0F);
            super.addCollisionBoxesToList(worldIn, pos, state, mask, list, collidingEntity);
         }
      } else {
         this.setBlockBounds(0.4375F, 0.0F, 0.0F, 0.5625F, 1.0F, 1.0F);
         super.addCollisionBoxesToList(worldIn, pos, state, mask, list, collidingEntity);
      }
   }

   @Override
   public void setBlockBoundsForItemRender() {
      this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
   }

   @Override
   public void setBlockBoundsBasedOnState(IBlockAccess worldIn, BlockPos pos) {
      float f = 0.4375F;
      float f1 = 0.5625F;
      float f2 = 0.4375F;
      float f3 = 0.5625F;
      boolean flag = this.canPaneConnectToBlock(worldIn.getBlockState(pos.north()).getBlock());
      boolean flag1 = this.canPaneConnectToBlock(worldIn.getBlockState(pos.south()).getBlock());
      boolean flag2 = this.canPaneConnectToBlock(worldIn.getBlockState(pos.west()).getBlock());
      boolean flag3 = this.canPaneConnectToBlock(worldIn.getBlockState(pos.east()).getBlock());
      if ((!flag2 || !flag3) && (flag2 || flag3 || flag || flag1)) {
         if (flag2) {
            f = 0.0F;
         } else if (flag3) {
            f1 = 1.0F;
         }
      } else {
         f = 0.0F;
         f1 = 1.0F;
      }

      if ((!flag || !flag1) && (flag2 || flag3 || flag || flag1)) {
         if (flag) {
            f2 = 0.0F;
         } else if (flag1) {
            f3 = 1.0F;
         }
      } else {
         f2 = 0.0F;
         f3 = 1.0F;
      }

      this.setBlockBounds(f, 0.0F, f2, f1, 1.0F, f3);
   }

   public final boolean canPaneConnectToBlock(Block blockIn) {
      return blockIn.isFullBlock()
         || blockIn == Blocks.glass
         || blockIn == Blocks.stained_glass
         || blockIn == Blocks.stained_glass_pane
         || blockIn instanceof BlockPane;
   }

   @Override
   protected boolean canSilkHarvest() {
      return true;
   }

   @Override
   public EnumWorldBlockLayer getBlockLayer() {
      return EnumWorldBlockLayer.CUTOUT_MIPPED;
   }

   @Override
   public int getMetaFromState(IBlockState state) {
      return 0;
   }

   @Override
   protected BlockState createBlockState() {
      return new BlockState(this, NORTH, EAST, WEST, SOUTH);
   }
}
