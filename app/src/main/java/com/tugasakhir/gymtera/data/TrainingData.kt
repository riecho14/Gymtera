package com.tugasakhir.gymtera.data

import android.os.Parcel
import android.os.Parcelable

data class TrainingData(val name: String, val imageUrl: Int) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "", parcel.readInt()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeInt(imageUrl)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<TrainingData> {
        override fun createFromParcel(parcel: Parcel): TrainingData {
            return TrainingData(parcel)
        }

        override fun newArray(size: Int): Array<TrainingData?> {
            return arrayOfNulls(size)
        }
    }
}

