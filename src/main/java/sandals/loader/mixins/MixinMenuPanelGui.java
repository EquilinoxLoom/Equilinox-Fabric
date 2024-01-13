package sandals.loader.mixins;

import fontRendering.Text;
import gameMenu.DnaButtonGui;
import gameMenu.GameMenuBackground;
import gameMenu.GameMenuGui;
import gameMenu.MenuPanelGui;
import guiRendering.GuiRenderData;
import guis.GuiComponent;
import guis.GuiTexture;
import mainGuis.ColourPalette;
import mainGuis.GuiRepository;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.fabricmc.loader.impl.FabricLoaderImpl;
import org.lwjgl.util.vector.Vector2f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import toolbox.Colour;
import userInterfaces.ClickListener;
import userInterfaces.GuiImage;
import userInterfaces.GuiPanel;
import userInterfaces.IconButtonUi;

import java.util.Iterator;
import java.util.List;
import java.util.StringJoiner;

@Mixin(value = MenuPanelGui.class, remap = false)
public abstract class MixinMenuPanelGui extends GuiComponent {
    @Shadow
    private GameMenuGui gameMenu;

    @Shadow private List<DnaButtonGui> buttons;

    @Shadow private DnaButtonGui addButton(int index, GuiTexture line, String text, ClickListener listener) { return null; }

    @Inject(method = "init", at = @At(value = "INVOKE", target = "LgameMenu/MenuPanelGui;updateNewWorldButton()V", shift = At.Shift.BEFORE))
    private void addModsTab(CallbackInfo ci) {
        final Colour MOD_NAME = ColourPalette.DARK_GREY;
        final Colour MOD_TEXT = ColourPalette.MIDDLE_GREY;

        final float x = 0.15f;
        final int breakDesc = (int) (((0.25f - x) * 200) + 105);

        ClickListener listener = event -> { if (event.isLeftClick()) { gameMenu.setNewSecondaryScreen(
                new GuiPanel(GameMenuBackground.getStandardColour(), 0.65F) {
                    protected void init() {
                        super.init();
                        IconButtonUi backButton = new IconButtonUi(GameMenuGui.BACK_ICON, ColourPalette.DARK_GREY, ColourPalette.MIDDLE_GREY);
                        addComponentY(backButton, GameMenuGui.BACK_BUTTON_POS.x, GameMenuGui.BACK_BUTTON_POS.y, 0.12F);
                        backButton.addListener(event -> {
                            if (event.isLeftClick()) gameMenu.closeSecondaryScreen();
                        });
                        float y = 0.1F;
                        Iterator<ModContainer> iterator = FabricLoaderImpl.INSTANCE.getAllMods().iterator();
                        while (iterator.hasNext()) {
                            ModContainer mod = iterator.next();
                            ModMetadata data = mod.getMetadata();
                            float nameWidth = addText(data.getName(), 0.9F, MOD_NAME, x, y, 1).getActualWidth();
                            addText(data.getVersion().getFriendlyString(), 0.8f, MOD_TEXT, x + nameWidth + 0.025f, y + 0.01f, 0.8f);
                            y += 0.05F;
                            String description = data.getDescription();
                            if (description.length() < breakDesc) {
                                addText(description, 0.8f, MOD_TEXT, x, y, 0.8f);
                            } else {
                                String[] tokens = description.split(" ");
                                StringJoiner s1 = new StringJoiner(" ");
                                StringJoiner s2 = new StringJoiner(" ");
                                for (String token : tokens) {
                                    if (s1.length() + token.length() < breakDesc) s1.add(token);
                                    else s2.add(token);
                                }
                                addText(s1.toString(), 0.8f, MOD_TEXT, x, y, 0.8f);
                                y += 0.04F;
                                addText(s2.toString(), 0.8f, MOD_TEXT, x, y, 0.8f);
                            }
                            y += 0.065F;
                            if (iterator.hasNext()) {
                                float f = (0.25F / x);
                                f = (f - 1) / 2 + 1;
                                addComponent(new GuiImage(GuiRepository.BLOCK), 0.19F * (x / 0.25F), y - 0.01F, 0.62F * f, pixelsToRelativeY(1.0F));
                            }
                        }
                    }

                    protected void updateGuiTexturePositions(Vector2f position, Vector2f scale) {}

                    protected void updateSelf() {}

                    protected void getGuiTextures(GuiRenderData data) {}

                    private Text addText(String name, float fontSize, Colour color, float x, float y, float width) {
                        Text text = Text.newText(name).setFontSize(fontSize).create();
                        text.setColour(color);
                        addText(text, x, y, width);
                        return text;
                    }
                });
        }};
        this.buttons.add(addButton(8, (new GuiImage(GuiRepository.LINES[3])).getTexture(), "Mods", listener));
    }
}