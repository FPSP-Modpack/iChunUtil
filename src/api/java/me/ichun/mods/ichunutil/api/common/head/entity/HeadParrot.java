package me.ichun.mods.ichunutil.api.common.head.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import me.ichun.mods.ichunutil.api.common.head.HeadInfo;
import net.minecraft.entity.passive.ParrotEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class HeadParrot extends HeadInfo<ParrotEntity>
{
    @OnlyIn(Dist.CLIENT)
    @Override
    public float getIrisScale(ParrotEntity living, MatrixStack stack, float partialTick, int eye)
    {
        return super.getIrisScale(living, stack, partialTick, eye) * (living.isPartying() ? 1.6F : 1F);
    }
}
