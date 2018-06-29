package com.kumar.appupdatedemo;

import com.google.gson.annotations.SerializedName;

/**
 * Created by kumara on 29/6/18.
 */

public class BundleInfo {
    @SerializedName("uri")
    String uri;

    @SerializedName("type")
    String bundleType;

    @SerializedName("pkg")
    String packageName;

    @SerializedName("version")
    int version;

    public BundleInfo() {
    }

    public BundleInfo(String packageName) {
        this.packageName = packageName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BundleInfo that = (BundleInfo) o;

        return packageName.equals(that.packageName);
    }

    @Override
    public int hashCode() {
        return packageName.hashCode();
    }
}
