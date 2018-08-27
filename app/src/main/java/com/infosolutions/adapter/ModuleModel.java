package com.infosolutions.adapter;

/**
 * Created by shailesh on 10/7/17.
 */

public class ModuleModel {

    public ModuleModel() {
    }

    public ModuleModel(String moduleIconImage, String moduleTitle, String moduleId) {
        this.moduleIconImage = moduleIconImage;
        this.moduleTitle = moduleTitle;
        this.moduleId = moduleId;
    }

    private String moduleIconImage;
    private String moduleTitle;
    private String moduleId;

    public String getModuleIconImage() {
        return moduleIconImage;
    }

    public void setModuleIconImage(String moduleIconImage) {
        this.moduleIconImage = moduleIconImage;
    }

    public String getModuleTitle() {
        return moduleTitle;
    }

    public void setModuleTitle(String moduleTitle) {
        this.moduleTitle = moduleTitle;
    }

    public String getModuleId() {
        return moduleId;
    }

    public void setModuleId(String moduleId) {
        this.moduleId = moduleId;
    }

    @Override
    public String toString() {
        return "ModuleModel{" +
                "moduleIconImage='" + moduleIconImage + '\'' +
                ", moduleTitle='" + moduleTitle + '\'' +
                ", moduleId='" + moduleId + '\'' +
                '}';
    }
}
