plugins {
    id("com.android.asset-pack")
}

assetPack {
    packName = "tour_content"
    dynamicDelivery {
        deliveryType.set("on-demand")
    }
}
