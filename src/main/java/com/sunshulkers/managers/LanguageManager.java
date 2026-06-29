package com.sunshulkers.managers;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

/**
 * Менеджер локализации.
 * Отвечает за загрузку и чтение файлов перевода из папки locales.
 */
public class LanguageManager {

    private final JavaPlugin plugin;
    private FileConfiguration langConfig;
    private File langFile;
    private final String currentLang;

    public LanguageManager(JavaPlugin plugin, String currentLang) {
        this.plugin = plugin;
        this.currentLang = currentLang;
        setup();
    }

    /**
     * Подготавливает папку и загружает нужный языковой файл.
     */
    public void setup() {
        File langFolder = new File(plugin.getDataFolder(), "locales");
        if (!langFolder.exists()) {
            langFolder.mkdirs(); // Создаем папку locales, если её еще нет
        }

        // Ищем файл по типу ru.yml, en.yml и т.д.
        langFile = new File(langFolder, currentLang + ".yml");

        if (!langFile.exists()) {
            try {
                // Пытаемся вытащить дефолтный файл из ресурсов самого плагина
                plugin.saveResource("locales/" + currentLang + ".yml", false);
            } catch (IllegalArgumentException e) {
                // Если файла нет даже в ресурсах джарника — создаем пустой, чтобы не ловить краши
                try {
                    langFile.createNewFile();
                    plugin.getLogger().warning("Файл локализации " + currentLang + ".yml не найден в ресурсах! Создан пустой файл.");
                } catch (IOException ex) {
                    plugin.getLogger().log(Level.SEVERE, "Не удалось создать файл языка!", ex);
                }
            }
        }

        // Загружаем конфиг в память
        langConfig = YamlConfiguration.loadConfiguration(langFile);
    }

    /**
     * Получить сырой конфиг языка (если вдруг нужно достать лист или секцию)
     */
    public FileConfiguration getConfig() {
        return langConfig;
    }

    /**
     * Перезагрузка файла языка (для команды /sunshulkers reload)
     */
    public void reload() {
        langConfig = YamlConfiguration.loadConfiguration(langFile);
    }

    /**
     * Удобный метод для получения сообщений.
     * Если ключа нет, вернет сам ключ, чтобы сразу было видно, где косяк.
     * * @param path путь к сообщению в yml файле
     * @return строка с сообщением
     */
    public String getMessage(String path) {
        return langConfig.getString(path, "&cMessage not found: " + path);
    }
}