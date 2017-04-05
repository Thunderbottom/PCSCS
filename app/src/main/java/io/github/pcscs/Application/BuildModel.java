package io.github.pcscs.Application;

/**
 * Created by chinmaypai on 4/5/17.
 */

public class BuildModel {
    String buildName;

    public BuildModel(String buildName) {
        this.buildName = buildName;
    }

    public BuildModel() {

    }

    public String getBuildName() {
        return buildName;
    }

    public void setBuildName(String buildName) {
        this.buildName = buildName;
    }

}
