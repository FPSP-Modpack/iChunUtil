package me.ichun.mods.ichunutil.client.gui.bns.window;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import me.ichun.mods.ichunutil.client.gui.bns.window.constraint.Constraint;
import me.ichun.mods.ichunutil.client.gui.bns.window.constraint.IConstrainable;
import me.ichun.mods.ichunutil.client.gui.config.window.WindowValues;
import me.ichun.mods.ichunutil.client.render.RenderHelper;
import me.ichun.mods.ichunutil.common.iChunUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.util.Util;

import javax.annotation.Nullable;
import java.util.*;

import static me.ichun.mods.ichunutil.client.gui.bns.window.constraint.Constraint.Property.Type.LEFT;

public class WindowDock<M extends IWindows> extends Window<M>
{
    public LinkedHashMap<ArrayList<Window<?>>, Constraint.Property.Type> docked = new LinkedHashMap<>();
    public HashMap<Window<?>, WindowSize> dockedOriSize = new HashMap<>();

    public WindowDock(M parent)
    {
        super(parent);
        size(parent.getWidth(), parent.getHeight());
        if(parent instanceof IConstrainable)
        {
            setConstraint(Constraint.matchParent(this, (IConstrainable)parent, 0));
        }
        borderSize = () -> iChunUtil.configClient.guiDockPadding;
        titleSize = () -> 0;
    }

    @Override
    public boolean canShowTitle()
    {
        return false;
    }

    @Override
    public boolean hasTitle()
    {
        return false;
    }

    @Override
    public boolean canDrag()
    {
        return false;
    }

    @Override
    public boolean canDragResize()
    {
        return false;
    }

    @Override
    public boolean canBringToFront()
    {
        return false;
    }

    @Override
    public boolean canBeDocked() { return false; }

    @Override
    public boolean canBeUndocked() { return false; }

    @Override
    public void init()
    {
        constraint.apply();
        docked.keySet().forEach(windows -> windows.forEach(window -> {
            window.constraint.apply();
            window.resize(Minecraft.getInstance(), this.width, this.height);
        }));
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTick)
    {
        //Render BORDER HIGHLIGHT
        double left = 0;
        double top = 0;
        double right = width;
        double bottom = height;
        for(Map.Entry<ArrayList<Window<?>>, Constraint.Property.Type> e : docked.entrySet())
        {
            for(Window<?> key : e.getKey())
            {
                Constraint.Property.Type value = e.getValue();
                switch(value)
                {
                    case LEFT:
                    {
                        if(key.getRight() > left)
                        {
                            left = key.getRight();
                        }
                        break;
                    }
                    case TOP:
                    {
                        if(key.getBottom() > top)
                        {
                            top = key.getBottom();
                        }
                        break;
                    }
                    case RIGHT:
                    {
                        if(key.getLeft() < right)
                        {
                            right = key.getLeft();
                        }
                        break;
                    }
                    case BOTTOM:
                    {
                        if(key.getTop() < bottom)
                        {
                            bottom = key.getTop();
                        }
                        break;
                    }
                }
            }
        }

        if(getWorkspace().getFocused() instanceof Window && getWorkspace().isDragging() && ((Window<?>)getWorkspace().getFocused()).canBeDocked() && !getWorkspace().isDocked((Window)getWorkspace().getFocused()))
        {
            int dockSnap = 4;
            boolean draw = (mouseY >= top && mouseY < bottom && (mouseX >= left && mouseX < left + dockSnap || mouseX >= right - dockSnap && mouseX < right)) || (mouseX >= left && mouseX < right && (mouseY >= top && mouseY < top + dockSnap || mouseY >= bottom - dockSnap && bottom < right));
            if(draw)
            {
                if(mouseY >= top && mouseY < bottom)
                {
                    if(mouseX >= left && mouseX < left + dockSnap)
                    {
                        right = left + dockSnap;
                    }
                    else if(mouseX >= right - dockSnap && mouseX < right)
                    {
                        left = right - dockSnap;
                    }
                }
                if(mouseX >= left && mouseX < right)
                {
                    if(mouseY >= top && mouseY < top + dockSnap)
                    {
                        bottom = top + dockSnap;
                    }
                    else if(mouseY >= bottom - dockSnap && bottom < right)
                    {
                        top = bottom - dockSnap;
                    }
                }

                RenderSystem.enableBlend();
                RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
                if(renderMinecraftStyle())
                {
                    float scale = 8;
                    float scaleTex = 512F;
                    RenderSystem.depthMask(false);
                    RenderSystem.depthFunc(514);
                    RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_COLOR, GlStateManager.DestFactor.ONE);
                    bindTexture(ItemRenderer.RES_ITEM_GLINT);
                    RenderSystem.matrixMode(5890);
                    RenderSystem.pushMatrix();
                    RenderSystem.scalef((float)scale, (float)scale, (float)scale);
                    float f = (float)(Util.milliTime() % 3000L) / 3000.0F / (float)scale;
                    RenderSystem.translatef(f, 0.0F, 0.0F);
                    RenderSystem.rotatef(-50.0F, 0.0F, 0.0F, 1.0F);
                    RenderHelper.draw(left, top, right - left, bottom - top, 0, left / scaleTex, right / scaleTex, top / scaleTex, bottom / scaleTex);
                    RenderSystem.popMatrix();
                    RenderSystem.pushMatrix();
                    RenderSystem.scalef((float)scale, (float)scale, (float)scale);
                    float f1 = (float)(Util.milliTime() % 4873L) / 4873.0F / (float)scale;
                    RenderSystem.translatef(-f1, 0.0F, 0.0F);
                    RenderSystem.rotatef(10.0F, 0.0F, 0.0F, 1.0F);
                    RenderHelper.draw(left, top, right - left, bottom - top, 0, left / scaleTex, right / scaleTex, top / scaleTex, bottom / scaleTex);
                    RenderSystem.popMatrix();
                    RenderSystem.matrixMode(5888);
                    RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
                    RenderSystem.depthFunc(515);
                    RenderSystem.depthMask(true);
                }
                else
                {
                    RenderHelper.drawColour(getTheme().tabBorder[0], getTheme().tabBorder[1], getTheme().tabBorder[2], 150, left, top, right - left, bottom - top, 0);
                }
                RenderSystem.disableBlend();
            }
        }
        //END RENDER BORDER HIGHLIGHT

