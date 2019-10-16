package me.ichun.mods.ichunutil.client.gui.bns.window;

import me.ichun.mods.ichunutil.client.gui.bns.Theme;
import me.ichun.mods.ichunutil.client.gui.bns.Workspace;
import me.ichun.mods.ichunutil.client.gui.bns.window.constraint.Constraint;
import me.ichun.mods.ichunutil.client.gui.bns.window.constraint.IConstrainable;
import me.ichun.mods.ichunutil.client.gui.bns.window.constraint.IConstrained;
import me.ichun.mods.ichunutil.client.render.RenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.INestedGuiEventHandler;
import net.minecraft.client.gui.IRenderable;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Supplier;

@SuppressWarnings("unchecked")
public abstract class Fragment<M extends Fragment> implements IConstrainable, IConstrained, INestedGuiEventHandler, IRenderable
{
    public static final ResourceLocation VANILLA_TABS = new ResourceLocation("textures/gui/container/creative_inventory/tabs.png");
    public static final ResourceLocation VANILLA_WIDGETS = new ResourceLocation("textures/gui/widgets.png");

    public M parentFragment;
    public @Nonnull Constraint constraint = Constraint.NONE;
    public Fragment(M parentFragment)
    {
        this.parentFragment = parentFragment;
    }

    public <T extends Fragment> T setConstraint(Constraint constraint)
    {
        this.constraint = constraint;
        return (T)this;
    }

    public Theme getTheme()
    {
        return parentFragment.getTheme();
    }

    public boolean renderMinecraftStyle()
    {
        return parentFragment.renderMinecraftStyle();
    }

    public abstract void init();

    @Override
    public boolean isMouseOver(double mouseX, double mouseY)
    {
        return isMouseBetween(mouseX, getLeft(), getLeft() + width) && isMouseBetween(mouseY, getTop(), getTop() + height);
    }

    public boolean isMouseBetween(double mousePos, double p1, double p2)
    {
        return mousePos >= p1 && mousePos < p2;
    }

    public FontRenderer getFontRenderer()
    {
        return parentFragment.getFontRenderer();
    }

    public void drawString(String s, float posX, float posY)
    {
        if(renderMinecraftStyle())
        {
            getFontRenderer().drawStringWithShadow(s, posX, posY, getMinecraftFontColour());
        }
        else
        {
            getFontRenderer().drawString(s, posX, posY, Theme.getAsHex(getTheme().font));
        }
    }

    public int getMinecraftFontColour()
    {
        return 16777215;
    }

    public void bindTexture(ResourceLocation rl)
    {
        Minecraft.getInstance().getTextureManager().bindTexture(rl);
    }

    public @Nullable Fragment getTopMostFragment(double mouseX, double mouseY)
    {
        if(isMouseOver(mouseX, mouseY))
        {
            Fragment fragment = this;
            for(IGuiEventListener child : this.children())
            {
                if(child instanceof Fragment)
                {
                    Fragment fragment1 = ((Fragment)child).getTopMostFragment(mouseX, mouseY);
                    if(fragment1 != null)
                    {
                        fragment = fragment1;
                    }
                }
            }
            return fragment;
        }
        return null;
    }

    public void setScissor()
    {
        RenderHelper.startGlScissor(getLeft(), getTop(), width, height);
    }

    public void endScissor()
    {
        RenderHelper.endGlScissor();
    }

    public @Nullable String tooltip(double mouseX, double mouseY)
    {
        return null;
    }

    public String reString(String s, int length) //shortens the string and slaps and ellipsis at the end
    {
        if(getFontRenderer().getStringWidth(s) > length)
        {
            String s1 = s;
            while(getFontRenderer().getStringWidth(s1 + Workspace.ELLIPSIS) > length)
            {
                s1 = s1.substring(0, s1.length() - 1);
            }
            return s1 + Workspace.ELLIPSIS;
        }
        return s;
    }

    //INestedGuiEventHandler
    @Nullable
    private IGuiEventListener focused;
    private boolean isDragging;

