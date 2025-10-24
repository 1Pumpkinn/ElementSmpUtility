package hs.custommodels.model;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a custom model with all its properties
 */
public class CustomModel {

    private final String id;
    private final ModelType type;
    private final Material baseMaterial;
    private final int customModelData;
    private final Component displayName;
    private final List<Component> lore;
    private final String modelPath;
    private final String texturePath;
    private final boolean unbreakable;
    private final boolean glowing;
    private final int stackSize;

    private CustomModel(Builder builder) {
        this.id = builder.id;
        this.type = builder.type;
        this.baseMaterial = builder.baseMaterial;
        this.customModelData = builder.customModelData;
        this.displayName = builder.displayName;
        this.lore = builder.lore;
        this.modelPath = builder.modelPath;
        this.texturePath = builder.texturePath;
        this.unbreakable = builder.unbreakable;
        this.glowing = builder.glowing;
        this.stackSize = builder.stackSize;
    }

    public String getId() {
        return id;
    }

    public ModelType getType() {
        return type;
    }

    public Material getBaseMaterial() {
        return baseMaterial;
    }

    public int getCustomModelData() {
        return customModelData;
    }

    public Component getDisplayName() {
        return displayName;
    }

    public List<Component> getLore() {
        return new ArrayList<>(lore);
    }

    public String getModelPath() {
        return modelPath;
    }

    public String getTexturePath() {
        return texturePath;
    }

    public boolean isUnbreakable() {
        return unbreakable;
    }

    public boolean isGlowing() {
        return glowing;
    }

    public int getStackSize() {
        return stackSize;
    }

    public enum ModelType {
        ITEM,
        BLOCK,
        FURNITURE,
        TOOL,
        ARMOR
    }

    public static class Builder {
        private String id;
        private ModelType type = ModelType.ITEM;
        private Material baseMaterial = Material.PAPER;
        private int customModelData = 1;
        private Component displayName;
        private List<Component> lore = new ArrayList<>();
        private String modelPath;
        private String texturePath;
        private boolean unbreakable = false;
        private boolean glowing = false;
        private int stackSize = 64;

        public Builder(String id) {
            this.id = id;
            this.displayName = Component.text(id);
        }

        public Builder type(ModelType type) {
            this.type = type;
            return this;
        }

        public Builder baseMaterial(Material material) {
            this.baseMaterial = material;
            return this;
        }

        public Builder customModelData(int cmd) {
            this.customModelData = cmd;
            return this;
        }

        public Builder displayName(Component name) {
            this.displayName = name;
            return this;
        }

        public Builder lore(List<Component> lore) {
            this.lore = new ArrayList<>(lore);
            return this;
        }

        public Builder addLore(Component line) {
            this.lore.add(line);
            return this;
        }

        public Builder modelPath(String path) {
            this.modelPath = path;
            return this;
        }

        public Builder texturePath(String path) {
            this.texturePath = path;
            return this;
        }

        public Builder unbreakable(boolean unbreakable) {
            this.unbreakable = unbreakable;
            return this;
        }

        public Builder glowing(boolean glowing) {
            this.glowing = glowing;
            return this;
        }

        public Builder stackSize(int size) {
            this.stackSize = Math.max(1, Math.min(64, size));
            return this;
        }

        public CustomModel build() {
            return new CustomModel(this);
        }
    }
}