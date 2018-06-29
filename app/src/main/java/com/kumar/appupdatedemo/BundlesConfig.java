package com.kumar.appupdatedemo;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by kumara on 29/6/18.
 */

public class BundlesConfig {
    @SerializedName("version")
    String version;

    @SerializedName("bundles")
    List<BundleInfo> bundleInfoList;

    public String getVersion() {
        return version;
    }

    public List<BundleInfo> getBundleInfoList() {
        return bundleInfoList;
    }
}