    @Override
    public boolean isDragging()
    {
        return this.isDragging;
    }

    @Override
    public void setDragging(boolean b)
    {
        this.isDragging = true;
    }

    @Nullable
    @Override
    public IGuiEventListener getFocused()
    {
        return focused;
    }

    @Override
    public void setFocused(@Nullable IGuiEventListener iGuiEventListener)
    {
        focused = iGuiEventListener;
    }

    public void unfocus(@Nullable IGuiEventListener guiReplacing) // pass the unfocused event down. Unfocus triggers before focus is set
    {
        IGuiEventListener lastFocused = getFocused();
        if(lastFocused instanceof Fragment && guiReplacing != lastFocused)
        {
            ((Fragment)lastFocused).unfocus(guiReplacing);
            setFocused(null); //set focus to nothing. MouseClicked will handle the focus of the new object.
        }
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) //pass down the mouse released to the focused event
    {
        this.setDragging(false);
        return getFocused() != null && getFocused().mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        if(isMouseOver(mouseX, mouseY)) //only return true if we're clicking on us
        {
            INestedGuiEventHandler.super.mouseClicked(mouseX, mouseY, button); //this calls setDragging();
            return true;
        }
        return false;
    }

            //IConstrainable
    public int posX;
    public int posY;
    public int width;
    public int height;

    @Override
    public int getLeft() //gets true position on screen.
    {
        return parentFragment.getLeft() + posX;
    }

    @Override
    public int getRight() //gets true position on screen.
    {
        return parentFragment.getLeft() + posX + width;
    }

    @Override
    public int getTop() //gets true position on screen.
    {
        return parentFragment.getTop() + posY;
    }

    @Override
    public int getBottom() //gets true position on screen.
    {
        return parentFragment.getTop() + posY + height;
    }

    //IConstrained
    @Override
    public void setPosX(int x)
    {
        this.posX = x;
    }

    @Override
    public void setPosY(int y)
    {
        this.posY = y;
    }

    @Override
    public void setLeft(int x) // this will be a the new left
    {
        this.posX = x - parentFragment.getLeft();
    }

    @Override
    public void setRight(int x)
    {
        this.width = x - getLeft();
    }

    @Override
    public void setTop(int y)
    {
        this.posY = y - parentFragment.getTop();
    }

    @Override
    public void setBottom(int y)
    {
        this.height = y - getTop();
    }

    @Override
    public void setWidth(int width)
    {
        this.width = width;
    }

    @Override
    public void setHeight(int height)
    {
        this.height = height;
    }

    @Override
    public void expandX(int width) //expands to minimum
    {
        if(this.width < width)
        {
            int lack = width - this.width;
            this.posX -= (lack / 2) + lack % 2;
            this.width = width;
        }
    }

    @Override
    public void expandY(int height)
    {
        if(this.height < height)
        {
            int lack = height - this.height;
            this.posY -= (lack / 2) + lack % 2;
            this.height = height;
        }
    }

    @Override
    public void contractX(int width) //contracts to max
    {
        if(this.width > width)
        {
            int lack = this.width - width;
            this.posX += (lack / 2) + lack % 2;
            this.width = width;
        }
    }

    @Override
    public void contractY(int height)
    {
        if(this.height > height)
        {
            int lack = this.height - height;
            this.posY += (lack / 2) + lack % 2;
            this.height = height;
        }
    }

    @Override
    public int getWidth()
    {
        return width;
    }

    @Override
    public int getHeight()
    {
        return height;
    }

    @Override
    public int getParentWidth()
    {
        return parentFragment.getWidth();
    }

    @Override
    public int getParentHeight()
    {
        return parentFragment.getHeight();
    }

    @Override
    public Supplier<Integer> getMinWidth()
    {
        return () -> 1;
    }

    @Override
    public Supplier<Integer> getMinHeight()
    {
        return () -> 1;
    }

    @Override
    public Supplier<Integer> getMaxWidth()
    {
        return () -> 1000000;
    }

    @Override
    public Supplier<Integer> getMaxHeight()
    {
        return () -> 1000000;
    }
}
