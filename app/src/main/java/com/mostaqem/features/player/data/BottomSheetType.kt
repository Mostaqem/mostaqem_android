package com.mostaqem.features.player.data

sealed class BottomSheetType{
    data object Reciters: BottomSheetType()
    data object Queue: BottomSheetType()
    data object Recitations: BottomSheetType()
    data object None : BottomSheetType()
}