        List<ArrayList<Window<?>>> keys = new ArrayList<>(docked.keySet());
        for(int i = keys.size() - 1; i >= 0; i--)
        {
            ArrayList<Window<?>> windows = keys.get(i);
            windows.forEach(window -> window.render(mouseX, mouseY, partialTick));
        }
    }

    @Override
    public void resize(Minecraft mc, int width, int height)
    {
        constraint.apply();
        docked.keySet().forEach(windows -> windows.forEach(window -> {
            window.constraint.apply();
            window.resize(Minecraft.getInstance(), this.width, this.height);
        }));
    }

    @Override
    public void tick()
    {
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button)
    {
        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double distX, double distY)
    {
        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount)
    {
        Window<?> windowOver = getWindowOver(mouseX, mouseY);
        if(windowOver != null)
        {
            return windowOver.mouseScrolled(mouseX, mouseY, amount);
        }
        return false;
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY)
    {
        return !parent.isObstructed(this, mouseX, mouseY) && isMouseBetween(mouseX, getLeft(), getLeft() + width) && isMouseBetween(mouseY, getTop(), getTop() + height);
    }

    //TODO test changeFocus!

    @Override
    public @Nullable Fragment<?> getTopMostFragment(double mouseX, double mouseY)
    {
        if(isMouseOver(mouseX, mouseY))
        {
            Fragment<?> fragment = this;
            for(ArrayList<Window<?>> windows : this.docked.keySet())
            {
                for(Window<?> window : windows)
                {
                    Fragment<?> fragment1 = window.getTopMostFragment(mouseX, mouseY);
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

    public boolean isDocked(Window<?> window)
    {
        for(ArrayList<Window<?>> windows : docked.keySet())
        {
            if(windows.contains(window))
            {
                return true;
            }
        }
        return false;
    }

    public IWindows.DockInfo getDockInfo(double mouseX, double mouseY, boolean dockStack)
    {
        if(dockStack)
        {
            Window<?> window = getWindowOver(mouseX, mouseY);
            if(window != null && window.canDockStack())
            {
                return new IWindows.DockInfo(window, getAnchorType(window));
            }
        }

        double left = 0;
        double top = 0;
        double right = width;
        double bottom = height;
        for(Map.Entry<ArrayList<Window<?>>, Constraint.Property.Type> e : docked.entrySet())
        {
            for(Window<?> key : e.getKey())
            {
                Constraint.Property.Type value = e.getValue();
                switch(value)
                {
                    case LEFT:
                    {
                        if(key.getRight() > left)
                        {
                            left = key.getRight();
                        }
                        break;
                    }
                    case TOP:
                    {
                        if(key.getBottom() > top)
                        {
                            top = key.getBottom();
                        }
                        break;
                    }
                    case RIGHT:
                    {
                        if(key.getLeft() < right)
                        {
                            right = key.getLeft();
                        }
                        break;
                    }
                    case BOTTOM:
                    {
                        if(key.getTop() < bottom)
                        {
                            bottom = key.getTop();
                        }
                        break;
                    }
                }
            }
        }

        int dockSnap = 4;
        if(mouseY >= top && mouseY < bottom)
        {
            if(mouseX >= left && mouseX < left + dockSnap)
            {
                return new IWindows.DockInfo(null, LEFT);
            }
            else if(mouseX >= right - dockSnap && mouseX < right)
            {
                return new IWindows.DockInfo(null, Constraint.Property.Type.RIGHT);
            }
        }
        if(mouseX >= left && mouseX < right)
        {
            if(mouseY >= top && mouseY < top + dockSnap)
            {
                return new IWindows.DockInfo(null, Constraint.Property.Type.TOP);
            }
            else if(mouseY >= bottom - dockSnap && bottom < right)
            {
                return new IWindows.DockInfo(null, Constraint.Property.Type.BOTTOM);
            }
        }

        return null;
    }

    public void addToDocked(Window<?> dockedWin, Window<?> window)
    {
        for(Map.Entry<ArrayList<Window<?>>, Constraint.Property.Type> e : docked.entrySet())
        {
            if(e.getKey().contains(dockedWin))
            {
                dockedOriSize.put(window, new WindowSize(window.constraint, window.getLeft(), window.getTop(), window.getWidth(), window.getHeight()));
                //TODO constaints
                e.getKey().add(window);
                break;
            }
        }
    }

    public void addToDock(Window<?> window, Constraint.Property.Type type)
    {
        dockedOriSize.put(window, new WindowSize(window.constraint, window.getLeft(), window.getTop(), window.getWidth(), window.getHeight()));

        //TODO sort out constraints?
        Constraint constraint = new Constraint(window);
        for(Constraint.Property.Type type1 : Constraint.Property.Type.values())
        {
            IConstrainable constrainable = getAnchor(type1);
            if(type1 != type.getOpposite())
            {
                if(constrainable != null)
                {
                    constraint = constraint.type(type1, constrainable, type1.getOpposite(), -(Integer)window.borderSize.get() + borderSize.get());
                }
                else
                {
                    constraint = constraint.type(type1, this, type1, -(Integer)window.borderSize.get() + borderSize.get());
                }
            }
        }

        ArrayList<Window<?>> windows = new ArrayList<>();
        windows.add(window);
        docked.put(windows, type);
        window.setConstraint(constraint);
        window.constraint.apply();
        if(getWorkspace().hasInit())
        {
            window.resize(Minecraft.getInstance(), this.width, this.height);
        }
    }

    public void removeFromDock(Window<?> window)
    {
        Iterator<Map.Entry<ArrayList<Window<?>>, Constraint.Property.Type>> iterator = docked.entrySet().iterator();
        while(iterator.hasNext())
        {
            Map.Entry<ArrayList<Window<?>>, Constraint.Property.Type> e = iterator.next();
            ArrayList<Window<?>> windows = e.getKey();
            if(windows.contains(window)) // window is in this arraylist. sort it out
            {
                if(windows.size() == 1)
                {
                    iterator.remove(); //we're using a linkedHASHmap. Empty ArrayLists aren't very friendly.
                }
                else
                {
                    //TODO Update the constraints
                }
                break;
            }
        }

        WindowSize size = dockedOriSize.get(window);
        window.setConstraint(size.constraint);
        window.setLeft(size.x);
        window.setTop(size.y);
        window.setWidth(size.width);
        window.setHeight(size.height);
        window.resize(Minecraft.getInstance(), window.parent.getWidth(), window.parent.getHeight());
        dockedOriSize.remove(window);
    }

    public IConstrainable getAnchor(Constraint.Property.Type type) //gets the element to anchor on based on type
    {
        IConstrainable typeMost = null;
        for(Map.Entry<ArrayList<Window<?>>, Constraint.Property.Type> e : docked.entrySet())
        {
            if(e.getValue() == type)
            {
                typeMost = e.getKey().get(0);
            }
        }
        return typeMost;
    }

    public Constraint.Property.Type getAnchorType(Window<?> window)
    {
        for(Map.Entry<ArrayList<Window<?>>, Constraint.Property.Type> e : docked.entrySet())
        {
            if(e.getKey().contains(window))
            {
                return e.getValue();
            }
        }
        return null;
    }

    public Window<?> getWindowOver(double mouseX, double mouseY)
    {
        for(ArrayList<Window<?>> windows : docked.keySet())
        {
            for(Window<?> window : windows)
            {
                if(window.isMouseOver(mouseX, mouseY))
                {
                    return window;
                }
            }
        }
        return null;
    }

    public static class WindowSize
    {
        public final Constraint constraint;
        public final int x;
        public final int y;
        public final int width;
        public final int height;

        public WindowSize(Constraint constraint, int x, int y, int width, int height) {
            this.constraint = constraint;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }
    }
}
