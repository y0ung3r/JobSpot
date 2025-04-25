package com.anyjob.ui.explorer.search.controls.bottomSheets.addresses.models

import com.yandex.mapkit.GeoObject

class UserAddress(
    val geoObject: GeoObject,
    val formattedAddress: String
)