package me.ichun.mods.ichunutil.common.head.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import me.ichun.mods.ichunutil.common.head.HeadInfo;
import net.minecraft.entity.monster.EndermanEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class HeadEnderman extends HeadInfo<EndermanEntity>
{
    @Override
    public float getPupilScale(EndermanEntity living, MatrixStack stack, float partialTick, int eye)
    {
        if(living.isScreaming())
        {
            return 0.4F;
        }
        return super.getPupilScale(living, stack, partialTick, eye);
    }
}
