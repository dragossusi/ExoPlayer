/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.android.exoplayer2.ui

import android.content.Context
import android.graphics.Canvas
import android.os.Build
import android.support.annotation.RequiresApi
import android.util.AttributeSet
import com.google.android.exoplayer2.util.Assertions
import com.google.android.exoplayer2.util.Util

/**
 * A time bar that shows a current position, buffered position, duration and ad markers.
 *
 *
 * A DefaultTimeBar can be customized by setting attributes, as outlined below.
 *
 * <h3>Attributes</h3>
 *
 * The following attributes can be set on a DefaultTimeBar when used in a layout XML file:
 *
 *
 *
 *
 *
 *  * **`bar_height`** - Dimension for the height of the time bar.
 *
 *  * Default: [.DEFAULT_BAR_HEIGHT_DP]
 *
 *  * **`touch_target_height`** - Dimension for the height of the area in which touch
 * interactions with the time bar are handled. If no height is specified, this also determines
 * the height of the view.
 *
 *  * Default: [.DEFAULT_TOUCH_TARGET_HEIGHT_DP]
 *
 *  * **`ad_marker_width`** - Dimension for the width of any ad markers shown on the
 * bar. Ad markers are superimposed on the time bar to show the times at which ads will play.
 *
 *  * Default: [.DEFAULT_AD_MARKER_WIDTH_DP]
 *
 *  * **`scrubber_enabled_size`** - Dimension for the diameter of the circular scrubber
 * handle when scrubbing is enabled but not in progress. Set to zero if no scrubber handle
 * should be shown.
 *
 *  * Default: [.DEFAULT_SCRUBBER_ENABLED_SIZE_DP]
 *
 *  * **`scrubber_disabled_size`** - Dimension for the diameter of the circular scrubber
 * handle when scrubbing isn't enabled. Set to zero if no scrubber handle should be shown.
 *
 *  * Default: [.DEFAULT_SCRUBBER_DISABLED_SIZE_DP]
 *
 *  * **`scrubber_dragged_size`** - Dimension for the diameter of the circular scrubber
 * handle when scrubbing is in progress. Set to zero if no scrubber handle should be shown.
 *
 *  * Default: [.DEFAULT_SCRUBBER_DRAGGED_SIZE_DP]
 *
 *  * **`scrubber_drawable`** - Optional reference to a drawable to draw for the
 * scrubber handle. If set, this overrides the default behavior, which is to draw a circle for
 * the scrubber handle.
 *  * **`played_color`** - Color for the portion of the time bar representing media
 * before the current playback position.
 *
 *  * Corresponding method: [.setPlayedColor]
 *  * Default: [.DEFAULT_PLAYED_COLOR]
 *
 *  * **`scrubber_color`** - Color for the scrubber handle.
 *
 *  * Corresponding method: [.setScrubberColor]
 *  * Default: see [.getDefaultScrubberColor]
 *
 *  * **`buffered_color`** - Color for the portion of the time bar after the current
 * played position up to the current buffered position.
 *
 *  * Corresponding method: [.setBufferedColor]
 *  * Default: see [.getDefaultBufferedColor]
 *
 *  * **`unplayed_color`** - Color for the portion of the time bar after the current
 * buffered position.
 *
 *  * Corresponding method: [.setUnplayedColor]
 *  * Default: see [.getDefaultUnplayedColor]
 *
 *  * **`ad_marker_color`** - Color for unplayed ad markers.
 *
 *  * Corresponding method: [.setAdMarkerColor]
 *  * Default: [.DEFAULT_AD_MARKER_COLOR]
 *
 *  * **`played_ad_marker_color`** - Color for played ad markers.
 *
 *  * Corresponding method: [.setPlayedAdMarkerColor]
 *  * Default: see [.getDefaultPlayedAdMarkerColor]
 *
 *
 */
@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
class DefaultRoundedTimeBar
/** Creates a new time bar.  */
// Suppress warnings due to usage of View methods in the constructor.
(context: Context, attrs: AttributeSet?) : DefaultTimeBar(context, attrs), TimeBar {

    private val cornersRadius: Float

    init {

        val defaultCornersRadius = (5 * context.resources.displayMetrics.density)
        if (attrs != null) {
            val a = context.theme.obtainStyledAttributes(attrs, R.styleable.DefaultRoundedTimeBar, 0,
                    0)
            try {
                cornersRadius = a.getDimension(R.styleable.DefaultRoundedTimeBar_corners_radius, defaultCornersRadius)
            } finally {
                a.recycle()
            }
        } else {
            cornersRadius = defaultCornersRadius
        }
    }

    protected override fun drawTimeBar(canvas: Canvas) {
        val progressBarHeight = progressBar.height()
        val barTop = progressBar.centerY() - progressBarHeight / 2
        val barBottom = barTop + progressBarHeight
        if (getDuration() <= 0) {
            canvas.drawRoundRect(
                    progressBar.left.toFloat(),
                    barTop.toFloat(),
                    progressBar.right.toFloat(),
                    barBottom.toFloat(),
                    cornersRadius,
                    cornersRadius,
                    unplayedPaint
            )
            return
        }
        var bufferedLeft = bufferedBar.left
        val bufferedRight = bufferedBar.right
        val progressLeft = Math.max(Math.max(progressBar.left, bufferedRight), scrubberBar.right)
        if (progressLeft < progressBar.right) {
            canvas.drawRoundRect(
                    progressLeft.toFloat(),
                    barTop.toFloat(),
                    progressBar.right.toFloat(),
                    barBottom.toFloat(),
                    cornersRadius,
                    cornersRadius,
                    unplayedPaint
            )
        }
        bufferedLeft = Math.max(bufferedLeft, scrubberBar.right)
        if (bufferedRight > bufferedLeft) {
            canvas.drawRoundRect(
                    bufferedLeft.toFloat(),
                    barTop.toFloat(),
                    bufferedRight.toFloat(),
                    barBottom.toFloat(),
                    cornersRadius,
                    cornersRadius,
                    bufferedPaint
            )
        }
        if (scrubberBar.width() > 0) {
            canvas.drawRoundRect(
                    scrubberBar.left.toFloat(),
                    barTop.toFloat(),
                    scrubberBar.right.toFloat(),
                    barBottom.toFloat(),
                    cornersRadius,
                    cornersRadius,
                    playedPaint
            )
        }
        if (adGroupCount == 0) {
            return
        }
        val adGroupTimesMs = Assertions.checkNotNull(this.adGroupTimesMs)
        val playedAdGroups = Assertions.checkNotNull(this.playedAdGroups)
        val adMarkerOffset = adMarkerWidth / 2
        for (i in 0 until adGroupCount) {
            val adGroupTimeMs = Util.constrainValue(adGroupTimesMs[i], 0, getDuration())
            val markerPositionOffset = (progressBar.width() * adGroupTimeMs / getDuration()).toInt() - adMarkerOffset
            val markerLeft = progressBar.left + Math.min(progressBar.width() - adMarkerWidth,
                    Math.max(0, markerPositionOffset))
            val paint = if (playedAdGroups[i]) playedAdMarkerPaint else adMarkerPaint
            canvas.drawRoundRect(
                    markerLeft.toFloat(),
                    barTop.toFloat(),
                    (markerLeft + adMarkerWidth).toFloat(),
                    barBottom.toFloat(),
                    cornersRadius,
                    cornersRadius,
                    paint
            )
        }
    }

}
