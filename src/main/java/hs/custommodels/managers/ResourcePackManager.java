package hs.custommodels.managers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import hs.custommodels.CustomModels;
import hs.custommodels.model.CustomModel;
import org.bukkit.Material;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Generates resource packs from custom models
 */
public class ResourcePackManager {

    private final CustomModels plugin;
    private final Gson gson;
    private final File packDir;
    private final File outputDir;

    public ResourcePackManager(CustomModels plugin) {
        this.plugin = plugin;
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        this.packDir = new File(plugin.getDataFolder(), "pack");
        this.outputDir = new File(plugin.getDataFolder(), "output");
        outputDir.mkdirs();
    }

    /**
     * Generate the complete resource pack
     */
    public boolean generateResourcePack() {
        try {
            plugin.getLogger().info("Generating resource pack...");

            // Clear and recreate pack directory
            clearDirectory(packDir);
            packDir.mkdirs();

            // Generate pack.mcmeta
            generatePackMcmeta();

            // Generate model files
            generateModelFiles();

            // Copy textures
            copyTextures();

            // Create ZIP file
            File zipFile = new File(outputDir, "CustomModels-ResourcePack.zip");
            createZip(packDir, zipFile);

            plugin.getLogger().info("Resource pack generated: " + zipFile.getAbsolutePath());
            return true;

        } catch (Exception e) {
            plugin.getLogger().severe("Failed to generate resource pack: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Generate pack.mcmeta file
     */
    private void generatePackMcmeta() throws IOException {
        JsonObject root = new JsonObject();
        JsonObject pack = new JsonObject();

        pack.addProperty("pack_format", 48); // 1.21.4+ format
        pack.addProperty("description", plugin.getConfig().getString("pack-description", "Custom Models Resource Pack"));

        root.add("pack", pack);

        File mcmetaFile = new File(packDir, "pack.mcmeta");
        try (FileWriter writer = new FileWriter(mcmetaFile)) {
            gson.toJson(root, writer);
        }
    }

    /**
     * Generate all model files organized by material
     */
    private void generateModelFiles() throws IOException {
        Map<Material, List<CustomModel>> modelsByMaterial = new HashMap<>();

        // Group models by base material
        for (CustomModel model : plugin.getModelManager().getAllModels().values()) {
            modelsByMaterial
                    .computeIfAbsent(model.getBaseMaterial(), k -> new ArrayList<>())
                    .add(model);
        }

        // Generate overrides for each material
        for (Map.Entry<Material, List<CustomModel>> entry : modelsByMaterial.entrySet()) {
            Material material = entry.getKey();
            List<CustomModel> models = entry.getValue();

            generateMaterialOverrides(material, models);
        }

        plugin.getLogger().info("Generated model overrides for " + modelsByMaterial.size() + " materials");
    }

    /**
     * Generate model override file for a specific material
     */
    private void generateMaterialOverrides(Material material, List<CustomModel> models) throws IOException {
        String materialName = material.name().toLowerCase();
        File modelFile = new File(packDir, "assets/minecraft/models/item/" + materialName + ".json");
        modelFile.getParentFile().mkdirs();

        JsonObject root = new JsonObject();
        root.addProperty("parent", "minecraft:item/generated");

        // Textures
        JsonObject textures = new JsonObject();
        textures.addProperty("layer0", "minecraft:item/" + materialName);
        root.add("textures", textures);

        // Overrides
        JsonArray overrides = new JsonArray();
        for (CustomModel model : models) {
            JsonObject override = new JsonObject();

            JsonObject predicate = new JsonObject();
            predicate.addProperty("custom_model_data", model.getCustomModelData());
            override.add("predicate", predicate);

            // Use custom model path if provided, otherwise use default
            String modelPath = model.getModelPath().isEmpty()
                    ? "custommodels:item/" + model.getId()
                    : model.getModelPath();
            override.addProperty("model", modelPath);

            overrides.add(override);
        }
        root.add("overrides", overrides);

        try (FileWriter writer = new FileWriter(modelFile)) {
            gson.toJson(root, writer);
        }
    }

    /**
     * Copy texture files from plugin directory to pack
     */
    private void copyTextures() throws IOException {
        File texturesSource = new File(plugin.getDataFolder(), "textures");
        if (!texturesSource.exists()) {
            plugin.getLogger().warning("No textures directory found");
            return;
        }

        File texturesDest = new File(packDir, "assets/custommodels/textures");
        texturesDest.mkdirs();

        copyDirectory(texturesSource, texturesDest);
        plugin.getLogger().info("Copied textures to resource pack");
    }

    /**
     * Copy models from plugin directory to pack
     */
    private void copyModels() throws IOException {
        File modelsSource = new File(plugin.getDataFolder(), "models");
        if (!modelsSource.exists()) {
            return;
        }

        File modelsDest = new File(packDir, "assets/custommodels/models");
        modelsDest.mkdirs();

        copyDirectory(modelsSource, modelsDest);
    }

    /**
     * Create ZIP file from directory
     */
    private void createZip(File sourceDir, File zipFile) throws IOException {
        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFile))) {
            zipDirectory(sourceDir, sourceDir, zos);
        }
    }

    /**
     * Recursively zip a directory
     */
    private void zipDirectory(File rootDir, File sourceDir, ZipOutputStream zos) throws IOException {
        File[] files = sourceDir.listFiles();
        if (files == null) return;

        for (File file : files) {
            if (file.isDirectory()) {
                zipDirectory(rootDir, file, zos);
            } else {
                String zipPath = rootDir.toPath().relativize(file.toPath()).toString().replace("\\", "/");
                ZipEntry entry = new ZipEntry(zipPath);
                zos.putNextEntry(entry);

                try (FileInputStream fis = new FileInputStream(file)) {
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = fis.read(buffer)) > 0) {
                        zos.write(buffer, 0, length);
                    }
                }
                zos.closeEntry();
            }
        }
    }

    /**
     * Copy directory recursively
     */
    private void copyDirectory(File source, File dest) throws IOException {
        if (!source.exists()) return;

        if (source.isDirectory()) {
            if (!dest.exists()) {
                dest.mkdirs();
            }

            File[] files = source.listFiles();
            if (files != null) {
                for (File file : files) {
                    copyDirectory(file, new File(dest, file.getName()));
                }
            }
        } else {
            Files.copy(source.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
    }

    /**
     * Clear directory contents
     */
    private void clearDirectory(File dir) {
        if (dir.exists()) {
            File[] files = dir.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        clearDirectory(file);
                    }
                    file.delete();
                }
            }
        }
    }

    /**
     * Get the generated resource pack file
     */
    public File getResourcePackFile() {
        return new File(outputDir, "CustomModels-ResourcePack.zip");
    }
}