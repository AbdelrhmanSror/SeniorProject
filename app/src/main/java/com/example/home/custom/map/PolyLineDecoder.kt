package com.example.home.custom.map

import com.google.android.gms.maps.model.LatLng
import com.google.maps.model.DirectionsResult

// Utility class  that decodes Polylines.
object PolyLineDecoder {
    fun startDecode(result: DirectionsResult): List<LatLng> {
        val newDecodedPath = ArrayList<LatLng>()
        //for (route in result.routes) {
        //get just the first route
        val decodedPath =
            decode(result.routes[0].overviewPolyline.encodedPath)

        // This loops through all the LatLng coordinates of ONE polyline.
        for (latLng in decodedPath) {
            newDecodedPath.add(
                LatLng(
                    latLng.latitude,
                    latLng.longitude
                )
            )
        }
        return newDecodedPath
    }

    /** Decodes an encoded path string into a sequence of LatLngs.  */
    private fun decode(encodedPath: String): List<LatLng> {

        val len = encodedPath.length

        val path = java.util.ArrayList<LatLng>(len / 2)
        var index = 0
        var lat = 0
        var lng = 0

        while (index < len) {
            var result = 1
            var shift = 0
            var b: Int
            do {
                b = encodedPath[index++].toInt() - 63 - 1
                result += b shl shift
                shift += 5
            } while (b >= 0x1f)
            lat += if (result and 1 != 0) (result shr 1).inv() else result shr 1

            result = 1
            shift = 0
            do {
                b = encodedPath[index++].toInt() - 63 - 1
                result += b shl shift
                shift += 5
            } while (b >= 0x1f)
            lng += if (result and 1 != 0) (result shr 1).inv() else result shr 1

            path.add(LatLng(lat * 1e-5, lng * 1e-5))
        }

        return path
    }

}