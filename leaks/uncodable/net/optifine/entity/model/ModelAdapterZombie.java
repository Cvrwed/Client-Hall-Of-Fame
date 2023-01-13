package net.optifine.entity.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelZombie;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderZombie;
import net.minecraft.entity.monster.EntityZombie;

public class ModelAdapterZombie extends ModelAdapterBiped {
   public ModelAdapterZombie() {
      super(EntityZombie.class, "zombie", 0.5F);
   }

   @Override
   public ModelBase makeModel() {
      return new ModelZombie();
   }

   @Override
   public IEntityRenderer makeEntityRender(ModelBase modelBase, float shadowSize) {
      RenderManager rendermanager = Minecraft.getMinecraft().getRenderManager();
      RenderZombie renderzombie = new RenderZombie(rendermanager);
      Render.setModelBipedMain(renderzombie, (ModelBiped)modelBase);
      renderzombie.mainModel = modelBase;
      renderzombie.shadowSize = shadowSize;
      return renderzombie;
   }
}
